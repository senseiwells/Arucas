package me.senseiwells;

import me.senseiwells.arucas.api.ContextBuilder;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.classes.ArucasWrapperExtension;
import me.senseiwells.impl.wrappers.ArucasTestWrapper;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class Main {
	public static void main(String[] args) throws InterruptedException, IOException {
		Context context = new ContextBuilder()
			.setDisplayName("System.in")
			.addDefault()
			.generateArucasFiles()
			.addWrappers("test", ArucasTestWrapper::new)
			.build();
		
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
