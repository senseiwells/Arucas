package me.senseiwells.arucas.extensions.wrappers;

import me.senseiwells.arucas.api.wrappers.ArucasConstructor;
import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.api.wrappers.ArucasMember;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;

public class ArucasTestWrapper implements IArucasWrappedClass {
	@Override
	public String getName() {
		return "Test";
	}
	
	@ArucasMember
	public static Value<?> memberStaticValue = StringValue.of("Static MEMBER!");
	
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
	
//	@ArucasOperator(Token.Type.PLUS)
//	public Value<?> op_add(Context context, ListValue list, MapValue map, StringValue string) {
//
//		return null;
//	}
	
	@ArucasFunction
	public Value<?> toString(Context context) {
		return StringValue.of("Testing string");
	}
}
