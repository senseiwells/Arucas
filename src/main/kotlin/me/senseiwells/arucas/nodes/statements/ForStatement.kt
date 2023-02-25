package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.nodes.expressions.Expression
import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This statement allows for for loops.
 *
 * @param initial The initializer statement.
 * @param condition The conditional expression.
 * @param expression The end statement.
 * @param body The statement to execute if condition is true.
 * @param start The trace position - if the condition is not a boolean.
 * @param end The end trace position.
 */
class ForStatement(
    val initial: Statement,
    val condition: Expression,
    val expression: Expression,
    val body: Statement,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitFor(this)
}