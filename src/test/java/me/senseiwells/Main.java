package me.senseiwells;

import me.senseiwells.arucas.api.ContextBuilder;
import me.senseiwells.arucas.extensions.ArucasDebugClass;
import me.senseiwells.arucas.extensions.wrappers.ArucasTestWrapper;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.ArucasValueMapCustom;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.MapValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.StringValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Context context = new ContextBuilder()
			.setDisplayName("System.in")
			.addDefault()
			.addClasses(ArucasDebugClass::new)
			.addWrapper(ArucasTestWrapper::new)
			.build();
		
		context.getThreadHandler()
			.setStopErrorHandler(System.out::println)
			.setErrorHandler(System.out::println)
			.setFatalErrorHandler((c, t, s) -> t.printStackTrace());
		
		try {
			ArucasValueMapCustom map = new ArucasValueMapCustom();
			System.out.println(map.toString(context));
			
			map.put(context, StringValue.of("A"), NumberValue.of(123));
			System.out.println(map.toString(context));
			
			map.put(context, StringValue.of("A"), NumberValue.of(12342));
			System.out.println(map.toString(context));
			
			map.put(context, StringValue.of("B"), NumberValue.of(232));
			System.out.println(map.toString(context));
			
			map.put(context, NumberValue.of(232), StringValue.of("B"));
			System.out.println(map.toString(context));
		}
		catch(CodeError e) {
			e.printStackTrace();
		}
		
		while (true) {
			Scanner scanner = new Scanner(System.in);
			String line = scanner.nextLine();
			if (line.trim().equals("")) {
				continue;
			}
			context.getThreadHandler().runOnThread(context, "System.in", line);
		}
	}
}
