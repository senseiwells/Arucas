package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.nodes.expressions.Expression
import me.senseiwells.arucas.nodes.StatementVisitor

/**
 * This statement is used for when an expression is passed as a statement.
 *
 * @param expression The expression to be evaluated.
 */
class ExpressionStatement(
    val expression: Expression
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitExpression(this)
}