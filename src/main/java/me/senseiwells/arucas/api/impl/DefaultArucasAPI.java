package me.senseiwells.arucas.api.impl;

import me.senseiwells.arucas.api.IArucasAPI;
import me.senseiwells.arucas.api.IArucasInput;
import me.senseiwells.arucas.api.IArucasOutput;
import me.senseiwells.arucas.core.Arucas;

import java.nio.file.Path;

public class DefaultArucasAPI implements IArucasAPI {
	private final ImplArucasIO IOHandler = new ImplArucasIO();
	private final Path IMPORT_PATH = Arucas.PATH.resolve("libs");

	@Override
	public IArucasInput getInput() {
		return this.IOHandler;
	}

	@Override
	public IArucasOutput getOutput() {
		return this.IOHandler;
	}

	@Override
	public Path getImportPath() {
		return this.IMPORT_PATH;
	}
}
