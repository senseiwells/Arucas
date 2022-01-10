package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.impl.ArucasValueMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.MapValue;
import me.senseiwells.arucas.values.Value;

import java.util.Map;

public class MapNode extends Node {
	private final Map<Node, Node> mapNode;

	public MapNode(Map<Node, Node> mapNode, ISyntax startPos, ISyntax endPos) {
		super(new Token(Token.Type.MAP, startPos, endPos));
		this.mapNode = mapNode;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		ArucasValueMap valueMap = new ArucasValueMap();
		for (Map.Entry<Node, Node> entry : this.mapNode.entrySet()) {
			Value<?> key = entry.getKey().visit(context);
			Value<?> value = entry.getValue().visit(context);
			if (key.value == null || value.value == null) {
				throw new RuntimeError("Cannot put null inside a map", this.syntaxPosition, context);
			}
			
			valueMap.put(key, value);
		}
		return new MapValue(valueMap);
	}
}
