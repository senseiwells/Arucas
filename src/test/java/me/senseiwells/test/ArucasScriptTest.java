package me.senseiwells.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArucasScriptTest {
	@Test
	public void generateCompiledDocumentation() {
		assertEquals("true", ArucasHelper.runSafe("return run(File.getDirectory().getPath() + '/src/test/resources/code/compile_docs.arucas');"));
	}
}
