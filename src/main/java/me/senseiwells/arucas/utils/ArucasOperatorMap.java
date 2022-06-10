package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.util.HashMap;
import java.util.Map;

public class ArucasOperatorMap<T extends FunctionValue> {
	private Map<Integer, Map<Token.Type, T>> operatorsMap;

	public void add(Token.Type type, T function) {
		if (this.operatorsMap == null) {
			this.operatorsMap = new HashMap<>();
		}

		Map<Token.Type, T> operatorMap = this.operatorsMap.computeIfAbsent(function.getCount(), i -> new HashMap<>());
		operatorMap.put(type, function);
	}

	public T get(Token.Type type, int parameterCount) {
		if (this.operatorsMap == null) {
			return null;
		}

		Map<Token.Type, T> operatorMap = this.operatorsMap.get(parameterCount);
		if (operatorMap != null) {
			return operatorMap.get(type);
		}
		return null;
	}

	public void forEach(Functions.TriConsumer<Integer, Token.Type, T> triConsumer) {
		if (this.operatorsMap != null) {
			for (Map.Entry<Integer, Map<Token.Type, T>> entry : this.operatorsMap.entrySet()) {
				int parameters = entry.getKey();
				for (Map.Entry<Token.Type, T> operatorMap : entry.getValue().entrySet()) {
					triConsumer.accept(parameters, operatorMap.getKey(), operatorMap.getValue());
				}
			}
		}
	}
}
