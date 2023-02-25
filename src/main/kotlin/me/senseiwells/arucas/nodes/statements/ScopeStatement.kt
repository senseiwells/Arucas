package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This statement is used to evaluate a statement in a new scope.
 *
 *  @param statements The list of statement to evaluate.
 *  @param end The start trace position.
 *  @param end The end trace position.
 */
class ScopeStatement(
    val statements: Statement,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitScope(this)
}