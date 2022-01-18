package me.senseiwells.impl.wrappers;

import me.senseiwells.arucas.api.wrappers.*;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;

public class ArucasTestWrapper implements IArucasWrappedClass {
	// TODO: add test cases
	@Override
	public String getName() {
		return "Test";
	}

	@ArucasMember(assignable = false)
	public static final Value<?> memberStaticValue = StringValue.of("Static MEMBER!");
	
	@ArucasMember
	public Value<?> memberValue = StringValue.of("Normal MEMBER!");
	
	@ArucasFunction
	public Value<?> getName(Context context, ListValue list, MapValue map, StringValue string) {
		return null;
	}

	@ArucasConstructor
	public void constructor(Context context) {
		this.memberValue = NumberValue.of(10);
	}

	@ArucasOperator(Token.Type.MINUS)
	public Value<?> minusU(Context context) {
		return NumberValue.of(-10);
	}

	@ArucasOperator(Token.Type.MINUS)
	public Value<?> minusBin(Context context, NumberValue numberValue) {
		return NumberValue.of(10 - numberValue.value);
	}

	@ArucasOperator(Token.Type.NOT)
	public Value<?> not(Context context) {
		return BooleanValue.TRUE;
	}

	@ArucasFunction
	public Value<?> awesomeTestMethod(Context context, ListValue list) {
		System.out.println(memberValue);
		System.out.println("I got called OMG :D 1");
		System.out.printf("0: %s\n", this);
		System.out.printf("1: %s\n", list);
		return StringValue.of("11111111 is awesome");
	}
	
	@ArucasFunction
	public Value<?> testing(Context context, ListValue list, MapValue map, StringValue string) {
		System.out.printf("0: %s\n", this);
		System.out.printf("1: %s\n", list);
		System.out.printf("2: %s\n", map);
		System.out.printf("3: %s\n", string);
		return StringValue.of("This is awesome 2222222");
	}
	
	@ArucasFunction
	public static Value<?> benchmark(Context context, ListValue list, MapValue map, StringValue string) {
		System.out.printf("0: %s\n", list);
		System.out.printf("1: %s\n", map);
		System.out.printf("2: %s\n", string);
		return NullValue.NULL;
	}
	
	@ArucasFunction
	public Value<?> toList(Context context) {
		return NullValue.NULL;
	}
	
	@ArucasFunction
	public static Value<?> staticMethod(Context context) {
		return NullValue.NULL;
	}
	
	@ArucasFunction
	public Value<?> toString(Context context) {
		return StringValue.of("Testing string");
	}
}
