package me.senseiwells.arucas.api;

import java.nio.file.Path;

@SuppressWarnings("unused")
public interface IArucasAPI {
	/**
	 * Gets the input handler for the
	 * language runtime.
	 */
	IArucasInput getInput();

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
	 * Used to obfuscate a Java class name
	 */
	default String obfuscateClassName(String name) {
		return name;
	}

	/**
	 * Used to obfuscate a Java method name
	 */
	default String obfuscateMethodName(Class<?> clazz, String name) {
		return name;
	}

	/**
	 * Used to obfuscate a Java field name
	 */
	default String obfuscateFieldName(Class<?> clazz, String name) {
		return name;
	}
}
