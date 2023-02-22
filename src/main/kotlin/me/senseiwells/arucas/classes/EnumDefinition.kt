package me.senseiwells.arucas.classes

import me.senseiwells.arucas.builtin.EnumDef
import me.senseiwells.arucas.builtin.FunctionDef
import me.senseiwells.arucas.builtin.StringDef
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.functions.builtin.BuiltInFunction
import me.senseiwells.arucas.utils.*
import me.senseiwells.arucas.utils.impl.ArucasEnum
import me.senseiwells.arucas.utils.impl.ArucasList

class EnumDefinition(
    name: String,
    interpreter: Interpreter,
    localTable: StackTable,
    interfaces: Set<InterfaceDefinition>?
): ArucasClassDefinition(name, interpreter, localTable, interpreter.getPrimitive(EnumDef::class), interfaces) {
    private val enums = lazy { HashMap<String, ClassInstance>() }

    init {
        val valuesMethod = BuiltInFunction.of("values", this::values)
        val fromStringMethod = BuiltInFunction.of("fromString", this::fromString)
        this.staticMethods.value.add(this.interpreter.create(FunctionDef::class, valuesMethod))
        this.staticMethods.value.add(this.interpreter.create(FunctionDef::class, fromStringMethod))
    }

    fun addEnum(interpreter: Interpreter, name: String, arguments: MutableList<ClassInstance>, trace: LocatableTrace) {
        val callTrace = CallTrace(trace, "new ${this.name}::${arguments.size}")
        val enum = ClassInstance(this)
        enum.setPrimitive(this.superclass(), ArucasEnum(name, this.enums.value.size))
        this.init(interpreter, enum, arguments, callTrace)
        this.enums.value[name] = enum
    }

    fun getEnum(name: String): ClassInstance? {
        return if (this.enums.isInitialized()) this.enums.value[name] else null;
    }

    fun getNames(): Collection<String> {
        return if (!this.enums.isInitialized()) listOf() else this.enums.value.keys
    }

    override fun canExtend(): Boolean {
        return false
    }

    override fun superclass(): EnumDef {
        return super.superclass() as EnumDef
    }

    override fun accessConstructor(trace: Trace): Nothing {
        runtimeError("Enums cannot be constructed", trace)
    }

    override fun callConstructor(interpreter: Interpreter, args: MutableList<ClassInstance>, trace: CallTrace): Nothing {
        this.accessConstructor(trace)
    }

    override fun staticMemberAccess(interpreter: Interpreter, name: String, trace: LocatableTrace): ClassInstance {
        if (this.enums.isInitialized()) {
            this.enums.value[name]?.let { return it }
        }
        return super.staticMemberAccess(interpreter, name, trace)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun values(arguments: Arguments): ArucasList {
        val list = ArucasList()
        if (this.enums.isInitialized()) {
            list.addAll(this.enums.value.values)
        }
        return list
    }

    private fun fromString(arguments: Arguments): ClassInstance {
        val name = arguments.nextPrimitive(StringDef::class)
        return this.getEnum(name) ?: arguments.interpreter.getNull()
    }
}