package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.annotations.ClassDoc
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc
import me.senseiwells.arucas.api.docs.annotations.ReturnDoc
import me.senseiwells.arucas.classes.CreatableDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.core.Type
import me.senseiwells.arucas.functions.builtin.MemberFunction
import me.senseiwells.arucas.utils.Arguments
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.utils.StringUtils
import me.senseiwells.arucas.utils.Util.Types.NUMBER
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToInt

@ClassDoc(
    name = NUMBER,
    desc = [
        "This class cannot be constructed as it has a literal representation.",
        "For math related functions see the Math class."
    ]
)
class NumberDef(interpreter: Interpreter): CreatableDefinition<Double>(NUMBER, interpreter) {
    private companion object {
        val DECIMAL_FORMAT = DecimalFormat("#.############", DecimalFormatSymbols.getInstance(Locale.UK))
    }

    fun literal(string: String) = this.create(StringUtils.parseNumber(string))

    override fun canExtend() = false

    override fun plus(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): ClassInstance {
        return instance
    }

    override fun minus(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): Double {
        return -instance.asPrimitive(this)
    }

    override fun plus(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        val otherNumber = other.getPrimitive(this) ?: return super.plus(instance, interpreter, other, trace)
        return instance.asPrimitive(this) + otherNumber
    }

    override fun minus(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        val otherNumber = other.getPrimitive(this) ?: return super.minus(instance, interpreter, other, trace)
        return instance.asPrimitive(this) - otherNumber
    }

    override fun multiply(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        val otherNumber = other.getPrimitive(this) ?: return super.multiply(instance, interpreter, other, trace)
        return instance.asPrimitive(this) * otherNumber
    }

    override fun divide(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        val otherNumber = other.getPrimitive(this) ?: return super.divide(instance, interpreter, other, trace)
        return instance.asPrimitive(this) / otherNumber
    }

    override fun power(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        val otherNumber = other.getPrimitive(this) ?: return super.power(instance, interpreter, other, trace)
        return instance.asPrimitive(this).pow(otherNumber)
    }

    override fun bitAnd(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        val otherNumber = other.getPrimitive(this) ?: return super.bitAnd(instance, interpreter, other, trace)
        return instance.asPrimitive(this).toLong() and otherNumber.toLong()
    }

    override fun bitOr(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        val otherNumber = other.getPrimitive(this) ?: return super.bitOr(instance, interpreter, other, trace)
        return instance.asPrimitive(this).toLong() or otherNumber.toLong()
    }

    override fun xor(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        val otherNumber = other.getPrimitive(this) ?: return super.xor(instance, interpreter, other, trace)
        return instance.asPrimitive(this).toLong() xor otherNumber.toLong()
    }

    override fun shiftLeft(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        val otherNumber = other.getPrimitive(this) ?: return super.shiftLeft(instance, interpreter, other, trace)
        return instance.asPrimitive(this).toLong() shl otherNumber.toInt()
    }

    override fun shiftRight(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        val otherNumber = other.getPrimitive(this) ?: return super.shiftRight(instance, interpreter, other, trace)
        return instance.asPrimitive(this).toLong() shr otherNumber.toInt()
    }

    override fun compare(instance: ClassInstance, interpreter: Interpreter, type: Type, other: ClassInstance, trace: LocatableTrace): Any? {
        val otherNumber = other.getPrimitive(this) ?: return super.compare(instance, interpreter, type, other, trace)
        return when (type) {
            Type.LESS_THAN -> instance.asPrimitive(this) < otherNumber
            Type.LESS_THAN_EQUAL -> instance.asPrimitive(this) <= otherNumber
            Type.MORE_THAN -> instance.asPrimitive(this) > otherNumber
            Type.MORE_THAN_EQUAL -> instance.asPrimitive(this) >= otherNumber
            else -> super.compare(instance, interpreter, type, other, trace)
        }
    }

    override fun toString(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): String {
        return DECIMAL_FORMAT.format(instance.getPrimitive(this))
    }

    override fun defineMethods(): List<MemberFunction> {
        return listOf(
            MemberFunction.of("round", this::round),
            MemberFunction.of("ceil", this::ceil),
            MemberFunction.of("floor", this::floor),
            MemberFunction.of("isInfinite", this::isInfinite),
            MemberFunction.of("isNaN", this::isNan)
        )
    }

    @FunctionDoc(
        name = "round",
        desc = ["This allows you to round a number to the nearest integer"],
        returns = ReturnDoc(NumberDef::class, ["The rounded number."]),
        examples = ["3.5.round();"]
    )
    private fun round(arguments: Arguments): Int {
        return arguments.nextPrimitive(this).roundToInt()
    }

    @FunctionDoc(
        name = "ceil",
        desc = ["This allows you to round a number up to the nearest integer"],
        returns = ReturnDoc(NumberDef::class, ["The rounded number."]),
        examples = ["3.5.ceil();"]
    )
    private fun ceil(arguments: Arguments): Double {
        return ceil(arguments.nextPrimitive(this))
    }

    @FunctionDoc(
        name = "floor",
        desc = ["This allows you to round a number down to the nearest integer"],
        returns = ReturnDoc(NumberDef::class, ["The rounded number."]),
        examples = ["3.5.floor();"]
    )
    private fun floor(arguments: Arguments): Double {
        return floor(arguments.nextPrimitive(this))
    }

    @FunctionDoc(
        name = "isInfinite",
        desc = ["This allows you to check if a number is infinite"],
        returns = ReturnDoc(BooleanDef::class, ["True if the number is infinite."]),
        examples = ["(1/0).isInfinite();"]
    )
    private fun isInfinite(arguments: Arguments): Boolean {
        return arguments.nextPrimitive(this).isInfinite()
    }

    @FunctionDoc(
        name = "isNaN",
        desc = ["This allows you to check if a number is not a number"],
        returns = ReturnDoc(BooleanDef::class, ["True if the number is not a number."]),
        examples = ["(0/0).isNaN();"]
    )
    private fun isNan(arguments: Arguments): Boolean {
        return arguments.nextPrimitive(this).isNaN()
    }
}