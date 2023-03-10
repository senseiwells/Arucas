package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This statement declares a class.
 *
 * @param name the name of the class.
 * @param parents the parents of the class.
 * @param body the body of the class.
 * @param start the trace position - if class name is taken.
 * @param end the end trace position.
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