package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.core.Type
import me.senseiwells.arucas.nodes.expressions.Expression
import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.utils.Parameter

/**
 * This statement declares the body of a class.
 *
 * @param fields The fields for the class.
 * @param staticFields The static fields for the class.
 * @param staticInitializers The static initializers for the class.
 * @param methods The methods for the class.
 * @param staticMethods The static methods for the class.
 * @param operators The operators for the class.
 * @param start The trace position - if class is invalid.
 * @param end The end trace position.
 */
class ClassBodyStatement(
    val fields: Map<Parameter, Expression>,
    val staticFields: Map<Parameter, Expression>,
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