# Welcome to the Arucas Wiki!

### What is Arucas?

Arucas is an dynamically typed interpreted language using Java as it's host language.  
The language was designed to be easily integrated into other Java programs, and the main purpose of this language was for Minecraft Scripting which can be found in my mod [here](https://github.com/senseiwells/EssentialClient)  
The syntax of the language is similar to that of Java, and JavaScript.

This is the wiki as of Arucas Version `v1.2.0`

## Wiki Pages

> #### [Language Syntax](#language-syntax)
> #### [Built-in Functions](#built-in-functions)
> #### [Built-in Classes](#built-in-classes)
> #### [Example Code](#example-code)
> #### [Arucas API](#arucas-api)


# Language Syntax

## Literals

Arucas provides 6 object types that you are able to create with literals, these are:

`Number` - An object containing a floating point number, represented by a number

`String` - An object containing an array of characters, represented by any text within two double or single quote marks

`Boolean` - An object containing only one of two possible values, represented by `true` or `false`

`List` - An object containing a list of other objects, represented by having object seperated with comma's within square brackets

`Map` - An object containing a list of key and value pairs of objects, represented by having a list of pairs of objects separated by colons within curly brackets

`Null` - An object representing nothing, represented by `null`

## Basic Operators:

`=` - This assigns a value to a variable

`+` - This adds two numbers together or concatenates two strings

`-` - This subtracts one number from another

`*` - This multiplies one number from another

`/` - This divides one number by another

`^` - This multiplies one number by itself a number of times

`++` - This increments the value of a number by one

`--` - This decrements the value of a number by one

`.` - This allows you to access members of a value

## Logical Operators:

`&&` - This is the logical AND

`||` - This is the logical OR

`!` - This is the logical NOT

`~` - This is the logical XOR

`==` - This evaluates whether two objects are equal

`>` - This evaluates whether a number is more than another

`<` - This evaluates whether a number is less than another

`>=` - This evaluates whether a number is greater than or equal to another

`<=` - This evaluates whether a number is less than or equal to another

One thing to note for AND and OR is that the left side will always be evaluated first, and if it does not evaluate to `true` in case of AND, or `false` in the case of OR, the right side will not be evalutated.

Another thing to note is that custom classes are able to override some of these operators changing their functionality, more information will be on this in the classes section.

## Bitwise Operators:

`&` - This is the bitwise AND

`|` - This is the bitwise OR

`~` - This is the bitwise XOR

`>>` - Right bitshift

`<<` - Left bitshift

One thing to note is that the bitwise AND and OR also work for Booleans, however they do not short circuit, so in the case of AND if the left side was evaluated to `false` it would still evaluate the left side, and similarly OR will evaluate the right side even if the left was already `true`.

## Comments:

You are able to make comments in your code that the compiler will ignore.

`//` - Used to comment until a line break

`/* */` - Used for multi-line comments

## Keywords:


### `{` and `}`

- These are used to define scopes, or code blocks
- Scopes can contain multiple lines of code inside then and usually is idented to visually show it's in a different scope. Any variables initialised inside of a scope cannot be accessed outside of that scope, but variables defined outside of the scope can be accessed and assigned inside of that scope, scopes can be used independantly as well as with other statements.

Example:
```kotlin
outside = 9;
{
    // variable only defined inside this scope
    inside = 10;
    // variable defined outside of the scope can be assigned and accessed
    outside = outside - 2;
}
// this would crash as 'inside' doesn't exist outside the scope
outside = inside;
```

### `fun`

- This is used for defining functions
- Functions can have parameters, however functions outside of classes cannot be overloaded, overloaded functions can not be delegated. Functions are called by having their name followed by two brackets.

Examples:
```kotlin
fun doSomething() {
    print("something");
}

fun doSomethingElse(foo) {
    print(foo);
}

doSomething();
```

A thing to note is that the language is not fussy with how you format you code, in terms of new lines and spaces. So formatting like this is considered valid:

```kotlin
fun doSomething()
{
    if (true) print("");
}
```

### `return`

- This is for escaping out of a function or file
- You are able to return a value, however this is not required. If there is no return statement in a function it will automatically return `null`, you can also return a value out of a file.

Examples:
```kotlin
fun doSomething() {
    return "something";
}

return doSomething();
```

### `if`, `else if`, and `else`

- These are for evaluating boolean expressions
- You cannot have `else if`s and `else` without an `if`, if one of these has an expression evaluates to true the code inside of their code block will be executed, and the rest will be ignored.

Examples:
```kotlin
if (false) { 
    print("foo"); 
} 
else if (false) { 
    print("bar"); 
} 
else { 
    print("baz"); 
}
```

### `switch`, `case`, and `default`

- These are for matching values
- Switch statements can compare a value to both literals and other expressions, similar to an if statement, you cannot have two of the same literal in the cases and the switch statement is evaluated from first case down to last case with literals being the first thing that is checked in each case. If the value does not match any cases it will run the default branch. You cannot have more than one default branch.

Example:
```kotlin
fun getNum() {
    return 1;
}

number = 1;
switch (number) {
    // Even though getNum comes before 1 the value
    // will check against literals first, so in this
    // case getNum will ne
    case getNum(), 1, 2, 3 -> {
        print("low");
    }
    case 4, 5 -> { }
    case 6 -> {
        print("highest");
    }
    default -> {
        print("unkown");
    }
}
```

### `while`

- This is for creating a simple loop
- `while` evaluates a boolean expression similar to `if`, and if it evaluates to `true` then it will execute the code inside, and then go back to the top and repeat the process until it evaluates to `false`.

Examples:
```kotlin
while (true) {
    print("This is an infinite loop");
}

i = 0;
while (i < 10) {
    print(i);
    i++;
}
```

### `for`

- This is for creating an iteration loop
- `for` takes in three expressions, the first will only be executed once at the start, the second is similar to the condition in a while loop, and the third gets executed every time the `for` loop completes.

Example:
```kotlin
/* 
 * This example is exactly the same as the previous 
 * while loop example, just more consise
 */
for (i = 0; i < 10; i++) {
    print(i);
}

list = [4, 5, 2, 7];
for (i = 0; i < len(list); i++) {
    value = list.get(i);
    print(value);
}
```

### `foreach`

- This is for iterating over the values in a lists
- `foreach` this is similar to the Java enhanced for loop, you have an identifier which will hold your value and the desired list that you want to iterate over.

Example:
```kotlin
// this does the same as the previous for loop example, just more consise
list = [4, 5, 2, 7];
for (value : list) {
    print(value);
}
```

### `continue`

- This allows you to return to the top of a loop

Example:
```kotlin
for (i = 0; i < 10; i++) {
    if (i == 3) {
        continue;
    }
    print(i);
}
```

### `break`

- This allows you to escape a loop or case statement

Examples:
```kotlin
for (i = 0; i < 10; i++) {
    if (i == 3) {
        break;
    }
    print(i);
}

number = 18;
switch ("foo") {
    case "bar", "baz" -> {
        print("b");
    } 
    case "foo", "faz" -> {
        if (number >= 18) {
            break;
        }
        print("f");
    }
}
```

### `throw`

- This keyword is used to throw an error
- You can only throw `Error`s, you can construct an `Error` by using the `Error` class, if these go uncaught they will crash the program.

Example:
```kotlin
throw new Error("Something went wrong!", 540);
```

### `try` and `catch`

- These are used to handle exceptions
- `catch` can only catch `Error`s, the caught value will be of type `Error` which encapsulates the error message and possibly a value. If errors go uncaught they will crash the program.

Example:
```kotlin
try {
    throw null;
}
catch (error) {
    print(error);
}
```

### `class`

- This is used to define a `class`
- Classes allow for encapsulation, wrapping other objects (values) into a single object, this class defines that single object and what that object is able to do.
- If a class has no constructors you will be able to construct that class with no parameters. Classes also allow you to define methods, or class functions.  You can overload constructors, and methods in a class by differing the number of parameters.
- You can initialise the class members in the constructor, or initialise them when you define them. If you do not define them they will default to being `null`.

Example:
```kotlin
class Example {
    var other1;
    var other2 = 10;

    Example() { }

    Example(param) {
        this.other1 = param;
    }
}
```

### `var`

- This is used for declaring variables inside classes
- The var keyword can be used for `static` an non-`static` variables.
- These variables you define can be access using the `.` operator

Examples:
```kotlin
class Example {
    var pi = 3.14;
    var name = "sensei";
}
```

### `.`

- This is used to access members of an object
- This can be used on `class` names for `static` members or on an object for the object members.

Example:
```kotlin
class Example {
    var number = 10;
    
    // If there is no constructor
    // Arucas generates a synthetic one

    fun printNumber() {
        print(this.number);
    }
}

// We are able to construct it with no parameters
example = new Example();

// these two do the same thing
print(example.number);
example.printNumber();
```

### `this`

- This is used for refering to itself inside a class
- `this` is passed in internally, and you will have access to it inside a class constructor, method, or operator method, `this` allows you to refer to all of the members inside of your class, you can assign, access, or call them.

Example:
```kotlin
class Example {
    var number = 10;

    fun setNumber(newNumber) {
        this.number = newNumber;
    }

    fun resetNumber() {
        this.number = 10;
    }
}

e = new Example();
// When we call a method on a value
// it internally passes itself as a parameter
// so you could think of it as resetNumber(e)
// where inside the resetNumber function
// e is referenced with the this keyword
e.resetNumber();
``` 

### `new`

- This is used to construct a new object
- This is used for custom classes, as the built in objects cannot be constructed, as they have literals. This will return a new instance of that object.

Examples:
```kotlin
class Example {
    var number = 10;
    
    // We have multiple constructors
    Example() { }
    
    Example(number) {
        this.number = number;
    }
}

// Construction
defaultExample = new Example();
myExample = new Example(20);
```

### `operator`

- This allows you to override operator methods for an object
- An important thing to note about this is that it will be the left side's operator method that will get used, for example `a + b` would result in `a`'s plus operator getting called not `b`'s. Whereas `b + a` would result in `b`'s plus operator getting called not `a`'s
- The operators that you can override are: `+`, `-`, `*`, `/`, `^`, `<`, `<=`, `>`, `>=`, `==`, and `!`

Examples:
```kotlin
class Example {
    var number = 10;

    operator + (other) {
        return this.number + other;
    }

    operator ! () {
        return this.number * -1;
    }

    operator == (other) {
        return this.number == other;
    }
}

// Constructing the class using the new keyword
example = new Example();

plusExample = example + 10; // this would return 20
notExample = !example; // this would return -10
equalsExample = example == 10; // this would return true

// this would throw an error, because 10's operator is being called not example's
errorExample = 10 + example; 
``` 

### `static`

- This lets you define `static` members, methods, and initialisers in a class
- `static` members and methods can be access without an instance of the class. And a `static` initialiser will run when the class is evaluated, `static` methods can be overloaded. `static` members are access by typing the `class` name followed by a `.` and then the member name.

Example:
```kotlin
class Example {
    static var number = 10;

    static {
        print("Example class was loaded");
    }

    static fun print() {
        print(Example.number);
    }

    static fun print(prefix) {
        print("%s %s".formatted(prefix, Example.number)); 
    }
}
```

`enum`

- Enums are somwhat similar to classes, but they cannot be constructed and instead have static-like variables of the type that cannot be modified. Enums are great if you want constants that can also encapsulate other values.

Example:
```kotlin
// Every enum by default has 2 static functions
// Direction.values() and Direction.fromString(str)
// they get all the values of the enum and convert
// a string to a enum by name respectively. 
enum Direction {
    NORTH("North"),
    // You can have brackets if you want
    SOUTH(),
    // Or not
    EAST,
    WEST("Wow");

    Direction() {
        // All enum values by default have
        // 2 methods, getName and ordinal
        // getting the name of the enum and the
        // index of the enum respectively
        this.prettyName = this.getName();
    }
    
    /* Constructors are used when initialising
     * the enum values, they can take any values
     */
    Direction(prettyName) {
        this.prettyName = prettyName;
    }

    /* Not a good way to do this, should have
     * opposite as a member variable but this is
     * just an example.
     */
    fun getOpposite() {
        switch(this) {
            case Direction.NORTH -> return Direction.SOUTH;
            case Direction.SOUTH -> return Direction.NORTH;
            case Direction.EAST -> return Direction.WEST;
            case Direction.WEST -> return Direction.EAST;
        }
    }
}

// Direction.NORTH = "Random Value!"; <-- This would crash
// direction = new Direction(); <-- This would also crash
print(Direction.NORTH.getOpposite());
```

`import` and `from`

- These keywords allow you to import classes from other files.
- Currently cyclical imports are not supported but usually you don't need two files importing eachother.

```kotlin
// Imports all classes from util.Internal file
import * from util.Internal;
// Only imports Collector class from util.Collection
import Collector from util.Collection;
// If you want to import your own file you must have it
// inside your libraries folder, in Vanilla arucas this
// is Users/user/.arucas/libs/

// Since we are importing the Collector class we can not use it
collector = Collector.of(1, 2, 3, 4, 5, 6);
list = collector.filter(fun(value) { return value > 4; }).toList();
print(list);
```

## Built-in Functions:

These are the functions that Arucas Supports:

These functions do not need any class to be called, just a regular function, Anything inside brackets are parameters and these values are used in the function to perform an action

All functions can Throw an error if you input the incorrect Value type

### `run(path)`
- This is used to run a `.arucas` file, you can use on script to run other scripts
- Parameter - String: as a file path
- Returns - Value: any value that the file returns
- Throws: Error: `"Failed to execute script..."` if the file fails to execute
- Example: `run("/home/user/script.arucas");`

### `stop()`
- This is used to stop a script
- Example: `stop();`

### `sleep(milliseconds)`
- This pauses your program for a certain amount of milliseconds
- Parameter - Number: milliseconds to sleep
- Example: `sleep(1000);`

### `print(printValue)`
- This prints a value to the console
- Parameter - Value: the value to print
- Example: `print("Hello World");`

### `fullPrint(printValue)`
- This prints a number of values to the console
- Parameters - Arbitrary: any number of values to print
- Example: `print("Hello World", "This is a test", 123);`

### `input(prompt)`
- This is used to take an input from the user
- Parameter - String: the prompt to show the user
- Returns - String: the input from the user
- Example: `input("What is your name?");`

### `debug(bool)`
- This is used to enable or disable debug mode
- Parameter - Boolean: true to enable debug mode, false to disable debug mode
- Example: `debug(true);`

### `experimental(bool)`
- This is used to enable or disable experimental mode
- Parameter - Boolean: true to enable experimental mode, false to disable experimental mode
- Example: `experimental(true);`

### `suppressDeprecated(bool)`
- This is used to enable or disable suppressing deprecation warnings
- Parameter - Boolean: true to enable suppressing deprecation warnings, false to disable suppressing deprecation warnings
- Example: `suppressDeprecated(true);`

### `isMain()`
- This is used to check whether the script is the main script
- Returns - Boolean: true if the script is the main script, false if it is not
- Example: `isMain();`

### `getArucasVersion()`
- This is used to get the version of Arucas that is currently running
- Returns - String: the version of Arucas that is currently running
- Example: `getArucasVersion();`

### `random(bound)`
- This is used to generate a random integer between 0 and the bound
- Parameter - Number: the maximum bound (exclusive)
- Returns - Number: the random integer
- Example: `random(10);`

### `getTime()`
- This is used to get the current time formatted with HH:mm:ss in your local time
- Returns - String: the current time formatted with HH:mm:ss
- Example: `getTime();`

### `getNanoTime()`
- This is used to get the current time in nanoseconds
- Returns - Number: the current time in nanoseconds
- Example: `getNanoTime();`

### `getMilliTime()`
- This is used to get the current time in milliseconds
- Returns - Number: the current time in milliseconds
- Example: `getMilliTime();`

### `getUnixTime()`
- This is used to get the current time in seconds since the Unix epoch
- Returns - Number: the current time in seconds since the Unix epoch
- Example: `getUnixTime();`

### `getDate()`
- This is used to get the current date formatted with dd/MM/yyyy in your local time
- Returns - String: the current date formatted with dd/MM/yyyy
- Example: `getDate();`

### `len(collection)`
- This is used to get the length of a collection or string
- Parameter - String/Collection/Function: the collection or string
- Throws - Error: `"Cannot pass ... into len()"` if the parameter is not a collection or string
- Example: `len("Hello World");`

### `throwRuntimeError(message)`
- Deprecated: You should use the `throw` keyword
- This is used to throw a runtime error
- Parameter - String: the message of the error
- Throws - Error: the error with the message
- Example: `throwRuntimeError("I'm throwing this error");`

### `callFunctionWithList(function, list)`
- Deprecated: You should use Function class `Function.callWithList(fun() {}, [])`
- This is used to call a function with a list of arguments
- Parameters - Function, List: the function and the list of arguments
- Returns - Value: the return value of the function
- Example: `callFunctionWithList(fun(n1, n2, n3) {}, [1, 2, 3]);`

### `runFromString(string)`
- This is used to evaluate a string as a script
- Parameter - String: the string to evaluate
- Returns - Value: the return value of the script
- Example: `runFromString("return 1;");`


# Built-in Classes

These are classes that come with Arucas by default. You are able to use any of these values and functions! You may have to import some, import paths will be noted.


# Value class
Object class for Arucas. Every other class extends this and as such has all of these member functions.
Fully Documented.

## Member Functions

### `<Value>.instanceOf(type)`
- This checks whether a value is an instance of another type
- Parameter - Type: the other type you want to check against
- Returns - Boolean: whether the value is of that type
- Example: `10.instanceOf(String.type);`

### `<Value>.getValueType()`
- Deprecated: You should use `Type.of(<Value>).getName()`
- This returns the name of the type of the value
- Returns - Type: the type of the value
- Example: `10.getValueType();`

### `<Value>.copy()`
- This returns a copy of the value, some values might just return themselves
- Returns - Value: the copy of the value
- Example: `10.copy();`

### `<Value>.hashCode()`
- This returns the hashcode of the value
- Returns - Number: the hashcode of the value
- Example: `"thing".hashCode();`

### `<Value>.equals(other)`
- Deprecated: You should use `==`
- This checks whether the value is equal to another value
- Parameter - Value: the other value you want to check against
- Returns - Boolean: whether the values are equal
- Example: `10.equals(20);`

### `<Value>.toString()`
- This returns the string representation of the value
- Returns - String: the string representation of the value
- Example: `[10, 11, 12].toString();`


# String class
String class for Arucas.  
This class cannot be constructed since strings have a literal.  
Strings are immutable.  
Fully Documented.

## Member Functions

### `<String>.toList()`
- This makes a list of all the characters in the string
- Returns - List: the list of characters
- Example: `"hello".toList();`

### `<String>.replaceAll(regex, replace)`
- This replaces all the instances of a regex with the replace string
- Parameter - String, String: the regex you want to replace, the string you want to replace it with
- Returns - String: the modified string
- Example: `"hello".replaceAll("l", "x");`

### `<String>.uppercase()`
- This makes the string uppercase
- Returns - String: the uppercase string
- Example: `"hello".uppercase();`

### `<String>.lowercase()`
- This makes the string lowercase
- Returns - String: the lowercase string
- Example: `"HELLO".lowercase();`

### `<String>.toNumber()`
- This tries to convert the string to a number
- Returns - Number: the number value
- Example: `"0xFF".toNumber();`

### `<String>.format(strings...)`
- This formats the string with the given parameters
- Parameter - String...: the strings to add
- Returns - String: the formatted string
- Throws - Error: `"You are missing values to be formatted!"` if there are not enough parameters
- Example: `"%s %s".format("hello", "world");`

### `<String>.contains(string)`
- This checks if the string contains the given string
- Parameter - String: the string you want to check for
- Returns - Boolean: true if the string contains the given string
- Example: `"hello".contains("he");`

### `<String>.strip()`
- This strips the whitespace from the string
- Returns - String: the stripped string
- Example: `"  hello  ".strip();`

### `<String>.capitalise()`
- This capitalises the first letter of the string
- Returns - String: the capitalised string
- Example: `"foo".capitalise();`

### `<String>.split(regex)`
- This splits the string into a list of strings based on a regex
- Parameter - String: the regex to split the string with
- Returns - List: the list of strings
- Example: `"foo/bar/baz".split("/");`

### `<String>.subString(from, to)`
- This returns a substring of the string
- Parameters - Number, Number: the start index, the end index
- Returns - String: the substring
- Example: `"hello".subString(1, 3);`


# Number class
Number class for Arucas.  
This class cannot be constructed as it has a literal representation.  
For math related functions see the Math class.  
Fully Documented.

## Member Functions

### `<Number>.round()`
- This allows you to round a number to the nearest integer
- Returns - Number: the rounded number
- Example: `3.5.round();`

### `<Number>.ceil()`
- This allows you to round a number up to the nearest integer
- Returns - Number: the rounded number
- Example: `3.5.ceil();`

### `<Number>.floor()`
- This allows you to round a number down to the nearest integer
- Returns - Number: the rounded number
- Example: `3.5.floor();`

### `<Number>.modulus(otherNumber)`
- Deprecated: You should use `Math.mod(num1, num2)`
- This allows you to get the modulus of two numbers
- Parameter - Number: the divisor
- Returns - Number: the modulus of the two numbers
- Example: `5.modulus(2);`

### `<Number>.absolute()`
- Deprecated: You should use `Math.abs(num)`
- This allows you to get the absolute value of a number
- Returns - Number: the absolute value of the number
- Example: `-5.absolute();`

### `<Number>.toRadians()`
- Deprecated: You should use `Math.toRadians(num)`
- This allows you to convert a number in degrees to radians
- Returns - Number: the number in radians
- Example: `5.toRadians();`

### `<Number>.toDegrees()`
- Deprecated: You should use `Math.toDegrees(num)`
- This allows you to convert a number in radians to degrees
- Returns - Number: the number in degrees
- Example: `Math.PI.toDegrees();`

### `<Number>.isInfinite()`
- This allows you to check if a number is infinite
- Returns - Boolean: true if the number is infinite
- Example: `(0/0).isInfinite();`

### `<Number>.isNaN()`
- This allows you to check if a number is not a number
- Returns - Boolean: true if the number is not a number
- Example: `(0/0).isNaN();`


# Boolean class
Boolean class for Arucas.  
This class cannot be constructed since Booleans have literals.  
No Documentation.


# Null class
Null class for Arucas.  
This class cannot be constructed since null has a literal `null`  
No Documentation.


# List class
List class for Arucas.  
This class cannot be constructed since it has a literal, `[]`  
Fully Documented.

## Member Functions

### `<List>.get(index)`
- This allows you to get the value at a specific index
- Parameter - index: the index of the value you want to get
- Returns - Value: the value at the index
- Throws - Error: `"Index is out of bounds"` if the index is out of bounds
- Example: `["object", 81, 96, "case"].get(1);`

### `<List>.remove(index)`
- This allows you to remove the value at a specific index
- Parameter - index: the index of the value you want to remove
- Returns - Value: the value that was removed
- Throws - Error: `"Index is out of bounds"` if the index is out of bounds
- Example: `["object", 81, 96, "case"].remove(1);`

### `<List>.append(value)`
- This allows you to append a value to the end of the list
- Parameter - value: the value you want to append
- Returns - List: the list
- Example: `["object", 81, 96, "case"].append("foo");`

### `<List>.insert(value, index)`
- This allows you to insert a value at a specific index
- Parameter - value, index: the value you want to insert, the index you want to insert the value at
- Returns - List: the list
- Throws - Error: `"Index is out of bounds"` if the index is out of bounds
- Example: `["object", 81, 96, "case"].insert("foo", 1);`

### `<List>.addAll(collection)`
- This allows you to add all the values in a collection to the list
- Parameter - Collection: the collection you want to add
- Returns - List: the list
- Throws - Error: `"... is not a collection"` if the value is not a collection
- Example: `["object", 81, 96, "case"].addAll(["foo", "bar"]);`

### `<List>.concat(otherList)`
- This allows you to concatenate two lists
- Parameter - List: the list you want to concatenate with
- Returns - List: the concatenated list
- Example: `["object", 81, 96, "case"].concat(["foo", "bar"]);`

### `<List>.contains(value)`
- This allows you to check if the list contains a value
- Parameter - Value: the value you want to check for
- Returns - Boolean: true if the list contains the value, false otherwise
- Example: `["object", 81, 96, "case"].contains("foo");`

### `<List>.containsAll(collection)`
- This allows you to check if the list contains all the values in a collection
- Parameter - Collection: the collection you want to check for
- Returns - Boolean: true if the list contains all the values in the collection, false otherwise
- Throws - Error: `"... is not a collection"` if the value is not a collection
- Example: `["object", 81, 96, "case"].containsAll(["foo", "bar"]);`

### `<List>.isEmpty()`
- This allows you to check if the list is empty
- Returns - Boolean: true if the list is empty, false otherwise
- Example: `["object", 81, 96, "case"].isEmpty();`

### `<List>.clear()`
- This allows you to clear all the values the list
- Example: `["object", 81, 96, "case"].clear();`

### `<List>.indexOf(value)`
- This allows you to get the index of a value in the list
- Parameter - Value: the value you want to check for
- Returns - Number: the index of the value, -1 if the value is not in the list
- Example: `["object", 81, 96, "case"].indexOf("case");`

### `<List>.toString(value)`
- This converts the set to a string and evaluating any collections inside it
- Returns - String: the string representation of the set
- Example: `["object", 81, 96, "case"].toString();`


# Map class
Map class for Arucas.  
This class cannot be constructed since it has a literal, `{}`  
Fully Documented.

## Member Functions

### `<Map>.get(key)`
- This allows you to get the value of a key in the map
- Parameter - Value: the key you want to get the value of
- Returns - Value: the value of the key, will return null if non-existent
- Example: `{"key": "value"}.get("key");`

### `<Map>.getKeys()`
- This allows you to get the keys in the map
- Returns - List: a complete list of all the keys
- Example: `{"key": "value", "key2", "value2"}.getKeys();`

### `<Map>.getValues()`
- This allows you to get the values in the map
- Returns - List: a complete list of all the values
- Example: `{"key": "value", "key2", "value2"}.getValues();`

### `<Map>.put(key, value)`
- This allows you to put a key and value in the map
- Parameter - Value, Value: the key you want to put, the value you want to put
- Returns - Value: the previous value associated with the key, null if none
- Example: `{"key": "value"}.put("key2", "value2");`

### `<Map>.putIfAbsent(key, value)`
- This allows you to put a key and value in the map if it doesn't exist
- Parameter - Value, Value: the key you want to put, the value you want to put
- Returns - Value: the previous value associated with the key, null if none
- Example: `{"key": "value"}.putIfAbsent("key2", "value2");`

### `<Map>.putAll(anotherMap)`
- This allows you to put all the keys and values of another map into this map
- Parameter - Value: another map
- Example: `{"key": "value"}.putAll({"key2": "value2"});`

### `<Map>.remove(key)`
- This allows you to remove a key and its value from the map
- Parameter - Value: the key you want to remove
- Returns - Value: the value associated with the key, null if none
- Example: `{"key": "value"}.remove("key");`

### `<Map>.clear()`
- This allows you to clear the map of all the keys and values
- Example: `{"key": "value"}.clear();`

### `<Map>.isEmpty()`
- This allows you to check if the map is empty
- Returns - Boolean: true if the map is empty, false otherwise
- Example: `{"key": "value"}.isEmpty();`

### `<Map>.containsKey(key)`
- This allows you to check if the map contains a specific key
- Parameter - Value: the key you want to check
- Returns - Boolean: true if the map contains the key, false otherwise
- Example: `{"key": "value"}.containsKey("key");`

### `<Map>.toString()`
- This allows you to get the string representation of the map and evaluating any collections inside it
- Returns - String: the string representation of the map
- Example: `{"key": []}.toString();`


# Set class
Set class for Arucas.  
An instance of the class can be created by using `Set.of(values...)`  
Fully Documented.

## Static Member Functions

### `Set.of(values...)`
- This allows you to create a set with an arbitrary number of values
- Parameters - Value...: the values you want to add to the set
- Returns - Set: the set you created
- Example: `Set.of("object", 81, 96, "case");`

## Member Functions

### `<Set>.get(object)`
- This allows you to get a value from in the set
- Parameter - Value: the value you want to get from the set
- Returns - Value/Null: the value you wanted to get, null if it wasn't in the set
- Example: `Set.of("object").get("object");`

### `<Set>.remove(object)`
- This allows you to remove a value from the set
- Parameter - Value: the value you want to remove from the set
- Returns - Boolean: whether the value was removed from the set
- Example: `Set.of("object").remove("object");`

### `<Set>.add(value)`
- This allows you to add a value to the set
- Parameter - Value: the value you want to add to the set
- Returns - Boolean: whether the value was successfully added to the set
- Example: `Set.of().add("object");`

### `<Set>.addAll(otherCollection)`
- This allows you to add all the values in a collection into the set
- Parameter - Collection: the collection of values you want to add
- Returns - Set: the modified set
- Throws - Error: `"'...' is not a collection"` if the parameter isn't a collection
- Example: `Set.of().addAll(Set.of("object", 81, 96, "case"));`

### `<Set>.contains(value)`
- This allows you to check whether a value is in the set
- Parameter - Value: the value that you want to check in the set
- Returns - Boolean: whether the value is in the set
- Example: `Set.of("object").contains("object");`

### `<Set>.containsAll(otherCollection)`
- This allows you to check whether a collection of values are all in the set
- Parameter - Collection: the collection of values you want to check in the set
- Returns - Boolean: whether all the values are in the set
- Throws - Error: `"'...' is not a collection"` if the parameter isn't a collection
- Example: `Set.of("object").containsAll(Set.of("object", 81, 96, "case"));`

### `<Set>.isEmpty()`
- This allows you to check whether the set has no values
- Returns - Boolean: whether the set is empty
- Example: `Set.of().isEmpty();`

### `<Set>.clear()`
- This removes all values from inside the set
- Example: `Set.of("object").clear();`

### `<Set>.toString()`
- This converts the set to a string and evaluating any collections inside it
- Returns - String: the string representation of the set
- Example: `Set.of("object").toString();`


# Function class
Function class for Arucas.  
Fully Documented.

## Static Member Functions

### `Function.getBuiltIn(functionName, parameterCount)`
- Returns a built-in function delegate with the given name and parameter count.
- Parameters: String, Number: the name of the function, the parameter count of the function
- Returns - Function: the built-in function delegate
- Example: `Function.getBuiltIn("print", 1);`

### `Function.getMethod(object, methodName, parameterCount)`
- Returns a method delegate with the given name and parameter count.
- Parameters: Value, String, Number: the object to call the method on, the name of the method, the parameter count of the method
- Returns - Function: the method delegate
- Example: `Function.getMethod(this, "print", 1);`

### `Function.callWithList(delegate, parameters)`
- Calls the given delegate with the given parameters.
- Parameters: Function, List: the delegate to call, the parameters to pass to the delegate
- Returns - Value: the return value of the delegate
- Example: `Function.callWithList(this.print, List.of("Hello World!"));`

### `Function.call(delegate, parameters)`
- Calls the given delegate with the given arbitrary parameters.
- Parameters: Function, Value...: the delegate to call, the parameters to pass to the delegate
- Returns - Value: the return value of the delegate
- Example: `Function.call(Function.getBuiltIn("print", 1), "Hello World!");`


# Error class
Error class for Arucas.  
Fully Documented.

## Constructors

### `new Error()`
- This creates a new Error object with no message
- Returns - Error: the new Error object
- Example: `new Error();`

### `new Error(details)`
- This creates a new Error object with the given details as a message
- Parameter - String: the details of the error
- Returns - Error: the new Error object
- Example: `new Error("This is an error");`

### `new Error(details, value)`
- This creates a new Error object with the given details as a message and the given value
- Parameter - String, Value: the details of the error, the value that is related to the error
- Returns - Error: the new Error object
- Example: `new Error("This is an error", "object");`

## Member Functions

### `<Error>.getFormattedDetails()`
- This returns the message of the error in a formatted string
- Returns - String: the details of the error
- Example: `new Error("Error!").getFormattedDetails();`

### `<Error>.getDetails()`
- This returns the raw message of the error
- Returns - String: the details of the error
- Example: `new Error("Error!").getDetails();`

### `<Error>.getValue()`
- This returns the value that is related to the error
- Returns - Value: the value that is related to the error
- Example: `new Error("Error!", "object").getValue();`


# Type class
Type class for Arucas.  
Fully Documented.

## Static Member Functions

### `Type.of(value)`
- This gets the specific type of a value
- Parameter - Value: the value you want to get the type of
- Returns - Type: the type of the value
- Example: `Type.of(0);`

## Member Functions

### `<Type>.instanceOf(type)`
- This checks whether a type is a subtype of another type
- Parameter - Type: the other type you want to check against
- Returns - Boolean: whether the type is of that type
- Example: `Type.of("").instanceOf(Type.of(0));`

### `<Type>.getName()`
- This gets the name of the type
- Returns - String: the name of the type
- Example: `String.type.getName();`

### `<Type>.getConstructor(parameters)`
- This gets the constructor of the type
- Parameter - Number: the number of parameters for the constructor
- Returns - Function: the constructor of the type
- Example: `String.type.getConstructor(0);`

### `<Type>.getStaticMethod(name, parameters)`
- This gets the static method of the type
- Parameter - String: the name of the method
- Parameter - Number: the number of parameters for the method
- Returns - Function: the static method of the type
- Example: `String.type.getStaticMethod("nonExistent", 0);`


# Enum class
Enum class for Arucas.  
Fully Documented.

## Member Functions

### `<Enum>.getName()`
- This allows you to get the name of the enum value
- Returns - String: the name of the enum value
- Example: `enum.getName();`

### `<Enum>.ordinal()`
- This allows you to get the ordinal of the enum value
- Returns - Number: the ordinal of the enum value
- Example: `enum.ordinal();`


# Thread class
Thread class for Arucas.  
Fully Documented.

## Static Member Functions

### `Thread.getCurrentThread()`
- This gets the current thread that the code is running on
- Returns - Thread: the current thread
- Throws - Error: `"Thread is not safe to get"` if the thread doesn't originate from Arucas
- Example: `Thread.getCurrentThread();`

### `Thread.runThreaded(function)`
- This starts a new thread and runs a function on it, the thread will  
  terminate when it finishes executing the function, threads will stop automatically  
  when the program stops, you are also able to stop threads by using the Thread value
- Parameter - Function: the function you want to run on a new thread
- Returns - Thread: the new thread
- Example: `Thread.runThreaded(fun() { print(); });`

### `Thread.runThreaded(name, function)`
- This starts a new thread with a specific name and runs a function on it
- Parameters - String, Function: the name of the thread, the function you want to run on a new thread
- Returns - Thread: the new thread
- Example: `Thread.runThreaded("MyThread", fun() { print(); });`

### `Thread.freeze()`
- This freezes the current thread, stops anything else from executing on the thread  
  this can only be stopped by stopping the thread
- Returns - Thread: the current thread
- Example: `Thread.freeze();`

## Member Functions

### `<Thread>.isAlive()`
- This checks if the thread is alive (still running)
- Returns - Boolean: true if the thread is alive, false if not
- Example: `Thread.getCurrentThread().isAlive();`

### `<Thread>.getAge()`
- This gets the age of the thread in milliseconds
- Returns - Number: the age of the thread
- Example: `Thread.getCurrentThread().getAge();`

### `<Thread>.getName()`
- This gets the name of the thread
- Returns - String: the name of the thread
- Example: `Thread.getCurrentThread().getName();`

### `<Thread>.stop()`
- This stops the thread from executing, anything that was running will be instantly stopped
- Throws - Error: `"Thread is not alive"` if the thread is not alive
- Example: `Thread.getCurrentThread().stop();`


# Error class
Error class for Arucas.  
Fully Documented.

## Constructors

### `new Error()`
- This creates a new Error object with no message
- Returns - Error: the new Error object
- Example: `new Error();`

### `new Error(details)`
- This creates a new Error object with the given details as a message
- Parameter - String: the details of the error
- Returns - Error: the new Error object
- Example: `new Error("This is an error");`

### `new Error(details, value)`
- This creates a new Error object with the given details as a message and the given value
- Parameter - String, Value: the details of the error, the value that is related to the error
- Returns - Error: the new Error object
- Example: `new Error("This is an error", "object");`

## Member Functions

### `<Error>.getFormattedDetails()`
- This returns the message of the error in a formatted string
- Returns - String: the details of the error
- Example: `new Error("Error!").getFormattedDetails();`

### `<Error>.getDetails()`
- This returns the raw message of the error
- Returns - String: the details of the error
- Example: `new Error("Error!").getDetails();`

### `<Error>.getValue()`
- This returns the value that is related to the error
- Returns - Value: the value that is related to the error
- Example: `new Error("Error!", "object").getValue();`


# File class
File class for Arucas.  
Fully Documented.

## Constructors

### `new File(path)`
- This creates a new File object with set path
- Parameter - String: the path of the file
- Returns - File: the new File object
- Example: `new File("foo/bar/script.arucas");`

## Static Member Functions

### `File.getDirectory()`
- This returns the file of the working directory
- Returns - File: the file of the working directory
- Example: `File.getDirectory();`

## Member Functions

### `<File>.getName()`
- This returns the name of the file
- Example: `File.getName();`

### `<File>.read()`
- This reads the file and returns the contents as a string
- Returns - String: the contents of the file
- Throws - Error: `"There was an error reading the file: ..."` if there was an error reading the file
- Example: `new File("foo/bar/script.arucas").read();`

### `<File>.write(string)`
- This writes a string to a file
- Parameter - String: the string to write to the file
- Throws - Error: `"There was an error writing the file: ..."` if there was an error writing the file
- Example: `new File("foo/bar/script.arucas").write("Hello World!");`

### `<File>.getSubFiles()`
- This returns a list of all the sub files in the directory
- Returns - List: a list of all the sub files in the directory
- Throws - Error: `"Could not find any files"` if there are no files in the directory
- Example: `new File("foo/bar/script.arucas").getSubFiles();`

### `<File>.delete()`
- This deletes the file
- Returns - Boolean: true if the file was deleted
- Throws - Error: `"Could not delete file: ..."` if there was an error deleting the file
- Example: `new File("foo/bar/script.arucas").delete();`

### `<File>.exists()`
- This returns if the file exists
- Returns - Boolean: true if the file exists
- Throws - Error: `"Could not check file: ..."` if there was an error checking the file
- Example: `new File("foo/bar/script.arucas").exists();`

### `<File>.createDirectory()`
- This creates all parent directories of the file if they don't already exist
- Returns - Boolean: true if the directories were created
- Throws - Error: `"..."` if there was an error creating the directories
- Example: `new File("foo/bar/script.arucas").createDirectory();`

### `<File>.getPath()`
- This returns the path of the file
- Returns - String: the path of the file
- Example: `new File("foo/bar/script.arucas").getPath();`

### `<File>.getAbsolutePath()`
- This returns the absolute path of the file
- Returns - String: the absolute path of the file
- Example: `new File("foo/bar/script.arucas").getAbsolutePath();`

### `<File>.open()`
- This opens the file (as in opens it on your os)
- Example: `new File("foo/bar/script.arucas").open();`


# Math class
Math class extension for Arucas. Provides many basic math functions.  
This is a utility class, and cannot be constructed.  
Fully Documented.

## Static Member Functions

### `Math.round(num)`
- Rounds a number to the nearest integer
- Parameter - Number: the number to round
- Returns - Number: the rounded number
- Example: `Math.round(3.5);`

### `Math.ceil(num)`
- Rounds a number up to the nearest integer
- Parameter - Number: the number to round
- Returns - Number: the rounded number
- Example: `Math.ceil(3.5);`

### `Math.floor(num)`
- Rounds a number down to the nearest integer
- Parameter - Number: the number to round
- Returns - Number: the rounded number
- Example: `Math.floor(3.5);`

### `Math.sqrt(num)`
- Returns the square root of a number
- Parameter - Number: the number to square root
- Returns - Number: the square root of the number
- Example: `Math.sqrt(9);`

### `Math.abs(num)`
- Returns the absolute value of a number
- Parameter - Number: the number to get the absolute value of
- Returns - Number: the absolute value of the number
- Example: `Math.abs(-3);`

### `Math.mod(num1, num2)`
- Returns the remainder of a division
- Parameters - Number, Number: the number to divide, the divisor
- Returns - Number: the remainder of the division
- Example: `Math.mod(5, 2);`

### `Math.max(num1, num2)`
- Returns the largest number
- Parameters - Number, Number: the numbers to compare
- Returns - Number: the largest number
- Example: `Math.max(5, 2);`

### `Math.min(num1, num2)`
- Returns the smallest number
- Parameters - Number, Number: the numbers to compare
- Returns - Number: the smallest number
- Example: `Math.min(5, 2);`

### `Math.clamp(value, min, max)`
- Clamps a value between a minimum and maximum
- Parameters - Number, Number, Number: the value to clamp, the minimum, the maximum
- Returns - Number: the clamped value
- Example: `Math.clamp(10, 2, 8);`

### `Math.toRadians(num)`
- Converts a number from degrees to radians
- Parameter - Number: the number to convert
- Returns - Number: the number in radians
- Example: `Math.toRadians(90);`

### `Math.toDegrees(num)`
- Converts a number from radians to degrees
- Parameter - Number: the number to convert
- Returns - Number: the number in degrees
- Example: `Math.toDegrees(Math.PI);`

### `Math.log(num)`
- Returns the natural logarithm of a number
- Parameter - Number: the number to get the logarithm of
- Returns - Number: the natural logarithm of the number
- Example: `Math.log(Math.E);`

### `Math.logBase(base, num)`
- Returns the logarithm of a number with a specified base
- Parameters - Number, Number: the base, the number to get the logarithm of
- Returns - Number: the logarithm of the number
- Example: `Math.logBase(2, 4);`

### `Math.log10(num)`
- Returns the base 10 logarithm of a number
- Parameter - Number: the number to get the logarithm of
- Returns - Number: the base 10 logarithm of the number
- Example: `Math.log10(100);`

### `Math.sin(num)`
- Returns the sine of a number
- Parameter - Number: the number to get the sine of
- Returns - Number: the sine of the number
- Example: `Math.sin(Math.PI);`

### `Math.cos(num)`
- Returns the cosine of a number
- Parameter - Number: the number to get the cosine of
- Returns - Number: the cosine of the number
- Example: `Math.cos(Math.PI);`

### `Math.tan(num)`
- Returns the tangent of a number
- Parameter - Number: the number to get the tangent of
- Returns - Number: the tangent of the number
- Example: `Math.tan(Math.PI);`

### `Math.arcsin(num)`
- Returns the arc sine of a number
- Parameter - Number: the number to get the arc sine of
- Returns - Number: the arc sine of the number
- Example: `Math.arcsin(Math.sin(Math.PI));`

### `Math.arccos(num)`
- Returns the arc cosine of a number
- Parameter - Number: the number to get the arc cosine of
- Returns - Number: the arc cosine of the number
- Example: `Math.arccos(Math.cos(Math.PI));`

### `Math.arctan(num)`
- Returns the arc tangent of a number
- Parameter - Number: the number to get the arc tangent of
- Returns - Number: the arc tangent of the number
- Example: `Math.arctan(Math.tan(Math.PI));`

### `Math.cosec(num)`
- Returns the cosecant of a number
- Parameter - Number: the number to get the cosecant of
- Returns - Number: the cosecant of the number
- Example: `Math.cosec(Math.PI);`

### `Math.sec(num)`
- Returns the secant of a number
- Parameter - Number: the number to get the secant of
- Returns - Number: the secant of the number
- Example: `Math.sec(Math.PI);`

### `Math.cot(num)`
- Returns the cotangent of a number
- Parameter - Number: the number to get the cotangent of
- Returns - Number: the cotangent of the number
- Example: `Math.cot(Math.PI);`


# Network class
Network class extension for Arucas. Allows you to do http requests.  
Import the class with `import Network from util.Network;`  
This is a utility class and cannot be constructed.  
Fully Documented.

## Static Member Functions

### `Network.requestUrl(url)`
- Requests a url and returns the response
- Parameter - String: the url to request
- Returns - String: the response from the url
- Throws - Error: `"Failed to request data from ..."` if the request fails
- Example: `Network.requestUrl("https://google.com");`

### `Network.openUrl(url)`
- Opens a url in the default browser
- Parameter - String: the url to open
- Throws - Error: `"Failed to open url ..."` if the request to open
- Example: `Network.openUrl("https://google.com");`


# Collector class
Collector class for Arucas.  
Import the class with `import Collector from util.Collection;`  
This class is similar to Java streams, allowing for easy modifications of collections.  
Fully Documented.

## Static Member Functions

### `Collector.of(collection)`
- This creates a collector for a collection
- Parameter - Collection: the collection of values you want to evaluate
- Returns - Collector: the collector
- Throws - Error: `"'...' is not a collection"` if the parameter isn't a collection
- Example: `Collector.of([1, 2, 3]);`

### `Collector.of(value...)`
- This creates a collector for a collection
- Parameter - Value: the values you want to evaluate
- Returns - Collector: the collector
- Example: `Collector.of(1, 2, 3);`

### `Collector.isCollection(value)`
- This checks if the value is a collection
- Parameter - Value: the value you want to check
- Returns - Boolean: `true` if the value is a collection
- Example: `Collector.isCollection([]);`

## Member Functions

### `<Collector>.filter(predicate)`
- This filters the collection using the predicate
- Parameter - Function: a function that takes a value and returns Boolean, true if it should be kept, false if not
- Returns - Collector: the filtered collection
- Throws - Error: `"Predicate must return Boolean"` if the predicate doesn't return a Boolean
- Example: `Collector.of([1, 2, 3]).filter(fun(value) { return value < 3; });`

### `<Collector>.anyMatch(predicate)`
- This checks if any of the values in the collection match the predicate
- Parameter - Function: a function that takes a value and returns Boolean, true if it matches, false if not
- Returns - Boolean: true if any of the values match the predicate, false if not
- Throws - Error: `"Predicate must return Boolean"` if the predicate doesn't return a Boolean
- Example: `Collector.of([1, 2, 3]).anyMatch(fun(value) { return value < 3; });`

### `<Collector>.allMatch(predicate)`
- This checks if all the values in the collection match the predicate
- Parameter - Function: a function that takes a value and returns Boolean, true if it matches, false if not
- Returns - Boolean: true if all the values match the predicate, false if not
- Throws - Error: `"Predicate must return Boolean"` if the predicate doesn't return a Boolean
- Example: `Collector.of([1, 2, 3]).allMatch(fun(value) { return value < 5; });`

### `<Collector>.noneMatch(predicate)`
- This checks if none of the values in the collection match the predicate
- Parameter - Function: a function that takes a value and returns Boolean, true if it matches, false if not
- Returns - Boolean: true if none of the values match the predicate, false if not
- Throws - Error: `"Predicate must return Boolean"` if the predicate doesn't return a Boolean
- Example: `Collector.of([1, 2, 3]).noneMatch(fun(value) { return value < 5; });`

### `<Collector>.map(mapper)`
- This maps the values in Collector to a new value
- Parameter - Function: a function that takes a value and returns a new value
- Returns - Collector: a new Collector with the mapped values
- Example: `Collector.of([1, 2, 3]).map(fun(value) { return value * 2; });`

### `<Collector>.forEach(function)`
- This iterates over all the values in the Collector and calls the passed in function with each value
- Parameter - Function: a function that takes a value and returns nothing
- Returns - Collector: the Collector
- Example: `Collector.of([1, 2, 3]).forEach(fun(value) { print(value); });`

### `<Collector>.flatten()`
- If there are values in the collector that are collections they will be expanded, collections inside collections  
  are not flattened, you would have to call this method again
- Returns - Collector: a new Collector with the expanded values
- Example: `Collector.of([1, 2, [3, 4]]).flatten();`

### `<Collector>.toSet()`
- This puts all the values in the collector into a set and returns it
- Returns - Set: a set with all the values in the collector
- Example: `Collector.of([1, 2, 3]).toSet();`

### `<Collector>.toList()`
- This puts all the values in the collector into a list and returns it
- Returns - List: a list with all the values in the collector
- Example: `Collector.of([1, 2, 3]).toList();`


# Java class
Java class for Arucas. This allows for direct interaction from Arucas to Java  
Import the class with `import Java from util.Internal;`  
Fully Documented.

## Static Member Functions

### `Java.doubleOf(num)`
- Creates a Java value double, to be used in Java
- Parameter - Number: the number to convert to a Java double
- Returns - Java: the double in Java wrapper
- Example: `Java.doubleOf(1.0);`

### `Java.floatOf(num)`
- Creates a Java value float, to be used in Java, since  
  floats cannot be explicitly declared in Arucas
- Parameter - Number: the number to convert to a Java float
- Returns - Java: the float in Java wrapper
- Example: `Java.floatOf(1.0);`

### `Java.longOf(num)`
- Creates a Java value long, to be used in Java since  
  longs cannot be explicitly declared in Arucas
- Parameter - Number: the number to convert to a Java long
- Returns - Java: the long in Java wrapper
- Example: `Java.longOf(1000000000.0);`

### `Java.intOf(num)`
- Creates a Java value int, to be used in Java since  
  ints cannot be explicitly declared in Arucas
- Parameter - Number: the number to convert to a Java int
- Returns - Java: the int in Java wrapper
- Example: `Java.intOf(0xFF);`

### `Java.shortOf(num)`
- Creates a Java value short, to be used in Java since  
  shorts cannot be explicitly declared in Arucas
- Parameter - Number: the number to convert to a Java short
- Returns - Java: the short in Java wrapper
- Example: `Java.shortOf(0xFF);`

### `Java.byteOf(num)`
- Creates a Java value byte, to be used in Java since  
  bytes cannot be explicitly declared in Arucas
- Parameter - Number: the number to convert to a Java byte
- Returns - Java: the byte in Java wrapper
- Example: `Java.byteOf(0xFF);`

### `Java.charOf(string)`
- Creates a Java value char, to be used in Java since  
  chars cannot be explicitly declared in Arucas
- Parameter - String: the string with one character to convert to a Java char
- Returns - Java: the char in Java wrapper
- Throws - CodeError: if the string is not exactly one character long
- Example: `Java.charOf("f");`

### `Java.booleanOf(bool)`
- Creates a Java value boolean, to be used in Java
- Parameter - Boolean: the boolean to convert to a Java boolean
- Returns - Java: the boolean in Java wrapper
- Example: `Java.booleanOf(true);`

### `Java.valueOf(value)`
- Converts any Arucas value into a Java value then wraps it  
  in the Java wrapper and returns it
- Parameter - Value: any value to get the Java value of
- Returns - Java/Null: the Java wrapper value, null if argument was null
- Example: `Java.valueOf("Hello World!");`

### `Java.classFromName(className)`
- Gets a Java class from the name of the class
- Parameter - String: the name of the class you want to get
- Returns - Java: the Java Class<?> value wrapped in the Java wrapper
- Throws - Error: `"No such class with ..."` if the class is not found
- Example: `Java.classFromName("java.util.ArrayList");`

### `Java.getStaticField(className, fieldName)`
- Gets a static field Java value from a Java class
- Parameters - String, String: the name of the class, the name of the field
- Returns - Java: the Java value of the field wrapped in the Java wrapper
- Throws - Error: `"No such class with ..."` if the class is not found
- Example: `Java.getStaticField("java.lang.Integer", "MAX_VALUE");`

### `Java.setStaticField(className, fieldName, newValue)`
- Sets a static field in a Java class with a new value, the type of the new  
  value needs to match the type of the field, you can pass in Java wrapped values to  
  guarantee type matching, they will be unwrapped, regular values will be converted
- Parameters - String, String, Value: the name of the class, the name of the field, the new value
- Throw - Error: `"No such class with ..."` if the class is not found
- Example: `Java.setStaticField("java.lang.Integer", "MAX_VALUE", Java.intOf(100));`

### `Java.getStaticMethodDelegate(className, methodName, parameters)`
- Gets a static method delegate from a Java class, delegating the method is  
  much faster than directly calling it since it uses MethodHandles, if you are repetitively  
  calling a static method you should delegate it and call that delegate
- Parameters - String, String, Number: the name of the class, the name of the method, the number of parameters
- Returns - Function: the delegated Java method in an Arucas Function
- Throws - Error: `"..."` if the class is not found or method cannot be found
- Example: `Java.getStaticMethodDelegate("java.lang.Integer", "parseInt", 1);`

### `Java.arrayWithSize(size)`
- Creates a Java Object array with a given size, the array is filled with null values  
  by default and can be filled with any Java values, arrays cannot be expanded or shrunk, you cannot  
  create a primitive array, you will need some more reflection to do that
- Parameter - Number: the size of the array
- Returns - Java: the Java Object array
- Example: `Java.arrayWithSize(10);`

### `Java.arrayOf(values...)`
- Creates a Java Object array with a given values, this will be the size of the array,  
  again this cannot be used to create primitive arrays
- Parameters - Value...: the values to add to the array
- Returns - Java: the Java Object array
- Example: `Java.arrayOf(1, 2, 3, "string!", false);`

### `Java.callStaticMethod(className, methodName, parameters...)`
- Calls a static method of a Java class, this is slower than delegating a method,  
  but better for a one off call
- Parameters - String, String, Value...: the name of the class, the name of the method, any parameters  
  to call the method with, this can be none, a note - if you are calling a VarArg method then you must  
  have your VarArg parameters in a Java Object array
- Returns - Java: the return value of the method wrapped in the Java wrapper
- Throws - Error: `"..."` if the class is not found or the parameters are incorrect
- Example: `Java.callStaticMethod("java.lang.Integer", "parseInt", "123");`

### `Java.constructClass(className, parameters...)`
- This constructs a Java class with specified class name and parameters
- Parameters - String, Value...: the name of the class, any parameters to pass to the constructor,  
  there may be no parameters, again if calling VarArgs constructor you must have your VarArg  
  parameters in a Java Object array
- Returns - Java: the constructed Java Object wrapped in the Java wrapper
- Throws - Error: `"..."` if the class is not found or the parameters are incorrect
- Example: `Java.constructClass("java.util.ArrayList");`

## Member Functions

### `<Java>.toArucas()`
- This converts the Java value to an Arucas Value
- Returns - Value: the Value in Arucas, this may still be of Java value if the value cannot be  
  converted into an Arucas value, values like Strings, Numbers, Lists, etc... will be converted
- Example: `Java.valueOf([1, 2, 3]).toArucas();`

### `<Java>.getMethodDelegate(methodName, parameters)`
- This returns a method delegate for the specified method name and parameters,  
  delegating the method is much faster since it uses MethodHandles, so if you are calling  
  a method repetitively it is faster to delegate the method and then call the delegate
- Parameters - String, Number: the name of the method, the number of parameters
- Returns - Function: the function containing the Java method delegate
- Throws - Error: `"..."` if the method is not found
- Example: `Java.valueOf("string!").getMethodDelegate("isBlank", 0);`

### `<Java>.callMethod(methodName, parameters...)`
- Deprecated: You should call the method directly on the value: `Java.valueOf("").isBlank();`
- This calls the specified method with the specified parameters, this is slower  
  than calling a delegate, this is the same speed as calling the method directly on the value however
- Parameters - String, Value...: the name of the method, the parameters to call the method with,  
  this may be none, a note - if you are calling a VarArgs method you must pass a Java  
  Object array with your VarArg arguments
- Returns - Java: the return value of the method call wrapped in the Java wrapper
- Throws - Error: `"..."` if the method is not found
- Example: `Java.valueOf("").callMethod("isBlank");`

### `<Java>.getField(fieldName)`
- Deprecated: You should call the method directly on the value:`Java.constructClass("me.senseiwells.impl.Test").A;`
- This returns the Java wrapped value of the specified field
- Parameters - String: the name of the field
- Returns - Java: the Java wrapped value of the field
- Example: `Java.constructClass("me.senseiwells.impl.Test").getField("A");`

### `<Java>.setField(fieldName, value)`
- Deprecated: You should assign the value directly on the value:`Java.constructClass("me.senseiwells.impl.Test").A = "Hello";`
- This sets the specified field to the specified value
- Parameters - String, Value: the name of the field, the value to set the field to,  
  the value type must match the type of the field
- Example: `Java.constructClass("me.senseiwells.impl.Test").setField("A", "Hello");`


### Example code

Running a file from command line:

```kotlin
run("directory/filename.arucas");
```

Loops 100 times printing numbers from 1 to 100:

```kotlin
for (i = 1; i <= 100; i++) {
    print(i);
} 
```

Program that prints "Hello World" every second infinitely:

```kotlin
while (true) {
    sleep(1000);
    print("Hello World");
}
```

Iterating through a list using `foreach`:

```kotlin
list = ["value1", "value2", "value3", "value4", "value5"];

foreach (listVal : list) {
    print(listVal);
}
```

Basic function that takes a list and filters it for Strings:

```kotlin
list = [true, "foo", 9, false, null, "bar"];
stringList = [];

foreach (listVal : list) {
    if (listVal.instanceOf(String.type)) {
	    stringList.append(listVal);
    }
}

print(stringList);
```

Simple program that takes an input from a user and prints it back out:

```kotlin
name = input("What is your name? ");

print("Hi %s have a good day!".formatted(name));
```

Taking input from a user and storing it in a file as a Json:

```kotlin
file = new File(getDirectory() + "/src/test/resources/code/personMap.txt");
personMap = { };

shouldReadFile = input("Would you like to try and read the file?").lowercase();

if (shouldReadFile == "y" || shouldReadFile == "yes" || shouldReadFile == "true") {
    try {
        json = Json.fromString(file.read());
        personMap = json.getValue();
        print("Successfully read file");
    }
    catch (e) {
        print("Failed to read file");
    }
}
  
while (true) {
    name = input("What is your name?");
    age = null;
    while (true) {
        age = input("How old are you?");
        try {
            age = age.toNumber();
            if (age > 100 || age < 5)
                throwRuntimeError("");
        }
        catch (e) {
            print(e);
            print("That is an invalid age!");
            continue;
        }
        break;
    }

    personMap.put(name, age);
    exit = input("Would you like to exit? (y/n)");
    if (exit == "y")
        break;
}

json = Json.fromMap(personMap);
json.writeToFile(file);

print("Saved map to file");
```

Passing lambdas as parameters:

```kotlin
fun runWithDelay(seconds, function) {
    milliseconds = seconds * 1000;
    sleep(milliseconds);
    function();
}

print("program started");

runWithDelay(5, fun() {
    print("lambda");
});

print("program finished");
```

Creating a schedule function:

```kotlin
/**
 * So here we are just making a function with parameters milliseconds (Number) and function (Function)
 * We then call the function `runThreaded` which will run our code on a separate thread
 * `runThreaded` takes in a Function and a List (this is used to pass in parameters)
 * So we just create a lambda with the parameters ms and func, and inside the lambda we tell it to
 * sleep for ms and then call the func, we also have a list which allows us to pass in the parameters
 * for ms and func. 
 */

fun schedule(milliseconds, function) {  
    runThreaded(fun() {   
        sleep(milliseconds);   
        function();   
    });  
}

/*
 Now we can test to see if it works
 */

schedule(1000, fun() {  
    print("This will print second!");  
});  
  
print("This will print first!");
```


# Arucas API

The page of the Wiki is for developers who are interested in implementing the language in their own java projects.

## Usage
[![Release](https://jitpack.io/v/senseiwells/Arucas.svg)](https://jitpack.io/#senseiwells/Arucas)

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.senseiwells:Arucas:version'
}
```

After implementing Arucas you are ready to get started!

To run Arucas you must create a context for the code to run on, this can be done by using `ContextBuilder`. This also allows you to manage built-in extensions and classes, as well as output handlers.
```java
ContextBuilder builder = new ContextBuilder()
    .setDisplayName("MyArucas")                // Sets display name
    .addDefault()                              // Adds default classes, extensions, and conversions
    .addBuiltInClasses(MyBuiltInClass::new)    // Adds your built-in class that doesn't need to be imported
    .addClasses("MyUtils", MyCustomClass::new) // Adds your custom classes with the import path
    .addExtension(MyCustomExtension::new)      // Adds your custom extentions
    .addWrapper("MyUtils", MyWrapper::new)     // Adds your custom wrappers
    .setArucasAPI(new DefaultArucasAPI());     // Sets your API handler

Context context = builder.build();
```

You can then use context to manage your thread handler, this will manage all threads that Arucas uses.

```java
context.getThreadHandler()
    .setFatalErrorHandler(MY_LOGGER::fatal) // Output handler for fatal errors
    .addShutdownEvent(() -> {});             // Adds a Runnable to run when the program stops
```

Now you are able to run Arucas!

```java
arucasCode = "print('Printed from Arucas!');"; // This can be any Arucas code
context.getThreadHandler().runOnThread(context, "system", arucasCode, null);
```

## Wrapper Classes

Now that you've implemented Arucas, lets have a go at implementing some wrapper classes. These are classes in java that Arucas can directly access and use as if it were an actual class in Arucas.

Create a new class implementing `IArucasWrappedClass`

```java
/**
  * You should have the @ArucasClass annotation
  */
@ArucasClass(name = "Example")
public class ArucasExampleWrapper implements IArucasWrappedClass {
}
```

You can use annotations to allow Arucas to access fields and methods in your java class.
All annotated methods' first parameter **must** be of type `Context`, after will be the parameters passed in by Arucas to call that method. Methods must return `Value<?>` or a subclass of `Value<?>`, you can return the class type to be able to return `this`, returning void is also valid, it just returns `NullValue.NULL`.
All annotated fields **must** be of type `Value<?>`, unless the field is final since Arucas is not statically typed and you will be able to assign any `Value` to a field. Final fields are allowed, they will be able to be accessed in Arucas but not be able to be assigned to.
Both fields and methods can be static, and this will be reflected in Arucas too.
You are also able to create constructors and operator methods, these cannot be static.
Constructors have the same rules as methods except they must return `void`.
Operator methods have the same rules as methods but can only be assigned to valid operations.
You can create a static field annotated with `@ArucasDefinition` which will automatically be assigned the class definition at runtime. You can use this to instantiate the wrapper class.

```java
/**
 * Take from Arucas Discord API 
 */
@ArucasClass(name = "DiscordAttachment")  
public class DiscordAttachmentWrapper implements IArucasWrappedClass {  
    @ArucasDefinition  
    public static WrapperClassDefinition DEFINITION;  
  
    private Message.Attachment attachment;  
  
    @ArucasFunction  
    public void saveToFile(Context context, FileValue fileValue) {  
        this.attachment.downloadToFile(fileValue.value);  
    }  
  
    @ArucasFunction  
    public StringValue getFileName(Context context) {  
        return StringValue.of(this.attachment.getFileName());  
    }  
  
    @ArucasFunction  
    public StringValue getFileExtension(Context context) {  
        return StringValue.of(this.attachment.getFileExtension());  
    }  
  
    @ArucasFunction  
    public BooleanValue isImage(Context context) {  
        return BooleanValue.of(this.attachment.isImage());  
    }  
  
    @ArucasFunction  
    public BooleanValue isVideo(Context context) {  
        return BooleanValue.of(this.attachment.isVideo());  
    }  
  
    @ArucasFunction  
    public StringValue getUrl(Context context) {  
        return StringValue.of(this.attachment.getUrl());  
    }  
  
    @ArucasFunction  
    public NumberValue getSize(Context context) {  
        return NumberValue.of(this.attachment.getSize());  
    }  
  
    public static WrapperClassValue newDiscordAttachment(Message.Attachment attachment, Context context) throws CodeError {  
        DiscordAttachmentWrapper attachmentWrapper = new DiscordAttachmentWrapper();  
        attachmentWrapper.attachment = attachment;  
        return DEFINITION.createNewDefinition(attachmentWrapper, context, List.of());  
    }  
  
    @Override  
    public Message.Attachment asJavaValue() {  
        return this.attachment;  
    }  
}
```

Now in your context builder you can do this, and it will add your wrapped class into the language!

```java
ContextBuilder builder = new ContextBuilder()
    .setDisplayName("MyArucas")
    .addDefault()
    .addWrapper(ArucasExampleWrapper::new);
```

Now we can run this code in Arucas

```kotlin
example = new Example();                // This will print 'Constructing: Example'
print(example.PI);                      // This will print 3.1415926
print(example - 10);                    // This will print 'Binary Minus!'
example.setMemberField(["foo", "bar"]); // This sets the member field
print(example);                         // This now prints ["foo", "bar"]
```


