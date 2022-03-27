package me.senseiwells.arucas.api.impl;

import me.senseiwells.arucas.api.IArucasOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Consumer;

public class ArucasOutputImpl implements IArucasOutput {
	private static final Logger LOGGER = LoggerFactory.getLogger("Arucas");

	private Consumer<String> outputHandler;
	private Consumer<String> debugHandler;
	private String boldError;
	private String error;
	private String reset;

	public ArucasOutputImpl() {
		this.outputHandler = System.out::print;
		this.debugHandler = LOGGER::debug;
		this.boldError = "\033[1;31m";
		this.error = "\033[0;31m";
		this.reset = "\u001b[0m";
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
	public void setDebugHandler(Consumer<String> debugHandler) {
		this.debugHandler = debugHandler;
	}

	@Override
	public Consumer<String> getDebugHandler() {
		return this.debugHandler;
	}

	@Override
	public void setFormatting(String error, String boldError, String reset) {
		this.error = error;
		this.boldError = boldError;
		this.reset = reset;
	}

	@Override
	public String getErrorFormatting() {
		return this.error;
	}

	@Override
	public String getErrorFormattingBold() {
		return this.boldError;
	}

	@Override
	public String getResetFormatting() {
		return this.reset;
	}
}
