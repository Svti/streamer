package com.streamer.fun.scalar.core;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.streamer.fun.scalar.ScalarFunction;

public class TimeFunctions implements ScalarFunction {

	public static String TIMESTAMP_STR(long input, String pattern) {
		return new DateTime(input).toString(pattern);
	}

	public static long STR_TIMESTAMP(String input, String pattern) {
		return DateTime.parse(input, DateTimeFormat.forPattern(pattern)).getMillis();
	}

	public static long NOW() {
		return DateTime.now().getMillis();
	}

	public static String TIMESTAMP_MOD_MIN(int mod, long input, String pattern) {
		DateTime time = new DateTime(input);
		int val = time.getMinuteOfHour() % mod;
		if (val > 0) {
			time = time.minusMinutes(val);
		}
		return time.toString(pattern);
	}
}
