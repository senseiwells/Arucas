package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.arucas.utils.impl.ArucasOrderedMap;
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
	public Value visit(Context context) throws CodeError, ThrowValue {
		ArucasMap valueMap = new ArucasOrderedMap();
		for (Map.Entry<Node, Node> entry : this.mapNode.entrySet()) {
			Value key = entry.getKey().visit(context);
			Value value = entry.getValue().visit(context);

			valueMap.put(context, key, value);
		}

		return new MapValue(valueMap);
	}
}
