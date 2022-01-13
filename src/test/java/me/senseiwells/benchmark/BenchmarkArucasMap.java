package me.senseiwells.benchmark;

import me.senseiwells.arucas.api.ContextBuilder;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.extensions.wrappers.ArucasTestWrapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BenchmarkArucasMap {
	// TODO: Implement JMH
	public static void main(String[] args) {
		Context context = new ContextBuilder()
			.setDisplayName("System.in")
			.addDefault()
			.addWrapper(ArucasTestWrapper::new)
			.build();
		
		context.getThreadHandler()
			.setStopErrorHandler(System.out::println)
			.setErrorHandler(System.out::println)
			.setFatalErrorHandler((c, t, s) -> t.printStackTrace());
		
		final int STEPS = 1000000;
		final int WARMUP = 100000;
		final int MAX_VALUE = 100000;
		
		try {
			Random random = new Random();
			
			// Arucas
			random.setSeed(0);
			for (int i = 4; i < 20; i++) {
				int maxValue = 1 << i;
				debug("ArucasMap (%d) (%d)".formatted(STEPS, maxValue), benchmark_arucas_map(context, STEPS, WARMUP, maxValue, random));
			}
			
			// Java
			random.setSeed(0);
			debug("JavaMap", benchmark_java_map(STEPS, WARMUP, MAX_VALUE, random));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void debug(String name, List<Time> times) {
		System.out.printf("\n%s:\n", name);
		for (Time time : times) {
			System.out.printf("  %s\n", time);
		}
	}

	private static List<Time> benchmark_arucas_map(Context context, final int STEPS, final int WARMUP, final int MAX_VALUE, Random random) throws Exception {
		final double TIME = 1.0 / (double)STEPS;
		final double WARMUP_TIME = 1.0 / (double)WARMUP;
		List<Time> times = new ArrayList<>();
		
		ArucasCache.generate(MAX_VALUE);
		final NumberValue[] N_CACHE = ArucasCache.N_CACHE;
		final StringValue[] S_CACHE = ArucasCache.S_CACHE;
		
		long start;
		
		// Warmup
		start = System.nanoTime();
		ArucasMap arucas_map = new ArucasMap();
		for (int i = 0; i < WARMUP; i++) {
			int v = random.nextInt(MAX_VALUE);
			arucas_map.put(context, N_CACHE[v], S_CACHE[v]);
		}
		arucas_map.clear();
		final long WARMUP_VALUES = System.nanoTime() - start;
		times.add(new Time("Warmup : %.4f ns", WARMUP_VALUES * WARMUP_TIME));
		
		start = System.nanoTime();
		for (int i = 0; i < STEPS; i++) {
			int v = random.nextInt(MAX_VALUE);
			arucas_map.put(context, N_CACHE[v], S_CACHE[v]);
		}
		final long INSERT_VALUES = System.nanoTime() - start;
		times.add(new Time("Insert : %.4f ns", INSERT_VALUES * TIME));
		
		start = System.nanoTime();
		for (int i = 0; i < STEPS; i++) {
			int v = random.nextInt(MAX_VALUE);
			arucas_map.get(context, N_CACHE[v]);
		}
		final long LOOKUP_VALUES = System.nanoTime() - start;
		times.add(new Time("Lookup : %.4f ns", LOOKUP_VALUES * TIME));
		
		start = System.nanoTime();
		for (int i = 0; i < STEPS; i++) {
			int v = random.nextInt(MAX_VALUE);
			arucas_map.remove(context, N_CACHE[v]);
		}
		final long REMOVE_VALUES = System.nanoTime() - start;
		times.add(new Time("Removal: %.4f ns", REMOVE_VALUES * TIME));
		
		return times;
	}
	
	private static List<Time> benchmark_java_map(final int STEPS, final int WARMUP, final int MAX_VALUE, Random random) throws Exception {
		final double TIME = 1.0 / (double)STEPS;
		final double WARMUP_TIME = 1.0 / (double)WARMUP;
		List<Time> times = new ArrayList<>();
		
		JavaCache.generate(MAX_VALUE);
		final int[] N_CACHE = JavaCache.N_CACHE;
		final String[] S_CACHE = JavaCache.S_CACHE;
		
		long start;
		
		// Warmup
		start = System.nanoTime();
		Map<Integer, String> java_map = new ConcurrentHashMap<>();
		for (int i = 0; i < WARMUP; i++) {
			int v = random.nextInt(MAX_VALUE);
			java_map.put(N_CACHE[v], S_CACHE[v]);
		}
		java_map.clear();
		final long WARMUP_VALUES = System.nanoTime() - start;
		times.add(new Time("Warmup : %.4f ns", WARMUP_VALUES * WARMUP_TIME));
		
		start = System.nanoTime();
		for (int i = 0; i < STEPS; i++) {
			int v = random.nextInt(MAX_VALUE);
			java_map.put(N_CACHE[v], S_CACHE[v]);
		}
		final long INSERT_VALUES = System.nanoTime() - start;
		times.add(new Time("Insert : %.4f ns", INSERT_VALUES * TIME));
		
		start = System.nanoTime();
		for (int i = 0; i < STEPS; i++) {
			int v = random.nextInt(MAX_VALUE);
			java_map.get(N_CACHE[v]);
		}
		final long LOOKUP_VALUES = System.nanoTime() - start;
		times.add(new Time("Lookup : %.4f ns", LOOKUP_VALUES * TIME));
		
		start = System.nanoTime();
		for (int i = 0; i < STEPS; i++) {
			int v = random.nextInt(MAX_VALUE);
			java_map.remove(N_CACHE[v]);
		}
		final long REMOVE_VALUES = System.nanoTime() - start;
		times.add(new Time("Removal: %.4f ns", REMOVE_VALUES * TIME));
		
		return times;
	}
	
	private static class Time {
		String format;
		double time;
		
		Time(String format, double time) {
			this.time = time;
			this.format = format;
		
		}
		
		@Override
		public String toString() {
			return String.format(Locale.US,format, time);
		}
	}
	
	private static class ArucasCache {
		static NumberValue[] N_CACHE;
		static StringValue[] S_CACHE;
		
		public static void generate(int count) {
			N_CACHE = new NumberValue[count];
			S_CACHE = new StringValue[count];
			for (int i = 0; i < count; i++) {
				N_CACHE[i] = NumberValue.of(i);
				S_CACHE[i] = StringValue.of(Integer.toString(i));
			}
		}
	}
	
	private static class JavaCache {
		static int[] N_CACHE;
		static String[] S_CACHE;
		
		public static void generate(int count) {
			N_CACHE = new int[count];
			S_CACHE = new String[count];
			for (int i = 0; i < count; i++) {
				N_CACHE[i] = i;
				S_CACHE[i] = Integer.toString(i);
			}
		}
	}
}
