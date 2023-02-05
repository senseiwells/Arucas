package me.senseiwells.arucas.api.docs.visitor

/**
 * This interface provides method for
 * getting descriptions for a documentation class.
 */
sealed interface Describable {
    /**
     * This gets the description as an array.
     *
     * @return the description as an array.
     */
    fun getDescription(): Array<String>

    /**
     * This formats [getDescription] with a [separator].
     *
     * @return the formatted description.
     */
    fun getFormattedDescription(separator: String = " "): String {
        return this.getDescription().joinToString(separator)
    }
}