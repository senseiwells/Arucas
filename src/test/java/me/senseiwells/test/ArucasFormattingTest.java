package me.senseiwells.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArucasFormattingTest {
	@Test
	public void testMapFormatting() {
		assertEquals("{\"a\": 123}", ArucasHelper.runSafe("return {'a': 123};"));
		assertEquals("{321: \"b\"}", ArucasHelper.runSafe("return {321: 'b'};"));
		assertEquals("{<list>: <map>}", ArucasHelper.runSafe("return {[]: {}};"));
	}
	
	@Test
	public void testListFormatting() {
		assertEquals("[\"a\", 123]", ArucasHelper.runSafe("return ['a', 123];"));
		assertEquals("[321, \"b\"]", ArucasHelper.runSafe("return [321, 'b'];"));
		assertEquals("[<list>, <map>]", ArucasHelper.runSafe("return [[], {}];"));
	}
	
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
