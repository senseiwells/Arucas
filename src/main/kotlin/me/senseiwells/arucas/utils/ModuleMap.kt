package me.senseiwells.arucas.utils

import me.senseiwells.arucas.classes.ClassDefinition

/**
 * This class can be access over multiple
 * threads thus all the methods are synchronized.
 *
 * We do not provide any ways of accessing
 * the map directly as that could lead to a CME.
 */
class ModuleMap {
    private val map = HashMap<String, HashMap<String, ClassDefinition>>()
    private val tried = HashSet<String>()

    @Synchronized
    fun add(importPath: String, definition: ClassDefinition) {
        val definitions = this.map.getOrPut(importPath) { HashMap() }
        definitions[definition.name] = definition
        this.tried.add(importPath)
    }

    @Synchronized
    fun has(importPath: String): Boolean {
        return this.map.containsKey(importPath)
    }

    @Synchronized
    fun tried(importPath: String): Boolean {
        return this.tried.contains(importPath).also { this.tried.add(importPath) }
    }

    @Synchronized
    fun get(importPath: String, name: String): ClassDefinition? {
        return this.map[importPath]?.get(name)
    }

    @Synchronized
    fun forEach(importPath: String, function: (ClassDefinition) -> Unit) {
        this.map[importPath]?.values?.forEach(function)
    }

    @Synchronized
    fun forEach(function: (String, Collection<ClassDefinition>) -> Unit) {
        this.map.forEach { (path, defs) -> function(path, defs.values) }
    }
}