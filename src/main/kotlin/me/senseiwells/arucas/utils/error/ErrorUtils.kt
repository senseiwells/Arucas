package me.senseiwells.arucas.utils.error

import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.utils.Trace

object ErrorUtils {
    fun format(trace: Trace, interpreter: Interpreter?, message: String?): String? {
         if (interpreter == null || trace.fileName != interpreter.name) {
             // This interpreter does not belong to this trace
             return null
         }

        return format(trace, interpreter.content, message)
    }

    fun format(trace: Trace, fileContent: String, message: String?): String? {
        // TODO: This code is a proof of concept for printing more informational error messages
        //       This will replace all messages made by LocatableTrace messages and add additional
        //       information to the message.
        //       -
        //       There are errors if trace.line is outside the list size of 'lines' and some formatting
        //       has not been applied.

        if (trace is LocatableTrace) {
            val lines: List<String> = fileContent.lines()

            val errorLine = trace.line
            val errorStart = trace.column
            val errorEnd = trace.column + 1

            // Make sure we cannot throw an index of out bounds exception
            if (lines.size <= errorLine) {
                return null
            }

            val padSize: Int = Math.max(1, Math.ceil(Math.log10(errorLine.toDouble())).toInt());
            val padFormat: String = "%" + padSize + "d"
            val numPadding: String = " ".repeat(padSize)

            val sb = StringBuilder()

            sb.append("\n")
                .append(String.format(padFormat, errorLine + 1)).append(" | ").append(lines[errorLine]).append("\n")
                .append(numPadding).append(" | ").append(" ".repeat(errorStart)).append("^".repeat(errorEnd - errorStart))

            if (message != null) {
                sb.append("\n").append(numPadding).append(" | ").append(" ".repeat(errorStart)).append(message)
            }

            return sb.toString()
        }

        return null
    }
}
