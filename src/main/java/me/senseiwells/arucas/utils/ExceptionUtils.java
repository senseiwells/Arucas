package me.senseiwells.arucas.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {
	public static String getStackTrace(Throwable throwable) {
		final StringWriter stringWriter = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(stringWriter, true);
		throwable.printStackTrace(printWriter);
		return stringWriter.getBuffer().toString();
	}

	public static boolean runSafe(ThrowableRunnable runnable) {
		try {
			runnable.run();
			return true;
		}
		catch (Throwable throwable) {
			return false;
		}
	}

	@FunctionalInterface
	public interface ThrowableRunnable {
		void run() throws Throwable;
	}
}
