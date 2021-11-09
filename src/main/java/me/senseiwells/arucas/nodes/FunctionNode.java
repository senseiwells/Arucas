package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.MutableSyntaxImpl;
import me.senseiwells.arucas.values.functions.UserDefinedFunction;
import me.senseiwells.arucas.values.Value;

import java.util.List;

public class FunctionNode extends Node {
	private final Token variableNameToken;
	public UserDefinedFunction functionValue;

	public FunctionNode(Token functionToken, Token varNameToken, List<String> argumentNames) {
		super(varNameToken, new MutableSyntaxImpl(varNameToken.syntaxPosition.getStartPos(), null));
		this.variableNameToken = varNameToken;
		this.functionValue = new UserDefinedFunction(varNameToken.content, functionToken.syntaxPosition, argumentNames);
	}
	
	public void complete(Node bodyNode) {
		// Because recursive calls need access to the this node before
		// it's complete we need to initialize some values later.
		((MutableSyntaxImpl) this.syntaxPosition).end = bodyNode.syntaxPosition.getEndPos();
		this.functionValue.complete(bodyNode);
	}

	@Override
	public Value<?> visit(Context context) throws CodeError {
		String functionName = this.variableNameToken.content;
		if (context.isBuiltInFunction(functionName)) {
			throw new CodeError(
				CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
				"Cannot define %s() function as it is a predefined function".formatted(functionName),
				this.syntaxPosition
			);
		}
		
		context.setVariable(functionName, this.functionValue);
		return this.functionValue;
	}
}
