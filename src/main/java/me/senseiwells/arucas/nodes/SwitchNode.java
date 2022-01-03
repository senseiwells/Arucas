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
	private final Map<Node, Set<Value<?>>> cases;
	private final Node valueNode;
	private final Node defaultCase;
	
	public SwitchNode(Node valueNode, Node defaultCase, Map<Node, Set<Value<?>>> cases, ISyntax startPos, ISyntax endPos) {
		super(new Token(Token.Type.SWITCH, startPos, endPos));
		this.valueNode = valueNode;
		this.defaultCase = defaultCase;
		this.cases = cases;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		context.pushSwitchScope(this.syntaxPosition);
		Value<?> value = this.valueNode.visit(context);
		
		try {
			boolean matched = false;
			for (Map.Entry<Node, Set<Value<?>>> entry : this.cases.entrySet()) {
				Set<Value<?>> set = entry.getValue();
				Node node = entry.getKey();
				
				if (set.contains(value)) {
					node.visit(context);
					matched = true;
					break;
				}
			}
			
			if (!matched && this.defaultCase != null) {
				this.defaultCase.visit(context);
			}
		}
		catch (ThrowValue.Break tv) {
			context.moveScope(context.getBreakScope());
		}
		
		context.popScope();
		return new NullValue();
	}
}
