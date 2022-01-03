package me.senseiwells.test;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ArucasStatementTest {
	@Test
	public void testIfStatementBooleanEquality() {
		assertEquals("1", ArucasHelper.runSafe("if (true)  { return '1'; }  else { return '0'; }"));
		assertEquals("0", ArucasHelper.runSafe("if (false) { return '1'; }  else { return '0'; }"));
		assertEquals("2", ArucasHelper.runSafe("if (false) { return '1'; }  else if (true) { return '2'; }"));
	}
	
	@Test
	public void testIfStatementBracketSyntax() {
		assertEquals("1", ArucasHelper.runSafe("if (true) { return '1'; }  else { return '0'; }"));
		assertEquals("1", ArucasHelper.runSafe("if (true) { return '1'; }  else   return '0';  "));
		assertEquals("1", ArucasHelper.runSafe("if (true)   return '1';    else { return '0'; }"));
		assertEquals("1", ArucasHelper.runSafe("if (true)   return '1';    else   return '0';  "));
		assertEquals("1", ArucasHelper.runSafe("if (true) { return '1'; }"));
		assertEquals("1", ArucasHelper.runSafe("if (true)   return '1';  "));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("if true return '1';"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("if (true return '1';"));
	}
	
	@Test
	public void testIfStatementScope() {
		assertEquals("1", ArucasHelper.runSafe("X='0'; if (true)  { X='1'; }  else { X='2'; } return X;"));
		assertEquals("2", ArucasHelper.runSafe("X='0'; if (false) { X='1'; }  else { X='2'; } return X;"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("if (true) { X='1'; } return X;"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("if (true)   X='1';   return X;"));
		assertEquals("1", ArucasHelper.runSafe("X='0'; if (true) X='1'; return X;"));
	}
	
	@Test(timeout = 1000)
	public void testWhileStatementReturn() {
		assertEquals("1", ArucasHelper.runSafe("while (true)   return '1';  "));
		assertEquals("2", ArucasHelper.runSafe("while (true) { return '2'; }"));
		assertEquals("2", ArucasHelper.runSafe("while (true) { return '2'; }"));
	}
	
	@Test(timeout = 1000)
	public void testWhileStatementScope() {
		assertEquals("1", ArucasHelper.runSafe("X='0'; while (true)   return '1';  "));
		assertEquals("1", ArucasHelper.runSafe("X='0'; while (X == '0') { X='1'; } return X;"));
		assertEquals("10", ArucasHelper.runSafe("X=0; while (X < 10) { X = X + 1; } return X;"));
		assertEquals("2", ArucasHelper.runSafe("X=0; while (X == 0) { while (true) { X = X + 1; break; } X = 2; } return X;"));
	}
	
	@Test(timeout = 1000)
	public void testWhileStatementContinueBreak() {
		assertEquals("1", ArucasHelper.runSafe("X=0; while (true) { X = X + 1; break; } return X;"));
		assertEquals("2", ArucasHelper.runSafe("X='0'; while (X == '0') { X = '2'; continue; X = '4'; } return X;"));
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

	@Test
	public void testForEachStatement() {
		assertEquals("15", ArucasHelper.runSafeFull(
			"""
			X = 0;
			list = [1, 2, 3, 4, 5];
			foreach (number : list)
				X = X + number;
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
		assertEquals("5050", ArucasHelper.runSafeFull(
			"""
			fun recursion(sum, tail) {
				if (tail == 0) return sum;
				return recursion(sum + tail, tail - 1);
			}
			X = recursion(0, 100);
			""", "X"
		));
	}
	
	@Test
	public void testCallStatementNonFunction() {
		assertThrows(RuntimeError.class, () -> ArucasHelper.runUnsafeFull("X = 3; X();", "X"));
		assertEquals("3", ArucasHelper.runSafeFull("fun X() { return '3'; } Y = X();", "Y"));
	}
	
	@Test
	public void testTryStatement() {
		assertEquals("1", ArucasHelper.runSafeFull("X = '1'; try; catch (error);", "X" ));
		assertThrows(CodeError.class, () -> ArucasHelper.compile(" try;"));
		assertEquals("1", ArucasHelper.runSafeFull(
			"""
			X = '0';
			try {
				X = '1';
				throwRuntimeError("Error");
				X = '2';
			}
			catch (error);
			""", "X"
		));
		assertEquals("1", ArucasHelper.runSafeFull(
			"""
			X = true;
			I = 0;
			while (I < 10) {
				try {
					X = X + 1;
				}
				catch (error) {
					X = -8;
				}
				I = I + 1;
			}
			""", "X"
		));
		assertEquals("1", ArucasHelper.runSafeFull(
			"""
			X = null;
			I = 0;
			while (I < 10) {
				try {
					X = fun () {
						return '1';
					};
					if (I == 9) {
						X = null;
					}
					X();
				}
				catch (error) {
					X = '1';
				}
				I = I + 1;
			}
			""", "X"
		));
		assertThrows(CodeError.class, () -> ArucasHelper.compile(
			"""
			try {
				if (true) {
					X = '1';
					throwRuntimeError('error');
				}
			}
			catch (error) {
				print(X);
			}
			"""
		));
	}

	@Test
	public void testMapValue() {
		assertEquals("1", ArucasHelper.runSafeFull("map = {'one' : 1}; X = map.get('one');", "X"));
		assertThrows(RuntimeError.class, () -> ArucasHelper.runUnsafe("map = {}; map.get(null);"));
		assertThrows(RuntimeError.class, () -> ArucasHelper.runUnsafe("Y = null; map = { Y : 20 };"));
		assertEquals("one", ArucasHelper.runSafeFull(
			"""
			X = null;
			map = {
				1 : 'one',
				2 : 'two'
			};
			mapCopy = map.copy();
			map.remove(1);
			if (map.get(1) == null) {
				X = mapCopy.get(1);
			}
			""", "X"
		));
		assertEquals("[\"one\", \"one\"]", ArucasHelper.runSafeFull(
			"""
			map = {
				1 : 'one',
				2 : 'one'
			};
			X = map.getValues();
			""", "X"
		));
		assertEquals("1", ArucasHelper.runSafeFull(
			"""
			X = null;
			map = {
				'function' : fun() {
					return 10;
				},
				'otherFunction' : fun() {
					return 9;
				}
			};
			X = map.get('function')() - map.get('otherFunction')();
			""", "X"
		));
	}

	@Test
	public void testMemberFunctions() {
		assertEquals("true", ArucasHelper.runSafeFull(
			"""
			fun getBool() {
				return true;
			}

			X = getBool();
			X = X.toString();
			""", "X"
		));
		assertEquals("1", ArucasHelper.runSafeFull(
			"""
			fun getNumber() {
				return 1;
			}

			X = getNumber().toString();
			""", "X"
		));
		assertEquals("1", ArucasHelper.runSafeFull(
			"""
			X = 1;
			X = X.toString();
			""", "X"
		));
		assertEquals("1", ArucasHelper.runSafeFull(
			"""
			fun getNumber() {
				return 1;
			}

			X = getNumber();
			X = X.toString();
			""", "X"
		));
	}
}
