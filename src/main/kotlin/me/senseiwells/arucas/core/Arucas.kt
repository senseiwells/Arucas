package me.senseiwells.arucas.core

import me.senseiwells.arucas.classes.ClassInstance
import me.senseiwells.arucas.exceptions.Propagator
import me.senseiwells.arucas.nodes.Statement
import java.nio.file.Path

object Arucas {
    @JvmStatic
    val VERSION = "2.0.0"
    @JvmStatic
    val PATH: Path = Path.of(System.getProperty("user.home")).resolve(".arucas")

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