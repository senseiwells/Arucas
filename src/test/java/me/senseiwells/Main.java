package me.senseiwells;

import me.senseiwells.arucas.api.ContextBuilder;
import me.senseiwells.arucas.extensions.wrappers.ArucasTestWrapper;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Context context = new ContextBuilder()
			.setDisplayName("System.in")
			.addDefault()
			.addWrapper(ArucasTestWrapper::new)
			.build();
		
		context.getThreadHandler()
			.setStopErrorHandler(System.out::println)
			.setErrorHandler(System.out::println)
			.setFatalErrorHandler((c, t, s) -> t.printStackTrace());
	
		for(int i = 0; i < 1000; i++) {
			System.out.print(", new MemberFunction(\"isNaN%d\", this::isNan)".formatted(i));
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
