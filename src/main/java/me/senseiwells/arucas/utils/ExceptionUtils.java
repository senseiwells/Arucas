package me.senseiwells.arucas.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ExceptionUtils {
	public static String getStackTrace(Throwable throwable) {
		final StringWriter stringWriter = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(stringWriter, true);
		throwable.printStackTrace(printWriter);
		return stringWriter.getBuffer().toString();
	}

	public static boolean runSafe(Functions.ThrowableRunnable runnable) {
		return returnThrowable(runnable) == null;
	}

	public static Throwable returnThrowable(Functions.ThrowableRunnable runnable) {
		try {
			runnable.run();
			return null;
		}
		catch (Throwable throwable) {
			return throwable;
		}
	}

	public static <T> T catchAsNull(Functions.ThrowableSupplier<T> supplier) {
		return catchAsDefault(supplier, () -> null);
	}

	public static <T> T catchAsDefault(Functions.ThrowableSupplier<T> supplier, Supplier<T> defaultSupplier) {
		try {
			return supplier.get();
		}
		catch (Throwable throwable) {
			return defaultSupplier.get();
		}
	}

	public static void catchAndHandle(Functions.ThrowableRunnable runnable, Consumer<Throwable> handler) {
		Throwable throwable = returnThrowable(runnable);
		if (throwable != null) {
			handler.accept(throwable);
		}
	}

	public static void catchAndThrow(Functions.ThrowableRunnable runnable, Function<Throwable, ? extends RuntimeException> handler) {
		Throwable throwable = returnThrowable(runnable);
		if (throwable != null) {
			throw handler.apply(throwable);
		}
	}
}
