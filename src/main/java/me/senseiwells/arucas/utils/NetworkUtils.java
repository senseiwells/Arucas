package me.senseiwells.arucas.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class NetworkUtils {
	private static final String LIBRARY_URL = "https://api.github.com/repos/senseiwells/ArucasLibraries/contents/libs";
	private static final Gson GSON = new Gson();

	public static String getStringFromUrl(String url) {
		try {
			InputStream inputStream = new URL(url).openStream();
			if (inputStream == null) {
				throw new IOException();
			}
			Writer stringWriter = new StringWriter();
			char[] charBuffer = new char[2048];
			try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
				int counter;
				while ((counter = reader.read(charBuffer)) != -1) {
					stringWriter.write(charBuffer, 0, counter);
				}
			}
			finally {
				inputStream.close();
			}
			return stringWriter.toString();
		}
		catch (IOException ioException) {
			return null;
		}
	}

	// Returns true if successful
	public static boolean downloadFile(String url, File file) {
		try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
			URL website = new URL(url);
			ReadableByteChannel byteChannel = Channels.newChannel(website.openStream());
			fileOutputStream.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);
			return true;
		}
		catch (IOException e) {
			return false;
		}
	}

	// Returns true if successful
	public static boolean openUrl(String url) {
		Desktop desktop;
		if (!Desktop.isDesktopSupported() || !(desktop = Desktop.getDesktop()).isSupported(Desktop.Action.BROWSE)) {
			return false;
		}

		try {
			URI uri = new URI(url);
			desktop.browse(uri);
			return true;
		}
		catch (IOException | URISyntaxException e) {
			return false;
		}
	}

	public static void downloadLibrary(Path importPath, String scriptName) {
		String response = getStringFromUrl(LIBRARY_URL + "/" + scriptName);
		if (response != null) {
			JsonObject responseObject = ExceptionUtils.catchAsNull(() -> GSON.fromJson(response, JsonObject.class));
			if (responseObject != null) {
				String libraryContent = getStringFromUrl(responseObject.get("download_url").getAsString());
				if (libraryContent != null) {
					ExceptionUtils.runSafe(() -> {
						Files.createDirectories(importPath.getParent());
						Files.write(importPath, Collections.singleton(libraryContent));
					});
				}
			}
		}
	}
}
