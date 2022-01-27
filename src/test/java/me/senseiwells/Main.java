package me.senseiwells;

import me.senseiwells.arucas.api.ContextBuilder;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.impl.wrappers.ArucasTestWrapper;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		Context context = new ContextBuilder()
			.setDisplayName("System.in")
			.addDefault()
			.addWrapper(ArucasTestWrapper::new)
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
			
			CountDownLatch latch = new CountDownLatch(1);
			context.getThreadHandler().runOnThread(context, "System.in", line, latch);
			latch.await();
		}
	}
}
