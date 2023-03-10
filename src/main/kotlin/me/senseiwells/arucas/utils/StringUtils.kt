package me.senseiwells.arucas.utils

import java.util.*

/**
 * Utility object with helpful string methods.
 */
object StringUtils {
    /**
     * Converts all instances of `[\'] [\"] [\\] [\r] [\n] [\b] [\t] [\x..] [\u....]` to the correct character.
     *
     * @return the unescaped string.
     */
    @JvmStatic
    fun String.unescape(): String {
        val sb = StringBuilder()
        val len = this.length
        var escape = false
        var i = 0
        while (i < len) {
            val c = this[i]
            if (escape) {
                escape = false
                when (c) {
                    '\'', '\"', '\\' -> sb.append(c)
                    '0' -> sb.append('\u0000')
                    'r' -> sb.append('\r')
                    'n' -> sb.append('\n')
                    'b' -> sb.append('\b')
                    't' -> sb.append('\t')
                    'x' -> {
                        if (i + 3 > this.length) {
                            throw RuntimeException("(index:$i) Not enough characters for '\\x..' escape.")
                        }
                        val hex = this.substring(i + 1, i + 3)
                        try {
                            sb.append(hex.toInt(16).toChar())
                        } catch (e: NumberFormatException) {
                            throw RuntimeException("(index:$i) Invalid escape '\\x$hex'")
                        }
                        i += 2
                    }
                    'u' -> {
                        if (i + 5 > this.length) {
                            throw RuntimeException("(index:$i) Not enough characters for '\\u....' escape.")
                        }
                        val hex = this.substring(i + 1, i + 5)
                        try {
                            sb.append(hex.toInt(16).toChar())
                        } catch (e: NumberFormatException) {
                            throw RuntimeException("(index:$i) Invalid escape '\\u%s'")
                        }
                        i += 4
                    }
                    else -> throw RuntimeException("(index:$i) Invalid character escape '\\$c'")
                }
            } else if (c == '\\') {
                escape = true
            } else {
                sb.append(c)
            }
            i++
        }
        return sb.toString()
    }

    /**
     * Escapes a string to convert all control characters into their escaped form.
     *
     * @return the escaped string.
     */
    @JvmStatic
    fun String.escape(): String {
        val sb = StringBuilder()
        val len = this.length
        var i = 0
        while (i < len) {
            val c = this[i]
            when (c) {
                '\r' -> {
                    sb.append("\\r")
                    i++
                    continue
                }
                '\n' -> {
                    sb.append("\\n")
                    i++
                    continue
                }
                '\b' -> {
                    sb.append("\\b")
                    i++
                    continue
                }
                '\t' -> {
                    sb.append("\\t")
                    i++
                    continue
                }
                '\'' -> {
                    sb.append("\\'")
                    i++
                    continue
                }
                '\"' -> {
                    sb.append("\\\"")
                    i++
                    continue
                }
                '\\' -> {
                    sb.append("\\\\")
                    i++
                    continue
                }
            }
            if (c.code > 0xff) {
                sb.append("\\u").append(toHexString(c.code.toLong(), 4))
                i++
                continue
            }
            if (Character.isISOControl(c)) {
                sb.append("\\x").append(toHexString(c.code.toLong(), 2))
                i++
                continue
            }
            sb.append(c)
            i++
        }
        return sb.toString()
    }

    /**
     * Escapes a string so that it can safely be placed inside a regex expression.
     *
     * @return the regex safe string.
     */
    @JvmStatic
    fun String.regexEscape(): String {
        val sb = StringBuilder()
        var i = 0
        val len = this.length
        while (i < len) {
            val c = this[i]
            when (c) {
                '\u0000' -> {
                    sb.append("\\0")
                    i++
                    continue
                }
                '\n' -> {
                    sb.append("\\n")
                    i++
                    continue
                }
                '\r' -> {
                    sb.append("\\r")
                    i++
                    continue
                }
                '\t' -> {
                    sb.append("\\t")
                    i++
                    continue
                }
                '\\' -> {
                    sb.append("\\\\")
                    i++
                    continue
                }
                '^', '$', '?', '|', '*', '/', '+', '.', '(', ')', '[', ']', '{', '}' -> {
                    sb.append("\\").append(c)
                    i++
                    continue
                }
            }
            if (c.code > 0xff) { // Unicode
                sb.append("\\u").append(toHexString(c.code.toLong(), 4))
                i++
                continue
            }
            if (Character.isISOControl(c)) { // Control character
                sb.append("\\x").append(toHexString(c.code.toLong(), 2))
                i++
                continue
            }
            sb.append(c)
            i++
        }
        return sb.toString()
    }

    /**
     * Converts a string into a number.
     *
     * If the input string has a negative sign it will be handled correctly.
     *
     * ```
     * Binary:
     * 0[bB][01]+
     *
     * Hexadecimal:
     * 0[xX][0-9a-fA-F]+
     *
     * Octodecimal:
     * 0[0-7]+
     *
     * Decimal:
     * [0-9]+(\.[0-9]+)?
     * ```
     *
     * @return the number as a double.
     */
    @JvmStatic
    fun String.toNumber(): Double {
        var s = this
        require(s.isNotBlank()) { "Cannot convert an empty string to a number" }

        // First check if the value is negative.
        val isNegative = s[0] == '-'
        if (isNegative) {
            // If the string is negative we remove the first character.
            s = s.substring(1)
        }

        // Check for hex
        val result = if (s.startsWith("0x")) s.substring(2).toLong(16).toDouble() else s.toDouble()
        return if (isNegative) -result else result
    }

    /**
     * Ensures that the first character of the first string is
     * capital and also ensures the last character of the last
     * string ends with a full stop.
     *
     * @param strings the array of strings to punctuate.
     * @return the punctuated array of strings.
     */
    fun punctuate(strings: Array<String>): Array<String> {
        if (strings.isNotEmpty()) {
            strings[0] = strings[0].capitalise()
            val last = strings[strings.lastIndex]
            if (!last.endsWith(".")) {
                strings[strings.lastIndex] = "$last."
            }
        }
        return strings
    }

    /**
     * Capitalises a string.
     *
     * @return the capitalised string.
     */
    fun String.capitalise(): String {
        return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    /**
     * Converts a number into a hex string with a given minimum length.
     *
     * @param value  the value to be converted to a hex string
     * @param length the minimum length of that hex string
     * @return a hex string
     */
    private fun toHexString(value: Long, length: Int): String {
        require(length >= 1) { "The minimum length of the returned string cannot be less than one." }
        return String.format("%0" + length + "x", value)
    }
}