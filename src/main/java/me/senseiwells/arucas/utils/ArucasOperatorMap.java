package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ArucasOperatorMap<T extends FunctionValue> {
	private final Map<Token.Type, T> unaryOperatorMap;
	private final Map<Token.Type, T> binaryOperatorMap;

	public ArucasOperatorMap() {
		this.unaryOperatorMap = new HashMap<>();
		this.binaryOperatorMap = new HashMap<>();
	}

	public void add(Token.Type type, T function) {
		switch (function.getParameterCount()) {
			case 1 -> this.unaryOperatorMap.put(type, function);
			case 2 -> this.binaryOperatorMap.put(type, function);
		}
	}

	public T get(Token.Type type, int parameterCount) {
		return switch (parameterCount) {
			case 1 -> this.unaryOperatorMap.get(type);
			case 2 -> this.binaryOperatorMap.get(type);
			default -> null;
		};
	}

	public boolean hasOperator(Token.Type type, int parameterCount) {
		return this.get(type, parameterCount) != null;
	}

	public void forEach(BiConsumer<Token.Type, T> biConsumer) {
		for (Map.Entry<Token.Type, T> entry : this.unaryOperatorMap.entrySet()) {
			biConsumer.accept(entry.getKey(), entry.getValue());
		}
		for (Map.Entry<Token.Type, T> entry : this.binaryOperatorMap.entrySet()) {
			biConsumer.accept(entry.getKey(), entry.getValue());
		}
	}
}
