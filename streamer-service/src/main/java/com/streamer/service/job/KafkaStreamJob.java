package com.streamer.service.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.streamer.core.exception.StreammerException;
import com.streamer.core.parser.CreateTableParser.SqlParserResult;
import com.streamer.core.parser.SqlTree;
import com.streamer.core.parser.TableColumn;
import com.streamer.core.support.Spliter;
import com.streamer.core.utils.CoreUtils;
import com.streamer.service.core.StreamerConstant;

public class KafkaStreamJob {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private String name;

	private String ktable;

	private String token;

	private Spliter spliter;

	private String group;

	private String zookeeper;

	private String topics;

	private String reset;

	private int batch;

	private List<TableColumn> columns;

	private SqlTree sqlTree;

	public Logger getLogger() {
		return logger;
	}

	public String getName() {
		return name;
	}

	public String getKtable() {
		return ktable;
	}

	public String getToken() {
		return token;
	}

	public Spliter getSpliter() {
		return spliter;
	}

	public String getGroup() {
		return group;
	}

	public String getZookeeper() {
		return zookeeper;
	}

	public String getTopics() {
		return topics;
	}

	public String getReset() {
		return reset;
	}

	public int getBatch() {
		return batch;
	}

	public SqlTree getSqlTree() {
		return sqlTree;
	}

	public List<TableColumn> getColumns() {
		return columns;
	}

	public KafkaStreamJob(SqlTree sqlTree) {

		if (sqlTree.getPreDealTableMap().size() == 0) {
			throw new StreammerException(
					StreamerConstant.FORMAT_JOB_NAME(name) + "The kafka source table is empty , please check it");
		}
		if (sqlTree.getPreDealTableMap().size() > 1) {
			throw new StreammerException("Sorry, support one source kafka table only , please check it");
		}

		this.sqlTree = sqlTree;

		Map<String, Object> sourceMap = new HashMap<>();

		for (Entry<String, SqlParserResult> entry : sqlTree.getPreDealTableMap().entrySet()) {
			String type = String.valueOf(entry.getValue().getPropMap().get("type"));
			if (type.equalsIgnoreCase(getName())) {

				// 设置表
				this.ktable = entry.getValue().getTableName();

				this.name = entry.getValue().getQueryName();

				// 设置属性
				columns = CoreUtils.genFields(new ArrayList<>(), entry.getValue().getFieldsInfoStr());

				// 设置参数配置
				sourceMap.putAll(entry.getValue().getPropMap());
			}
		}

		if (!sourceMap.containsKey("kafka.topic")) {
			throw new StreammerException(StreamerConstant.FORMAT_JOB_NAME(name)
					+ "The kafka.topic is needed in kafka source and cant be empty , Config:" + sourceMap);
		} else {
			this.topics = String.valueOf(sourceMap.get("kafka.topic"));
		}

		if (!sourceMap.containsKey("kafka.zkurl")) {
			throw new StreammerException(StreamerConstant.FORMAT_JOB_NAME(name)
					+ "The kafka.zkurl is needed in kafka source and cant be empty , Config:" + sourceMap);
		} else {
			this.zookeeper = String.valueOf(sourceMap.get("kafka.zkurl"));
		}

		if (!sourceMap.containsKey("kafka.group")) {
			throw new StreammerException(StreamerConstant.FORMAT_JOB_NAME(name)
					+ "The kafka.group is needed in kafka source and cant be empty , Config:" + sourceMap);
		} else {
			this.group = String.valueOf(sourceMap.get("kafka.group"));
		}

		if (!sourceMap.containsKey("kafka.reset")) {
			throw new StreammerException(StreamerConstant.FORMAT_JOB_NAME(name)
					+ "The kafka.reset is needed in kafka source and cant be empty , it should be 'smallest' or 'largest' , Config:"
					+ sourceMap);
		} else {
			this.reset = String.valueOf(sourceMap.get("kafka.reset"));
		}

		if (!sourceMap.containsKey("kafka.batch")) {
			throw new StreammerException(StreamerConstant.FORMAT_JOB_NAME(name)
					+ "The kafka.batch is needed in kafka source and cant be empty , Config:" + sourceMap);
		} else {
			this.batch = Integer.valueOf(sourceMap.get("kafka.batch").toString());
		}

		if (!sourceMap.containsKey("split")) {
			logger.warn("The message split is default to set JSON");
			this.spliter = Spliter.JSON;
		} else {
			// split="`|="
			String split = sourceMap.get("split").toString();
			String splits[] = split.split("\\|");
			if (splits.length > 1) {
				this.spliter = Spliter.spOf("kv", splits[0], splits[1]);
			} else {
				throw new StreammerException(StreamerConstant.FORMAT_JOB_NAME(name)
						+ "The split is needed to be like k|v , | it is like `|=");
			}
		}

	}

}
