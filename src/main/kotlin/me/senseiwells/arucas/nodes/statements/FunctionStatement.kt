package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.typed.HintedParameter

/**
 * This statement declares a function.
 *
 * @param name the name of the function.
 * @param isClass whether the function is a method.
 * @param parameters the array of arguments with types.
 * @param arbitrary whether the function is varargs.
 * @param private whether the function is private.
 * @param returnTypes the return types of the function.
 * @param body the body of the function.
 * @param start the trace position - for stack trace.
 * @param end the end trace position.
*/
class FunctionStatement(
    val name: String,
    val isClass: Boolean,
    val private: Boolean,
    val parameters: List<HintedParameter>,
    val arbitrary: Boolean,
    val returnTypes: Array<String>?,
    val body: Statement,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitFunction(this)
}