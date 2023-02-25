package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.classes.PrimitiveDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.nodes.ExpressionVisitor
import kotlin.reflect.KClass

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