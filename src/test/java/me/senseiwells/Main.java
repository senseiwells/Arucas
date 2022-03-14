package me.senseiwells;

import me.senseiwells.arucas.api.ContextBuilder;
import me.senseiwells.arucas.extensions.discordapi.DiscordBotWrapper;
import me.senseiwells.arucas.extensions.discordapi.DiscordEventWrapper;
import me.senseiwells.arucas.utils.Context;
import org.w3c.dom.DOMConfiguration;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		Context context = new ContextBuilder()
			.setDisplayName("System.in")
			.addDefault()
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
