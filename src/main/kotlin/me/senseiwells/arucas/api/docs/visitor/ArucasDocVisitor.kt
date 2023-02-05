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
abstract class ArucasDocVisitor {
    /**
     * This visits all the children components. Overriding this
     * will prevent the other methods from being called.
     *
     * @param extensionDoc the documentation for the extension.
     * @param functions the list of function documentation.
     */
    open fun visitExtension(extensionDoc: ExtensionDoc, functions: List<FunctionDoc>) {
        this.visitExtension(extensionDoc)

        functions.forEach { this.visitExtensionFunction(extensionDoc, it) }
    }

    /**
     * This visits an [ExtensionDoc]. Following this
     * [visitExtensionFunction] will be called with the
     * functions from the extension.
     *
     * @param extensionDoc the documentation for the extension.
     */
    protected open fun visitExtension(extensionDoc: ExtensionDoc) { }

    /**
     * This visits a [FunctionDoc] from an extension.
     *
     * @param extensionDoc the extension documentation where the function is defined in.
     * @param functionDoc the documentation for the extension function.
     */
    protected open fun visitExtensionFunction(extensionDoc: ExtensionDoc, functionDoc: FunctionDoc) { }

    /**
     * This visits all the child components. Overriding this
     * will prevent the other methods from being called.
     *
     * @param classDoc the documentation for the class.
     * @param fields the field documentation.
     * @param constructors the constructor documentation.
     * @param methods the method documentation.
     * @param staticMethods the static method documentation.
     */
    open fun visitClass(classDoc: ClassDoc, fields: List<FieldDoc>, constructors: List<ConstructorDoc>, methods: List<FunctionDoc>, staticMethods: List<FunctionDoc>) {
        this.visitClass(classDoc)

        fields.forEach { this.visitStaticField(classDoc, it) }
        constructors.forEach { this.visitConstructor(classDoc, it) }
        methods.forEach { this.visitMethod(classDoc, it) }
        staticMethods.forEach { this.visitStaticMethod(classDoc, it) }
    }

    /**
     * This visits a [ClassDoc]. Following this [visitStaticField],
     * [visitConstructor], [visitMethod], and [visitStaticMethod] will
     * be called with the properties from the class.
     *
     * @param classDoc the documentation for the class.
     */
    protected open fun visitClass(classDoc: ClassDoc) { }

    /**
     * This visits a [FieldDoc] from a class.
     *
     * @param classDoc the class that the function was defined in.
     * @param fieldDoc the documentation for the static class field.
     */
    protected open fun visitStaticField(classDoc: ClassDoc, fieldDoc: FieldDoc) { }

    /**
     * This visits a [ConstructorDoc] from a class.
     *
     * @param classDoc the class that the function was defined in.
     * @param constructorDoc the documentation for the class constructor.
     */
    protected open fun visitConstructor(classDoc: ClassDoc, constructorDoc: ConstructorDoc) { }

    /**
     * This visits a [FunctionDoc] (method) from a class.
     *
     * @param classDoc the class that the function was defined in.
     * @param functionDoc the documentation for the class method.
     */
    protected open fun visitMethod(classDoc: ClassDoc, functionDoc: FunctionDoc) { }

    /**
     * This visits a [FunctionDoc] (static method) from a class.
     *
     * @param classDoc the class that the function was defined in.
     * @param functionDoc the documentation for the static class method.
     */
    protected open fun visitStaticMethod(classDoc: ClassDoc, functionDoc: FunctionDoc) { }
}