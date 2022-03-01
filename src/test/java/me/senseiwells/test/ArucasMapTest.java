package me.senseiwells.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArucasMapTest {
	@Test
	public void testMapPutReference() {
		assertEquals("[1, 2, 3, 4]", ArucasHelper.runSafeFull(
			"""
			map = {};
			list = [ 1, 2, 3 ];
			map.put('test', list);
			oldList = map.put('test', 'NewValue');
			
			oldList.append(4);
			""", "list"
		));
		assertEquals("[3, 2, 1]", ArucasHelper.runSafeFull(
			"""
			map = { 'test': [ 3, 2, 1 ] };
			X = map.put('test', 123);
			""", "X"
		));
		assertEquals("null", ArucasHelper.runSafeFull(
			"""
			map = {};
			X = map.put('test', 123);
			""", "X"
		));
	}
	
	@Test
	public void testMapGetReference() {
		assertEquals("[1, 2, 3, 4]", ArucasHelper.runSafeFull(
			"""
			map = { 'test': [ 1, 2, 3 ] };
			map.get('test').append(4);
			X = map.get('test');
			""", "X"
		));
	}
	
	@Test
	public void testMapPutIfAbsent() {
		assertEquals("[1, 2, 3]", ArucasHelper.runSafeFull(
			"""
			map = { 'test': [ 1, 2, 3 ] };
			map.putIfAbsent('test', 123);
			X = map.get('test');
			""", "X"
		));
		assertEquals("123", ArucasHelper.runSafeFull(
			"""
			map = {};
			map.putIfAbsent('test', 123);
			X = map.get('test');
			""", "X"
		));
		assertEquals("[1, 2, 3]", ArucasHelper.runSafeFull(
			"""
			map = { 'test': [ 1, 2, 3 ] };
			X = map.putIfAbsent('test', 123);
			""", "X"
		));
	}
	
	@Test
	public void testMapClear() {
		assertEquals("{}", ArucasHelper.runSafeFull(
			"""
			map = { 'a': 1, 'b': 2, 'c': 3 };
			map.clear();
			""", "map"
		));
	}
	
	@Test
	public void testMapRemoveReference() {
		assertEquals("[4, 3, 2, 1]", ArucasHelper.runSafeFull(
			"""
			list = [ 4, 3, 2 ];
			map = { 'test': list };
			map.remove('test').append(1);
			""", "list"
		));
		assertEquals("123", ArucasHelper.runSafeFull(
			"""
			map = { 'test': 123 };
			X = map.remove('test');
			""", "X"
		));
		assertEquals("{}", ArucasHelper.runSafeFull(
			"""
			map = { 'test': 123 };
			map.remove('test');
			""", "map"
		));
	}
	
	@Test
	public void testMapEquality() {
		assertEquals("true", ArucasHelper.runSafeFull(
			"""
			mapA = { 'a': 1, 'b': 2, 'c': 3, 'd': [ 1, 2, 3, 4, [ 5, 6, 7 ] ] };
			mapB = { 'c': 3, 'b': 2, 'a': 1, 'd': [ 1, 2, 3, 4, [ 5, 6, 7 ] ] };
			X = mapA.equals(mapB);
			""", "X"
		));
		assertEquals("false", ArucasHelper.runSafeFull(
			"""
			mapA = { 'a': 1, 'b': (5), 'c': 3, 'd': [ 1, 2, 3, 4, [ 5, 6, 7 ] ] };
			mapB = { 'c': 3, 'b': 2, 'a': 1, 'd': [ 1, 2, 3, 4, [ 5, 6, 7 ] ] };
			X = mapA.equals(mapB);
			""", "X"
		));
	}
	
	@Test
	public void testMapCopyReference() {
		assertEquals("[1, 2, 4]", ArucasHelper.runSafeFull(
			"""
			list = [ 1, 2 ];
			map = { 'a': list, 'b': { 'c': 123 } };
			mapCopy = map.copy();
			mapCopy.get('a').append(4);
			X = map.get('a');
			""", "X"
		));
		assertEquals("{\"c\": 321}", ArucasHelper.runSafeFull(
			"""
			list = [ 1, 2 ];
			map = { 'a': list, 'b': { 'c': 123 } };
			mapCopy = map.copy();
			mapCopy.get('b').put('c', 321);
			X = map.get('b');
			""", "X"
		));
	}
	
	@Test
	public void testMapFormatting() {
		assertEquals("{\"a\": 123}", ArucasHelper.runSafe("return {'a': 123};"));
		assertEquals("{321: \"b\"}", ArucasHelper.runSafe("return {321: 'b'};"));
		assertEquals("{<list>: <map>}", ArucasHelper.runSafe("return {[]: {}};"));
	}
}
