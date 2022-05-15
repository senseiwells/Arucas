package me.senseiwells;

import me.senseiwells.arucas.api.ContextBuilder;
import me.senseiwells.arucas.utils.ArgumentParser;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.impl.wrappers.ArucasTestWrapper;
import me.senseiwells.impl.wrappers.ChildWrapper;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Main {
	@Deprecated
	public static void main(String[] args) throws InterruptedException, IOException, ExecutionException {
		Context context = new ContextBuilder()
			.setDisplayName("System.in")
			.addDefault()
			.addWrappers(
				"test",
				ChildWrapper::new,
				ArucasTestWrapper::new
			)
			.generateArucasFiles()
			.build();

		ArgumentParser parser = new ArgumentParser()
			.addArgument("-noformat", c -> c.getOutput().setFormatting("", "", ""))
			.addArgument("-debug", c -> c.setDebug(true));
		parser.parse(context, args);

		context.getOutput().println("Welcome to Arucas Interpreter");
		Scanner scanner = new Scanner(System.in);
		boolean running = true;
		while (running) {
			System.out.print("\n>> ");

			String line = scanner.nextLine();
			switch (line.trim()) {
				case "" -> {
					continue;
				}
				case "quit", "exit" -> {
					running = false;
					continue;
				}
			}

			context.getThreadHandler().runOnMainThreadFuture(context, "System.in", line).get();
		}
	}
}
