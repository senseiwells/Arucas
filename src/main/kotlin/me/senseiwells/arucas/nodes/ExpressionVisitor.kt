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
 * @param T this declares the return type of all the visiting methods.
 * @see Resolver
 * @see Interpreter
 */
interface ExpressionVisitor<T> {
    /**
     * This visits an [ExactExpression] which holds
     * an exact class instance - much like a literal expression.
     *
     * @param exact the exact expression.
     * @see ExactExpression
     */
    fun visitExact(exact: ExactExpression): T

    /**
     * Visits a [LiteralExpression] which holds
     * a literal instance which is cached.
     *
     * @param L the primitive type.
     * @param literal the literal expression.
     * @see LiteralExpression
     */
    fun <L: PrimitiveDefinition<*>> visitLiteral(literal: LiteralExpression<L>): T

    /**
     * Visits a [BracketExpression] which wraps an
     * expression within it to ensure precedence.
     *
     * @param bracket the bracket expression.
     * @see BracketExpression
     */
    fun visitBracket(bracket: BracketExpression): T

    /**
     * Visits a [ListExpression] which holds a list of
     * other expressions to be evaluated to be converted into a list.
     *
     * @param list the list expression.
     * @see ListExpression
     */
    fun visitList(list: ListExpression): T

    /**
     * Visits a [MapExpression] which holds a map of key-value pairs
     * of expressions to be evaluated and to be converted into a map.
     *
     * @param map the map expression.
     * @see MapExpression
     */
    fun visitMap(map: MapExpression): T

    /**
     * Visits a [FunctionExpression] which represents a lambda
     * function. This function is not declared in a scope
     * but instead just returns the instance of itself.
     *
     * @param function the function expression.
     * @see FunctionExpression
     */
    fun visitFunction(function: FunctionExpression): T

    /**
     * Visits a [UnaryExpression] which performs an operation
     * on a single expression.
     *
     * @param unary the unary expression.
     * @see UnaryExpression
     */
    fun visitUnary(unary: UnaryExpression): T

    /**
     * Visits a [BinaryExpression] which performs an operation
     * between two different expressions.
     *
     * @param binary the binary expression.
     * @see BinaryExpression
     */
    fun visitBinary(binary: BinaryExpression): T

    /**
     * Visits an [UnpackAssignExpression] which unpacks values
     * from a list and assigns them to variables.
     *
     * @param assign the unpack assign expression.
     * @see UnpackAssignExpression
     */
    fun visitUnpackAssign(assign: UnpackAssignExpression): T

    /**
     * Visits an [AccessExpression] which accesses a
     * local variable, this may also be used to delegate
     * a declared function into a first class object.
     *
     * @param access the access expression.
     * @see AccessExpression
     */
    fun visitAccess(access: AccessExpression): T

    /**
     * Visits a [FunctionAccessExpression] which accesses a function
     * with a given name and number of parameters.
     *
     * @param access the function access expression.
     * @see FunctionAccessExpression
     */
    fun visitFunctionAccess(access: FunctionAccessExpression): T

    /**
     * Visits an [AssignExpression] which assigns an instance to a
     * variable, this may be in the current scope or any parent
     * scopes if a variable with the given name is already defined.
     *
     * @param assign the assign expression.
     * @see AssignExpression
     */
    fun visitAssign(assign: AssignExpression): T

    /**
     * Visits a [CallExpression] which calls a function and
     * passes arguments into the invocation.
     *
     * @param call the call expression.
     * @see CallExpression
     */
    fun visitCall(call: CallExpression): T

    /**
     * This visits a [ThisExpression] which represents the `this` keyword,
     * this accesses the instance of the class that the interpreter is in.
     *
     * @param thisExpression the 'this' expression.
     * @see ThisExpression
     */
    fun visitThis(thisExpression: ThisExpression): T

    /**
     * This visits a [SuperExpression] which represents the `super` keyword,
     * this accesses methods/fields on the parent class of one that the
     * interpreter is currently in.
     *
     * @param superExpression the super expression.
     * @see SuperExpression
     */
    fun visitSuper(superExpression: SuperExpression): T

    /**
     * This visits a [MemberAccessExpression] which represents accessing a member
     * of an object. This may also be used to delegate a function.
     *
     * @param access the member access expression.
     * @see MemberAccessExpression
     */
    fun visitMemberAccess(access: MemberAccessExpression): T

    /**
     * This visits a [MemberAssignExpression] which represents assigning a value
     * to a member of an object.
     *
     * @param assign the member assign expression.
     * @see MemberAssignExpression
     */
    fun visitMemberAssign(assign: MemberAssignExpression): T

    /**
     * This visits a [MemberCallExpression] which represents calling a member function
     * of an object.
     *
     * @param call the member call expression.
     * @see MemberCallExpression
     */
    fun visitMemberCall(call: MemberCallExpression): T

    /**
     * This visits a [NewAccessExpression] which represents delegating a
     * new instance of a class using the `new` keyword, this doesn't
     * invoke the constructor but instead delegates it for later.
     *
     * @param access the new access expression.
     * @see NewAccessExpression
     */
    fun visitNewAccess(access: NewAccessExpression): T

    /**
     * This visits a [NewCallExpression] which represents calling a
     * constructor function to create a new instance of a class.
     *
     * @param call the new call expression.
     * @see NewCallExpression
     */
    fun visitNewCall(call: NewCallExpression): T

    /**
     * This visits a [BracketAccessExpression] which represents accessing an element
     * of a collection using square brackets.
     *
     * @param access the bracket access expression.
     * @see BracketAccessExpression
     */
    fun visitBracketAccess(access: BracketAccessExpression): T

    /**
     * This visits a [BracketAssignExpression] which represents assigning a value
     * to an element of a collection using square brackets.
     *
     * @param assign the bracket assign expression.
     * @see BracketAssignExpression
     */
    fun visitBracketAssign(assign: BracketAssignExpression): T
}