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
		assertEquals("foobarbaz", ArucasHelper.runSafe("return '%sbar%s'.formatted(['foo', 'baz']);"));
		assertEquals("true", ArucasHelper.runSafe("return 'foobarbaz'.contains('bar');"));
		assertEquals("false", ArucasHelper.runSafe("return 'foobarbaz'.contains('far');"));
		assertEquals("foo bar baz", ArucasHelper.runSafe("return '   foo bar baz    '.strip();"));
		assertEquals("FooBaz", ArucasHelper.runSafe("return 'fooBaz'.capitalise();"));
		assertEquals("[\"h\", \"e\", \"ll\", \"o\"]", ArucasHelper.runSafe("return 'h:e:ll:o'.split(':');"));
		assertEquals("rfo", ArucasHelper.runSafe("return 'barfoo'.subString(2, 5);"));
		assertEquals("String", ArucasHelper.runSafe("return ''.getValueType();"));
		assertEquals("true", ArucasHelper.runSafe("return ''.instanceOf('String');"));
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
		assertEquals("Number", ArucasHelper.runSafe("return 0.getValueType();"));
		assertEquals("true", ArucasHelper.runSafe("return 0.instanceOf('Number');"));
	}
}
