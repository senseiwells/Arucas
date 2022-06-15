package me.senseiwells.test;

import me.senseiwells.arucas.throwables.RuntimeError;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class ArucasLoopTest {
	@Test(timeout = 1000)
	public void testWhileLoop() {
		assertEquals("10", ArucasHelper.runSafe(
			"""
			while (true) {
				return 10;
			}
			"""
		));
		assertEquals("9", ArucasHelper.runSafe(
			"""
			while (false) {
				return 10;
			}
			return 9;
			"""
		));
		assertEquals("10", ArucasHelper.runSafe(
			"""
			while (true) {
				break;
			}
			return 10;
			"""
		));
		assertEquals("40", ArucasHelper.runSafe(
			"""
			i = 0;
			total = 0;
			while (i < 10) {
				if (i == 5) {
					i++;
					continue;
				}
				total = total + i;
				i++;
			}
			return total;
			"""
		));
		assertThrows(RuntimeError.class, () -> ArucasHelper.runUnsafe(
			"""
			fun breakFunction() {
				break;
			}
			
			while (true) {
				breakFunction();
			}
			"""
		));
	}

	@Test
	public void testForLoop() {
		assertEquals("45", ArucasHelper.runSafe(
			"""
			total = 0;
			for (i = 0; i < 10; i++) {
				total = total + i;
			}
			return total;
			"""
		));
		assertEquals("0", ArucasHelper.runSafe(
			"""
			total = 0;
			for (i = 0; i < 10; i++) {
				continue;
			}
			return total;
			"""
		));
		assertEquals("10", ArucasHelper.runSafe(
			"""
			fun call() {
				i++;
			}
			
			i = 0;
			for (; i < 10; call()) {
				continue;
			}
			return i;
			"""
		));
		assertEquals("0", ArucasHelper.runSafe(
			"""
			fun call() {
				i++;
			}
			
			i = 0;
			for (; i < 10; call()) {
				break;
			}
			return i;
			"""
		));
	}

	@Test
	public void testForEachLoop() {
		assertEquals("6", ArucasHelper.runSafe(
			"""
			fun doSomething() {
				return [1, 2, 3];
			}
			
			total = 0;
			foreach (thing : doSomething()) {
				total = total + thing;
			}
			return total;
			"""
		));
	}
}
