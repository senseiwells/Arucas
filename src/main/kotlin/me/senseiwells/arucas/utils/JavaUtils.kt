package me.senseiwells.arucas.utils

import me.senseiwells.arucas.exceptions.RuntimeError
import me.senseiwells.arucas.extensions.JavaDef
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.experimental.xor

/**
 * Utility object that allows for appling operators to
 * [Any] java values.
 *
 * @see JavaDef
 */
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

    fun shiftLeft(first: Any, second: Any?, default: Any): Any {
        return binarySimilar(first, second, default, { a, b -> (a.toLong() shl b.toInt()).toByte() }, { a, b -> (a.toLong() shl b.toInt()).toShort() }, { a, b -> a shl b }, { a, b -> a shl b.toInt() })
    }

    fun shiftRight(first: Any, second: Any?, default: Any): Any {
        return binarySimilar(first, second, default, { a, b -> (a.toLong() shr b.toInt()).toByte() }, { a, b -> (a.toLong() shr b.toInt()).toShort() }, { a, b -> a shr b }, { a, b -> a shr b.toInt() })
    }

    fun bracketAccess(first: Any, second: Any?, default: Any): Any? {
        RuntimeError.wrap {
            return when (first) {
                is Array<*> -> when (second) {
                    is Number -> first[second.toInt()]
                    else -> default
                }
                is ByteArray -> when (second) {
                    is Number -> first[second.toInt()]
                    else -> default
                }
                is ShortArray -> when (second) {
                    is Number -> first[second.toInt()]
                    else -> default
                }
                is IntArray -> when (second) {
                    is Number -> first[second.toInt()]
                    else -> default
                }
                is LongArray -> when (second) {
                    is Number -> first[second.toInt()]
                    else -> default
                }
                is FloatArray -> when (second) {
                    is Number -> first[second.toInt()]
                    else -> default
                }
                is DoubleArray -> when (second) {
                    is Number -> first[second.toInt()]
                    else -> default
                }
                is CharArray -> when (second) {
                    is Number -> first[second.toInt()]
                    else -> default
                }
                is BooleanArray -> when (second) {
                    is Number -> first[second.toInt()]
                    else -> default
                }
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
                is Array<*> -> when (second) {
                    is Number -> (first as Array<Any?>)[second.toInt()] = third
                    else -> default
                }
                is ByteArray -> when {
                    second is Number && third is Number -> first[second.toInt()] = third.toByte()
                    else -> default
                }
                is ShortArray -> when {
                    second is Number && third is Number -> first[second.toInt()] = third.toShort()
                    else -> default
                }
                is IntArray -> when {
                    second is Number && third is Number -> first[second.toInt()] = third.toInt()
                    else -> default
                }
                is LongArray -> when {
                    second is Number && third is Number -> first[second.toInt()] = third.toLong()
                    else -> default
                }
                is FloatArray -> when {
                    second is Number && third is Number -> first[second.toInt()] = third.toFloat()
                    else -> default
                }
                is DoubleArray -> when {
                    second is Number && third is Number -> first[second.toInt()] = third.toDouble()
                    else -> default
                }
                is CharArray -> when (second) {
                    is Number -> when {
                        third is Number -> first[second.toInt()] = third.toChar()
                        third is Char -> first[second.toInt()] = third
                        third is CharSequence && third.length == 1 -> first[second.toInt()] = third[0]
                        else -> default
                    }
                    else -> default
                }
                is BooleanArray -> when {
                    second is Number && third is Boolean -> first[second.toInt()] = third
                    else -> default
                }
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
            else -> return binarySimilar(first, second, default, bothByte, bothShort, bothInt, bothLong)
        }
    }

    private fun binarySimilar(
        first: Any,
        second: Any?,
        default: Any,
        bothByte: (Byte, Byte) -> Byte,
        bothShort: (Short, Short) -> Short,
        bothInt: (Int, Int) -> Int,
        bothLong: (Long, Long) -> Long
    ): Any {
        return when (first) {
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
            else -> binarySimilar(first, second, default, bothInt, bothLong)
        }
    }

    private fun binarySimilar(
        first: Any,
        second: Any?,
        default: Any,
        bothInt: (Int, Int) -> Int,
        bothLong: (Long, Long) -> Long
    ): Any {
        return when (first) {
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