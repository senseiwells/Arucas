package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.nodes.expressions.Expression
import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This statement declares a switch statement.
 *
 * @param condition The condition to match.
 * @param casesList The cases to match.
 * @param caseStatements The statements for the cases.
 * @param defaultStatement The default statement.
 * @param start the start of the location - used for the stacktrace.
 * @param end the end of the location.
 */
class SwitchStatement(
    val condition: Expression,
    val casesList: List<List<Expression>>,
    val caseStatements: List<Statement>,
    val defaultStatement: Statement?,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitSwitch(this)
}