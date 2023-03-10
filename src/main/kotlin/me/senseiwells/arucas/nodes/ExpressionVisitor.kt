package me.senseiwells.arucas.nodes

import me.senseiwells.arucas.classes.PrimitiveDefinition
import me.senseiwells.arucas.compiler.Parser
import me.senseiwells.arucas.compiler.Resolver
import me.senseiwells.arucas.interpreter.Interpreter
import me.senseiwells.arucas.nodes.expressions.*

/**
 * Visitor interface which allows the implementation to
 * visit the different [Expression]s. The AST is parsed by
 * the [Parser].
 *
 * This is intended for use with [Resolver] and [Interpreter] however
 * can be used outside of this for analysis of code etc.
 *
 * @see Resolver
 * @see Interpreter
 */
interface ExpressionVisitor<T> {
    fun visitExact(exact: ExactExpression): T
    fun <L: PrimitiveDefinition<*>> visitLiteral(literal: LiteralExpression<L>): T
    fun visitBracket(bracket: BracketExpression): T
    fun visitList(list: ListExpression): T
    fun visitMap(map: MapExpression): T
    fun visitFunction(function: FunctionExpression): T

    fun visitUnary(unary: UnaryExpression): T
    fun visitBinary(binary: BinaryExpression): T

    fun visitUnpackAssign(assign: UnpackAssignExpression): T

    fun visitAccess(access: AccessExpression): T
    fun visitFunctionAccess(access: FunctionAccessExpression): T
    fun visitAssign(assign: AssignExpression): T
    fun visitCall(call: CallExpression): T

    fun visitThis(thisExpression: ThisExpression): T
    fun visitSuper(superExpression: SuperExpression): T

    fun visitMemberAccess(access: MemberAccessExpression): T
    fun visitMemberAssign(assign: MemberAssignExpression): T
    fun visitMemberCall(call: MemberCallExpression): T

    fun visitNewAccess(access: NewAccessExpression): T
    fun visitNewCall(call: NewCallExpression): T

    fun visitBracketAccess(access: BracketAccessExpression): T
    fun visitBracketAssign(assign: BracketAssignExpression): T
}