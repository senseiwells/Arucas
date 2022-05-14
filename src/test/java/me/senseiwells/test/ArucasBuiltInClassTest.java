package me.senseiwells.test;

import org.junit.Test;

import static org.junit.Assert.*;

public class ArucasBuiltInClassTest {
	@Test
	public void testStringClass() {
		assertEquals("[\"h\", \"e\", \"l\", \"l\", \"o\"]", ArucasHelper.runSafe("return 'hello'.toList();"));
		assertEquals("footestbaztest", ArucasHelper.runSafe("return 'foobarbazbar'.replaceAll('bar', 'test'); "));
		assertEquals("FOOBAR", ArucasHelper.runSafe("return 'foObAr'.uppercase();"));
		assertEquals("foobar", ArucasHelper.runSafe("return 'foObAr'.lowercase();"));
		assertEquals("90.1", ArucasHelper.runSafe("return '90.1'.toNumber();"));
		assertEquals("foobarbaz", ArucasHelper.runSafe("return '%sbar%s'.format('foo', 'baz');"));
		assertEquals("true", ArucasHelper.runSafe("return 'foobarbaz'.contains('bar');"));
		assertEquals("false", ArucasHelper.runSafe("return 'foobarbaz'.contains('far');"));
		assertEquals("foo bar baz", ArucasHelper.runSafe("return '   foo bar baz    '.strip();"));
		assertEquals("FooBaz", ArucasHelper.runSafe("return 'fooBaz'.capitalise();"));
		assertEquals("[\"h\", \"e\", \"ll\", \"o\"]", ArucasHelper.runSafe("return 'h:e:ll:o'.split(':');"));
		assertEquals("rfo", ArucasHelper.runSafe("return 'barfoo'.subString(2, 5);"));
		assertEquals("String", ArucasHelper.runSafe("return Type.of('').getName();"));
		assertEquals("true", ArucasHelper.runSafe("return ''.instanceOf(String.type);"));
	}

	@Test
	public void testNumberClass() {
		assertEquals("10", ArucasHelper.runSafe("return 9.5.round();"));
		assertEquals("10", ArucasHelper.runSafe("return 9.1.ceil();"));
		assertEquals("10", ArucasHelper.runSafe("return 10.9.floor();"));
		assertEquals("true", ArucasHelper.runSafe("return (1/0).isInfinite();"));
		assertEquals("false", ArucasHelper.runSafe("return 1.isInfinite();"));
		assertEquals("true", ArucasHelper.runSafe("return (0/0).isNaN();"));
		assertEquals("false", ArucasHelper.runSafe("return 1.isNaN();"));
		assertEquals("Number", ArucasHelper.runSafe("return Type.of(0).getName();"));
		assertEquals("true", ArucasHelper.runSafe("return 0.instanceOf(Number.type);"));
	}

	@Test
	public void testListClass() {
		assertEquals("foo", ArucasHelper.runSafe("return ['bar', 'foo'].get(1);"));
		assertEquals("10", ArucasHelper.runSafe("return [5, 10, 15].remove(1);"));
		assertEquals("[2, \"foo\"]", ArucasHelper.runSafe("return [2].append('foo');"));
		assertEquals("[\"foo\", 2]", ArucasHelper.runSafe("return [2].insert('foo', 0);"));
		assertEquals("[2, 3, 4, 5]", ArucasHelper.runSafe("return [2, 3].addAll([4, 5]);"));
		assertEquals("true", ArucasHelper.runSafe("return ['foo', 'bar'].contains('bar');"));
		assertEquals("false", ArucasHelper.runSafe("return ['foo', 'baz'].contains('bar');"));
		assertEquals("true", ArucasHelper.runSafe("return ['foo', 'bar', 'baz'].containsAll(['foo', 'baz']);"));
		assertEquals("true", ArucasHelper.runSafe("return [].isEmpty();"));
		assertEquals("false", ArucasHelper.runSafe("return [0].isEmpty();"));
		assertEquals("[]", ArucasHelper.runSafe("l = [1, 2, 3]; l.clear(); return l;"));
		assertEquals("List", ArucasHelper.runSafe("return Type.of([]).getName();"));
		assertEquals("true", ArucasHelper.runSafe("return [].instanceOf(List.type);"));
	}

