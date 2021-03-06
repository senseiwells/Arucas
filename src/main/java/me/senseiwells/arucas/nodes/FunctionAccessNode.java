package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.StringValue;

public class FunctionAccessNode extends Node {
	private final StringValue functionName;
	
	public FunctionAccessNode(Token token) {
		super(token);
		this.functionName = StringValue.of(token.content);
	}

	@Override
	public StringValue visit(Context context) throws CodeError {
		// Because we cannot verify the member name in here we will return the function name
		return this.functionName;
	}
}
