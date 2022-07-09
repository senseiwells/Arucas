package me.senseiwells.arucas.api.impl;

import me.senseiwells.arucas.api.IArucasAPI;
import me.senseiwells.arucas.api.IArucasOutput;
import me.senseiwells.arucas.core.Arucas;

import java.nio.file.Path;

public class DefaultArucasAPI implements IArucasAPI {
	private final IArucasOutput OUTPUT_HANDLER = new ImplArucasIO();
	private final Path IMPORT_PATH = Arucas.PATH.resolve("libs");

	@Override
	public IArucasOutput getOutput() {
		return this.OUTPUT_HANDLER;
	}

	@Override
	public Path getImportPath() {
		return this.IMPORT_PATH;
	}
}
