# Documentation

This page is a step-by-step guide to get you started with Arucas, showing you how to install the language, and how to write code for the language, showing you what is possible in the language as well as its features.

> #### [Installation](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/1.%20Installation.md)
> #### [Development Environment](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/2.%20Development%20Environment.md)

> #### [Syntax](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/3.%20Syntax.md)
> #### [Comments](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/4.%20Comments.md)
> #### [Literals](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/5.%20Literals.md)
> #### [Variables](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/6.%20Variables.md)
> #### [Output](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/7.%20Output.md)
> #### [Input](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/8.%20Input.md)
> #### [Operators](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/9.%20Operators.md)
> #### [Scopes](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/10.%20Scopes.md)
> #### [Conditional Statements](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/11.%20Conditional%20Statements.md)
> #### [Switch Statements](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/12.%20Switch%20Statements.md)
> #### [Loops](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/13.%20Loops.md)
> #### [Functions](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/14.%20Functions.md)
> #### [Members](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/15.%20Members.md)
> #### [Lists](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/16.%20Lists.md)
> #### [Maps](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/17.%20Maps.md)
> #### [Errors](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/18.%20Errors%20(Incomplete).md)
> #### [Imports](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/19.%20Imports%20(Incomplete).md)
> #### [Classes](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/20.%20Classes%20(Incomplete).md)
> #### [Enums](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/21.%20Enums%20(Incomplete).md)
> #### [Threads](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/22.%20Threads%20(Incomplete).md)
> #### [Java](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/23.%20Java%20Integration.md)


## Installation

This installation guide is for those who want to just run vanilla Arucas not bundled in with another application. If you are running Arucas inside another application you can just skip this part.

