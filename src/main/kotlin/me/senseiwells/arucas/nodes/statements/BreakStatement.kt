package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This statement allows for breaks inside loops and switches.
 *
 * @param trace The trace position - if used in an incorrect context.
 */
class BreakStatement(
    val trace: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitBreak(this)
}