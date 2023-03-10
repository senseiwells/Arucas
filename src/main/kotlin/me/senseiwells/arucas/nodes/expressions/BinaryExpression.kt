package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.core.Type
import me.senseiwells.arucas.nodes.ExpressionVisitor
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This expression applies a binary operator between two values.
 *
 * @param left the left hand side expression.
 * @param type the operator type.
 * @param right the right hand side expression.
 * @param trace the trace position - if the binary operation is invalid.
 */
class BinaryExpression(
    val left: Expression,
    val type: Type,
    val right: Expression,
    val trace: LocatableTrace
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitBinary(this)
}