package me.senseiwells.arucas.core;

import java.nio.file.Path;

public class Arucas {
	public static final String VERSION = "1.2.3";
	public static final Path PATH = Path.of(System.getProperty("user.home")).resolve(".arucas");

	private Arucas() { }
}
