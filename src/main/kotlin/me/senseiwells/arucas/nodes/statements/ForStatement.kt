package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.nodes.expressions.Expression

/**
 * This statement allows for for loops.
 *
 * @param initial the initializer statement.
 * @param condition the conditional expression.
 * @param expression the end statement.
 * @param body the statement to execute if condition is true.
 * @param start the trace position - if the condition is not a boolean.
 * @param end the end trace position.
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