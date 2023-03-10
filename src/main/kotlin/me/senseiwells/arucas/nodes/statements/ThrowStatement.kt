package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.nodes.expressions.Expression

/**
 * This statement allows for throwing errors.
 *
 * @param throwable the expression to throw.
 * @param trace the trace position - where the error occured.
 */
class ThrowStatement(
    val throwable: Expression,
    val trace: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitThrow(this)
}