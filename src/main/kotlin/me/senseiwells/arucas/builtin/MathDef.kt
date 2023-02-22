package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.annotations.*
import me.senseiwells.arucas.classes.PrimitiveDefinition
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.functions.builtin.BuiltInFunction
import me.senseiwells.arucas.utils.Arguments
import me.senseiwells.arucas.utils.Util.Types.MATH
import kotlin.math.*

@ClassDoc(
    name = MATH,
    desc = ["Provides many basic math functions. This is a utility class, and cannot be constructed"]
)
class MathDef(interpreter: Interpreter): PrimitiveDefinition<Unit>(MATH, interpreter) {
    @FieldDoc(name = "pi", desc = ["The value of pi"], type = NumberDef::class, examples = ["Math.pi;"])
    private val pi = Math.PI

    @FieldDoc(name = "e", desc = ["The value of e"], type = NumberDef::class, examples = ["Math.e;"])
    private val e = Math.E

    @FieldDoc(name = "root2", desc = ["The value of root 2"], type = NumberDef::class, examples = ["Math.root2;"])
    private val root2 = sqrt(2.0)

    override fun canExtend(): Boolean {
        return false
    }

    override fun defineStaticFields(): List<PrimitiveField> {
        return listOf(
            PrimitiveField("pi", this.pi, false),
            PrimitiveField("e", this.e, false),
            PrimitiveField("root2", this.root2, false)
        )
    }

    override fun defineStaticMethods(): List<BuiltInFunction> {
        return listOf(
            BuiltInFunction.of("round", 1, this::round),
            BuiltInFunction.of("ceil", 1, this::ceil),
            BuiltInFunction.of("floor", 1, this::floor),
            BuiltInFunction.of("sqrt", 1, this::sqrt),
            BuiltInFunction.of("abs", 1, this::abs),
            BuiltInFunction.of("mod", 2, this::mod),
            BuiltInFunction.of("rem", 2, this::rem),
            BuiltInFunction.of("max", 2, this::max),
            BuiltInFunction.of("min", 2, this::min),
            BuiltInFunction.of("clamp", 3, this::clamp),
            BuiltInFunction.of("toRadians", 1, this::toRadians),
            BuiltInFunction.of("toDegrees", 1, this::toDegrees),
            BuiltInFunction.of("signum", 1, this::signum),
            BuiltInFunction.of("lerp", 3, this::lerp),
            BuiltInFunction.of("ln", 1, this::ln),
            BuiltInFunction.of("log", 2, this::log),
            BuiltInFunction.of("log10", 1, this::log10),
            BuiltInFunction.of("sin", 1, this::sin),
            BuiltInFunction.of("cos", 1, this::cos),
            BuiltInFunction.of("tan", 1, this::tan),
            BuiltInFunction.of("arcsin", 1, this::arcsin),
            BuiltInFunction.of("arccos", 1, this::arccos),
            BuiltInFunction.of("arctan", 1, this::arctan),
            BuiltInFunction.of("arctan2", 2, this::arctan2),
            BuiltInFunction.of("cosec", 1, this::cosec),
            BuiltInFunction.of("sec", 1, this::sec),
            BuiltInFunction.of("cot", 1, this::cot),
            BuiltInFunction.of("sinh", 1, this::sinh),
            BuiltInFunction.of("cosh", 1, this::cosh),
            BuiltInFunction.of("tanh", 1, this::tanh),
        )
    }

