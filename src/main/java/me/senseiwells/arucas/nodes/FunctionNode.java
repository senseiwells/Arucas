package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.UserDefinedFunction;
import me.senseiwells.arucas.values.Value;

import java.util.List;

public class FunctionNode extends Node {
	public final Token variableNameToken;
	public final List<Token> argumentNameToken;
	public final Node bodyNode;
	public final UserDefinedFunction functionValue;

	public FunctionNode(Token varNameToken, List<Token> argumentNameToken, Node bodyNode) {
		super(bodyNode.token, varNameToken.startPos, bodyNode.endPos);
		this.variableNameToken = varNameToken;
		this.argumentNameToken = argumentNameToken;
		this.bodyNode = bodyNode;
		this.functionValue = new UserDefinedFunction(varNameToken.content,
			this.bodyNode,
			this.argumentNameToken.stream().map(t -> t.content).toList()
		);
		this.functionValue.setPos(this.startPos, this.endPos);
	}

	@Override
	public Value<?> visit(Context context) throws CodeError {
		String functionName = this.variableNameToken.content;
		if (context.isBuiltInFunction(functionName))
			throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "Cannot define " + functionName + "() function as it is a predefined function", this.startPos, this.endPos);
		
		context.setVariable(functionName, this.functionValue);
		return this.functionValue;
	}
}
