package me.senseiwells.arucas.utils

object TimeUtils {
    fun nanosToString(nanos: Long): String {
        var unit = "μs"
        var time = nanos / 1_000
        if (time > 5_000) {
            unit = "ms"
            time /= 1_000
        }
        if (time > 10_000) {
            unit = "s"
            time /= 1_000
        }
        return "$time$unit"
    }
}