package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.annotations.*
import me.senseiwells.arucas.classes.CreatableDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.compiler.token.Type
import me.senseiwells.arucas.exceptions.RuntimeError
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.functions.builtin.Arguments
import me.senseiwells.arucas.functions.builtin.ConstructorFunction
import me.senseiwells.arucas.functions.builtin.MemberFunction
import me.senseiwells.arucas.interpreter.Interpreter
import me.senseiwells.arucas.utils.StringUtils.toNumber
import me.senseiwells.arucas.utils.StringUtils.unescape
import me.senseiwells.arucas.utils.impl.ArucasList
import me.senseiwells.arucas.utils.misc.Types.STRING
import java.util.*
import java.util.regex.PatternSyntaxException

@ClassDoc(
    name = STRING,
    desc = [
        "This class represents an array of characters to form a string.",
        "This class cannot be instantiated directly, instead use the literal",
        "by using quotes. Strings are immutable in Arucas."
    ]
)
class StringDef(interpreter: Interpreter): CreatableDefinition<String>(STRING, interpreter) {
    private val pool = HashMap<String, ClassInstance>()

    fun literal(value: String): ClassInstance {
        val string = value.substring(1, value.length - 1).unescape()
        return this.pool.getOrPut(string) { super.create(string) }
    }

    override fun create(value: String) = this.pool.getOrElse(value) { super.create(value) }

    override fun canConstructDirectly() = false

    override fun plus(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): String {
        return instance.toString(interpreter, trace) + other.toString(interpreter, trace)
    }

    override fun compare(instance: ClassInstance, interpreter: Interpreter, type: Type, other: ClassInstance, trace: LocatableTrace): Any? {
        val otherString = other.getPrimitive(this) ?: return super.compare(instance, interpreter, type, other, trace)
        return when (type) {
            Type.LESS_THAN -> instance.asPrimitive(this) < otherString
            Type.LESS_THAN_EQUAL -> instance.asPrimitive(this) <= otherString
            Type.MORE_THAN -> instance.asPrimitive(this) > otherString
            Type.MORE_THAN_EQUAL -> instance.asPrimitive(this) >= otherString
            else -> super.compare(instance, interpreter, type, other, trace)
        }
    }

