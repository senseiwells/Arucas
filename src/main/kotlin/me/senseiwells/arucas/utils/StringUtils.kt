package me.senseiwells.arucas.utils

object StringUtils {
    /**
     * Joins all arguments using `StringBuilder`.
     */
    fun join(vararg args: Any?): String {
        val length = args.size
        if (length == 0) {
            return ""
        }
        val sb = StringBuilder()
        for (arg in args) {
            sb.append(arg)
        }
        return sb.toString()
    }

    /**
     * Converts all instances of `[\'] [\"] [\\] [\r] [\n] [\b] [\t] [\x..] [\u....]` to the correct character.
     */
    fun unescapeString(string: String): String {
        val sb = StringBuilder()
        val len = string.length
        var escape = false
        var i = 0
        while (i < len) {
            val c = string[i]
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
                        if (i + 3 > string.length) {
                            throw RuntimeException("(index:$i) Not enough characters for '\\x..' escape.")
                        }
                        val hex = string.substring(i + 1, i + 3)
                        try {
                            sb.append(hex.toInt(16).toChar())
                        } catch (e: NumberFormatException) {
                            throw RuntimeException("(index:$i) Invalid escape '\\x$hex'")
                        }
                        i += 2
                    }
                    'u' -> {
                        if (i + 5 > string.length) {
                            throw RuntimeException("(index:$i) Not enough characters for '\\u....' escape.")
                        }
                        val hex = string.substring(i + 1, i + 5)
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
     */
    fun escapeString(string: String): String {
        val sb = StringBuilder()
        val len = string.length
        var i = 0
        while (i < len) {
            val c = string[i]
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
     */
    fun regexEscape(string: String): String {
        val sb = StringBuilder()
        var i = 0
        val len = string.length
        while (i < len) {
            val c = string[i]
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
     */
    fun parseNumber(string: String): Double {
        var s = string
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
}