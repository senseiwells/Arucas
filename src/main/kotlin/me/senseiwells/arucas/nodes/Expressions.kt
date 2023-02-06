package me.senseiwells.arucas.nodes

import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.classes.PrimitiveDefinition
import me.senseiwells.arucas.core.Type
import me.senseiwells.arucas.utils.CallTrace
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.utils.Parameter
import kotlin.reflect.KClass

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

/**
 * The base expression class
 */
abstract class Expression: Visitable {
    internal abstract fun <T> visit(visitor: ExpressionVisitor<T>): T

    override fun toString(): String {
        return this::class.simpleName ?: "Expression"
    }
}

/**
 * This expression returns an exact class instance.
 *
 * @param instance The instance to get.
 */
class ExactExpression(
    val instance: ClassInstance
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitExact(this)
}

/**
 * This expression directly gets a value, these should be immutable.
 *
 * @param klass The primitive definition class.
 * @param supplier The supplier for the literal.
 */
class LiteralExpression<T: PrimitiveDefinition<*>>(
    val klass: KClass<out T>,
    val supplier: (T) -> ClassInstance
): Expression() {
    var cache: ClassInstance? = null

    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitLiteral(this)
}

/**
 * This expression allows for changing order of operations.
 *
 * @param expression The expression to wrap.
 */
class BracketExpression(
    val expression: Expression
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitBracket(this)
}

/**
 * This expression creates a function value.
 *
 * @param parameters The array of arguments with types.
 * @param arbitrary Whether the function is varargs.
 * @param returnTypes The return types of the function.
 * @param body The body of the function.
 * @param start The start trace position - for stack trace.
 * @param end The end trace position.
 */
class FunctionExpression(
    val name: String,
    val parameters: List<Parameter>,
    val arbitrary: Boolean,
    val returnTypes: Array<String>?,
    val body: Statement,
    val start: LocatableTrace,
    val end: LocatableTrace
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitFunction(this)
}

/**
 * This expression evaluates the list literal.
 *
 * @param expressions The list to evaluate.
 */
class ListExpression(
    val expressions: List<Expression>
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitList(this)
}

/**
 * This expression evaluates the map literal.
 *
 * @param expressions The map to evaluate.
 */
class MapExpression(
    val expressions: Map<Expression, Expression>
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitMap(this)
}

/**
 * This expression applies a unary operator on a value.
 *
 * @param type The operator type.
 * @param expression The expression that will have the operator applied to.
 * @param trace The trace position - if the unary operation is invalid.
 */
class UnaryExpression(
    val type: Type,
    val expression: Expression,
    val trace: LocatableTrace
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitUnary(this)
}

/**
 * This expression applies a binary operator between two values.
 *
 * @param left The left hand side expression.
 * @param type The operator type.
 * @param right The right hand side expression.
 * @param trace The trace position - if the binary operation is invalid.
 */
class BinaryExpression(
    val left: Expression,
    val type: Type,
    val right: Expression,
    val trace: LocatableTrace
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitBinary(this)
}

/**
 * This interface allows us to convert access expressions into assign ones.
 */
interface Assignable {
    fun toAssignable(assignee: Expression): AssignableExpression
}

/**
 * This interface allows us to convert access expressions into callable ones.
 */
interface Callable {
    fun toCallable(arguments: List<Expression>): Expression
}

/**
 * This abstract class is the base for all assignable expressions.
 */
abstract class AssignableExpression(
    val assignee: Expression,
    val trace: LocatableTrace
): Expression() {
    abstract fun copyWith(instance: ClassInstance): AssignableExpression
}

class UnpackAssignExpression(
    val assignables: List<AssignableExpression>,
    val assignee: Expression,
    val trace: LocatableTrace
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitUnpackAssign(this)
}

/**
 * This expression accesses a variable in the scope.
 *
 * @param name The name of the identifier to access.
 * @param trace The trace position - if the access is invalid.
 */
class AccessExpression(
    val name: String,
    val trace: LocatableTrace
): Expression(), Assignable, Callable {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitAccess(this)

    override fun toAssignable(assignee: Expression): AssignExpression {
        return AssignExpression(this.name, assignee, this.trace)
    }

    override fun toCallable(arguments: List<Expression>): Expression {
        return CallExpression(FunctionAccessExpression(this.name, arguments.size, this.trace), arguments, this.trace)
    }
}

/**
 * This expression access a function in the scope.
 *
 * @param name The name of the function to access.
 * @param parameters The number of parameters being passed in.
 * @param trace The trace position - if the access in invalid.
 */
class FunctionAccessExpression(
    val name: String,
    val parameters: Int,
    val trace: LocatableTrace
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitFunctionAccess(this)
}

