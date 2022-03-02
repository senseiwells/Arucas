package me.senseiwells.test;

import me.senseiwells.arucas.throwables.CodeError;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

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

	@Test
	public void testUnpacking() {
		assertThrows(CodeError.class, () -> ArucasHelper.compile("a, b, = [1, 2];"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile(", a, b = [1, 2];"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("1, 2 = [1, 2];"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile(
			"""
			class E {
				static var A, B = [1, 2];
			}
			"""
		));
		assertThrows(CodeError.class, () -> ArucasHelper.compile(
			"""
			class E { }
			E, a = [1, 2];
			"""
		));
		assertEquals("[\"a\", \"b\"]", ArucasHelper.runSafe(
			"""
			a = 'a';
			b = 'b';
			a, b = [b, a];
			return [b, a];
			"""
		));
		assertEquals("2", ArucasHelper.runSafe(
			"""
			a = 0;
			b = [1, 2];
			c = [a, b];
			d, e = c;
			f, g = e;
			return g;
			"""
		));
		assertEquals("3", ArucasHelper.runSafe(
			"""
			class E {
				var a;
			}
			(e = new E()).a, b = [1, 2];
			return e.a + b;
			"""
		));
		assertEquals("ohell", ArucasHelper.runSafe(
			"""
			class E {
				static var a;
				var c;
			}
			e = new E();
			E.a, e.c, h = ["he", "ll", "o"];
			return h + E.a + e.c;
			"""
		));
		assertEquals("10", ArucasHelper.runSafe(
			"""
			l = [1, 2, 3, 4, (a = 5)];
			return a + l.get(4);
			"""
		));
		assertEquals("6", ArucasHelper.runSafe(
			"""
			class E {
				static var a;
				static var b;
				static var c;
			}
			E.a, E.b, E.c = [1, 2, 3];
			return E.a + E.b + E.c;
			"""
		));
		assertEquals("6", ArucasHelper.runSafe(
			"""
			a, b, c = [1, 2, 3];
			return a + b + c;
			"""
		));
		assertEquals("5", ArucasHelper.runSafe(
			"""
			class E {
				static var a;
				var b;
			}
			// E.a = 1,
			// E.a = new E(), E.a.b = 2,
			// c = 3
			E.a, (E.a = new E()).b, c = [1, 2, 3];
			return c + E.a.b;
			"""
		));
		assertEquals("4", ArucasHelper.runSafe(
			"""
			a = (b, c, d = [1, 2, 3]);
			return a.get(2) + b;
			"""
		));

	}
}
