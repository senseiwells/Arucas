package me.senseiwells.arucas.extensions.wrappers;

import me.senseiwells.arucas.api.wrappers.ArucasFunction;
import me.senseiwells.arucas.api.wrappers.ArucasMember;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.classes.ArucasClassValue;

public class ArucasTestWrapper implements IArucasWrappedClass {
	@Override
	public String getName() {
		return "Test";
	}
	
	@ArucasMember
	public Value<?> memberValue = NullValue.NULL;
	
	@ArucasFunction
	public Value<?> getName(Context context, ListValue list, MapValue map, StringValue string) {
		
		return null;
	}
	
	@ArucasFunction
	public Value<?> awesomeTestMethod(Context context, ArucasClassValue value, ListValue list) {
		System.out.println("I got called OMG :D 1");
		System.out.printf("0: %s\n", value);
		System.out.printf("1: %s\n", list);
		return StringValue.of("11111111 is awesome");
	}
	
	@ArucasFunction
	public Value<?> awesomeTestMethod(Context context, ArucasClassValue value, ListValue list, MapValue map, StringValue string) {
		System.out.println("I got called OMG :D 2");
		System.out.printf("0: %s\n", value);
		System.out.printf("1: %s\n", list);
		System.out.printf("2: %s\n", map);
		System.out.printf("3: %s\n", string);
		return StringValue.of("This is awesome 2222222");
	}
	
	@ArucasFunction
	public Value<?> benchmark(Context context, ArucasClassValue value, ListValue list, MapValue map, StringValue string) {
		return NullValue.NULL;
	}
	
	@ArucasFunction
	public Value<?> toList(Context context, ArucasClassValue value) {
		return NullValue.NULL;
	}
	
//	@ArucasOperator(Token.Type.PLUS)
//	public Value<?> op_add(Context context, ListValue list, MapValue map, StringValue string) {
//
//		return null;
//	}
	
	@ArucasFunction
	public Value<?> toString(Context context) {
		return NullValue.NULL;
	}
}
