package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.Position;
import me.senseiwells.arucas.values.MapValue;
import me.senseiwells.arucas.values.Value;

import java.util.HashMap;
import java.util.Map;

public class MapNode extends Node {
	public final Map<Node, Node> mapNode;

	public MapNode(Map<Node, Node> mapNode, Position startPos, Position endPos) {
		super(new Token(Token.Type.MAP, startPos, endPos));
		this.mapNode = mapNode;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		Map<Value<?>, Value<?>> valueMap = new HashMap<>();
		for (Map.Entry<Node, Node> entry : this.mapNode.entrySet()) {
			Value<?> key = entry.getKey().visit(context);
			Value<?> value = entry.getValue().visit(context);
			if (key.value == null || value.value == null) {
				throw new RuntimeError("Cannot put null inside a map", this.startPos, this.endPos, context);
			}
			
			valueMap.put(key, value);
		}
		return new MapValue(valueMap);
	}
}
