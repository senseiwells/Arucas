package me.senseiwells.arucas.extensions

import me.senseiwells.arucas.api.docs.annotations.ClassDoc
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc
import me.senseiwells.arucas.api.docs.annotations.ParameterDoc
import me.senseiwells.arucas.api.docs.annotations.ReturnDoc
import me.senseiwells.arucas.builtin.*
import me.senseiwells.arucas.classes.ClassDefinition
import me.senseiwells.arucas.classes.CreatableDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.RuntimeError
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.utils.*
import me.senseiwells.arucas.utils.Util.Types.JAVA
import net.bytebuddy.ByteBuddy
import net.bytebuddy.implementation.MethodDelegation
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier

@ClassDoc(
    name = JAVA,
    desc = [
        "This class wraps Java values allowing for interactions between Java and Arucas.",
        "This class cannot be instantiated or extended but you can create Java values by",
        "using the static method 'Java.valueOf()' to convert Arucas to Java."
    ]
)
class JavaDef(interpreter: Interpreter): CreatableDefinition<Any>(JAVA, interpreter) {
    private companion object Null

    override fun canExtend() = false

    fun createNullable(any: Any?): ClassInstance {
        return this.create(any ?: Null)
    }

    override fun asJavaValue(instance: ClassInstance): Any? {
        val value = instance.asPrimitive(this)
        return if (value != Null) value else null
    }

    override fun memberFunctionAccess(instance: ClassInstance, name: String, args: MutableList<ClassInstance>, trace: Trace, origin: ClassDefinition): ClassInstance {
        if (!this.hasMemberFunction(name, args.size)) {
            val java = this.asJavaNotNull(instance, trace)
            return this.interpreter.create(FunctionDef::class, BuiltInFunction.java(java::class.java, java, name))
        }
        return super.memberFunctionAccess(instance, name, args, trace, origin)
    }

    override fun memberAccess(instance: ClassInstance, interpreter: Interpreter, name: String, trace: LocatableTrace): ClassInstance {
        val java = this.asJavaNotNull(instance, trace)
        return this.createNullable(ReflectionUtils.getField(java::class.java, java, name, interpreter.api.getObfuscator()))
    }

    override fun memberAssign(instance: ClassInstance, name: String, assignee: ClassInstance, trace: Trace): ClassInstance {
        val java = this.asJavaNotNull(instance, trace)
        ReflectionUtils.setField(java::class.java, java, assignee, name, this.interpreter.api.getObfuscator())
        return assignee
    }

    override fun not(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): Any? {
        val result = JavaUtils.not(this.asJavaNotNull(instance, trace), Null)
        return if (result == Null) super.not(instance, interpreter, trace) else this.create(result)
    }

    override fun plus(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): Any? {
        val result = JavaUtils.plus(this.asJavaNotNull(instance, trace), Null)
        return if (result == Null) super.plus(instance, interpreter, trace) else this.create(result)
    }

    override fun minus(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): Any? {
        val result = JavaUtils.minus(this.asJavaNotNull(instance, trace), Null)
        return if (result == Null) super.minus(instance, interpreter, trace) else this.create(result)
    }

    override fun plus(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        val result = JavaUtils.add(this.asJavaNotNull(instance, trace), other.asJava(), Null)
        return if (result == Null) super.plus(instance, interpreter, other, trace) else this.create(result)
    }

    override fun minus(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        val result = JavaUtils.minus(this.asJavaNotNull(instance, trace), other.asJava(), Null)
        return if (result == Null) super.minus(instance, interpreter, other, trace) else this.create(result)
    }

    override fun multiply(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        val result = JavaUtils.multiply(this.asJavaNotNull(instance, trace), other.asJava(), Null)
        return if (result == Null) super.multiply(instance, interpreter, other, trace) else this.create(result)
    }

    override fun divide(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        val result = JavaUtils.divide(this.asJavaNotNull(instance, trace), other.asJava(), Null)
        return if (result == Null) super.divide(instance, interpreter, other, trace) else this.create(result)
    }

    override fun and(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        val result = JavaUtils.and(this.asJavaNotNull(instance, trace), other.asJava(), Null)
        return if (result == Null) super.and(instance, interpreter, other, trace) else this.create(result)
    }

    override fun or(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        val result = JavaUtils.or(this.asJavaNotNull(instance, trace), other.asJava(), Null)
        return if (result == Null) super.or(instance, interpreter, other, trace) else this.create(result)
    }

    override fun bitAnd(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        val result = JavaUtils.bitAnd(this.asJavaNotNull(instance, trace), other.asJava(), Null)
        return if (result == Null) super.bitAnd(instance, interpreter, other, trace) else this.create(result)
    }

    override fun bitOr(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        val result = JavaUtils.bitOr(this.asJavaNotNull(instance, trace), other.asJava(), Null)
        return if (result == Null) super.bitOr(instance, interpreter, other, trace) else this.create(result)
    }

    override fun xor(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        val result = JavaUtils.xor(this.asJavaNotNull(instance, trace), other.asJava(), Null)
        return if (result == Null) super.xor(instance, interpreter, other, trace) else this.create(result)
    }

