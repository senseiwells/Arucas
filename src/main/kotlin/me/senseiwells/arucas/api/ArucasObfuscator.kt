package me.senseiwells.arucas.api

/**
 * Obfuscation API, this exists to allow for interactions with Java.
 *
 * We need to handle deobfuscation for reflection.
 * For example Minecraft obfuscates its code so to access
 * methods we need to obfuscate and deobfuscate names.
 */
interface ArucasObfuscator {
    companion object {
        /**
         * The default [ArucasObfuscator], this essentially does nothing.
         */
        @JvmStatic
        val default = object: ArucasObfuscator { }
    }

    /**
     * Whether Arucas should try and obfuscate the names.
     *
     * @return whether to obfuscate names.
     */
    fun shouldObfuscate(): Boolean = false

    /**
     * Used to obfuscate a Java class name.
     *
     * @param name the name of the class to obfuscate.
     * @return the obfuscated name of the class.
     */
    fun obfuscateClassName(name: String) = name

    /**
     * Used to obfuscate a Java method name.
     *
     * @param clazz the class that the method belongs to.
     * @param name the name of the method.
     * @return the obfuscated name of the method.
     */
    fun obfuscateMethodName(clazz: Class<*>, name: String) = name

    /**
     * Used to obfuscate a Java field name.
     *
     * @param clazz the class that the field belongs to.
     * @param name the name of the field.
     * @return the obfuscated name of the field.
     */
    fun obfuscateFieldName(clazz: Class<*>, name: String) = name

    /**
     * Used to deobfuscate a Java class name
     *
     * @param clazz the class to deobfuscate.
     * @return the deobfuscated name of the class.
     */
    fun deobfuscateClass(clazz: Class<*>): String = clazz.simpleName
}