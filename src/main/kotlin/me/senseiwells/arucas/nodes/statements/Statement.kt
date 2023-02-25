package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.nodes.StatementVisitor
import me.senseiwells.arucas.nodes.Visitable

/**
 * The base statement class.
 */
abstract class Statement: Visitable {
    abstract fun <T> visit(visitor: StatementVisitor<T>): T

    override fun toString(): String {
        return this::class.simpleName ?: "Statement"
    }
}