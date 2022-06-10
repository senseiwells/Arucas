package me.senseiwells.test;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
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
			X = listA == listB;
			""", "X"
		));
		assertEquals("false", ArucasHelper.runSafeFull(
			"""
			listA = [ 'a', 'b', 'c', 'd', [ 1, 2, 3, 4, [ 5, 6, 7 ] ] ];
			listB = [ 'a', 'b', 'c', '(different)', [ 1, 2, 3, 4, [ 5, 6, 7 ] ] ];
			X = listA == listB;
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
		assertThrows(RuntimeError.class, () -> ArucasHelper.runUnsafe("a, b, c, d = [1, 2, 3];"));
		assertThrows(RuntimeError.class, () -> ArucasHelper.runUnsafe("a, b = [1];"));
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

	@Test
	public void testBracketSyntax() {
		assertThrows(CodeError.class, () -> ArucasHelper.compile("[1, 2 ,3][2;"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("[1, 2 ,3][2, 3];"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("[1, 2 ,3[2, 3, 4, 5];"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("[1, 2 ,3] = [2, 3, 4, 5];"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("[1, 2 ,3][] = [2, 3, 4, 5];"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("[1, 2 ,3][2, 3, 4, 5] = [1];"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("[1, 2 ,3][2][] = [1];"));

		assertThrows(CodeError.class, () -> ArucasHelper.compile(
			"""
			class E {
				operator [ (accessor) { }
			}
			"""
		));

		assertThrows(CodeError.class, () -> ArucasHelper.compile(
			"""
			class E {
				operator [] () { }
			}
			"""
		));

		assertEquals("10", ArucasHelper.runSafe(
			"""
			class E {
				operator [] (a) { }
				operator [] (a, b) { }
			}
			return 10;
			"""
		));
	}

	@Test
	public void testBracketAccessing() {
		assertThrows(RuntimeError.class, () -> ArucasHelper.runUnsafe("[1, 2, 3][3];"));
		assertThrows(RuntimeError.class, () -> ArucasHelper.runUnsafe("[1, 2, 3][3] = 0;"));

		assertEquals("wow", ArucasHelper.runSafe("return [[[['wow']]]][0][0][0][0];"));
		assertEquals("1", ArucasHelper.runSafe("return [1, 2, 3][0];"));

		assertEquals("1", ArucasHelper.runSafe(
   			"""
			class E {
				var list;
			}
			   
			e = new E();
			e.list = [1, 2, 3];
			return e.list[0];
			"""
		));

		assertEquals("2", ArucasHelper.runSafe(
			"""
			class E {
				var list;
			}
			   
			e = new E();
			e.list = [e, 2, 3];
			return e.list[0].list[0].list[0].list[0].list[1];
			"""
		));

		assertEquals("3", ArucasHelper.runSafe(
		"""
			class E {
				var list;
			}
			   
			e = new E();
			e.list = [e, [e], 3];
			return e.list[1][0].list[1][0].list[0].list[2];
			"""
		));

		assertEquals("3", ArucasHelper.runSafe(
		"""
			class E {
				var list;
			   
				fun getList() {
					return this.list;
				}
			}
			   
			e = new E();
			e.list = [e, [e], fun() { return [1, 2, 3]; }];
			return e.getList()[1][0].getList()[1][0].getList()[0].getList()[2]()[2];
			"""
		));
	}

	@Test
	public void testBracketAssigning() {
		assertEquals("1", ArucasHelper.runSafe("return [1, 2, 3][0] = 5;"));

		assertEquals("aaa", ArucasHelper.runSafe(
		"""
			class E {
				var list;
				
				fun getList() {
					return this.list;
				}
			}
			e = new E();
			e.list = [e, [e], fun() { return [1, 2, 3]; }];
			e.getList()[1][0].getList()[1] = "aaa";
			return e.list[1];
			"""
		));

		assertEquals("4", ArucasHelper.runSafe(
			"""
			list = [1, 2, 3];
			a, b, c, list[0] = [1, 2, 3, 4];
			return list[0];
			"""
		));

		assertEquals("[5]", ArucasHelper.runSafe(
			"""
			list = [1];
			list[0], b, c = [5, 4, 3];
			return list;
			"""
		));
	}
}
