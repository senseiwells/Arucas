package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.utils.ConstructorInit
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.typed.HintedParameter

/**
 * This statement declares a class constructor.
 *
 * @param parameters the parameters for the constructor.
 * @param arbitrary whether the constructor is varargs.
 * @param private whether the constructor is private.
 * @param init the referencing constructor (this(), or super()).
 * @param body the body of the constructor.
 * @param start the trace position - if constructor is invalid.
 * @param end the end trace position.
 */
class ConstructorStatement(
    val parameters: List<HintedParameter>,
    val arbitrary: Boolean,
    val private: Boolean,
    val init: ConstructorInit,
    val body: Statement,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitConstructor(this)
}