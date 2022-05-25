package me.senseiwells.arucas.api.docs.parser;

import me.senseiwells.arucas.api.ContextBuilder;
import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.api.docs.MemberDoc;
import me.senseiwells.arucas.utils.ExceptionUtils;
import me.senseiwells.arucas.utils.ExceptionUtils.ThrowableSupplier;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.values.classes.WrapperClassDefinition;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

public abstract class DocParser {
	private final List<AbstractClassDefinition> definitions;
	private final List<IArucasExtension> extensions;

	protected DocParser() {
		this.definitions = new ArrayList<>();
		this.extensions = new ArrayList<>();
	}

	public abstract String parse();

	public void write(ThrowableSupplier<Path> path) {
		ExceptionUtils.runSafe(() -> Files.write(path.get(), Collections.singleton(this.parse())));
	}

	protected void addFromBuilder(ContextBuilder builder) {
		builder.getExtensions().stream()
			.map(Supplier::get).forEach(this::addExtension);
		builder.getBuiltInClasses().stream()
			.map(Supplier::get).forEach(this::addDefinition);
		builder.getClasses().values().stream()
			.flatMap(Collection::stream).forEach(e -> this.addDefinition(e.get()));
	}

	protected List<AbstractClassDefinition> getDefinitions() {
		return this.definitions;
	}

	protected List<IArucasExtension> getExtensions() {
		return this.extensions;
	}

	public void addDefinition(AbstractClassDefinition definition) {
		this.definitions.add(definition);
	}

	public void addExtension(IArucasExtension extension) {
		this.extensions.add(extension);
	}

	protected Integer getConstructorId(ConstructorDoc doc) {
		if (doc == null) {
			return null;
		}
		return doc.params().length / 3;
	}

	protected String getFunctionId(FunctionDoc doc) {
		if (doc == null) {
			return null;
		}
		var functionId = (doc.isStatic() ? "$" : "") + doc.name();
		return functionId + (doc.isVarArgs() ? "-1" : doc.params().length / 3);
	}

	protected String getMemberId(MemberDoc doc) {
		if (doc == null) {
			return null;
		}
		return (doc.isStatic() ? "$" : "") + doc.name();
	}

	protected class ParsedExtensionDocs {
		private final Map<String, FunctionDoc> functionDocMap;

		ParsedExtensionDocs(IArucasExtension extension) {
			this.functionDocMap = new HashMap<>();

			this.parse(extension);
		}

		public FunctionDoc getFunctionDoc(String functionId) {
			return this.functionDocMap.get(functionId);
		}

		private void parse(IArucasExtension extension) {
			for (Method method : extension.getClass().getDeclaredMethods()) {
				FunctionDoc doc = method.getAnnotation(FunctionDoc.class);
				String functionId = DocParser.this.getFunctionId(doc);
				if (functionId != null) {
					this.functionDocMap.put(functionId, doc);
				}
			}
		}
	}

	protected class ParsedClassDocs {
		private final Map<Integer, ConstructorDoc> constructorDocMap;
		private final Map<String, FunctionDoc> functionDocMap;
		private final Map<String, MemberDoc> memberDocMap;
		private final ClassDoc classDoc;
		private final boolean isWrapper;

		ParsedClassDocs(AbstractClassDefinition definition) {
			this.constructorDocMap = new HashMap<>();
			this.functionDocMap = new HashMap<>();
			this.memberDocMap = new HashMap<>();


			Class<?> definitionClass = definition.getDefiningClass();
			this.classDoc = definitionClass.getAnnotation(ClassDoc.class);
			this.isWrapper = definition instanceof WrapperClassDefinition;

			this.parse(definitionClass);
		}

		public ConstructorDoc getConstructorDoc(int parameters) {
			return this.constructorDocMap.get(parameters);
		}

		public FunctionDoc getFunctionDoc(String functionId) {
			return this.functionDocMap.get(functionId);
		}

		public MemberDoc getMemberDoc(String memberId) {
			return this.memberDocMap.get(memberId);
		}

		public ClassDoc getClassDoc() {
			return this.classDoc;
		}

		public boolean isWrapper() {
			return this.isWrapper;
		}

		private void parse(Class<?> definitionClass) {
			for (Method method : this.isWrapper ? definitionClass.getMethods() : definitionClass.getDeclaredMethods()) {
				ConstructorDoc constructorDoc = method.getAnnotation(ConstructorDoc.class);
				Integer constructorId = DocParser.this.getConstructorId(constructorDoc);
				if (constructorId != null) {
					this.constructorDocMap.put(constructorId, constructorDoc);
					continue;
				}

				FunctionDoc functionDoc = method.getAnnotation(FunctionDoc.class);
				String functionId = DocParser.this.getFunctionId(functionDoc);
				if (functionId != null) {
					this.functionDocMap.put(functionId, functionDoc);
					continue;
				}

				MemberDoc[] memberDocs = method.getAnnotationsByType(MemberDoc.class);
				for (MemberDoc memberDoc : memberDocs) {
					String memberId = DocParser.this.getMemberId(memberDoc);
					this.memberDocMap.put(memberId, memberDoc);
				}
			}

			if (this.isWrapper) {
				for (Field field : definitionClass.getFields()) {
					MemberDoc memberDoc = field.getAnnotation(MemberDoc.class);
					String memberId = DocParser.this.getMemberId(memberDoc);
					if (memberId != null) {
						this.memberDocMap.put(memberId, memberDoc);
					}
				}
			}
		}
	}
}
