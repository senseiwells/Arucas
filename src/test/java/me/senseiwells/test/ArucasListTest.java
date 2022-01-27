package me.senseiwells.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArucasListTest {
	@Test
	public void testListGetReference() {
		assertEquals("[1, 2, 3, 4]", ArucasHelper.runSafeFull(
			"""
			list = [ [ 1, 2, 3 ] ];
			list.get(0).append(4);
			X = list.get(0);
			""", "X"
		));
	}
	
	@Test
	public void testListEquality() {
		assertEquals("true", ArucasHelper.runSafeFull(
			"""
			listA = [ 'a', 'b', 'c', 'd', [ 1, 2, 3, 4, [ 5, 6, 7 ] ] ];
			listB = [ 'a', 'b', 'c', 'd', [ 1, 2, 3, 4, [ 5, 6, 7 ] ] ];
			X = listA.equals(listB);
			""", "X"
		));
		assertEquals("false", ArucasHelper.runSafeFull(
			"""
			listA = [ 'a', 'b', 'c', 'd', [ 1, 2, 3, 4, [ 5, 6, 7 ] ] ];
			listB = [ 'a', 'b', 'c', '(different)', [ 1, 2, 3, 4, [ 5, 6, 7 ] ] ];
			X = listA.equals(listB);
			""", "X"
		));
	}
	
	@Test
	public void testListFormatting() {
		assertEquals("[\"a\", 123]", ArucasHelper.runSafe("return ['a', 123];"));
		assertEquals("[321, \"b\"]", ArucasHelper.runSafe("return [321, 'b'];"));
		assertEquals("[<list>, <map>]", ArucasHelper.runSafe("return [[], {}];"));
	}
}
