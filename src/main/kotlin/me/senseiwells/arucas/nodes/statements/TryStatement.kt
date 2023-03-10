package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.typed.HintedParameter

/**
 * This statement allows for try-catch-finally blocks.
 *
 * @param body the try body.
 * @param catchBody the catch body.
 * @param catchParameter the catch parameter.
 * @param finally the finally body.
 * @param start the trace position.
 */
class TryStatement(
    val body: Statement,
    val catchBody: Statement,
    val catchParameter: HintedParameter?,
    val finally: Statement,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitTry(this)
}