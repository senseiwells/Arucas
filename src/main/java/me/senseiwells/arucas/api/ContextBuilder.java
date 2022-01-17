package me.senseiwells.arucas.api;

import me.senseiwells.arucas.api.impl.ArucasOutput;
import me.senseiwells.arucas.api.wrappers.ArucasWrapper;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.extensions.ArucasBuiltInExtension;
import me.senseiwells.arucas.extensions.ArucasMathClass;
import me.senseiwells.arucas.utils.ArucasClassDefinitionMap;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.AbstractBuiltInFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Runtime context class of the programming language
 */
@SuppressWarnings("unused")
public class ContextBuilder {
	private final List<Supplier<IArucasExtension>> extensions = new ArrayList<>();
	private final List<Supplier<ArucasClassExtension>> classes = new ArrayList<>();
	private final List<Supplier<IArucasWrappedClass>> wrappers = new ArrayList<>();
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
		return this.addClasses(
			Value.ArucasBaseClass::new,
			StringValue.ArucasStringClass::new,
			BooleanValue.ArucasBooleanClass::new,
			ListValue.ArucasListClass::new,
			MapValue.ArucasMapClass::new,
			NullValue.ArucasNullClass::new,
			NumberValue.ArucasNumberClass::new,
			ThreadValue.ArucasThreadClass::new,
			FileValue.ArucasFileClass::new,
			JsonValue.ArucasJsonClass::new,
			ArucasMathClass::new
		);
	}
	
	public ContextBuilder addClasses(List<Supplier<ArucasClassExtension>> extensions) {
		this.classes.addAll(extensions);
		return this;
	}
	
	@SafeVarargs
	public final ContextBuilder addClasses(Supplier<ArucasClassExtension>... extensions) {
		this.classes.addAll(List.of(extensions));
		return this;
	}
	
	public ContextBuilder addWrapper(Supplier<IArucasWrappedClass> supplier) {
		this.wrappers.add(supplier);
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
		ArucasFunctionMap<AbstractBuiltInFunction<?>> extensionList = new ArucasFunctionMap<>();
		ArucasClassDefinitionMap classDefinitions = new ArucasClassDefinitionMap();

		for (Supplier<IArucasExtension> supplier : this.extensions) {
			extensionList.addAll(supplier.get().getDefinedFunctions());
		}

		for (Supplier<ArucasClassExtension> supplier : this.classes) {
			classDefinitions.add(supplier.get());
		}
		
		for (Supplier<IArucasWrappedClass> supplier : this.wrappers) {
			classDefinitions.add(ArucasWrapper.createWrapper(supplier));
		}

		classDefinitions.merge();
		
		ArucasOutput arucasOutput = new ArucasOutput();
		arucasOutput.setOutputHandler(this.outputHandler);
		
		ArucasThreadHandler threadHandler = new ArucasThreadHandler();
		
		Context context = new Context(this.displayName, extensionList, classDefinitions, threadHandler, arucasOutput);
		context.setSuppressDeprecated(this.suppressDeprecated);
		return context;
	}
}
