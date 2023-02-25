package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.nodes.expressions.Expression
import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This statement declares an enum
 *
 * @param name The name of the enum.
 * @param parents The parents of the enum.
 * @param enums The enums of the enum.
 * @param body The body of the enum.
 * @param start The trace position - if enum name is taken.
 * @param end The end trace position.
 */
class EnumStatement(
    val name: String,
    val parents: List<String>,
    val enums: LinkedHashMap<String, Pair<List<Expression>, LocatableTrace>>,
    val body: Statement,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitEnum(this)
}