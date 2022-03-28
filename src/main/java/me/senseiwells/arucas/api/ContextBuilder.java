package me.senseiwells.arucas.api;

import me.senseiwells.arucas.api.impl.ArucasOutputImpl;
import me.senseiwells.arucas.values.classes.ArucasWrapperExtension;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.extensions.ArucasBuiltInExtension;
import me.senseiwells.arucas.extensions.ArucasMathClass;
import me.senseiwells.arucas.extensions.ArucasNetworkClass;
import me.senseiwells.arucas.extensions.discordapi.*;
import me.senseiwells.arucas.utils.ArucasClassDefinitionMap;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.AbstractBuiltInFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Runtime context class of the programming language
 */
@SuppressWarnings("unused")
public class ContextBuilder {
	private final List<Supplier<IArucasExtension>> extensions;
	private final List<Supplier<ArucasClassExtension>> classes;
	private final List<Supplier<IArucasWrappedClass>> wrappers;
	private IArucasOutput outputHandler;
	private boolean suppressDeprecated;
	private String displayName;

	public ContextBuilder() {
		this.extensions = new ArrayList<>();
		this.classes = new ArrayList<>();
		this.wrappers = new ArrayList<>();
		this.outputHandler = new ArucasOutputImpl();
		this.suppressDeprecated = false;
		this.displayName = "";
	}

	public ContextBuilder setDisplayName(String displayName) {
		this.displayName = Objects.requireNonNull(displayName);
		return this;
	}

	public ContextBuilder setSuppressDeprecated(boolean suppressDeprecated) {
		this.suppressDeprecated = suppressDeprecated;
		return this;
	}

	public ContextBuilder setOutputHandler(IArucasOutput outputHandler) {
		this.outputHandler = outputHandler;
		return this;
	}

	public ContextBuilder addDefaultExtensions() {
		return this.addExtensions(
			ArucasBuiltInExtension::new
		);
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
			TypeValue.ArucasTypeClass::new,
			EnumValue.ArucasEnumClass::new,
			FunctionValue.ArucasFunctionClass::new,
			StringValue.ArucasStringClass::new,
			BooleanValue.ArucasBooleanClass::new,
			ErrorValue.ArucasErrorClass::new,
			ListValue.ArucasListClass::new,
			SetValue.ArucasSetClass::new,
			MapValue.ArucasMapClass::new,
			NullValue.ArucasNullClass::new,
			NumberValue.ArucasNumberClass::new,
			ThreadValue.ArucasThreadClass::new,
			FileValue.ArucasFileClass::new,
			ArucasMathClass::new,
			ArucasNetworkClass::new
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

	public ContextBuilder addDefaultWrappers() {
		return this.addWrappers(
			DiscordAttachmentWrapper::new,
			DiscordBotWrapper::new,
			DiscordChannelWrapper::new,
			DiscordEventWrapper::new,
			DiscordMessageWrapper::new,
			DiscordServerWrapper::new,
			DiscordUserWrapper::new
		);
	}

	@SafeVarargs
	public final ContextBuilder addWrappers(Supplier<IArucasWrappedClass>... suppliers) {
		this.wrappers.addAll(List.of(suppliers));
		return this;
	}

	/**
	 * Make sure to define extensions before calling this method.
	 * This method will override all functions defined after this
	 * call.
	 */
	public ContextBuilder addDefault() {
		return this.addDefaultExtensions()
			.addDefaultClasses()
			.addDefaultWrappers();
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
			classDefinitions.add(ArucasWrapperExtension.createWrapper(supplier));
		}

		classDefinitions.merge();

		ArucasThreadHandler threadHandler = new ArucasThreadHandler();

		Context context = new Context(this.displayName, extensionList, classDefinitions, threadHandler, this.outputHandler);
		context.setSuppressDeprecated(this.suppressDeprecated);
		return context;
	}
}
