package me.senseiwells.test;

import me.senseiwells.arucas.api.ContextBuilder;
import me.senseiwells.arucas.extensions.util.JavaValue;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class ArucasJavaTest {
	private static final ContextBuilder BUILDER_WITH_JAVA;

	static {
		BUILDER_WITH_JAVA = new ContextBuilder()
			.setDisplayName("Java Test")
			.addDefault()
			.addBuiltInClasses(JavaValue.ArucasJavaClass::new);
	}

	@Test
	public void testJavaType() {
		assertThrows(CodeError.class, () -> ArucasHelper.runUnsafe("Java.valueOf(null);"));
		assertThrows(RuntimeError.class, () -> ArucasHelper.runUnsafe("Java.charOf('string');", BUILDER_WITH_JAVA.build()));
		assertEquals("null", ArucasHelper.runSafe("Java.valueOf(null);", BUILDER_WITH_JAVA.build()));
		assertEquals("true", ArucasHelper.runSafe(
			"""
			jString = Java.valueOf("wow");
			return jString.startsWith("wo");
			""", BUILDER_WITH_JAVA.build()
		));
		assertEquals("[wow, 4, j, null]", ArucasHelper.runSafe(
			"""
			jArray = Java.arrayOf("wow", Java.intOf(4), Java.charOf("j"), null);
			return Java.callStaticMethod("java.util.Arrays", "toString", jArray);
			""", BUILDER_WITH_JAVA.build()
		));
		assertEquals("1$lambda", ArucasHelper.runSafe(
			"""
			del = fun() { };
			return Java.valueOf(del);
			""", BUILDER_WITH_JAVA.build()
		));
		assertEquals("10", ArucasHelper.runSafe(
			"""
			import Java from util.Internal;
			return Java.intOf(10);
			"""
		));
	}

	@Test
	public void testConvertJavaToArucas() {
		assertEquals("[1, 2, 3, 4, 5]", ArucasHelper.runSafe(
			"""
			array = Java.arrayOf(1, 2, 3, 4);
			list = array.toArucas();
			return list.append(5);
			""", BUILDER_WITH_JAVA.build()
		));
	}

	@Test
	public void testJavaCallMethods() {
		assertEquals("[1, 2, 3, 4, \"F\"]", ArucasHelper.runSafe(
			"""
			list = Java.callStaticMethod("me.senseiwells.impl.Test", "G").toArucas();
			list.append(Java.callStaticMethod("me.senseiwells.impl.Test", "F").toArucas());
			return list;
			""", BUILDER_WITH_JAVA.build()
		));
		assertEquals("me.senseiwells.impl.Test@E", ArucasHelper.runSafe(
			"""
			testInstance = Java.constructClass("me.senseiwells.impl.Test");
			// This isn't calling Java toString, rather Arucas toString, it returns StringValue
			return testInstance.toString().subString(0, 25) + testInstance.E();
			""", BUILDER_WITH_JAVA.build()
		));
		assertEquals("TestingString", ArucasHelper.runSafe(
			"""
			testInstance = Java.constructClass("me.senseiwells.impl.Test", "TestingString");
			return testInstance.D;
			""", BUILDER_WITH_JAVA.build()
		));
		assertEquals("C", ArucasHelper.runSafe(
			"""
			return Java.getStaticField("me.senseiwells.impl.Test", "C");
			""", BUILDER_WITH_JAVA.build()
		));
		assertThrows(RuntimeError.class, () -> ArucasHelper.runUnsafe(
			"""
			testInstance = Java.constructClass("me.senseiwells.impl.Test", "TestingString");
			testInstance.A = "This field is final";
			""", BUILDER_WITH_JAVA.build()
		));
		assertEquals("Reassigned", ArucasHelper.runSafe(
			"""
			suppressDeprecated(true);
			testInstance = Java.constructClass("me.senseiwells.impl.Test", "TestingString");
			testInstance.B = "Re";
			testInstance.setField("D", "assigned");
			return testInstance.B.toString() + testInstance.D;
			""", BUILDER_WITH_JAVA.build()
		));
		assertEquals("E", ArucasHelper.runSafe(
			"""
			suppressDeprecated(true);
			testInstance = Java.constructClass("me.senseiwells.impl.Test");
			return testInstance.callMethod("E");
			""", BUILDER_WITH_JAVA.build()
		));
		assertEquals("EEE", ArucasHelper.runSafe(
			"""
			del = Java.constructClass("me.senseiwells.impl.Test").E;
			return del().toString() + del().toString() + del().toString();
			""", BUILDER_WITH_JAVA.build()
		));
	}
}
