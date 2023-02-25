package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This statement allows for importing of other classes outside this file.
 *
 * @param names The names of the classes to import, empty if import all.
 * @param path The path to the file to import.
 * @param local Whether the import is local.
 * @param trace The trace position - if import is invalid.
 */
class ImportStatement(
    val names: List<String>,
    val path: String,
    val local: Boolean,
    val trace: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitImport(this)
}