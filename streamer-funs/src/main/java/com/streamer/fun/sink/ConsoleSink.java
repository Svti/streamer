package com.streamer.fun.sink;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.streamer.core.message.Row;
import com.streamer.core.parser.SqlTree;
import com.streamer.spi.console.PrettyTable;

public class ConsoleSink extends BaseSink {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private String job;

	public ConsoleSink(SqlTree sqlTree) {
		job = sqlTree.getPreDealTableMap().values().iterator().next().getQueryName();
	}

	@Override
	public void checkConfig() {

	}

	@Override
	public void process(List<List<Row>> list) throws Exception {

		if (list.isEmpty()) {
			logger.warn("There are not found any result for job:{} in ConsoleSink", job);
			return;
		}

		List<String> headers = new ArrayList<>();
		for (Row row : list.get(0)) {
			headers.add(row.getKey());
		}

		PrettyTable table = PrettyTable.fieldNames(headers.toArray(new String[headers.size()]));
		for (List<Row> rows : list) {
			List<Object> values = new ArrayList<>();
			for (Row row : rows) {
				values.add(row.getValue());
			}
			table.addRow(values.toArray());
		}

		logger.info("Job:{} for ConsoleSink print", job);

		logger.info(table.toString());
	}

}
