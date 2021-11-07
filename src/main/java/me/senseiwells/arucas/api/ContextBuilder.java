package me.senseiwells.arucas.api;

import me.senseiwells.arucas.extensions.*;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.util.*;
import java.util.function.Consumer;

/**
 * Runtime context class of the programming language
 */
@SuppressWarnings("unused")
public class ContextBuilder {
	private final List<Class<? extends IArucasExtension>> extensions = new ArrayList<>();
	private final List<Class<?>> valueList = new ArrayList<>();
	private Consumer<String> printDeprecated = System.out::println;
	private String displayName = "";
	
	public ContextBuilder() { }
	
	public ContextBuilder setDisplayName(String displayName) {
		this.displayName = Objects.requireNonNull(displayName);
		return this;
	}

	public ContextBuilder setPrintDeprecated(Consumer<String> printDeprecated) {
		this.printDeprecated = printDeprecated;
		return this;
	}

	public ContextBuilder addDefaultExtensions() {
		this.extensions.addAll(List.of(
			ArucasBuiltInExtension.class,
			ArucasListMembers.class,
			ArucasNumberMembers.class,
			ArucasStringMembers.class,
			ArucasMapMembers.class
		));
		return this;
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
		this.valueList.addAll(List.of(
			StringValue.class,
			NumberValue.class,
			MapValue.class,
			ListValue.class,
			BooleanValue.class,
			FunctionValue.class
		));
		return this;
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

		Map<String, Class<?>> valueMap = new HashMap<>();

		for (Class<?> clazz : this.valueList) {
			String className = clazz.getSimpleName();
			valueMap.put(className, clazz);
			if (className.endsWith("Value")) {
				className = className.substring(0, className.length() - 5);
				valueMap.put(className, clazz);
			}
		}

		return new Context(this.displayName, list, valueMap, this.printDeprecated);
	}
}
