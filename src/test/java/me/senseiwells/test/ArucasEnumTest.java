package me.senseiwells.test;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class ArucasEnumTest {
	@Test
	public void testEnumSyntax() {
		assertThrows(CodeError.class, () -> ArucasHelper.compile("enum { }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("enum E { E() { } }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("enum E { A E() { } }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("enum E { A, E() { } }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("enum E { , }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("enum E { enum }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("enum E;"));
		assertEquals("A", ArucasHelper.runSafe("enum E { A, B } return E.A.getName();"));
		assertEquals("A", ArucasHelper.runSafe("enum E { A, B(), } return E.A.getName();"));
		assertEquals("A", ArucasHelper.runSafe("enum E { A, B; } return E.A.getName();"));
		assertEquals("A", ArucasHelper.runSafe("enum E { A(), B,; } return E.A.getName();"));
		assertEquals("A", ArucasHelper.runSafe("enum E { A, B,; } return E.A.getName();"));
		assertEquals("[]", ArucasHelper.runSafe("enum E { } return E.values();"));
		assertEquals("[]", ArucasHelper.runSafe("enum E { ; E() { } } return E.values();"));
		assertEquals("null", ArucasHelper.runSafe(
			"""	
			enum E {
				A("a"), B, C("c", 1), D, E("e");
			   
				static var T;
			   
				static {
					T = "10";
				}
			   
				var s;
			   
				E(s, b) {
					this.s = s;
				}
			   
				E(s) {
					this.s = s;
				}
			   
				E() {
					this.s = "empty";
				}
			   
				operator + (other) { }
			   
				operator + () { }
			   
				fun toString() {
					return this.s;
				}
			   
				static fun getT() {
					return E.T;
				}
			}
			"""
		));
	}

	@Test
	public void testEnumAssignability() {
		assertThrows(RuntimeError.class, () -> ArucasHelper.runUnsafe("enum E { A } E.A = '';"));
		assertEquals("10", ArucasHelper.runSafe(
		"""
		enum E {
			A;
			static var T;
		}
		   
		E.T = 10;
		return E.T;
		"""
		));
	}

	@Test
	public void testArbitraryParameters() {
		assertEquals("[1, 2, 3]", ArucasHelper.runSafe(
			"""
			enum E {
				A(1, 2, 3),
				B;
				
				var values;
				E(a...) {
					this.values = a;
				}
			}
			   
			return E.A.values.concat(E.B.values);
			"""
		));
	}
}
