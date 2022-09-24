package me.senseiwells.arucas.utils

import me.senseiwells.arucas.core.Interpreter

abstract class Trace(val fileName: String) {
    companion object {
        @JvmStatic
        val INTERNAL = InternalTrace(null)
    }

    override fun toString(): String {
        return "File: ${this.fileName}"
    }

    open fun toString(interpreter: Interpreter, message: String?): String {
        return this.toString()
    }
}

open class LocatableTrace(fileName: String, val fileContent: String, val line: Int, val column: Int): Trace(fileName) {
    override fun toString(): String {
        return "${super.toString()}, Line: ${this.line + 1}, Column: ${this.column + 1}"
    }

    fun errorFormat(message: String?, maxLength: Int = 60): String {
        val lines = this.fileContent.lines()
        val errorLine = this.line + 1
        val padSize = Math.max(1, Math.ceil(Math.log10(errorLine.toDouble())).toInt());
        val padFormat = "%" + padSize + "d"
        val numPadding = " ".repeat(padSize)

        var errorStart = this.column
        var errorEnd = this.column + 1
        var errorString = lines[errorLine - 1]
        
        if (errorStart > maxLength / 2) {
            val diff = errorStart - (maxLength / 2)
            errorStart -= diff - 4
            errorEnd -= diff - 4
            errorString = "... " + errorString.substring(diff)
        }

        if (errorString.length > maxLength - 4) {
            if (errorEnd > maxLength - 4) {
                errorEnd = maxLength - 4
            }

            errorString = errorString.substring(0, maxLength - 4) + " ..."
        }

        val sb = StringBuilder()

        sb.append("\n")
            .append(String.format(padFormat, errorLine)).append(" | ").append(errorString).append("\n")
            .append(numPadding).append(" | ").append(" ".repeat(errorStart)).append("^".repeat(errorEnd - errorStart))

        if (message != null) {
            sb.append("\n").append(numPadding).append(" | ").append(" ".repeat(errorStart)).append(message)
        }

        return sb.toString()
    }

    open fun toString(interpreter: Interpreter, detail: String?, message: String?): String {
        return "${toString()}${detail ?: ""}${errorFormat(message, interpreter.properties.errorMaxLength)}"
    }

    override fun toString(interpreter: Interpreter, message: String?): String {
        return toString(interpreter, null, message)
    }
}

open class CallTrace(fileName: String, fileContent: String, line: Int, column: Int, private val callName: String): LocatableTrace(fileName, fileContent, line, column) {
    constructor(trace: LocatableTrace, string: String): this(trace.fileName, trace.fileContent, trace.line, trace.column, string)

    override fun toString(): String {
        return "${super.toString()}, In: ${this.callName}"
    }
}

class InternalTrace(private val details: String?): CallTrace("\$internal", "", 0, 0, "") {
    override fun toString(): String {
        return "File: ${this.fileName}${if (this.details == null) "" else ", ${this.details}"}"
    }

    override fun toString(interpreter: Interpreter, detail: String?, message: String?): String {
        return toString()
    }
}