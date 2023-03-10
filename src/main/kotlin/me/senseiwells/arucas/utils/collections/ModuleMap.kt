package me.senseiwells.arucas.utils.collections

import me.senseiwells.arucas.classes.ClassDefinition

/**
 * This class can be access over multiple
 * threads thus all the methods are synchronized.
 *
 * We do not provide any ways of accessing
 * the map directly as that could lead to a [ConcurrentModificationException].
 */
class ModuleMap {
    private val builtIns = HashMap<String, ClassDefinition>()
    private val map = HashMap<String, HashMap<String, ClassDefinition>>()
    private val tried = HashSet<String>()

    private val lazy = HashMap<String, () -> Unit>()

    @Synchronized
    fun addBuiltIn(definition: ClassDefinition) {
        this.builtIns[definition.name] = definition
    }

    @Synchronized
    fun addLazy(importPath: String, lazy: () -> Unit) {
        this.lazy[importPath] = lazy
    }

    @Synchronized
    fun add(importPath: String, definition: ClassDefinition) {
        val definitions = this.map.getOrPut(importPath) { HashMap() }
        definitions[definition.name] = definition
        this.tried.add(importPath)
    }

    @Synchronized
    fun has(importPath: String): Boolean {
        return this.map.containsKey(importPath) || this.lazy.containsKey(importPath)
    }

    @Synchronized
    fun tried(importPath: String): Boolean {
        return this.tried.contains(importPath).also { this.tried.add(importPath) }
    }

    @Synchronized
    fun get(importPath: String, name: String): ClassDefinition? {
        this.lazy[importPath]?.invoke()?.let { this.lazy.remove(importPath) }
        return this.map[importPath]?.get(name)
    }

    @Synchronized
    fun getBuiltIn(name: String): ClassDefinition? {
        return this.builtIns[name]
    }

    fun builtIns(): MutableCollection<ClassDefinition> {
        return this.builtIns.values
    }

    fun forEach(function: (String, Collection<ClassDefinition>) -> Unit) {
        this.map.forEach { (path, defs) -> function(path, defs.values) }
    }
}