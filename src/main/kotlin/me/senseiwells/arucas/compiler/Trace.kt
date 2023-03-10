package me.senseiwells.arucas.compiler

import me.senseiwells.arucas.interpreter.Interpreter

abstract class Trace(val fileName: String) {
    companion object {
        @JvmField
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

    open fun toString(interpreter: Interpreter, detail: String?, message: String?): String {
        return "${toString()}${detail ?: ""}${interpreter.api.getOutput().formatStackTrace(interpreter, message, this)}"
    }

    override fun toString(interpreter: Interpreter, message: String?): String {
        return this.toString(interpreter, null, message)
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