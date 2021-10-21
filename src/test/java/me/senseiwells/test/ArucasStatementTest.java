package me.senseiwells.test;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ErrorRuntime;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ArucasStatementTest {
	@Test
	public void testIfStatementBooleanEquality() {
		assertEquals("1", ArucasHelper.runSafe("if(true)  { return '1'; } else { return '0'; }"));
		assertEquals("0", ArucasHelper.runSafe("if(false) { return '1'; } else { return '0'; }"));
		assertEquals("2", ArucasHelper.runSafe("if(false) { return '1'; } else if(true) { return '2'; }"));
	}
	
	@Test
	public void testIfStatementBracketSyntax() {
		assertEquals("1", ArucasHelper.runSafe("if(true) { return '1'; } else { return '0'; }"));
		assertEquals("1", ArucasHelper.runSafe("if(true) { return '1'; } else   return '0';  "));
		assertEquals("1", ArucasHelper.runSafe("if(true)   return '1';   else { return '0'; }"));
		assertEquals("1", ArucasHelper.runSafe("if(true)   return '1';   else   return '0';  "));
		assertEquals("1", ArucasHelper.runSafe("if(true) { return '1'; }"));
		assertEquals("1", ArucasHelper.runSafe("if(true)   return '1';  "));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("if true return '1';"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("if(true return '1';"));
	}
	
	@Test
	public void testIfStatementScope() {
		assertEquals("1", ArucasHelper.runSafe("X='0'; if(true)  { X='1'; } else { X='2'; } return X;"));
		assertEquals("2", ArucasHelper.runSafe("X='0'; if(false) { X='1'; } else { X='2'; } return X;"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("if(true) { X='1'; } return X;"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("if(true)   X='1';   return X;"));
		assertEquals("1", ArucasHelper.runSafe("X='0'; if(true) X='1'; return X;"));
	}
	
	@Test(timeout = 1000)
	public void testWhileStatementReturn() {
		assertEquals("1", ArucasHelper.runSafe("while(true)   return '1';  "));
		assertEquals("2", ArucasHelper.runSafe("while(true) { return '2'; }"));
		assertEquals("2", ArucasHelper.runSafe("while(true) { return '2'; }"));
	}
	
	@Test(timeout = 1000)
	public void testWhileStatementScope() {
		assertEquals("1", ArucasHelper.runSafe("X='0'; while(true)   return '1';  "));
		assertEquals("1", ArucasHelper.runSafe("X='0'; while(X == '0') { X='1'; } return X;"));
		assertEquals("10.0", ArucasHelper.runSafe("X=0; while(X < 10) { X = X + 1; } return X;"));
		assertEquals("2.0", ArucasHelper.runSafe("X=0; while(X == 0) { while(true) { X = X + 1; break; } X = 2; } return X;"));
	}
	
	@Test(timeout = 1000)
	public void testWhileStatementContinueBreak() {
		assertEquals("1.0", ArucasHelper.runSafe("X=0; while(true) { X = X + 1; break; } return X;"));
		assertEquals("2", ArucasHelper.runSafe("X='0'; while(X == '0') { X = '2'; continue; X = '4'; } return X;"));
		assertEquals("3", ArucasHelper.runSafeFull(
			"""
 			X = '0';
 			while (X == '0') {
 				X = '4';
 				while (X == '4') {
 					X = '2';
 					continue;
 				}
 				if (X == '2') {
 					X = '3';
 					continue;
 				}
 			}
 			""", "X"
		));
	}
	
	@Test(timeout = 1000)
	public void testScopeStatementScope() {
		assertEquals("1", ArucasHelper.runSafe("X='0'; { X='1'; } return X;"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("{ X='1'; } return X;"));
		assertEquals("0", ArucasHelper.runSafe("X='0'; { (fun(){X='1';})(); } return X;"));
		
	}
	
	@Test(timeout = 1000)
	public void testCallStatementRecursion() {
		assertEquals("5050.0", ArucasHelper.runSafeFull(
			"""
			fun recursion(sum, tail) {
				if(tail == 0) return sum;
				return recursion(sum + tail, tail - 1);
			}
			X = recursion(0, 100);
			""", "X"
		));
	}
	
	@Test
	public void testCallStatementNonFunction() {
		assertThrows(ErrorRuntime.class, () -> ArucasHelper.runUnsafeFull("X = 3; X();", "X"));
		assertEquals("3", ArucasHelper.runSafeFull("fun X() { return '3'; } Y = X();", "Y"));
	}
	
	@Test
	public void testFunctionStatement() {
		assertEquals("0", ArucasHelper.runSafeFull("fun test(A, B, C) {} Q = '0';", "Q"));
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
