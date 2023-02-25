package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.nodes.expressions.Expression
import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This statement allows for while loops.
 *
 * @param condition The conditional expression.
 * @param body The statement to execute if condition is true.
 * @param Start The trace position - if the condition is not a boolean.
 * @param end The end trace position.
 */
class WhileStatement(
    val condition: Expression,
    val body: Statement,
    val Start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitWhile(this)
}