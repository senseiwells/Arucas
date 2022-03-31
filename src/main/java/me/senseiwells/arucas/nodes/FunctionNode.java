package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.MutableSyntaxImpl;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.UserDefinedFunction;
import me.senseiwells.arucas.values.Value;

import java.util.List;

public class FunctionNode extends Node {
	private final Token variableNameToken;
	private final UserDefinedFunction functionValue;

	public FunctionNode(Token functionToken, Token varNameToken, List<String> argumentNames) {
		this(varNameToken, new UserDefinedFunction(varNameToken.content, argumentNames, functionToken.syntaxPosition));
	}

	public FunctionNode(Token varNameToken, UserDefinedFunction functionValue) {
		super(varNameToken, new MutableSyntaxImpl(varNameToken.syntaxPosition.getStartPos(), null));
		this.variableNameToken = varNameToken;
		this.functionValue = functionValue;
	}
	
	public void complete(Node bodyNode) {
		// Because recursive calls need access to the node before
		// it's complete we need to initialize some values later
		((MutableSyntaxImpl) this.syntaxPosition).end = bodyNode.syntaxPosition.getEndPos();
		this.functionValue.complete(bodyNode);
	}
	
	public UserDefinedFunction getFunctionValue() {
		return this.functionValue;
	}

	@Override
	public Value<?> visit(Context context) throws CodeError {
		String functionName = this.variableNameToken.content;
		context.throwIfStackNameTaken(null, this.syntaxPosition);

		this.functionValue.setLocalContext(context.createBranch());
		context.setVariable(functionName, this.functionValue);
		return this.functionValue;
	}
}
