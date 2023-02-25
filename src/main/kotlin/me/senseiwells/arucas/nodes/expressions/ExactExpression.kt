package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.nodes.ExpressionVisitor

/**
 * This expression returns an exact class instance.
 *
 * @param instance The instance to get.
 */
class ExactExpression(
    val instance: ClassInstance
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitExact(this)
}