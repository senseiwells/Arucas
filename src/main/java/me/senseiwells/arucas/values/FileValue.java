package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
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

import static me.senseiwells.arucas.utils.ValueTypes.*;

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
		return FILE;
	}

	@ClassDoc(
		name = FILE,
		desc = "This class allows you to manipulate files."
	)
	public static class ArucasFileClass extends ArucasClassExtension {
		public ArucasFileClass() {
			super(FILE);
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

		@ConstructorDoc(
			desc = "This creates a new File object with set path",
			params = {STRING, "path", "the path of the file"},
			example = "new File('foo/bar/script.arucas')"
		)
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

		@FunctionDoc(
			isStatic = true,
			name = "getDirectory",
			desc = "This returns the file of the working directory",
			returns = {FILE, "the file of the working directory"},
			example = "File.getDirectory()"
		)
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

		@FunctionDoc(
			name = "getName",
			desc = "This returns the name of the file",
			returns = {STRING, "the name of the file"},
			example = "File.getName()"
		)
		private Value<?> getName(Context context, MemberFunction function) throws CodeError {
			FileValue thisValue = function.getThis(context, FileValue.class);
			return StringValue.of(thisValue.value.getName());
		}

		@FunctionDoc(
			name = "read",
			desc = "This reads the file and returns the contents as a string",
			returns = {STRING, "the contents of the file"},
			throwMsgs = {
				"There was an error reading the file: ...",
				"Out of Memory - The file you are trying to read is too large"
			},
			example = "file.read()"
		)
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

		@FunctionDoc(
			name = "write",
			desc = "This writes a string to a file",
			params = {STRING, "string", "the string to write to the file"},
			throwMsgs = "There was an error writing the file: ...",
			example = "file.write('Hello World!')"
		)
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

		@FunctionDoc(
			name = "getSubFiles",
			desc = "This returns a list of all the sub files in the directory",
			returns = {LIST, "a list of all the sub files in the directory"},
			throwMsgs = "Could not find any files",
			example = "file.getSubFiles()"
		)
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

		@FunctionDoc(
			name = "delete",
			desc = "This deletes the file",
			returns = {BOOLEAN, "true if the file was deleted"},
			throwMsgs = "Could not delete file: ...",
			example = "file.delete()"
		)
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

		@FunctionDoc(
			name = "exists",
			desc = "This returns if the file exists",
			returns = {BOOLEAN, "true if the file exists"},
			throwMsgs = "Could not check file: ...",
			example = "file.exists()"
		)
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

		@FunctionDoc(
			name = "createDirectory",
			desc = "This creates all parent directories of the file if they don't already exist",
			returns = {BOOLEAN, "true if the directories were created"},
			throwMsgs = "...",
			example = "file.createDirectory()"
		)
		private Value<?> createDirectory(Context context, MemberFunction function) throws CodeError {
			FileValue thisValue = function.getThis(context, FileValue.class);
			try {
				return BooleanValue.of(thisValue.value.mkdirs());
			}
			catch (InvalidPathException e) {
				throw new RuntimeError(e.getMessage(), function.syntaxPosition, context);
			}
		}

		@FunctionDoc(
			name = "getPath",
			desc = "This returns the path of the file",
			returns = {STRING, "the path of the file"},
			example = "file.getPath()"
		)
		private Value<?> getPath(Context context, MemberFunction function) throws CodeError {
			FileValue thisValue = function.getThis(context, FileValue.class);
			return StringValue.of(thisValue.value.getPath());
		}

		@FunctionDoc(
			name = "getAbsolutePath",
			desc = "This returns the absolute path of the file",
			returns = {STRING, "the absolute path of the file"},
			example = "file.getAbsolutePath()"
		)
		private Value<?> getAbsolutePath(Context context, MemberFunction function) throws CodeError {
			FileValue thisValue = function.getThis(context, FileValue.class);
			try {
				return StringValue.of(thisValue.value.getAbsolutePath());
			}
			catch (SecurityException e) {
				throw new RuntimeError(e.getMessage(), function.syntaxPosition, context);
			}
		}

		@FunctionDoc(
			name = "open",
			desc = "This opens the file (as in opens it on your os)",
			example = "file.open()"
		)
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
