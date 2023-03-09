package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.utils.ConstructorInit
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.typed.HintedParameter

/**
 * This statement declares a class constructor.
 *
 * @param parameters The parameters for the constructor.
 * @param arbitrary Whether the constructor is varargs.
 * @param init The referencing constructor (this(), or super()).
 * @param body The body of the constructor.
 * @param start The trace position - if constructor is invalid.
 * @param end The end trace position.
 */
class ConstructorStatement(
    val parameters: List<HintedParameter>,
    val arbitrary: Boolean,
    val init: ConstructorInit,
    val body: Statement,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitConstructor(this)
}