    @FunctionDoc(
        isStatic = true,
        name = "round",
        desc = ["Rounds a number to the nearest integer"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to round."])],
        returns = ReturnDoc(NumberDef::class, ["The rounded number."]),
        examples = ["Math.round(3.5);"]
    )
    private fun round(arguments: Arguments) = round(arguments.nextPrimitive(NumberDef::class))

    @FunctionDoc(
        isStatic = true,
        name = "ceil",
        desc = ["Rounds a number up to the nearest integer"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to round."])],
        returns = ReturnDoc(NumberDef::class, ["The rounded number."]),
        examples = ["Math.ceil(3.5);"]
    )
    private fun ceil(arguments: Arguments) = ceil(arguments.nextPrimitive(NumberDef::class))

    @FunctionDoc(
        isStatic = true,
        name = "floor",
        desc = ["Rounds a number down to the nearest integer"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to round."])],
        returns = ReturnDoc(NumberDef::class, ["The rounded number."]),
        examples = ["Math.floor(3.5);"]
    )
    private fun floor(arguments: Arguments) = floor(arguments.nextPrimitive(NumberDef::class))

    @FunctionDoc(
        isStatic = true,
        name = "sqrt",
        desc = ["Returns the square root of a number"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to square root."])],
        returns = ReturnDoc(NumberDef::class, ["The square root of the number."]),
        examples = ["Math.sqrt(9);"]
    )
    private fun sqrt(arguments: Arguments) = sqrt(arguments.nextPrimitive(NumberDef::class))

    @FunctionDoc(
        isStatic = true,
        name = "abs",
        desc = ["Returns the absolute value of a number"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to get the absolute value of."])],
        returns = ReturnDoc(NumberDef::class, ["The absolute value of the number."]),
        examples = ["Math.abs(-3);"]
    )
    private fun abs(arguments: Arguments) = abs(arguments.nextPrimitive(NumberDef::class))

    @FunctionDoc(
        isStatic = true,
        name = "mod",
        desc = ["Returns the modulus of a division"],
        params = [ParameterDoc(NumberDef::class, "num1", ["The number to divide."]), ParameterDoc(NumberDef::class, "num2", ["The divisor."])],
        returns = ReturnDoc(NumberDef::class, ["The modulus of the division."]),
        examples = ["Math.mod(5, 2);"]
    )
    private fun mod(arguments: Arguments): Double {
        val first = arguments.nextPrimitive(NumberDef::class)
        val second = arguments.nextPrimitive(NumberDef::class)
        return (first % second + second) % second
    }

    @FunctionDoc(
        isStatic = true,
        name = "rem",
        desc = ["Returns the remainder of a division"],
        params = [ParameterDoc(NumberDef::class, "num1", ["The number to divide."]), ParameterDoc(NumberDef::class, "num2", ["The divisor."])],
        returns = ReturnDoc(NumberDef::class, ["The remainder of the division."]),
        examples = ["Math.rem(5, 2);"]
    )
    private fun rem(arguments: Arguments): Double {
        val first = arguments.nextPrimitive(NumberDef::class)
        val second = arguments.nextPrimitive(NumberDef::class)
        return first % second
    }

    @FunctionDoc(
        isStatic = true,
        name = "max",
        desc = ["Returns the largest number"],
        params = [ParameterDoc(NumberDef::class, "num1", ["The first number to compare."]), ParameterDoc(NumberDef::class, "num2", ["The second number to compare."])],
        returns = ReturnDoc(NumberDef::class, ["The largest number."]),
        examples = ["Math.max(5, 2);"]
    )
    private fun max(arguments: Arguments): Double {
        val first = arguments.nextPrimitive(NumberDef::class)
        val second = arguments.nextPrimitive(NumberDef::class)
        return max(first, second)
    }

    @FunctionDoc(
        isStatic = true,
        name = "min",
        desc = ["Returns the smallest number"],
        params = [ParameterDoc(NumberDef::class, "num1", ["The first number to compare."]), ParameterDoc(NumberDef::class, "num2", ["The second number to compare."])],
        returns = ReturnDoc(NumberDef::class, ["The smallest number."]),
        examples = ["Math.min(5, 2);"]
    )
    private fun min(arguments: Arguments): Double {
        val first = arguments.nextPrimitive(NumberDef::class)
        val second = arguments.nextPrimitive(NumberDef::class)
        return min(first, second)
    }

    @FunctionDoc(
        isStatic = true,
        name = "clamp",
        desc = ["Clamps a value between a minimum and maximum"],
        params = [
            ParameterDoc(NumberDef::class, "value", ["The value to clamp."]),
            ParameterDoc(NumberDef::class, "min", ["The minimum."]),
            ParameterDoc(NumberDef::class, "max", ["The maximum."])
        ],
        returns = ReturnDoc(NumberDef::class, ["The clamped value."]),
        examples = ["Math.clamp(10, 2, 8);"]
    )
    private fun clamp(arguments: Arguments): Double {
        val first = arguments.nextPrimitive(NumberDef::class)
        val second = arguments.nextPrimitive(NumberDef::class)
        val third = arguments.nextPrimitive(NumberDef::class)
        return max(min(first, third), second)
    }

    @FunctionDoc(
        isStatic = true,
        name = "toRadians",
        desc = ["Converts a number from degrees to radians"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to convert."])],
        returns = ReturnDoc(NumberDef::class, ["The number in radians."]),
        examples = ["Math.toRadians(90);"]
    )
    private fun toRadians(arguments: Arguments) = Math.toRadians(arguments.nextPrimitive(NumberDef::class))

    @FunctionDoc(
        isStatic = true,
        name = "toDegrees",
        desc = ["Converts a number from radians to degrees"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to convert."])],
        returns = ReturnDoc(NumberDef::class, ["The number in degrees."]),
        examples = ["Math.toDegrees(Math.pi);"]
    )
    private fun toDegrees(arguments: Arguments) = Math.toDegrees(arguments.nextPrimitive(NumberDef::class))

    @FunctionDoc(
        isStatic = true,
        name = "signum",
        desc = [
            "Returns the sign of a number, 1 if the number is positive,",
            "-1 if the number is negative, and 0 if the number is 0"
        ],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to get the sign of."])],
        returns = ReturnDoc(NumberDef::class, ["The sign of the number."]),
        examples = ["Math.signum(3);"]
    )
    private fun signum(arguments: Arguments) = sign(arguments.nextPrimitive(NumberDef::class))

    @FunctionDoc(
        isStatic = true,
        name = "lerp",
        desc = ["Linear interpolation between two numbers"],
        params = [
            ParameterDoc(NumberDef::class, "start", ["The first number."]),
            ParameterDoc(NumberDef::class, "end", ["The second number."]),
            ParameterDoc(NumberDef::class, "delta", ["The interpolation factor."])
        ],
        returns = ReturnDoc(NumberDef::class, ["The interpolated number."]),
        examples = ["Math.lerp(0, 10, 0.5);"]
    )
    private fun lerp(arguments: Arguments): Double {
        val start = arguments.nextPrimitive(NumberDef::class)
        val end = arguments.nextPrimitive(NumberDef::class)
        val delta = arguments.nextPrimitive(NumberDef::class)
        return start + (end - start) * delta
    }

    @FunctionDoc(
        isStatic = true,
        name = "ln",
        desc = ["Returns the natural logarithm of a number"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to get the logarithm of."])],
        returns = ReturnDoc(NumberDef::class, ["The natural logarithm of the number."]),
        examples = ["Math.ln(Math.e);"]
    )
    private fun ln(arguments: Arguments) = ln(arguments.nextPrimitive(NumberDef::class))

    @FunctionDoc(
        isStatic = true,
        name = "log",
        desc = ["Returns the logarithm of a number with a specified base"],
        params = [
            ParameterDoc(NumberDef::class, "base", ["The base."]),
            ParameterDoc(NumberDef::class, "num", ["The number to get the logarithm of."])
        ],
        returns = ReturnDoc(NumberDef::class, ["The logarithm of the number."]),
        examples = ["Math.log(2, 4);"]
    )
    private fun log(arguments: Arguments): Double {
        val base = arguments.nextPrimitive(NumberDef::class)
        val value = arguments.nextPrimitive(NumberDef::class)
        return log(value, base)
    }

    @FunctionDoc(
        isStatic = true,
        name = "log10",
        desc = ["Returns the base 10 logarithm of a number"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to get the logarithm of."])],
        returns = ReturnDoc(NumberDef::class, ["The base 10 logarithm of the number."]),
        examples = ["Math.log10(100);"]
    )
    private fun log10(arguments: Arguments) = log10(arguments.nextPrimitive(NumberDef::class))

    @FunctionDoc(
        isStatic = true,
        name = "sin",
        desc = ["Returns the sine of a number"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to get the sine of."])],
        returns = ReturnDoc(NumberDef::class, ["The sine of the number."]),
        examples = ["Math.sin(Math.pi);"]
    )
    private fun sin(arguments: Arguments) = sin(arguments.nextPrimitive(NumberDef::class))

    @FunctionDoc(
        isStatic = true,
        name = "cos",
        desc = ["Returns the cosine of a number"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to get the cosine of."])],
        returns = ReturnDoc(NumberDef::class, ["The cosine of the number."]),
        examples = ["Math.cos(Math.pi);"]
    )
    private fun cos(arguments: Arguments) = cos(arguments.nextPrimitive(NumberDef::class))

    @FunctionDoc(
        isStatic = true,
        name = "tan",
        desc = ["Returns the tangent of a number"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to get the tangent of."])],
        returns = ReturnDoc(NumberDef::class, ["The tangent of the number."]),
        examples = ["Math.tan(Math.pi);"]
    )
    private fun tan(arguments: Arguments) = tan(arguments.nextPrimitive(NumberDef::class))

    @FunctionDoc(
        isStatic = true,
        name = "arcsin",
        desc = ["Returns the arc sine of a number"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to get the arc sine of."])],
        returns = ReturnDoc(NumberDef::class, ["The arc sine of the number."]),
        examples = ["Math.arcsin(Math.sin(Math.pi));"]
    )
    private fun arcsin(arguments: Arguments) = asin(arguments.nextPrimitive(NumberDef::class))

    @FunctionDoc(
        isStatic = true,
        name = "arccos",
        desc = ["Returns the arc cosine of a number"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to get the arc cosine of."])],
        returns = ReturnDoc(NumberDef::class, ["The arc cosine of the number."]),
        examples = ["Math.arccos(Math.cos(Math.pi));"]
    )
    private fun arccos(arguments: Arguments) = acos(arguments.nextPrimitive(NumberDef::class))

    @FunctionDoc(
        isStatic = true,
        name = "arctan",
        desc = ["Returns the arc tangent of a number"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to get the arc tangent of."])],
        returns = ReturnDoc(NumberDef::class, ["The arc tangent of the number."]),
        examples = ["Math.arctan(Math.tan(Math.pi));"]
    )
    private fun arctan(arguments: Arguments) = atan(arguments.nextPrimitive(NumberDef::class))

    @FunctionDoc(
        isStatic = true,
        name = "arctan2",
        desc = [
            "Returns the angle theta of the polar coordinates (r, theta) that correspond to the rectangular",
            "coordinates (x, y) by computing the arc tangent of the value y / x"
        ],
        params = [
            ParameterDoc(NumberDef::class, "y", ["The ordinate coordinate."]),
            ParameterDoc(NumberDef::class, "x", ["The abscissa coordinate."])
        ],
        returns = ReturnDoc(NumberDef::class, ["The theta component of the point (r, theta)."]),
        examples = ["Math.arctan2(Math.tan(Math.pi), Math.cos(Math.pi)); // -3.141592"]
    )
    private fun arctan2(arguments: Arguments) = atan2(arguments.nextPrimitive(NumberDef::class), arguments.nextPrimitive(NumberDef::class))

    @FunctionDoc(
        isStatic = true,
        name = "cosec",
        desc = ["Returns the cosecant of a number"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to get the cosecant of."])],
        returns = ReturnDoc(NumberDef::class, ["The cosecant of the number."]),
        examples = ["Math.cosec(Math.pi);"]
    )
    private fun cosec(arguments: Arguments) = 1.0 / sin(arguments.nextPrimitive(NumberDef::class))

    @FunctionDoc(
        isStatic = true,
        name = "sec",
        desc = ["Returns the secant of a number"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to get the secant of."])],
        returns = ReturnDoc(NumberDef::class, ["The secant of the number."]),
        examples = ["Math.sec(Math.pi);"]
    )
    private fun sec(arguments: Arguments) = 1.0 / cos(arguments.nextPrimitive(NumberDef::class))

    @FunctionDoc(
        isStatic = true,
        name = "cot",
        desc = ["Returns the cotangent of a number"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to get the cotangent of."])],
        returns = ReturnDoc(NumberDef::class, ["The cotangent of the number."]),
        examples = ["Math.cot(Math.pi);"]
    )
    private fun cot(arguments: Arguments) = 1.0 / tan(arguments.nextPrimitive(NumberDef::class))

    @FunctionDoc(
        isStatic = true,
        name = "sinh",
        desc = ["Returns the hyperbolic sine of a number"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to get the hyperbolic sine of."])],
        returns = ReturnDoc(NumberDef::class, ["The hyperbolic sine of the number."]),
        examples = ["Math.sinh(1);"]
    )
    private fun sinh(arguments: Arguments) = sinh(arguments.nextPrimitive(NumberDef::class))

    @FunctionDoc(
        isStatic = true,
        name = "cosh",
        desc = ["Returns the hyperbolic cosine of a number"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to get the hyperbolic cosine of."])],
        returns = ReturnDoc(NumberDef::class, ["The hyperbolic cosine of the number."]),
        examples = ["Math.cosh(1);"]
    )
    private fun cosh(arguments: Arguments) = cosh(arguments.nextPrimitive(NumberDef::class))

    @FunctionDoc(
        isStatic = true,
        name = "tanh",
        desc = ["Returns the hyperbolic tangent of a number"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to get the hyperbolic tangent of."])],
        returns = ReturnDoc(NumberDef::class, ["The hyperbolic tangent of the number."]),
        examples = ["Math.tanh(1);"]
    )
    private fun tanh(arguments: Arguments) = tanh(arguments.nextPrimitive(NumberDef::class))
}