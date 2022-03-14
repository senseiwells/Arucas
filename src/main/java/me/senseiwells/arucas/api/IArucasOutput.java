package me.senseiwells.arucas.api;

import java.util.function.Consumer;

/**
 * Print interface for arucas
 */
@SuppressWarnings("unused")
public interface IArucasOutput {
	// Declare methods that pipeline the output stream
	void setOutputHandler(Consumer<String> outputHandler);
	Consumer<String> getOutputHandler();

	// Declare methods used for printing
	void print(Object object);
	void println();
	void println(Object object);

	// Declare methods that provide additional formatting
	void setErrorFormatting(String error, String boldError);
	String getErrorFormatting();
	String getErrorFormattingBold();
}
