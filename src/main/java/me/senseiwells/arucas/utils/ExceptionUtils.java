package me.senseiwells.arucas.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

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

	public static Throwable returnThrowable(ThrowableRunnable runnable) {
		try {
			runnable.run();
			return null;
		}
		catch (Throwable throwable) {
			return throwable;
		}
	}

	public static <T> T catchAsNull(ThrowableSupplier<T> supplier) {
		try {
			return supplier.get();
		}
		catch (Throwable throwable) {
			return null;
		}
	}

	@FunctionalInterface
	public interface ThrowableRunnable {
		void run() throws Throwable;
	}

	@FunctionalInterface
	public interface ThrowableSupplier<T> {
		T get() throws Throwable;
	}
}
