package me.senseiwells.arucas.utils

import me.senseiwells.arucas.nodes.Visitable

class LocalCache {
    private val varCache = HashMap<Visitable, Int>()
    private val functionCache = HashMap<Visitable, Int>()
    private val classCache = HashMap<Visitable, Int>()

    fun setVar(visitable: Visitable, distance: Int) = this.varCache.put(visitable, distance)

    fun setFunction(visitable: Visitable, distance: Int) = this.functionCache.put(visitable, distance)

    fun setClass(visitable: Visitable, distance: Int) = this.classCache.put(visitable, distance)

    fun getVar(visitable: Visitable) = this.varCache[visitable]

    fun getFunction(visitable: Visitable) = this.functionCache[visitable]

    fun getClass(visitable: Visitable) = this.classCache[visitable]

    fun mergeWith(other: LocalCache) {
        other.varCache.forEach(this::setVar)
        other.functionCache.forEach(this::setFunction)
        other.classCache.forEach(this::setClass)
    }
}