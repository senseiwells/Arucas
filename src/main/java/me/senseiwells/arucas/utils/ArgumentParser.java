package me.senseiwells.arucas.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ArgumentParser {
	Map<String, Consumer<Context>> argumentMap;

	public ArgumentParser() {
		this.argumentMap = new HashMap<>();
	}

	public ArgumentParser addArgument(String argument, Consumer<Context> consumer) {
		this.argumentMap.put(argument, consumer);
		return this;
	}

	public void parse(Context context, String[] arguments) {
		for (String argument : arguments) {
			Consumer<Context> callback = this.argumentMap.get(argument);
			if (callback != null) {
				callback.accept(context);
			}
		}
	}
}
