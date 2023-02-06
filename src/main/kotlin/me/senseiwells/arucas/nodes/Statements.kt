package me.senseiwells.arucas.nodes

import me.senseiwells.arucas.core.Type
import me.senseiwells.arucas.utils.ConstructorInit
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.utils.Parameter

interface StatementVisitor<T> {
    fun visitVoid(void: VoidStatement): T

    fun visitStatements(statements: Statements): T
    fun visitScope(scope: ScopeStatement): T

    fun visitLocalVar(local: LocalVarStatement): T
    fun visitExpression(expression: ExpressionStatement): T
    fun visitIf(ifStatement: IfStatement): T
    fun visitSwitch(switch: SwitchStatement): T
    fun visitFunction(function: FunctionStatement): T
    fun visitReturn(returnStatement: ReturnStatement): T

    fun visitWhile(whileStatement: WhileStatement): T
    fun visitFor(forStatement: ForStatement): T
    fun visitForeach(foreach: ForeachStatement): T
    fun visitContinue(continueStatement: ContinueStatement): T
    fun visitBreak(breakStatement: BreakStatement): T

    fun visitTry(tryStatement: TryStatement): T
    fun visitThrow(throwStatement: ThrowStatement): T

    fun visitConstructor(constructor: ConstructorStatement): T
    fun visitClassBody(classBody: ClassBodyStatement): T
    fun visitClass(classStatement: ClassStatement): T
    fun visitEnum(enumStatement: EnumStatement): T
    fun visitInterface(interfaceStatement: InterfaceStatement): T

    fun visitImport(importStatement: ImportStatement): T
}

/**
 * The base statement class
 */
abstract class Statement: Visitable {
    abstract fun <T> visit(visitor: StatementVisitor<T>): T

    override fun toString(): String {
        return this::class.simpleName ?: "Statement"
    }
}

/**
 * This is a dummy statement
 */
class VoidStatement private constructor(): Statement() {
    companion object {
        val INSTANCE = VoidStatement()
    }

    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitVoid(this)
}

/**
 * This statement is used to evaluate multiple statements without new scope.
 *
 * @param statements The list of statements to evaluate
 */
class Statements(
    val statements: List<Statement>
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitStatements(this)
}

/**
 * This statement is used to evaluate a statement in a new scope.
 *
 *  @param statements The list of statement to evaluate.
 *  @param end The start trace position.
 *  @param end The end trace position.
 */
class ScopeStatement(
    val statements: Statement,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitScope(this)
}

/**
 * This statement is used to declare a local variable.
 *
 * @param name The name of the local variable.
 * @param assignee The expression to assign.
 * @param trace The trace position - if the declaration is invalid.
 */
class LocalVarStatement(
    val name: String,
    val assignee: Expression,
    val types: Array<String>?,
    val trace: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitLocalVar(this)
}

/**
 * This statement is used for when an expression is passed as a statement.
 *
 * @param expression The expression to be evaluated.
 */
class ExpressionStatement(
    val expression: Expression
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitExpression(this)
}

/**
 * This statement allows for control flow.
 *
 * @param condition The conditional expression.
 * @param body The statement to execute if condition is met.
 * @param otherwise The statement to execute if condition is not met.
 * @param start The start trace position.
 * @param end The end trace position.
 */
class IfStatement(
    val condition: Expression,
    val body: Statement,
    val otherwise: Statement,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitIf(this)
}

/**
 * This statement declares a switch statement.
 *
 * @param condition The condition to match.
 * @param casesList The cases to match.
 * @param caseStatements The statements for the cases.
 * @param defaultStatement The default statement.
 * @param start the start of the location - used for the stacktrace.
 * @param end the end of the location.
 */
class SwitchStatement(
    val condition: Expression,
    val casesList: List<List<Expression>>,
    val caseStatements: List<Statement>,
    val defaultStatement: Statement?,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitSwitch(this)
}

/**
 * This statement declares a function.
 *
 * @param name The name of the function.
 * @param isClass Whether the function is a method.
 * @param parameters The array of arguments with types.
 * @param arbitrary Whether the function is varargs.
 * @param returnTypes The return types of the function.
 * @param body The body of the function.
 * @param start The trace position - for stack trace.
 * @param end The end trace position.
*/
class FunctionStatement(
    val name: String,
    val isClass: Boolean,
    val parameters: List<Parameter>,
    val arbitrary: Boolean,
    val returnTypes: Array<String>?,
    val body: Statement,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitFunction(this)
}

/**
 * This statement allows for returning values in functions and top level.
 *
 * @param expression The expression to return.
 * @param trace The trace position - if used in an incorrect context.
 */
class ReturnStatement(
    val expression: Expression,
    val trace: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitReturn(this)
}

/**
 * This statement allows for while loops.
 *
 * @param condition The conditional expression.
 * @param body The statement to execute if condition is true.
 * @param Start The trace position - if the condition is not a boolean.
 * @param end The end trace position.
 */
class WhileStatement(
    val condition: Expression,
    val body: Statement,
    val Start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitWhile(this)
}

/**
 * This statement allows for for loops.
 *
 * @param initial The initializer statement.
 * @param condition The conditional expression.
 * @param expression The end statement.
 * @param body The statement to execute if condition is true.
 * @param start The trace position - if the condition is not a boolean.
 * @param end The end trace position.
 */
class ForStatement(
    val initial: Statement,
    val condition: Expression,
    val expression: Expression,
    val body: Statement,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitFor(this)
}

/**
 * This statement allows for foreach loops.
 *
 * @param name The name of the iterated variable.
 * @param iterable The expression to iterate over.
 * @param body The statement to execute for each iteration.
 * @param start The trace position - if the iterable expression is not iterable.
 * @param end The end trace position.
 */
class ForeachStatement(
    val name: String,
    val iterable: Expression,
    val body: Statement,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitForeach(this)
}

/**
 * This statement allows for continues inside loops.
 *
 * @param trace The trace position - if used in an incorrect context.
 */
class ContinueStatement(
    val trace: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitContinue(this)
}

/**
 * This statement allows for breaks inside loops and switches.
 *
 * @param trace The trace position - if used in an incorrect context.
 */
class BreakStatement(
    val trace: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitBreak(this)
}

/**
 * This statement allows for try-catch-finally blocks.
 *
 * @param body The try body.
 * @param catchBody The catch body.
 * @param catchParameter The catch parameter.
 * @param finally The finally body.
 * @param start The trace position.
 */
class TryStatement(
    val body: Statement,
    val catchBody: Statement,
    val catchParameter: Parameter?,
    val finally: Statement,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitTry(this)
}

/**
 * This statement allows for throwing errors.
 *
 * @param throwable The expression to throw.
 * @param trace The trace position - where the error occured.
 */
class ThrowStatement(
    val throwable: Expression,
    val trace: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitThrow(this)
}

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
    val parameters: List<Parameter>,
    val arbitrary: Boolean,
    val init: ConstructorInit,
    val body: Statement,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitConstructor(this)
}

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

/**
 * This statement declares a class.
 *
 * @param name The name of the class.
 * @param parents The parents of the class.
 * @param body The body of the class.
 * @param start The trace position - if class name is taken.
 * @param end The end trace position.
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

/**
 * This statement allows for an interface definition.
 *
 * @param name The name of the interface.
 * @param requiredMethods The required methods for the interface.
 * @param start The trace position - if interface name is taken.
 * @param end The end trace position.
 */
class InterfaceStatement(
    val name: String,
    val requiredMethods: List<Pair<String, Int>>,
    val start: LocatableTrace,
    val end: LocatableTrace
): Statement() {
    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitInterface(this)
}

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



