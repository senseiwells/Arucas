package me.senseiwells.arucas.core;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.nodes.Node;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

import java.util.List;

public class Run {
	
	public static Value<?> run(Context context, String fileName, String fileContent) throws CodeError {
		List<Token> values = new Lexer(fileContent, fileName).createTokens();
		Node nodeResult = new Parser(values, context).parse();
		try {
			Value<?> value = nodeResult.visit(context);
			if (context.isDebug()) {
				System.out.println(value);
			}
			return new NullValue();
		}
		catch (ThrowValue.Return tv) {
			return tv.returnValue;
		}
		catch (ThrowValue tv) {
			throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "Cannot use keywords 'break' or 'continue' outside loop", nodeResult.startPos, nodeResult.endPos);
		}
	}
}
