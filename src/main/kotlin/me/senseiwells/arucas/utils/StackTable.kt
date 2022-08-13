package me.senseiwells.arucas.utils

import me.senseiwells.arucas.classes.ClassDefinition
import me.senseiwells.arucas.classes.ClassInstance

class StackTable(val parent: StackTable? = null) {
    private val definitions by lazy { HashMap<String, ClassDefinition>() }
    private val modules by lazy { HashMap<String, ClassDefinition>() }
    private val variables by lazy { HashMap<String, ClassInstance>() }
    private val functions by lazy { FunctionMap() }

    fun addModule(definition: ClassDefinition) {
        this.modules[definition.name] = definition
    }

    fun getModules(): Collection<ClassDefinition> {
        return this.modules.values
    }

    fun defineClass(definition: ClassDefinition): ClassDefinition? {
        return this.definitions.put(definition.name, definition)
    }

    fun getClass(name: String): ClassDefinition? {
        return this.definitions[name] ?: this.modules[name] ?: this.parent?.getClass(name)
    }

    fun getClass(name: String, distance: Int): ClassDefinition? {
        return this.findAncestor(distance).let { it.definitions[name] ?: it.modules[name] }
    }

    fun getClasses(): Collection<ClassDefinition> {
        return this.definitions.values
    }

    fun defineVar(name: String, value: ClassInstance): ClassInstance? {
        return this.variables.put(name, value)
    }

    fun defineVar(name: String, value: ClassInstance, distance: Int): ClassInstance? {
        return this.findAncestor(distance).defineVar(name, value)
    }

    fun getVar(name: String): ClassInstance? {
        return this.variables[name] ?: this.parent?.getVar(name)
    }

    fun getVar(name: String, distance: Int): ClassInstance? {
        return this.findAncestor(distance).variables[name]
    }

    fun assignVar(name: String, value: ClassInstance): Boolean {
        if (this.variables.containsKey(name)) {
            this.variables[name] = value
            return true
        }
        val parent = this.parent
        return parent != null && parent.assignVar(name, value)
    }

    fun defineFunction(function: ClassInstance): Boolean {
        return this.functions.add(function)
    }

    fun getFunction(name: String, parameters: Int): ClassInstance? {
        return this.functions.get(name, parameters) ?: this.parent?.getFunction(name, parameters)
    }

    fun getFunction(name: String, parameters: Int, distance: Int): ClassInstance? {
        return this.findAncestor(distance).functions.get(name, parameters)
    }

    fun hasFunction(name: String): Boolean {
        return this.functions.has(name) || this.parent?.hasFunction(name) ?: false
    }

    private fun findAncestor(distance: Int): StackTable {
        var current = this
        for (i in 0 until distance) {
            current = current.parent ?:
                throw IllegalArgumentException("Couldn't find ancestor at distance $distance: $this")
        }
        return current
    }
}