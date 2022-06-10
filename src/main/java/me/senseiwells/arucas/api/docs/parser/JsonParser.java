package me.senseiwells.arucas.api.docs.parser;

import com.google.gson.*;
import me.senseiwells.arucas.api.ContextBuilder;
import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.api.docs.MemberDoc;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.values.classes.WrapperClassDefinition;
import me.senseiwells.arucas.values.functions.FunctionValue;

public class JsonParser extends DocParser {
	private static final Gson GSON;

	static {
		GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
	}

	public static JsonParser of(ContextBuilder builder) {
		JsonParser parser = new JsonParser();
		parser.addFromBuilder(builder);
		return parser;
	}

	public JsonParser() {
		super();
	}

	@Override
	public String parse() {
		return GSON.toJson(this.toJson());
	}

	public JsonObject toJson() {
		JsonObject object = new JsonObject();

		object.add("extensions", this.getExtensionsAsJson());
		object.add("classes", this.getClassesAsJson());

		return object;
	}

	private JsonObject getExtensionsAsJson() {
		JsonObject allExtensions = new JsonObject();

		for (IArucasExtension extension : this.getExtensions()) {
			ParsedExtensionDocs docs = new ParsedExtensionDocs(extension);

			JsonArray array = new JsonArray();
			for (FunctionValue function : extension.getDefinedFunctions()) {
				String functionId = function.getName() + function.getCount();
				FunctionDoc doc = docs.getFunctionDoc(functionId);
				if (doc != null) {
					array.add(this.getFunctionAsJson(doc));
					continue;
				}
				JsonObject functionObject = new JsonObject();
				functionObject.addProperty("name", function.getName());
				array.add(functionObject);
			}

			allExtensions.add(extension.getName(), array);
		}

		return allExtensions;
	}

	private JsonObject getClassesAsJson() {
		JsonObject allClasses = new JsonObject();

		for (AbstractClassDefinition definition : this.getDefinitions()) {
			JsonObject classObject = new JsonObject();

			ParsedClassDocs docs = new ParsedClassDocs(definition);

			classObject.addProperty("name", definition.getName());
			JsonElement description = JsonNull.INSTANCE, importPath = JsonNull.INSTANCE;
			ClassDoc classDoc = docs.getClassDoc();
			if (classDoc != null) {
				description = this.arrayToJson(classDoc.desc());
				if (!classDoc.importPath().isBlank()) {
					importPath = new JsonPrimitive(classDoc.importPath());
				}
			}
			classObject.add("desc", description);
			classObject.add("import_path", importPath);

			JsonArray staticMembers = new JsonArray();
			for (String member : definition.getStaticMemberVariables().keySet()) {
				JsonObject memberObject = new JsonObject();
				MemberDoc memberDoc = docs.getMemberDoc("$" + member);
				memberObject.addProperty("name", member);

				if (memberDoc != null) {
					memberObject.addProperty("assignable", memberDoc.assignable());
					memberObject.add("desc", this.arrayToJson(memberDoc.desc()));
					memberObject.addProperty("type", memberDoc.type());
					memberObject.add("examples", this.arrayToJson(memberDoc.examples()));
				}

				staticMembers.add(memberObject);
			}
			classObject.add("static_members", staticMembers);

			if (docs.isWrapper()) {
				JsonArray members = new JsonArray();
				for (String member : ((WrapperClassDefinition) definition).getFieldNames()) {
					JsonObject memberObject = new JsonObject();
					MemberDoc memberDoc = docs.getMemberDoc(member);
					memberObject.addProperty("name", member);

					if (memberDoc != null) {
						memberObject.addProperty("assignable", memberDoc.assignable());
						memberObject.add("desc", this.arrayToJson(memberDoc.desc()));
						memberObject.addProperty("type", memberDoc.type());
						memberObject.add("examples", this.arrayToJson(memberDoc.examples()));
					}

					members.add(memberObject);
				}
				classObject.add("members", members);
			}

			JsonArray constructors = new JsonArray();
			for (FunctionValue function : definition.getConstructors()) {
				JsonObject constructor = new JsonObject();
				int parameters = docs.isWrapper() ? function.getCount() - 1 : function.getCount();
				ConstructorDoc doc = docs.getConstructorDoc(parameters);

				if (doc != null) {
					constructor.add("desc", this.arrayToJson(doc.desc()));
					if (doc.params().length > 0 && doc.params().length % 3 == 0) {
						constructor.add("params", this.paramsToJson(doc.params()));
					}
					constructor.add("examples", this.arrayToJson(doc.example()));
				}

				constructors.add(constructor);
			}
			classObject.add("constructors", constructors);

			JsonArray methods = new JsonArray();
			for (FunctionValue function : definition.getMethods()) {
				String functionId = function.getName() + (function.getCount() == -1 ? "-1" : function.getCount() - 1);
				FunctionDoc doc = docs.getFunctionDoc(functionId);

				if (doc != null) {
					methods.add(this.getFunctionAsJson(doc));
					continue;
				}

				JsonObject functionObject = new JsonObject();
				functionObject.addProperty("name", function.getName());
				methods.add(functionObject);
			}
			classObject.add("methods", methods);

			JsonArray staticMethods = new JsonArray();
			for (FunctionValue function : definition.getStaticMethods()) {
				FunctionDoc doc = docs.getFunctionDoc("$" + function.getName() + function.getCount());

				if (doc != null) {
					staticMethods.add(this.getFunctionAsJson(doc));
					continue;
				}

				JsonObject functionObject = new JsonObject();
				functionObject.addProperty("name", function.getName());
				methods.add(functionObject);
			}
			classObject.add("static_methods", staticMethods);

			allClasses.add(definition.getName(), classObject);
		}

		return allClasses;
	}

	private JsonObject getFunctionAsJson(FunctionDoc doc) {
		JsonObject object = new JsonObject();

		object.addProperty("name", doc.name());
		object.addProperty("is_arbitrary", doc.isVarArgs());
		// object.addProperty("is_static", doc.isStatic());

		if (doc.deprecated().length > 0) {
			object.add("deprecated", this.arrayToJson(doc.deprecated()));
		}

		object.add("desc", this.arrayToJson(doc.desc()));

		if (doc.params().length > 0 && doc.params().length % 3 == 0) {
			object.add("params", this.paramsToJson(doc.params()));
		}

		if (doc.returns().length == 2) {
			JsonObject returnObject = new JsonObject();

			returnObject.addProperty("type", doc.returns()[0]);
			returnObject.addProperty("desc", doc.returns()[1]);

			object.add("returns", returnObject);
		}

		if (doc.throwMsgs().length > 0) {
			object.add("throws", this.arrayToJson(doc.throwMsgs()));
		}

		object.add("examples", this.arrayToJson(doc.example()));

		return object;
	}

	private JsonArray paramsToJson(String[] params) {
		JsonArray allParameters = new JsonArray();

		for (int i = 0; i < params.length; ) {
			JsonObject param = new JsonObject();
			String type = params[i++];
			String name = params[i++];
			String desc = params[i++];

			param.addProperty("name", name);
			param.addProperty("type", type);
			param.addProperty("desc", desc);

			allParameters.add(param);
		}

		return allParameters;
	}

	private JsonArray arrayToJson(String... array) {
		JsonArray jsonArray = new JsonArray();

		for (String string : array) {
			jsonArray.add(string);
		}

		return jsonArray;
	}
}
