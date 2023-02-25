package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.nodes.expressions.Expression
import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This statement is used to declare a local variable.
 *
 * @param name The name of the local variable.
 * @param assignee The expression to assign.
 * @param trace The trace position - if the declaration is invalid.
 */
class LocalVarStatement(
    val name: String,
    val assignee: Expression,
    val types: Array<String>?,
    val trace: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitLocalVar(this)
}