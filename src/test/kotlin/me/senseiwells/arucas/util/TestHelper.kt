package me.senseiwells.arucas.util

import me.senseiwells.arucas.api.ArucasAPI
import me.senseiwells.arucas.api.ArucasErrorHandler
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.CompileError
import me.senseiwells.arucas.exceptions.RuntimeError
import me.senseiwells.arucas.extensions.JavaDef
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.ExecutionException

object TestHelper {
    val API_DEFAULT = ArucasAPI.Builder().addDefault().setErrorHandler(ArucasErrorHandler.noop).build()
    val API_JAVA = ArucasAPI.Builder().addDefault().setErrorHandler(ArucasErrorHandler.noop).addBuiltInDefinitions(::JavaDef).build()

    fun compile(code: String, api: ArucasAPI = API_DEFAULT) {
        Interpreter.of(code, "test-compile", api).compile()
    }

    fun execute(code: String, api: ArucasAPI = API_DEFAULT): Any? {
        try {
            return Interpreter.of(code, "test-execute", api).executeBlocking().asJava()
        } catch (e: ExecutionException) {
            val cause = e.cause
            cause ?: throw e
            throw cause
        }
    }

    fun assertEquals(expected: Any?, code: String, api: ArucasAPI = API_DEFAULT) {
        val value = if (expected is Number) expected.toDouble() else expected
        kotlin.test.assertEquals(value, this.execute(code, api))
    }

    fun throwsRuntime(code: String, api: ArucasAPI = API_DEFAULT) {
        assertThrows<RuntimeError> { this.execute(code, api) }
    }

    fun throwsCompile(code: String) {
        assertThrows<CompileError> { this.compile(code) }
    }
}