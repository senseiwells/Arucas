package me.senseiwells.arucas.api.docs.parser;

import me.senseiwells.arucas.api.ContextBuilder;
import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.api.docs.MemberDoc;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CodeDocParser extends DocParser {
	private static final String INDENT = "    ";
	private static final String NO_INDENT = "";

	public static CodeDocParser of(ContextBuilder builder) {
		CodeDocParser parser = new CodeDocParser();
		parser.addFromBuilder(builder);
		return parser;
	}

	public CodeDocParser() {
		super();
	}

	@Override
	public String parse() {
		return this.parseExtensions() + "\n\n\n" + this.parseClasses();
	}

	public String parseExtensions() {
		StringBuilder builder = new StringBuilder();

		builder.append("/* Natively implemented in Java\n");
		builder.append(" * These functions are treated as if\n");
		builder.append(" * they were defined in the global scope.\n");
		builder.append(" * These do not need to be imported\n");
		builder.append(" */\n\n");

		for (IArucasExtension extension : this.getExtensions()) {
			builder.append("/* ").append(extension.getName()).append(" */\n");

			ParsedExtensionDocs docs = new ParsedExtensionDocs(extension);

			for (FunctionValue function : extension.getDefinedFunctions()) {
				String functionId = function.getName() + function.getCount();
				FunctionDoc doc = docs.getFunctionDoc(functionId);
				List<String> parameterNames = this.addFunctionDoc(builder, NO_INDENT, doc);
				builder.append("fun ").append(function.getName()).append("(");
				this.addArgumentsToBuilder(builder, parameterNames, function.getCount());
				builder.append(");\n\n");
			}
		}

		return builder.toString();
	}

	public String parseClasses() {
		StringBuilder builder = new StringBuilder();
		for (AbstractClassDefinition definition : this.getDefinitions()) {
			builder.append(this.parseClass(definition)).append("\n\n");
		}
		return builder.toString();
	}

	private String parseClass(AbstractClassDefinition definition) {
		StringBuilder builder = new StringBuilder();
		ParsedClassDocs docs = new ParsedClassDocs(definition);

		ClassDoc classDoc = docs.getClassDoc();
		if (classDoc != null) {
			builder.append("/* Native, implemented in Java\n");
			for (String desc : classDoc.desc()) {
				builder.append(" * ").append(desc).append("\n");
			}
			if (classDoc.name().isEmpty()) {
				builder.append(" * This class does not need to be imported.");
			}
			else {
				builder.append(" * Import with 'import ").append(classDoc.name());
				builder.append(" from ").append(classDoc.importPath()).append("'");
			}
			builder.append("\n */\n");
		}
		else {
			builder.append("/* Native, implemented in Java */\n");
		}

		builder.append("class ");

		builder.append(definition.getName()).append(" {\n");
		boolean hadVars = false;
		for (String member : definition.getStaticMemberVariables().keySet()) {
			MemberDoc doc = docs.getMemberDoc("$" + member);
			if (doc != null) {
				builder.append(INDENT).append("/*\n");
				builder.append(INDENT).append(" * ").append(doc.desc()).append("\n");
				builder.append(INDENT).append(" * The type of the field is ").append(doc.type()).append("\n");
				builder.append(INDENT).append(" * This field is ").append(doc.assignable() ? "" : "not ").append("assignable\n");
				builder.append(INDENT).append(" */\n");
			}
			builder.append(INDENT).append("static var ").append(member).append(";\n");
			hadVars = true;
		}

		if (hadVars) {
			builder.append("\n");
		}

		for (FunctionValue function : definition.getConstructors()) {
			int parameterCount = docs.isWrapper() ? function.getCount() - 1 : function.getCount();
			ConstructorDoc doc = docs.getConstructorDoc(parameterCount);
			List<String> parameterNames = null;
			if (doc != null) {
				builder.append(INDENT).append("/*\n");
				for (String desc : doc.desc()) {
					builder.append(INDENT).append(" * ").append(desc).append("\n");
				}
				parameterNames = this.addParameters(builder, INDENT, doc.params());
				builder.append(INDENT).append(" */\n");
			}
			builder.append(INDENT).append(definition.getName()).append("(");
			this.addArgumentsToBuilder(builder, parameterNames, parameterCount);
			builder.append(");\n\n");
		}


		for (FunctionValue function : definition.getMethods()) {
			String functionId = function.getName() + (function.getCount() == -1 ? "-1" : function.getCount() - 1);
			FunctionDoc doc = docs.getFunctionDoc(functionId);
			List<String> parameterNames = this.addFunctionDoc(builder, INDENT, doc);
			builder.append(INDENT).append("fun ").append(function.getName()).append("(");
			this.addArgumentsToBuilder(builder, parameterNames, function.getCount() - 1);
			builder.append(");\n\n");
		}

		for (FunctionValue function : definition.getStaticMethods()) {
			FunctionDoc doc = docs.getFunctionDoc("$" + function.getName() + function.getCount());
			List<String> parameterNames = this.addFunctionDoc(builder, INDENT, doc);
			builder.append(INDENT).append("static fun ").append(function.getName()).append("(");
			this.addArgumentsToBuilder(builder, parameterNames, function.getCount());
			builder.append(");\n\n");
		}

		String classAsString = builder.toString();
		if (classAsString.endsWith("\n\n")) {
			classAsString = classAsString.substring(0, classAsString.length() - 1);
		}
		classAsString += "}";
		return classAsString;
	}

	protected void addArgumentsToBuilder(StringBuilder builder, List<String> args, int params) {
		if (args == null) {
			if (params < 0) {
				builder.append("args...");
				return;
			}
			for (int i = 0; i < params; i++) {
				String argName = "arg" + (i + 1);
				builder.append(argName);
				if (i < params - 1) {
					builder.append(", ");
				}
			}
			return;
		}

		Iterator<String> stringIterator = args.iterator();
		while (stringIterator.hasNext()) {
			String argName = stringIterator.next();
			builder.append(argName);
			if (stringIterator.hasNext()) {
				builder.append(", ");
			}
		}
	}

	protected List<String> addFunctionDoc(StringBuilder builder, String indent, FunctionDoc doc) {
		List<String> parameterNames = null;
		if (doc != null) {
			builder.append(indent).append("/*\n");

			boolean deprecated = false;
			for (String deprecate : doc.deprecated()) {
				builder.append(indent).append(" * ");
				if (!deprecated) {
					builder.append("Deprecated: ");
					deprecated = true;
				}
				builder.append(deprecate).append("\n");
			}

			for (String desc : doc.desc()) {
				builder.append(indent).append(" * ").append(desc).append("\n");
			}

			parameterNames = this.addParameters(builder, indent, doc.params());

			String[] returns = doc.returns();
			if (returns.length == 2) {
				builder.append(indent).append(" * ").append("Returns - ").append(returns[0]);
				builder.append(": ").append(returns[1]).append("\n");
			}

			boolean hasThrown = false;
			for (String throwMessage : doc.throwMsgs()) {
				if (!hasThrown) {
					builder.append(indent).append(" * ").append("Throws - Error: ").append(throwMessage);
					hasThrown = true;
					continue;
				}
				builder.append(", ").append(throwMessage);
			}
			if (hasThrown) {
				builder.append("\n");
			}

			builder.append(indent).append(" */\n");
		}
		return parameterNames;
	}

	protected List<String> addParameters(StringBuilder builder, String indent, String[] params) {
		if (params.length % 3 == 0) {
			List<String> parameterNames = new ArrayList<>();
			for (int i = 0; i < params.length; i += 3) {
				builder.append(indent).append(" * ").append("Parameter - ");
				builder.append(params[i]).append(" (").append(params[i + 1]).append("): ");
				builder.append(params[i + 2]).append("\n");
				parameterNames.add(params[i + 1]);
			}
			return parameterNames;
		}
		return null;
	}
}