/**
 * This expression assigns an identifier in the scope.
 *
 * @param name The name of the identifier to assign to.
 * @param assignee The expression to assign to the identifier.
 * @param trace The trace position - if the assignment is invalid.
 */
class AssignExpression(
    val name: String,
    assignee: Expression,
    trace: LocatableTrace
): AssignableExpression(assignee, trace) {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitAssign(this)

    override fun copyWith(instance: ClassInstance): AssignExpression {
        return AssignExpression(this.name, ExactExpression(instance), this.trace)
    }
}

/**
 * This expression calls an expression, for example a function.
 *
 * @param expression The expression to be called.
 * @param arguments The arguments to pass into the call.
 * @param trace The trace position - if the call is invalid.
 */
class CallExpression(
    val expression: Expression,
    val arguments: List<Expression>,
    val trace: LocatableTrace
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitCall(this)
}

/**
 * This expression accesses the 'this' reference if available.
 *
 * @param trace The trace position - if the access is invalid.
 */
class ThisExpression(
    val trace: LocatableTrace
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitThis(this)
}

/**
 * This expression is used to access super class members if available.
 *
 * @param trace The trace position - if the access is invalid.
 */
class SuperExpression(
    val trace: LocatableTrace
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitSuper(this)
}

/**
 * This expression accesses an identifier.
 *
 * @param expression The expression to access.
 * @param name The name of the identifier.
 * @param trace The trace position - if the access is invalid.
 */
class MemberAccessExpression(
    val expression: Expression,
    val name: String,
    val trace: LocatableTrace
): Expression(), Assignable, Callable {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitMemberAccess(this)

    override fun toAssignable(assignee: Expression): MemberAssignExpression {
        return MemberAssignExpression(this.expression, this.name, assignee, this.trace)
    }

    override fun toCallable(arguments: List<Expression>): Expression {
        return MemberCallExpression(this.expression, this.name, arguments, this.trace)
    }
}

/**
 * This expression assigns a value to a member.
 *
 * @param expression The expression to assign to.
 * @param name The name of the identifier.
 * @param assignee The expression to assign to the field.
 * @param trace The trace position - if the assignment is invalid.
 */
class MemberAssignExpression(
    val expression: Expression,
    val name: String,
    assignee: Expression,
    trace: LocatableTrace
): AssignableExpression(assignee, trace) {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitMemberAssign(this)

    override fun copyWith(instance: ClassInstance): AssignableExpression {
        return MemberAssignExpression(this.expression, this.name, ExactExpression(instance), trace)
    }
}

/**
 * This expression calls a member on a value.
 *
 * @param expression The expression to call on.
 * @param name The name of the member to call.
 * @param arguments The arguments to pass into the call.
 * @param trace The trace position - if the call is invalid.
 */
class MemberCallExpression(
    val expression: Expression,
    val name: String,
    val arguments: List<Expression>,
    val trace: LocatableTrace
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitMemberCall(this)
}

/**
 * This expression is used to access a class constructor.
 *
 * @param name The name of the class to instantiate.
 * @param trace The trace position - if the access is invalid.
 */
class NewAccessExpression(
    val name: String,
    val trace: LocatableTrace
): Expression(), Callable {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitNewAccess(this)

    override fun toCallable(arguments: List<Expression>): Expression {
        return NewCallExpression(this.name, arguments, CallTrace(this.trace, "new ${this.name}::${arguments.size}"))
    }
}

/**
 * This expression is used to call a class constructor.
 *
 * @param name The name of the class to instantiate.
 * @param arguments The arguments to pass into the constructor.
 * @param trace The trace position - if the call is invalid.
 */
class NewCallExpression(
    val name: String,
    val arguments: List<Expression>,
    val trace: CallTrace
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitNewCall(this)
}

/**
 * This expression is used to access brackets on an expression.
 *
 * @param expression The expression to access.
 * @param index The index to access.
 * @param trace The trace position - if the access is invalid.
 */
class BracketAccessExpression(
    val expression: Expression,
    val index: Expression,
    val trace: LocatableTrace
): Expression(), Assignable {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitBracketAccess(this)

    override fun toAssignable(assignee: Expression): BracketAssignExpression {
        return BracketAssignExpression(this.expression, this.index, assignee, this.trace)
    }
}

/**
 * This expression is used to assign something to an expression using brackets.
 *
 * @param expression The expression to assign to.
 * @param index The index to assign to.
 * @param assignee The expression to assign to the index.
 * @param trace The trace position - if the assignment is invalid.
 */
class BracketAssignExpression(
    val expression: Expression,
    val index: Expression,
    assignee: Expression,
    trace: LocatableTrace
): AssignableExpression(assignee, trace) {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitBracketAssign(this)

    override fun copyWith(instance: ClassInstance): BracketAssignExpression {
        return BracketAssignExpression(this.expression, this.index, ExactExpression(instance), trace)
    }
}