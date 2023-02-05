package me.senseiwells.arucas.api.docs.visitor

import me.senseiwells.arucas.api.ArucasAPI
import me.senseiwells.arucas.api.ArucasExtension
import me.senseiwells.arucas.builtin.ObjectDef
import me.senseiwells.arucas.classes.ClassDefinition
import me.senseiwells.arucas.classes.PrimitiveDefinition
import me.senseiwells.arucas.core.Interpreter
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*
import kotlin.reflect.KClass
import me.senseiwells.arucas.api.docs.annotations.ClassDoc as ClassDocAnnotation
import me.senseiwells.arucas.api.docs.annotations.ConstructorDoc as ConstructorDocAnnotation
import me.senseiwells.arucas.api.docs.annotations.ExtensionDoc as ExtensionDocAnnotation
import me.senseiwells.arucas.api.docs.annotations.FieldDoc as FieldDocAnnotation
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc as FunctionDocAnnotation
import me.senseiwells.arucas.api.docs.annotations.ParameterDoc as ParameterDocAnnotation
import me.senseiwells.arucas.api.docs.annotations.ReturnDoc as ReturnDocAnnotation

/**
 * This is a parser that parses [ClassDefinition] and [ArucasExtension]
 * documentation. It then visits each part of the documentation with
 * [ArucasDocVisitor]s.
 *
 * Extensions are parsed first with the functions being
 * visited in alphabetical order (and from the least parameters to most).
 * Classes are parsed starting with static fields, then constructors,
 * then constructors, then methods, then static methods.
 *
 * ```kt
 * val api = ArucasAPI.Builder().addDefault().build()
 * val parser = ArucasDocParser(api)
 * parser.addVisitor(visitor)
 * parser.parse()
 * ```
 *
 * @see ArucasDocVisitor
 */
class ArucasDocParser(private val api: ArucasAPI) {
    private val universe = TreeMap<String, ClassDefinition>()
    private val builtin = ArrayList<ClassDefinition>()
    private val modules = ArrayList<ClassDefinition>()
    private val extensions = ArrayList<ArucasExtension>()
    private val visitors = ArrayList<ArucasDocVisitor>()

    init {
        this.loadApi()
    }

    /**
     * This adds an [ArucasDocVisitor] to the parser which
     * will be called when [parse] is called.
     *
     * @param visitor the visitor to add.
     */
    fun addVisitor(visitor: ArucasDocVisitor) {
        this.visitors.add(visitor)
    }

    /**
     * This parses all the extensions and definitions
     * (including built-in and modules) in alphabetical,
     * visiting all the [visitors]. Visiting extensions
     * then the classes.
     */
    fun parse() {
        this.parseExtensions()
        for (definition in this.universe.values) {
            this.parseClass(definition)
        }
    }

    /**
     * This parses all the extensions in alphabetical order.
     */
    fun parseExtensions() {
        for (extension in this.extensions) {
            this.parseExtension(extension)
        }
    }

    /**
     * This parses only the built-in definitions in alphabetical order.
     */
    fun parseBuiltIn() {
        for (definition in this.builtin) {
            this.parseClass(definition)
        }
    }

    /**
     * This parses only the module definitions in alphabetical order.
     */
    fun parseModules() {
        for (definition in this.modules) {
            this.parseClass(definition)
        }
    }

    private fun parseExtension(extension: ArucasExtension) {
        val extensionDoc = this.getExtensionDoc(extension)

        this.visitors.forEach { it.visitExtension(extensionDoc) }

        val functions = LinkedList<FunctionDoc>()
        for (method in extension::class.java.declaredMethods) {
            functions.add(this.getFunctionDoc(method) ?: continue)
        }
        this.sortFunctions(functions)
        for (function in functions) {
            this.visitors.forEach { it.visitExtensionFunction(extensionDoc, function) }
        }
    }

    private fun parseClass(definition: ClassDefinition) {
        val definitionClass = definition::class.java
        val classDoc = this.getClassDoc(definitionClass)

        this.visitors.forEach { it.visitClass(classDoc) }

        val staticFields = LinkedList<FieldDoc>()
        val constructors = LinkedList<ConstructorDoc>()
        val methods = LinkedList<FunctionDoc>()
        val staticMethods = LinkedList<FunctionDoc>()

        for (method in definitionClass.declaredMethods) {
            val constructorDoc = this.getConstructorDoc(method)
            if (constructorDoc != null) {
                constructors.add(constructorDoc)
                continue
            }
            val functionDoc = this.getFunctionDoc(method) ?: continue
            if (functionDoc.isStatic()) {
                staticMethods.add(functionDoc)
            } else {
                methods.add(functionDoc)
            }
        }
        for (field in definitionClass.declaredFields) {
            val fieldDoc = this.getFieldDoc(field) ?: continue
            if (fieldDoc.isStatic()) {
                staticFields.add(fieldDoc)
            }
        }

        staticFields.sortWith { a, b -> a.getName().compareTo(b.getName()) }
        constructors.sortWith { a, b -> a.getParameterCount().compareTo(b.getParameterCount()) }
        this.sortFunctions(methods)
        this.sortFunctions(staticMethods)

        this.visitors.forEach { v -> staticFields.forEach { v.visitStaticField(classDoc, it) } }
        this.visitors.forEach { v -> constructors.forEach { v.visitConstructor(classDoc, it) } }
        this.visitors.forEach { v -> methods.forEach { v.visitMethod(classDoc, it) } }
        this.visitors.forEach { v -> staticMethods.forEach { v.visitStaticMethod(classDoc, it) } }
    }

