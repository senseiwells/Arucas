package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.Value;

import java.util.List;

public class UnpackAssignNode extends VariableAssignNode {
	private final List<VariableAssignNode> assignableNodes;

	public UnpackAssignNode(Token token, List<VariableAssignNode> assignableNodes, Node valueNode) {
		super(token, valueNode);
		this.assignableNodes = assignableNodes;
	}

	@Override
	public Value visit(Context context) throws CodeError, ThrowValue {
		Value unpackable = this.getNewValue(context);

		if (!(unpackable.getValue() instanceof ArucasList list)) {
			throw new RuntimeError("You can only unpack lists", this.syntaxPosition, context);
		}
		if (list.size() != this.assignableNodes.size()) {
			throw new RuntimeError("List size does not match number of variables", this.syntaxPosition, context);
		}

		for (int i = 0; i < this.assignableNodes.size(); i++) {
			VariableAssignNode assignNode = this.assignableNodes.get(i);
			Value newValue = list.get(i);
			assignNode.setValue(newValue);
			assignNode.visit(context);
		}

		return unpackable;
	}
}