    override fun bracketAccess(instance: ClassInstance, interpreter: Interpreter, index: ClassInstance, trace: LocatableTrace): ClassInstance {
        val result = JavaUtils.bracketAccess(this.asJavaNotNull(instance, trace), index.asJava(), Null)
        return if (result == Null) super.bracketAccess(instance, interpreter, index, trace) else this.createNullable(result)
    }

    override fun bracketAssign(instance: ClassInstance, interpreter: Interpreter, index: ClassInstance, assignee: ClassInstance, trace: LocatableTrace): ClassInstance {
        val result = JavaUtils.bracketAssign(this.asJavaNotNull(instance, trace), index.asJava(), assignee.asJava(), Null)
        return if (result == Null) super.bracketAssign(instance, interpreter, index, assignee, trace) else this.createNullable(result)
    }

    override fun equals(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Boolean {
        return instance.asJava() == other.asJava()
    }

    override fun hashCode(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): Int {
        return instance.asJava().hashCode()
    }

    override fun toString(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): String {
        return instance.asJava().toString()
    }

    private fun asJavaNotNull(instance: ClassInstance, trace: Trace): Any {
        return instance.asJava() ?: runtimeError("Java value was null", trace)
    }

    override fun defineStaticMethods(): List<BuiltInFunction> {
        return listOf(
            BuiltInFunction.of("classOf", 1, this::classOf),
            BuiltInFunction.of("classFromName", 1, this::classFromName, "You should use 'Java.classOf()' instead"),
            BuiltInFunction.of("getStaticField", 2, this::getStaticField, "You should use 'Java.classOf()' then access the static field"),
            BuiltInFunction.of("setStaticField", 3, this::setStaticField, "You should use 'Java.classOf()' then assign the static field"),
            BuiltInFunction.of("getStaticMethodDelegate", 3, this::getStaticMethodDelegate, "You should use 'Java.classOf()' then wrap the static method"),
            BuiltInFunction.arb("callStaticMethod", this::callStaticMethod, "You should use 'Java.classOf()' then call the static method"),
            BuiltInFunction.arb("constructClass", this::constructClass, "You should use 'Java.classOf()' then invoke the result to construct a class"),
            BuiltInFunction.of("valueOf", 1, this::valueOf),
            BuiltInFunction.of("doubleOf", 1, this::doubleOf),
            BuiltInFunction.of("floatOf", 1, this::floatOf),
            BuiltInFunction.of("longOf", 1, this::longOf),
            BuiltInFunction.of("intOf", 1, this::intOf),
            BuiltInFunction.of("shortOf", 1, this::shortOf),
            BuiltInFunction.of("byteOf", 1, this::byteOf),
            BuiltInFunction.of("charOf", 1, this::charOf),
            BuiltInFunction.of("booleanOf", 1, this::booleanOf),
            BuiltInFunction.arb("arrayOf", this::arrayOf),
            BuiltInFunction.arb("typedArrayOf", this::typedArrayOf),
            BuiltInFunction.of("arrayWithSize", 1, this::objectArray, "Use 'Java.objectArray()' instead"),
            BuiltInFunction.of("objectArray", 1, this::objectArray),
            BuiltInFunction.of("typedArray", 2, this::typedArray),
            BuiltInFunction.of("doubleArray", 1, this::doubleArray),
            BuiltInFunction.of("floatArray", 1, this::floatArray),
            BuiltInFunction.of("longArray", 1, this::longArray),
            BuiltInFunction.of("intArray", 1, this::intArray),
            BuiltInFunction.of("shortArray", 1, this::shortArray),
            BuiltInFunction.of("byteArray", 1, this::byteArray),
            BuiltInFunction.of("charArray", 1, this::charArray),
            BuiltInFunction.of("booleanArray", 1, this::booleanArray),
            BuiltInFunction.of("runnableOf", 1, this::runnableOf),
            BuiltInFunction.of("consumerOf", 1, this::consumerOf),
            BuiltInFunction.of("supplierOf", 1, this::supplierOf),
            BuiltInFunction.of("functionOf", 1, this::functionOf),
            BuiltInFunction.of("predicateOf", 1, this::predicateOf),
            BuiltInFunction.of("implementClass", 2, this::implementClass)
        )
    }

    @FunctionDoc(
        isStatic = true,
        name = "classOf",
        desc = ["Gets a Java class from the name of the class"],
        params = [ParameterDoc(StringDef::class, "className", ["The name of the class you want to get."])],
        returns = ReturnDoc(JavaClassDef::class, ["The Java class value which can be used as a class reference."]),
        examples = ["Java.classOf('java.util.ArrayList');"]
    )
    private fun classOf(arguments: Arguments): ClassInstance {
        val name = arguments.nextPrimitive(StringDef::class)
        val clazz = ReflectionUtils.getClass(name, arguments.interpreter.api.getObfuscator())
        return arguments.interpreter.create(JavaClassDef::class, clazz)
    }

    @FunctionDoc(
        deprecated = ["You should use 'Java.classOf(name)' instead"],
        isStatic = true,
        name = "classFromName",
        desc = ["Gets a Java class from the name of the class"],
        params = [ParameterDoc(StringDef::class, "className", ["The name of the class you want to get."])],
        returns = ReturnDoc(JavaDef::class, ["The Java Class<?> value wrapped in the Java wrapper."]),
        examples = ["Java.classFromName('java.util.ArrayList');"]
    )
    private fun classFromName(arguments: Arguments): ClassInstance {
        val name = arguments.nextPrimitive(StringDef::class)
        val clazz = ReflectionUtils.getClass(name, arguments.interpreter.api.getObfuscator())
        return this.create(clazz)
    }

    @FunctionDoc(
        deprecated = ["You should use 'Java.classOf(name)' then access the static field"],
        isStatic = true,
        name = "getStaticField",
        desc = ["Gets a static field Java value from a Java class"],
        params = [ParameterDoc(StringDef::class, "className", ["The name of the class."]), ParameterDoc(StringDef::class, "fieldName", ["The name of the field."])],
        returns = ReturnDoc(JavaDef::class, ["The Java value of the field wrapped in the Java wrapper."]),
        examples = ["Java.getStaticField('java.lang.Integer', 'MAX_VALUE');"]
    )
    private fun getStaticField(arguments: Arguments): ClassInstance {
        val className = arguments.nextPrimitive(StringDef::class)
        val field = arguments.nextPrimitive(StringDef::class)
        val clazz = ReflectionUtils.getClass(className, arguments.interpreter.api.getObfuscator())
        return this.createNullable(
            ReflectionUtils.getField(clazz, null, field, arguments.interpreter.api.getObfuscator())
        )
    }

    @FunctionDoc(
        deprecated = ["You should use 'Java.classOf(name)' then assign the static field"],
        isStatic = true,
        name = "setStaticField",
        desc = ["Sets a static field in a Java class with a new value"],
        params = [
            ParameterDoc(StringDef::class, "className", ["The name of the class."]),
            ParameterDoc(StringDef::class, "fieldName", ["The name of the field."]),
            ParameterDoc(ObjectDef::class, "newValue", ["The new value."])
        ],
        examples = [
            """
            // Obviously this won't work, but it's just an example
            Java.setStaticField('java.lang.Integer', 'MAX_VALUE', Java.intOf(100));"
            """
        ]
    )
    private fun setStaticField(arguments: Arguments) {
        val className = arguments.nextPrimitive(StringDef::class)
        val field = arguments.nextPrimitive(StringDef::class)
        val newValue = arguments.next()
        val clazz = ReflectionUtils.getClass(className, arguments.interpreter.api.getObfuscator())
        ReflectionUtils.setField(clazz, null, newValue, field, arguments.interpreter.api.getObfuscator())
    }

    @FunctionDoc(
        deprecated = ["You should use 'Java.classOf(name)' then wrap the static method"],
        isStatic = true,
        name = "getStaticMethodDelegate",
        desc = [
            "Gets a static method delegate from a Java class, this should",
            "be avoided and instance use 'classOf' to get the class instance",
            "and then call the method on that class instance. The parameter count",
            "parameter is no longer used internally but remains for backwards compatibility"
        ],
        params = [
            ParameterDoc(StringDef::class, "className", ["The name of the class."]),
            ParameterDoc(StringDef::class, "methodName", ["The name of the method."]),
            ParameterDoc(NumberDef::class, "parameters", ["The number of parameters."])
        ],
        returns = ReturnDoc(FunctionDef::class, ["The delegated Java method in an Arucas Function."]),
        examples = ["Java.getStaticMethodDelegate('java.lang.Integer', 'parseInt', 1);"]
    )
    private fun getStaticMethodDelegate(arguments: Arguments): ClassInstance {
        val className = arguments.nextPrimitive(StringDef::class)
        val name = arguments.nextPrimitive(StringDef::class)
        // val parameters = arguments.nextPrimitive(NumberDef::class).toInt()
        val clazz = ReflectionUtils.getClass(className, arguments.interpreter.api.getObfuscator())
        return arguments.interpreter.create(FunctionDef::class, BuiltInFunction.java(clazz, null, name))
    }

    @FunctionDoc(
        deprecated = ["You should use 'Java.classOf(name)' then call the static method"],
        isStatic = true,
        name = "callStaticMethod",
        desc = [
            "Calls a static method of a Java class.",
            "This should be avoided and instead use 'classOf' to get the",
            "instance of the class then call the static method on that"
        ],
        params = [
            ParameterDoc(StringDef::class, "className", ["The name of the class."]),
            ParameterDoc(StringDef::class, "methodName", ["The name of the method."]),
            ParameterDoc(ObjectDef::class, "parameters", ["Any parameters to call the method with."], true),
        ],
        returns = ReturnDoc(JavaDef::class, ["The return value of the method wrapped in the Java wrapper."]),
        examples = ["Java.callStaticMethod('java.lang.Integer', 'parseInt', '123');"]
    )
    private fun callStaticMethod(arguments: Arguments): ClassInstance {
        val className = arguments.nextPrimitive(StringDef::class)
        val name = arguments.nextPrimitive(StringDef::class)
        val clazz = ReflectionUtils.getClass(className, arguments.interpreter.api.getObfuscator())
        return this.createNullable(
            ReflectionUtils.callMethod(clazz, null, name, arguments.getRemaining(), arguments.interpreter.api.getObfuscator())
        )
    }

    @FunctionDoc(
        deprecated = ["You should use 'Java.classOf(name)' then call the result to construct the class"],
        isStatic = true,
        name = "constructClass",
        desc = [
            "This constructs a Java class with specified class name and parameters.",
            "This should be avoided and instead use 'classOf' to get the class",
            "instance then call the constructor on that instance"
        ],
        params = [
            ParameterDoc(StringDef::class, "className", ["The name of the class."]),
            ParameterDoc(ObjectDef::class, "parameters", ["Any parameters to pass to the constructor."], true),
        ],
        returns = ReturnDoc(JavaDef::class, ["The constructed Java Object wrapped in the Java wrapper."]),
        examples = ["Java.constructClass('java.util.ArrayList');"]
    )
    private fun constructClass(arguments: Arguments): ClassInstance {
        val className = arguments.nextPrimitive(StringDef::class)
        val clazz = ReflectionUtils.getClass(className, arguments.interpreter.api.getObfuscator())
        return this.createNullable(
            ReflectionUtils.callConstructor(clazz, arguments.getRemaining(), arguments.interpreter.api.getObfuscator())
        )
    }

    @FunctionDoc(
        isStatic = true,
        name = "valueOf",
        desc = ["Converts any Arucas value into a Java value then wraps it in the Java wrapper and returns it"],
        params = [ParameterDoc(ObjectDef::class, "value", ["Any value to get the Java value of."])],
        returns = ReturnDoc(JavaDef::class, ["The Java wrapper value, null if argument was null."]),
        examples = ["Java.valueOf('Hello World!');"]
    )
    private fun valueOf(arguments: Arguments): ClassInstance {
        return this.create(arguments.next().asJava() ?: Null)
    }

    @FunctionDoc(
        isStatic = true,
        name = "doubleOf",
        desc = ["Creates a Java value double, to be used in Java"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to convert to a Java double."])],
        returns = ReturnDoc(JavaDef::class, ["The double in Java wrapper."]),
        examples = ["Java.doubleOf(1.0);"]
    )
    private fun doubleOf(arguments: Arguments): ClassInstance {
        return this.create(arguments.nextPrimitive(NumberDef::class))
    }

    @FunctionDoc(
        isStatic = true,
        name = "floatOf",
        desc = ["Creates a Java value float, to be used in Java"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to convert to a Java float."])],
        returns = ReturnDoc(JavaDef::class, ["The float in Java wrapper."]),
        examples = ["Java.floatOf(1.0);"]
    )
    private fun floatOf(arguments: Arguments): ClassInstance {
        return this.create(arguments.nextPrimitive(NumberDef::class).toFloat())
    }

    @FunctionDoc(
        isStatic = true,
        name = "intOf",
        desc = ["Creates a Java value int, to be used in Java"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to convert to a Java int."])],
        returns = ReturnDoc(JavaDef::class, ["The int in Java wrapper."]),
        examples = ["Java.intOf(1);"]
    )
    private fun intOf(arguments: Arguments): ClassInstance {
        return this.create(arguments.nextPrimitive(NumberDef::class).toInt())
    }

    @FunctionDoc(
        isStatic = true,
        name = "longOf",
        desc = ["Creates a Java value long, to be used in Java"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to convert to a Java long."])],
        returns = ReturnDoc(JavaDef::class, ["The long in Java wrapper."]),
        examples = ["Java.longOf(1);"]
    )
    private fun longOf(arguments: Arguments): ClassInstance {
        return this.create(arguments.nextPrimitive(NumberDef::class).toLong())
    }

    @FunctionDoc(
        isStatic = true,
        name = "shortOf",
        desc = ["Creates a Java value short, to be used in Java"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to convert to a Java short."])],
        returns = ReturnDoc(JavaDef::class, ["The short in Java wrapper."]),
        examples = ["Java.shortOf(1);"]
    )
    private fun shortOf(arguments: Arguments): ClassInstance {
        return this.create(arguments.nextPrimitive(NumberDef::class).toInt().toShort())
    }

    @FunctionDoc(
        isStatic = true,
        name = "byteOf",
        desc = ["Creates a Java value byte, to be used in Java"],
        params = [ParameterDoc(NumberDef::class, "num", ["The number to convert to a Java byte."])],
        returns = ReturnDoc(JavaDef::class, ["The byte in Java wrapper."]),
        examples = ["Java.byteOf(1);"]
    )
    private fun byteOf(arguments: Arguments): ClassInstance {
        return this.create(arguments.nextPrimitive(NumberDef::class).toInt().toByte())
    }

    @FunctionDoc(
        isStatic = true,
        name = "charOf",
        desc = ["Creates a Java value char, to be used in Java"],
        params = [ParameterDoc(StringDef::class, "char", ["The char to convert to a Java char."])],
        returns = ReturnDoc(JavaDef::class, ["The char in Java wrapper."]),
        examples = ["Java.charOf('a');"]
    )
    private fun charOf(arguments: Arguments): ClassInstance {
        val string = arguments.nextPrimitive(StringDef::class)
        if (string.length != 1) {
            runtimeError("String must be 1 character long")
        }
        return this.create(string[0])
    }

    @FunctionDoc(
        isStatic = true,
        name = "booleanOf",
        desc = ["Creates a Java value boolean, to be used in Java"],
        params = [ParameterDoc(BooleanDef::class, "bool", ["The boolean to convert to a Java boolean."])],
        returns = ReturnDoc(JavaDef::class, ["The boolean in Java wrapper."]),
        examples = ["Java.booleanOf(true);"]
    )
    private fun booleanOf(arguments: Arguments): ClassInstance {
        return this.create(arguments.nextPrimitive(BooleanDef::class))
    }

    @FunctionDoc(
        isStatic = true,
        name = "arrayOf",
        desc = [
            "Creates a Java Object array with a given values, this will be the size of the array,",
            "this cannot be used to create primitive arrays"
        ],
        params = [ParameterDoc(ObjectDef::class, "values", ["The values to add to the array."], true)],
        returns = ReturnDoc(JavaDef::class, ["The Java Object array."]),
        examples = ["Java.arrayOf(1, 2, 3, 'string!', false);"]
    )
    private fun arrayOf(arguments: Arguments): ClassInstance {
        val array = Array(arguments.size()) { arguments.get(it).asJava() }
        return this.create(array)
    }

    @FunctionDoc(
        isStatic = true,
        name = "typedArrayOf",
        desc = [
            "Creates a Java array with a given type with given values. This will also",
            "be the size of the array. If any value does not match the given type an error will be thrown."
        ],
        params = [
            ParameterDoc(JavaClassDef::class, "castType", ["The type to cast the arrays to."]),
            ParameterDoc(JavaDef::class, "values", ["The values to add to the array."], true)
        ],
        returns = ReturnDoc(JavaDef::class, ["The typed object array."]),
        examples = ["Java.typedArrayOf(Java.classOf('java.lang.String'), 'foo', 'bar')"]
    )
    private fun typedArrayOf(arguments: Arguments): ClassInstance {
        val javaClass = arguments.nextPrimitive(JavaClassDef::class)
        val remaining = arguments.getRemaining()
        @Suppress("UNCHECKED_CAST")
        val array = java.lang.reflect.Array.newInstance(javaClass, remaining.size) as Array<Any?>
        RuntimeError.wrap {
            remaining.forEachIndexed { i, instance -> array[i] = instance.asJava() }
        }
        return this.create(array)
    }

    @FunctionDoc(
        isStatic = true,
        name = "objectArray",
        desc = [
            "Creates a Java Object array with a given size, the array is filled with null values",
            "by default and can be filled with any Java values, this array cannot be expanded."
        ],
        params = [ParameterDoc(NumberDef::class, "size", ["The size of the array."])],
        returns = ReturnDoc(JavaDef::class, ["The Java Object array."]),
        examples = ["Java.objectArray(10);"]
    )
    private fun objectArray(arguments: Arguments): ClassInstance {
        val size = arguments.nextPrimitive(NumberDef::class).toInt()
        return if (size >= 0) this.create(arrayOfNulls<Any?>(size)) else runtimeError("Array size must be >= 0")
    }

    @FunctionDoc(
        isStatic = true,
        name = "typedArray",
        desc = [
            "Creates a Java typed array with a given size, the array is filled with null values",
            "by default and can be filled with the given typed Java values, this array cannot be expanded."
        ],
        params = [
            ParameterDoc(JavaClassDef::class, "type", ["The type of the array."]),
            ParameterDoc(NumberDef::class, "size", ["The size of the array."])
        ],
        returns = ReturnDoc(JavaDef::class, ["The Java typed array."]),
        examples = ["Java.typedArray(Java.classOf('java.util.String'), 10);"]
    )
    private fun typedArray(arguments: Arguments): ClassInstance {
        val javaClass = arguments.nextPrimitive(JavaClassDef::class)
        val size = arguments.nextPrimitive(NumberDef::class).toInt()
        if (size >= 0) {
            return this.create(java.lang.reflect.Array.newInstance(javaClass, size))
        } else {
            runtimeError("Array size must be >= 0")
        }
    }

    @FunctionDoc(
        isStatic = true,
        name = "doubleArray",
        desc = [
            "Creates a Java double array with a given size, the array is filled with 0's",
            "by default and can be filled with only doubles"
        ],
        params = [ParameterDoc(NumberDef::class, "size", ["The size of the array."])],
        returns = ReturnDoc(JavaDef::class, ["The Java double array."]),
        examples = ["Java.doubleArray(10);"]
    )
    private fun doubleArray(arguments: Arguments): ClassInstance {
        val size = arguments.nextPrimitive(NumberDef::class).toInt()
        return if (size >= 0) this.create(DoubleArray(size)) else runtimeError("Array size must be >= 0")
    }

    @FunctionDoc(
        isStatic = true,
        name = "floatArray",
        desc = [
            "Creates a Java float array with a given size, the array is filled with 0's",
            "by default and can be filled with only floats"
        ],
        params = [ParameterDoc(NumberDef::class, "size", ["The size of the array."])],
        returns = ReturnDoc(JavaDef::class, ["The Java float array."]),
        examples = ["Java.floatArray(10);"]
    )
    private fun floatArray(arguments: Arguments): ClassInstance {
        val size = arguments.nextPrimitive(NumberDef::class).toInt()
        return if (size >= 0) this.create(FloatArray(size)) else runtimeError("Array size must be >= 0")
    }

    @FunctionDoc(
        isStatic = true,
        name = "intArray",
        desc = [
            "Creates a Java int array with a given size, the array is filled with 0's",
            "by default and can be filled with only ints"
        ],
        params = [ParameterDoc(NumberDef::class, "size", ["The size of the array."])],
        returns = ReturnDoc(JavaDef::class, ["The Java int array."]),
        examples = ["Java.intArray(10);"]
    )
    private fun intArray(arguments: Arguments): ClassInstance {
        val size = arguments.nextPrimitive(NumberDef::class).toInt()
        return if (size >= 0) this.create(IntArray(size)) else runtimeError("Array size must be >= 0")
    }

    @FunctionDoc(
        isStatic = true,
        name = "longArray",
        desc = [
            "Creates a Java long array with a given size, the array is filled with 0's",
            "by default and can be filled with only longs"
        ],
        params = [ParameterDoc(NumberDef::class, "size", ["The size of the array."])],
        returns = ReturnDoc(JavaDef::class, ["The Java long array."]),
        examples = ["Java.longArray(10);"]
    )
    private fun longArray(arguments: Arguments): ClassInstance {
        val size = arguments.nextPrimitive(NumberDef::class).toInt()
        return if (size >= 0) this.create(LongArray(size)) else runtimeError("Array size must be >= 0")
    }

    @FunctionDoc(
        isStatic = true,
        name = "shortArray",
        desc = [
            "Creates a Java short array with a given size, the array is filled with 0's",
            "by default and can be filled with only shorts"
        ],
        params = [ParameterDoc(NumberDef::class, "size", ["The size of the array."])],
        returns = ReturnDoc(JavaDef::class, ["The Java short array."]),
        examples = ["Java.shortArray(10);"]
    )
    private fun shortArray(arguments: Arguments): ClassInstance {
        val size = arguments.nextPrimitive(NumberDef::class).toInt()
        return if (size >= 0) this.create(ShortArray(size)) else runtimeError("Array size must be >= 0")
    }

    @FunctionDoc(
        isStatic = true,
        name = "byteArray",
        desc = [
            "Creates a Java byte array with a given size, the array is filled with 0's",
            "by default and can be filled with only bytes"
        ],
        params = [ParameterDoc(NumberDef::class, "size", ["The size of the array."])],
        returns = ReturnDoc(JavaDef::class, ["The Java byte array."]),
        examples = ["Java.byteArray(10);"]
    )
    private fun byteArray(arguments: Arguments): ClassInstance {
        val size = arguments.nextPrimitive(NumberDef::class).toInt()
        return if (size >= 0) this.create(ByteArray(size)) else runtimeError("Array size must be >= 0")
    }

    @FunctionDoc(
        isStatic = true,
        name = "charArray",
        desc = [
            "Creates a Java char array with a given size, the array is filled with null characters's",
            "by default and can be filled with only chars"
        ],
        params = [ParameterDoc(NumberDef::class, "size", ["The size of the array."])],
        returns = ReturnDoc(JavaDef::class, ["The Java char array."]),
        examples = ["Java.charArray(10);"]
    )
    private fun charArray(arguments: Arguments): ClassInstance {
        val size = arguments.nextPrimitive(NumberDef::class).toInt()
        return if (size >= 0) this.create(CharArray(size)) else runtimeError("Array size must be >= 0")
    }

    @FunctionDoc(
        isStatic = true,
        name = "booleanArray",
        desc = [
            "Creates a Java boolean array with a given size, the array is filled with false",
            "by default and can be filled with only booleans"
        ],
        params = [ParameterDoc(NumberDef::class, "size", ["The size of the array."])],
        returns = ReturnDoc(JavaDef::class, ["The Java boolean array."]),
        examples = ["Java.booleanArray(10);"]
    )
    private fun booleanArray(arguments: Arguments): ClassInstance {
        val size = arguments.nextPrimitive(NumberDef::class).toInt()
        return if (size >= 0) this.create(BooleanArray(size)) else runtimeError("Array size must be >= 0")
    }

    @FunctionDoc(
        isStatic = true,
        name = "runnableOf",
        desc = [
            "Creates a Java Runnable object from a given function, this must",
            "have no paramters and any return values will be ignored"
        ],
        params = [ParameterDoc(FunctionDef::class, "function", ["The function to be executed."])],
        returns = ReturnDoc(JavaDef::class, ["The Java Runnable object."]),
        examples = [
            """
            Java.runnableOf(fun() {
                print('runnable');
            });
            """
        ]
    )
    private fun runnableOf(arguments: Arguments): ClassInstance {
        val function = arguments.nextFunction()
        val child = arguments.interpreter.branch()
        val runnable = Runnable { child.branch().call(function, listOf()) }
        return this.create(runnable)
    }

    @FunctionDoc(
        isStatic = true,
        name = "consumerOf",
        desc = [
            "Creates a Java Consumer object from a given function, it must have one",
            "parameter and any return values will be ignored"
        ],
        params = [ParameterDoc(FunctionDef::class, "function", ["The function to be executed."])],
        returns = ReturnDoc(JavaDef::class, ["The Java Consumer object."]),
        examples = [
            """
            Java.consumerOf(fun(something) {
                print(something);
            });
            """
        ]
    )
    private fun consumerOf(arguments: Arguments): ClassInstance {
        val function = arguments.nextFunction()
        val child = arguments.interpreter.branch()
        val consumer = Consumer<Any?> { child.branch().call(function, listOf(child.convertValue(it))) }
        return this.create(consumer)
    }

    @FunctionDoc(
        isStatic = true,
        name = "supplierOf",
        desc = ["Creates a Java Supplier object from a given function"],
        params = [ParameterDoc(FunctionDef::class, "function", ["The function to be executed, this must have no parameters and must return (supply) a value."])],
        returns = ReturnDoc(JavaDef::class, ["The Java Supplier object."]),
        examples = [
            """
            Java.supplierOf(fun() {
                return 'supplier';
            });
            """
        ]
    )
    private fun supplierOf(arguments: Arguments): ClassInstance {
        val function = arguments.nextFunction()
        val child = arguments.interpreter.branch()
        val supplier = Supplier<Any?> { child.branch().call(function, listOf()).asJava() }
        return this.create(supplier)
    }

    @FunctionDoc(
        isStatic = true,
        name = "functionOf",
        desc = ["Creates a Java Function object from a given function"],
        params = [ParameterDoc(FunctionDef::class, "function", ["The function to be executed, this must have one parameter and must return a value."])],
        returns = ReturnDoc(JavaDef::class, ["The Java Function object."]),
        examples = [
            """
            Java.functionOf(fun(something) {
                return something;
            });
            """
        ]
    )
    private fun functionOf(arguments: Arguments): ClassInstance {
        val callable = arguments.nextFunction()
        val child = arguments.interpreter.branch()
        val function = Function<Any?, Any?> { child.branch().call(callable, listOf(child.convertValue(it))) }
        return this.create(function)
    }

    @FunctionDoc(
        isStatic = true,
        name = "predicateOf",
        desc = ["Creates a Java Predicate object from a given function"],
        params = [ParameterDoc(FunctionDef::class, "function", ["The function to be executed, this must have one parameter and must return a boolean."])],
        returns = ReturnDoc(JavaDef::class, ["The Java Predicate object."]),
        examples = [
            """
            Java.predicateOf(fun(something) {
                return something == 'something';
            });
            """
        ]
    )
    private fun predicateOf(arguments: Arguments): ClassInstance {
        val function = arguments.nextFunction()
        val child = arguments.interpreter.branch()
        val predicate = Predicate<Any?> {
            val result = child.branch().call(function, listOf(child.convertValue(it)))
            result.getPrimitive(BooleanDef::class) ?: runtimeError("Predicate must return a boolean")
        }
        return this.create(predicate)
    }

    @FunctionDoc(
        isStatic = true,
        name = "implementClass",
        desc = ["Creates a new Java class definition extending/implementing the given classes."],
        params = [
            ParameterDoc(FunctionDef::class, "superclasses", ["The superclasses of the wanted definition. These should be JavaClass types, there can only be 1 (abstract) class, as many interfaces."]),
            ParameterDoc(FunctionDef::class, "invokeHandler", ["This function will intercept all method calls, it will be passed the name of the method and any arguments"])
        ],
        returns = ReturnDoc(JavaDef::class, ["The Java Predicate object."]),
        examples = [
            """
            Java.implementClass([Java.classOf("java.lang.Runnable")], fun(name, args) {
                // ...
            });
            """
        ]
    )
    private fun implementClass(arguments: Arguments): Class<*> {
        arguments.interpreter.throwIfNotExperimental { "Implementing Java classes is experimental, enable with `experimental(true)`" }

        val collection = arguments.nextPrimitive(CollectionDef::class)
        val invokeHandler = arguments.nextFunction()

        val interfaces = LinkedList<Class<*>>()
        var superclass: Class<*> = Object::class.java
        for (instance in collection) {
            val clazz = instance.getPrimitive(JavaClassDef::class)
            clazz ?: runtimeError("Required a list of JavaClass'")
            if (clazz.isInterface) {
                interfaces.add(clazz)
            } else {
                if (superclass != Object::class.java) {
                    runtimeError("Cannot have multiple superclasses")
                }
                superclass = clazz
            }
        }
        return ByteBuddy()
            .subclass(superclass)
            .implement(interfaces)
            .intercept(MethodDelegation.to(ReflectionUtils.functionToInterceptor(arguments.interpreter, invokeHandler)))
            .make()
            .load(this::class.java.classLoader)
            .loaded
    }

    override fun defineMethods(): List<MemberFunction> {
        return listOf(
            MemberFunction.of("toArucas", this::toArucas),
            MemberFunction.of("getField", 1, this::getField, "You should get the field directly"),
            MemberFunction.of("setField", 2, this::setField, "You should set the field directly"),
            MemberFunction.arb("callMethod", this::callMethod, "You should call the method directly"),
            MemberFunction.of("getMethodDelegate", 2, this::getMethodDelegate, "Consider wrapping the method in a lambda instead"),
        )
    }

    @FunctionDoc(
        name = "toArucas",
        desc = [
            "This converts the Java value to an Arucas Value if possible, this may still",
            "be of a Java value if it cannot be converted. For example, Strings, Numbers, Lists",
            "will be converted but "
        ],
        returns = ReturnDoc(ObjectDef::class, [
            "The Value in Arucas, this may still be of Java value if the value cannot be",
            "converted into an Arucas value, values like Strings, Numbers, Lists, etc... will be converted."
        ]),
        examples = ["Java.valueOf([1, 2, 3]).toArucas();"]
    )
    private fun toArucas(arguments: Arguments): ClassInstance {
        return arguments.interpreter.convertValue(arguments.next().asJava())
    }

    @FunctionDoc(
        deprecated = ["You should call the method directly on the value: `Java.constructClass('me.senseiwells.impl.Test').A;`"],
        name = "getField",
        desc = [
            "This returns the Java wrapped value of the specified field.",
            "There is no reason for you to be using this method, it will be removed in future versions"
        ],
        params = [ParameterDoc(StringDef::class, "fieldName", ["The name of the field."])],
        returns = ReturnDoc(JavaDef::class, ["The Java wrapped value of the field."]),
        examples = ["Java.constructClass('me.senseiwells.impl.Test').getField('A');"]
    )
    private fun getField(arguments: Arguments): ClassInstance {
        val java = this.asJavaNotNull(arguments.next(), Trace.INTERNAL)
        val fieldName = arguments.nextPrimitive(StringDef::class)
        return this.createNullable(
            ReflectionUtils.getField(java::class.java, java, fieldName, arguments.interpreter.api.getObfuscator())
        )
    }

    @FunctionDoc(
        deprecated = ["You should assign the value directly on the value: Java.constructClass('me.senseiwells.impl.Test').A = 'Hello';"],
        name = "setField",
        desc = [
            "This sets the specified field to the specified value",
            "There is no reason for you to be using this method, it will be removed in future versions"
        ],
        params = [
            ParameterDoc(StringDef::class, "fieldName", ["The name of the field."]),
            ParameterDoc(ObjectDef::class, "value", ["The value to set the field to, the value type must match the type of the field."])
        ],
        examples = ["Java.constructClass('me.senseiwells.impl.Test').setField('A', 'Hello');"]
    )
    private fun setField(arguments: Arguments) {
        val java = this.asJavaNotNull(arguments.next(), Trace.INTERNAL)
        val fieldName = arguments.nextPrimitive(StringDef::class)
        ReflectionUtils.setField(java::class.java, java, arguments.next(), fieldName, arguments.interpreter.api.getObfuscator())
    }

    @FunctionDoc(
        deprecated = ["You should call the method directly on the value: Java.valueOf('').isBlank();"],
        name = "callMethod",
        desc = [
            "This calls the specified method with the specified parameters, calling the method",
            "with this function has no benefits unless you are calling a function that also is",
            "native to Arucas. For example `object.copy()` will use the Arucas 'copy' function.",
            "But this is extremely rare so almost all of the time you should all the method normally."
        ],
        params = [
            ParameterDoc(StringDef::class, "methodName", ["The name of the method."]),
            ParameterDoc(ObjectDef::class, "parameters", ["the parameters to call the method with"], true)
        ],
        returns = ReturnDoc(JavaDef::class, ["The return value of the method call wrapped in the Java wrapper."]),
        examples = ["Java.valueOf('').callMethod('isBlank');"]
    )
    private fun callMethod(arguments: Arguments): ClassInstance {
        val java = this.asJavaNotNull(arguments.next(), Trace.INTERNAL)
        val name = arguments.nextPrimitive(StringDef::class)
        val params = arguments.getRemaining()
        return this.createNullable(
            ReflectionUtils.callMethod(java::class.java, java, name, params, arguments.interpreter.api.getObfuscator())
        )
    }

    @FunctionDoc(
        deprecated = ["Consider wrapping the method in a lambda instead"],
        name = "getMethodDelegate",
        desc = [
            "This returns a method delegate for the specified method name and parameters.",
            "This should be avoided and replaced with a Arucas function wrapping the call instead.",
            "For example: `delegate = (fun() { Java.valueOf('').isBlank(); });`.",
            "Another thing to note is that the parameter count parameter is no longer",
            "used and ignored internally, instead the parameters are calculated when you",
            "call the delegate. The parameter remains for backwards compatability."
        ],
        params = [
            ParameterDoc(StringDef::class, "methodName", ["The name of the method."]),
            ParameterDoc(NumberDef::class, "parameters", ["The number of parameters."])
        ],
        returns = ReturnDoc(FunctionDef::class, ["The function containing the Java method delegate."]),
        examples = ["Java.valueOf('string!').getMethodDelegate('isBlank', 0);"]
    )
    private fun getMethodDelegate(arguments: Arguments): ClassInstance {
        val java = arguments.next().asJava() ?: Null
        val name = arguments.nextPrimitive(StringDef::class)
        // val parameters = arguments.nextPrimitive(NumberDef::class).toInt()
        return arguments.interpreter.create(FunctionDef::class, BuiltInFunction.java(java::class.java, java, name))
    }
}