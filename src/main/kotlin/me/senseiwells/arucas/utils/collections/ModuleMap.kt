package me.senseiwells.arucas.utils.collections

import me.senseiwells.arucas.classes.ClassDefinition
import me.senseiwells.arucas.interpreter.Interpreter

/**
 * This class is used between an [Interpreter] and all of
 * its children and branches to access the built-in and
 * imported classes to not have to load them multiple times.
 *
 * This map implementation also supports for lazy importing
 * allowing the [Interpreter] to import a file only when it
 * needs to be accessed which allows for circular imports.
 *
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

    /**
     * Adds a built-in class definition.
     *
     * @param definition the built-in class definition.
     */
    @Synchronized
    fun addBuiltIn(definition: ClassDefinition) {
        this.builtIns[definition.name] = definition
    }

    /**
     * Adds a lazy import. This usually runs the script
     * which will add to the map in its execution.
     *
     * @param importPath the import path that is being lazily imported.
     * @param lazy the function to executed when needed.
     */
    @Synchronized
    fun addLazy(importPath: String, lazy: () -> Unit) {
        this.lazy[importPath] = lazy
    }

    /**
     * Adds a class definition to the map.
     *
     * @param importPath the import path of the class.
     * @param definition the class definition.
     */
    @Synchronized
    fun add(importPath: String, definition: ClassDefinition) {
        val definitions = this.map.getOrPut(importPath) { HashMap() }
        definitions[definition.name] = definition
        this.tried.add(importPath)
    }

    /**
     * Checks whether the map has a given import path.
     * This checks both currently available classes and lazy classes.
     *
     * @param importPath the import path of the file.
     * @return whether it can be imported.
     */
    @Synchronized
    fun has(importPath: String): Boolean {
        return this.map.containsKey(importPath) || this.lazy.containsKey(importPath)
    }

    /**
     * Checks whether a given import path has tried to be imported previously.
     *
     * @param importPath the import path of the file.
     * @return whether it has tried to be imported previously.
     */
    @Synchronized
    fun tried(importPath: String): Boolean {
        return this.tried.contains(importPath).also { this.tried.add(importPath) }
    }

    /**
     * This gets a class definition for a given import path and name.
     *
     * @param importPath the import path for the class.
     * @param name the name of the class.
     * @return the class definition, null if not found.
     */
    @Synchronized
    fun get(importPath: String, name: String): ClassDefinition? {
        this.lazy[importPath]?.invoke()?.let { this.lazy.remove(importPath) }
        return this.map[importPath]?.get(name)
    }

    /**
     * This gets a build-in class definition.
     *
     * @param name the name of the class.
     * @return the class definition, null if not found.
     */
    @Synchronized
    fun getBuiltIn(name: String): ClassDefinition? {
        return this.builtIns[name]
    }

    /**
     * This gets a collection of all the built-in definitions.
     *
     * @return the collection of built-ins.
     */
    internal fun builtIns(): MutableCollection<ClassDefinition> {
        return this.builtIns.values
    }

    /**
     * This iterators over all the importable class definitions.
     *
     * @param function the consumer accepting the path of the classes and the collection of classes in that path.
     */
    internal fun forEach(function: (String, Collection<ClassDefinition>) -> Unit) {
        this.map.forEach { (path, defs) -> function(path, defs.values) }
    }
}