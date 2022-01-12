package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ExceptionUtils;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.ConstructorFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;

public class FileValue extends Value<File> {
	private FileValue(File value) {
		super(value);
	}

	public static FileValue of(File value) {
		return new FileValue(value);
	}

	@Override
	public Value<File> copy(Context context) {
		return this;
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		return "<File - " + this.value.toString() + ">";
	}

	@Override
	public int getHashCode(Context context) throws CodeError {
		return this.value.hashCode();
	}

	@Override
	public boolean isEquals(Context context, Value<?> other) throws CodeError {
		return (other instanceof FileValue that) && this.value.equals(that.value);
	}

	public static class ArucasFileClass extends ArucasClassExtension {
		public ArucasFileClass() {
			super("File");
		}

		@Override
		public ArucasFunctionMap<ConstructorFunction> getDefinedConstructors() {
			return ArucasFunctionMap.of(
				new ConstructorFunction("path", this::newFile)
			);
		}

		private FileValue newFile(Context context, BuiltInFunction function) throws CodeError {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
			return FileValue.of(new File(stringValue.value));
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
					new BuiltInFunction("getDirectory", this::getDirectory)
			);
		}

		private Value<?> getDirectory(Context context, BuiltInFunction function) {
			String filePath = System.getProperty("user.dir");
			return FileValue.of(new File(filePath));
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("read", this::readFile),
				new MemberFunction("write", "string", this::writeFile),
				new MemberFunction("delete", this::deleteFile),
				new MemberFunction("exists", this::exists),
				new MemberFunction("getSubFiles", this::getSubFiles),
				new MemberFunction("createDirectory", this::createDirectory),
				new MemberFunction("getPath", this::getPath),
				new MemberFunction("getAbsolutePath", this::getAbsolutePath),
				new MemberFunction("open", this::open)
			);
		}

		private Value<?> readFile(Context context, MemberFunction function) throws CodeError {
			FileValue thisValue = function.getThis(context, FileValue.class);
			try {
				return StringValue.of(Files.readString(thisValue.value.toPath()));
			}
			catch (OutOfMemoryError e) {
				throw new RuntimeError("Out of Memory - The file you are trying to read is too large", function.syntaxPosition, context);
			}
			catch (InvalidPathException e) {
				throw new RuntimeError(e.getMessage(), function.syntaxPosition, context);
			}
			catch (IOException | SecurityException e) {
				throw new RuntimeError(
					"There was an error reading the file: \"%s\"\n%s".formatted(thisValue.getAsString(context), ExceptionUtils.getStackTrace(e)),
					function.syntaxPosition,
					context
				);
			}
		}

		private Value<?> writeFile(Context context, MemberFunction function) throws CodeError {
			FileValue thisValue = function.getThis(context, FileValue.class);
			StringValue writeValue = function.getParameterValueOfType(context, StringValue.class, 1);

			try (PrintWriter printWriter = new PrintWriter(thisValue.value)) {
				printWriter.println(writeValue.value);
				return NullValue.NULL;
			}
			catch (FileNotFoundException | SecurityException e) {
				throw new RuntimeError(
					"There was an error writing the file: \"%s\"\n%s".formatted(thisValue.getAsString(context), ExceptionUtils.getStackTrace(e)),
					function.syntaxPosition,
					context
				);
			}
		}

		private Value<?> getSubFiles(Context context, MemberFunction function) throws CodeError {
			FileValue thisValue = function.getThis(context, FileValue.class);
			try {
				File[] files = thisValue.value.listFiles();
				if (files == null) {
					throw new RuntimeError("Could not find any files", function.syntaxPosition, context);
				}
				ArucasList fileList = new ArucasList();
				for (File file : files) {
					fileList.add(StringValue.of(file.getName()));
				}
				return new ListValue(fileList);
			}
			catch (SecurityException e) {
				throw new RuntimeError(e.getMessage(), function.syntaxPosition, context);
			}
		}

		private Value<?> deleteFile(Context context, MemberFunction function) throws CodeError {
			FileValue thisValue = function.getThis(context, FileValue.class);
			try {
				return BooleanValue.of(thisValue.value.delete());
			}
			catch (SecurityException exception) {
				throw new RuntimeError(
					"Could not delete file: %s".formatted(thisValue.getAsString(context)),
					function.syntaxPosition,
					context
				);
			}
		}

		private Value<?> exists(Context context, MemberFunction function) throws CodeError {
			FileValue thisValue = function.getThis(context, FileValue.class);
			try {
				return BooleanValue.of(thisValue.value.exists());
			}
			catch (SecurityException exception) {
				throw new RuntimeError(
					"Could not check file: %s".formatted(thisValue.getAsString(context)),
					function.syntaxPosition,
					context
				);
			}
		}

		private Value<?> createDirectory(Context context, MemberFunction function) throws CodeError {
			FileValue thisValue = function.getThis(context, FileValue.class);
			try {
				return BooleanValue.of(thisValue.value.mkdirs());
			}
			catch (InvalidPathException e) {
				throw new RuntimeError(e.getMessage(), function.syntaxPosition, context);
			}
		}

		private Value<?> getPath(Context context, MemberFunction function) throws CodeError {
			FileValue thisValue = function.getThis(context, FileValue.class);
			return StringValue.of(thisValue.value.getPath());
		}

		private Value<?> getAbsolutePath(Context context, MemberFunction function) throws CodeError {
			FileValue thisValue = function.getThis(context, FileValue.class);
			try {
				return StringValue.of(thisValue.value.getAbsolutePath());
			}
			catch (SecurityException e) {
				throw new RuntimeError(e.getMessage(), function.syntaxPosition, context);
			}
		}

		private Value<?> open(Context context, MemberFunction function) throws CodeError {
			FileValue thisValue = function.getThis(context, FileValue.class);
			try {
				Desktop.getDesktop().open(thisValue.value);
			}
			catch (Exception e) {
				throw new RuntimeError(
					"An error occured while opening the file: %s".formatted(thisValue.getAsString(context)),
					function.syntaxPosition,
					context
				);
			}
			return NullValue.NULL;
		}

		@Override
		public Class<?> getValueClass() {
			return FileValue.class;
		}
	}
}
