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
	}
	
	@Test
	public void testFunctionStatementScope() {
		assertEquals("0", ArucasHelper.runSafeFull(
			"""
			X = fun() {
				Y = '0';
				A = fun() { Y = '1'; };
				A();
				return Y;
			}();
			""", "X"
		));
		assertThrows(CodeError.class, () -> ArucasHelper.runUnsafeFull(
			"""
			X = fun() {
				A = fun() { return Y; };
				Y = '0';
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
}
