package me.senseiwells.test;

import me.senseiwells.arucas.api.ContextBuilder;
import me.senseiwells.arucas.core.Lexer;
import me.senseiwells.arucas.core.Parser;
import me.senseiwells.arucas.extensions.ArucasMathClass;
import me.senseiwells.arucas.extensions.ArucasNetworkClass;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.util.List;

public class ArucasHelper {
	public static Context createContext() {
		ContextBuilder builder = new ContextBuilder()
			.setDisplayName("root")
			.addDefault();
		return createContext(builder.build());
	}

	public static Context createContextNoBuiltIns() {
		ContextBuilder builder = new ContextBuilder()
			.setDisplayName("root")
			.addClasses(
				"BuiltIn",
				Value.ArucasBaseClass::new,
				TypeValue.ArucasTypeClass::new,
				EnumValue.ArucasEnumClass::new,
				FunctionValue.ArucasFunctionClass::new,
				StringValue.ArucasStringClass::new,
				BooleanValue.ArucasBooleanClass::new,
				ErrorValue.ArucasErrorClass::new,
				ListValue.ArucasListClass::new,
				SetValue.ArucasSetClass::new,
				MapValue.ArucasMapClass::new,
				NullValue.ArucasNullClass::new,
				NumberValue.ArucasNumberClass::new,
				ThreadValue.ArucasThreadClass::new,
				FileValue.ArucasFileClass::new,
				ArucasMathClass::new,
				ArucasNetworkClass::new
			);
		return createContext(builder.build());
	}

	public static Context createContext(Context parent) {
		return parent.createChildContext("Test Context");
	}

	public static void compile(String syntax) throws CodeError {
		Context context = createContext();
		List<Token> tokens = new Lexer(syntax, "").createTokens();
		new Parser(tokens, context).parse();
	}
	
	public static String runUnsafe(String syntax) throws CodeError {
		Context context = createContext();
		context.getThreadHandler().runOnThreadReturnable(context, "", "_run_value=(fun(){%s})();".formatted(syntax));
		Value<?> value = context.getStackTable().get("_run_value");
		return value == null ? null : value.getAsString(context);
	}
	
	public static String runSafe(String syntax) {
		try {
			return runUnsafe(syntax);
		}
		catch (CodeError e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String runUnsafeFull(String syntax, String resultVariable) throws CodeError {
		Context context = createContext();
		context.getThreadHandler().runOnThreadReturnable(context, "", syntax);
		Value<?> value = context.getStackTable().get(resultVariable);
		return value == null ? null : value.getAsString(context);
	}
	
	public static String runSafeFull(String syntax, String resultVariable) {
		try {
			return runUnsafeFull(syntax, resultVariable);
		}
		catch (CodeError e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String runUnsafeFull(String syntax, String resultVariable, Context context) throws CodeError {
		context = createContext(context);
		context.getThreadHandler().runOnThreadReturnable(context, "", syntax);
		Value<?> value = context.getStackTable().get(resultVariable);
		return value == null ? null : value.getAsString(context);
	}

	public static String runSafeFull(String syntax, String resultVariable, Context context) {
		try {
			return runUnsafeFull(syntax, resultVariable, context);
		}
		catch (CodeError e) {
			e.printStackTrace();
			return null;
		}
	}
}
