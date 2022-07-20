package me.senseiwells.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArucasLazyTest {
	@Test
	public void testAsync() {
		assertEquals("10", ArucasHelper.runSafe("return Thread.runAsync(fun() { return 10; });"));
		assertEquals("true", ArucasHelper.runSafe(
			"""
			time = getMilliTime();
			async = Thread.runAsync(fun() {
				sleep(100);
				return 0;
			});
			return (async + getMilliTime() - time) > 100;
			"""
		));
		assertEquals("false", ArucasHelper.runSafe(
			"""
			async = Thread.runAsync(fun() {
				Thread.freeze();
			});
			return async.isReady();
			"""
		));
		assertEquals("true", ArucasHelper.runSafe(
			"""
			async = Thread.runAsync(fun() {
				return 10;
			});
			async + 0;
			return async.isReady();
			"""
		));
	}

	@Test
	public void testLazy() {
		assertEquals("0", ArucasHelper.runSafe(
			"""
			X = 0;
			value = lazy { X++; };
			return X;
			"""
		));
		assertEquals("1", ArucasHelper.runSafe(
			"""
			X = 0;
			value = lazy { X++; };
			value == null;
			return X;
			"""
		));
		assertEquals("1", ArucasHelper.runSafe(
			"""
			X = 0;
			value = lazy { X++; };
			value == null;
			value == null;
			return X;
			"""
		));

		assertEquals("Number", ArucasHelper.runSafe(
			"""
			E = lazy { return 10; };
			return Type.of(E).getName();
			"""
		));
		assertEquals("9", ArucasHelper.runSafe(
			"""
			E = lazy { return 10; };
			return E - 1;
			"""
		));
		assertEquals("0", ArucasHelper.runSafe(
			"""
			E = lazy { return fun() { }; };
			return len(E);
			"""
		));

		assertEquals("2", ArucasHelper.runSafe(
			"""
			X = 0;
			class E {
				var field = lazy {
					X++;
					return 0;
				};
			}
			
			e = new E();
			e.field == 0;
			e.field == 0;
			new E().field == 0;
			
			return X;
			"""
		));
	}
}
