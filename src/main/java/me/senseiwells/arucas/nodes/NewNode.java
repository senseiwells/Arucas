package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

import java.util.ArrayList;
import java.util.List;

public class NewNode extends Node {
	private final List<Node> arguments;
	private final Token className;
	
	public NewNode(Token className, List<Node> arguments, ISyntax startPos, ISyntax endPos) {
		super(new Token(Token.Type.NEW, startPos, endPos));
		this.arguments = arguments;
		this.className = className;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError, ThrowValue {
		// Throws an error if the thread has been interrupted
		this.keepRunning();

		AbstractClassDefinition definition = context.getClassDefinition(this.className.content);
		if (definition == null) {
			throw new RuntimeError("The class '%s' does not exist".formatted(this.className.content), this.syntaxPosition, context);
		}
		
		List<Value<?>> parameters = new ArrayList<>();
		for (Node node : this.arguments) {
			parameters.add(node.visit(context));
		}
		
		return definition.createNewDefinition(context, parameters, this.syntaxPosition);
	}
}
