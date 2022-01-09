package me.senseiwells.test;

import me.senseiwells.arucas.throwables.CodeError;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ArucasSwitchTest {
	@Test
	public void testSwitchStatement() {
		assertEquals("two", ArucasHelper.runSafeFull(
			"""
			fun test(value) {
				switch (value) {
					case '1', '2', '3' -> return 'one';
					case '4', '5', '6' -> return 'two';
					case '7' -> return 'three';
				}
				return null;
			}
			X = test('5');
			""", "X"
		));
		assertEquals("negative three", ArucasHelper.runSafeFull(
			"""
			fun test(value) {
				switch (value) {
					case -3 -> return 'negative three';
					case 1 -> return 'one';
					case 2 -> return 'two';
				}
				return null;
			}
			X = test(-3);
			""", "X"
		));
	}
	
	@Test
	public void testSwitchStatementWrongType() {
		assertThrows(CodeError.class, () -> ArucasHelper.compile("switch (1) { case 1, '2' -> break; }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("switch (1) { case 1 -> break; case '2' -> break; }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("switch (1) { case 1 -> break; case 1 -> break; }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("switch (1) { case [] -> break; }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("switch (1) { case {} -> break; }"));
	}
	
	@Test
	public void testSwitchStatementMinus() {
		assertThrows(CodeError.class, () -> ArucasHelper.compile("switch (1) { case -1, -'2' -> break; }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("switch (1) { case -'1', -'2' -> break; }"));
	}
	
	@Test
	public void testSwitchDuplicateCase() {
		assertThrows(CodeError.class, () -> ArucasHelper.compile("switch (1) { default -> break; default -> break; }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("switch (1) { case '2', '2' -> break; }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("switch (1) { case -3, -3 -> break; }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("switch (1) { case 1, 1 -> break; }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("switch (1) { case '3' -> break; case '3' -> break; }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("switch (1) { case -2 -> break; case -2 -> break; }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("switch (1) { case 1 -> break; case 1 -> break; }"));
	}
	
	@Test
	public void testSwitchStatementSyntax() {
		assertThrows(CodeError.class, () -> ArucasHelper.compile("switch (1) { case -> break; }"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("switch (1) { case 1 -> { } { } }"));
		assertEquals("null", ArucasHelper.runSafe("switch (1) { }"));
	}
	
	@Test
	public void testSwitchStatementDefault() {
		assertEquals("three", ArucasHelper.runSafeFull(
			"""
			fun test(value) {
				switch (value) {
					case 1 -> return 'one';
					case 2 -> return 'two';
					default -> return 'three';
				}
				return null;
			}
			X = test(4);
			""", "X"
		));
		assertEquals("two", ArucasHelper.runSafeFull("X = 'one'; switch (1) { default -> X = 'two'; }", "X"));
	}
}
