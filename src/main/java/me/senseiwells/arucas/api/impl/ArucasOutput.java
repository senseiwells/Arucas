package me.senseiwells.arucas.api.impl;

import me.senseiwells.arucas.api.IArucasOutput;

import java.util.Objects;
import java.util.function.Consumer;

public class ArucasOutput implements IArucasOutput {
	private Consumer<String> outputHandler = System.out::print;
	
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
}
