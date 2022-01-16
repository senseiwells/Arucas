package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.util.List;

public class MergedClassMethods {
	final ArucasFunctionMap<FunctionValue> methods;

	private MergedClassMethods() {
		this.methods = ArucasFunctionMap.of();
	}

	public FunctionValue getMethod(String name, int parameters) {
		return this.methods.get(name, parameters);
	}

	public static MergedClassMethods mergeMethods(List<AbstractClassDefinition> classDefinitions) {
		MergedClassMethods mergedMethods = new MergedClassMethods();

		for (int i = classDefinitions.size() - 1; i >= 0; i--) {
			mergedMethods.methods.addAll(classDefinitions.get(i).getMethods());
		}

		return mergedMethods;
	}
}
