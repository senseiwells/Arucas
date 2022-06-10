package me.senseiwells.impl;

import java.util.function.Function;

@SuppressWarnings("all")
public class Test {
	public final String A = "A";
	public String B = "B";

	public static String C = "C";

	public String D;

	public Test() {
	}

	public Test(String D) {
		this.D = D;
	}

	public String E() {
		return "E";
	}

	public static String F() {
		return "F";
	}

	public static int[] G() {
		return new int[]{1, 2, 3, 4};
	}

	public static String runFunction(Function<String, String> function) {
		return function.apply(C);
	}
}
