package me.senseiwells.arucas.nodes

import me.senseiwells.arucas.builtin.ErrorDef
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.core.Parser
import me.senseiwells.arucas.core.Resolver
import me.senseiwells.arucas.exceptions.Propagator
import me.senseiwells.arucas.exceptions.RuntimeError
import me.senseiwells.arucas.nodes.statements.*

/**
 * Visitor interface which allows the implementation to
 * visit the different [Statement]s. The AST is parsed by
 * the [Parser].
 *
 * This is intended for use with [Resolver] and [Interpreter] however
 * can be used outside of this for analysis of code etc.
 *
 * @param T this declares the return type of all the visiting methods.
 * @see Resolver
 * @see Interpreter
 */
interface StatementVisitor<T> {
    /**
     * This visits a [VoidStatement], these are 'dummy'
     * statements which the [Parser] uses when it encounters
     * a redundant statement (e.g. an empty scope).
     *
     * This should usually do nothing.
     *
     * @param void the void statement.
     * @see VoidStatement
     */
    fun visitVoid(void: VoidStatement): T

    /**
     * This visits [Statements] which is just a collection of
     * [Statement]s.
     *
     * This should iterate over each statement and visit them.
     *
     * @param statements the multiple statements to be visited.
     * @see Statements
     */
    fun visitStatements(statements: Statements): T

    /**
     * This visits a [ScopeStatement] which declares when there is
     * a new scope.
     *
     * @param scope the scope to visit.
     * @see ScopeStatement
     */
    fun visitScope(scope: ScopeStatement): T

    /**
     * This visits a [LocalVarStatement] which declares a variable
     * in the current scope.
     *
     * @param local the local statements to visit.
     * @see LocalVarStatement
     */
    fun visitLocalVar(local: LocalVarStatement): T

    /**
     * This visits an [ExpressionStatement] which houses an expression
     * to be evaluated by an [ExpressionVisitor].
     *
     * @param expression the expression to evaluate.
     * @see ExpressionStatement
     * @see ExpressionVisitor
     */
    fun visitExpression(expression: ExpressionStatement): T

    /**
     * This visits an [IfStatement] which has a condition
     * to be evaluated by an [ExpressionVisitor] which decides
     * whether to execute the body or otherwise [Statement].
     *
     * @param ifStatement the if statement to evaluate.
     * @see IfStatement
     */
    fun visitIf(ifStatement: IfStatement): T

    /**
     * This visits a [SwitchStatement] which has a value to
     * be evaluated by an [ExpressionVisitor] which is then used
     * to check against the cases which also need to be evaluated.
     * Then when a match is found it will execute the body of the case.
     *
     * @param switch the switch statement to be evaluated.
     * @see SwitchStatement
     */
    fun visitSwitch(switch: SwitchStatement): T

    /**
     * This visits a [FunctionStatement] which declares a function
     * in a given scope/class. This should create a function
     * and store it on the stack.
     *
     * @param function the function statement to be evaluated.
     * @see FunctionStatement
     */
    fun visitFunction(function: FunctionStatement): T

    /**
     * This visits a [ReturnStatement] which should throw a
     * [Propagator.Return] which should be caught by the
     * [ExpressionVisitor.visitCall].
     *
     * @param returnStatement the return statement to be evaluated.
     * @see ReturnStatement
     * @see Propagator.Return
     */
    fun visitReturn(returnStatement: ReturnStatement): T

    /**
     * This visits a [WhileStatement] which should evaluate the condition
     * and loop over the [Statement]s until the condition is no longer met.
     *
     * @param whileStatement the while statement to be evaluated.
     * @see WhileStatement
     */
    fun visitWhile(whileStatement: WhileStatement): T

    /**
     * This visits a [ForStatement] which should evaluate the initial
     * statement then evaluate the condition and loop over the [Statement]s
     * until the condition is no longer met. After each loop it should
     * also evaluate the final expression.
     *
     * @param forStatement the for statement to be evaluated.
     * @see ForStatement
     */
    fun visitFor(forStatement: ForStatement): T

    /**
     * This visits a [ForeachStatement] which should evaluate the iterable
     * and execute the body statement with each element in ther iterable.
     *
     * @param foreach the foreach statement to be evaluated.
     * @see ForeachStatement
     */
    fun visitForeach(foreach: ForeachStatement): T

    /**
     * This visits a [ContinueStatement] which should throw [Propagator.Continue]
     * which should be caught in any of the loop visitors.
     *
     * @param continueStatement the continue statement to be evaluated.
     * @see ContinueStatement
     */
    fun visitContinue(continueStatement: ContinueStatement): T

    /**
     * This visits a [BreakStatement] which should throw [Propagator.Break] which should
     * be caught in any of the loop visitors and the switch visitor.
     *
     * @param breakStatement the break statement to be evaluated.
     * @see BreakStatement
     */
    fun visitBreak(breakStatement: BreakStatement): T

    /**
     * This visits a [TryStatement] which should catch any [RuntimeError]s
     * and pass it to the catch block to be handled. It should also
     * always execute the `finally` statement if given one.
     *
     * @param tryStatement the try statement to be evaluated.
     * @see TryStatement
     */
    fun visitTry(tryStatement: TryStatement): T

    /**
     * This visits a [ThrowStatement] which should throw an
     * instance of [ErrorDef].
     *
     * @param throwStatement the throw statement to be evaluated.
     * @see ThrowStatement
     */
    fun visitThrow(throwStatement: ThrowStatement): T

    /**
     * This visits a [ConstructorStatement] which should define
     * a constructor in the current class that is being visited.
     *
     * @param constructor the constructor statement to be evaluated.
     * @see ConstructorStatement
     */
    fun visitConstructor(constructor: ConstructorStatement): T

    /**
     * This visits a [ClassBodyStatement] which defines all the
     * different parts of the class.
     *
     * @param classBody the class body statement to be evaluated.
     * @see ClassBodyStatement
     */
    fun visitClassBody(classBody: ClassBodyStatement): T

    /**
     * This visits a [ClassStatement] which defines the name
     * and parents of the class and also contains the [ClassBodyStatement].
     *
     * @param classStatement the class statement to be evaluated.
     * @see ClassStatement
     */
    fun visitClass(classStatement: ClassStatement): T

    /**
     * This visits an [EnumStatement] which defines all the enum
     * values as well as the name of the enum and any parents.
     * Similarly to the [ClassStatement] it also contains a [ClassBodyStatement].
     *
     * @param enumStatement the enum statement to be evaluated.
     * @see EnumStatement
     */
    fun visitEnum(enumStatement: EnumStatement): T

    /**
     * This visits an [InterfaceStatement] which defines an interface
     * definition this should record all the methods that the interface
     * is declaring.
     *
     * @param interfaceStatement the interface statement to be evaluated.
     * @see InterfaceStatement
     */
    fun visitInterface(interfaceStatement: InterfaceStatement): T

    /**
     * This visits an [ImportStatement] which defines what
     * imports should be available in a given script.
     *
     * @param importStatement the import statement to be evaluated.
     * @see ImportStatement
     */
    fun visitImport(importStatement: ImportStatement): T
}



