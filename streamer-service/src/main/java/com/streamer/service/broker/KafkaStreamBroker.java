package com.streamer.service.broker;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.calcite.jdbc.CalciteConnection;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.type.SqlTypeName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.streamer.core.exception.StreammerException;
import com.streamer.core.message.KafkaMessage;
import com.streamer.core.message.Row;
import com.streamer.core.source.AbstractKafkaStreamFactory;
import com.streamer.core.support.Signal;
import com.streamer.core.utils.CoreUtils;
import com.streamer.fun.load.ScalarFunctionLoader;
import com.streamer.fun.load.SideFunctionLoader;
import com.streamer.fun.sink.StreamerSinkFactory;
import com.streamer.service.core.ServiceConstant;
import com.streamer.service.job.KafkaStreamJob;
import com.streamer.service.ops.LocalService;
import com.streamer.service.table.KafkaMessageTable;;

public class KafkaStreamBroker extends AbstractKafkaStreamFactory implements Runnable {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private CalciteConnection connection;

	private SchemaPlus schemaPlus;

	private ApplicationContext context;

	private KafkaStreamJob job;

	public KafkaStreamBroker(KafkaStreamJob job, ApplicationContext context) {
		this.job = job;
		this.context = context;
		String sql = job.getSqlTree().getExecSql().getQuerySql().replaceAll("\n", " ");
		logger.info("Streammer SQL Query: {}", sql);
	}

	@Override
	public void run() {
		Properties info = new Properties();
		info.setProperty("lex", "MYSQL");
		try {
			// 初始化连接
			this.connection = DriverManager.getConnection("jdbc:calcite:", info).unwrap(CalciteConnection.class);
			this.schemaPlus = connection.getRootSchema();

			// 系统Function注册
			ScalarFunctionLoader.load(schemaPlus);

			// 维度Funtion注册
			SideFunctionLoader.load(job.getSqlTree(), schemaPlus);

			// 启动信号量
			Signal.map.putIfAbsent(job.getToken(), new AtomicLong(1));

			// 启动多线程
			start(job.getToken(), job.getZookeeper(), job.getTopics(), job.getGroup(), job.getReset(), job.getBatch());
		} catch (Exception e) {
			exceptionStop(e);
		}
	}

	@Override
	public void onReceive(List<KafkaMessage> messages) {
		try {
			// 只支持消费一个表的数据
			schemaPlus.add(job.getKtable(), new KafkaMessageTable(messages, job.getSpliter(), job.getColumns()));

			// 创建连接
			Statement stmt = connection.createStatement();

			// 装载Kafka数据源
			List<List<Row>> streams = queryKafkaStreams(stmt);

			// 输出结果
			String type = job.getSqlTree().getPreDealSinkMap().get(job.getSqlTree().getExecSql().getTargetTable())
					.getPropMap().get("type").toString();

			// 处理输出
			String outputName = CoreUtils.upperCaseFirstChar(type.toLowerCase()) + "Sink";
			StreamerSinkFactory.getSinkByClass(outputName, job.getSqlTree()).process(streams);

			// 关闭stmt链接
			stmt.close();

			commit(job.getToken());
		} catch (Exception e) {
			exceptionStop(e);
		}
	}

	private List<List<Row>> queryKafkaStreams(Statement stmt) throws Exception {
		List<List<Row>> list = new ArrayList<>();
		ResultSet rs = stmt.executeQuery(job.getSqlTree().getExecSql().getQuerySql());
		while (rs.next()) {
			int cnt = rs.getMetaData().getColumnCount();
			List<Row> rows = new ArrayList<>();
			for (int i = 1; i <= cnt; i++) {
				Row row = new Row(rs.getMetaData().getColumnLabel(i), CoreUtils
						.dataTypeToObj(SqlTypeName.valueOf(rs.getMetaData().getColumnTypeName(i)), rs.getObject(i)));
				rows.add(row);
			}
			list.add(rows);
		}
		return list;
	}

	private void exceptionStop(Exception e) {
		logger.error(ServiceConstant.FORMAT_JOB_NAME(job.getName()) + e.getMessage(), e);
		context.getBean(LocalService.class).stopLocalNodeByName(job.getToken(), job.getName());
		logger.info("The kafka consumer will be shutdown...");
		if (consumer != null) {
			consumer.shutdown();
		}
		throw new StreammerException(ServiceConstant.FORMAT_JOB_NAME(job.getName()) + e.getMessage(), e);
	}
}
