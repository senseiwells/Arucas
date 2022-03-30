package me.senseiwells.arucas.api;

import me.senseiwells.arucas.api.impl.ArucasOutputImpl;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.core.Arucas;
import me.senseiwells.arucas.extensions.ArucasBuiltInExtension;
import me.senseiwells.arucas.extensions.ArucasMathClass;
import me.senseiwells.arucas.extensions.ArucasNetworkClass;
import me.senseiwells.arucas.utils.ArucasClassDefinitionMap;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ExceptionUtils;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.classes.ArucasWrapperExtension;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

/**
 * Runtime context class of the programming language
 */
@SuppressWarnings("unused")
public class ContextBuilder {
	private final List<Supplier<IArucasExtension>> extensions;
	private final List<Supplier<ArucasClassExtension>> builtInClasses;
	private final Map<String, List<Supplier<ArucasClassExtension>>> classes;
	private final Map<String, List<Supplier<IArucasWrappedClass>>> wrappers;
	private IArucasOutput outputHandler;
	private boolean suppressDeprecated;
	private String displayName;
	private Path importPath;

	public ContextBuilder() {
		this.extensions = new ArrayList<>();
		this.builtInClasses = new ArrayList<>();
		this.classes = new HashMap<>();
		this.wrappers = new HashMap<>();
		this.outputHandler = new ArucasOutputImpl();
		this.suppressDeprecated = false;
		this.displayName = "";
		this.importPath = Arucas.PATH.resolve("libs");
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

	@SafeVarargs
	public final ContextBuilder addExtensions(Supplier<IArucasExtension>... extensions) {
		this.extensions.addAll(List.of(extensions));
		return this;
	}

	public ContextBuilder addDefaultClasses() {
		return this.addBuiltInClasses(
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

	/**
	 * This adds classes that will always be available at runtime, they do not need to be imported
	 */
	@SafeVarargs
	public final ContextBuilder addBuiltInClasses(Supplier<ArucasClassExtension>... extensions) {
		return this.addBuiltInClasses(List.of(extensions));
	}

	public final ContextBuilder addBuiltInClasses(List<Supplier<ArucasClassExtension>> extensions) {
		this.builtInClasses.addAll(extensions);
		return this;
	}

	/**
	 * This adds classes that will need to be imported with the file name
	 */
	@SafeVarargs
	public final ContextBuilder addClasses(String importFileName, Supplier<ArucasClassExtension>... extensions) {
		return this.addClasses(importFileName, List.of(extensions));
	}

	public final ContextBuilder addClasses(String importFileName, List<Supplier<ArucasClassExtension>> extensions) {
		List<Supplier<ArucasClassExtension>> suppliers = this.classes.computeIfAbsent(importFileName, s -> new ArrayList<>());
		suppliers.addAll(extensions);
		return this;
	}

	/**
	 * This adds wrapper classes that will need to be imported with the file name
	 */
	@SafeVarargs
	public final ContextBuilder addWrappers(String importFileName, Supplier<IArucasWrappedClass>... suppliers) {
		return this.addWrappers(importFileName, List.of(suppliers));
	}

	public final ContextBuilder addWrappers(String importFileName, List<Supplier<IArucasWrappedClass>> suppliers) {
		List<Supplier<IArucasWrappedClass>> supplierList = this.wrappers.computeIfAbsent(importFileName, s -> new ArrayList<>());
		supplierList.addAll(suppliers);
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

	/**
	 * Sets the path that arucas will use to import files
	 */
	public ContextBuilder setImportPath(Path newImportPath) {
		this.importPath = newImportPath;
		return this;
	}

	public ContextBuilder generateArucasFiles() throws IOException {
		if (!Files.exists(this.importPath)) {
			Files.createDirectories(this.importPath);
		}

		StringBuilder builder = new StringBuilder();
		Path generationPath = this.importPath.resolve("BuiltIn.arucas");

		for (Supplier<ArucasClassExtension> supplier : this.builtInClasses) {
			builder.append(supplier.get().toString()).append("\n\n");
		}
		Files.write(generationPath, Collections.singleton(builder.toString()));

		for (Map.Entry<String, List<Supplier<ArucasClassExtension>>> entry : this.classes.entrySet()) {
			builder = new StringBuilder();
			generationPath = this.importPath.resolve(entry.getKey() + ".arucas");
			for (Supplier<ArucasClassExtension> supplier : entry.getValue()) {
				builder.append(supplier.get()).append("\n\n");
			}
			Files.write(generationPath, Collections.singleton(builder.toString()));
		}

		for (Map.Entry<String, List<Supplier<IArucasWrappedClass>>> entry : this.wrappers.entrySet()) {
			builder = new StringBuilder();
			generationPath = this.importPath.resolve(entry.getKey() + ".arucas");
			for (Supplier<IArucasWrappedClass> supplier : entry.getValue()) {
				builder.append(ArucasWrapperExtension.createWrapper(supplier)).append("\n\n");
			}
			Files.write(generationPath, Collections.singleton(builder.toString()));
		}

		return this;
	}

	public Context build() {
		ArucasFunctionMap<FunctionValue> extensionList = new ArucasFunctionMap<>();
		ArucasClassDefinitionMap classDefinitions = new ArucasClassDefinitionMap();

		for (Supplier<IArucasExtension> supplier : this.extensions) {
			extensionList.addAll(supplier.get().getDefinedFunctions());
		}

		for (Supplier<ArucasClassExtension> supplier : this.builtInClasses) {
			classDefinitions.add(supplier.get());
		}

		Map<String, ArucasClassDefinitionMap> importables = new HashMap<>();
		importables.put("BuiltIn", classDefinitions);

		this.classes.forEach((s, suppliers) -> {
			ArucasClassDefinitionMap definitions = new ArucasClassDefinitionMap();
			for (Supplier<ArucasClassExtension> supplier : suppliers) {
				definitions.add(supplier.get());
			}
			importables.put(s, definitions);
		});

		this.wrappers.forEach((s, suppliers) -> {
			ArucasClassDefinitionMap definitions = new ArucasClassDefinitionMap();
			for (Supplier<IArucasWrappedClass> supplier : suppliers) {
				definitions.add(ArucasWrapperExtension.createWrapper(supplier));
			}
			importables.put(s, definitions);
		});

		classDefinitions.merge();

		ArucasThreadHandler threadHandler = new ArucasThreadHandler();

		Context context = new Context(this.displayName, extensionList, classDefinitions, importables, null, threadHandler, this.outputHandler, this.importPath);
		context.setSuppressDeprecated(this.suppressDeprecated);
		return context;
	}
}
