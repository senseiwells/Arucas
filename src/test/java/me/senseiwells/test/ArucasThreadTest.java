package me.senseiwells.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArucasThreadTest {
	@Test
	public void stopThreadTest() {
		assertEquals("10", ArucasHelper.runSafe(
			"""
			thread = Thread.runThreaded(fun() {
				Thread.freeze();
			});
			thread.stop();
			return 10;
			"""
		));
		assertEquals("10", ArucasHelper.runSafe(
			"""
			thread = Thread.runThreaded(fun() {
				thread.stop();
			});
			return 10;
			"""
		));
	}

	@Test
	public void threadScopeTest() {
		assertEquals("[10, 11]", ArucasHelper.runSafe(
			"""
			l = [];
			thread = Thread.runThreaded(fun() l.append(10););
			sleep(50);
			l.append(11);
			return l;
			"""
		));
	}
}
