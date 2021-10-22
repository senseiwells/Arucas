package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.functions.UserDefinedFunction;
import me.senseiwells.arucas.values.Value;

import java.util.List;

public class FunctionNode extends Node {
	public final Token variableNameToken;
	public final List<String> argumentNames;
	public final Node bodyNode;
	public final UserDefinedFunction functionValue;

	public FunctionNode(Token varNameToken, List<String> argumentNames, Node bodyNode) {
		super(bodyNode.token, varNameToken.startPos, bodyNode.endPos);
		this.variableNameToken = varNameToken;
		this.argumentNames = argumentNames;
		this.bodyNode = bodyNode;
		this.functionValue = new UserDefinedFunction(varNameToken.content,
			this.bodyNode,
			this.argumentNames
		);
		this.functionValue.setPos(this.startPos, this.endPos);
	}

	@Override
	public Value<?> visit(Context context) throws CodeError {
		String functionName = this.variableNameToken.content;
		if (context.isBuiltInFunction(functionName))
			throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "Cannot define %s() function as it is a predefined function".formatted(functionName), this.startPos, this.endPos);
		
		context.setVariable(functionName, this.functionValue);
		return this.functionValue;
	}
}