First you need to install the latest version of Arucas, you can download the jar file from [here](https://github.com/senseiwells/Arucas/releases). 
After downloading the jar make sure you have Java 16 or above installed as Arucas relies on this, you can then run the jar using the command line, replacing `<version>` with the appropriate version:
```
java -jar Arucas-<version>.jar -noformat
```
Now you will be running the Arucas Interpreter, here you can type any Arucas code and it will be run, if you want to exit the interpreter you can simply type:
```
exit
```
To run a file with Arucas code from the command line you can use the Built-in function:
```kotlin
run("path/of/arucas/file.arucas");
```


## Development Environment

We recommend the use of the [Arucas Plugin](https://github.com/Kariaro/ArucasHighlighter/tree/main) designed for IntelliJ by [HardCoded](https://github.com/Kariaro), this highlights your code informing you of errors in your code, and adding nice colours :).

Alternatively, if you do not wish to use IntelliJ another option is to use VSCode and set the language to `Java`, and disable validation for error highlighting. You can also configure VSCode to automatically recognize `.arucas` files as Java. 

So now that you are able to run Arucas files, what do we put inside? If you have not already, you should take a look at the [Language Syntax](https://github.com/senseiwells/Arucas/blob/master/docs/Language%20Documentation/3.%20Syntax.md) page briefly, but we will cover everything in detail here.


# Language Syntax

This section has a quick rundown of all the syntax that the language has, like a cheat sheet. You may want to refer back to this section after reading other sections.

## Literals

Arucas provides 6 object types that you are able to create with literals, these are:

`Number` - An object containing a floating point number, represented by a number

`String` - An object containing an array of characters, represented by any text within two double or single quote marks

`Boolean` - An object containing only one of two possible values, represented by `true` or `false` 

`List` - An object containing a list of other objects, represented by having object separated with comma's within square brackets

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

One thing to note for AND and OR is that the left side will always be evaluated first, and if it does not evaluate to `true` in the case of AND, or `false` in the case of OR, the right side will not be evaluated.

Another thing to note is that custom classes are able to override some of these operators, changing their functionality, more information will be on this in the classes section.

## Bitwise Operators:

`&` - This is the bitwise AND

`|` - This is the bitwise OR

`~` - This is the bitwise XOR

`>>` - Right bit shift

`<<` - Left bit shift

One thing to note is that the bitwise AND and OR also work for Booleans, however they do not short circuit, so in the case of AND if the left side was evaluated to `false` it would still evaluate the left side, and similarly OR will evaluate the right side even if the left was already `true`.

## Comments:

You are able to make comments in your code that the compiler will ignore.

`//` - Used to comment until a line break

`/* */` - Used for multi-line comments

## Keywords:


### `{` and `}` 

- These are used to define scopes, or code blocks
- Scopes can contain multiple lines of code inside then and usually is indented to visually show it's in a different scope. Any variables initialized inside a scope cannot be accessed outside that scope, but variables defined outside the scope can be accessed and assigned inside that scope, scopes can be used independently as well as with other statements.

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

A thing to note is that the language is not fussy with how you format your code, in terms of new lines and spaces. So formatting like this is considered valid:

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
- You cannot have `else if`s and `else` without an `if`, if one of these has an expression evaluates to true the code inside their code block will be executed, and the rest will be ignored.

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
- Switch statements can compare a value to both literals and other expressions, similar to an if statement, you cannot have two of the same literal in the cases and the switch statement is evaluated from first case down to last case with literals being the first thing that is checked in each case. If the value does not match any cases, it will run the default branch. You cannot have more than one default branch.

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

- This is for iterating over the values in a list
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
- `catch` can only catch `Error`s, the caught value will be of type `Error` which encapsulates the error message and possibly a value. If errors go uncaught, they will crash the program.

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
- If a class has no constructors, you will be able to construct that class with no parameters. Classes also allow you to define methods, or class functions.  You can overload constructors, and methods in a class by differing the number of parameters.
- You can initialize the class members in the constructor, or initialize them when you define them. If you do not define them, they will default to being `null`.

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
- The var keyword can be used for `static` and non-`static` variables.
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

- This is used for referring to itself inside a class
- `this` is passed in internally, and you will have access to it inside a class constructor, method, or operator method, `this` allows you to refer to all the members inside your class, you can assign, access, or call them.

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
- This is used for custom classes, as the built-in objects cannot be constructed, as they have literals. This will return a new instance of that object.

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
- An important thing to note about this is that it will be the left side's operator method that will get used, for example `a + b` would result in `a`'s plus the operator getting called not `b`'s. Whereas `b + a` would result in `b`'s plus operator getting called not `a`'s
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

- This lets you define `static` members, methods, and initializers in a class
- `static` members and methods can be access without an instance of the class. And a `static` initializer will run when the class is evaluated, `static` methods can be overloaded. `static` members are access by typing the `class` name followed by a `.` and then the member name.

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

- Enums are somewhat similar to classes, but they cannot be constructed and instead have static-like variables of the type that cannot be modified. Enums are great if you want constants that can also encapsulate other values.

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
- Currently, cyclical imports are not supported, but usually you don't need two files importing each other.

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

## Comments

Let's start by introducing comments, comments don't do anything in terms of running the code, but instead they allow you to describe what is happening in the code, when the code is run all comments are completely ignored.

There are two types of comments, single line and multiline. A single line comment can be written by typing `//` followed by your text, and a multi line comment consists of `/*` followed by your line(s) of text and then closed with `*/`:
```
// This is a single line comment
/*
 This is a multi line comment
 Very cool!
 */
```
An important thing to note about single line comments is that text will only be ignored after the `//`, anything on the left of the comment will still run as code.

## Literals

### String literals

Creating a string is similar to all other languages, you can create a string of characters by using double quotes, `""`, or single quotes, `''`, this is personal preference as there is no difference.
```kotlin
"Example string"
'Example string'
```
You are also able to escape certain characters by using the `\` character, this is to be able to use `"`, `'`, and other characters like tab `\t`, and newline `\n` inside of strings.
```kotlin
"\tIntended example with new line\n"
```

Now if you tried to just have a string literal in an Arucas file it would throw an error, this is because all code expressions must be followed by a `;`, this is how the language is able to know when one expression ends and another starts.
```kotlin
"This is valid syntax!";
```

### Number literals

Numbers are very easy to create, you can simply just type them! Numbers can be easily modified and are an essential value, we will explore more about how to manipulate numbers in the operators section.
```kotlin
0; 1; 2; 3; 4; 5;
```
Decimals are also supported:
```kotlin
1.5; 3.1415926; 
```
You are also able to write numbers in hexadecimal (base 16), don't worry if you don't know what this is, it's not necessary to use, just a handy feature.
```kotlin
0xFF; 0x1B9E00;
```

### Boolean literals

Booleans are very simple, there are just two possible literals for these.
```kotlin
true;
false;
```
These values are used to do boolean logic, which we will cover in the operators section.

### Null literal

Null is as simple as it gets, there is only one literal for it:
```
null;
```
The `null` value represents nothing, it is used when a value doesn't exist, be careful will how it is used though, null safety is important, you don't want to get `null` values where you want other values.

### List literals

Lists are a more complex data structure and these allow you to store many values of any type inside of it, including other lists. Lists in Arucas have a very simple syntax:
```kotlin
["Example", 1, true, null];
```
Lists are great for storing many pieces of data in one place. We will cover lists more in detail later in this guide.

### Map literals

Maps are also a complex data structure that allow you to map one value to another, allowing you to make keys to access values. The syntax is very straightforward:
```kotlin
// Here we are mapping numbers to their names
{1: "one", 2: "two", 3: "three"};
```
Maps are a fast way of storing data that needs to be accessed, again we will cover maps in greater detail later.


## Variables

A key part of programming is being able to manipulate data, and we do this by using variables, you can think of a variable like a container. To define a variable we need to give it a name and then a value, and we can assign the variable with the value by using the assignment operator, `=`:
```kotlin
exampleVariable = "Example string";
```
Assigning a variable is like putting something inside the container.

Variable names can only include letters and underscores, by convention variable names should follow camel casing, this is where you capitalize all the words bar the first then squash them together.

Once you have defined a variable, you can reassign the variable by again using the assignment operator.
```kotlin
exampleVariable = "Example string";
exampleVariable = "Overwritten!";
// exampleVariable now stores the value: "Overwritten"
```
Variables can store any type of value, we will come onto other types of values, for example numbers or booleans.

Now once you have stored a value in a variable you can use it by referencing the name of the variable, referring back to the previous analogy, this is like peeking into the container to see what is inside.
```kotlin
exampleVariable = "Example string";
print(exampleVariable);
// We would get an output of: Example string
```


## Output

Now we know how to create a string, we can output it to the console. We can do this by using a function, we will cover functions in more detail later, but for now we can just use it and accept that it works. The main function that you will use to output is called `print`, and to call the function we follow the name up with a pair of brackets:
```kotlin
print();
```
This won't actually print anything, since we haven't told it what to print. We can provide this information by adding arguments inside our brackets:
```kotlin
// Having 1 parameter in the print function causes 
// it to automatically add a new line after it
print("Hello World!");
// This would print: Hello World\n
```
The `print` function also has the capability of concatenating (joining) strings together
```kotlin
print("Hello", "World");
// This would print: Hello World
```

## Input

We can take input from the console, by using another simple function called `input`, unlike print this function can only have one parameter, this is the prompt that the user is displayed with for their input:
```kotlin
input("What is your name? ");
```
The user will then be able to type in the console and once they press enter their input will be submitted.

So now that we've got the function to prompt the user with input we need to store it, and we can do this by using a variable, like before how we stored literals inside a variable we can store what we call the return value of the function in a variable too:
```kotlin
userInput = input("What is your name? ");
```
Now that we have the user input stored in a variable, we can use it inside our code:
```kotlin
userInput = input("What is your name? ");
// If the user inputted "mike"
print("Your name is: ", userInput, "\n");
// This should print Your name is: mike\n
```

## Operators

There are quite a few operators in Arucas but don't worry, most of them are similar to other languages and are easy to pick up!

### `(` and `)` - Brackets

While not necessarily an operator, I think that brackets are an important thing to mention before we talk about the other operators. Similar to what you might have learnt in maths, brackets allow you to change the order of operations. We will cover where brackets may be useful in the following operators.

### `=` - Assignment

We have already covered this operator, the assignment operator, briefly when talking about creating variables, but I will reiterate, this operator allows you to assign a value to a variable:
```kotlin
exampleVariable = "Example string";
```
The assignment operator also has a neat feature that allows you to assign multiple values at the same time using lists:
```kotlin
example1, example2, example3 = [1, 2, 3];
print(example1); // This would print 1
print(example2); // This would print 2
print(example3); // This would print 3
```
This works with any list but the list must be the same size as the number of values you want to assign to:
```kotlin
// This would crash since there is not a match
// with the number of variables and values
example1, example2 = [1, 2, 3];
```

Another thing that is important about assignment is that it will also return the value that was just assigned, for example:
```kotlin
v = 10;
print(v = 12);
// This will print 12 because the assignment
// returns the value that was assigned which was 12
```
A mention about brackets here, the value being assigned will always be evaluated first so for example:
```kotlin
v = 0;
// We will cover the addition operator in the next
// section you might want to read that first then come back
print(v = 10 + 3);
// This will print 13 because v was assigned
// to the value of 13 because 10 + 3 = 13
```
But what if we want to assign a value to a variable, then manipulate that value to print:
```kotlin
v = 0;
// We put the assignment in brackets so it
// happens with out adding the 3 to the 10
print((v = 10) + 3);
// This will print 13 because v was assigned to 10
// and then the assignment returned the value which 
// in this case was 10, then it added 3 to that value
// which equals 13 which was then printed

print(v);
// This will print 10 since we did not assign 13 to v
``` 

### `+` - Addition

This is the addition operator, this allows you to add two things together, usually numbers, but this operator by default also works with strings to concatenate them.
```kotlin
result = 9 + 10;
print(result); 
// This would print 21... I mean 19

print(0.5 + 0.5);
// This would print 1

stringResult = "5" + "6";
print(stringResult);
// This would print 56

print("Hello W" + "orld");
// This would print Hello World 
```

The addition operator can also be used as a unary operator, this means that you can have it on the left side of a value with no other value on the left, like this:
```kotlin
print(+10);
// This would print 10
```
This is pretty redundant, but is to be consistent with the subtraction unary operator.

### `-` - Subtraction

This is the subtraction operator, this allows you to take away one value to another, by default this operator only works with numbers.
```kotlin
someMath = 29 - 8;
print(someMath);
// This would print 21

print(9 - 80);
// This would print -71
```

The subtraction operator can also be used as a unary operator, this allows you to write negative values:
```kotlin
print(-10);
// This would print -10
```
An important thing to note is that the subtraction operator has a low predecence and so it will be applied last, here is an example:
```kotlin
-2 ^ 2; // -> -4, this does 2 ^ 2 then makes it negative

(-2) ^ 2; // -> 4
```

### `*` - Multiplication

This is the multiplication operator, and this allows you to multiply two values together, by default this only works with numbers.
```kotlin
print(5 * 4);
// This would print 20

print(20 * 0.5);
// This would print 10
```

An important thing to note is that multiplication will take precedence over addition and subtraction, here's an example:
```kotlin
3 + 4 * 5; // -> 23
```
If you want addition to take precedence, then you will need to use brackets:
```kotlin
(3 + 4) * 5; // -> 35
```

### `/` - Division

This is the division operator, this allows for dividing of two values, by default this only works with numbers.
```kotlin
print(20 / 2);
// This would print 10

print(3.141 / 500);
// This would print 0.006282
```

Similar to multiplication, division takes precedence over addition and subtraction.

### `^` - Exponent

This is the exponent operator and allows you to raise a base to a power, by default this only works with numbers.
```kotlin
print(2 ^ 5);
// This would print 32

print(25 ^ 0.5);
// This would print 5
```

Exponents take precedence over both addition, subtraction, multiplication, and division, here's an example:
```kotlin
5 * 2 ^ 3; // -> 40

(5 * 2) ^ 3; // -> 1000
```

### `++` and `--` - Increment and Decrement

These are the increment and decrement operators, by default these only work on numbers, these are just syntactic sugar for making a value equal to one more or less than its current value:
```kotlin
value = 9;
value++; // value now equals 10
value--; // value now equals 9
```

Using the increment and decrement operators is the exact same as writing:
```kotlin
value = 9;
value = value + 1; // value now equals 10
value = value - 1; // value now equals 9
```

Internally, Arucas compiles the first example into the second example. The increment and decrement are just a shorthand.

### `.` - Dot

The dot operator is used to access and call members of a value, don't worry if you don't know what this means, yet we will cover this in more detail. Every value has members by default, and this is how you can interact with them.
```kotlin
value = "Example string";
value = value.uppercase();
// value now equals "EXAMPLE STRING"
```

### `&&` - AND

This is the and operator, and by default is used between boolean values for boolean logic. Here is and example:
```kotlin
true && true; // -> true
true && false; // -> false
false && true; // -> false
false && false; // -> false
```

The and operator takes two boolean values and will only return `true` if both boolean values are `true` otherwise it will return `false`.

An important feature of this and operator is that it short circuits. Now to explain this you need to understand that the expressions are evaluated one at a time, and it goes from left to right. If the left expression of the and operator is `false` then it knows that no matter whether the right-hand side is `true` or fast it will always return `false` so it skips evaluating the right-hand side.

If you want to use an and operator that evaluates both sides you can use the bitwise and operator `&`, we will go over this later.

### `||` - OR

This is the or operator, and by default is used between boolean values for boolean logic. Here is an example:
```kotlin
true || true; // -> true
true || false; // -> true
false || true; // -> true
false || false; // -> false
```
The or operator takes two booleans and will only return `true` if at least one of the boolean values is `true`, otherwise it will return `false`.

Similarly to the and operator, this will short circuit, if the left-hand side evaluates to `true` then it will always return `true` so it skips evaluating the right-hand side.

If you want to use an or operator that evaluates both sides you can use the bitwise or operator `|`, we will again go over this later.

### `~` - XOR

This is the exclusive or operator and can be used with booleans (as well as numbers, but will cover this later) by default. Here is an example:
```kotlin
true ~ true; // -> false
true ~ false; // -> true
false ~ true; // -> true
false ~ false; // -> false
```
This exclusive or operator takes two boolean and will only return `true` if the boolean values are different from each other, in this case one must always be `true`, and one must always be `false` for it to return `true`.

This operator does not short circuit since it always needs to check both left and right-hand side, this is the same operator that is used for the bitwise XOR, we will go over this later.

### `!` - NOT

This is the not operator and by default only can be used for booleans, this inverts the boolean, here is an example:
```kotlin
!true; // -> false
!false; // -> true
```
This takes the boolean and returns the opposite boolean value, unlike the other operators shown this a unary only operator, meaning it only has a value on the right-hand side and not the left.

### `==` - Equals

This is the equals operator and can be used between any values, it checks whether two values are equal.
```kotlin
true == false; // -> false
"string" == "string"; // -> true

num = 10;
num == 10; // -> true
```
This is often useful for doing `null` checks, the safest way to do a null check is the following:
```kotlin
example = null;
example = "";
null == example; // -> false
```

### `>`, `<`, `>=`, and `<=` - Comparison

These are the comparison operators that can be used to see whether values are greater than, less than, greater than or equal, or less than or equal. Be default, this only works with numbers.
```kotlin
9 > 5; // -> true
9 < 5; // -> false
5 >= 5; // -> true
6.5 <= 6.2; // -> false 
```

### `&` - Bitwise AND

This is the bitwise and operator, this works on both booleans and numbers. On booleans it acts similar to the `&&` operator but does not short circuit. On numbers, it compares the bits, here is an example of `420 & 255`
```
110100100 <- 420
011111111 <- 255
--------- &
010100100 <- 164
```
It compares the bits in each position with each other and will only return 1 if both bits in both numbers are 1 in that position.

### `|` - Bitwise OR

This is the bitwise or operator, this works on both booleans and numbers. On booleans it acts similar to the `|` operator but does not short circuit. On numbers, it compares the bits, here is an example of `240 | 14`
```
11110000 <- 240
00001110 <- 14
-------- |
11111110 <- 254
```
It compares the bits in each position with each other and will return 1 if either of the bits at that position is 1.

### `~` - Bitwise XOR

This is the bitwise exclusive or operator, this is the same operator that is used for the boolean XOR previously mentioned, but this can also be used to manipulate bits. Here is an example: `165 ~ 170`
```
10100101 <- 165
10101010 <- 170
-------- ~
00001111 <- 15
```
It compares the bits in each position with eachother and will only return 1 if only 1 of the bits is 1 and the other is 0.

### `>>` and `<<` - Bit shift right and Bit shift left

These are the bit shifting operators, these by default only work on numbers, they work by taking the bits of the number and shifting them left or right by a certain amount.
```kt
255 >> 2; // 11111111 -> 00111111 = 63
64 << 1; // 0100000 -> 10000000 = 128
```

## Scopes

Scopes are sections in your program that you define your code, scopes determine what variables, functions, and classes are accessible, by default the program runs in the global scope where everything is accessible to the rest of the program.

You are able to define scopes by using the `{` and `}`. For example:
```kotlin
// Global scope
{
    // Defined scope
}
```

Anything that is defined in a scope is only accessible to that scope and any scopes inside that scope:
```kotlin
// Global scope
// Anything here is accessible ANYWHERE in the program
// i is in the global scope
i = 10;
{
    print(i); // -> 10
    // j is in a sub scope and cannot be accessed
    // in any parent scope, in this case that would
    // be the global scope
    j = 20;
    {
        print(i); // -> 10
        print(j); // -> 20
        // Both i and j are accessible because this
        // scope has parents with both of these values
    }
}
print(j); 
// This would throw an Error because
// j is not defined in this scope

print(i); // -> 10
```

Assigning variables in scope also works similarly, if a variable is defined in the global scope, and you reassign that variable in a scope than the variables in the global scope will be modified.
```kotlin
i = 0;
{
    i = 10;
}
{
    i = i - 1;
}
print(i); // -> 9
```

## Conditional Statements

Conditional statements allow you to branch your code into different scopes based on a boolean. The keywords that are used for conditional statements are `if` and `else`. It works by evaluating an expression, if it evaluates to true then it will run the scope after the `if` statement, otherwise if there is an `else` after the if then it will run that scope instead:
```kotlin
if (true) {
    // This will always be run
    // since true is always true
}

if (false) {
    // This will never run
    // since false is always not true
} else {
    // This will always run
}
```

Here is a better example:
```kotlin
name = input("What is your name");
if (name == "Sensei") {
    print("Wow that's a very cool name!");
} else {
    print("That's a cool name but not as cool as Sensei!");
}
```

Shorthand syntax, this syntax applies to most statements that have a scope after it.
This allows you to not use the braces after a statement, but only allows you to have one statement inside of it:
```kotlin
// Skipping the braces
// for one statement
if (true) print("That was true");
else print("This is imposible");
```

This shorthand syntax allows use to easily chain these conditional statements to create `else if`:
```kotlin
if (false) {
    // Do something
} else if (true) {
    // Do something else
}

// This above is the same as writing:
if (false); // Do something
else {
    if (true) {
        // Do something else
    }
}
// You are just skipping the braces after else
// since if is only one statement
```

Long chains of `else if`s are not recommended, and instead you should take a look at the `switch` statement which has a much nicer syntax than:
```kotlin
name = input("Name?");
if (name == "Alex") {
    // Alex
} else if (name == "James") {
    // James
} else if (name == "Xavier") {
    // Xavier
} else if (name == "Jenny") {
    // Jenny
}
// ...
```

## Switch Statements

Switch statements allow you to match values with an input, switch statements are faster when comparing literals (String, Number, Boolean, Null) as they can be evaluated at compile time. Switch statements have cases and will match the input to a case, which will then run a scope accordingly. Switch statements cannot have duplicate literals, but can have expressions that are evaluated at run time (like functions):
```kotlin
name = input("Name?");
switch (name) {
    case "Alex" -> {
        // Alex
    }
    case "James" -> {
        // Alex
    }
    case "Xavier" -> {
        // Xavier
    }
    case "Jenny" -> {
        // Jenny
    }
}

switch (name) {
    case input("Name again?") -> {
        // name == input("Name again?")
    }
}
```

Switch statements also have the ability to have multiple values for each case, as well as having a default case which will be run if the input matches none of the cases.
```kotlin
name = input("Name?");
switch(name) {
    case "Alex", "Steve" -> {
        // name == "Alex" || name == "Steve"
    }
    case null -> {
        // name == null
    }
    default -> {
        // Name was not Alex or Steve and was not null
    }
}
```

## Loops

There are different ways of looping in Arucas, they all are similar, but some work better in certain applications.

### `while`

While loops are the simplest form of loops they work by checking a condition then running a section of code, after it has finished running it will return to the condition and check it again, the loop will end when the condition is evaluated to false or if a `break` statement is used inside a loop, but we will cover this later.

Here is a simple example of how you could make an infinite loop that will never end.
```kotlin
// The condition inside this while expression 
// is true, this means it will always be true
// and as a result this loop will never end
while (true) {
}
// This program will never end naturally
```

You can use a while loop to iterate over numbers, here is an example:
```kotlin
counter = 0;
// This will loop until counter >= 10
while (counter < 10) {
    counter++;
    // Increments the counter by 1
}
```

However, this way of iterating can lead to human errors, accidentally missing the increment of the counter would lead to the loop never ending, and so we would more commonly use a `for` loop. 

### `for`

The for keyword is used to define a for loop, similar to the C style loop. The for expression contains 3 sub expressions, the first is evaluated at the start of the loop only, the second is evaluated as the condition for the loop to continue similar to the while loop, and the last gets executed whenever the loop reaches the end.
```kotlin
// Usually define the initial variable in the first expression (i = 0)
// The condition in the second (i < 10)
// Thirdly the expression that gets run at the end of each loop (i++)
for (i = 0; i < 10; i++) {
}
```

Similarly to the while loop you can easily make an infinite for loop, the expressions in the for loop can remain empty allowing you to do something similar to the following:
```kotlin
// No first expression
// Condition is always true
// No final expression
for (; true;) {
}
```

A common use for `for` loops is iterating over the indexes of a list, we haven't covered lists in great detail just yet, but you are welcome to come back here once we have.
```kotlin
list = ["foo", "bar", "baz"];

// Remember indexes start at 0!
for (i = 0; i < len(list); i++) {
    item = list.get(i);
    print(item);
}
```

### `foreach`

Foreach is a developed version of the for loop allowing easier iteration of collections, this could be lists, sets, collectors, etc.
Similar to the previous example in the for loop, but you do not need to define an index to iterate over, you can just simply iterate over each item in the list.
```kotlin
list = ["foo", "bar", "baz"];

foreach (item : list) {
    print(item);
}
```

This is a much simpler way of iterating, something that you should keep in mind is that when iterating over maps, it iterates over the keys in the map, you can then use that to get the value if you wish:
```kotlin
map = {"foo": "oof", "bar": "rab", "baz": "zab"};

foreach (key : map) {
    value = map.get(key);
    print(key);   // -> foo, bar, baz
    print(value); // -> oof, rab, zab
}
```

### `break`

The `break` keyword allows you to break out of a loop at any point, and the loop will no longer be executed further, this cannot break out of nested loops only the most recent loop that you are inside. The break keyword works inside `while`, `for`, and `foreach` loops.
```kotlin
// Same iteration as shown before from 0-9
for (i = 0; i < 10; i++) {
    // If i is greater than 5 we stop and break the loop
    if (i > 5) {
        break;
    }
}
```

### `continue`

The `continue` keyword is similar to the break keyword in that it allows you to disrupt the flow of a loop. Unlike the break keyword however this doesn't terminate the loop, instead it stops the loop and returns it back to the beginning, this works with `while`, `for`, and `foreach` loops.
```kotlin
for (i = 0; i < 10; i++) {
    if (i == 6) {
        // We go back to the start of the loop
        // the final statement in the for loop
        // still gets executed so i increments
        continue;
    }
    print(i); // 0, 1, 2, 3, 4, 5, 7, 8, 9 <- no 6
}
```

### Recursion

Recursion is a type of loop or iteration that works when a function calls itself causing a chain effect, usually the function has a condition where it does not call itself and exits, usually recursion is slower than the other traditional loops and is more unsafe as it can lead to a possibility of the stack overflowing which will lead to it throwing an error.
```kotlin
// This function is unsafe and will result in a stack overflow
fun recurse() {
    // Calls itself
    recurse();
}
```

A more safe approach if you must use recursion is to have a counter that lets the function know how deep it is:
```kotlin
fun recurse(depth) {
    if (depth > 10) {
        print("Depth of 10, stopping...");
        return;
    }
    // Increase the depth ever time we recurse
    recurse(depth + 1);
}
// This is now safe to call, it will only call itself
// Until it hits the depth limit
recurse(0);
```

## Iterable and Iterator

It may be the case that you would like to create your own class that is functional with `foreach`, this is possible by extending the `Iterable` class and implementing the `iterator()` method in your class. This must return an `Iterator` which will be used to iterate in the `foreach`:

```kotlin
class ExampleIterator(): Iterator {
	var exampleValue = 0;

	ExampleIterator(): super();

	// Overriden
	fun hasNext() {
		return exampleValue < 10;
	}

	// Overriden
	fun next() {
		return exampleValue++;
	}
}

class ExampleIterable: Iterable {
	fun iterator() {
		return new ExampleIterator();
	}
}
```




## Functions

Functions are a great abstraction that we use to hide complexity and easily reuse code, functions allow you to write code that can be executed from elsewhere in your program by referencing the function's name, or identifier. 

### Simple Functions

Functions in Arucas are defined with the `fun` keyword followed by an identifier then brackets which contain your parameters for the function we will cover this more in a moment, then it is followed by some statements in a scope, here is an example:
```kotlin
fun exampleFunction() {
    print("Function was called");
}

// To call a function we use the name of the
// function then brackets to call it
// similar to how we use the print function
exampleFunction(); // prints "Function was called"
```

Now, if we wanted to add some parameters:
```kotlin
// The parameter is a variable that you can
// use inside of your function, in this case
// we take in a name then use it to print a statement
fun anotherFunction(name) {
    print("Your name is " + name + "!");
}

// To call the function with a parameter we 
// just need to do the same as before but
// include what we want to pass into the function
// as the name variable
anotherFunction("sensei"); // prints "Your name is sensei!"
```

You can add more parameters by adding commas and listing all the parameters you wish to take in:
```kotlin
// Parameter names must be different
// so you can differentiate between them in the function
fun moreFunction(number1, number2) {
    // We take both numbers and print the sum of them
    print(number1 + number2);
}

// To call the function we put the parameters
// we want to pass in separated by commas in the
// same order that the function has them
// in this case number1 = 9, number2 = 10
moreFunction(9, 10); // prints 19
```

### Variable Parameters

You are also able to take in a variable amount of parameters, this means that you can call the function with as many parameters as you want, and it is passed into the function as a list. To do this, we define the parameter in the function followed by `...`. 
```kotlin
// The numbers parameter is always a list
// filled in with the parameters
variableParameters(numbers...) {
    total = 0;
    // Since numbers is a list we can iterate
    // over it using a foreach loop
    foreach (num : numbers) {
        total = total + num;
    }
    // This function adds up all the given numbers
    // and then outputs the total
    print(total);
}

variableParameters(1, 2, 3, 4, 9); // prints 19
// numbers = [1, 2, 3, 4, 9]

variableParameters(); // prints 0
// numbers = []

variableParameters(-9); // prints -9
// numbers = [-9]
```

Another important thing to know is that functions are first class objects, meaning that they are treated just like any other value. So you can store functions in variables and pass functions into other functions, which allows you to write more flexible code.
```kotlin
fun exampleFunction() {
    print("Example function was called!");
}

// We don't call the function just reference it by it's name
// so exampleFunction not exampleFunction()
variable = exampleFunction;

// Now since variable stores the exampleFunction function
// we can actually call variable as if it were a function
variable(); // prints "Example function was called!"
```

### Lambdas

You are also able to create anonymous functions or more frequently called lambdas, these functions can be defined on the go and cannot be called like normal functions since they do not exist with a name, but you can still call them through a variable like previously shown, you can define an anonymous function with the `fun` keyword and skipping the identifier and then brackets with the parameters followed by your statements in a scope.
```kotlin
lambda = fun() {
    print("This is a lambda");
};
lambda(); // prints "This is a lambda"
```

Here is a use when you need to pass a function into another function:
```kotlin
fun runFunctionWithDelay(delay, function) {
    // This pauses the program for an amount of milliseconds
    sleep(delay);
    // This calls the function
    function();
}

// In this case our lambda cannot have parameters
// because runFunctionWithDelay doesn't pass in any parameters
runFunctionWithDelay(100, fun() {
    print("Printed after 100 milliseconds");
});
```

### Return Statements

Functions have the ability to return values, functions technically always return values in fact, similar to how the `input` function works which we looked at earlier that returns whatever the user inputted into the console. Return statements can be used inside of functions to return a value, you can do this by using the `return` keyword followed by a value, or alternatively if you don't want to return a value you can just leave it blank, if you leave it blank then the function will by default just return `null`, then follow that by a semicolon:
```kotlin
fun biggerThanFive(num) {
    if (num > 5) {
        return "Bigger than 5";
    }
    return "Smaller than 5";
}

// We need to assign the return value to something
biggerThan5 = biggerThanFive(20); // -> "Bigger than 5"

print(biggerThan5); // prints "Bigger than 5"

// We can also ignore the return value if we don't want it
biggerThanFive(4);
// In this case ignoring the return value is pointless but
// this may be the case of other functions that run code
```

Here is an example where `return` is used to escape a function to stop its execution, return does not always need to return a value and this is a very common use case which can reduce your indentation level which can make your code cleaner and easier to read overall. 
```kotlin
fun printNameIfOver18(name, age) {
    if (age < 18) {
        // Under 18 so we don't want to
        // execute any more of this function
        // so we just return out of the function
        return;
    }
    print(name);
}

// Another way of writing the previous function
fun printNameIfOver18(name, age) {
    // In this case the indentation doesn't matter
    // but when you have long chains it can make a 
    // big difference and so you might want to return
    if (age >= 18) {
        print(name);
    }
}
```

### Function Scoping

Functions capture variables in the previous scopes to be able to use them in the scope, this lets you use variables from previous scopes:
```kotlin
{
    // Inner scope
    inner = "Random String";
    fun doSomething() {
        print(inner);
    }
    
    doSomething();
}
```

However, functions also have a special property where since they capture the whole previous scope that can access variables that are not yet defined:
```kotlin
{
    fun doSomething() {
        // Technically this variable doesn't exist yet
        print(postFunctionVariable);
    }

    // Now it exists
    postFunctionVariable = 10;

    // Now we call the function
    doSomething();
}
```

However, if you call the function before you define the variable the program will throw an error:
```kotlin
{
    fun doSomething() {
        // Technically this variable doesn't exist yet
        print(postFunctionVariable);
    }

    // Now we call the function
    doSomething();
    // This will crash because postFunctionVariable is not defined yet!

    // Now it exists
    postFunctionVariable = 10;
}
```

Here is an example that may seem confusing at first, but understanding how scoping of functions works helps:
```kotlin
fun generatePrintFunction(stringToPrint) {
    function = fun() {
        // The stringToPrint variable is captured here
        // and saved for later in this function
        print(stringToPrint);
    };
    return function;
}

printFunction = generatePrintFunction("FOO");
// printFunction now contains a lambda that prints a string
// the lambda has the variable "FOO" saved in it that it will use
// when we call the printFunction

printFunction(); // prints "FOO"
```

Another example where the variable changed between function calls:
```kotlin
variable = 10;

fun printSomething() {
	print(variable);
}

printSomething(); // prints 10

variable = 20;
printSomething(); // prints 20
```

### Overloading Functions

Overloading functions is the ability to have functions that have a different number of parameters defined separately and not interfere with each other, overloading functions are possible inside of classes which we will cover later, but defining functions in the scope you cannot overload them, one will simply overwrite the other. This may be possible at a later date, at which point this section of the documentation will be updated.

Here is an example:
```kotlin
fun exampleFunction() {
    print("ExampleFunction");
}

exampleFunction(); // prints "ExampleFunction"

// Define another function with name exampleFunction
// with different amount of parameters
fun exampleFunction(parameter) {
    print("ExampleFunctionWithParam");
}

exampleFunction(null); // prints "ExampleFunctionWithParam"

exampleFunction(); // throws an error since original function was overwritten
```  

This however is not the case with built-in functions, since they are implemented internally they work slightly different and as such they can be overloaded, all information on built in overloads will be documented separately, see the next section on Built-In Functions.

### Delegating Functions

Delegating a function is when instead of calling the function you use it as if it were an object and store the action function as a value much like a lambda:
```kotlin
fun delegateExample() {
	print("called");
}

del = delegateExample; // We didn't call the function
// Since 'del' now has a function stored in it we can call it
del(); // prints 'called'
```

Previously, before Arucas 2.0.0 you could not delegate a function that was overloaded however in Arucas 2.0.0+ we can now do that too.
```kotlin
fun overload() {
	print("0");
}

fun overload(arg) {
	print("1");
}

fun overload(arg1, arg2) {
	print("2");
}

delegate = overload;
// Delegate does not store the 'overload' function but
// instead stores the information to be able to call
// the 'overload' function at a later point

// This means we can do this:
delegate(); // prints '0'
delegate(0); // prints '1'
delegate(0, 0); // prints '2'

// However if you give incorrect number of args:
delegate(0, 0, 0); // Error
```

### Built-In Functions

Throughout this documentation I have been using built-in functions, these functions are implemented natively in the language that allow you to do the basic functions, some examples that I have been using are `print`, `input`, and `sleep`.
There is a list of these basic functions and these provide some key functionality to the language, so you should review them, and they can be found on the separate page [here](https://github.com/senseiwells/Arucas/blob/main/docs/Extensions.md). 

## Members

Members are part of values, they can either be functions or fields. They allow you to interact and use part of the values. These are usually accessed through the `.` operator, followed by the field or function name (and brackets if you are calling the function). Classes can also have members, these are known as static members, static members are not based on an object but instead on the class definition itself.
```kotlin
hello = "hElLo";
// Lowercase is a method of <String>, and
// when it gets called it returns a complete
// lowercase representation of the string
print(hello.lowercase());

// type is a static field of the String class
// this is the type that represents the class
print(String.type);
```

Usually each class has its own static members and each value has its own members too, these are documented on a separate page, and you can find that [here](https://github.com/senseiwells/Arucas/blob/main/docs/Classes.md).

## Lists

Lists are a form of collection, they are the key data structure in the language, they allow you to store multiple values inside of one value, lists in Arucas are dynamic meaning that they do not have a fixed size you are also able to put any type of value in a list. Lists are an ordered data structure, meaning their order stays consistent with how you input values into the list.

### Simple Lists

Lists are very simple to use, as mentioned earlier in the documentation they can be declared with the List literal `[]`:
```kotlin
// Creating an empty list
list = [];
```

You are also able to put values inside the square brackets to declare a list with items in them.
```kotlin
// List with values 1, 2, "string"
list = [1, 2, "string"];
```

We can get the number of values inside a list by using a built-in function: `len`:
```kotlin
print(len([true, false, null])); // -> 3

print(len([])); // -> 0
```

### Using Lists

An important concept is understanding indexes of lists, each value as an index in a list with the first value in the list having an index of 0 and then incrementing by one until the last value. We can then use this to access values in the list.
```kotlin
list = ["first", "second", "third"];

// Index 0 corralates to the first index
print(list.get(0)); // -> "first"

// A short hand for accessing lists was introduced in 1.2.0
// We can use the [] operator to access an index in the list
print(list[1]); // -> "second"
```
Something to note is that if an index is provided that is outside the bounds of the list, then an error will be thrown.

To manipulate the contents of the list, we can take a look at the available methods:
```kotlin
list = [1, 2, 3];

// append method adds a value **to the end** of the list
list.append(4);

// insert method adds a value at a specific index in the list
list.insert(0, 0); // list = [0, 1, 2, 3]

// remove methods removes a value at a specific index
list.remove(3); // list = [0, 1, 2]

// set method sets the value at a specific index
list.set("zero", 0); // list = ["zero", 1, 2]

// A short hand for assigning indexes of lists was introduced in 1.2.0
// We can again use the [] operator to assign an index in the list
list[1] = "one"; // list = ["zero", "one", 2]
```

### List Unpacking

Lists provide special functionality in Arucas as they provide the ability to unpack them. This means you are able to extract all the variables in the list into variables. Here is an example:
```kotlin
// position with list having x, y, and z coordinates
position = [100, 50, -900];

// If we wanted to extract those we could do this:
x = position.get(0);
y = position.get(1);
z = position.get(2);

// Or we can use a shorthand: list unpacking
x, y, z = position; // x = 100, y = 50, z = -900

// To be able to do this the number of variables
// you are assigning must be equal to the length
// of the list, otherwise it will throw an error
```

Changing the values in the variables will not change the values in the list:
```kotlin
position = [100, 50, 200];
print(position); // -> [100, 50, 200]

x, y, z = position; // x = 100, y = 50, z = 200

x = 10;
// We now set x to 10 but this does not change
// the value in the list that x was assigned first

print(position); // -> [100, 50, 200]
```

## Maps

Maps like lists are a form of collection, maps can be seen as similar to lists, but instead of using indexes to access a value maps allow you to define a specific key value to access certain values. Maps have a literal, and by default these will be ordered based on the order they are inputted. Maps also have no fixed size.

### Simple Maps

Maps have a literal: `{}` which can be used to create maps. In the literal, you must declare a key and a value which are separated by a colon:
```kotlin
map = {"key": "value"};

// We can also declare an empty map
map = {};
```
Any types can be used for keys or values, for custom classes you need to ensure they have an appropriate hashing function, however we will cover this later in the classes section.

To define multiple key value pairs we just separate them with a comma, this is usually done over multiple lines:
```kotlin
map = {
    "key": "value",
    "otherKey": "value",
    "foo": "bar"
};
```

Similarly to Lists we can get the length of the map by using `len`, this returns the number of key value pairs:
```kotlin
len({"a": "A", "b": "B"}); // -> 2

len({}); // -> 0
```

### Using Maps

Maps are very similar to lists except using specific keys to access and assign values, here are some examples:
```kotlin
map = {
    "one": 1,
    "two": 2,
    "three": 3
};

// get method allows us to get a value using a key
print(map.get("one")); // -> 1
// Similarly to lists maps also allow the short hand by

// using the bracket operator
print(map["two"]); // -> 2

// put method allows us to add a key value pair
// this will replace any previous value with the given key
map.put("four", 4); // map = {"one": 1, "two": 2, "three": 3, "four": 4}

// Here is an example of it replacing an existing key
map.put("four", 0); // map = {"one": 1, "two": 2, "three": 3, "four": 0}

// Similar to list we can also set keys by using the bracket operator
map["four"] = 4; // map = {"one": 1, "two": 2, "three": 3, "four": 4}

// remove method removes a key and value from the map
map.remove("one"); // map = {"two": 2, "three": 3, "four": 4}
```

Some other useful methods of the map are those that get all the keys and values:
```kotlin
map = {
    "one": 1,
    "two": 2,
    "three": 3
};

// These methods return lists containing keys and values
keys = map.getKeys(); // keys = ["one", "two", "three"]
values = map.getValues(); // values = [1, 2, 3]
```

Another useful thing to note is that when using the `foreach` loop maps will loop over their keys, inside the loop you can the use it to access the value:
```kotlin
map = {
    "one": 1,
    "two": 2,
    "three": 3
};

foreach (key : map) {
    value = map[key];
    // Do something
}
```

### Sets

Sets are another form of collection. A set is basically just a map that doesn't have values, this allows for a list-like collection, however you cannot access the values using an index and cannot have duplicate values in the set. Sets have the benefit that they are faster to search than lists, an example of this will be shown in the section.

Unlike Maps and Lists, Sets do not have a literal form and so you will need to use the `Set` class to create a set, we do this by using the `of` method that can take an arbitrary amount of parameters:
```kotlin
// Empty set
Set.of();

Set.of(1, 2, 3); // -> <1, 2, 3>
```

As previously mentioned sets have the benefit of a fast searching algorithm, here is an example:
```kotlin
validNames = Set.of("Foo", "Bar", "Baz");

name = "Some Example Name";

// contains method is much faster than list
if (validNames.contains(name)) {
    print("You are valid");
}
```

## Errors

Errors are used in a program to indicate that something has gone wrong. Error's allow for a more complex control flow as when an error is thrown it propagates directly out of function calls until the error is either caught or it stops the program.

### Creation

By default there is only one `Error` class which is built-in. This class can be instantiated and can be thrown. To create an error we can simply just call the `Error` class's constructor:

```java
new Error();
```

This creates an empty error, to create an error with a message that will display on the stacktrace we can add a parameter:

```java
new Error("Something went wrong");
```

And lastly if we want to add a value to the error which we can use when the error is caught we can add a last parameter:

```java
new Error("Something went very wrong", [1, 2, 3]);
```

One last thing to mention is that the error class is extendable and when extending it will change the name of the error on the stacktrace:

```
Error: Something went wrong
> File: console, Line: 1, Column: 1, In: throwError::0 
1 | throwError();
  | ^ 
```

Compared to:

```
ChildClassError: Something went very wrong
> File: console, Line: 1, Column: 1, In: throwError::0 
1 | throwChildClassError();
  | ^ 
```

### Throwing 

To throw an error we simply just use the `throw` keyword. You can **only** throw objects that are of the `Error` type, this includes any child classes. If you attempt to throw a non-error type then an error will be thrown:

```kotlin
error = new Error("Something went wrong");
throw error;
```

### Catching

As previously mentioned it's possible to catch propagating errors. This can be done with the `try catch` syntax, the `catch` must be followed by braces with a 'parameter' which will reference the error that has been caught. Any errors that happen inside a `try catch` may be caught:

```kotlin
try {
	throw new Error();
} catch (e) {
	// Ignore
}
```

You are also able to specify the type of error that you would like to catch by type hinting the parameter:

```kotlin
class CustomError: Error {
	CustomError(): super();
}

try {
	throw new CustomError();
} catch (e: CustomError) {
	print("CustomError caught");
}
```

By doing this any other errors that are not an instance of `CustomError` would be ignored by the catch.

### Finally

Finally is a useful keyword, it allows for code to be executed if an error is thrown like a catch but unlike catch does not actually catch the error. This is especially useful if you need to reset or close something after running a try statement:

```kotlin
fun something() {
	// May throw error here
}

state = false;

try {
	state = true;
	something();
} finally {
	// Reset
	state = false;
}
```

Using finally is essentially the equivalent to:

```kotlin
try {
	// ...
} catch (e) {
	// Finally code here
	
	// Re-throw the error
	throw e;
}
```

In the case that an error is not thrown the code in the finally clause will simply just execute after the try block has executed.

Something to also note is that the `catch` and `finally` keywords can be used in conjunction with eachother:

```kotlin
try {
	// ...
} catch (e) {
	// ...
} finally {
	// ...
}
```

## Imports

Importing is a large part of almost every programming language. Importing is used to use code from other libraries so you do not need to write it yourself.

Importing in Arucas is made as simple as possible, by default you will be able to import any libraries that are already built-in. For example the `Json` class from the `util.Json` module. You can import using the `import` and `from` keywords:

```kotlin
import Json from util.Json;
```

You can import specific classes from any module, and you can import multiple classes by separating them with a comma:

```kotlin
import A, B, C from abc.ABC;
```

Further if you want to import a lot of classes from a module you can instead use a `*` to indicate that you want to import all of the classes:

```kotlin
// In this case it'll only import Json as it's the only
// Class in the module but in other cases it'll import multiple
import * from util.Json;
```

Generally it's better to import specific classes only as it will prevent class name conflicts. And further it helps while running your code; this is because imports in Arucas are lazy. This means that imports are not evaluated immediately but only evaluated when needed. 

If you reference a class that doesn't already exist in the scope then the interpreter tries to find that class in any of the imports you have, if you do not specify the class names in the import then the interpreter is forced to import everything.

The reason imports are done this way is to allow for cyclical imports:

```kotlin
// File A.arucas

import ClassB from B;

class ClassA {
	static fun doSomething() {
		// ClassB is only imported once we get here
		B.doSomething();
	}
}
```

```kotlin
// File B.arucas

import ClassA from A;

class ClassB {
	static fun doSomething() {
		print("B does something!");
	}
}

// ClassA is only imported once we get here
ClassA.doSomething(); // prints 'B does something'
```

One important thing to note is that you can only import classes from different files and you cannot directly import global variables or functions, although you can simply just use static variables or static functions to achieve the same behaviour.

## Libraries

The ability to import other classes would be quite useless to only import built-in classes so Arucas allows you to import classes from a library repository (which can be found [here](https://github.com/senseiwells/ArucasLibraries)). To import these you just simply import any class from the given module, the libraries will be automatically downloaded given you have an internet connection.

For example if I wanted to import [`ImmutableList`](https://github.com/senseiwells/ArucasLibraries/blob/master/libs/util/Collections.arucas#L464-L518):

```kotlin
import ImmutableList from util.Collections;
```

If you have made a library you are welcome to create a pull request to submit the library so that other users can use your code!

## Local

Libraries by default are stored in `C:/Users/<user>/.arucas/libs`, this may differ if you are using Arucas embedded in another application. This folder also contains stubs for the built-in classes and built-in functions with their documentation.

Arucas will automatically update any libraries when you import them, if for some reason you would prefer for this not to happen you can use the `local` keyword to prevent Arucas from checking the repository for updates:

```kotlin
import local ImmutableList from util.Collections;
```

Similarly you can use this to keep local dependancies. You can leave your local dependancy in the `libs` folder and by using the `local` keyword Arucas will skip checking the repository speeding up your import.

## Classes

Classes in Arucas allow for abstraction, they provide a way to encapsulate values into another value, and classes let you define certain behaviour with the value such as interactions with operators and the methods that the value has.

### Syntax

The class syntax is very simple and similar to many other languages, we use the `class` keyword to declare a class definition followed by the name of the class and then a series of class statements which we will cover further on in this section.
```kotlin
// Classes should follow Pascal Naming
class ExampleClass {
}
```

### Constructors

Constructors are essentially functions that are run when the class is instantiated. These are often used to set fields and often take parameters. By default, if no constructor is declared then a synthetic constructor is created: you will be able to construct the class without any parameters. However, if any constructors are defined this synthetic constructor will not be available.

To define a constructor in a class, we use the class name followed by brackets which can contain parameters and then followed by a statement which is run when the class is instantiated.
```kotlin
class ExampleClass {
    // Constructor
    ExampleClass() {
    }
}
```

Since Arucas 2.0.0 you can also call an overloaded constructor inside another constructor. This can be done by calling `this` after your constructor definition:
```kotlin
class ExampleClass {
	ExampleClass(number) {
	}
	// Here we call this() constructor
	// with a parameter of 10
	ExampleClass(): this(10) {
	}
}
```

When you do this the referenced constructor will always be executed first.

### Fields

Fields are essentially variables that are stored in a class and can be accessed by that class at anytime. As of writing this documentation all fields are public and mutable meaning that anyone can access and modify the value of a field.

Fields are defined in the class body using the `var` keyword and can also optionally be type hinted to enforce only specific types to be allowed in the field.
```kotlin
class Example {
	var exampleField;
	var typedField: String;
}
```

An issue with the current version is that typed fields will always be null until they are assigned a value, this essentially means that you you cannot have a typed field without it accepting `Null` or having it assigned when created. Which brings me onto the next point, fields can be initialised in the body, the expression will be re-evaluated everytime a class is created:
```kotlin
class Example {
	var exampleField = "initialised";
}
```

Fields are what allows for encapsulation since you can have as many fields as you want in a class:
```kotlin
class Person {
	var name;
	var age;
	var height;
	var gender;
	// ...
}
```

### Methods

Methods are just functions that belong to a class. Methods are defined like functions but instead they are declated inside the class body.
```kotlin
class Example {
	fun sayHello() {
		print("hello!");
	}
}
```

To use a method you need an instance of the class to call it.
```kotlin
e = new Example(); // Instance of the Example class
e.sayHello(); // prints 'hello!'
```

Methods are special in the fact that they implicitly pass the calling object into the method allowing you to access fields and other methods from within that method, you can reference this calling object with the keyword `this`.
```kotlin
class Example {
	var string;
	Example(string) {
		// this references this object
		// you are setting the field 'string'
		this.string = string;
	}
	fun say() {
		// We access the 'string'
		// field on this
		print(this.string);
	}
}
```

This may seem confusing, what does `this` mean? Well internally it works the same as a regular function, `this` is just a parameter that is passed in implicitly:
```kotlin
// this is a keyword thus this example
// would fail to compile, but just an example
fun say(this) {
	print(this.string);
}
class Example {
	var string;
	Example(string) {
		this.string = string;
	}
}
e = new Example("foo");
say(e); // prints 'foo'
```

Other than that methods work exactly the same as functions which you can read about [here](https://github.com/senseiwells/Arucas/blob/main/docs/Language%20Documentation/14.%20Functions.md).

### Operators

Arucas allows you to declare how operations should work between classes. You defined operations much like methods but instead of using `fun` you use the `operator` keyword and instead of being followed by a name you follow it with the operator you want to override:
```kotlin
class Example {
	var number = 10;
	operator + (other: Example) {
		return this.number + other.number;
	}
}
e1 = new Example();
e1.number = 22;
e2 = new Example();
print(e1 + e2); // prints 32
```

The parameters in the operator are also significant since you can override both unary and binary operators (and technically a ternary).

Here is a table of all the overridable operators:

#### Unary (no parameters):
Name | Operator | Example
-|-|-
NOT | `!` | `!false`
PLUS | `+` | `+10`
MINUS | `-` | `-10`

#### Binary (one parameter):
Name | Operator | Example
-|-|-
PLUS | `+` | `1 + 1`
MINUS | `-` | `1 - 1`
MULTIPLY | `*` | `2 * 2`
DIVIDE | `/` | `4 / 2`
POWER | `^` | `2 ^ 2`
LESS_THAN | `<` | `55 < 90`
LESS_THAN_EQUAL | `<=` | `43 <= 10`
MORE_THAN | `>` | `66 > 22`
MORE_THAN_EQUAL | `>=` | `78 >= 0`
EQUAL | `==` | `6 == 6`
NOT_EQUAL | `!=` | `"wow" != "foo"`
AND | `&&` | `true && false`
OR | `\|\|` | `false \|\| true`
XOR | `~` | `true ~ false`
SHIFT_LEFT | `<<` | `2 << 1`
SHIFT_RIGHT | `>>` | `2 >> 1`
BIT_AND | `&` | `56 & 7`
BIT_OR | `\|` | `92 \| 45`
SQUARE_BRACKET | `[]` | `[8, 9, 10][2]`

#### Ternary (two parameters)
Name | Operator | Example
-|-|-
SQUARE_BRACKET | `[]` | `[8, 9, 10][2] = 11`

### Static Methods and Fields

Static methods and fields work very much like the global scope, you can define variables and functions in a class that do not need an instance of the class to be called. The reason you may want to put your methods in a class as static instead of the global scope is because it stops cluttering the global scope and more importantly allows other script to be able to use your function; as the global scope cannot be imported, only classes can thus static methods can be used whereas global functions cannot.

Defining a static field or method is extremely easy, it is the same as a regular method or field but instead has the `static` keywork before it.
```kotlin
class Example {
	static fun staticMethod() {
		print("Called static method");
	}
}
```

And to call a static method you just use the class name followed by the dot operator then the method name and any arguments:
```kotlin
Example.staticMethod(); // prints 'Called static method'
```

Static methods do not have access to `this` because there is no class instance to access.

### Inheritance

Inheritance was introduced in Arucas 2.0.0. It allows for your classes to inherit methods and fields from a parent class. Arucas does not support multi-inheritance however does support interface inheritance.

To inherit a class you simply add a colon after your class name and then follow that with the name of the class you wish to inherit from.
```kotlin
class Parent {
}
class Child: Parent {
}
```

When inheriting from a parent class (or superclass) you must initialise the parents constructor, this is enforced to avoid unexpected behaviours, this is required even if the parent has a default constructor or a constructor with no parameters. You can do this by adding a colon after your constructor and calling `super`.

```kotlin
class Parent {
	Parent() {
		print("Constructing parent!");
	}
}
class BadChild: Parent {
	// This will throw an error because
	// the child class is not initialising
	// the parents constructor
	BadChild() {
	}
}
// Assuming it were to compile
new BadChild(); // this would print nothing
class GoodChild: Parent {
	GoodChild(): super() {
	}
}
new GoodChild(); // This will print 'Constructing parent!'
```

Another thing to note is that the child class does not need to directly call the super constructor, as long as it is called at some point it is allowed. For example you can call an overloaded constructor that eventually calls the super constructor:
```kotlin
class Parent {
	Parent() {
		print("Constructing parent!");
	}
}
class Child: Parent {
	// This calls super
	Child(): super() {
	}
	// This class constructor with no args
	Child(argument): this() {
	}
}
new Child(); // prints 'Constructing parent!'
```

As mentioned child classes inherit methods and fields from their parent classes:
```kotlin
class Parent {
	var foo;
	Parent(bar) {
		this.foo = bar;
	}
	fun printFoo() {
		print(this.foo);
	}
}
class Child: Parent {
	Child(bar): super(bar);
	fun getFoo() {
		return this.foo;
	}
}
c = new Child("foo");
c.getFoo(); // -> 'foo'
c.printFoo(); // prints 'foo'
c.foo; // -> 'foo'
```

As well as inheriting child classes can override methods.
```kotlin
class Parent {
	var foo;
	Parent(bar) {
		this.foo = bar;
	}
	fun something() {
		print("Parent something!");
	}
	fun callSomething() {
		this.something();
	}
}
class Child: Parent {
	Child(): super("foo!");
	fun something() {
		print("Child something!");
	}
}
c = new Child();
p = new Parent("bar");
c.callSomething(); // prints 'Child something!'
p.callSomething(); // prints 'Parent something!'
```

Even if a child overrides a parents method or operator it can still access it by using the `super` keyword, this just calls the method belonging to the parent.
```kotlin
class Parent {
	operator + (other) {
		return -1;
	}
	fun getSomething() {
		return "Parent";
	}
}
class Child: Parent {
	Child(): super();
	operator + (other) {
		if (other == 3) {
			return this.getSomething();
		}
		if (other == 4) {
			return super.getSomething();
		}
		return super + other;
	}
	fun getSomething() {
		return "Child";
	}
}
c = new Child();
c + 3; // -> "Child"
c + 4; // -> "Parent"
c + 0; // -> -1
```

An interesting thing to also note is how types work with inheritance. using the static method `Type.of(<Object>)` gets the exact type of the object:
```kotlin
class Parent {
	fun getTypeName() {
		return Type.of(this).getName();
	}
}
class Child: Parent {
	Child(): super();
}
new Parent().getTypeName(); // "Parent"
new Child().getTypeName(); // "Child"
```

This is because the `this` reference inside of the `Parent` class is of a type that is or a child class of `Parent` and since `Type.of(<Object>)` returns the exact type it will always be that of `Parent` or a child class of itself.

It is also worthy to note that you are able to extend some built-in classes if they allow it. For example you are permitted to extend the `Function` class:
```kotlin
class Example: Function {
	Example(): super();
	// This is the method that gets called
	// when you call a function with '()'
	fun invoke() {
		print("hi");
	}
	// You can also define
	// invoke with multiple parameters
	fun invoke(arg1, arg2) {
		print("hi two!");
	}
}
// So you can do stuff like this:
e = new Example();
e(); // Regular 'invoke' with no arguments, prints 'hi'
e(0, 0); // 'invoke' with 2 arguments, prints 'hi two!'
```

### Interfaces

While interfaces are still technically inheritance I split it up since the last section is quite big. What interfaces allow you to do is make a blueprint for a class, and if a class decides to implement an interface it **must** implement all the functions that were specified in an interface.

A class can implement multiple interfaces and if the methods are not implemented an error will be thrown. This is useful to be able to ensure that the values you pass around have specific methods. The requirements for a method to be implemented is for it to have the same name and same number of parameters. Like regular overriding of methods this does not force you to inherit the types however it is good practice to do so.
```kotlin
interface Addable {
	fun add(other);
}
class NoAdd {
}
class Add: Addable {
	fun add(other) {
		return 10;
	}
}
// We specifically tell this function that
// the first parameter must be of the type
// Addable, this ensures that we have a method
// 'add' that we can call.
fun addAny(first: Addable, second) {
	first.add(second);
}
addAny(new Add(), "foobar"); // -> 10
addAny(new NoAdd(), "foobar"); // Error
```

Interfaces can work alongside class inheritance and as mentioned you can implement as many interfaces as needed. Interfaces are also permitted to be implemented on enums.
```kotlin
// Kinda pointless to have in interface
// with nothing in it, but this is an example
interface A { }
interface B { }
class Parent { }
// This doesn't need to be in a specific order
class Child: Parent, A, B {
	Child(): super();
}
```

## Enums

Enums provide a nice way to program constants. This is done by using an enum class which can be declared using the `enum` keyword, much like a regular class enums can have defined methods and fields.

### Syntax

The syntax to declare an enum class is very simple, just the `enum` keyword followed by the enum name, then inside your backets you can define your constants separated by commas:

```kotlin
enum Direction {
	NORTH, EAST, SOUTH, WEST
}
```

These can then just be simply accessed like a static field:

```kotlin
Direction.NORTH;
Direction.EAST;
Direction.SOUTH;
Direction.WEST;
```

### Constructors

## Threads

### Purpose

### Creation

### Stopping Threads

### Thread Safety

## Java Integration

If there are specific things you want to achieve that aren't possible with the base language, you may want to look into calling Java code from within your script. This is possibly by using the `util.Internal` library and importing the `Java` class.
```kotlin
import Java from util.Internal;
``` 

### Java Types

There are many static methods for the Java class, and these will be key for creating Java typed values. One such method is `valueOf`, this converts any Arucas typed value into a Java one:
```kotlin
import Java from util.Internal;

jString = Java.valueOf(""); // Arucas String type -> Java String type
```

All Java typed values have the Arucas type of `Java` and they all have some basic methods you can use, these allow you to access their methods and fields which we will explore later in this documentation. Another method which is important is the `toArucas` method, which tries to convert the Java typed value back into an Arucas typed value.
```kotlin
import Java from util.Internal;

jString = Java.valueOf(""); // Java String type

string = jString.toArucas(); // Back to Arucas String type
``` 

Not every Java type has a conversion and so if you try to convert a Java type that does not have a conversion it will simply just return itself.

### Methods and Fields

`Java` values have a property that allows them to call Java methods, there are different ways this can be done, but the advised way is to call the method as usual:
```kotlin
import Java from util.Internal;

jString = Java.valueOf("");
// Java methods return Java typed values too
// The isBlank method is a Java method!
jBoolean = jString.isBlank();

if (jBoolean.toArucas()) {
    print("String was blank");
}
```
You are also able to call methods with parameters the same way you would call an Arucas function, however the types of the values must match the method signature, the arguments you pass in should generally be Java typed.

Accessing fields is also similar to Arucas this can be done by just using the dot operator:
```kotlin
import Java from util.Internal;

array = Java.arrayOf();
// 'length' field of Java array type
array.length;
```

### Constructing Java Objects

Now this is great, but what if we want to construct a Java Object? Well we can get the Java class which we can then use to call a construtor. We can get the Java class with the `Java.classOf()` method and passing in the class name as a parameter:

```kotlin
import Java from util.Internal;

ArrayList = Java.classOf("java.util.ArrayList");

// We can then just construct the ArrayList object
// by calling the class. We do NOT need the new keyword
// here because we are not creating a new Arucas object.
jList = ArrayList();

// Adding Java Strings into ArrayList
jList.add("One"); 
jList.add("Two");
```

As mentioned before Arucas values can be converted to Java values, and you have the ability to construct Java classes, but there are still some cases where Java type values cannot be created. These are primitives, arrays, and lambdas. To remedy this, the Java class provides static methods to create these types of values:
```kotlin
import Java from util.Internal;

Java.intOf(10); // Creates Java int type
Java.floatOf(9.5); // Creates Java float type
Java.charOf("h"); // Creates Java char type
// ...

Java.arrayOf("wow", 7, false); // Creats Object[] with values, arbitrary arguments
Java.intArray(10); // Creats int[] with size passed in
Java.byteArray(10); // Creates byte[] with size passed in
// ...

// Runnables take no args and returns nothing
Java.runnableOf(fun() {
    print("runnable!");
});
// Consumables take 1 arg and returns nothing
Java.consumerOf(fun(arg) {
    print("consumer!: " + arg);
});
// Suppliers take no args and returns something
Java.supplierOf(fun() {
    print("supplier!");
    return false;
});
// Functions take 1 arg and returns something
Java.functionOf(fun(arg) {
    print("function!: " + arg);
    return true;
});
```

### Static Methods and Fields

Now we know how we can construct objects and call their methods in Java, what about static methods and fields? Well, similarly to how we constructed an object if we get the Java class we can simply just call the methods on this object:
```kotlin
import Java from util.Internal;

Integer = Java.classOf("java.lang.Integer");

// Method call...
Integer.parseInt("120");

// Field access...
Integer.MAX_VALUE;

// Field assignment...
// Obviously this won't work, but it's just an example
Integer.MAX_VALUE = Java.intOf(100);
```

