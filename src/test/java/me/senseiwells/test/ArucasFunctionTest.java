package me.senseiwells.test;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class ArucasFunctionTest {
	@Test
	public void testFunctionStatement() {
		assertEquals("0", ArucasHelper.runSafeFull("fun test(A, B, C) {} Q = '0';", "Q"));
		assertEquals("1", ArucasHelper.runSafeFull("Q = fun() { return '1'; }();", "Q"));
	}

	@Test
	public void testFunctionCallScope() {
		assertEquals("valid", ArucasHelper.runSafeFull(
			"""
			A = null;
			{
				fun g() { return 'valid'; }
				fun test() { return g(); }
				A = test;
			}
			X = A();
			""", "X"
		));
		assertEquals("1", ArucasHelper.runSafeFull(
			"""
			X = '1';
			fun test(X) {}
			test(100);
			""", "X"
		));
		assertEquals("10", ArucasHelper.runSafe(
			"""
			del = null;
			{
				a = 10;
				del = fun() {
					return a;
				};
			}
			return del();
			"""
		));
		assertEquals("99", ArucasHelper.runSafe(
			"""
			del = null;
			{
				a = 99;
				del = fun() {
					return a;
				};
			}
			{
				a = 100;
				return del();
			}
			"""
		));
		assertEquals("2", ArucasHelper.runSafe(
			"""
			del = null;
			{
				a = 0;
				del = fun() {
					a++;
					return a;
				};
			}
			del();
			return del();
			"""
		));
	}

	@Test
	public void testFunctionStatementScope() {
		assertEquals("1", ArucasHelper.runSafeFull(
			"""
			X = fun() {
				Y = '0';
				A = fun() { Y = '1'; };
				A();
				return Y;
			}();
			""", "X"
		));
		assertEquals("2", ArucasHelper.runSafeFull(
			"""
			Q = '0';
			(fun() {
				A = fun() { Q = '2'; };
				Q = '1';
				A();
			})();
			""", "Q"
		));
		assertEquals("3", ArucasHelper.runSafeFull(
			"""
			Q = (((fun() { return (fun() { return (fun() { X = '3'; return X; }); }); })())())();
			""", "Q"
		));
	}

	@Test
	public void testFunctionParameters() {
		assertEquals("45", ArucasHelper.runSafe(
			"""
			lambda = fun(s, t) {
				return s + t;
			};
			a = lambda((a = 12) + 1, b = 10) + a + b;
			return a;
			"""
		));
	}

	@Test
	public void testFunctionDelegate() {
		assertEquals("1", ArucasHelper.runSafe(
			"""
			f = fun() {
			    return 1;
			};
			return f();
			"""
		));
		assertEquals("d1", ArucasHelper.runSafe(
			"""
			class E {
			    static fun d() {
			        return "d1";
			    }
			}
			del = E.d;
			return del();
			"""
		));
		assertThrows(RuntimeError.class, () -> ArucasHelper.runUnsafe(
			"""
			class E {
			    static fun d() {
			        return "d1";
			    }
			    static fun d(p) {
			        return "d2";
			    }
			}
			del = E.d;
			return del();
			"""
		));
		assertEquals("d2", ArucasHelper.runSafe(
			"""
			class E {
			    static fun d() {
			        return "d1";
			    }
			    static fun d(p) {
			        return "d2";
			    }
			}
			del = E.type.getStaticMethod("d", 1);
			return del(null);
			"""
		));
	}

	@Test
	public void testArbitraryParameters() {
		assertThrows(CodeError.class, () -> ArucasHelper.compile("fun test(a, b...) { }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("fun test(a, b, c, d...) { }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("fun test(...a) { }"));
		assertEquals("[1, 2, 3]", ArucasHelper.runSafe(
			"""
			del = fun(a...) {
				return a;
			};
			return del(1, 2, 3);
			"""
		));
		assertEquals("26", ArucasHelper.runSafe(
			"""
			fun adder(nums...) {
				total = 0;
				foreach (n : nums) {
					total = total + n;
				}
				return total;
			}
			return adder(5, 6, 7, 8);
			"""
		));
		assertEquals("[]", ArucasHelper.runSafe(
			"""
			del = fun(a...) {
				return a;
			};
			return del();
			"""
		));
	}
}
