package com.streamer.fun.load;

import java.util.Collection;
import java.util.Map.Entry;

import org.apache.calcite.schema.ScalarFunction;
import org.apache.calcite.schema.SchemaPlus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.streamer.core.parser.CreateSideParser;
import com.streamer.core.parser.SqlTree;
import com.streamer.core.side.JdbcSide;
import com.streamer.core.side.SideType;
import com.streamer.core.utils.CoreUtils;
import com.streamer.fun.side.SideTableFactory;

public class SideFunctionLoader {

	private static final Logger logger = LoggerFactory.getLogger(SideTableFactory.class);

	// 注意：方法名是区分大小写的!
	public static void load(SqlTree sqlTree, SchemaPlus schemaPlus) {
		for (Entry<String, CreateSideParser.SqlParserResult> entry : sqlTree.getPreDealSideMap().entrySet()) {
			String type = entry.getValue().getPropMap().get("type").toString().toLowerCase();
			switch (SideType.nameOf(type)) {
			case JDBC:
				// 添加维度表
				for (Entry<String, Collection<ScalarFunction>> funs : new SideTableFactory()
						.createAll(JdbcSide.class, entry.getKey(), CoreUtils.wirteConfig(entry.getValue())).asMap()
						.entrySet()) {
					for (ScalarFunction function : funs.getValue()) {
						schemaPlus.add(funs.getKey(), function);
					}

					logger.info("side funtion table {} has been load ", funs.getKey());
				}
				break;
			default:
				break;
			}
		}
	}
}
