package me.senseiwells.arucas.nodes.expressions

/**
 * This interface allows us to convert access expressions into assign ones.
 */
interface Assignable {
    fun toAssignable(assignee: Expression): AssignableExpression
}