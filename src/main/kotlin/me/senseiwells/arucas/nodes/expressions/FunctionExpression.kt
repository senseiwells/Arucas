package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.nodes.ExpressionVisitor
import me.senseiwells.arucas.nodes.statements.Statement
import me.senseiwells.arucas.typed.HintedParameter

/**
 * This expression creates a function value.
 *
 * @param parameters the array of arguments with types.
 * @param arbitrary whether the function is varargs.
 * @param returnTypes the return types of the function.
 * @param body the body of the function.
 * @param start the start trace position - for stack trace.
 * @param end the end trace position.
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