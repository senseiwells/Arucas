# Arucas API

The page of the Wiki is for developers who are interested in implementing the language in their own Java or Kotlin projects.

## Implementation

[![Release](https://jitpack.io/v/senseiwells/Arucas.svg)](https://jitpack.io/#senseiwells/Arucas)

```groovy
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation("com.github.senseiwells:Arucas:2.0.2")

    // Arucas relies on these dependancies, if you do not
    // already implement these you need to add them.
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")
    implementation("com.google.code.gson:gson:2.9.0")
}
```

After implementing Arucas you are ready to get started!

## API

### Getting Started

Arucas provides an API for you to be able to add your own built-in functions and classes, input/output handlers, library managers, obfuscation handling, and Java reflection conversions.

To get started we can create a basic Arucas API, we can use the API Builder to configure out API:

Kotlin:
```kotlin
import me.senseiwells.arucas.api.ArucasAPI

// ...
val api = ArucasAPI.Builder()
    .addDefault()
    .build()
//...
```

Java:
```java
import me.senseiwells.arucas.api.ArucasAPI;

// ...
ArucasAPI api = new ArucasAPI.Builder()
    .addDefault()
    .build();
//...
```

The `addDefault` method simply adds the default IO handler, default built-in functions and classes, and default Java reflection conversions.

You are also able to call these each individually:

Kotlin:
```kotlin
import me.senseiwells.arucas.api.ArucasAPI

// ...
val api = ArucasAPI.Builder()
    .addDefaultConversions()
    .addDefaultExtensions()
    .addDefaultBuiltInDefinitions()
    .addDefaultClassDefinitions()
    .build()
//...
```

Java:
```java
import me.senseiwells.arucas.api.ArucasAPI;

// ...
ArucasAPI api = new ArucasAPI.Builder()
    .addDefaultConversions()
    .addDefaultExtensions()
    .addDefaultBuiltInDefinitions()
    .addDefaultClassDefinitions()
    .build();
//...
```

Now with an `ArucasAPI` object we are able to run some Arucas code:

Kotlin:
```kotlin
import me.senseiwells.arucas.api.ArucasAPI
import me.senseiwells.arucas.api.ThreadHandler
import me.senseiwells.arucas.core.Interpreter

// ...
val api = ArucasAPI.Builder()
    .addDefault()
    .build()
val code = "print('Hello World!');"
val interpreter = Interpreter.of(code, "My Code", api, ::ThreadHandler)

// We can run the interpreter async:
interpreter.threadHandler.executeAsync()
// Alternatively we can run it so it blocks the current thread:
// interpreter.threadHandler.executeBlocking()
//...
```

Java:
```java
import me.senseiwells.arucas.api.ArucasAPI;
import me.senseiwells.arucas.api.ThreadHandler;
import me.senseiwells.arucas.core.Interpreter;

// ...
ArucasAPI api = new ArucasAPI.Builder()
    .addDefault()
    .build();
String code = "print('Hello World!');";
Interpreter interpreter = Interpreter.of(code, "My Code", api, ThreadHandler::new);

// We can run the interpreter async:
interpreter.getThreadHandler().executeAsync();
// Alternatively we can run it so it blocks the current thread:
// interpreter.getThreadHandler().executeBlocking();
//...
```

### Custom Extensions

An extension provides you with the ability to create global functions for Arucas scripts.

Implementing one is quite simple, you must create a new class that implements `ArucasExtension`:

Kotlin:
```kotlin
import me.senseiwells.arucas.api.ArucasExtension
import me.senseiwells.arucas.utils.BuiltInFunction

class MyCustomExtension: ArucasExtension {
    override fun getName() = "MyCustomExtension"

    override fun getBuiltInFunctions(): List<BuiltInFunction> {
        return listOf();
    }
}
```

Java:
```java
```kotlin
import me.senseiwells.arucas.api.ArucasExtension
import me.senseiwells.arucas.utils.BuiltInFunction

import java.util.List;

public class MyCustomExtension implements ArucasExtension {
    @Override
    public String getName() {
        return "MyCustomExtension";
    }

    @Override
    public List<BuiltInFunction> getBuiltInFunctions() {
        return List.of();
    }
}
```

#### Functions

You are able to define the functions you want in your extension and return them in the `getBuiltInFunctions` method.

To create a `BuiltInFunction` you can use the helper methods in the class. You are able to specify the name and parameters of your function, you are also able to define a function with a variable number of parameters. You are able to name multiple functions the same, given that they have a different number of parameters. Built-in functions also have the ability to support an arbitrary number of parameters.

Kotlin:
```kotlin
import me.senseiwells.arucas.utils.BuiltInFunction

// ...
// 0 parameters
BuiltInFunction.of("something", { arguments ->
    
}) 
// 1 parameters
BuiltInFunction.of("something", 1, { arguments ->
    
})
// Arbitrary number of parameters
BuiltInFunction.arb("something", { arguments ->

})
// ...
```

Java:
```java
import me.senseiwells.arucas.utils.BuiltInFunction;

// ...
// 0 parameters
BuiltInFunction.of("foo", arguments -> {
    return null;
});
// 1 parameters
BuiltInFunction.of("foo", 1, arguments -> {
    return null;
});
// Arbitrary number of parameters
BuiltInFunction.arb("something", arguments -> {

});
// ...
```

You can  define the behaviour of the function with a lambda. This lambda passes in a parameter of type `Arguments`, this object holds all the arguments that were passed into the function as well as the interpreter that the function was called from. The arguments will be of type `ClassInstance`, these are Arucas objects.

#### Arguments

The `Arguments` object is flexible in how you can access the arguments, you have the ability to sequentially access the arguments as well as automatically cast them to the desired type. If you require a specific type and the argument is found to not be of the wanted type then an error will be thrown automatically.

Kotlin:
```kotlin
import me.senseiwells.arucas.builtin.StringDef
import me.senseiwells.arucas.utils.BuiltInFunction

// ...
BuiltInFunction.of("bar", 3, { arguments ->
    arguments.arguments // Gets all the arguments in a list
    arguments.interpreter // Gets the interpreter
    arguments.function // Gets the function that was called

    arguments.size() // Gets the total number of arguments

    arguments.get(0) // Gets an argument at an index 0
    arguments.get(0, StringDef::class) // Gets argument at index 0, ensuring it is an instance of StringDef
    arguments.getPrimitive(0, StringDef::class) // Gets argument at index 0 and casts it to a Kotlin String

    arguments.next() // Gets the next argument (this starts at 0)
    arguments.next(StringDef::class) // Gets the next argument, ensuring it is an instance of StringDef
    arguments.nextPrimitive(StringDef::class) // Gets the next argument and casts it to a Kotlin String, this is probably the most used

    arguments.skip() // Skips an argument
    arguments.hasNext() // Checks whether there are any arguments left
    arguments.isNext(StringDef::class) // Checks whether the next argument is an instance of StringDef
    arguments.setIndex(0) // Sets the argument index
    arguments.resetIndex() // Sets the argument index to 0
    arguments.getRemaining() // Gets any remaining arguments as a list
})
// ...
```

Java:
```java
import me.senseiwells.arucas.builtin.StringDef;
import me.senseiwells.arucas.utils.BuiltInFunction;

// ...
BuiltInFunction.of("bar", 3, arguments -> {
    arguments.getArguments(); // Gets all the arguments in a list
    arguments.getInterpreter(); // Gets the interpreter
    arguments.getFunction(); // Gets the function that was called

    arguments.size(); // Gets the total number of arguments

    arguments.get(0); // Gets an argument at an index 0
    arguments.get(0, StringDef.class); // Gets argument at index 0, ensuring it is an instance of StringDef
    arguments.getPrimitive(0, StringDef.class); // Gets argument at index 0 and casts it to a Java String

    arguments.next(); // Gets the next argument (this starts at 0)
    arguments.next(StringDef.class); // Gets the next argument, ensuring it is an instance of StringDef
    arguments.nextPrimitive(StringDef.class); // Gets the next argument and casts it to a Java String, this is probably the most used

    arguments.skip(); // Skips an argument
    arguments.hasNext(); // Checks whether there are any arguments left
    arguments.isNext(StringDef.class); // Checks whether the next argument is an instance of StringDef
    arguments.setIndex(0); // Sets the argument index
    arguments.resetIndex(); // Sets the argument index to 0
    arguments.getRemaining(); // Gets any remaining arguments as a list
    
    return null;
});
// ...
```

You may also return values from this lambda. You can either return a value that is of `ClassInstance`, or alternatively you can just return any `Object`. The interpreter will try to convert any value that is not a `ClassInstance` into one, for example it will turn a `String` into a `StringDef` instance. It does this through the `ValueConverter`, if you are returning any object that does not have an Arucas definition already you need to add a conversion to your API, this is discussed in more detail in the the [Java Support](#custom-java-support) section.

#### Example

Here is a full example:

Kotlin:
```kotlin
import me.senseiwells.arucas.api.ArucasExtension
import me.senseiwells.arucas.builtin.NumberDef
import me.senseiwells.arucas.builtin.StringDef
import me.senseiwells.arucas.utils.Arguments  
import me.senseiwells.arucas.utils.BuiltInFunction

class BuiltInExtension: ArucasExtension {
    override fun getName() = "BuiltInExtension"

    override fun getBuiltInFunctions(): List<BuiltInFunction> {  
        return listOf(  
            BuiltInFunction.of("print", 1, this::print),  
            BuiltInFunction.arb("sum", this::add)
        )
    }

    private fun print(arguments: Arguments) {
        println(arguments.nextPrimitive(StringDef::class))
    }

    private fun add(arguments: Arguments): Double {
        var sum = 0.0
        while (arguments.hasNext()) {
            sum += arguments.nextPrimitive(NumberDef::class)
        }
        return sum
    }
}
```

Java:
```java
import me.senseiwells.arucas.api.ArucasExtension;
import me.senseiwells.arucas.builtin.NumberDef;
import me.senseiwells.arucas.builtin.StringDef;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.BuiltInFunction;

import java.util.List;

public class BuiltInExtension implements ArucasExtension {
    @Override
    public String getName() {
        return "BuiltInExtension";
    }

    @Override
    public List<BuiltInFunction> getBuiltInFunctions() {
        return List.of(
            BuiltInFunction.of("print", 1, this::print),  
            BuiltInFunction.arb("sum", this::add)
        );
    }

    private Void print(Arguments arguments) {
        System.out.println(arguments.nextPrimitive(StringDef.class));
        return null;
    }

    private double add(Arguments arguments) {
        double sum = 0.0;
        while (arguments.hasNext()) {
            sum += arguments.nextPrimitive(NumberDef.class);
        }
        return sum;
    }
}
```

#### Adding Your Extension

Now that you've created an extension you'll want to add it to your `ArucasAPI`:

Kotlin:
```kotlin
import me.senseiwells.arucas.api.ArucasAPI

// ...
val api = ArucasAPI.Builder()
    .addDefault()
    .addBuiltInExtension(BuiltInExtension())
    .build()
//...
```

Java:
```java
import me.senseiwells.arucas.api.ArucasAPI;

// ...
ArucasAPI api = new ArucasAPI.Builder()
    .addDefault()
    .addBuiltInExtension(new BuiltInExtension())
    .build();
//...
```

### Custom Classes

Custom classes allow you to provide `Objects` with defined behaviours for Arucas.

Creating a custom class is more complex than an extension but provides much more functionality. You can create one by either extendind the `PrimitiveDefinition` or `CreatableDefinition` class, you must provide the name of the class definition as well as the interpreter that it is being defined on. You can find an example below:

Kotlin:
```kotlin
import me.senseiwells.arucas.classes.PrimitiveDefinition  
import me.senseiwells.arucas.core.Interpreter

class MyCustomClassDef(interpreter: Interpreter): PrimitiveDefinition<Any>("MyCustomClass", interpreter) {

}
```

Java:
```java
import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.core.Interpreter;

public class MyCustomClassDef extends PrimitiveDefinition<Object> {
    public MyCustomClassDef(Interpreter interpreter) {
        super("MyCustomClass", interpreter);
    }
}
```

Both `PrimitiveDefinition` and `CreatableDefinition` have a type parameter, this is because these class definition essentially act as a wrapper for a specific data type. For example if you were creating an Arucas class for `Boolean` then you would pass `Boolean` into the type parameter. For the example above `Any`/`Object` is used for a generic example.

Lets take a look at the difference between `PrimitiveDefinition` and `CreatableDefinition`. `CreatableDefinition` extends `PrimitiveDefinition` and changes one method: `#create()`. `CreatableDefinition` makes this method public allowing for the interpreter to be able to create instances of this class with an instance of the type parameter. There are cases were you would not want to be able to create new instances of your definition, for example `Boolean`. There are only 2 possible boolean values in which case they can just be stored as constants instead of having the interpreter instantiate a new definition instance each time.

#### Constructors

Adding constructors to your class definition is very similar to adding functions to an extension. You can override the `defineConstructors` method and return a list of `ConstructorFunction`. This function is very similar to the `BuiltInFunction` that we discussed previously however the arguments will also include a `ClassInstance` as the first argument (with any other arguments passed in following). You do not need to account this argument in your `parameters` when creating the `ConstructorFunction`.

Inside this constructor you **must** set the primitive value of the `ClassInstance`, an example is shown below:

Kotlin:
```kotlin  
import me.senseiwells.arucas.builtin.StringDef
import me.senseiwells.arucas.classes.CreatableDefinition
import me.senseiwells.arucas.core.Interpreter  
import me.senseiwells.arucas.utils.Arguments  
import me.senseiwells.arucas.utils.ConstructorFunction  

import java.io.File

class FileDef(interpreter: Interpreter): CreatableDefinition<File>("File", interpreter) {
    override fun defineConstructors(): List<ConstructorFunction> {
        return listOf(
            ConstructorFunction.of(1, this::construct)
        )
    }

    private fun construct(arguments: Arguments) {
        val instance = arguments.next()
        val path = arguments.nextPrimitive(StringDef::class)
        instance.setPrimitive(this, File(path))
    }
}
```

Java:
```java
import kotlin.Unit;
import me.senseiwells.arucas.builtin.StringDef;  
import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ConstructorFunction;

import java.io.File;
import java.util.List;

public class FileDef extends CreatableDefinition<File> {
    public FileDef(Interpreter interpreter) {
        super("File", interpreter);
    }

    @Override
    public List<ConstructorFunction> defineConstructors() {
        return List.of(
            ConstructorFunction.of(1, this::construct)
        );
    }

    private Unit construct(Arguments arguments) {
        ClassInstance instance = arguments.next();
        String path = arguments.nextPrimitive(StringDef.class);
        instance.setPrimitive(this, new File(path));
        return null;
    }
}
```

#### Methods

Methods are also very straight forward to implement. To start you must override the `defineMethods` method, which will return a list of `MemberFunction`. Similarly to the `ConstructorFunction` this function will have the `ClassInstance` object refering to `this` object as the first argument with any other arguments following. Like `BuiltInFunctions` you may overload functions.

Here is an example:

Kotlin:
```kotlin  
import me.senseiwells.arucas.builtin.StringDef
import me.senseiwells.arucas.classes.CreatableDefinition
import me.senseiwells.arucas.core.Interpreter    
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.utils.Arguments  
import me.senseiwells.arucas.utils.MemberFunction  

import java.io.File  
import java.io.IOException

class FileDef(interpreter: Interpreter): CreatableDefinition<File>("File", interpreter) {
    override fun defineMethods(): List<MemberFunction> {
        return listOf(
            MemberFunction.of("getName", this::getName),
            MemberFunction.of("write", this::write)
        )
    }
    
    private fun getName(arguments: Arguments): String {  
        val file = arguments.nextPrimitive(this)  
        return file.name  
    }

    private fun write(arguments: Arguments) {
        val file = arguments.nextPrimitive(this)  
        val string = arguments.nextPrimitive(StringDef::class)  
        try {  
            file.writeText(string)  
        } catch (e: IOException) {  
            runtimeError("There was an error writing the file '$file'", e)  
        }  
    }
}
```

Java:
```java
import me.senseiwells.arucas.builtin.StringDef;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.MemberFunction; 

import java.io.File;  
import java.io.IOException;  
import java.nio.file.Files;
import java.util.List;

class FileDef extends CreatableDefinition<File> {
    public FileDef(Interpreter interpreter) {
        super("File", interpreter);
    }

    @Override 
    public List<MemberFunction> defineMethods() {
        return List.of(
            MemberFunction.of("getName", this::getName),
            MemberFunction.of("write", this::write)
        );
    }
    
    private String getName(Arguments arguments) {  
        File file = arguments.nextPrimitive(this);
        return file.getName();
    }

    private Void write(Arguments arguments) {
        File file = arguments.nextPrimitive(this);
        String string = arguments.nextPrimitive(StringDef.class);
        try {
            Files.writeString(file.toPath(), string);
        } catch (IOException e) {  
            throw new RuntimeError("There was an error writing the file '" + file + "'", e);
        }  
        return null;
    }
}
```

#### Static Methods

#### Static Fields

#### Operators

#### Inheritance

#### Java

#### Adding Your Class

Now that you've created a class you'll want to add it to your `ArucasAPI`, there are two ways you can add your custom class. Either as a 'Built-In' class, or an 'Importable' class. Having your class be built in means that scripts do not have to import the class, otherwise they need to import the class from the given import path.

Kotlin:
```kotlin
import me.senseiwells.arucas.api.ArucasAPI

// ...
val api = ArucasAPI.Builder()
    .addDefault()
    .addBuiltInDefinitions(::MyCustomClassDef)
    // Or
    .addClassDefinitions("import.path", ::MyCustomClassDef)
    .build()
//...
```

Java:
```java
import me.senseiwells.arucas.api.ArucasAPI;

// ...
ArucasAPI api = new ArucasAPI.Builder()
    .addDefault()
    .addBuiltInDefinitions(MyCustomClassDef::new)
    // Or
    .addClassDefinitions("import.path", MyCustomClassDef::new)
    .build();
//...
```

### Custom Input and Output

### Custom Java Support

### Custom Documentation

### Custom Thread Handler

### Custom Library Manager


