package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ArucasOperatorMap<T extends FunctionValue> {
	// Initialize these later to make sure we do not allocate empty memory
	private Map<Token.Type, T> unaryOperatorMap;
	private Map<Token.Type, T> binaryOperatorMap;

	public void add(Token.Type type, T function) {
		switch (function.getCount()) {
			case 1 -> {
				if (this.unaryOperatorMap == null) {
					this.unaryOperatorMap = new HashMap<>();
				}
				this.unaryOperatorMap.put(type, function);
			}
			case 2 -> {
				if (this.binaryOperatorMap == null) {
					this.binaryOperatorMap = new HashMap<>();
				}
				this.binaryOperatorMap.put(type, function);
			}
		}
	}

	public T get(Token.Type type, int parameterCount) {
		return switch (parameterCount) {
			case 1 -> this.unaryOperatorMap == null ? null : this.unaryOperatorMap.get(type);
			case 2 -> this.binaryOperatorMap == null ? null : this.binaryOperatorMap.get(type);
			default -> null;
		};
	}

	public boolean hasOperator(Token.Type type, int parameterCount) {
		return this.get(type, parameterCount) != null;
	}

	public void forEach(BiConsumer<Token.Type, T> biConsumer) {
		if (this.unaryOperatorMap != null) {
			for (Map.Entry<Token.Type, T> entry : this.unaryOperatorMap.entrySet()) {
				biConsumer.accept(entry.getKey(), entry.getValue());
			}
		}
		
		if (this.binaryOperatorMap != null) {
			for (Map.Entry<Token.Type, T> entry : this.binaryOperatorMap.entrySet()) {
				biConsumer.accept(entry.getKey(), entry.getValue());
			}
		}
	}
}
