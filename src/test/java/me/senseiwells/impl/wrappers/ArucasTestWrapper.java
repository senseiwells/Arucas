package me.senseiwells.impl.wrappers;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.wrappers.*;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.classes.WrapperClassDefinition;
import me.senseiwells.arucas.values.classes.WrapperClassValue;

import java.util.List;

@ArucasClass(name = "Test")
public class ArucasTestWrapper implements IArucasWrappedClass {

	@ArucasDefinition
	public static WrapperClassDefinition DEFINITION;

	@ArucasMember(assignable = false)
	public static final Value memberStaticValue = StringValue.of("Static MEMBER!");

	@ArucasMember
	public Value memberValue = StringValue.of("Normal MEMBER!");

	@ArucasFunction
	public Value getName(Context context, ListValue list, MapValue map, StringValue string) throws CodeError {
		return DEFINITION.createNewDefinition(context, List.of(), ISyntax.EMPTY);
	}

	@ArucasConstructor
	public void constructor(Context context) {
		this.memberValue = NumberValue.of(10);
	}

	@ArucasConstructor
	public void constructor(Context context, NumberValue numberValue) {

	}

	@ArucasOperator(Token.Type.MINUS)
	public Value minusU(Context context) {
		return NumberValue.of(-10);
	}

	@ArucasOperator(Token.Type.MINUS)
	public Value minusBin(Context context, NumberValue numberValue) {
		return NumberValue.of(10 - numberValue.value);
	}

	@ArucasOperator(Token.Type.NOT)
	public Value not(Context context) {
		return BooleanValue.TRUE;
	}

	@ArucasFunction
	public ArucasTestWrapper awesomeTestMethod(Context context, ListValue list) {
		System.out.println(this.memberValue);
		System.out.println("I got called OMG :D 1");
		System.out.printf("0: %s\n", this);
		System.out.printf("1: %s\n", list);
		return this;
	}

	@ArucasFunction
	public void cool(Context context, WrapperClassValue classValue) {
		ArucasTestWrapper instance = classValue.getWrapper(ArucasTestWrapper.class);
		System.out.println(instance.memberValue);
	}

	@ArucasFunction
	public Value testing(Context context, ListValue list, MapValue map, StringValue string) {
		System.out.printf("0: %s\n", this);
		System.out.printf("1: %s\n", list);
		System.out.printf("2: %s\n", map);
		System.out.printf("3: %s\n", string);
		return StringValue.of("This is awesome 2222222");
	}

	@ArucasFunction
	public static Value benchmark(Context context, ListValue list, MapValue map, StringValue string) {
		System.out.printf("0: %s\n", list);
		System.out.printf("1: %s\n", map);
		System.out.printf("2: %s\n", string);
		return NullValue.NULL;
	}

	@ArucasFunction
	public Value toList(Context context) {
		return NullValue.NULL;
	}

	@ArucasFunction
	public static Value staticMethod(Context context) {
		return NullValue.NULL;
	}

	@ArucasFunction
	public Value toString(Context context) {
		return StringValue.of("Testing string");
	}
}
