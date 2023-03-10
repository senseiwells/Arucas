package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This statement allows for an interface definition.
 *
 * @param name the name of the interface.
 * @param requiredMethods the required methods for the interface.
 * @param start the trace position - if interface name is taken.
 * @param end the end trace position.
 */
class InterfaceStatement(
    val name: String,
    val requiredMethods: List<Pair<String, Int>>,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitInterface(this)
}