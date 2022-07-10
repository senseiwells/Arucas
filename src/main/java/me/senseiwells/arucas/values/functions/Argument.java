package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.values.classes.AbstractClassDefinition;

import java.util.List;

public class Argument {
	private final String name;
	private List<AbstractClassDefinition> types;

	public Argument(String name) {
		this.name = name;
	}

	public void setTypes(List<AbstractClassDefinition> type) {
		this.types = type;
	}

	public String getName() {
		return this.name;
	}

	public List<AbstractClassDefinition> getTypes() {
		return this.types;
	}
}
