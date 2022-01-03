package me.senseiwells;

import me.senseiwells.arucas.api.ArucasThreadHandler;
import me.senseiwells.arucas.api.ContextBuilder;
import me.senseiwells.arucas.core.Run;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowStop;
import me.senseiwells.arucas.utils.Context;

import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Context context = new ContextBuilder()
			.setDisplayName("System.in")
			.addDefault()
			.build();
		
		context.getThreadHandler()
			.setStopErrorHandler(System.out::println)
			.setErrorHandler(System.out::println)
			.setFatalErrorHandler((c, t, s) -> t.printStackTrace());

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
