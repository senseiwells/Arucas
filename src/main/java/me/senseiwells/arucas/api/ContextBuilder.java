package me.senseiwells.arucas.api;

import me.senseiwells.arucas.api.impl.ArucasOutput;
import me.senseiwells.arucas.extensions.*;
import me.senseiwells.arucas.utils.Context;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Runtime context class of the programming language
 */
@SuppressWarnings("unused")
public class ContextBuilder {
	private final List<Supplier<IArucasExtension>> extensions = new ArrayList<>();
	private final List<Supplier<IArucasValueExtension>> valueExtensions = new ArrayList<>();
	private Consumer<String> outputHandler = System.out::print;
	private boolean suppressDeprecated;
	private String displayName = "";
	
	public ContextBuilder() { }
	
	public ContextBuilder setDisplayName(String displayName) {
		this.displayName = Objects.requireNonNull(displayName);
		return this;
	}

	public ContextBuilder setSuppressDeprecated(boolean suppressDeprecated) {
		this.suppressDeprecated = suppressDeprecated;
		return this;
	}

	public ContextBuilder setOutputHandler(Consumer<String> outputHandler) {
		this.outputHandler = outputHandler;
		return this;
	}

	public ContextBuilder addDefaultExtensions() {
		return this.addExtensions(List.of(
			ArucasBuiltInExtension::new
		));
	}
	
	public ContextBuilder addExtensions(List<Supplier<IArucasExtension>> extensions) {
		this.extensions.addAll(extensions);
		return this;
	}

	@SafeVarargs
	public final ContextBuilder addExtensions(Supplier<IArucasExtension>... extensions) {
		this.extensions.addAll(List.of(extensions));
		return this;
	}
	
	public ContextBuilder addDefaultValueExtensions() {
		return this.addValueExtensions(List.of(
			ArucasMapMembers::new,
			ArucasNumberMembers::new,
			ArucasListMembers::new,
			ArucasStringMembers::new,
			ArucasBuiltInMembers::new
		));
	}
	
	public ContextBuilder addValueExtensions(List<Supplier<IArucasValueExtension>> extensions) {
		this.valueExtensions.addAll(extensions);
		return this;
	}
	
	@SafeVarargs
	public final ContextBuilder addValueExtensions(Supplier<IArucasValueExtension>... extensions) {
		this.valueExtensions.addAll(List.of(extensions));
		return this;
	}
	
	/**
	 * Make sure to define extensions before calling this method.
	 * This method will override all functions defined after this
	 * call.
	 */
	public ContextBuilder addDefault() {
		return this.addDefaultExtensions()
			.addDefaultValueExtensions();
	}
	
	public Context build() {
		List<IArucasExtension> extensionList = new ArrayList<>();
		List<IArucasValueExtension> valueExtensions = new ArrayList<>();
  
		for (Supplier<IArucasExtension> supplier : this.extensions) {
			extensionList.add(supplier.get());
		}
		
		for (Supplier<IArucasValueExtension> supplier : this.valueExtensions) {
			valueExtensions.add(supplier.get());
		}
		
		ArucasOutput arucasOutput = new ArucasOutput();
		arucasOutput.setOutputHandler(this.outputHandler);
		
		Context context = new Context(this.displayName, extensionList, valueExtensions, arucasOutput);
		context.setSuppressDeprecated(this.suppressDeprecated);
		return context;
	}
}
