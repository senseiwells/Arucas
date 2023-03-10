package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.core.Type
import me.senseiwells.arucas.nodes.expressions.Expression
import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.typed.HintedParameter
import me.senseiwells.arucas.typed.HintedVariable

/**
 * This statement declares the body of a class.
 *
 * @param fields the fields for the class.
 * @param staticFields the static fields for the class.
 * @param staticInitializers the static initializers for the class.
 * @param methods the methods for the class.
 * @param staticMethods the static methods for the class.
 * @param operators the operators for the class.
 * @param start the trace position - if class is invalid.
 * @param end the end trace position.
 */
class ClassBodyStatement(
    val fields: Collection<HintedVariable>,
    val staticFields: Collection<HintedVariable>,
    val staticInitializers: List<Statement>,
    val constructors: List<ConstructorStatement>,
    val methods: List<FunctionStatement>,
    val staticMethods: List<FunctionStatement>,
    val operators: List<Pair<FunctionStatement, Type>>,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitClassBody(this)
}