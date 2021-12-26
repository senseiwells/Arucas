package me.senseiwells.arucas.utils;

import java.util.HashMap;
import java.util.Map;

public class ArucasThreadUtils {
	private static final Map<Long, Thread> threadMap = new HashMap<>();

	public static void addThreadToMap(long id, Thread thread) {
		threadMap.put(id, thread);
	}

	public static Thread getThreadById(long id) {
		return threadMap.get(id);
	}
}
