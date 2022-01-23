package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SwitchNode extends Node {
	private final List<Set<Object>> matches;
	private final List<Node> cases;
	private final Node valueNode;
	private final Node defaultCase;
	
	public SwitchNode(Node valueNode, Node defaultCase, List<Set<Object>> matches, List<Node> cases, ISyntax startPos, ISyntax endPos) {
		super(new Token(Token.Type.SWITCH, startPos, endPos));
		this.valueNode = valueNode;
		this.defaultCase = defaultCase;
		this.matches = matches;
		this.cases = cases;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		context.pushSwitchScope(this.syntaxPosition);
		Value<?> value = this.valueNode.visit(context);
		
		try {
			// Get the match object
			Object matchObject = value.value;
			for (int i = 0, len = this.matches.size(); i < len; i++) {
				if (this.matches.get(i).contains(matchObject)) {
					this.cases.get(i).visit(context);
					context.popScope();
					return NullValue.NULL;
				}
			}
			
			if (this.defaultCase != null) {
				this.defaultCase.visit(context);
			}
		}
		catch (ThrowValue.Break tv) {
			context.moveScope(context.getBreakScope());
		}
		
		context.popScope();
		return NullValue.NULL;
	}
}
