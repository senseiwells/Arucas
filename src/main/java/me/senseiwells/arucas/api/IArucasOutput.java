package me.senseiwells.arucas.api;

import java.util.function.Consumer;

/**
 * Print interface for arucas
 */
@SuppressWarnings("unused")
public interface IArucasOutput {
	/**
	 * Sets the current output handler
	 */
	void setOutputHandler(Consumer<String> outputHandler);

	/**
	 * Gets the current output handler
	 */
	Consumer<String> getOutputHandler();

	/**
	 * Sets the current debug handler
	 */
	void setDebugHandler(Consumer<String> debugHandler);

	/**
	 * Gets the current debug handler
	 */
	Consumer<String> getDebugHandler();

	/**
	 * Sets the current error formatting
	 */
	void setFormatting(String error, String boldError, String reset);

	/**
	 * Gets the current error formatting
	 */
	String getErrorFormatting();
	String getErrorFormattingBold();
	String getResetFormatting();

	default void print(Object object) {
		this.getOutputHandler().accept(String.valueOf(object));
	}

	default void println() {
		this.print("\n");
	}

	default void println(Object object) {
		this.print(object + "\n");
	}

	default void log(Object object) {
		this.getDebugHandler().accept(String.valueOf(object));
	}

	default String addErrorFormatting(String string) {
		return this.getErrorFormatting() + string + this.getResetFormatting();
	}

	default String addErrorFormattingBold(String string) {
		return this.getErrorFormattingBold() + string + this.getResetFormatting();
	}


	IArucasOutput DUMMY = new IArucasOutput() {
		private final Consumer<String> dummyConsumer = s -> { };

		@Override
		public void setOutputHandler(Consumer<String> outputHandler) { }

		@Override
		public Consumer<String> getOutputHandler() {
			return this.dummyConsumer;
		}

		@Override
		public void setDebugHandler(Consumer<String> debugHandler) { }

		@Override
		public Consumer<String> getDebugHandler() {
			return this.dummyConsumer;
		}

		@Override
		public void setFormatting(String error, String boldError, String reset) { }

		@Override
		public String getErrorFormatting() {
			return "";
		}

		@Override
		public String getErrorFormattingBold() {
			return "";
		}

		@Override
		public String getResetFormatting() {
			return "";
		}
	};
}
