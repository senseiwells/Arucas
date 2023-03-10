package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.nodes.StatementVisitor

/**
 * This statement allows for continues inside loops.
 *
 * @param trace the trace position - if used in an incorrect context.
 */
class ContinueStatement(
    val trace: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitContinue(this)
}