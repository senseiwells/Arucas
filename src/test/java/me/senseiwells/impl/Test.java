package me.senseiwells.impl;

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
}
