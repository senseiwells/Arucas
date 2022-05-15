package me.senseiwells.arucas.api.docs.parser;

import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.api.docs.MemberDoc;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.values.classes.WrapperClassDefinition;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class ClassDocParser extends DocParser {
	private static final String INDENT = "    ";

	private final AbstractClassDefinition definition;
	private final Class<?> definitionClass;
	private final boolean isWrapper;

	public ClassDocParser(AbstractClassDefinition definition) {
		this.definition = definition;
		this.definitionClass = definition.getDefiningClass();
		this.isWrapper = definition instanceof WrapperClassDefinition;
	}

	/**
	 * This generates the Java implemented Arucas functions
	 * into Arucas code, including all the methods and fields,
	 * that are available, and also providing documentation for
	 * the classes, fields and functions.
	 */
	public String parse() {
		StringBuilder builder = new StringBuilder();

		ClassDoc classDoc = this.definitionClass.getAnnotation(ClassDoc.class);
		if (classDoc != null) {
			builder.append("/* Native, implemented in Java\n");
			for (String desc : classDoc.desc()) {
				builder.append(" * ").append(desc);
			}
			builder.append("\n * Import with 'import ").append(classDoc.name());
			builder.append(" from ").append(classDoc.importPath()).append("'");
			builder.append("\n */\n");
		}
		else {
			builder.append("/* Native, implemented in Java */\n");
		}

		Map<Integer, ConstructorDoc> constructorDocMap = new HashMap<>();
		Map<String, FunctionDoc> functionDocMap = new HashMap<>();
		Map<String, MemberDoc> memberDocMap = new HashMap<>();
		
		for (Method method : this.isWrapper ? this.definitionClass.getMethods() : this.definitionClass.getDeclaredMethods()) {
			ConstructorDoc constructorDoc = method.getAnnotation(ConstructorDoc.class);
			if (constructorDoc != null) {
				constructorDocMap.put(constructorDoc.params().length / 3, constructorDoc);
				continue;
			}
			FunctionDoc functionDoc = method.getAnnotation(FunctionDoc.class);
			if (functionDoc != null) {
				String functionId = (functionDoc.isStatic() ? "$" : "") + functionDoc.name();
				functionId += functionDoc.isVarArgs() ? "-1" : functionDoc.params().length / 3;
				functionDocMap.put(functionId, functionDoc);
				continue;
			}
			MemberDoc[] memberDocs = method.getAnnotationsByType(MemberDoc.class);
			for (MemberDoc memberDoc : memberDocs) {
				String memberId = (memberDoc.isStatic() ? "$" : "") + memberDoc.name();
				memberDocMap.put(memberId, memberDoc);
			}
		}
		if (this.isWrapper) {
			for (Field field : this.definitionClass.getFields()) {
				MemberDoc memberDoc = field.getAnnotation(MemberDoc.class);
				if (memberDoc != null) {
					String memberId = (memberDoc.isStatic() ? "$" : "") + memberDoc.name();
					memberDocMap.put(memberId, memberDoc);
				}
			}
		}

		builder.append("class ");

		builder.append(this.definition.getName()).append(" {\n");
		boolean hadVars = false;
		for (String member : this.definition.getStaticMemberVariables().keySet()) {
			MemberDoc doc = memberDocMap.get("$" + member);
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

		for (FunctionValue function : this.definition.getConstructors()) {
			int parameterCount = this.isWrapper ? function.getCount() - 1 : function.getCount();
			ConstructorDoc doc = constructorDocMap.get(parameterCount);
			List<String> parameterNames = null;
			if (doc != null) {
				builder.append(INDENT).append("/*\n");
				for (String desc : doc.desc()) {
					builder.append(INDENT).append(" * ").append(desc).append("\n");
				}
				parameterNames = this.addParameters(builder, doc.params());
				builder.append(INDENT).append(" */\n");
			}
			builder.append(INDENT).append(this.definition.getName()).append("(");
			this.addArgumentsToBuilder(builder, parameterNames, parameterCount);
			builder.append(");\n\n");
		}


		for (FunctionValue function : this.definition.getMethods()) {
			String functionId = function.getName() + (function.getCount() == -1 ? "-1" : function.getCount() - 1);
			FunctionDoc doc = functionDocMap.get(functionId);
			List<String> parameterNames = this.addFunctionDoc(builder, doc);
			builder.append(INDENT).append("fun ").append(function.getName()).append("(");
			this.addArgumentsToBuilder(builder, parameterNames, function.getCount() - 1);
			builder.append(");\n\n");
		}

		for (FunctionValue function : this.definition.getStaticMethods()) {
			FunctionDoc doc = functionDocMap.get("$" + function.getName() + function.getCount());
			List<String> parameterNames = this.addFunctionDoc(builder, doc);
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

	@Override
	protected String getIndent() {
		return INDENT;
	}
}
