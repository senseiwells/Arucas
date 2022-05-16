package me.senseiwells.arucas.api.docs.parser;

import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.lang.reflect.Method;
import java.util.*;

public class ExtensionDocParser extends DocParser {
	private final List<IArucasExtension> extensions;

	public ExtensionDocParser(List<IArucasExtension> extensions) {
		this.extensions = extensions;
	}

	@Override
	public String parse() {
		StringBuilder builder = new StringBuilder();

		builder.append("/* Natively implemented in Java\n");
		builder.append(" * These functions are treated as if\n");
		builder.append(" * they were defined in the global scope.\n");
		builder.append(" * These do not need to be imported\n");
		builder.append(" */\n\n");

		for (IArucasExtension extension : this.extensions) {
			builder.append("/* ").append(extension.getName()).append(" */\n");

			Map<String, FunctionDoc> functionDocMap = new HashMap<>();
			for (Method method : extension.getClass().getDeclaredMethods()) {
				FunctionDoc doc = method.getAnnotation(FunctionDoc.class);
				if (doc != null) {
					String functionId = doc.name() + (doc.isVarArgs() ? "-1" : doc.params().length / 3);
					functionDocMap.put(functionId, doc);
				}
			}

			for (FunctionValue function : extension.getDefinedFunctions()) {
				String functionId = function.getName() + function.getCount();
				FunctionDoc doc = functionDocMap.get(functionId);
				List<String> parameterNames = this.addFunctionDoc(builder, doc);
				builder.append("fun ").append(function.getName()).append("(");
				this.addArgumentsToBuilder(builder, parameterNames, function.getCount());
				builder.append(");\n\n");
			}
		}

		return builder.toString();
	}

	@Override
	protected String getIndent() {
		return "";
	}
}
