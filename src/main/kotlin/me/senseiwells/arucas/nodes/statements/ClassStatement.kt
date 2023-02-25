package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This statement declares a class.
 *
 * @param name The name of the class.
 * @param parents The parents of the class.
 * @param body The body of the class.
 * @param start The trace position - if class name is taken.
 * @param end The end trace position.
 */
class ClassStatement(
    val name: String,
    val parents: List<String>,
    val body: ClassBodyStatement,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitClass(this)
}