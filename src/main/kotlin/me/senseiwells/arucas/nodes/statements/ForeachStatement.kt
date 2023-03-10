package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.nodes.expressions.Expression
import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This statement allows for foreach loops.
 *
 * @param name the name of the iterated variable.
 * @param iterable the expression to iterate over.
 * @param body the statement to execute for each iteration.
 * @param start the trace position - if the iterable expression is not iterable.
 * @param end the end trace position.
 */
class ForeachStatement(
    val name: String,
    val iterable: Expression,
    val body: Statement,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitForeach(this)
}