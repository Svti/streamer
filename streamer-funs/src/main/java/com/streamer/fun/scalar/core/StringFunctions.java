package com.streamer.fun.scalar.core;

import org.apache.commons.lang3.StringUtils;

import com.streamer.fun.scalar.ScalarFunction;

public class StringFunctions implements ScalarFunction {

	public static int LENGHT(String input) {
		if (StringUtils.isEmpty(input) || StringUtils.isBlank(input)) {
			return input.length();
		} else {
			return 0;
		}
	}

	public static int LENGHT(String[] input) {
		if (input != null) {
			return input.length;
		} else {
			return 0;
		}
	}

	public static String IFNULL(String input, long output) {
		if (StringUtils.isEmpty(input) || StringUtils.isBlank(input)) {
			return String.valueOf(output);
		} else {
			return input;
		}
	}

	public static String IFNULL(String input, String output) {
		if (StringUtils.isEmpty(input) || StringUtils.isBlank(input)) {
			return output;
		} else {
			return input;
		}
	}

	public static String[] SPLIT(String input, String split) {
		if (StringUtils.isNotEmpty(input) && StringUtils.isNotBlank(input)) {
			return input.split(split);
		} else {
			return new String[] {};
		}
	}

	public static long SPLIT_LENGTH(String input, String split) {
		if (StringUtils.isNotEmpty(input) && StringUtils.isNotBlank(input)) {
			return input.split(split).length;
		} else {
			return 0;
		}
	}

	public static long LENGTH(String input) {
		if (StringUtils.isNotEmpty(input) && StringUtils.isNotBlank(input)) {
			return input.length();
		} else {
			return 0;
		}
	}

	public static String CONCAT(Object a1, Object a2) {
		return StringUtils.join(a1, a2);
	}

	public static String CONCAT(Object a1, Object a2, Object a3) {
		return StringUtils.join(a1, a2, a3);
	}

	public static String CONCAT(Object a1, Object a2, Object a3, Object a4) {
		return StringUtils.join(a1, a2, a3, a4);
	}

	public static String CONCAT(Object a1, Object a2, Object a3, Object a4, Object a5) {
		return StringUtils.join(a1, a2, a3, a4, a5);
	}

	public static String CONCAT(Object a1, Object a2, Object a3, Object a4, Object a5, Object a6) {
		return StringUtils.join(a1, a2, a3, a4, a5, a6);
	}

	public static String CONCAT(Object a1, Object a2, Object a3, Object a4, Object a5, Object a6, Object a7) {
		return StringUtils.join(a1, a2, a3, a4, a5, a6, a7);
	}

	public static String CONCAT(Object a1, Object a2, Object a3, Object a4, Object a5, Object a6, Object a7,
			Object a8) {
		return StringUtils.join(a1, a2, a3, a4, a5, a6, a7, a8);
	}

	public static String CONCAT(Object a1, Object a2, Object a3, Object a4, Object a5, Object a6, Object a7, Object a8,
			Object a9) {
		return StringUtils.join(a1, a2, a3, a4, a5, a6, a7, a8, a9);
	}

	public static String CONCAT(Object a1, Object a2, Object a3, Object a4, Object a5, Object a6, Object a7, Object a8,
			Object a9, Object a10) {
		return StringUtils.join(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10);
	}

	public static String CONCAT(Object a1, Object a2, Object a3, Object a4, Object a5, Object a6, Object a7, Object a8,
			Object a9, Object a10, Object a11) {
		return StringUtils.join(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11);
	}

	public static String CONCAT(Object a1, Object a2, Object a3, Object a4, Object a5, Object a6, Object a7, Object a8,
			Object a9, Object a10, Object a11, Object a12) {
		return StringUtils.join(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12);
	}

	public static String CONCAT(Object a1, Object a2, Object a3, Object a4, Object a5, Object a6, Object a7, Object a8,
			Object a9, Object a10, Object a11, Object a12, Object a13) {
		return StringUtils.join(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13);
	}

	public static String CONCAT(Object a1, Object a2, Object a3, Object a4, Object a5, Object a6, Object a7, Object a8,
			Object a9, Object a10, Object a11, Object a12, Object a13, Object a14) {
		return StringUtils.join(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14);
	}

	public static String CONCAT(Object a1, Object a2, Object a3, Object a4, Object a5, Object a6, Object a7, Object a8,
			Object a9, Object a10, Object a11, Object a12, Object a13, Object a14, Object a15) {
		return StringUtils.join(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15);
	}

	public static String CONCAT(Object a1, Object a2, Object a3, Object a4, Object a5, Object a6, Object a7, Object a8,
			Object a9, Object a10, Object a11, Object a12, Object a13, Object a14, Object a15, Object a16) {
		return StringUtils.join(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16);
	}

	public static String CONCAT(Object a1, Object a2, Object a3, Object a4, Object a5, Object a6, Object a7, Object a8,
			Object a9, Object a10, Object a11, Object a12, Object a13, Object a14, Object a15, Object a16, Object a17) {
		return StringUtils.join(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17);
	}

	public static String CONCAT(Object a1, Object a2, Object a3, Object a4, Object a5, Object a6, Object a7, Object a8,
			Object a9, Object a10, Object a11, Object a12, Object a13, Object a14, Object a15, Object a16, Object a17,
			Object a18) {
		return StringUtils.join(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18);
	}

	public static String CONCAT(Object a1, Object a2, Object a3, Object a4, Object a5, Object a6, Object a7, Object a8,
			Object a9, Object a10, Object a11, Object a12, Object a13, Object a14, Object a15, Object a16, Object a17,
			Object a18, Object a19) {
		return StringUtils.join(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19);
	}

	public static String CONCAT(Object a1, Object a2, Object a3, Object a4, Object a5, Object a6, Object a7, Object a8,
			Object a9, Object a10, Object a11, Object a12, Object a13, Object a14, Object a15, Object a16, Object a17,
			Object a18, Object a19, Object a20) {
		return StringUtils.join(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19,
				a20);
	}

}
