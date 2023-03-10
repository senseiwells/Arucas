package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.nodes.expressions.Expression

/**
 * This statement allows for while loops.
 *
 * @param condition the conditional expression.
 * @param body the statement to execute if condition is true.
 * @param Start the trace position - if the condition is not a boolean.
 * @param end the end trace position.
 */
class WhileStatement(
    val condition: Expression,
    val body: Statement,
    val Start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitWhile(this)
}