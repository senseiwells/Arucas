package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.nodes.expressions.Expression

/**
 * This statement is used to declare a local variable.
 *
 * @param name the name of the local variable.
 * @param assignee the expression to assign.
 * @param trace the trace position - if the declaration is invalid.
 */
class LocalVarStatement(
    val name: String,
    val assignee: Expression,
    val types: Array<String>?,
    val trace: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitLocalVar(this)
}