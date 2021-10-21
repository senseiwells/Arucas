package me.senseiwells;

import me.senseiwells.arucas.api.ContextBuilder;
import me.senseiwells.arucas.core.Run;
import me.senseiwells.arucas.extensions.ArucasBuiltInExtension;
import me.senseiwells.arucas.extensions.ArucasListExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowStop;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

import java.util.Scanner;
import java.util.Set;

public class Main {
	public static void main(String[] args) {
		Context context = new ContextBuilder()
			.setDisplayName("System.in")
			.setExtensions(Set.of(ArucasBuiltInExtension.class, ArucasListExtension.class))
			.create();
		
		while (true) {
			Scanner scanner = new Scanner(System.in);
			String line = scanner.nextLine();
			if (line.trim().equals(""))
				continue;
			
			Value<?> values;
			try {
				values = Run.run(context, "System.in", line);
				if (context.isDebug())
					System.out.println(values);
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
