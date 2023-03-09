package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.nodes.ExpressionVisitor
import me.senseiwells.arucas.nodes.statements.Statement
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.typed.HintedParameter

/**
 * This expression creates a function value.
 *
 * @param parameters The array of arguments with types.
 * @param arbitrary Whether the function is varargs.
 * @param returnTypes The return types of the function.
 * @param body The body of the function.
 * @param start The start trace position - for stack trace.
 * @param end The end trace position.
 */
class FunctionExpression(
    val name: String,
    val parameters: List<HintedParameter>,
    val arbitrary: Boolean,
    val returnTypes: Array<String>?,
    val body: Statement,
    val start: LocatableTrace,
    val end: LocatableTrace
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitFunction(this)
}