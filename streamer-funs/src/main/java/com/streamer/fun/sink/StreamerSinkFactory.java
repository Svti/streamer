package com.streamer.fun.sink;

import java.lang.reflect.Constructor;

import com.streamer.core.parser.SqlTree;

public class StreamerSinkFactory {

	private static String sinkBasePackage = BaseSink.class.getPackage().getName() + ".";

	public static BaseSink getSinkByClass(String className, SqlTree sqlTree) throws Exception {
		BaseSink outputBase = null;
		Class<?> clazz = Class.forName(sinkBasePackage + className).asSubclass(BaseSink.class);
		Constructor<?> constructor = clazz.getDeclaredConstructor(new Class[] { SqlTree.class });
		outputBase = (BaseSink) constructor.newInstance(sqlTree);
		return outputBase;
	}
}
