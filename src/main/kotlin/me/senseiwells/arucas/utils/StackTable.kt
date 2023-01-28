package me.senseiwells.arucas.utils

import me.senseiwells.arucas.classes.ClassDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance

/**
 * This class holds all the data in a given scopes. It also holds
 * a reference to its parent to be able to find data in previous scopes.
 *
 * This class holds variables, functions, classes, and imported modules.
 *
 * @param moduleMap the existing modules.
 * @param parent the stack's parent, null if this is global.
 */
class StackTable internal constructor(
    /**
     * The existing modules.
     */
    private val moduleMap: ModuleMap,
    /**
     * The stack's parent, null if this is global.
     */
    val parent: StackTable? = null
) {
    /**
     * Map of class definitions in this stack.
     */
    private val definitions by lazy { HashMap<String, ClassDefinition>() }

    /**
     * Map of variables in this stack.
     */
    private val variables by lazy { HashMap<String, ClassInstance>() }

    /**
     * Map of functions in this stack.
     *
     * @see FunctionMap
     */
    private val functions by lazy { FunctionMap() }

    /**
     * Map of promised classes from a given module.
     */
    private val promised by lazy { HashMap<String, String>() }

    /**
     * Set of modules that have been completely imported with `*`
     */
    private val all by lazy { LinkedHashSet<String>() }

    /**
     * Adds a class definition to the stack.
     *
     * @param definition the definition to add.
     */
    fun defineClass(definition: ClassDefinition) {
        this.definitions[definition.name] = definition
    }

    /**
     * Tries to get a class definition from modules.
     *
     * @param name the name of the class.
     * @return the class definition, `null` if not found.
     */
    fun getClassFromModules(name: String): ClassDefinition? {
        val definition = this.promised[name]?.let { this.moduleMap.get(it, name) }
        if (definition == null) {
            this.moduleMap.getBuiltIn(name)?.let { return it }
            for (import in this.all) {
                this.moduleMap.get(import, name)?.let {
                    this.promised[name] = import
                    return it
                }
            }
        }
        return definition
    }

    /**
     * Makes a promise that a class belongs to a specific module.
     *
     * @param name the name of the class.
     * @param path the module import path.
     */
    fun addModule(name: String, path: String) {
        this.promised[name] = path
    }

    /**
     * Adds all the classes from a module.
     *
     * @param path the module import path.
     */
    fun addAllModules(path: String) {
        this.all.add(path)
    }

    /**
     * Gets a class definition from the stack. This will
     * search modules as well as parent stacks for the class.
     *
     * @param name name of the class.
     * @return the class definition, `null` if not found.
     */
    fun getClass(name: String): ClassDefinition? {
        return this.definitions[name] ?: this.getClassFromModules(name) ?: this.parent?.getClass(name)
    }

    /**
     * Gets a class from a specific ancestor stack, this saves searching all the parents.
     *
     * @param name name of the class.
     * @param distance the number of stacks to traverse before finding the class.
     * @return the class definition, `null` if not found.
     */
    fun getClass(name: String, distance: Int): ClassDefinition? {
        return this.findAncestor(distance).let { it.definitions[name] ?: it.getClassFromModules(name) }
    }

    /**
     * Adds a variable to the stack.
     *
     * @param name the name of the variable.
     * @param value the value that the variable is storing.
     * @return the value which was previously stored in the variable, may be null.
     */
    fun defineVar(name: String, value: ClassInstance): ClassInstance? {
        return this.variables.put(name, value)
    }

    /**
     * Adds a variable to a specific ancestor stack.
     *
     * @param name the name of the variable.
     * @param value the value that the variable is storing.
     * @param distance the number of stacks to traverse before defining the variable.
     * @return the value which was previously stored in the variable, may be null.
     */
    fun defineVar(name: String, value: ClassInstance, distance: Int): ClassInstance? {
        return this.findAncestor(distance).defineVar(name, value)
    }

    /**
     * Gets the value of a variable. This will
     * also search parent stacks for the variable.
     *
     * @param name the name of the variable.
     * @return the value of the variable, may be null if not found.
     */
    fun getVar(name: String): ClassInstance? {
        return this.variables[name] ?: this.parent?.getVar(name)
    }

    /**
     * Gets the value of the variable at a specific ancestor stack.
     *
     * @param name the name of the variable.
     * @param distance the number of stacks to traverse before finding the variable.
     * @return the value of the variable, may be null if not found.
     */
    fun getVar(name: String, distance: Int): ClassInstance? {
        return this.findAncestor(distance).variables[name]
    }

    /**
     * Tries to assign a variable from the current stack.
     * This will only set the variable if it is already defined,
     * and it will search parent stacks until it hits the global.
     *
     * @param name the name of the variable.
     * @param value the value to set the variable to.
     * @return whether the variable was assigned.
     */
    fun assignVar(name: String, value: ClassInstance): Boolean {
        if (this.variables.containsKey(name)) {
            this.variables[name] = value
            return true
        }
        val parent = this.parent
        return parent != null && parent.assignVar(name, value)
    }

    /**
     * This defines a function to the current stack.
     *
     * @param function the function [ClassInstance].
     * @return true if no function was overwritten, false otherwise.
     */
    fun defineFunction(function: ClassInstance): Boolean {
        return this.functions.add(function)
    }

    /**
     * This gets a function from the current stack with a given
     * name and number of parameters. This will also search parent stacks.
     *
     * @param name the name of the function.
     * @param parameters the number of parameters the function has.
     * @return the function [ClassInstance], null if not found.
     */
    fun getFunction(name: String, parameters: Int): ClassInstance? {
        return this.functions.get(name, parameters) ?: this.parent?.getFunction(name, parameters)
    }

    /**
     * This gets a function from a specific ancestor stack.
     *
     * @param name the name of the function.
     * @param parameters the number of parameters the function has.
     * @return the function [ClassInstance], null if not found.
     */
    fun getFunction(name: String, parameters: Int, distance: Int): ClassInstance? {
        return this.findAncestor(distance).functions.get(name, parameters)
    }

    /**
     * Checks whether the stack has a function, this also checks parent stacks.
     *
     * @param name name of the function.
     * @return whether a function with the given name exists.
     */
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