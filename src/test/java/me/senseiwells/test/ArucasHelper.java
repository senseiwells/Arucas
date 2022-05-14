package me.senseiwells.test;

import me.senseiwells.arucas.api.ContextBuilder;
import me.senseiwells.arucas.core.Lexer;
import me.senseiwells.arucas.core.Parser;
import me.senseiwells.arucas.extensions.ArucasMathClass;
import me.senseiwells.arucas.extensions.util.ArucasNetworkClass;
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
				"NoBuiltIn",
				GenericValue.ArucasBaseClass::new,
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
		return runUnsafe(syntax, createContext());
	}

	public static String runUnsafe(String syntax, Context context) throws CodeError {
		Value value = context.getThreadHandler().runOnMainThreadAndWait(context, "Test", syntax);
		return value.getAsString(context);
	}

	public static String runSafe(String syntax) {
		return runSafe(syntax, createContext());
	}
	
	public static String runSafe(String syntax, Context context) {
		try {
			return runUnsafe(syntax, context);
		}
		catch (CodeError e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String runUnsafeFull(String syntax, String resultVariable) throws CodeError {
		return runUnsafeFull(syntax, resultVariable, createContext());
	}

	public static String runUnsafeFull(String syntax, String resultVariable, Context context) throws CodeError {
		return runUnsafe(syntax + "return " + resultVariable + ";", context);
	}

	public static String runSafeFull(String syntax, String resultVariable) {
		return runSafeFull(syntax, resultVariable, createContext());
	}

	public static String runSafeFull(String syntax, String resultVariable, Context context) {
		return runSafe(syntax + "return " + resultVariable + ";", context);
	}
}
