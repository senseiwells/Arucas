package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.nodes.StatementVisitor

/**
 * This statement is used to evaluate a statement in a new scope.
 *
 *  @param statements the list of statement to evaluate.
 *  @param end the start trace position.
 *  @param end the end trace position.
 */
class ScopeStatement(
    val statements: Statement,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitScope(this)
}