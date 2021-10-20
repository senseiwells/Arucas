package me.senseiwells.test;

import me.senseiwells.arucas.core.Lexer;
import me.senseiwells.arucas.core.Parser;
import me.senseiwells.arucas.nodes.Node;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.SymbolTable;

import java.util.List;
import java.util.Objects;

public class ArucasHelper {
	public static Node compile(String syntax) throws CodeError {
		Context context = new Context("root", null, null);
		context.symbolTable = new SymbolTable().setDefaultSymbols(context);
		
		List<Token> tokens = new Lexer(syntax, "").createTokens();
		return new Parser(tokens, context).parse();
	}
	
	public static String runUnsafe(String syntax) throws CodeError, ThrowValue {
		Node node = compile("_run_value=(fun(){%s})();".formatted(syntax));
		node.visit();
		return node.context.symbolTable.get("_run_value").toString();
	}
	
	/**
	 * Run code without generating any exception message
	 */
	public static String runSafe(String syntax) {
		try {
			return runUnsafe(syntax);
		}
		catch(CodeError | ThrowValue e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String runUnsafeFull(String syntax, String resultVariable) throws CodeError, ThrowValue {
		Node node = compile(syntax);
		node.visit();
		return Objects.toString(node.context.symbolTable.get(resultVariable));
	}
	
	public static String runSafeFull(String syntax, String resultVariable) {
		try {
			return runUnsafeFull(syntax, resultVariable);
		}
		catch(CodeError | ThrowValue e) {
			e.printStackTrace();
			return null;
		}
	}
}
