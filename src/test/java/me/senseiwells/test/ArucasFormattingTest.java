package me.senseiwells.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArucasFormattingTest {
	// If possible move these into the value tests
	
	@Test
	public void testBooleanFormatting() {
		assertEquals("true", ArucasHelper.runSafe("return true;"));
		assertEquals("[false]", ArucasHelper.runSafe("return [false];"));
		assertEquals("{true: false}", ArucasHelper.runSafe("return {true: false};"));
	}
	
	@Test
	public void testClassFormatting() {
		assertEquals("[test]", ArucasHelper.runSafeFull(
			"""
			class A {
				fun toString() {
					return 'test';
				}
			}
			X = [new A()];
			""", "X"
		));
	}
}
