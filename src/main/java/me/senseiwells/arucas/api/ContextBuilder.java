package me.senseiwells.arucas.api;

import me.senseiwells.arucas.api.impl.ArucasOutput;
import me.senseiwells.arucas.extensions.*;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Runtime context class of the programming language
 */
@SuppressWarnings("unused")
public class ContextBuilder {
	private final List<Supplier<IArucasExtension>> extensions = new ArrayList<>();
	private final List<Supplier<ArucasClassExtension>> classes = new ArrayList<>();
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
	
	public ContextBuilder addDefaultClasses() {
		return this.addClases(
			StringValue.ArucasStringClass::new,
			BooleanValue.ArucasBooleanClass::new,
			ListValue.ArucasListClass::new,
			MapValue.ArucasMapClass::new,
			NullValue.ArucasNullClass::new,
			NumberValue.ArucasNumberClass::new,
			ArucasMathClass::new
		);
	}
	
	public ContextBuilder addClasses(List<Supplier<ArucasClassExtension>> extensions) {
		this.classes.addAll(extensions);
		return this;
	}
	
	@SafeVarargs
	public final ContextBuilder addClases(Supplier<ArucasClassExtension>... extensions) {
		this.classes.addAll(List.of(extensions));
		return this;
	}
	
	/**
	 * Make sure to define extensions before calling this method.
	 * This method will override all functions defined after this
	 * call.
	 */
	public ContextBuilder addDefault() {
		return this.addDefaultExtensions()
			.addDefaultClasses();
	}
	
	public Context build() {
		List<IArucasExtension> extensionList = new ArrayList<>();
		List<AbstractClassDefinition> classDefinitions = new ArrayList<>();

		for (Supplier<IArucasExtension> supplier : this.extensions) {
			extensionList.add(supplier.get());
		}

		for (Supplier<ArucasClassExtension> supplier : this.classes) {
			classDefinitions.add(supplier.get());
		}
		
		ArucasOutput arucasOutput = new ArucasOutput();
		arucasOutput.setOutputHandler(this.outputHandler);
		
		Context context = new Context(this.displayName, extensionList, classDefinitions, arucasOutput);
		context.setSuppressDeprecated(this.suppressDeprecated);
		return context;
	}
}
