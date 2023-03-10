package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.nodes.ExpressionVisitor

/**
 * This expression accesses the 'this' reference if available.
 *
 * @param trace the trace position - if the access is invalid.
 */
class ThisExpression(
    val trace: LocatableTrace
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitThis(this)
}