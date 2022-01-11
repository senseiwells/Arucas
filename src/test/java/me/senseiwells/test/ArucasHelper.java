package me.senseiwells.test;

import me.senseiwells.arucas.api.ContextBuilder;
import me.senseiwells.arucas.core.Lexer;
import me.senseiwells.arucas.core.Parser;
import me.senseiwells.arucas.nodes.Node;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

import java.util.List;

public class ArucasHelper {
	private static class NodeContext {
		private final Node node;
		private final Context context;

		public NodeContext(Node node, Context context) {
			this.node = node;
			this.context = context;
		}
	}
	
	public static NodeContext compile(String syntax) throws CodeError {
		Context context = new ContextBuilder()
			.setDisplayName("root")
			.setOutputHandler(System.out::print)
			.addDefault()
			.build();
		
		List<Token> tokens = new Lexer(syntax, "").createTokens();
		return new NodeContext(new Parser(tokens, context).parse(), context);
	}
	
	public static String runUnsafe(String syntax) throws CodeError, ThrowValue {
		NodeContext nodeContext = compile("_run_value=(fun(){%s})();".formatted(syntax));
		nodeContext.node.visit(nodeContext.context);
		Value<?> value = nodeContext.context.getStackTable().get("_run_value");
		return value == null ? null : value.getAsString(nodeContext.context);
	}
	
	public static String runSafe(String syntax) {
		try {
			return runUnsafe(syntax);
		}
		catch (CodeError | ThrowValue e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String runUnsafeFull(String syntax, String resultVariable) throws CodeError, ThrowValue {
		NodeContext nodeContext = compile(syntax);
		nodeContext.node.visit(nodeContext.context);
		Value<?> value = nodeContext.context.getStackTable().get(resultVariable);
		return value == null ? null : value.getAsString(nodeContext.context);
	}
	
	public static String runSafeFull(String syntax, String resultVariable) {
		try {
			return runUnsafeFull(syntax, resultVariable);
		}
		catch (CodeError | ThrowValue e) {
			e.printStackTrace();
			return null;
		}
	}
}
