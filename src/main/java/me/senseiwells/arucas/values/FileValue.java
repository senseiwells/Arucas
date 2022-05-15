package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Arguments;
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

public class FileValue extends GenericValue<File> {
	private FileValue(File value) {
		super(value);
	}

	public static FileValue of(File value) {
		return new FileValue(value);
	}

	@Override
	public GenericValue<File> copy(Context context) {
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
	public boolean isEquals(Context context, Value other) throws CodeError {
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
				ConstructorFunction.of(1, this::newFile)
			);
		}

		@ConstructorDoc(
			desc = "This creates a new File object with set path",
			params = {STRING, "path", "the path of the file"},
			example = "new File('foo/bar/script.arucas')"
		)
		private FileValue newFile(Arguments arguments) throws CodeError {
			StringValue stringValue = arguments.getNext(StringValue.class);
			return FileValue.of(new File(stringValue.value));
		}

		@Override
		public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
			return ArucasFunctionMap.of(
				BuiltInFunction.of("getDirectory", this::getDirectory)
			);
		}

		@FunctionDoc(
			isStatic = true,
			name = "getDirectory",
			desc = "This returns the file of the working directory",
			returns = {FILE, "the file of the working directory"},
			example = "File.getDirectory()"
		)
		private Value getDirectory(Arguments arguments) {
			String filePath = System.getProperty("user.dir");
			return FileValue.of(new File(filePath));
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
				MemberFunction.of("getName", this::getName),
				MemberFunction.of("read", this::readFile),
				MemberFunction.of("write", 1, this::writeFile),
				MemberFunction.of("delete", this::deleteFile),
				MemberFunction.of("exists", this::exists),
				MemberFunction.of("getSubFiles", this::getSubFiles),
				MemberFunction.of("createDirectory", this::createDirectory),
				MemberFunction.of("getPath", this::getPath),
				MemberFunction.of("getAbsolutePath", this::getAbsolutePath),
				MemberFunction.of("open", this::open)
			);
		}

		@FunctionDoc(
			name = "getName",
			desc = "This returns the name of the file",
			returns = {STRING, "the name of the file"},
			example = "File.getName()"
		)
		private Value getName(Arguments arguments) throws CodeError {
			FileValue thisValue = arguments.getNext(FileValue.class);
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
		private Value readFile(Arguments arguments) throws CodeError {
			FileValue thisValue = arguments.getNext(FileValue.class);
			try {
				return StringValue.of(Files.readString(thisValue.value.toPath()));
			}
			catch (OutOfMemoryError e) {
				throw arguments.getError("Out of Memory - The file you are trying to read is too large");
			}
			catch (InvalidPathException e) {
				throw arguments.getError(e.getMessage());
			}
			catch (IOException | SecurityException e) {
				throw arguments.getError(
					"There was an error reading the file '%s'\n%s",
					thisValue.getAsString(arguments.getContext()),
					ExceptionUtils.getStackTrace(e)
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
		private Value writeFile(Arguments arguments) throws CodeError {
			FileValue thisValue = arguments.getNext(FileValue.class);
			StringValue writeValue = arguments.getNext(StringValue.class);

			try (PrintWriter printWriter = new PrintWriter(thisValue.value)) {
				printWriter.println(writeValue.value);
				return NullValue.NULL;
			}
			catch (FileNotFoundException | SecurityException e) {
				throw arguments.getError(
					"There was an error writing the file '%s'\n%s",
					thisValue.getAsString(arguments.getContext()),
					ExceptionUtils.getStackTrace(e)
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
		private Value getSubFiles(Arguments arguments) throws CodeError {
			FileValue thisValue = arguments.getNext(FileValue.class);
			try {
				File[] files = thisValue.value.listFiles();
				if (files == null) {
					throw arguments.getError("Could not find any files");
				}
				ArucasList fileList = new ArucasList();
				for (File file : files) {
					fileList.add(FileValue.of(file));
				}
				return new ListValue(fileList);
			}
			catch (SecurityException e) {
				throw arguments.getError(e.getMessage());
			}
		}

		@FunctionDoc(
			name = "delete",
			desc = "This deletes the file",
			returns = {BOOLEAN, "true if the file was deleted"},
			throwMsgs = "Could not delete file: ...",
			example = "file.delete()"
		)
		private Value deleteFile(Arguments arguments) throws CodeError {
			FileValue thisValue = arguments.getNext(FileValue.class);
			try {
				return BooleanValue.of(thisValue.value.delete());
			}
			catch (SecurityException exception) {
				throw arguments.getError("Could not delete file '%s'", thisValue);
			}
		}

		@FunctionDoc(
			name = "exists",
			desc = "This returns if the file exists",
			returns = {BOOLEAN, "true if the file exists"},
			throwMsgs = "Could not check file: ...",
			example = "file.exists()"
		)
		private Value exists(Arguments arguments) throws CodeError {
			FileValue thisValue = arguments.getNext(FileValue.class);
			try {
				return BooleanValue.of(thisValue.value.exists());
			}
			catch (SecurityException exception) {
				throw arguments.getError("Could not check file '%s'", thisValue);
			}
		}

		@FunctionDoc(
			name = "createDirectory",
			desc = "This creates all parent directories of the file if they don't already exist",
			returns = {BOOLEAN, "true if the directories were created"},
			throwMsgs = "...",
			example = "file.createDirectory()"
		)
		private Value createDirectory(Arguments arguments) throws CodeError {
			FileValue thisValue = arguments.getNext(FileValue.class);
			try {
				return BooleanValue.of(thisValue.value.mkdirs());
			}
			catch (InvalidPathException e) {
				throw arguments.getError(e.getMessage());
			}
		}

		@FunctionDoc(
			name = "getPath",
			desc = "This returns the path of the file",
			returns = {STRING, "the path of the file"},
			example = "file.getPath()"
		)
		private Value getPath(Arguments arguments) throws CodeError {
			FileValue thisValue = arguments.getNext(FileValue.class);
			return StringValue.of(thisValue.value.getPath());
		}

		@FunctionDoc(
			name = "getAbsolutePath",
			desc = "This returns the absolute path of the file",
			returns = {STRING, "the absolute path of the file"},
			example = "file.getAbsolutePath()"
		)
		private Value getAbsolutePath(Arguments arguments) throws CodeError {
			FileValue thisValue = arguments.getNext(FileValue.class);
			try {
				return StringValue.of(thisValue.value.getAbsolutePath());
			}
			catch (SecurityException e) {
				throw arguments.getError(e.getMessage());
			}
		}

		@FunctionDoc(
			name = "open",
			desc = "This opens the file (as in opens it on your os)",
			example = "file.open()"
		)
		private Value open(Arguments arguments) throws CodeError {
			FileValue thisValue = arguments.getNext(FileValue.class);
			try {
				Desktop.getDesktop().open(thisValue.value);
			}
			catch (Exception e) {
				throw arguments.getError("An error occurred while opening the file '%s'", thisValue);
			}
			return NullValue.NULL;
		}
	}
}
