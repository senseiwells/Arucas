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

object Arucas {
    const val VERSION = "3.0.0"

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
            p.addBool("-format") { builder.setOutput(DefaultArucasIO(it)) }
            p.addBool("-debug") { properties.isDebug = it }
            p.addBool("-experimental") { properties.isExperimental = it }
            p.addBool("-suppressDeprecated") { properties.logDeprecated = it }
            p.addBool("-cmdLine") { cmdLine = it }
            p.addInt("-maxErrorLength") { properties.errorMaxLength = it }
            p.addStr("-run") { runFile(api, it) }
            p.addStr("-generate") { ArucasDocVisitors.generateDefault(Path.of(it), api) }

            p.parse(args)
        }

        if (cmdLine) {
            commandLine(api)
        }
    }

    private fun commandLine(api: ArucasAPI) {
        api.getOutput().println("\nWelcome to the Arucas Interpreter!")
        while (true) {
            api.getOutput().print("\n>> ")
            val content = readln().trim()
            when {
                content.isBlank() -> continue
                content == "quit" || content == "exit" -> exitProcess(130)
                else -> {
                    val matcher = Regex("^.*?\\.arucas\$").find(content)
                    if (matcher != null) {
                        if (runFile(api, matcher.value)) {
                            continue
                        }
                    }
                }
            }

            Interpreter.of(content, "console", api).executeAsync().get()
        }
    }

    private fun runFile(api: ArucasAPI, filePath: String): Boolean {
        return try {
            val path = Path.of(filePath)
            val fileName = path.fileName?.toString() ?: filePath
            val content = Files.readString(path)
            Interpreter.of(content, fileName, api).executeBlocking()
            true
        } catch (e: ArucasError) {
            true
        } catch (e: Exception) {
            api.getOutput().printError("Could not read file '$filePath':\n${e.stackTraceToString()}")
            false
        }
    }
}