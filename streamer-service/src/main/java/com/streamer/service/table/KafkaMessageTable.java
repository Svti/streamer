package com.streamer.service.table;

import java.util.List;

import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.type.SqlTypeName;

import com.streamer.core.message.KafkaMessage;
import com.streamer.core.parser.TableColumn;
import com.streamer.core.support.Spliter;

public class KafkaMessageTable extends AbstractTable implements ScannableTable {

	private List<KafkaMessage> messages;

	private List<TableColumn> columns;

	private Spliter spliter;

	public KafkaMessageTable(List<KafkaMessage> messages, Spliter spliter, List<TableColumn> columns) {
		this.spliter = spliter;
		this.messages = messages;
		this.columns = columns;
	}

	@Override
	public Enumerable<Object[]> scan(DataContext context) {
		return new AbstractEnumerable<Object[]>() {
			public Enumerator<Object[]> enumerator() {
				return new KafkaEnumerator<Object[]>(messages, spliter, columns);
			}
		};
	}

	@Override
	public RelDataType getRowType(RelDataTypeFactory typeFactory) {

		RelDataTypeFactory.Builder builder = typeFactory.builder();

		// 添加自定义的字段
		if (columns == null) {
			throw new RuntimeException("Can't found any column in souece");
		}

		if (columns.isEmpty()) {
			throw new RuntimeException("Can't found any column in souece");
		}

		for (TableColumn column : columns) {

			String alias = column.getAlias();

			if (alias.startsWith("_")) {

				String name = alias.substring(1, alias.length()).toLowerCase();

				switch (name) {
				case "offset":
				case "timestamp":
				case "partition":
					builder.add(alias, SqlTypeName.BIGINT);
					break;
				case "topic":
				case "key":
				case "value":
					builder.add(alias, SqlTypeName.VARCHAR);
					break;
				default:
					builder.add(column.getAlias(), column.getTypeName());
					break;
				}
			} else {
				builder.add(column.getAlias(), column.getTypeName());
			}
		}
		return builder.build();
	}

}