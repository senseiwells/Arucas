package me.senseiwells.arucas.extensions.util;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.NetworkUtils;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;

import java.io.File;
import java.util.List;

/**
 * Network class extension for Arucas. Allows you to do http requests. <br>
 * Import the class with <code>import Network from util.Network;</code> <br>
 * This is a utility class and cannot be constructed. <br>
 * Fully Documented.
 * @author senseiwells
 */
public class ArucasNetworkClass extends ArucasClassExtension {
	public ArucasNetworkClass() {
		super("Network");
	}

	@Override
	public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
		return ArucasFunctionMap.of(
			new BuiltInFunction("requestUrl", "url", this::requestUrl),
			new BuiltInFunction("downloadFile", List.of("url", "file"), this::downloadFile),
			new BuiltInFunction("openUrl", "url", this::openUrl)
		);
	}

	/**
	 * Name: <code>Network.requestUrl(url)</code> <br>
	 * Description: Requests an url and returns the response <br>
	 * Parameter - String: the url to request <br>
	 * Returns - String: the response from the url <br>
	 * Throws - Error: <code>"Failed to request data from ..."</code> if the request fails <br>
	 * Example: <code>Network.requestUrl("https://google.com");</code>
	 */
	private Value<?> requestUrl(Context context, BuiltInFunction function) throws CodeError {
		String url = function.getFirstParameter(context, StringValue.class).value;
		String response = NetworkUtils.getStringFromUrl(url);
		if (response == null) {
			throw new RuntimeError("Failed to request data from '%s'".formatted(url), function.syntaxPosition, context);
		}
		return StringValue.of(response);
	}

	/**
	 * Name: <code>Network.downloadFile(url, file)</code> <br>
	 * Description: Downloads a file from a url to a file <br>
	 * Parameter - String, File: the url to download from, the file to download it to <br>
	 * Returns - Boolean: whether the download was successful <br>
	 * Example: <code>Network.downloadFile("https://arucas.com", new File("dir/downloads"));</code>
	 */
	private Value<?> downloadFile(Context context, BuiltInFunction function) throws CodeError {
		String url = function.getFirstParameter(context, StringValue.class).value;
		File file = function.getParameterValueOfType(context, FileValue.class, 1).value;
		return BooleanValue.of(NetworkUtils.downloadFile(url, file));
	}

	/**
	 * Name: <code>Network.openUrl(url)</code> <br>
	 * Description: Opens a url in the default browser <br>
	 * Parameter - String: the url to open <br>
	 * Throws - Error: <code>"Failed to open url ..."</code> if the request to open <br>
	 * Example: <code>Network.openUrl("https://google.com");</code>
	 */
	private Value<?> openUrl(Context context, BuiltInFunction function) throws CodeError {
		String url = function.getFirstParameter(context, StringValue.class).value;
		if (!NetworkUtils.openUrl(url)) {
			throw new RuntimeError("Failed to open url '%s'".formatted(url), function.syntaxPosition, context);
		}
		return NullValue.NULL;
	}

	@Override
	public Class<? extends BaseValue> getValueClass() {
		return null;
	}
}
