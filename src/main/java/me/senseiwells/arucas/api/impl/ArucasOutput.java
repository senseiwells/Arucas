package me.senseiwells.arucas.api.impl;

import me.senseiwells.arucas.api.IArucasOutput;

import java.util.Objects;
import java.util.function.Consumer;

public class ArucasOutput implements IArucasOutput {
	private Consumer<String> outputHandler;
	private String boldError;
	private String error;

	public ArucasOutput() {
		this.outputHandler = System.out::print;
		this.boldError = "\033[1;31m";
		this.error = "\033[0;31m";
	}

	@Override
	public void setOutputHandler(Consumer<String> outputHandler) {
		this.outputHandler = Objects.requireNonNull(outputHandler);
	}

	@Override
	public Consumer<String> getOutputHandler() {
		return this.outputHandler;
	}
	
	@Override
	public void print(Object object) {
		this.outputHandler.accept(String.valueOf(object));
	}
	
	@Override
	public void println() {
		this.print("\n");
	}

	@Override
	public void println(Object object) {
		this.print(object + "\n");
	}

	@Override
	public void setErrorFormatting(String error, String boldError) {
		this.error = error;
		this.boldError = boldError;
	}

	@Override
	public String getErrorFormatting() {
		return this.error;
	}

	@Override
	public String getErrorFormattingBold() {
		return this.boldError;
	}
}
