package me.senseiwells.test;

import me.senseiwells.arucas.throwables.CodeError;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class ArucasImportTest {
	@Test
	public void testImportSyntax() {
		assertThrows(CodeError.class, () -> ArucasHelper.compile("import *;"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("import Error;"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("from Error import *;"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("import * from *;"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("import * from 'BuiltIn';"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("import * from file/path;"));
		assertThrows(CodeError.class, () -> ArucasHelper.compile("import * from BuiltIn"));
		assertEquals("null", ArucasHelper.runSafe("import * from BuiltIn;"));
		assertEquals("null", ArucasHelper.runSafe("import List from BuiltIn;"));
		assertEquals("null", ArucasHelper.runSafe("import String from BuiltIn;"));
	}

	@Test
	public void testImportStatement() {
		assertThrows(CodeError.class, () -> ArucasHelper.runUnsafe("import * from A;"));
		assertThrows(CodeError.class, () -> ArucasHelper.runUnsafeFull(
			"X = Set.of(); ", "X", ArucasHelper.createContextNoBuiltIns()
		));
		assertThrows(CodeError.class, () -> ArucasHelper.runUnsafeFull(
			"import List from BuiltIn; X = Set.of();", "X", ArucasHelper.createContextNoBuiltIns()
		));
		assertThrows(CodeError.class, () -> ArucasHelper.runUnsafeFull(
			"X = Set.of(); import Set from BuiltIn;", "X", ArucasHelper.createContextNoBuiltIns()
		));
		assertEquals("<>", ArucasHelper.runSafeFull(
			"import * from BuiltIn; X = Set.of();", "X", ArucasHelper.createContextNoBuiltIns()
		));
		assertEquals("<>", ArucasHelper.runSafeFull(
			"import Set from BuiltIn; X = Set.of();", "X", ArucasHelper.createContextNoBuiltIns()
		));
	}
}
