package me.senseiwells.benchmark;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.arucas.utils.impl.ArucasOrderedMap;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.test.ArucasHelper;
import org.openjdk.jmh.annotations.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Benchmarking the speed of ArucasOrderedMap, LinkedHashMap, and ConcurrentHashMap
 * LinkedHashMap and ConcurrentHashMap should be almost the exact same speed.
 * ArucasOrderedMap (according to my benchmarks) is around 1% slower than the Java maps
 */
public class BenchmarkArucasMap {
	static final int STEPS = 1_000_000;
	static final int MAX_VALUE = 10_000;

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@Fork(value = 2)
	@Warmup(iterations = 3)
	@Measurement(iterations = 5)
	public void benchmarkArucasMap() throws CodeError {
		final Random RANDOM = new Random();
		final ArucasCache CACHE = new ArucasCache(MAX_VALUE);
		final Context CONTEXT = ArucasHelper.createContext();
		final ArucasMap ARUCAS_MAP = new ArucasOrderedMap();

		RANDOM.setSeed(0L);

		for (int i = 0; i < STEPS; i++) {
			int variable = RANDOM.nextInt(MAX_VALUE);
			ARUCAS_MAP.put(CONTEXT, CACHE.NUMBER_CACHE[variable], CACHE.STRING_CACHE[variable]);
		}
		for (int i = 0; i < STEPS; i++) {
			int variable = RANDOM.nextInt(MAX_VALUE);
			ARUCAS_MAP.get(CONTEXT, CACHE.NUMBER_CACHE[variable]);
		}
		for (int i = 0; i < STEPS; i++) {
			int variable = RANDOM.nextInt(MAX_VALUE);
			ARUCAS_MAP.remove(CONTEXT, CACHE.NUMBER_CACHE[variable]);
		}
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@Fork(value = 2)
	@Warmup(iterations = 3)
	@Measurement(iterations = 5)
	public void benchmarkJavaMap() {
		final Random RANDOM = new Random();
		final JavaCache CACHE = new JavaCache(MAX_VALUE);
		// noinspection all
		final Map<Integer, String> JAVA_MAP = new LinkedHashMap<>();

		RANDOM.setSeed(0L);

		for (int i = 0; i < STEPS; i++) {
			int variable = RANDOM.nextInt(MAX_VALUE);
			JAVA_MAP.put(CACHE.NUMBER_CACHE[variable], CACHE.STRING_CACHE[variable]);
		}
		for (int i = 0; i < STEPS; i++) {
			int variable = RANDOM.nextInt(MAX_VALUE);
			JAVA_MAP.get(CACHE.NUMBER_CACHE[variable]);
		}
		for (int i = 0; i < STEPS; i++) {
			int variable = RANDOM.nextInt(MAX_VALUE);
			JAVA_MAP.remove(CACHE.NUMBER_CACHE[variable]);
		}
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@Fork(value = 2)
	@Warmup(iterations = 3)
	@Measurement(iterations = 5)
	public void benchmarkJavaConcurrentMap() {
		final Random RANDOM = new Random();
		final JavaCache CACHE = new JavaCache(MAX_VALUE);
		// noinspection all
		final Map<Integer, String> JAVA_MAP = new ConcurrentHashMap<>();

		RANDOM.setSeed(0L);

		for (int i = 0; i < STEPS; i++) {
			int variable = RANDOM.nextInt(MAX_VALUE);
			JAVA_MAP.put(CACHE.NUMBER_CACHE[variable], CACHE.STRING_CACHE[variable]);
		}
		for (int i = 0; i < STEPS; i++) {
			int variable = RANDOM.nextInt(MAX_VALUE);
			JAVA_MAP.get(CACHE.NUMBER_CACHE[variable]);
		}
		for (int i = 0; i < STEPS; i++) {
			int variable = RANDOM.nextInt(MAX_VALUE);
			JAVA_MAP.remove(CACHE.NUMBER_CACHE[variable]);
		}
	}

	private static class ArucasCache {
		final NumberValue[] NUMBER_CACHE;
		final StringValue[] STRING_CACHE;

		private ArucasCache(final int count) {
			this.NUMBER_CACHE = new NumberValue[count];
			this.STRING_CACHE = new StringValue[count];
			for (int i = 0; i < count; i++) {
				this.NUMBER_CACHE[i] = NumberValue.of(i);
				this.STRING_CACHE[i] = StringValue.of(Integer.toString(i));
			}
		}
	}

	private static class JavaCache {
		final int[] NUMBER_CACHE;
		final String[] STRING_CACHE;

		private JavaCache(final int count) {
			this.NUMBER_CACHE = new int[count];
			this.STRING_CACHE = new String[count];
			for (int i = 0; i < count; i++) {
				this.NUMBER_CACHE[i] = i;
				this.STRING_CACHE[i] = Integer.toString(i);
			}
		}
	}
}
