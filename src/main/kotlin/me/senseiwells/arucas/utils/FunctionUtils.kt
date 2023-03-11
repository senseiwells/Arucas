package me.senseiwells.arucas.utils

import me.senseiwells.arucas.builtin.BooleanDef
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.compiler.CallTrace
import me.senseiwells.arucas.compiler.Trace
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.interpreter.Interpreter

object FunctionUtils {
    @JvmStatic
    @JvmOverloads
    fun callAsPredicate(interpreter: Interpreter, predicate: ClassInstance, value: ClassInstance, trace: CallTrace = Trace.INTERNAL): Boolean {
        return interpreter.call(predicate, listOf(value), trace).getPrimitive(BooleanDef::class)
            ?: runtimeError("Predicate function must return boolean", trace)
    }
}