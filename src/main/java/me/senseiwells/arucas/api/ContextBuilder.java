package me.senseiwells.arucas.api;

import me.senseiwells.arucas.api.docs.parser.ClassDocParser;
import me.senseiwells.arucas.api.docs.parser.ExtensionDocParser;
import me.senseiwells.arucas.api.impl.DefaultArucasAPI;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.extensions.ArucasBuiltInExtension;
import me.senseiwells.arucas.extensions.ArucasMathClass;
import me.senseiwells.arucas.extensions.util.ArucasNetworkClass;
import me.senseiwells.arucas.extensions.util.CollectorValue;
import me.senseiwells.arucas.extensions.util.JavaValue;
import me.senseiwells.arucas.utils.*;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.arucas.utils.impl.ArucasSet;
import me.senseiwells.arucas.utils.impl.ArucasThread;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.values.classes.ArucasWrapperCreator;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Runtime context class of the programming language
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ContextBuilder {
	private final List<Supplier<IArucasExtension>> extensions;
	private final List<Supplier<ArucasClassExtension>> builtInClasses;
	private final Map<String, List<Supplier<ArucasClassExtension>>> classes;
	private final Map<String, List<Supplier<IArucasWrappedClass>>> wrappers;
	private final ValueConverter converter;

	private IArucasAPI arucasAPI;
	private String displayName;
	private boolean suppressDeprecated;
	private int poolSize;

	public ContextBuilder() {
		this.extensions = new ArrayList<>();
		this.builtInClasses = new ArrayList<>();
		this.classes = new HashMap<>();
		this.wrappers = new HashMap<>();
		this.converter = new ValueConverter();
		this.arucasAPI = new DefaultArucasAPI();
		this.displayName = "";
		this.poolSize = 2;
	}

	public ContextBuilder setDisplayName(String displayName) {
		this.displayName = Objects.requireNonNull(displayName);
		return this;
	}

	public ContextBuilder setSuppressDeprecated(boolean suppressDeprecated) {
		this.suppressDeprecated = suppressDeprecated;
		return this;
	}

	public ContextBuilder setThreadPoolSize(int size) {
		this.poolSize = size;
		return this;
	}

	public ContextBuilder setArucasAPI(IArucasAPI api) {
		this.arucasAPI = api;
		return this;
	}

	@SafeVarargs
	public final ContextBuilder addExtensions(Supplier<IArucasExtension>... extensions) {
		this.extensions.addAll(List.of(extensions));
		return this;
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
	 * This lets you add a Java class to be converted to a value. <br>
	 * The object that is passed into the function is
	 * guaranteed to be of the same type as clazz
	 */
	public final <T> ContextBuilder addConversion(Class<T> clazz, Functions.Bi<T, Context, Value> converter) {
		this.converter.addClass(clazz, converter);
		return this;
	}

	/**
	 * This allows you to add a converter for all arrays, since you cannot
	 * check for primitives and Object arrays with a class check. So we
	 * have a function to box all primitive arrays to Object arrays
	 */
	public final ContextBuilder addArrayConversion(Functions.Bi<Object[], Context, Value> converter) {
		this.converter.addArrayConversion(converter);
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
			.addDefaultConversions();
	}

	public ContextBuilder addDefaultExtensions() {
		return this.addExtensions(
			ArucasBuiltInExtension::new
		);
	}

	public ContextBuilder addDefaultClasses() {
		this.addBuiltInClasses(
			GenericValue.ArucasBaseClass::new,
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
			ArucasMathClass::new
		);
		this.addClasses(
			"util\\Collection",
			CollectorValue.ArucasCollectorClass::new
		);
		this.addClasses(
			"util\\Network",
			ArucasNetworkClass::new
		);
		this.addClasses(
			"util\\Internal",
			JavaValue.ArucasJavaClass::new
		);
		return this;
	}

	public ContextBuilder addDefaultConversions() {
		this.addConversion(String.class, (s, c) -> StringValue.of(s));
		this.addConversion(Number.class, (n, c) -> NumberValue.of(n.doubleValue()));
		this.addConversion(Boolean.class, (s, c) -> BooleanValue.of(s));
		this.addConversion(ArucasThread.class, (t, c) -> ThreadValue.of(t));
		this.addConversion(File.class, (f, c) -> FileValue.of(f));
		this.addConversion(ArucasList.class, (l, c) -> new ListValue(l));
		this.addConversion(ArucasSet.class, (s, c) -> new SetValue(s));
		this.addConversion(ArucasMap.class, (m, c) -> new MapValue(m));
		this.addConversion(AbstractClassDefinition.class, (a, c) -> a.getType());
		this.addConversion(Stream.class, (s, c) -> c.convertValue(s.toList()));
		this.addArrayConversion((o, c) -> {
			ArucasList arucasList = new ArucasList();
			for (Object object : o) {
				arucasList.add(c.convertValue(object));
			}
			return new ListValue(arucasList);
		});
		this.addConversion(List.class, (l, c) -> {
			ArucasList arucasList = new ArucasList();
			for (Object object : l) {
				arucasList.add(c.convertValue(object));
			}
			return new ListValue(arucasList);
		});
		this.addConversion(Set.class, (s, c) -> {
			ArucasSet arucasSet = new ArucasSet();
			for (Object object : s) {
				arucasSet.add(c, c.convertValue(object));
			}
			return new SetValue(arucasSet);
		});
		this.addConversion(Map.class, (m, c) -> {
			Map<?, ?> map = (Map<?, ?>) m;
			ArucasMap arucasMap = new ArucasMap();
			for (Map.Entry<?, ?> entry : map.entrySet()) {
				Value key = c.convertValue(entry.getKey());
				Value value = c.convertValue(entry.getValue());
				arucasMap.put(c, key, value);
			}
			return new MapValue(arucasMap);
		});
		return this;
	}

	public List<Supplier<IArucasExtension>> getExtensions() {
		return this.extensions;
	}

	public List<Supplier<ArucasClassExtension>> getBuiltInClasses() {
		return this.builtInClasses;
	}

	public Map<String, List<Supplier<ArucasClassExtension>>> getClasses() {
		return this.classes;
	}

	public Map<String, List<Supplier<IArucasWrappedClass>>> getWrappers() {
		return this.wrappers;
	}

	public ContextBuilder generateArucasFiles() throws IOException {
		ArucasClassDefinitionMap classDefinitions = new ArucasClassDefinitionMap();

		for (Supplier<ArucasClassExtension> supplier : this.builtInClasses) {
			classDefinitions.add(supplier.get());
		}

		Map<String, ArucasClassDefinitionMap> importables = new HashMap<>();
		importables.put("BuiltIn", classDefinitions);

		this.classes.forEach((s, suppliers) -> {
			ArucasClassDefinitionMap definitions = importables.computeIfAbsent(s, str -> new ArucasClassDefinitionMap());
			for (Supplier<ArucasClassExtension> supplier : suppliers) {
				definitions.add(supplier.get());
			}
		});

		this.wrappers.forEach((s, suppliers) -> {
			ArucasClassDefinitionMap definitions = importables.computeIfAbsent(s, str -> new ArucasClassDefinitionMap());
			for (Supplier<IArucasWrappedClass> supplier : suppliers) {
				definitions.add(ArucasWrapperCreator.createWrapper(supplier));
			}
		});

		for (Map.Entry<String, ArucasClassDefinitionMap> entry : importables.entrySet()) {
			StringBuilder builder = new StringBuilder();
			Path generationPath = this.arucasAPI.getImportPath().resolve(entry.getKey() + ".arucas");
			Path parent = generationPath.getParent();
			if (!Files.exists(parent)) {
				Files.createDirectories(parent);
			}
			for (AbstractClassDefinition definition : entry.getValue()) {
				builder.append(new ClassDocParser(definition).parse()).append("\n\n");
			}
			Files.write(generationPath, Collections.singleton(builder.toString()));
		}

		if (!this.extensions.isEmpty()) {
			Path generationPath = this.arucasAPI.getImportPath().resolve("Extensions.arucas");
			Path parent = generationPath.getParent();
			if (!Files.exists(parent)) {
				Files.createDirectories(parent);
			}
			String documentation = new ExtensionDocParser(this.extensions.stream().map(Supplier::get).toList()).parse();
			Files.write(generationPath, Collections.singleton(documentation));
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
			ArucasClassDefinitionMap definitions = importables.computeIfAbsent(s, str -> new ArucasClassDefinitionMap());
			for (Supplier<ArucasClassExtension> supplier : suppliers) {
				definitions.add(supplier.get());
			}
		});

		this.wrappers.forEach((s, suppliers) -> {
			ArucasClassDefinitionMap definitions = importables.computeIfAbsent(s, str -> new ArucasClassDefinitionMap());
			for (Supplier<IArucasWrappedClass> supplier : suppliers) {
				definitions.add(ArucasWrapperCreator.createWrapper(supplier));
			}
		});

		importables.values().forEach(ArucasClassDefinitionMap::merge);
		classDefinitions.merge();

		ArucasThreadHandler threadHandler = new ArucasThreadHandler();
		threadHandler.setThreadPoolSize(this.poolSize);

		Context context = new Context(this.displayName, null, extensionList, threadHandler, this.converter, this.arucasAPI);
		context.setSuppressDeprecated(this.suppressDeprecated);
		return context.setStackTable(classDefinitions, importables, null);
	}
}
