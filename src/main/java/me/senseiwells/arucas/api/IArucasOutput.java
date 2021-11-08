package me.senseiwells.arucas.api;

import java.util.Locale;
import java.util.function.Consumer;

/**
 * Print interface for arucas
 */
public interface IArucasOutput {
	// Declare methods that pipeline the output stream.
	void setOutputHandler(Consumer<String> outputHandler);
	Consumer<String> getOutputHandler();
	
	// Declare methods used for printing.
	void print(Object object);
	void printf(String format, Object... args);
	void printf(Locale locale, String format, Object... args);
	void println();
	void println(Object object);
}
