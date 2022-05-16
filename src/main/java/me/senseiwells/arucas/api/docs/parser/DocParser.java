package me.senseiwells.arucas.api.docs.parser;

import me.senseiwells.arucas.api.docs.FunctionDoc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class DocParser {
	protected abstract String getIndent();

	public abstract String parse();

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

	protected List<String> addFunctionDoc(StringBuilder builder, FunctionDoc doc) {
		String indent = this.getIndent();
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

			parameterNames = this.addParameters(builder, doc.params());

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

	protected List<String> addParameters(StringBuilder builder, String[] params) {
		if (params.length % 3 == 0) {
			String indent = this.getIndent();
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
