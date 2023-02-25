package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This statement allows for an interface definition.
 *
 * @param name The name of the interface.
 * @param requiredMethods The required methods for the interface.
 * @param start The trace position - if interface name is taken.
 * @param end The end trace position.
 */
class InterfaceStatement(
    val name: String,
    val requiredMethods: List<Pair<String, Int>>,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitInterface(this)
}