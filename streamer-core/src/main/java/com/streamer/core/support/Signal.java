package com.streamer.core.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Signal {

	public static Map<String, AtomicLong> map = new ConcurrentHashMap<>();

}
