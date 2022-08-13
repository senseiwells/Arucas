package me.senseiwells.arucas.extensions

import me.senseiwells.arucas.api.docs.ClassDoc
import me.senseiwells.arucas.api.docs.FunctionDoc
import me.senseiwells.arucas.builtin.FileDef
import me.senseiwells.arucas.builtin.StringDef
import me.senseiwells.arucas.classes.PrimitiveDefinition
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.utils.Arguments
import me.senseiwells.arucas.utils.BuiltInFunction
import me.senseiwells.arucas.utils.Util
import me.senseiwells.arucas.utils.Util.Types.BOOLEAN
import me.senseiwells.arucas.utils.Util.Types.FILE
import me.senseiwells.arucas.utils.Util.Types.NETWORK
import me.senseiwells.arucas.utils.Util.Types.STRING
import java.awt.Desktop
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException

@ClassDoc(
    name = NETWORK,
    desc = ["Allows you to do http requests. This is a utility class and cannot be constructed."],
    importPath = "util.Network"
)
class NetworkDef(interpreter: Interpreter): PrimitiveDefinition<Unit>(NETWORK, interpreter) {
    override fun defineStaticMethods(): List<BuiltInFunction> {
        return listOf(
            BuiltInFunction.of("requestUrl", 1, this::requestUrl),
            BuiltInFunction.of("downloadFile", 2, this::downloadFile),
            BuiltInFunction.of("openUrl", 1, this::openUrl)
        )
    }

    @FunctionDoc(
        isStatic = true,
        name = "requestUrl",
        desc = ["Requests an url and returns the response"],
        params = [STRING, "url", "the url to request"],
        returns = [STRING, "the response from the url"],
        examples = ["Network.requestUrl('https://google.com');"]
    )
    private fun requestUrl(arguments: Arguments): String {
        val url = arguments.nextPrimitive(StringDef::class)
        return Util.Network.getStringFromUrl(url) ?: runtimeError("Failed to request data from '$url'")
    }

    @FunctionDoc(
        isStatic = true,
        name = "downloadFile",
        desc = ["Downloads a file from an url to a file"],
        params = [STRING, "url", "the url to download from", FILE, "file", "the file to download to"],
        returns = [BOOLEAN, "whether the download was successful"],
        examples = ["Network.downloadFile('https://arucas.com', new File('dir/downloads'));"]
    )
    private fun downloadFile(arguments: Arguments): Boolean {
        val url = arguments.nextPrimitive(StringDef::class)
        val file = arguments.nextPrimitive(FileDef::class)
        return Util.Network.downloadFile(url, file)
    }

    @FunctionDoc(
        isStatic = true,
        name = "openUrl",
        desc = ["Opens an url in the default browser"],
        params = [STRING, "url", "the url to open"],
        examples = ["Network.openUrl('https://google.com');"]
    )
    private fun openUrl(arguments: Arguments) {
        val url = arguments.nextPrimitive(StringDef::class)
        try {
            Desktop.getDesktop().browse(URI(url))
        } catch (e: UnsupportedOperationException) {
            runtimeError("Opening urls is not supported on your system", e)
        } catch (e: IOException) {
            runtimeError("Couldn't launch browser", e)
        } catch (e: URISyntaxException) {
            runtimeError("Invalid url '$url'", e)
        }
    }
}