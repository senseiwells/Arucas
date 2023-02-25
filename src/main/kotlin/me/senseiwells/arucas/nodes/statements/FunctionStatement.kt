package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.utils.Parameter

/**
 * This statement declares a function.
 *
 * @param name The name of the function.
 * @param isClass Whether the function is a method.
 * @param parameters The array of arguments with types.
 * @param arbitrary Whether the function is varargs.
 * @param returnTypes The return types of the function.
 * @param body The body of the function.
 * @param start The trace position - for stack trace.
 * @param end The end trace position.
*/
class FunctionStatement(
    val name: String,
    val isClass: Boolean,
    val parameters: List<Parameter>,
    val arbitrary: Boolean,
    val returnTypes: Array<String>?,
    val body: Statement,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitFunction(this)
}