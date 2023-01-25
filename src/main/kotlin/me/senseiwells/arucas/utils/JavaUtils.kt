package me.senseiwells.arucas.utils

import me.senseiwells.arucas.exceptions.RuntimeError
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.experimental.xor

object JavaUtils {
    fun not(first: Any, default: Any): Any {
        return when (first) {
            is Boolean -> !first
            else -> default
        }
    }

    fun plus(first: Any, default: Any): Any {
        return when (first) {
            is Byte -> +first
            is Int -> +first
            is Short -> +first
            is Long -> +first
            is Float -> +first
            is Double -> +first
            else -> default
        }
    }

    fun minus(first: Any, default: Any): Any {
        return when (first) {
            is Byte -> -first
            is Int -> -first
            is Short -> -first
            is Long -> -first
            is Float -> -first
            is Double -> -first
            else -> default
        }
    }

    fun add(first: Any?, second: Any?, default: Any): Any {
        return when (first) {
            is Byte -> when (second) {
                is Byte -> first + second
                is Short -> first + second
                is Int -> first + second
                is Long -> first + second
                is Float -> first + second
                is Double -> first + second
                else -> default
            }
            is Int -> when (second) {
                is Byte -> first + second
                is Short -> first + second
                is Int -> first + second
                is Long -> first + second
                is Float -> first + second
                is Double -> first + second
                else -> default
            }
            is Short -> when (second) {
                is Byte -> first + second
                is Short -> first + second
                is Int -> first + second
                is Long -> first + second
                is Float -> first + second
                is Double -> first + second
                else -> default
            }
            is Long -> when (second) {
                is Byte -> first + second
                is Short -> first + second
                is Int -> first + second
                is Long -> first + second
                is Float -> first + second
                is Double -> first + second
                else -> default
            }
            is Float -> when (second) {
                is Byte -> first + second
                is Short -> first + second
                is Int -> first + second
                is Long -> first + second
                is Float -> first + second
                is Double -> first + second
                else -> default
            }
            is Double -> when (second) {
                is Byte -> first + second
                is Short -> first + second
                is Int -> first + second
                is Long -> first + second
                is Float -> first + second
                is Double -> first + second
                else -> default
            }
            is String -> first + second
            else -> default
        }
    }

    fun minus(first: Any, second: Any?, default: Any): Any {
        return when (first) {
            is Byte -> when (second) {
                is Byte -> first - second
                is Short -> first - second
                is Int -> first - second
                is Long -> first - second
                is Float -> first - second
                is Double -> first - second
                else -> default
            }
            is Int -> when (second) {
                is Byte -> first - second
                is Short -> first - second
                is Int -> first - second
                is Long -> first - second
                is Float -> first - second
                is Double -> first - second
                else -> default
            }
            is Short -> when (second) {
                is Byte -> first - second
                is Short -> first - second
                is Int -> first - second
                is Long -> first - second
                is Float -> first - second
                is Double -> first - second
                else -> default
            }
            is Long -> when (second) {
                is Byte -> first - second
                is Short -> first - second
                is Int -> first - second
                is Long -> first - second
                is Float -> first - second
                is Double -> first - second
                else -> default
            }
            is Float -> when (second) {
                is Byte -> first - second
                is Short -> first - second
                is Int -> first - second
                is Long -> first - second
                is Float -> first - second
                is Double -> first - second
                else -> default
            }
            is Double -> when (second) {
                is Byte -> first - second
                is Short -> first - second
                is Int -> first - second
                is Long -> first - second
                is Float -> first - second
                is Double -> first - second
                else -> default
            }
            else -> default
        }
    }

    fun multiply(first: Any, second: Any?, default: Any): Any {
        return when (first) {
            is Byte -> when (second) {
                is Byte -> first * second
                is Short -> first * second
                is Int -> first * second
                is Long -> first * second
                is Float -> first * second
                is Double -> first * second
                else -> default
            }
            is Int -> when (second) {
                is Byte -> first * second
                is Short -> first * second
                is Int -> first * second
                is Long -> first * second
                is Float -> first * second
                is Double -> first * second
                else -> default
            }
            is Short -> when (second) {
                is Byte -> first * second
                is Short -> first * second
                is Int -> first * second
                is Long -> first * second
                is Float -> first * second
                is Double -> first * second
                else -> default
            }
            is Long -> when (second) {
                is Byte -> first * second
                is Short -> first * second
                is Int -> first * second
                is Long -> first * second
                is Float -> first * second
                is Double -> first * second
                else -> default
            }
            is Float -> when (second) {
                is Byte -> first * second
                is Short -> first * second
                is Int -> first * second
                is Long -> first * second
                is Float -> first * second
                is Double -> first * second
                else -> default
            }
            is Double -> when (second) {
                is Byte -> first * second
                is Short -> first * second
                is Int -> first * second
                is Long -> first * second
                is Float -> first * second
                is Double -> first * second
                else -> default
            }
            else -> default
        }
    }

