package me.senseiwells.arucas.utils

import me.senseiwells.arucas.nodes.SuperExpression
import me.senseiwells.arucas.nodes.Visitable

/**
 * Cache storing distances for [StackTable] for different [Visitable]s.
 *
 * This allows us to jump to specific positions in the [StackTable] to look
 * for variables/functions/classes, so we do not need to search the stack hierarchy.
 */
class LocalCache {
    private val varCache = HashMap<Visitable, Int>()
    private val functionCache = HashMap<Visitable, Int>()
    private val classCache = HashMap<Visitable, Int>()
    private val superCache = HashMap<SuperExpression, String>()

    fun setVar(visitable: Visitable, distance: Int) = this.varCache.put(visitable, distance)

    fun setFunction(visitable: Visitable, distance: Int) = this.functionCache.put(visitable, distance)

    fun setClass(visitable: Visitable, distance: Int) = this.classCache.put(visitable, distance)

    fun setSuper(superExpression: SuperExpression, className: String) = this.superCache.put(superExpression, className)

    fun getVar(visitable: Visitable) = this.varCache[visitable]

    fun getFunction(visitable: Visitable) = this.functionCache[visitable]

    fun getClass(visitable: Visitable) = this.classCache[visitable]

    fun getSuper(superExpression: SuperExpression) = this.superCache[superExpression]

    fun mergeWith(other: LocalCache) {
        other.varCache.forEach(this::setVar)
        other.functionCache.forEach(this::setFunction)
        other.classCache.forEach(this::setClass)
        other.superCache.forEach(this::setSuper)
    }
}