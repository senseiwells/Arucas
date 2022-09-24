package me.senseiwells.arucas.core

import me.senseiwells.arucas.classes.ClassInstance
import me.senseiwells.arucas.exceptions.*
import me.senseiwells.arucas.nodes.Statement
import java.nio.file.Path

object Arucas {
    @JvmStatic
    val VERSION = "2.0.0"
    @JvmStatic
    val PATH: Path = Path.of(System.getProperty("user.home")).resolve(".arucas")

    @JvmStatic
    fun runSafe(interpreter: Interpreter): ClassInstance {
        return try {
            run(interpreter)
        } catch (e: RuntimeError) {
            throw e
        } catch (f: FatalError) {
            // Fatal errors shouldn't be handled by
            // the parent as this
            throw f
        } catch (e: CompileError) {
            // Compile errors are propagated as runtime errors
            // so parent script can handle accordingly
            runtimeError("Compiling failed", e)
        }
    }

    @JvmStatic
    fun run(interpreter: Interpreter): ClassInstance {
        return try {
            interpreter.interpret()
            interpreter.getNull()
        } catch (returnPropagator: Propagator.Return) {
            returnPropagator.returnValue
        }
    }

    @JvmStatic
    fun compile(content: String, name: String): List<Statement> {
        val tokens = Lexer(content, name).createTokens()
        return Parser(tokens).parse()
    }
}