package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.nodes.expressions.Expression
import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This statement allows for control flow.
 *
 * @param condition The conditional expression.
 * @param body The statement to execute if condition is met.
 * @param otherwise The statement to execute if condition is not met.
 * @param start The start trace position.
 * @param end The end trace position.
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