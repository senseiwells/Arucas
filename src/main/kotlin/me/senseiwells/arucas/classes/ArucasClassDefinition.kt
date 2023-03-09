package me.senseiwells.arucas.classes

import me.senseiwells.arucas.builtin.BooleanDef
import me.senseiwells.arucas.builtin.NumberDef
import me.senseiwells.arucas.builtin.StringDef
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.core.Type
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.nodes.expressions.Expression
import me.senseiwells.arucas.typed.HintedParameter
import me.senseiwells.arucas.typed.HintedVariable
import me.senseiwells.arucas.utils.*

open class ArucasClassDefinition(
    name: String,
    interpreter: Interpreter,
    private val localTable: StackTable,
    private val superclass: ClassDefinition?,
    private val interfaces: Set<InterfaceDefinition>?
): ClassDefinition(name, interpreter) {
    val fields = lazy { ArrayList<HintedVariable>() }
    val operators = lazy { OperatorMap() }

    init {
        this.constructors.value // Unlazy
    }

    override fun init(interpreter: Interpreter, instance: ClassInstance, args: MutableList<ClassInstance>, trace: CallTrace) {
        if (this.fields.isInitialized()) {
            for (hinted in this.fields.value) {
                val field = hinted.create(interpreter, this.localTable, trace)
                instance.addInstanceField(hinted.name, field)
            }
        }

        super.init(interpreter, instance, args, trace)

        for (field in instance.getInstanceFields()) {
            field.finalise(trace)
        }
    }

    override fun canConstructDirectly(): Boolean {
        return true
    }

    override fun superclass(): ClassDefinition {
        return this.superclass ?: super.superclass()
    }

    override fun interfaces(): Set<InterfaceDefinition> {
        return this.interfaces ?: super.interfaces()
    }

    override fun asJavaValue(instance: ClassInstance): ClassInstance {
        return instance
    }

    override fun isInClass(interpreter: Interpreter): Boolean {
        return interpreter.isWithinStack(this.localTable)
    }

    override fun hasMemberFunction(instance: ClassInstance, name: String, parameters: Int): Boolean {
        return this.methods.isInitialized() && this.methods.value.has(name, parameters)
    }

    override fun bracketAccess(instance: ClassInstance, interpreter: Interpreter, index: ClassInstance, trace: LocatableTrace): ClassInstance {
        if (this.operators.isInitialized()) {
            this.operators.value.get(Type.LEFT_SQUARE_BRACKET, 2)?.let {
                return interpreter.call(it, listOf(instance, index),
                    CallTrace(trace, "${instance.definition.name}[${index.definition.name}]")
                )
            }
        }
        return super.bracketAccess(instance, interpreter, index, trace)
    }

    override fun bracketAssign(instance: ClassInstance, interpreter: Interpreter, index: ClassInstance, assignee: ClassInstance, trace: LocatableTrace): ClassInstance {
        if (this.operators.isInitialized()) {
            this.operators.value.get(Type.LEFT_SQUARE_BRACKET, 3)?.let {
                return interpreter.call(it, listOf(instance, index, assignee),
                    CallTrace(
                        trace,
                        "${instance.definition.name}[${index.definition.name}] = ${assignee.definition.name}"
                    )
                )
            }
        }
        return super.bracketAssign(instance, interpreter, index, assignee, trace)
    }

    override fun unary(instance: ClassInstance, interpreter: Interpreter, type: Type, trace: LocatableTrace): ClassInstance {
        if (this.operators.isInitialized()) {
            this.operators.value.get(type, 1)?.let {
                return interpreter.call(it, listOf(instance), CallTrace(trace, "$type${instance.definition.name}"))
            }
        }
        return super.unary(instance, interpreter, type, trace)
    }

    override fun binary(instance: ClassInstance, interpreter: Interpreter, type: Type, other: ClassInstance, trace: LocatableTrace): ClassInstance {
        if (this.operators.isInitialized()) {
            this.operators.value.get(type, 2)?.let {
                return interpreter.call(it, listOf(instance, other),
                    CallTrace(trace, "${instance.definition.name} $type ${other.definition.name}")
                )
            }
        }
        return super.binary(instance, interpreter, type, other, trace)
    }

    override fun copy(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): ClassInstance {
        if (instance.definition.hasMemberFunction(instance, "copy", 1)) {
            return instance.callMember(interpreter, "copy", listOf(), instance.definition, trace)
        }
        return super.copy(instance, interpreter, trace)
    }

    override fun equals(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Boolean {
        if (this.operators.isInitialized()) {
            this.operators.value.get(Type.EQUALS, 2)?.let {
                val callTrace = CallTrace(trace, "${instance.definition.name} == ${other.definition.name}")
                val returnVal = interpreter.call(it, listOf(instance, other), callTrace)
                return returnVal.getPrimitive(BooleanDef::class) ?: runtimeError("Expected '==' operator to return a Boolean")
            }
        }
        return super.equals(instance, interpreter, other, trace)
    }

    override fun hashCode(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): Int {
        if (this.hasMemberFunction(instance, "hashCode", 1)) {
            return instance.callMemberPrimitive(interpreter, "hashCode", listOf(), NumberDef::class, trace).toInt()
        }
        return super.hashCode(instance, interpreter, trace)
    }

    override fun toString(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): String {
        if (this.hasMemberFunction(instance, "toString", 1)) {
            return instance.callMemberPrimitive(interpreter, "toString", listOf(), StringDef::class, trace)
        }
        return super.toString(instance, interpreter, trace)
    }
}