package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ExceptionUtils;
import me.senseiwells.arucas.utils.ValueTypes;
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

	@Override
	public String getTypeName() {
		return ValueTypes.FILE;
	}

	/**
	 * File class for Arucas. <br>
	 * Fully Documented.
	 * @author senseiwells
	 */
	public static class ArucasFileClass extends ArucasClassExtension {
		public ArucasFileClass() {
			super(ValueTypes.FILE);
		}

		@Override
		public Class<FileValue> getValueClass() {
			return FileValue.class;
		}

		@Override
		public ArucasFunctionMap<ConstructorFunction> getDefinedConstructors() {
			return ArucasFunctionMap.of(
				new ConstructorFunction("path", this::newFile)
			);
		}

		/**
		 * Name: <code>new File(path)</code> <br>
		 * Description: This creates a new File object with set path <br>
		 * Parameter - String: the path of the file <br>
		 * Returns - File: the new File object <br>
		 * Example: <code>new File("foo/bar/script.arucas");</code>
		 */
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

		/**
		 * Name: <code>File.getDirectory()</code> <br>
		 * Description: This returns the file of the working directory <br>
		 * Returns - File: the file of the working directory <br>
		 * Example: <code>File.getDirectory();</code>
		 */
		private Value<?> getDirectory(Context context, BuiltInFunction function) {
			String filePath = System.getProperty("user.dir");
			return FileValue.of(new File(filePath));
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				new MemberFunction("getName", this::getName),
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

		/**
		 * Name: <code>&lt;File>.getName()</code> <br>
		 * Description: This returns the name of the file <br>
		 * Example: <code>File.getName();</code>
		 */
		private Value<?> getName(Context context, MemberFunction function) throws CodeError {
			FileValue thisValue = function.getThis(context, FileValue.class);
			return StringValue.of(thisValue.value.getName());
		}

		/**
		 * Name: <code>&lt;File>.read()</code> <br>
		 * Description: This reads the file and returns the contents as a string <br>
		 * Returns - String: the contents of the file <br>
		 * Throws - Error: <code>"There was an error reading the file: ..."</code> if there was an error reading the file <br>
		 * Example: <code>new File("foo/bar/script.arucas").read();</code>
		 */
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

		/**
		 * Name: <code>&lt;File>.write(string)</code> <br>
		 * Description: This writes a string to a file <br>
		 * Parameter - String: the string to write to the file <br>
		 * Throws - Error: <code>"There was an error writing the file: ..."</code> if there was an error writing the file <br>
		 * Example: <code>new File("foo/bar/script.arucas").write("Hello World!");</code>
		 */
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


		/**
		 * Name: <code>&lt;File>.getSubFiles()</code> <br>
		 * Description: This returns a list of all the sub files in the directory <br>
		 * Returns - List: a list of all the sub files in the directory <br>
		 * Throws - Error: <code>"Could not find any files"</code> if there are no files in the directory <br>
		 * Example: <code>new File("foo/bar/script.arucas").getSubFiles();</code>
		 */
		private Value<?> getSubFiles(Context context, MemberFunction function) throws CodeError {
			FileValue thisValue = function.getThis(context, FileValue.class);
			try {
				File[] files = thisValue.value.listFiles();
				if (files == null) {
					throw new RuntimeError("Could not find any files", function.syntaxPosition, context);
				}
				ArucasList fileList = new ArucasList();
				for (File file : files) {
					fileList.add(FileValue.of(file));
				}
				return new ListValue(fileList);
			}
			catch (SecurityException e) {
				throw new RuntimeError(e.getMessage(), function.syntaxPosition, context);
			}
		}

		/**
		 * Name: <code>&lt;File>.delete()</code> <br>
		 * Description: This deletes the file <br>
		 * Returns - Boolean: true if the file was deleted <br>
		 * Throws - Error: <code>"Could not delete file: ..."</code> if there was an error deleting the file <br>
		 * Example: <code>new File("foo/bar/script.arucas").delete();</code>
		 */
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

		/**
		 * Name: <code>&lt;File>.exists()</code> <br>
		 * Description: This returns if the file exists <br>
		 * Returns - Boolean: true if the file exists <br>
		 * Throws - Error: <code>"Could not check file: ..."</code> if there was an error checking the file <br>
		 * Example: <code>new File("foo/bar/script.arucas").exists();</code>
		 */
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

		/**
		 * Name: <code>&lt;File>.createDirectory()</code> <br>
		 * Description: This creates all parent directories of the file if they don't already exist <br>
		 * Returns - Boolean: true if the directories were created <br>
		 * Throws - Error: <code>"..."</code> if there was an error creating the directories <br>
		 * Example: <code>new File("foo/bar/script.arucas").createDirectory();</code>
		 */
		private Value<?> createDirectory(Context context, MemberFunction function) throws CodeError {
			FileValue thisValue = function.getThis(context, FileValue.class);
			try {
				return BooleanValue.of(thisValue.value.mkdirs());
			}
			catch (InvalidPathException e) {
				throw new RuntimeError(e.getMessage(), function.syntaxPosition, context);
			}
		}

		/**
		 * Name: <code>&lt;File>.getPath()</code> <br>
		 * Description: This returns the path of the file <br>
		 * Returns - String: the path of the file <br>
		 * Example: <code>new File("foo/bar/script.arucas").getPath();</code>
		 */
		private Value<?> getPath(Context context, MemberFunction function) throws CodeError {
			FileValue thisValue = function.getThis(context, FileValue.class);
			return StringValue.of(thisValue.value.getPath());
		}

		/**
		 * Name: <code>&lt;File>.getAbsolutePath()</code> <br>
		 * Description: This returns the absolute path of the file <br>
		 * Returns - String: the absolute path of the file <br>
		 * Example: <code>new File("foo/bar/script.arucas").getAbsolutePath();</code>
		 */
		private Value<?> getAbsolutePath(Context context, MemberFunction function) throws CodeError {
			FileValue thisValue = function.getThis(context, FileValue.class);
			try {
				return StringValue.of(thisValue.value.getAbsolutePath());
			}
			catch (SecurityException e) {
				throw new RuntimeError(e.getMessage(), function.syntaxPosition, context);
			}
		}

		/**
		 * Name: <code>&lt;File>.open()</code> <br>
		 * Description: This opens the file (as in opens it on your os) <br>
		 * Example: <code>new File("foo/bar/script.arucas").open();</code>
		 */
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
	}
}
