package me.senseiwells.arucas

import me.senseiwells.arucas.api.ArucasAPI
import me.senseiwells.arucas.api.ImplArucasIO
import me.senseiwells.arucas.api.ThreadHandler
import me.senseiwells.arucas.api.docs.parser.DocParser
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.utils.ArgumentParser
import me.senseiwells.arucas.utils.Properties
import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern
import kotlin.system.exitProcess

private val FILE_MATCHER = Pattern.compile("^.*?\\.arucas\$")

fun main(args: Array<String>) {
    val builder = ArucasAPI.Builder()
        .addDefault()

    val properties = Properties()
    builder.setInterpreterProperties { properties }
    val api = builder.build()

    var cmdLine = true
    ArgumentParser().let { p ->
        p.addBool("-format") { builder.setOutput(ImplArucasIO(it)) }
        p.addBool("-debug") { properties.isDebug = it }
        p.addBool("-experimental") { properties.isExperimental = it }
        p.addBool("-suppressDeprecated") { properties.logDeprecated = it }
        p.addBool("-cmdLine") { cmdLine = it }
        p.addInt("-maxErrorLength") { properties.errorMaxLength = it }
        p.addStr("-run") { runFile(api, it) }
        p.addStr("-generate") { DocParser.generateAll(Path.of(it), api) }

        p.parse(args)
    }

    if (cmdLine) {
        commandLine(api)
    }
}

fun commandLine(api: ArucasAPI) {
    api.getOutput().println("\nWelcome to the Arucas Interpreter!")
    while (true) {
        api.getOutput().print("\n>> ")
        val content = readln().trim()
        when {
            content.isBlank() -> continue
            content == "quit" || content == "exit" -> exitProcess(130)
            else -> {
                val matcher = FILE_MATCHER.matcher(content)
                if (matcher.find()) {
                    if (runFile(api, matcher.group())) {
                        continue
                    }
                }
            }
        }

        Interpreter.of(content, "console", api, ::ThreadHandler).threadHandler.executeBlocking()
    }
}

fun runFile(api: ArucasAPI, filePath: String): Boolean {
    return try {
        val path = Path.of(filePath)
        val fileName = path.fileName?.toString() ?: filePath
        val content = Files.readString(path)
        Interpreter.of(content, fileName, api, ::ThreadHandler).threadHandler.executeBlocking()
        true
    } catch (e: Exception) {
        api.getOutput().printError("Could not read file '$filePath':\n${e.stackTraceToString()}")
        false
    }
}