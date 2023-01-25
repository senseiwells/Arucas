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
        @JvmStatic
        val default = object: ArucasObfuscator { }
    }

    /**
     * Whether Arucas should try and obfuscate the names.
     */
    fun shouldObfuscate(): Boolean = false

    /**
     * Used to obfuscate a Java class name.
     */
    fun obfuscateClassName(name: String) = name

    /**
     * Used to obfuscate a Java method name.
     */
    fun obfuscateMethodName(clazz: Class<*>, name: String) = name

    /**
     * Used to obfuscate a Java field name.
     */
    fun obfuscateFieldName(clazz: Class<*>, name: String) = name

    /**
     * Used to deobfuscate a Java class name
     */
    fun deobfuscateClass(clazz: Class<*>): String = clazz.simpleName
}