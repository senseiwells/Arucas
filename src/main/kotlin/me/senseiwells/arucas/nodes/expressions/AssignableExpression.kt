package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This abstract class is the base for all assignable expressions.
 */
abstract class AssignableExpression(
    val assignee: Expression,
    val trace: LocatableTrace
): Expression() {
    abstract fun copyWith(instance: ClassInstance): AssignableExpression
}