package me.senseiwells;

import me.senseiwells.arucas.api.ContextBuilder;
import me.senseiwells.arucas.core.Run;
import me.senseiwells.arucas.extensions.*;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowStop;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Context context = new ContextBuilder()
			.setDisplayName("System.in")
			.setExtensions(
				ArucasBuiltInExtension.class,
				ArucasListMembers.class,
				ArucasNumberMembers.class,
				ArucasStringMembers.class,
				ArucasMapMembers.class
			)
			.create();
		
		while (true) {
			Scanner scanner = new Scanner(System.in);
			String line = scanner.nextLine();
			if (line.trim().equals("")) {
				continue;
			}

			try {
				Run.run(context, "System.in", line);
			}
			catch (ThrowStop e) {
				System.out.println(e.toString(context));
				break;
			}
			catch (CodeError e) {
				System.out.println(e.toString(context));
			}
		}
	}
}
