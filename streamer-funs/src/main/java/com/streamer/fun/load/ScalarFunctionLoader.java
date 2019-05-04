package com.streamer.fun.load;

import java.util.Collection;
import java.util.Map.Entry;

import org.apache.calcite.schema.ScalarFunction;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.impl.ScalarFunctionImpl;

import com.streamer.fun.scalar.core.JsonFunctions;
import com.streamer.fun.scalar.core.StringFunctions;
import com.streamer.fun.scalar.core.TimeFunctions;

public class ScalarFunctionLoader {

	// 注意：方法名是区分大小写的!
	public static void load(SchemaPlus schemaPlus) {

		// TODO 扫描获取所有的FUNCTION

		// 添加JSON UDF
		for (Entry<String, Collection<ScalarFunction>> funs : ScalarFunctionImpl.createAll(JsonFunctions.class).asMap()
				.entrySet()) {
			for (ScalarFunction function : funs.getValue()) {
				schemaPlus.add(funs.getKey(), function);
			}
		}
		// 添加时间UDF
		for (Entry<String, Collection<ScalarFunction>> funs : ScalarFunctionImpl.createAll(TimeFunctions.class).asMap()
				.entrySet()) {
			for (ScalarFunction function : funs.getValue()) {
				schemaPlus.add(funs.getKey(), function);
			}
		}
		// 添加String UDF
		for (Entry<String, Collection<ScalarFunction>> funs : ScalarFunctionImpl.createAll(StringFunctions.class)
				.asMap().entrySet()) {
			for (ScalarFunction function : funs.getValue()) {
				schemaPlus.add(funs.getKey(), function);
			}
		}
	}

}
