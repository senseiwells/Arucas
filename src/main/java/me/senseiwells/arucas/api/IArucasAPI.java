package me.senseiwells.arucas.api;

import java.nio.file.Path;

public interface IArucasAPI {
	/**
	 * Gets the Output handler for the
	 * language runtime.
	 */
	IArucasOutput getOutput();

	/**
	 * Gets the Import path for the
	 * language to import Arucas classes.
	 */
	Path getImportPath();

	/**
	 * Whether Arucas should try and obfuscate the names
	 */
	default boolean shouldObfuscate() {
		return false;
	}

	/**
	 * Used to obfuscate Java classes, methods and field names when
	 * calling methods, accessing/assigning fields, or constructing
	 * classes on {@link me.senseiwells.arucas.extensions.util.JavaValue},
	 * since names may be deobfuscated, for example: Minecraft
	 */
	default String obfuscate(String name) {
		return name;
	}

	/**
	 * Used to deobfuscate Java classes, methods and field names when
	 * calling methods, accessing/assigning fields, or constructing
	 * classes on {@link me.senseiwells.arucas.extensions.util.JavaValue},
	 * since names may be obfuscated, for example: Minecraft
	 */
	default String deobfuscate(String name) {
		return name;
	}
}
