package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.utils.Parameter

/**
 * This statement allows for try-catch-finally blocks.
 *
 * @param body The try body.
 * @param catchBody The catch body.
 * @param catchParameter The catch parameter.
 * @param finally The finally body.
 * @param start The trace position.
 */
class TryStatement(
    val body: Statement,
    val catchBody: Statement,
    val catchParameter: Parameter?,
    val finally: Statement,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitTry(this)
}