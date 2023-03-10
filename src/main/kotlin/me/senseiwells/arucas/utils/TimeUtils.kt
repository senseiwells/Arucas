package me.senseiwells.arucas.utils

/**
 * Utility object that provides functions for time.
 */
object TimeUtils {
    /**
     * Formats a number of nanoseconds into microseconds, milliseconds, or seconds.
     *
     * @param nanos the number of nanoseconds.
     * @return the formatted time in microseconds, milliseconds, or seconds.
     */
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