    override fun compare(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Int {
        val otherString = other.getPrimitive(this) ?: return super.compare(instance, interpreter, other, trace)
        return instance.asPrimitive(this).compareTo(otherString)
    }

    override fun bracketAccess(instance: ClassInstance, interpreter: Interpreter, index: ClassInstance, trace: LocatableTrace): ClassInstance {
        val i = index.getPrimitive(NumberDef::class)?.toInt() ?: runtimeError("Expected number to index string", trace)
        return this.create(instance.asPrimitive(this)[i].toString())
    }

    override fun toString(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): String {
        return instance.asPrimitive(this)
    }

    override fun defineConstructors(): List<ConstructorFunction> {
        return listOf(
            ConstructorFunction.of(1, this::construct)
        )
    }

    @ConstructorDoc(
        desc = [
            "This creates a new string object, not from the string pool, with the given string.",
            "This cannot be called directly, only from child classes"
        ],
        examples = [
            """
            class ChildString: String {
                ChildString(): super("example");
            }
            """
        ]
    )
    private fun construct(arguments: Arguments) {
        val instance = arguments.next()
        val string = arguments.nextPrimitive(StringDef::class)
        instance.setPrimitive(this, string)
    }

    override fun defineMethods(): List<MemberFunction> {
        return listOf(
            MemberFunction.of("toList", this::toList, "Use '<String>.chars()' instead"),
            MemberFunction.of("chars", this::chars),
            MemberFunction.of("length", this::length),
            MemberFunction.of("uppercase", this::uppercase),
            MemberFunction.of("lowercase", this::lowercase),
            MemberFunction.of("capitalize", this::capitalize),
            MemberFunction.of("reverse", this::reverse),
            MemberFunction.of("contains", 1, this::contains),
            MemberFunction.of("startsWith", 1, this::startsWith),
            MemberFunction.of("endsWith", 1, this::endsWith),
            MemberFunction.arb("format", this::format),
            MemberFunction.of("toNumber", this::toNumber),
            MemberFunction.of("strip", this::strip),
            MemberFunction.of("subString", 2, this::subString),
            MemberFunction.of("split", 1, this::split),
            MemberFunction.of("matches", 1, this::matches),
            MemberFunction.of("find", 1, this::find),
            MemberFunction.of("findAll", 1, this::findAll),
            MemberFunction.of("replaceAll", 2, this::replaceAll),
            MemberFunction.of("replaceFirst", 2, this::replaceFirst),
        )
    }

    @FunctionDoc(
        deprecated = ["Use '<String>.chars()' instead"],
        name = "toList",
        desc = ["This makes a list of all the characters in the string"],
        returns = ReturnDoc(ListDef::class, ["The list of characters."]),
        examples = ["'hello'.toList(); // ['h', 'e', 'l', 'l', 'o']"]
    )
    private fun toList(arguments: Arguments): ArucasList {
        return this.chars(arguments)
    }

    @FunctionDoc(
        name = "chars",
        desc = ["This makes a list of all the characters in the string"],
        returns = ReturnDoc(ListDef::class, ["The list of characters."]),
        examples = ["'hello'.chars(); // ['h', 'e', 'l', 'l', 'o']"]
    )
    private fun chars(arguments: Arguments): ArucasList {
        val string = arguments.nextPrimitive(this)
        val list = ArucasList()
        for (i in string.indices) {
            list.add(this.create(string[i].toString()))
        }
        return list
    }

    @FunctionDoc(
        name = "length",
        desc = ["This returns the length of the string"],
        returns = ReturnDoc(NumberDef::class, ["The length of the string."]),
        examples = ["'hello'.length(); // 5"]
    )
    private fun length(arguments: Arguments): Int {
        return arguments.nextPrimitive(this).length
    }

    @FunctionDoc(
        name = "uppercase",
        desc = ["This returns the string in uppercase"],
        returns = ReturnDoc(StringDef::class, ["The string in uppercase."]),
        examples = ["'hello'.uppercase(); // 'HELLO'"]
    )
    private fun uppercase(arguments: Arguments): String {
        return arguments.nextPrimitive(this).uppercase(Locale.UK)
    }

    @FunctionDoc(
        name = "lowercase",
        desc = ["This returns the string in lowercase"],
        returns = ReturnDoc(StringDef::class, ["The string in lowercase."]),
        examples = ["'HELLO'.lowercase(); // 'hello'"]
    )
    private fun lowercase(arguments: Arguments): String {
        return arguments.nextPrimitive(this).lowercase(Locale.UK)
    }

    @FunctionDoc(
        name = "capitalize",
        desc = ["This returns the string in capitalized form"],
        returns = ReturnDoc(StringDef::class, ["The string in capitalized form."]),
        examples = ["'hello'.capitalize(); // 'Hello'"]
    )
    private fun capitalize(arguments: Arguments): String {
        return arguments.nextPrimitive(this).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.UK) else it.toString()
        }
    }

    @FunctionDoc(
        name = "reverse",
        desc = ["This returns the string in reverse"],
        returns = ReturnDoc(StringDef::class, ["The string in reverse."]),
        examples = ["'hello'.reverse(); // 'olleh'"]
    )
    private fun reverse(arguments: Arguments): String {
        return arguments.nextPrimitive(this).reversed()
    }

    @FunctionDoc(
        name = "contains",
        desc = ["This returns whether the string contains the given string"],
        params = [ParameterDoc(StringDef::class, "string", ["The string to check."])],
        returns = ReturnDoc(BooleanDef::class, ["Whether the string contains the given string."]),
        examples = ["'hello'.contains('lo'); // true"]
    )
    private fun contains(arguments: Arguments): Boolean {
        val string = arguments.nextPrimitive(this)
        val substring = arguments.nextPrimitive(this)
        return string.contains(substring)
    }

    @FunctionDoc(
        name = "startsWith",
        desc = ["This returns whether the string starts with the given string"],
        params = [ParameterDoc(StringDef::class, "string", ["The string to check."])],
        returns = ReturnDoc(BooleanDef::class, ["Whether the string starts with the given string."]),
        examples = ["'hello'.startsWith('he'); // true"]
    )
    private fun startsWith(arguments: Arguments): Boolean {
        val string = arguments.nextPrimitive(this)
        val substring = arguments.nextPrimitive(this)
        return string.startsWith(substring)
    }

    @FunctionDoc(
        name = "endsWith",
        desc = ["This returns whether the string ends with the given string"],
        params = [ParameterDoc(StringDef::class, "string", ["The string to check."])],
        returns = ReturnDoc(BooleanDef::class, ["Whether the string ends with the given string."]),
        examples = ["'hello'.endsWith('lo'); // true"]
    )
    private fun endsWith(arguments: Arguments): Boolean {
        val string = arguments.nextPrimitive(this)
        val substring = arguments.nextPrimitive(this)
        return string.endsWith(substring)
    }

    @FunctionDoc(
        name = "format",
        desc = [
            "This formats the string using the given arguments.",
            "This internally uses the Java String.format() method.",
            "For how to use see here: https://www.javatpoint.com/java-string-format"
        ],
        params = [ParameterDoc(ObjectDef::class, "objects", ["The objects to insert."], true)],
        returns = ReturnDoc(StringDef::class, ["The formatted string."]),
        examples = ["'%s %s'.format('hello', 'world'); // 'hello world'"]
    )
    private fun format(arguments: Arguments): String {
        val string = arguments.nextPrimitive(this)
        try {
            return string.format(arguments.getRemaining().map { it.asJava() })
        } catch (e: IllegalFormatException) {
            runtimeError("Couldn't format string: '$string'", e)
        }
    }

    @FunctionDoc(
        name = "toNumber",
        desc = [
            "This tries to convert the string to a number.",
            "This method can convert hex or denary into numbers.",
            "If the string is not a number, it will throw an error"
        ],
        returns = ReturnDoc(NumberDef::class, ["The number."]),
        examples = ["'99'.toNumber(); // 99"]
    )
    private fun toNumber(arguments: Arguments): Number {
        val string = arguments.nextPrimitive(this)
        return try {
            string.toNumber()
        } catch (e: NumberFormatException) {
            runtimeError("Couldn't convert string to number: '$string'", e)
        }
    }

    @FunctionDoc(
        name = "strip",
        desc = ["This strips the whitespace from the string"],
        returns = ReturnDoc(StringDef::class, ["The stripped string."]),
        examples = ["'  hello  '.strip(); // 'hello'"]
    )
    private fun strip(arguments: Arguments): String {
        val string = arguments.nextPrimitive(this)
        return string.trim()
    }

    @FunctionDoc(
        name = "subString",
        desc = ["This returns a substring of the string"],
        params = [
            ParameterDoc(NumberDef::class, "from", ["The start index (inclusive)."]),
            ParameterDoc(NumberDef::class, "to", ["The end index (exclusive)."])
        ],
        returns = ReturnDoc(StringDef::class, ["The substring."]),
        examples = ["'hello'.subString(1, 3); // 'el'"]
    )
    private fun subString(arguments: Arguments): String {
        val string = arguments.nextPrimitive(this)
        val from = arguments.nextPrimitive(NumberDef::class).toInt()
        val to = arguments.nextPrimitive(NumberDef::class).toInt()
        return RuntimeError.wrap {
            string.substring(from, to)
        }
    }

    @FunctionDoc(
        name = "split",
        desc = ["This splits the string into a list of strings based on a regex"],
        params = [ParameterDoc(StringDef::class, "regex", ["The regex to split the string with."])],
        returns = ReturnDoc(ListDef::class, ["The list of strings."]),
        examples = ["'foo/bar/baz'.split('/');"]
    )
    private fun split(arguments: Arguments): List<String> {
        val string = arguments.nextPrimitive(this)
        val regex = arguments.nextPrimitive(this)
        return RuntimeError.wrap {
            string.split(this.safeRegex(regex))
        }
    }

    @FunctionDoc(
        name = "matches",
        desc = ["This returns whether the string matches the given regex"],
        params = [ParameterDoc(StringDef::class, "regex", ["The regex to match the string with."])],
        returns = ReturnDoc(BooleanDef::class, ["Whether the string matches the given regex."]),
        examples = ["'foo'.matches('f.*'); // true"]
    )
    private fun matches(arguments: Arguments): Boolean {
        val string = arguments.nextPrimitive(this)
        val regex = arguments.nextPrimitive(this)
        return string.matches(this.safeRegex(regex))
    }

    @FunctionDoc(
        name = "find",
        desc = [
            "This finds all matches of the regex in the string,",
            "this does not find groups, for that use `<String>.findGroups(regex)`"
        ],
        params = [ParameterDoc(StringDef::class, "regex", ["The regex to search the string with."])],
        returns = ReturnDoc(ListDef::class, ["The list of all instances of the regex in the string."]),
        examples = ["'102i 1i'.find('([\\\\d+])i'); // ['2i', '1i']"]
    )
    private fun find(arguments: Arguments): ArucasList {
        val string = arguments.nextPrimitive(this)
        val regex = arguments.nextPrimitive(this)
        val list = ArucasList()
        for (match in this.safeRegex(regex).findAll(string)) {
            list.add(this.create(match.value))
        }
        return list
    }

    @FunctionDoc(
        name = "findAll",
        desc = [
            "This finds all matches and groups of a regex in the matches in the string",
            "the first group of each match will be the complete match and following",
            "will be the groups of the regex, a group may be empty if it doesn't exist"
        ],
        params = [ParameterDoc(StringDef::class, "regex", ["The regex to search the string with."])],
        returns = ReturnDoc(ListDef::class, ["A list of match groups, which is a list containing matches."]),
        examples = ["'102i 1i'.findAll('([\\\\d+])i'); // [['2i', '2'], ['1i', '1']]"]
    )
    private fun findAll(arguments: Arguments): ArucasList {
        val string = arguments.nextPrimitive(this)
        val regex = arguments.nextPrimitive(this)
        val list = ArucasList()
        for (match in this.safeRegex(regex).findAll(string)) {
            val subList = ArucasList()
            for (group in match.groupValues) {
                subList.add(this.create(group))
            }
            list.add(arguments.interpreter.create(ListDef::class, subList))
        }
        return list
    }

    @FunctionDoc(
        name = "replaceAll",
        desc = ["This replaces all the instances of a regex with the replace string"],
        params = [
            ParameterDoc(StringDef::class, "regex", ["The regex you want to replace."]),
            ParameterDoc(StringDef::class, "replacement", ["The string you want to replace it with."])
        ],
        returns = ReturnDoc(StringDef::class, ["The modified string."]),
        examples = ["'hello'.replaceAll('l', 'x'); // 'hexxo'"]
    )
    private fun replaceAll(arguments: Arguments): String {
        val string = arguments.nextPrimitive(this)
        val regex = arguments.nextPrimitive(this)
        val replacement = arguments.nextPrimitive(this)
        return string.replace(this.safeRegex(regex), replacement)
    }

    @FunctionDoc(
        name = "replaceFirst",
        desc = ["This replaces the first instance of a regex with the replace string"],
        params = [
            ParameterDoc(StringDef::class, "regex", ["The regex you want to replace."]),
            ParameterDoc(StringDef::class, "replacement", ["The string you want to replace it with."])
        ],
        returns = ReturnDoc(StringDef::class, ["The modified string."]),
        examples = ["'hello'.replaceFirst('l', 'x'); // 'hexlo'"]
    )
    private fun replaceFirst(arguments: Arguments): String {
        val string = arguments.nextPrimitive(this)
        val regex = arguments.nextPrimitive(this)
        val replacement = arguments.nextPrimitive(this)
        return string.replaceFirst(this.safeRegex(regex), replacement)
    }

    private fun safeRegex(string: String): Regex {
        return try {
            Regex(string)
        } catch (e: PatternSyntaxException) {
            runtimeError("Invalid regex: '$string'", e)
        }
    }
}