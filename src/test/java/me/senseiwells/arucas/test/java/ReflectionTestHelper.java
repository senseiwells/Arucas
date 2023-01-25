package me.senseiwells.arucas.test.java;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class ReflectionTestHelper {
	public static final Object IMMUTABLE_CONSTANT = new Object();
	public static String STRING_CONSTANT = "DEFAULT";
	public static int INT_CONSTANT = 10;

	public static String returnStringMethod() {
		return STRING_CONSTANT;
	}

	public static int returnIntMethod() {
		return INT_CONSTANT;
	}

	public static int returnIntPlusOneParameterMethod(int parameter) {
		return parameter + 1;
	}

	public static Object returnParameterMethod(Object parameter) {
		return parameter;
	}

	public static List<Integer> returnVarargsAsList(int... parameters) {
		return Arrays.stream(parameters).boxed().toList();
	}

	public Object returnObjectMethod() {
		return IMMUTABLE_CONSTANT;
	}
}
