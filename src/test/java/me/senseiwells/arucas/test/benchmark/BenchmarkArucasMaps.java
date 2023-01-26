package me.senseiwells.arucas.test.benchmark;

import me.senseiwells.arucas.api.ArucasAPI;
import me.senseiwells.arucas.builtin.NumberDef;
import me.senseiwells.arucas.builtin.StringDef;
import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.utils.impl.ArucasOrderedMap;
import org.openjdk.jmh.annotations.*;

import java.util.LinkedHashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * From my testing as of <code>10.08.22 (dd.mm.yy)</code>:
 * {@link ArucasOrderedMap} is <code>~2x</code> slower than {@link LinkedHashMap}.
 * After profiling it seems {@link LinkedHashMap#putVal(int, Object, Object, boolean, boolean)} takes
 * a lot more time than the others.
 */
@SuppressWarnings("JavadocReference")
@State(Scope.Thread)
public class BenchmarkArucasMaps {
	static final int STEPS = 1_000_000;
	static final int MAX_VALUE = 10_000;

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@Fork(value = 2)
	@Warmup(iterations = 2)
	@Measurement(iterations = 5)
	public void benchmarkArucasMap() {
		var api = new ArucasAPI.Builder().addDefault().build();
		var interpreter = Interpreter.dummy(api);

		var random = new Random(0);
		var cache = new ArucasCache(MAX_VALUE, interpreter);
		var map = new ArucasOrderedMap();

		for (int i = 0; i < STEPS; i++) {
			var variable = random.nextInt(MAX_VALUE);
			map.put(interpreter, cache.numberCache[variable], cache.stringCache[variable]);
		}

		for (int i = 0; i < STEPS; i++) {
			var variable = random.nextInt(MAX_VALUE);
			map.get(interpreter, cache.numberCache[variable]);
		}

		for (int i = 0; i < STEPS; i++) {
			var variable = random.nextInt(MAX_VALUE);
			map.remove(interpreter, cache.numberCache[variable]);
		}
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@Fork(value = 2)
	@Warmup(iterations = 2)
	@Measurement(iterations = 5)
	public void benchmarkJavaLinkedHashMap() {
		var random = new Random(0);
		var cache = new JavaCache(MAX_VALUE);
		@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
		var map = new LinkedHashMap<Integer, String>();

		for (int i = 0; i < STEPS; i++) {
			var variable = random.nextInt(MAX_VALUE);
			map.put(cache.numberCache[variable], cache.stringCache[variable]);
		}
		for (int i = 0; i < STEPS; i++) {
			var variable = random.nextInt(MAX_VALUE);
			map.get(cache.numberCache[variable]);
		}
		for (int i = 0; i < STEPS; i++) {
			var variable = random.nextInt(MAX_VALUE);
			map.remove(cache.numberCache[variable]);
		}
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@Fork(value = 2)
	@Warmup(iterations = 2)
	@Measurement(iterations = 5)
	public void benchmarkJavaConcurrentMap() {
		var random = new Random(0);
		var cache = new JavaCache(MAX_VALUE);
		@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
		var map = new ConcurrentHashMap<Integer, String>();

		for (int i = 0; i < STEPS; i++) {
			var variable = random.nextInt(MAX_VALUE);
			map.put(cache.numberCache[variable], cache.stringCache[variable]);
		}
		for (int i = 0; i < STEPS; i++) {
			var variable = random.nextInt(MAX_VALUE);
			map.get(cache.numberCache[variable]);
		}
		for (int i = 0; i < STEPS; i++) {
			var variable = random.nextInt(MAX_VALUE);
			map.remove(cache.numberCache[variable]);
		}
	}

	private static class ArucasCache {
		final ClassInstance[] numberCache;
		final ClassInstance[] stringCache;

		private ArucasCache(int count, Interpreter interpreter) {
			this.numberCache = new ClassInstance[count];
			this.stringCache = new ClassInstance[count];
			for (int i = 0; i < count; i++) {
				this.numberCache[i] = interpreter.create(NumberDef.class, (double) i);
				this.stringCache[i] = interpreter.create(StringDef.class, String.valueOf(i));
			}
		}
	}

	private static class JavaCache {
		final Integer[] numberCache;
		final String[] stringCache;

		private JavaCache(int count) {
			this.numberCache = new Integer[count];
			this.stringCache = new String[count];
			for (int i = 0; i < count; i++) {
				this.numberCache[i] = i;
				this.stringCache[i] = String.valueOf(i);
			}
		}
	}
}
