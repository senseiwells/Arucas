package me.senseiwells.arucas.nodes.statements

import me.senseiwells.arucas.nodes.StatementVisitor

/**
 * This is a dummy statement.
 */
class VoidStatement private constructor(): Statement() {
    companion object {
        val INSTANCE = VoidStatement()
    }

    override fun <T> visit(visitor: StatementVisitor<T>) = visitor.visitVoid(this)
}