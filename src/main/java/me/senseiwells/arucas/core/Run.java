package me.senseiwells.arucas.core;

import me.senseiwells.arucas.api.IArucasOutput;
import me.senseiwells.arucas.nodes.Node;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.ArucasClassDefinitionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

import java.util.List;

public class Run {
	public static Value<?> run(Context context, String fileName, String fileContent) throws CodeError {
		context = context.createChildContext(fileName);
		Node nodeResult = compile(context, fileName, fileContent);
		long startTime = System.nanoTime();
		Value<?> returnValue = null;
		try {
			context.pushRunScope();
			returnValue = nodeResult.visit(context);
			return NullValue.NULL;
		}
		catch (ThrowValue.Return tv) {
			return returnValue = tv.getReturnValue();
		}
		catch (ThrowValue tv) {
			throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, tv.getMessage(), nodeResult.syntaxPosition);
		}
		finally {
			if (context.isDebug()) {
				IArucasOutput output = context.getOutput();
				if (returnValue != null) {
					output.log("Return values: " + returnValue.getAsString(context) + "\n");
				}
				output.log("Execution time: " + (System.nanoTime() - startTime) / 1000 + " microseconds for '" + fileName + "'\n");
			}
		}
	}

	public static Node compile(Context context, String fileName, String fileContent) throws CodeError {
		List<Token> values = new Lexer(fileContent, fileName).createTokens();
		return new Parser(values, context).parse();
	}

	public static ArucasClassDefinitionMap importClasses(Context context, String fileName, String fileContent) throws CodeError {
		Node nodeResult = compile(context, fileName, fileContent);
		try {
			context.pushRunScope();
			nodeResult.visit(context);
			return context.getAllClassDefinitions();
		}
		catch (ThrowValue tv) {
			throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, tv.getMessage(), nodeResult.syntaxPosition);
		}
	}
}
