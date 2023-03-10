package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.nodes.ExpressionVisitor

/**
 * This expression applies an unpacking assignment.
 *
 * @param assignables the list of assignable expressions to unpack to.
 * @param assignee the list expressions containing the values to unpack.
 * @param trace the trace location if an error occurs.
 */
class UnpackAssignExpression(
    val assignables: List<AssignableExpression>,
    val assignee: Expression,
    val trace: LocatableTrace
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitUnpackAssign(this)
}