package me.senseiwells.arucas.api;

import me.senseiwells.arucas.api.impl.ArucasOutput;
import me.senseiwells.arucas.extensions.*;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Runtime context class of the programming language
 */
@SuppressWarnings("unused")
public class ContextBuilder {
	private final List<Class<? extends IArucasExtension>> extensions = new ArrayList<>();
	private final List<Class<?>> valueList = new ArrayList<>();
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
			ArucasBuiltInExtension.class,
			ArucasListMembers.class,
			ArucasNumberMembers.class,
			ArucasStringMembers.class,
			ArucasMapMembers.class
		));
	}
	
	public ContextBuilder addExtensions(List<Class<? extends IArucasExtension>> extensions) {
		this.extensions.addAll(extensions);
		return this;
	}

	@SafeVarargs
	public final ContextBuilder addExtensions(Class<? extends IArucasExtension>... extensions) {
		this.extensions.addAll(List.of(extensions));
		return this;
	}
	
	public ContextBuilder addDefaultValues() {
		return this.addValues(List.of(
			StringValue.class,
			NumberValue.class,
			MapValue.class,
			ListValue.class,
			BooleanValue.class,
			FunctionValue.class
		));
	}
	
	public ContextBuilder addValues(List<Class<?>> values) {
		this.valueList.addAll(values);
		return this;
	}

	public final ContextBuilder addValues(Class<?>... values) {
		this.valueList.addAll(List.of(values));
		return this;
	}
	
	public Context build() {
		List<IArucasExtension> list = new ArrayList<>();
  
		for (Class<? extends IArucasExtension> clazz : this.extensions) {
			try {
				list.add(clazz.getDeclaredConstructor().newInstance());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Map<String, Class<?>> valueMap = this.valueList.stream()
			.collect(Collectors.toMap(clazz -> clazz.getSimpleName().replaceFirst("Value$", ""), i -> i));
		
		ArucasOutput arucasOutput = new ArucasOutput();
		arucasOutput.setOutputHandler(this.outputHandler);
		
		Context context = new Context(this.displayName, list, valueMap, arucasOutput);
		context.setSuppressDeprecated(this.suppressDeprecated);
		return context;
	}
}
