package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.nodes.expressions.Expression
import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This statement allows for control flow.
 *
 * @param condition the conditional expression.
 * @param body the statement to execute if condition is met.
 * @param otherwise the statement to execute if condition is not met.
 * @param start the start trace position.
 * @param end the end trace position.
 */
class IfStatement(
    val condition: Expression,
    val body: Statement,
    val otherwise: Statement,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitIf(this)
}