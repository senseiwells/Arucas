package me.senseiwells.arucas.api.impl;

import me.senseiwells.arucas.api.IArucasInput;
import me.senseiwells.arucas.api.IArucasOutput;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ImplArucasIO implements IArucasInput, IArucasOutput {
	private final Scanner inputScanner;
	private final Consumer<String> outputHandler;
	private final Consumer<String> debugHandler;
	private String boldError;
	private String error;
	private String reset;

	public ImplArucasIO() {
		this.inputScanner = new Scanner(System.in);
		this.outputHandler = System.out::print;
		this.debugHandler = this.outputHandler;
		this.boldError = "\033[1;31m";
		this.error = "\033[0;31m";
		this.reset = "\u001b[0m";
	}

	@Override
	public Consumer<String> getOutputHandler() {
		return this.outputHandler;
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

	@Override
	public CompletableFuture<String> takeInput() {
		return CompletableFuture.completedFuture(this.inputScanner.next());
	}
}
