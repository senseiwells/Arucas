package me.senseiwells.arucas

import me.senseiwells.arucas.api.ArucasAPI
import me.senseiwells.arucas.api.docs.visitor.impl.ArucasDocVisitors
import me.senseiwells.arucas.api.impl.DefaultArucasIO
import me.senseiwells.arucas.exceptions.ArucasError
import me.senseiwells.arucas.interpreter.Interpreter
import me.senseiwells.arucas.interpreter.Properties
import me.senseiwells.arucas.utils.misc.ArgumentParser
import java.nio.file.Files
import java.nio.file.Path
import kotlin.system.exitProcess

/**
 * The main arucas class.
 */
object Arucas {
    /**
     * The current version of Arucas.
     */
    const val VERSION = "2.3.0"

    /**
     * The path of the `.arucas` folder where the default config and libraries are stored.
     */
    @JvmField
    val PATH: Path = Path.of(System.getProperty("user.home")).resolve(".arucas")

    @JvmStatic
    fun main(args: Array<String>) {
        val builder = ArucasAPI.Builder()
            .addDefault()

        val properties = Properties()
        builder.setInterpreterProperties { properties }
        val api = builder.build()

        var cmdLine = true
        ArgumentParser().let { p ->
            p.addBoolean("-format") { builder.setOutput(DefaultArucasIO(it)) }
            p.addBoolean("-debug") { properties.isDebug = it }
            p.addBoolean("-experimental") { properties.isExperimental = it }
            p.addBoolean("-suppressDeprecated") { properties.logDeprecated = it }
            p.addBoolean("-cmdLine") { cmdLine = it }
            p.addInt("-maxErrorLength") { properties.errorMaxLength = it }
            p.addString("-run") { runFile(api, it) }
            p.addString("-generate") { ArucasDocVisitors.generateDefault(Path.of(it), api) }

            p.parse(args)
        }

        if (cmdLine) {
            commandLine(api)
        }
    }

    private fun commandLine(api: ArucasAPI) {
        api.getOutput().println("\nWelcome to the Arucas Interpreter!")
        val lastInputs = ArrayList<String>()
        while (true) {
            api.getOutput().print("\n>> ")
            var content = readln().trim()
            if (content.matches(Regex("^\\^+$"))) {
                val index = content.length - 1
                if (index > lastInputs.lastIndex) {
                    api.getOutput().printError("> Cannot get previous input for '$content'")
                    continue
                }
                content = lastInputs[index]
            }

            when {
                content.isBlank() -> continue
                content == "quit" || content == "exit" -> exitProcess(130)
                else -> {
                    val matcher = Regex("^.*?\\.arucas\$").find(content)
                    if (matcher != null) {
                        runFile(api, matcher.value)
                        if (content != lastInputs.firstOrNull()) {
                            lastInputs.add(0, content)
                        }
                        continue
                    }
                }
            }

            Interpreter.of(content, "console", api).executeAsync().get()
            if (content != lastInputs.firstOrNull()) {
                lastInputs.add(0, content)
            }
        }
    }

    private fun runFile(api: ArucasAPI, filePath: String) {
        try {
            val path = Path.of(filePath)
            val fileName = path.fileName?.toString() ?: filePath
            val content = Files.readString(path)
            Interpreter.of(content, fileName, api).executeBlocking()
        } catch (_: ArucasError) {

        } catch (e: Exception) {
            api.getOutput().printError("Could not read file '$filePath':\n${e.stackTraceToString()}")
        }
    }
}