	@Test
	public void testMapClass() {
		assertEquals("foo", ArucasHelper.runSafe("return {'a' : 'foo', 'b' : 'bar'}.get('a');"));
		assertEquals("[\"a\"]", ArucasHelper.runSafe("return {'a' : 'foo'}.getKeys();"));
		assertEquals("[\"foo\"]", ArucasHelper.runSafe("return {'a' : 'foo'}.getValues();"));
		assertEquals("{\"a\": \"foo\"}", ArucasHelper.runSafe("m = {}; m.put('a', 'foo'); return m;"));
		assertEquals("{\"a\": \"bar\"}", ArucasHelper.runSafe("m = {'a' : 'bar'}; m.putIfAbsent('a', 'foo'); return m;"));
		assertEquals("{\"b\": \"foo\"}", ArucasHelper.runSafe("m = {}; m.putAll({'b' : 'foo'}); return m;"));
		assertEquals("{\"a\": \"foo\"}", ArucasHelper.runSafe("m = {'a' : 'foo', 'b' : 'bar'}; m.remove('b'); return m;"));
		assertEquals("{}", ArucasHelper.runSafe("m = {'a' : 'foo'}; m.clear(); return m;"));
		assertEquals("true", ArucasHelper.runSafe("return {}.isEmpty();"));
		assertEquals("Map", ArucasHelper.runSafe("return Type.of({}).getName();"));
		assertEquals("true", ArucasHelper.runSafe("return {}.instanceOf(Map.type);"));
	}

	@Test
	public void testMathClass() {
		assertEquals("10", ArucasHelper.runSafe("return Math.round(9.5);"));
		assertEquals("10", ArucasHelper.runSafe("return Math.ceil(9.1);"));
		assertEquals("10", ArucasHelper.runSafe("return Math.floor(10.9);"));
		assertEquals("10", ArucasHelper.runSafe("return Math.sqrt(100);"));
		assertEquals("10", ArucasHelper.runSafe("return Math.abs(-10);"));
		assertEquals("10", ArucasHelper.runSafe("return Math.mod(21, 11);"));
		assertEquals("10", ArucasHelper.runSafe("return Math.max(10, 9);"));
		assertEquals("10", ArucasHelper.runSafe("return Math.min(10.1, 10);"));
		assertEquals("10", ArucasHelper.runSafe("return Math.clamp(0, 10, 20);"));
		assertEquals("10", ArucasHelper.runSafe("return Math.toRadians(1800)/Math.pi;"));
		assertEquals("10", ArucasHelper.runSafe("return Math.toDegrees(Math.pi/18);"));
		assertEquals("10", ArucasHelper.runSafe("return Math.log(Math.e ^ 10);"));
		assertEquals("10", ArucasHelper.runSafe("return Math.log(3, 3 ^ 10);"));
		assertEquals("10", ArucasHelper.runSafe("return Math.log10(10 ^ 10);"));
		assertEquals("10", ArucasHelper.runSafe("return 10 * Math.sin(Math.pi/2);"));
		assertEquals("10", ArucasHelper.runSafe("return 10 * Math.cos(0);"));
		assertEquals("10", ArucasHelper.runSafe("return 10 * Math.tan(Math.pi/4);"));
		assertEquals("3.14159265359", ArucasHelper.runSafe("return Math.pi;"));
		assertEquals("2.718281828459", ArucasHelper.runSafe("return Math.e;"));
		assertEquals("1.414213562373", ArucasHelper.runSafe("return Math.root2;"));
	}

	@Test(timeout = 1000)
	public void testThreadClass() {
		assertEquals("true", ArucasHelper.runSafe("return Thread.getCurrentThread().isAlive();"));
		assertEquals("false", ArucasHelper.runSafe("t = Thread.runThreaded(fun() { }); sleep(100); return t.isAlive();"));
		assertEquals("foo", ArucasHelper.runSafe("t = Thread.runThreaded('foo', fun() { }); return t.getName();"));
		assertEquals("Thread", ArucasHelper.runSafe("return Type.of(Thread.getCurrentThread()).getName();"));
		assertEquals("true", ArucasHelper.runSafe("return Thread.getCurrentThread().instanceOf(Thread.type);"));
	}
}
