# Arucas API

The page of the Wiki is for developers who are interested in implementing the language in their own java or kotlin projects.

## Implementation

[![Release](https://jitpack.io/v/senseiwells/Arucas.svg)](https://jitpack.io/#senseiwells/Arucas)

```gradle
repositories {
    maven { url 'https://jitpack.io' }
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

You are able to define the functions you want in your extension and return them in the `getBuiltInFunctions` method.

To create a `BuiltInFunction` you can use the helper methods in the class. You are able to specify the name and parameters of your function, you are also able to define a function with a variable number of parameters. You are able to name multiple functions the same, given that they have a different number of parameters.

Kotlin:
```kotlin
import me.senseiwells.arucas.utils.BuiltInFunction

// ...
// 0 parameters
BuiltInFunction.of("something") { arguments ->
	
} 
// 3 parameters
BuiltInFunction("something", 1) { arguments ->
	
}
// ...
```

Java:
```java
```

### Custom Classes

### Custom Input and Output

### Custom Java Support

### Custom Documentation

### Custom Thread Handler

### Custom Library Manager


