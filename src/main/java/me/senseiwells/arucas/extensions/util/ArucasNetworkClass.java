package me.senseiwells.arucas.extensions.util;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.NetworkUtils;
import me.senseiwells.arucas.utils.ValueTypes;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;

import java.io.File;

import static me.senseiwells.arucas.utils.ValueTypes.*;

@ClassDoc(
	name = ValueTypes.NETWORK,
	desc = "Allows you to do http requests. This is a utility class and cannot be constructed.",
	importPath = "util.Network"
)
public class ArucasNetworkClass extends ArucasClassExtension {
	public ArucasNetworkClass() {
		super(ValueTypes.NETWORK);
	}

	@Override
	public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
		return ArucasFunctionMap.of(
			BuiltInFunction.of("requestUrl", 1, this::requestUrl),
			BuiltInFunction.of("downloadFile", 2, this::downloadFile),
			BuiltInFunction.of("openUrl", 1, this::openUrl)
		);
	}

	@FunctionDoc(
		isStatic = true,
		name = "requestUrl",
		desc = "Requests an url and returns the response",
		params = {STRING, "url", "the url to request"},
		returns = {STRING, "the response from the url"},
		throwMsgs = "Failed to request data from ...",
		example = "Network.requestUrl('https://google.com');"
	)
	private Value requestUrl(Arguments arguments) throws CodeError {
		String url = arguments.getNextVal(StringValue.class);
		String response = NetworkUtils.getStringFromUrl(url);
		if (response == null) {
			throw arguments.getError("Failed to request data from '%s'", url);
		}
		return StringValue.of(response);
	}

	@FunctionDoc(
		isStatic = true,
		name = "downloadFile",
		desc = "Downloads a file from an url to a file",
		params = {
			STRING, "url", "the url to download from",
			FILE, "file", "the file to download to"
		},
		returns = {BOOLEAN, "whether the download was successful"},
		example = "Network.downloadFile('https://arucas.com', new File('dir/downloads'));"
	)
	private Value downloadFile(Arguments arguments) throws CodeError {
		String url = arguments.getNextVal(StringValue.class);
		File file = arguments.getNextVal(FileValue.class);
		return BooleanValue.of(NetworkUtils.downloadFile(url, file));
	}

	@FunctionDoc(
		isStatic = true,
		name = "openUrl",
		desc = "Opens an url in the default browser",
		params = {STRING, "url", "the url to open"},
		throwMsgs = "Failed to open url ...",
		example = "Network.openUrl('https://google.com');"
	)
	private Value openUrl(Arguments arguments) throws CodeError {
		String url = arguments.getNextVal(StringValue.class);
		if (!NetworkUtils.openUrl(url)) {
			throw arguments.getError("Failed to open url '%s'", url);
		}
		return NullValue.NULL;
	}

	@Override
	public Class<? extends Value> getValueClass() {
		return null;
	}
}
