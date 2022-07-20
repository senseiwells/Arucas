package me.senseiwells.test;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class ArucasTypeTest {
	@Test
	public void testReturnTypes() {
		assertEquals("tmp", ArucasHelper.runSafe(
			"""
			fun X(): String {
				return "tmp";
			}
			return X();
			"""
		));
		assertThrows(RuntimeError.class, () -> ArucasHelper.runUnsafe(
			"""
			fun X(): String {
				return 10;
			}
			X();
			"""
		));
		assertEquals("60", ArucasHelper.runSafe(
			"""
			fun Q(): String | Number {
				return 60;
			}
			return Q();
			"""
		));

		assertEquals("null", ArucasHelper.runSafe(
			"""
			class E {
				static fun Y(): Null {
					return null;
				}
			}
			return E.Y();
			"""
		));
		assertThrows(RuntimeError.class, () -> ArucasHelper.runUnsafe(
			"""
			class E {
				static fun Y(): Null {
					return "";
				}
			}
			return E.Y();
			"""
		));

		assertThrows(CodeError.class, () -> ArucasHelper.compile(
			"""
			class E {
				E(): Null { }
			}
			"""
		));
	}

	@Test
	public void testParameterTypes() {
		assertThrows(CodeError.class, () -> ArucasHelper.compile("fun X(a: ClassThatDoesntExist) { }"));

		assertEquals("19", ArucasHelper.runSafe(
			"""
			fun X(a: Number, b: Number) {
				return a + b;
			}
			return X(10, 9);
			"""
		));
		assertThrows(RuntimeError.class, () -> ArucasHelper.runUnsafe(
			"""
			fun X(a: Number, b: Number) {
				return a + b;
			}
			X(null, 10);
			"""
		));
		assertEquals("XA", ArucasHelper.runSafe(
			"""
			class E {
				operator + (other: String): String {
					return "X" + other;
				}
			}
			return new E() + "A";
			"""
		));

		assertEquals("wow", ArucasHelper.runSafe(
			"""
			del = fun(s: String): String {
				return s;
			};
			return del("wow");
			"""
		));
	}

	@Test
	public void testFieldTypes() {
		assertEquals("wow", ArucasHelper.runSafe(
			"""
			class E {
				var string: String = "";
			}
			
			e = new E();
			e.string = "wow";
			return e.string;
			"""
		));

		assertThrows(RuntimeError.class, () -> ArucasHelper.runUnsafe("class E { var s: String = 10; } new E();"));
		assertThrows(RuntimeError.class, () -> ArucasHelper.runUnsafe(
			"""
			class E {
				var s: String = "";
			}
			e = new E();
			e.s = null;
			"""
		));
	}
}
