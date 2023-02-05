package me.senseiwells.arucas.api.docs.visitor

import me.senseiwells.arucas.api.ArucasExtension
import me.senseiwells.arucas.classes.PrimitiveDefinition

/**
 * This visitor allows you to visit the different
 * aspects of the documentation for the [ArucasExtension]
 * and [PrimitiveDefinition], this is intended
 * for data generation such as converting the documentation
 * into a markdown format, see [].
 *
 * The order of which documentation is visited is
 * pre-defined, you can see [ArucasDocParser].
 * Your visitor must be added to the parser
 * with [ArucasDocParser.addVisitor].
 *
 * @see ArucasDocParser
 */
interface ArucasDocVisitor {
    /**
     * This visits an [ExtensionDoc]. Following this
     * [visitExtensionFunction] will be called with the
     * functions from the extension.
     *
     * @param extensionDoc the documentation for the extension.
     */
    fun visitExtension(extensionDoc: ExtensionDoc) { }

    /**
     * This visits a [FunctionDoc] from an extension.
     *
     * @param functionDoc the documentation for the extension function.
     * @param extensionDoc the extension documentation where the function is defined in.
     */
    fun visitExtensionFunction(extensionDoc: ExtensionDoc, functionDoc: FunctionDoc) { }

    /**
     * This visits a [ClassDoc]. Following this [visitStaticField],
     * [visitConstructor], [visitMethod], and [visitStaticMethod] will
     * be called with the properties from the class.
     *
     * @param classDoc the documentation for the class.
     */
    fun visitClass(classDoc: ClassDoc) { }

    /**
     * This visits a [FieldDoc] from a class.
     *
     * @param fieldDoc the documentation for the static class field.
     * @param classDoc the class that the field was defined in.
     */
    fun visitStaticField(classDoc: ClassDoc, fieldDoc: FieldDoc) { }

    /**
     * This visits a [ConstructorDoc] from a class.
     *
     * @param constructorDoc the documentation for the class constructor.
     * @param classDoc the class that the constructor was defined in.
     */
    fun visitConstructor(classDoc: ClassDoc, constructorDoc: ConstructorDoc) { }

    /**
     * This visits a [FunctionDoc] (method) from a class.
     *
     * @param functionDoc the documentation for the class method.
     * @param classDoc the class that the function was defined in.
     */
    fun visitMethod(classDoc: ClassDoc, functionDoc: FunctionDoc) { }

    /**
     * This visits a [FunctionDoc] (static method) from a class.
     *
     * @param functionDoc the documentation for the static class method.
     * @param classDoc the class that the function was defined in.
     */
    fun visitStaticMethod(classDoc: ClassDoc, functionDoc: FunctionDoc) { }
}