    private fun getExtensionDoc(extension: ArucasExtension): ExtensionDoc {
        var annotation = extension::class.java.getAnnotation(ExtensionDocAnnotation::class.java)
        if (annotation == null) {
            annotation = ExtensionDocAnnotation(extension.getName(), arrayOf())
        }
        return ExtensionDoc(annotation)
    }

    private fun getFunctionDoc(method: Method): FunctionDoc? {
        var annotation = method.getAnnotation(FunctionDocAnnotation::class.java)
        if (annotation == null) {
            @Suppress("DEPRECATION")
            val old = method.getAnnotation(me.senseiwells.arucas.api.docs.FunctionDoc::class.java) ?: return null
            annotation = FunctionDocAnnotation(
                old.isStatic,
                old.deprecated,
                old.name,
                old.desc,
                this.convertParameters(old.params),
                this.convertReturns(old.returns),
                old.examples
            )
        }
        return FunctionDoc(annotation)
    }

    private fun getClassDoc(clazz: Class<*>): ClassDoc {
        var annotation = clazz.getAnnotation(ClassDocAnnotation::class.java)
        if (annotation == null) {
            @Suppress("DEPRECATION")
            val old = clazz.getAnnotation(me.senseiwells.arucas.api.docs.ClassDoc::class.java)
                ?: throw IllegalStateException("Class '${clazz}' was not documented!")

            var superclass: KClass<out PrimitiveDefinition<*>> = ObjectDef::class
            if (PrimitiveDefinition::class.java.isAssignableFrom(old.superclass.java)) {
                @Suppress("UNCHECKED_CAST")
                superclass = old.superclass as KClass<out PrimitiveDefinition<*>>
            }
            annotation = ClassDocAnnotation(old.name, old.desc, old.importPath, superclass, old.language)
        }
        return ClassDoc(annotation)
    }

    private fun getConstructorDoc(method: Method): ConstructorDoc? {
        var annotation = method.getAnnotation(ConstructorDocAnnotation::class.java)
        if (annotation == null) {
            @Suppress("DEPRECATION")
            val old = method.getAnnotation(me.senseiwells.arucas.api.docs.ConstructorDoc::class.java) ?: return null
            annotation = ConstructorDocAnnotation(old.desc, this.convertParameters(old.params), old.examples)
        }
        return ConstructorDoc(annotation)
    }

    private fun getFieldDoc(field: Field): FieldDoc? {
        var annotation = field.getAnnotation(FieldDocAnnotation::class.java)
        if (annotation == null) {
            @Suppress("DEPRECATION")
            val old = field.getAnnotation(me.senseiwells.arucas.api.docs.FieldDoc::class.java) ?: return null
            val definition = this.universe[old.type]
            if (definition !is PrimitiveDefinition<*>) {
                throw IllegalArgumentException("No such primitive definition with name ${old.type}")
            }
            @Suppress("USELESS_CAST")
            val clazz = (definition as PrimitiveDefinition<*>)::class
            annotation = FieldDocAnnotation(old.isStatic, old.name, old.desc, clazz, old.assignable, old.examples)
        }
        return FieldDoc(annotation)
    }

    private fun convertParameters(parameters: Array<String>): Array<ParameterDocAnnotation> {
        if (parameters.size % 3 != 0) {
            throw IllegalStateException("Incorrect parameters: ${parameters.contentToString()}")
        }
        val size = parameters.size / 3
        val list = Array(size) {
            val i = 3 * it
            val definition = this.universe[parameters[i]]
            if (definition !is PrimitiveDefinition<*>) {
                throw IllegalArgumentException("No such primitive definition with name ${parameters[i]}")
            }
            @Suppress("USELESS_CAST")
            val clazz = (definition as PrimitiveDefinition<*>)::class
            ParameterDocAnnotation(clazz, parameters[i + 1], arrayOf(parameters[i + 2]))
        }
        return list
    }

    private fun convertReturns(returns: Array<String>): ReturnDocAnnotation {
        if (returns.size != 2) {
            throw IllegalStateException("Incorrect returns: ${returns.contentToString()}")
        }
        val definition = this.universe[returns[0]]
        if (definition !is PrimitiveDefinition<*>) {
            throw IllegalArgumentException("No such primitive definition with name ${returns[0]}")
        }
        @Suppress("USELESS_CAST")
        val clazz = (definition as PrimitiveDefinition<*>)::class
        return ReturnDocAnnotation(clazz, arrayOf(returns[1]))
    }

    private fun sortFunctions(functions: MutableList<FunctionDoc>) {
        functions.sortWith { a, b ->
            val result = a.getName().compareTo(b.getName())
            if (result == 0) a.getParameterCount().compareTo(b.getParameterCount()) else result
        }
    }

    private fun loadApi() {
        val interpreter = Interpreter.dummy(this.api)

        for (builtin in interpreter.modules.builtIns()) {
            this.universe[builtin.name] = builtin
            this.builtin.add(builtin)
        }
        this.builtin.sortWith { a, b -> a.name.compareTo(b.name) }
        interpreter.modules.forEach { _, modules ->
            for (module in modules) {
                this.universe[module.name] = module
                this.modules.add(module)
            }
        }
        this.modules.sortWith { a, b -> a.name.compareTo(b.name) }

        val extensions = this.api.getBuiltInExtensions()
        if (extensions != null) {
            this.extensions.addAll(extensions)
        }
        this.extensions.sortWith { a, b -> a.getName().compareTo(b.getName()) }
    }
}