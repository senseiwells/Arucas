package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.nodes.StatementVisitor

/**
 * This statement is used to evaluate multiple statements without new scope.
 *
 * @param statements the list of statements to evaluate
 */
class Statements(
    val statements: List<Statement>
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitStatements(this)
}