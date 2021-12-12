package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

import java.util.Map;
import java.util.Set;

public class SwitchNode extends Node {
	private final Node valueNode;
	private final Map<Node, Set<Value<?>>> cases;
	
	public SwitchNode(Node valueNode, Map<Node, Set<Value<?>>> cases, ISyntax startPos, ISyntax endPos) {
		super(new Token(Token.Type.SWITCH, startPos, endPos));
		this.valueNode = valueNode;
		this.cases = cases;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		context.pushSwitchScope(this.syntaxPosition);
		Value<?> value = this.valueNode.visit(context);
		
		try {
			for (Map.Entry<Node, Set<Value<?>>> entry : this.cases.entrySet()) {
				Set<Value<?>> set = entry.getValue();
				Node node = entry.getKey();
				
				if (set.contains(value)) {
					node.visit(context);
					break;
				}
			}
		}
		catch (ThrowValue.Break tv) {
			context.moveScope(context.getBreakScope());
		}
		
		context.popScope();
		return new NullValue();
	}
}
