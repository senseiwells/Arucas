package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.nodes.expressions.Expression

/**
 * This statement allows for returning values in functions and top level.
 *
 * @param expression the expression to return.
 * @param trace the trace position - if used in an incorrect context.
 */
class ReturnStatement(
    val expression: Expression,
    val trace: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitReturn(this)
}