    fun divide(first: Any, second: Any?, default: Any): Any {
        return when (first) {
            is Byte -> when (second) {
                is Byte -> first / second
                is Short -> first / second
                is Int -> first / second
                is Long -> first / second
                is Float -> first / second
                is Double -> first / second
                else -> default
            }
            is Int -> when (second) {
                is Byte -> first / second
                is Short -> first / second
                is Int -> first / second
                is Long -> first / second
                is Float -> first / second
                is Double -> first / second
                else -> default
            }
            is Short -> when (second) {
                is Byte -> first / second
                is Short -> first / second
                is Int -> first / second
                is Long -> first / second
                is Float -> first / second
                is Double -> first / second
                else -> default
            }
            is Long -> when (second) {
                is Byte -> first / second
                is Short -> first / second
                is Int -> first / second
                is Long -> first / second
                is Float -> first / second
                is Double -> first / second
                else -> default
            }
            is Float -> when (second) {
                is Byte -> first / second
                is Short -> first / second
                is Int -> first / second
                is Long -> first / second
                is Float -> first / second
                is Double -> first / second
                else -> default
            }
            is Double -> when (second) {
                is Byte -> first / second
                is Short -> first / second
                is Int -> first / second
                is Long -> first / second
                is Float -> first / second
                is Double -> first / second
                else -> default
            }
            else -> default
        }
    }

    fun and(first: Any, second: Any?, default: Any): Any {
        return when (first) {
            is Boolean -> when (second) {
                is Boolean -> first && second
                else -> default
            }
            else -> default
        }
    }

    fun or(first: Any, second: Any?, default: Any): Any {
        return when (first) {
            is Boolean -> when (second) {
                is Boolean -> first || second
                else -> default
            }
            else -> default
        }
    }

    fun xor(first: Any, second: Any?, default: Any): Any {
        return binarySimilar(first, second, default, Boolean::xor, Byte::xor, Short::xor, Int::xor, Long::xor)
    }

    fun bitAnd(first: Any, second: Any?, default: Any): Any {
        return binarySimilar(first, second, default, Boolean::and, Byte::and, Short::and, Int::and, Long::and)
    }

    fun bitOr(first: Any, second: Any?, default: Any): Any {
        return binarySimilar(first, second, default, Boolean::or, Byte::or, Short::or, Int::or, Long::or)
    }

    fun bracketAccess(first: Any, second: Any?, default: Any): Any? {
        RuntimeError.wrap {
            return when (first) {
                is List<*> -> when (second) {
                    is Number -> first[second.toInt()]
                    else -> default
                }
                is Map<*, *> -> first[second]
                else -> default
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun bracketAssign(first: Any, second: Any?, third: Any?, default: Any): Any? {
        RuntimeError.wrap {
            return when (first) {
                is MutableList<*> -> when (second) {
                    is Number -> (first as MutableList<Any?>).set(second.toInt(), third)
                    else -> default
                }
                is MutableMap<*, *> -> (first as MutableMap<Any?, Any?>).put(second, third)
                else -> default
            }
        }
    }

    private fun binarySimilar(
        first: Any,
        second: Any?,
        default: Any,
        bothBoolean: (Boolean, Boolean) -> Boolean,
        bothByte: (Byte, Byte) -> Byte,
        bothShort: (Short, Short) -> Short,
        bothInt: (Int, Int) -> Int,
        bothLong: (Long, Long) -> Long
    ): Any {
        return when (first) {
            is Boolean -> when (second) {
                is Boolean -> bothBoolean(first, second)
                else -> default
            }
            is Byte -> when (second) {
                is Byte -> bothByte(first, second)
                is Short -> bothByte(first, second.toByte())
                is Int -> bothByte(first, second.toByte())
                is Long -> bothByte(first, second.toByte())
                else -> default
            }
            is Short -> when (second) {
                is Byte -> bothShort(first, second.toShort())
                is Short -> bothShort(first, second)
                is Int -> bothShort(first, second.toShort())
                is Long -> bothShort(first, second.toShort())
                else -> default
            }
            is Int -> when (second) {
                is Byte -> bothInt(first, second.toInt())
                is Short -> bothInt(first, second.toInt())
                is Int -> bothInt(first, second)
                is Long -> bothInt(first, second.toInt())
                else -> default
            }
            is Long -> when (second) {
                is Byte -> bothLong(first, second.toLong())
                is Short -> bothLong(first, second.toLong())
                is Int -> bothLong(first, second.toLong())
                is Long -> bothLong(first, second)
                else -> default
            }
            else -> default
        }
    }
}