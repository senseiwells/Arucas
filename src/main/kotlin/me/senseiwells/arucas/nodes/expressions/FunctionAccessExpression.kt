package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.nodes.ExpressionVisitor

/**
 * This expression access a function in the scope.
 *
 * @param name the name of the function to access.
 * @param parameters the number of parameters being passed in.
 * @param trace the trace position - if the access in invalid.
 */
class FunctionAccessExpression(
    val name: String,
    val parameters: Int,
    val trace: LocatableTrace
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitFunctionAccess(this)
}