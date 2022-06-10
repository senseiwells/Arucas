# Documentation

This page is a step by step guide to get you started with Arucas, showing you how to install the language, and how to write code for the language, showing you what is possible in the language as well as its features.

This guide is for Arucas 1.2.0+

> #### [Installation ](#installation)
> #### [Development Environment ](#development-environment)

## Installation

This installation guide is for those who want to just run vanilla Arucas not bundled in with another application. If you are running Arucas inside another application you can just skip this part.

First you need to install the latest version of Arucas, you can download the jar file from [here](https://github.com/senseiwells/Arucas/releases). 
After downloading the jar make sure you have Java 16 or above installed as Arucas relies on this, you can then run the jar using the command line:
```
java -jar Arucas-1.2.0.jar -noformat
```
Now you will be running the Arucas Interpreter, here you can type any Arucas code and it will be run, if you want to exit the interpreter you can simply type:
```
exit
```
To run a file with Arucas code from the command line you can use the Built-in function:
```kotlin
run("path/of/arucas/file.arucas");
```
Alternatively you can do this directly in the command line to run a file:
```
path/of/arucas/file.arucas
```

## Development Environment

We recommend the use of the [Arucas Plugin](https://github.com/Kariaro/ArucasHighlighter/tree/main) designed for IntelliJ by [HardCoded](https://github.com/Kariaro), this highlights your code informing you of errors in your code, and adding nice colours :).

Alternatively if you do not wish to use IntelliJ another option is to use VSCode and set the language to `Java`, and disable validation for error highlighting. You can also configure VSCode to automatically recognise `.arucas` files as Java.

#

So now that you are able to run Arucas files what do we put inside? If you have not already you should take a look at the [Language Syntax]() page briefly but we will cover everything in detail here.

## Comments

Lets start by introducing comments, comments don't do anything in terms of running the code but instead they allow you to describe what is happening in the code, when the code is run all comments are completely ignored.

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

Numbers are very easy to create, you can simply just type them! Numbers can be easily modified and are an essential value, we will explore more about how to manilpulate numbers in the operators section.
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
These values are used to do boolean logic which we will cover in the operators section.

### Null literal

Null is as simple as it gets, there is only one literal for it:
```
null;
```
The `null` value represents nothing, it is used when a value doesn't exist, be careful will how it is used though, null safety is important, you don't want to get `null` values where you want other values.

### List literals

Lists are a more complex data structure and these allow you store many values of any type inside of it, including other lists. Lists in Arucas have a very simple syntax:
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
Maps are a fast way of storing data that needs to be accessed, again we will cover maps in greate detail later.

## Variables

A key part of programming is being able to manipulate data and we do this by using variables, you can think of a variable like a container. To define a variable we need to give it a name and then a value, and we can assign the variable with the value by using the assignment operator, `=`:
```kotlin
exampleVariable = "Example string";
```
Assigning a variable is like putting something inside the container.

Variable names can only include letters and underscores, by convention variable names should follow camel casing, this is where you capitalise all the words bar the first then squash them together.

Once you have defined a variable you can reassign the variable by again using the assignment operator.
```kotlin
exampleVariable = "Example string";
exampleVariable = "Overwritten!";
// exampleVariable now stores the value: "Overwritten"
```
Variables can store any type of value, we will come onto other types of values, for example numbers or booleans.

Now once you have stored a value in a variable you can use it by referencing the name of the variable, refering back to the previous analogy, this is like peeking into the container to see what is inside.
```kotlin
exampleVariable = "Example string";
print(exampleVariable);
// We would get an output of: Example string
```

## Output

Now we know how to create a string we can output it to the console. We can do this by using a function, we will cover functions in more detail later but for now we can just use it and accept that it works. The main function that you will use to output is called `print`, and to call the function we follow the name up with a pair of brackets:
```kotlin
print();
```
This won't actually print anything since we haven't told it what to print. We can provide this information by adding arguments inside our brackets:
```kotlin
// Having 1 parameter in the print function causes 
// it to automatically add a new line after it
print("Hello World!");
// This would print: Hello World\n
```
The `print` function also has the capability of concatenating (joining) strings togther
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

So now that we've got the function to prompt the user with input we need to store it, and we can to this by using a variable, like before how we stored literals inside a variable we can store what we call the return value of the function in a variable too:
```kotlin
userInput = input("What is your name? ");
```
Now that we have the user input stored in a variable we can use it inside our code:
```kotlin
userInput = input("What is your name? ");
// If the user inputted "mike"
print("Your name is: ", userInput, "\n");
// This should print Your name is: mike\n
```

## Operators

There are quite a few operators in Arucas but don't worry most of them are similar to other languages and are easy to pick up!

### `(` and `)` - Brackets

While not necessarily an operator I think that brackets are an important thing to mention before we talk about the other operators. Similar to what you might have learnt in maths brackets allow you to change the order of operations. We will cover where brackets may be useful in the following operators.

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
But what if we want to assign a value to a variable then manipulate that value to print:
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

The addition operator can also be used as a unary operator, this means that you can have it on the left side of a value with no other value on the left like this:
```kotlin
print(+10);
// This would print 10
```
This is pretty redundant but is to be consistent with the subtraction unary operator.

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
If you want addition to take precedence then you will need to use brackets:
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

Similar to multiplication division takes precedence over addition and subtraction.

### `^` - Exponent

This is the exponent operator and allows you to raise a base to a power, by default this only works with numners.
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

These are the increment and decrement operators, by default these only work on numbers, these are just syntactic sugar for making a value equal to one more or less than it's current value:
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
Internally Arucas compiles the first example into the second example. The increment and decrement are just a short hand.

### `.` - Dot

The dot operator is used to access and call members of a value, don't worry if you don't know what this means yet we will cover this in more detail. Every value has members by default and this is how you can interact with them.
```kotlin
value = "Example string";
value = value.uppercase();
// value now equals "EXAMPLE STRING"
```

### `&&` - AND

This is the and operator and by default is used between boolean values for boolean logic. Here is and example:
```kotlin
true && true; // -> true
true && false; // -> false
false && true; // -> false
false && false; // -> false
```
The and operator takes two boolean values and will only return `true` if both boolean values are `true` otherwise it will return `false`.

An important feature of this and operator is that it short circuits. Now to explain this you need to understand that the expressions are evaluated one at a time and it goes from left to right. If the left expression of the and operator is `false` then it knows that no matter whether the right hand side is `true` or fast it will always return `false` so it skips evaluating the right hand side.

If you want to use an and operator that evaluates both sides you can use the bitwise and operator `&`, we will go over this later.

### `||` - OR

This is the or operator and by default is used between boolean values for boolean logic. Here is an example:
```kotlin
true || true; // -> true
true || false; // -> true
false || true; // -> true
false || false; // -> false
```
The or operator takes two booleans and will only return `true` if at least one of the boolean values is `true`, otherwise it will return `false`.

Similarly to the and operator, this will short circuit, if the left hand side evaluates to `true` then it will always return `true` so it skips evaluating the right hand side.

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

This operator does not short circuit since it always needs to check both left and right hand side, this is the same operator that is used for the bitwise XOR, we will go over this later.

### `!` - NOT

This is the not operator and by default only can be used for booleans, this inverts the boolean, here is an exampe:
```kotlin
!true; // -> false
!false; // -> true
```
This takes the boolean and returns the opposite boolean value, unlike the other operators shown this a unary only operator, meaning it only has a value on the right hand side and not the left.

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

These are the comparison operators that can be used to see whether values are greater than, less than, greater than or equal, or less than or equal. Be default this only works with numbers.
```kotlin
9 > 5; // -> true
9 < 5; // -> false
5 >= 5; // -> true
6.5 <= 6.2; // -> false 
```

### `&` - Bitwise AND

This is the bitwise and operator, this works on both booleans and numbers. On booleans it acts similar to the `&&` operator but does not short circuit. On numbers it compares the bits, here is an example of `420 & 255`
```
110100100 <- 420
011111111 <- 255
--------- &
010100100 <- 164
```
It compares the bits in each position with eachother and will only return 1 if both bits in both numbers are 1 in that position.

### `|` - Bitwise OR

This is the bitwise or operator, this works on both booleans and numbers. On booleans it acts similar to the `|` operator but does not short circuit. On numbers it compares the bits, here is an example of `240 | 14`
```
11110000 <- 240
00001110 <- 14
-------- |
11111110 <- 254
```
It compares the bits in each position with eachother and will return 1 if either of the bits at that position is 1.

### `~` - Bitwise XOR

This is the bitwise exclusive or operator, this is the same operator that is used for the boolean xor previously mentioned, but this can also be used to manipulate bits. Here is an example: `165 ~ 170`
```
10100101 <- 165
10101010 <- 170
-------- ~
00001111 <- 15
```
It compares the bits in each position with eachother and will only return 1 if only 1 of the bits is 1 and the other is 0.

### `>>` and `<<` - Bitshift right and Bitshift left

These are the bitshifting operators, these by default only work on numbers, they work by taking the bits of the number and shifting them left or right by a certain amount.
```kt
255 >> 2; // 11111111 -> 00111111 = 63
64 << 1; // 0100000 -> 10000000 = 128
```

## Scopes

Scopes are sections in your program that you definte your code, scopes determine what variables, functions, and classes are accessible, by default the program runs in the global scope where everything is accessible to the rest of thr program.

You are able to define scopes by using the `{` and `}`. For example:
```kotlin
// Global scope
{
    // Defined scope
}
```

Anything that is defined in a scope is only accessible to that scope and any scopes inside of that scope:
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

Assigning variables in scope also works similarly, if a variable is defined in the global scope and you reassign that variable in a scope then the variables in the global scope will be modified.
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
}
else {
    // This will always run
}
```

Here is a better example:
```kotlin
name = input("What is your name");
if (name == "Sensei") {
    print("Wow that's a very cool name!");
}
else {
    print("That's a cool name but not as cool as Sensei!");
}
```

Short hand syntax, this syntax applies for most statements that have a scope after it.
This allows you to not use the braces after a statement but only allows you to have one statement inside of it:
```kotlin
// Skipping the braces
// for one statement
if (true) print("That was true");
else print("This is imposible");
```

This short hand syntax allows use to easily chain these conditional statements to create `else if`:
```kotlin
if (false) {
    // Do something
}
else if (true) {
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

Long chains of `else if`s are not recommended and instead you should take a look at the `switch` statement which has a much nicer syntax than:
```kotlin
name = input("Name?");
if (name == "Alex") {
    // Alex
}
else if (name == "James") {
    // James
}
else if (name == "Xavier") {
    // Xavier
}
else if (name == "Jenny") {
    // Jenny
}
// ...
```

## Switch Statements

Switch statements allows you to match values with an input, switch statements are faster when comparing literals (String, Number, Boolean, Null) as they can be evaluated at compile time. Switch statements have cases and will match the input to a case which will then run a scope accordingly. Switch statements cannot have duplicate literals but can have expressions that are evaluated at run time (like functions):
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

There are different ways of looping in Arucas, they all are similar but some work better in certain applications.

### `while`

While loops are the simplist form of loops they work by checking a condition then running a section of code, after it has finished running it will return to the condition and check it again, the loop will end when the condition is evaluated to false or if a `break` statement is used inside of a loop, but will will cover this later.

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

However this way of iterating can lead to human errors, accidentally missing the increment of the counter would lead to the loop never ending and so we would more commonly use a `for` loop. 

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

A common use for `for` loops is iterating over the indexes of a list, we haven't covered lists in great detail just yet but you are welcome to come back here once we have.
```kotlin
list = ["foo", "bar", "baz"];

// Remember indexes start at 0!
for (i = 0; i < len(list); i++) {
    item = list.get(i);
    print(item);
}
```

### `foreach`

For each is a developed version of the for loop allowing easier iteration of collections, this could be lists, sets, collectors, etc.
Similar to the previous example in the for loop but you do not need to define an index to iterate over, you can just simply iterate over each item in the list.
```kotlin
list = ["foo", "bar", "baz"];

foreach (item : list) {
    print(item);
}
```

This is a much simplier way of iterating, something that you should keep in mind is that when iterating over maps, it iterates over the keys in the map, you can then use that to get the value if you wish:
```kotlin
map = {"foo": "oof", "bar": "rab", "baz": "zab"};

foreach (key : map) {
    value = map.get(key);
    print(key);   // -> foo, bar, baz
    print(value); // -> oof, rab, zab
}
```

### `break`

The break keyword allows you to break out of a loop at any point, and the lopp will no longer be executed further, this cannot break out of nested loops only the most recent loop that you are inside. The break keyword works inside of `while`, `for`, and `foreach` loops.
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

The continue keyword is similar to the break keyword in that it allows you to disrupt the flow of a loop. Unlike the break keyword however this doesn't terminate the loop, instead it stops the loop and returns it back to the beginning, this works with `while`, `for`, and `foreach` loops.
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

## Functions

Functions are a great abstraction that we use to hide complexity and easily reuse code, functions allow you to write code that can be executed from elsewhere in your program by referencing the functions name, or identifier. 

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

Now if we wanted to add some parameters:
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

You are also able to take in a variable amount of parameters, this means that you can call the function with as many parameters as you want and it is passed into the function as a list. To do this we define the parameter in the function followed by `...`. 
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

Another important thing to know is that functions are first class objects, meaning that they are treated just like any other value. So you can store functions in variables and pass functions into other functions which allows you to write more flexible code.
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

You are also able to create anonymous functions or more frequently called lambdas, these functions can be defined on the go and cannot be called like normal functions since they do not exist with a name, but you can still call them through a variable like previously shown, you can definte an anonymous function with the `fun` keyword and skipping he identifier and then brackets with the parameters followed by your statements in a scope.
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

Functions have the ability to return values, functions technically always return values infact, similar to how the `input` function works which we looked at earlier that returns whatever the user inputted into the console. Return statements can be used inside of functions to return a value, you can do this by using the `return` keyword followed by a value, or alternatively if you don't want to return a value you can just leave it blank, if you leave it blank then the function will by default just return `null`, then follow that by a semicolon:
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

Here is an example where `return` is used to escape a function to stop its execution, return does not always need to return a value and this is a very common usecase which can reduce your indentation level which can make your code cleaner and easier to read overall. 
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

However functions also have a special property where since they capture the whole previous scope that can access variables that are not yet defined:
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

However if you call the function before you definte the variable the program will throw an error:
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

Here is an example that may seem confusing at first but understanding how scoping of functions works helps:
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

Overloading functions is the ablility to have functions that have different number of parameters defined separately and not interfer with eachother, overloading functions are possible inside of classes which we will cover later, but defining functions in the scope you cannot overload them, one will simply overwrite the other. This may be possible at a later date at which point this section of the documentation will be updated.

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

This however is not the case with built in functions, since they are implemented internally they work slightly different and as such they are able to be overloaded, all information on built in overloads will be documented separately, see the next section on Built In Functions.

### Built In Functions

Throughout this documentation I have been using built in functions, these functions are implemented natively in the language that allow you to do the basic functions, some examples that I have been using are `print`, `input`, and `sleep`.
There is a list of these basic functions and these provide some key functionality to the language so you should review them, and they can be found on the separate page [here](). 

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

Usually each class has its own static members and each value has its own members too, these are documented on a separate page and you can find that [here]().

## Lists

Lists are a form of collection, they are key data structure in the language, they allow you to store multiple values inside of one value, lists in Arucas are dynamic meaning that they do not have a fixed size you are also able to put any type of value in a list. Lists are ordered data structure meaning their order stays consistent with how you input values into the list.

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

We can get the number of values inside of a list by using a built in function: `len`:
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
Something to note is that if an index is provided that is outside the bounds of the list then an error will be thrown.

To manipulate the contents of the list we can take a look at the available methods:
```kotlin
list = [1, 2, 3];

// append method adds a value **to the end** of the list
list.append(4);

// insert method adds a value at a specific index in the list
list.insert(0, 0); // list = [0, 1, 2, 3]

// remove methods removes a value at a specific index
list.remove(3); // list = [0, 1, 2]

// set method sets the value at a specific index
list.set(0, "zero"); // list = ["zero", 1, 2]

// A short hand for assigning indexes of lists was introduced in 1.2.0
// We can again use the [] operator to assign an index in the list
list[1] = "one"; // list = ["zero", "one", 2]
```

### List Unpacking

Lists provide special functionality in Arucas as they provide the ability to unpack them. This means you are able to extract all of the variables in the list into variables. Here is an example:
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

Maps like lists are a form of collection, maps can be seen as similar to lists, but instead of using indexes to access a value maps allow you to definte a specific key value to access certain values. Maps have a literal and by default these will be ordered based on the order they are inputted. Maps also have no fixed size.

### Simple Maps

Maps have a literal: `{}` which can be used to create maps. In the literal you must declare a key and a value which are separated by a colon:
```kotlin
map = {"key": "value"};

// We can also declare an empty map
map = {};
```
Any types can be used for keys or values, for custom classes you need to ensure they have an appropriate hashing function however we will cover this later in the classes section.

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

Some other useful methods of the map are those that get all of the keys and values:
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

Another useful thing to note is that when using the `foreach` loop maps will loop over their keys, inside of the loop you can the use it to access the value:
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

Sets are another form of collection. A set is basically just a map that doesn't have values, this allows for a list-like collection however you cannot access the values using an index and cannot have duplicate values in the set. Sets have the benefit that they are faster to search than lists, an example of this will be shown in the section.

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

### Creation

### Throwing 

### Catching

## Imports

## Classes

Classes in Arucas allow for abstraction, they provide a way to encapsulate values into another value, and classes let you define certain behaviour with the value such as interactions with operators and the methods that the value has.

### Syntax

The class syntax is very simple and similar to many other languages, we use the `class` keyword to declare a class definition followed by the name of the class and then a series of class statements which we will cover further on in this section.
```kotlin
// Classes follow Pascal Naming
class ExampleClass {
}
```

### Constructors

Constructors are essentially functions that are run when the class is instantiated. These are often used to set fields and often take parameters. By default if no constructor is declared then a synthetic constructor is created: you will be able to construct the class without any parameters. However if any constructors are defined this synthetic constructor will not be available.

To define a constructor in a class we use the class name followed by brackets which can contain parameters and then followed by a statement which is run when the class is instantiated.
```kotlin
class ExampleClass {
    // Constructor
    ExampleClass() {
    }
}
```

### Fields

### Methods

### Operators

### Static Methods and Fields

## Enums

### Syntax

### Constructors

## Threads

### Purpose

### Creation

### Stopping Threads

### Thread Safety

## Java Integration

If there are specific things you want to achieve that aren't possible with the base language you may want to look into calling Java code from within your script. This is possibly by using the `util.Internal` library and importing the `Java` class.
```kotlin
import Java from util.Internal;
``` 

### Java Types

There are many static methods for the Java class and these will be key for creating Java typed values. One such method is `valueOf`, this converts any Arucas typed value into a Java one:
```kotlin
import Java from util.Internal;

jString = Java.of(""); // Arucas String type -> Java String type
```

All Java typed values have the Arucas type of `Java` and they all have some basic methods you can use, these allow you to access their methods and fields which we will explore later in this documentation. Another method which is important is the `toArucas` method which tries to convert the Java typed value back into an Arucas typed value.
```kotlin
import Java from util.Internal;

jString = Java.of(""); // Java String type

string = jString.toArucas(); // Back to Arucas String type
``` 

Not every Java type has a conversion and so if you try to convert a Java type that does not have a conversion it will simply just return itself.

### Methods and Fields

`Java` values have a property that allows them to call Java methods, there are different ways this can be done but the advised way is to call the method as usual:
```kotlin
import Java from util.Internal;

jString = Java.of("");
// Java methods return Java typed values too
// The isBlank method is a Java method!
jBoolean = jString.isBlank();

if (jBoolean.toArucas()) {
    print("String was blank");
}
```
You are also able to call methods with parameters the same way you would call an Arucas function, however the types of the values must match the method signiture, the arguments you pass in should generally be Java typed.

Something to note about methods is that they use the Java reflection library internally which makes calling Java methods quite slow. On a small scale this is fine however if you plan on repeatedly call a method you should consider delegating the method. When the method is delegated the Internal library creates a MethodHandle which is significantly faster.
```kotlin
import Java from util.Internal;

jString = Java.of("");
delegate = jString.isBlank;

for (i = 0; i < 100; i++) {
    delegate();
}
```

Accessing fields is also similar to Arucas this can be done by just using the dor operator:
```kotlin
import Java from util.Internal;

array = Java.arrayOf();
// length field of Java array type
array.length;
```

### Constructing Java Objects

Now this is great but what if we want to construct a Java Object? Well we can use `Java.constructClass()`, this method takes in the class name and then any amount of parameters:
```kotlin
import Java from util.Internal;

ArrayList = "java.util.ArrayList";

// From looking at Java code this would invoke the
// constructor with no parameters
jList = Java.constructClass(ArrayList);

// Adding Java Strings into ArrayList
jList.add("One"); 
jList.add("Two");
```

As mentioned before Arucas values can be converted to Java values and you have the ability to construct Java classes but there are still some cases where Java type values cannot be created. These are primitives, arrays, and lambdas. To remedy this the Java class provides static methods to create these types of values:
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

Now we know how we can construct Object and call their methods in Java, what about static methods and fields? Well this is done again through the Java class with a static method:
```kotlin
import Java from util.Internal;

Integer = "java.lang.Integer";

// Class name, method name, parameters...
Java.callStaticMethod(Integer, "parseInt", "120");

// Class name, field name
Java.getStaticField(Integer, "MAX_VALUE");

// Class name, field name, new value (must be correct type)
// Obviously this won't work, but it's just an example
Java.setStaticField(Integer, "MAX_VALUE", Java.intOf(100));"
```

#

# Language Syntax Overview

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


#


## BuiltInExtension  
  
### `getArucasVersion()`  
- Description: This is used to get the version of Arucas that is currently running  
- Returns - String: the version of Arucas that is currently running  
- Example:  
```kt  
getArucasVersion();  
```  
  
### `getMilliTime()`  
- Description: This is used to get the current time in milliseconds  
- Returns - Number: the current time in milliseconds  
- Example:  
```kt  
getMilliTime();  
```  
  
### `debug(bool)`  
- Description: This is used to enable or disable debug mode  
- Parameter - Boolean (`bool`): true to enable debug mode, false to disable debug mode  
- Example:  
```kt  
debug(true);  
```  
  
### `runFromString(string)`  
- Description: This is used to evaluate a string as a script  
- Parameter - String (`string`): the string to evaluate  
- Returns - Value: the return value of the script  
- Example:  
```kt  
runFromString('return 1;');  
```  
  
### `getNanoTime()`  
- Description: This is used to get the current time in nanoseconds  
- Returns - Number: the current time in nanoseconds  
- Example:  
```kt  
getNanoTime();  
```  
  
### `getTime()`  
- Description: This is used to get the current time formatted with HH:mm:ss in your local time  
- Returns - String: the current time formatted with HH:mm:ss  
- Example:  
```kt  
getTime();  
```  
  
### `isMain()`  
- Description: This is used to check whether the script is the main script  
- Returns - Boolean: true if the script is the main script, false if it is not  
- Example:  
```kt  
isMain();  
```  
  
### `throwRuntimeError(message)`  
- Deprecated: You should use the `throw` keyword  
- Description: This is used to throw a runtime error  
- Parameter - String (`message`): the message of the error  
- Throws - Error:  
  - `'the error with the message'`  
- Example:  
```kt  
throwRuntimeError('I'm throwing this error');  
```  
  
### `experimental(bool)`  
- Description: This is used to enable or disable experimental mode  
- Parameter - Boolean (`bool`): true to enable experimental mode, false to disable experimental mode  
- Example:  
```kt  
experimental(true);  
```  
  
### `run(path)`  
- Description: This is used to run a .arucas file, you can use on script to run other scripts  
- Parameter - String (`path`): as a file path  
- Returns - Value: any value that the file returns  
- Throws - Error:  
  - `'Failed to execute script...'`  
- Example:  
```kt  
run('/home/user/script.arucas');  
```  
  
### `getUnixTime()`  
- Description: This is used to get the current time in seconds since the Unix epoch  
- Returns - Number: the current time in seconds since the Unix epoch  
- Example:  
```kt  
getUnixTime();  
```  
  
### `sleep(milliseconds)`  
- Description: This pauses your program for a certain amount of milliseconds  
- Parameter - Number (`milliseconds`): milliseconds to sleep  
- Example:  
```kt  
sleep(1000);  
```  
  
### `random(bound)`  
- Description: This is used to generate a random integer between 0 and the bound  
- Parameter - Number (`bound`): the maximum bound (exclusive)  
- Returns - Number: the random integer  
- Example:  
```kt  
random(10);  
```  
  
### `input(prompt)`  
- Description: This is used to take an input from the user  
- Parameter - String (`prompt`): the prompt to show the user  
- Returns - String: the input from the user  
- Example:  
```kt  
input('What is your name?');  
```  
  
### `print(printValue...)`  
- Description: This prints a number of values to the console  
- Parameter - Value (`printValue...`): the value to print  
- Example:  
```kt  
print('Hello World', 'This is a test', 123);  
```  
  
### `print(printValue)`  
- Description: This prints a value to the console  
- Parameter - Value (`printValue`): the value to print  
- Example:  
```kt  
print('Hello World');  
```  
  
### `len(collection)`  
- Description: This is used to get the length of a collection or string  
- Parameter - String (`collection`): the collection or string  
- Throws - Error:  
  - `'Cannot pass ... into len()'`  
- Example:  
```kt  
len("Hello World");  
```  
  
### `stop()`  
- Description: This is used to stop a script  
- Example:  
```kt  
stop();  
```  
  
### `callFunctionWithList(function, list)`  
- Deprecated: You should use Function class `Function.callWithList(fun() {}, [])`  
- Description: This is used to call a function with a list of arguments  
- Parameters:  
  - Function (`function`): the function  
  - List (`list`): the list of arguments  
- Returns - Value: the return value of the function  
- Example:  
```kt  
callFunctionWithList(fun(n1, n2, n3) { }, [1, 2, 3]);  
```  
  
### `suppressDeprecated(bool)`  
- Description: This is used to enable or disable suppressing deprecation warnings  
- Parameter - Boolean (`bool`): true to enable suppressing deprecation warnings, false to disable suppressing deprecation warnings  
- Example:  
```kt  
suppressDeprecated(true);  
```  
  
### `getDate()`  
- Description: This is used to get the current date formatted with dd/MM/yyyy in your local time  
- Returns - String: the current date formatted with dd/MM/yyyy  
- Example:  
```kt  
getDate();  
```

#


# Boolean class  
Boolean class for Arucas.  
  
This class cannot be constructed since Booleans have literals.  
  
Class does not need to be imported.  
  
Fully Documented.  
  
  
  
# Collector class  
Collector class for Arucas.  
  
This class is similar to Java streams, allowing for easy modifications of collections.  
  
Import with `import Collector from util.Collection;`  
  
Fully Documented.  
  
## Methods  
  
### `<Collector>.flatten()`  
- Description: If there are values in the collector that are collections they will be expanded,   
collections inside collections are not flattened, you would have to call this method again  
- Returns - Collector: a new Collector with the expanded values  
- Example:  
```kt  
Collector.of([1, 2, [3, 4]]).flatten();  
```  
  
### `<Collector>.filter(predicate)`  
- Description: This filters the collection using the predicate  
- Parameter - Function (`predicate`): a function that takes a value and returns Boolean, true if it should be kept, false if not  
- Returns - Collector: the filtered collection  
- Throws - Error:  
  - `'Predicate must return Boolean'`  
- Example:  
```kt  
Collector.of([1, 2, 3]).filter(fun(value) {  
 return value < 3;});  
```  
  
### `<Collector>.toSet()`  
- Description: This puts all the values in the collector into a set and returns it  
- Returns - Set: a set with all the values in the collector  
- Example:  
```kt  
Collector.of([1, 2, 3]).toSet();  
```  
  
### `<Collector>.forEach(function)`  
- Description: This iterates over all the values in the Collector and calls the passed in function with each value  
- Parameter - Function (`function`): a function that takes a value and returns nothing  
- Returns - Collector: the Collector  
- Example:  
```kt  
Collector.of([1, 2, 3]).forEach(fun(value) {  
 print(value);});  
```  
  
### `<Collector>.noneMatch(predicate)`  
- Description: This checks if none of the values in the collection match the predicate  
- Parameter - Function (`predicate`): a function that takes a value and returns Boolean, true if it matches, false if not  
- Returns - Boolean: true if none of the values match the predicate, false if not  
- Throws - Error:  
  - `'Predicate must return Boolean'`  
- Example:  
```kt  
Collector.of([1, 2, 3]).noneMatch(fun(value) {  
 return value < 5;});  
```  
  
### `<Collector>.toList()`  
- Description: This puts all the values in the collector into a list and returns it  
- Returns - List: a list with all the values in the collector  
- Example:  
```kt  
Collector.of([1, 2, 3]).toList();  
```  
  
### `<Collector>.map(mapper)`  
- Description: This maps the values in Collector to a new value  
- Parameter - Function (`mapper`): a function that takes a value and returns a new value  
- Returns - Collector: a new Collector with the mapped values  
- Example:  
```kt  
Collector.of([1, 2, 3]).map(fun(value) {  
 return value * 2;});  
```  
  
### `<Collector>.allMatch(predicate)`  
- Description: This checks if all the values in the collection match the predicate  
- Parameter - Function (`predicate`): a function that takes a value and returns Boolean, true if it matches, false if not  
- Returns - Boolean: true if all the values match the predicate, false if not  
- Throws - Error:  
  - `'Predicate must return Boolean'`  
- Example:  
```kt  
Collector.of([1, 2, 3]).anyMatch(fun(value) {  
 return value < 5;});  
```  
  
### `<Collector>.anyMatch(predicate)`  
- Description: This checks if any of the values in the collection match the predicate  
- Parameter - Function (`predicate`): a function that takes a value and returns Boolean, true if it matches, false if not  
- Returns - Boolean: true if any of the values match the predicate, false if not  
- Throws - Error:  
  - `'Predicate must return Boolean'`  
- Example:  
```kt  
Collector.of([1, 2, 3]).anyMatch(fun(value) {  
 return value < 3;});  
```  
  
## Static Methods  
  
### `Collector.of(value...)`  
- Description: This creates a collector for a collection  
- Parameter - Value (`value...`): the values you want to evaluate  
- Returns - Collector: the collector  
- Example:  
```kt  
Collector.of(1, 2, '3');  
```  
  
### `Collector.of(collection)`  
- Description: This creates a collector for a collection  
- Parameter - Collection (`collection`): the collection of values you want to evaluate  
- Returns - Collector: the collector  
- Throws - Error:  
  - `'... is not a collection'`  
- Example:  
```kt  
Collector.of([1, 2, 3]);  
```  
  
### `Collector.isCollection(value)`  
- Description: This checks if the value is a collection  
- Parameter - Value (`value`): the value you want to check  
- Returns - Boolean: true if the value is a collection  
- Example:  
```kt  
Collector.isCollection([1, 2, 3]);  
```  
  
  
# Enum class  
Enum class for Arucas.  
  
All enums extends this class.  
  
Class does not need to be imported.  
  
Fully Documented.  
  
## Methods  
  
### `<Enum>.getName()`  
- Description: This allows you to get the name of the enum value  
- Returns - String: the name of the enum value  
- Example:  
```kt  
enum.getName();  
```  
  
### `<Enum>.ordinal()`  
- Description: This allows you to get the ordinal of the enum value  
- Returns - Number: the ordinal of the enum value  
- Example:  
```kt  
enum.ordinal();  
```  
  
  
  
# Error class  
Error class for Arucas.  
  
This class is the only type that can be thrown  
  
Class does not need to be imported.  
  
Fully Documented.  
  
## Constructors  
  
### `new Error()`  
- Description: This creates a new Error value with no message  
- Example:  
```kt  
new Error();  
```  
### `new Error(details)`  
- Description: This creates a new Error value with the given details as a message  
- Parameter - String (`details`): the details of the error  
- Example:  
```kt  
new Error('This is an error');  
```  
### `new Error(details, value)`  
- Description: This creates a new Error value with the given details as a message and the given value  
- Parameters:  
  - String (`details`): the details of the error  
  - Value (`value`): the value that is related to the error  
- Example:  
```kt  
new Error('This is an error', [1, 2, 3]);  
```  
  
## Methods  
  
### `<Error>.getValue()`  
- Description: This returns the value that is related to the error  
- Returns - Value: the value that is related to the error  
- Example:  
```kt  
error.getValue();  
```  
  
### `<Error>.getDetails()`  
- Description: This returns the raw message of the error  
- Returns - String: the details of the error  
- Example:  
```kt  
error.getDetails();  
```  
  
### `<Error>.getFormattedDetails()`  
- Description: This returns the message of the error in a formatted string  
- Returns - String: the details of the error  
- Example:  
```kt  
error.getFormattedDetails();  
```  
  
  
  
# File class  
File class for Arucas.  
  
This class allows you to manipulate files.  
  
Class does not need to be imported.  
  
Fully Documented.  
  
## Constructors  
  
### `new File(path)`  
- Description: This creates a new File object with set path  
- Parameter - String (`path`): the path of the file  
- Example:  
```kt  
new File('foo/bar/script.arucas')  
```  
  
## Methods  
  
### `<File>.read()`  
- Description: This reads the file and returns the contents as a string  
- Returns - String: the contents of the file  
- Throws - Error:  
  - `'There was an error reading the file: ...'`  
  - `'Out of Memory - The file you are trying to read is too large'`  
- Example:  
```kt  
file.read()  
```  
  
### `<File>.getName()`  
- Description: This returns the name of the file  
- Returns - String: the name of the file  
- Example:  
```kt  
File.getName()  
```  
  
### `<File>.getAbsolutePath()`  
- Description: This returns the absolute path of the file  
- Returns - String: the absolute path of the file  
- Example:  
```kt  
file.getAbsolutePath()  
```  
  
### `<File>.getPath()`  
- Description: This returns the path of the file  
- Returns - String: the path of the file  
- Example:  
```kt  
file.getPath()  
```  
  
### `<File>.exists()`  
- Description: This returns if the file exists  
- Returns - Boolean: true if the file exists  
- Throws - Error:  
  - `'Could not check file: ...'`  
- Example:  
```kt  
file.exists()  
```  
  
### `<File>.createDirectory()`  
- Description: This creates all parent directories of the file if they don't already exist  
- Returns - Boolean: true if the directories were created  
- Throws - Error:  
  - `'...'`  
- Example:  
```kt  
file.createDirectory()  
```  
  
### `<File>.getSubFiles()`  
- Description: This returns a list of all the sub files in the directory  
- Returns - List: a list of all the sub files in the directory  
- Throws - Error:  
  - `'Could not find any files'`  
- Example:  
```kt  
file.getSubFiles()  
```  
  
### `<File>.delete()`  
- Description: This deletes the file  
- Returns - Boolean: true if the file was deleted  
- Throws - Error:  
  - `'Could not delete file: ...'`  
- Example:  
```kt  
file.delete()  
```  
  
### `<File>.write(string)`  
- Description: This writes a string to a file  
- Parameter - String (`string`): the string to write to the file  
- Throws - Error:  
  - `'There was an error writing the file: ...'`  
- Example:  
```kt  
file.write('Hello World!')  
```  
  
### `<File>.open()`  
- Description: This opens the file (as in opens it on your os)  
- Example:  
```kt  
file.open()  
```  
  
## Static Methods  
  
### `File.getDirectory()`  
- Description: This returns the file of the working directory  
- Returns - File: the file of the working directory  
- Example:  
```kt  
File.getDirectory()  
```  
  
  
# Function class  
Function class for Arucas.  
  
Adds utilities for delegating and calling functions.  
  
Class does not need to be imported.  
  
Fully Documented.  
  
## Static Methods  
  
### `Function.call(delegate, parameters...)`  
- Description: Calls the given delegate with the given arbitrary parameters  
- Parameters:  
  - Function (`delegate`): the delegate to call  
  - Value (`parameters...`): the parameters to pass to the delegate  
- Returns - Value: the return value of the delegate  
- Example:  
```kt  
Function.call(Function.getBuiltIn('print', 1), 'Hello World!');  
```  
  
### `Function.getMethod(value, methodName, parameterCount)`  
- Description: Returns a method delegate with the given name and parameter count  
- Parameters:  
  - Value (`value`): the value to call the method on  
  - String (`methodName`): the name of the method  
  - Number (`parameterCount`): the parameter count of the method  
- Returns - Function: the method delegate  
- Example:  
```kt  
Function.getMethod('String', 'contains', 1);  
```  
  
### `Function.callWithList(delegate, parameters)`  
- Description: Calls the given delegate with the given parameters  
- Parameters:  
  - Function (`delegate`): the delegate to call  
  - List (`parameters`): the parameters to pass to the delegate  
- Returns - Value: the return value of the delegate  
- Example:  
```kt  
Function.callWithList(fun(m1, m2) { }, ['Hello', 'World']);  
```  
  
### `Function.getBuiltIn(functionName, parameterCount)`  
- Description: Returns a built-in function delegate with the given name and parameter count  
- Parameters:  
  - String (`functionName`): the name of the function  
  - Number (`parameterCount`): the parameter count of the function  
- Returns - Function: the built-in function delegate  
- Example:  
```kt  
Function.getBuiltIn('print', 1);  
```  
  
  
# Java class  
Java class for Arucas.  
  
This allows for direct interaction from Arucas to Java  
  
Import with `import Java from util.Internal;`  
  
Fully Documented.  
  
## Methods  
  
### `<Java>.callMethod(methodName, parameters...)`  
- Deprecated: You should call the method directly on the value: Java.valueOf('').isBlank();  
- Description: This calls the specified method with the specified parameters, this is slower   
than calling a delegate, this is the same speed as calling the method directly on the value however  
- Parameters:  
  - String (`methodName`): the name of the method  
  - Value (`parameters...`): the parameters to call the method with, this may be none, a note - if you are calling a VarArgs method you must pass a Java Object array with your VarArg arguments  
- Returns - Java: the return value of the method call wrapped in the Java wrapper  
- Throws - Error:  
  - `'No such method ... with ... parameters exists for ...'`  
  - `'First parameter must be name of method'`  
- Example:  
```kt  
Java.valueOf('').callMethod('isBlank');  
```  
  
### `<Java>.setField(fieldName, value)`  
- Deprecated: You should assign the value directly on the value: Java.constructClass('me.senseiwells.impl.Test').A = 'Hello';  
- Description: This sets the specified field to the specified value  
- Parameters:  
  - String (`fieldName`): the name of the field  
  - Value (`value`): the value to set the field to, the value type must match the type of the field  
- Example:  
```kt  
Java.constructClass('me.senseiwells.impl.Test').setField('A', 'Hello');  
```  
  
### `<Java>.getField(fieldName)`  
- Deprecated: You should call the method directly on the value: Java.constructClass('me.senseiwells.impl.Test').A;  
- Description: This returns the Java wrapped value of the specified field  
- Parameter - String (`fieldName`): the name of the field  
- Returns - Java: the Java wrapped value of the field  
- Example:  
```kt  
Java.constructClass('me.senseiwells.impl.Test').getField('A');  
```  
  
### `<Java>.toArucas()`  
- Description: This converts the Java value to an Arucas Value  
- Returns - Value: the Value in Arucas, this may still be of Java value if the value cannot be converted into an Arucas value, values like Strings, Numbers, Lists, etc... will be converted  
- Example:  
```kt  
Java.valueOf([1, 2, 3]).toArucas();  
```  
  
### `<Java>.getMethodDelegate(methodName, parameters)`  
- Description: This returns a method delegate for the specified method name and parameters,   
delegating the method is much faster since it uses MethodHandles, so if you are calling   
a method repetitively it is faster to delegate the method and then call the delegate  
- Parameters:  
  - String (`methodName`): the name of the method  
  - Number (`parameters`): the number of parameters  
- Returns - Function: the function containing the Java method delegate  
- Throws - Error:  
  - `'No such method ... with ... parameters can be found'`  
- Example:  
```kt  
Java.valueOf('string!').getMethodDelegate('isBlank', 0);  
```  
  
## Static Methods  
  
### `Java.floatOf(num)`  
- Description: Creates a Java value float, to be used in Java, since floats cannot be explicitly declared in Arucas  
- Parameter - Number (`num`): the number to convert to a Java float  
- Returns - Java: the float in Java wrapper  
- Example:  
```kt  
Java.floatOf(1.0);  
```  
  
### `Java.shortOf(num)`  
- Description: Creates a Java value short, to be used in Java since shorts cannot be explicitly declared in Arucas  
- Parameter - Number (`num`): the number to convert to a Java short  
- Returns - Java: the short in Java wrapper  
- Example:  
```kt  
Java.shortOf(0xFF);  
```  
  
### `Java.classFromName(className)`  
- Description: Gets a Java class from the name of the class  
- Parameter - String (`className`): the name of the class you want to get  
- Returns - Java: the Java Class<?> value wrapped in the Java wrapper  
- Throws - Error:  
  - `'No such class with ...'`  
- Example:  
```kt  
Java.classFromName('java.util.ArrayList');  
```  
  
### `Java.longArray(size)`  
- Description: Creates a Java long array with a given size, the array is filled with 0's   
by default and can be filled with only longs  
- Parameter - Number (`size`): the size of the array  
- Returns - Java: the Java long array  
- Example:  
```kt  
Java.longArray(10);  
```  
  
### `Java.supplierOf(function)`  
- Description: Creates a Java Supplier object from a given function  
- Parameter - Function (`function`): the function to be executed, this must have no parameters and must return (supply) a value  
- Returns - Java: the Java Supplier object  
- Example:  
```kt  
Java.supplierOf(fun() {  
 return "supplier";});  
```  
  
### `Java.functionOf(function)`  
- Description: Creates a Java Function object from a given function  
- Parameter - Function (`function`): the function to be executed, this must have one parameter and must return a value  
- Returns - Java: the Java Function object  
- Example:  
```kt  
Java.functionOf(fun(num) {  
 return num + 10;});  
```  
  
### `Java.doubleOf(num)`  
- Description: Creates a Java value double, to be used in Java  
- Parameter - Number (`num`): the number to convert to a Java double  
- Returns - Java: the double in Java wrapper  
- Example:  
```kt  
Java.doubleOf(1.0);  
```  
  
### `Java.getStaticField(className, fieldName)`  
- Description: Gets a static field Java value from a Java class  
- Parameters:  
  - String (`className`): the name of the class  
  - String (`fieldName`): the name of the field  
- Returns - Java: the Java value of the field wrapped in the Java wrapper  
- Throws - Error:  
  - `'No such class with ...'`  
- Example:  
```kt  
Java.getStaticField('java.lang.Integer', 'MAX_VALUE');  
```  
  
### `Java.constructClass(className, parameters...)`  
- Description: This constructs a Java class with specified class name and parameters  
- Parameters:  
  - String (`className`): the name of the class  
  - Value (`parameters...`): any parameters to pass to the constructor, there may be no parameters, again if calling VarArgs constructor you must have your VarArg parameters in a Java Object array  
- Returns - Java: the constructed Java Object wrapped in the Java wrapper  
- Throws - Error:  
  - `'First parameter must be a class name'`  
  - `'No such class with ...'`  
  - `'No such constructor with ... parameters exists for ...'`  
- Example:  
```kt  
Java.constructClass('java.util.ArrayList');  
```  
  
### `Java.shortArray(size)`  
- Description: Creates a Java short array with a given size, the array is filled with 0's   
by default and can be filled with only shorts  
- Parameter - Number (`size`): the size of the array  
- Returns - Java: the Java short array  
- Example:  
```kt  
Java.shortArray(10);  
```  
  
### `Java.runnableOf(function)`  
- Description: Creates a Java Runnable object from a given function  
- Parameter - Function (`function`): the function to be executed, this must have no parameters and any return values will be ignored  
- Returns - Java: the Java Runnable object  
- Example:  
```kt  
Java.runnableOf(fun() {  
 print('runnable');});  
```  
  
### `Java.charOf(string)`  
- Description: Creates a Java value char, to be used in Java since chars cannot be explicitly declared in Arucas  
- Parameter - String (`string`): the string with one character to convert to a Java char  
- Returns - Java: the char in Java wrapper  
- Throws - Error:  
  - `'String must be 1 character long'`  
- Example:  
```kt  
Java.charOf('f');  
```  
  
### `Java.charArray(size)`  
- Description: Creates a Java char array with a given size, the array is filled with null's   
(null characters) by default and can be filled with only chars  
- Parameter - Number (`size`): the size of the array  
- Returns - Java: the Java char array  
- Example:  
```kt  
Java.charArray(10);  
```  
  
### `Java.intOf(num)`  
- Description: Creates a Java value int, to be used in Java since ints cannot be explicitly declared in Arucas  
- Parameter - Number (`num`): the number to convert to a Java int  
- Returns - Java: the int in Java wrapper  
- Example:  
```kt  
Java.intOf(0xFF);  
```  
  
### `Java.floatArray(size)`  
- Description: Creates a Java float array with a given size, the array is filled with 0's   
by default and can be filled with only floats  
- Parameter - Number (`size`): the size of the array  
- Returns - Java: the Java float array  
- Example:  
```kt  
Java.floatArray(10);  
```  
  
### `Java.booleanArray(size)`  
- Description: Creates a Java boolean array with a given size, the array is filled with false   
by default and can be filled with only booleans  
- Parameter - Number (`size`): the size of the array  
- Returns - Java: the Java boolean array  
- Example:  
```kt  
Java.booleanArray(10);  
```  
  
### `Java.doubleArray(size)`  
- Description: Creates a Java double array with a given size, the array is filled with 0's   
by default and can be filled with only doubles  
- Parameter - Number (`size`): the size of the array  
- Returns - Java: the Java double array  
- Example:  
```kt  
Java.doubleArray(10);  
```  
  
### `Java.byteOf(num)`  
- Description: Creates a Java value byte, to be used in Java since bytes cannot be explicitly declared in Arucas  
- Parameter - Number (`num`): the number to convert to a Java byte  
- Returns - Java: the byte in Java wrapper  
- Example:  
```kt  
Java.byteOf(0xFF);  
```  
  
### `Java.valueOf(value)`  
- Description: Converts any Arucas value into a Java value then wraps it in the Java wrapper and returns it  
- Parameter - Value (`value`): any value to get the Java value of  
- Example:  
```kt  
Java.valueOf('Hello World!');  
```  
  
### `Java.byteArray(size)`  
- Description: Creates a Java byte array with a given size, the array is filled with 0's   
by default and can be filled with only bytes  
- Parameter - Number (`size`): the size of the array  
- Returns - Java: the Java byte array  
- Example:  
```kt  
Java.byteArray(10);  
```  
  
### `Java.longOf(num)`  
- Description: Creates a Java value long, to be used in Java since longs cannot be explicitly declared in Arucas  
- Parameter - Number (`num`): the number to convert to a Java long  
- Returns - Java: the long in Java wrapper  
- Example:  
```kt  
Java.longOf(1000000000.0);  
```  
  
### `Java.arrayOf(values...)`  
- Description: Creates a Java Object array with a given values, this will be the size of the array,   
again this cannot be used to create primitive arrays  
- Parameter - Value (`values...`): the values to add to the array  
- Returns - Java: the Java Object array  
- Example:  
```kt  
Java.arrayOf(1, 2, 3, 'string!', false);  
```  
  
### `Java.arrayWithSize(size)`  
- Description: Creates a Java Object array with a given size, the array is filled with null values   
by default and can be filled with any Java values, this array cannot be expanded  
- Parameter - Number (`size`): the size of the array  
- Returns - Java: the Java Object array  
- Example:  
```kt  
Java.arrayWithSize(10);  
```  
  
### `Java.booleanOf(bool)`  
- Description: Creates a Java value boolean, to be used in Java  
- Parameter - Boolean (`bool`): the boolean to convert to a Java boolean  
- Returns - Java: the boolean in Java wrapper  
- Example:  
```kt  
Java.booleanOf(true);  
```  
  
### `Java.intArray(size)`  
- Description: Creates a Java int array with a given size, the array is filled with 0's   
by default and can be filled with only ints  
- Parameter - Number (`size`): the size of the array  
- Returns - Java: the Java int array  
- Example:  
```kt  
Java.intArray(10);  
```  
  
### `Java.callStaticMethod(className, methodName, parameters...)`  
- Description: Calls a static method of a Java class, this is slower than delegating a method, but better for a one off call  
- Parameters:  
  - String (`className`): the name of the class  
  - String (`methodName`): the name of the method  
  - Value (`parameters...`): any parameters to call the method with, this can be none, a note - if you are calling a VarArg method then you must have your VarArg parameters in a Java Object array  
- Returns - Java: the return value of the method wrapped in the Java wrapper  
- Throws - Error:  
  - `'First parameter must be a class name and the second parameter must be a method name'`  
  - `'No such class with ...'`  
  - `'No such method ... with ... parameters exists for ...'`  
- Example:  
```kt  
Java.callStaticMethod('java.lang.Integer', 'parseInt', '123');  
```  
  
### `Java.setStaticField(className, fieldName, newValue)`  
- Description: Sets a static field in a Java class with a new value, the type of the new value needs to match the type of the field,   
you can pass in Java wrapped values to guarantee type matching, they will be unwrapped, regular values will be converted  
- Parameters:  
  - String (`className`): the name of the class  
  - String (`fieldName`): the name of the field  
  - Value (`newValue`): the new value  
- Throws - Error:  
  - `'No such class with ...'`  
- Example:  
```kt  
// Obviously this won't work, but it's just an example  
Java.setStaticField('java.lang.Integer', 'MAX_VALUE', Java.intOf(100));"  
```  
  
### `Java.getStaticMethodDelegate(className, methodName, parameters)`  
- Description: Gets a static method delegate from a Java class, delegating the method is much faster than directly calling it since it uses MethodHandles,   
if you are repetitively calling a static method you should delegate it and call that delegate  
- Parameters:  
  - String (`className`): the name of the class  
  - String (`methodName`): the name of the method  
  - Number (`parameters`): the number of parameters  
- Returns - Function: the delegated Java method in an Arucas Function  
- Throws - Error:  
  - `'No such class with ...'`  
  - `'No such method ... with ... parameters can be found'`  
- Example:  
```kt  
Java.getStaticMethodDelegate('java.lang.Integer', 'parseInt', 1);  
```  
  
### `Java.consumerOf(function)`  
- Description: Creates a Java Consumer object from a given function  
- Parameter - Function (`function`): the function to be executed, this must have one parameter and any return values will be ignored, the parameter type is unknown at compile time  
- Returns - Java: the Java Consumer object  
- Example:  
```kt  
Java.consumerOf(fun(something) {  
 print(something);});  
```  
  
  
# Json class  
Json class for Arucas.  
  
This class allows you to create and manipulate JSON objects.  
  
Import with `import Json from util.Json;`  
  
Fully Documented.  
  
## Methods  
  
### `<Json>.writeToFile(file)`  
- Description: This writes the Json to a file  
- Parameter - File (`file`): the file that you want to write to  
- Throws - Error:  
  - `'There was an error writing the file: ...'`  
- Example:  
```kt  
json.writeToFile(new File('D:/cool/realDirectory'));  
```  
  
### `<Json>.getValue()`  
- Description: This converts the Json back into a Value  
- Returns - Value: the Value parsed from the Json  
- Example:  
```kt  
json.getValue();  
```  
  
## Static Methods  
  
### `Json.fromMap(map)`  
- Description: This converts a map into a Json, an important thing to note is that  
any values that are not Numbers, Booleans, Lists, Maps, or Null will use their  
toString() member to convert them to a string  
- Parameter - Map (`map`): the map that you want to parse into a Json  
- Returns - Json: the Json parsed from the map  
- Example:  
```kt  
Json.fromMap({'key': ['value1', 'value2']});  
```  
  
### `Json.fromList(list)`  
- Description: This converts a list into a Json, an important thing to note is that  
any values that are not Numbers, Booleans, Lists, Maps, or Null will use their  
toString() member to convert them to a string  
- Parameter - List (`list`): the list that you want to parse into a Json  
- Returns - Json: the Json parsed from the list  
- Example:  
```kt  
Json.fromList(['value', 1, true]);  
```  
  
### `Json.fromString(string)`  
- Description: This converts a string into a Json provided it is formatted correctly  
- Parameter - String (`string`): the string that you want to parse into a Json  
- Returns - Json: the Json parsed from the string  
- Throws - Error:  
  - `'Json could not be parsed'`  
- Example:  
```kt  
Json.fromString('{"key":"value"}');  
```  
  
  
# List class  
List class for Arucas.  
  
This class cannot be constructed since it has a literal, `[]`  
  
Class does not need to be imported.  
  
Fully Documented.  
  
## Methods  
  
### `<List>.containsAll(collection)`  
- Description: This allows you to check if the list contains all the values in a collection  
- Parameter - Collection (`collection`): the collection you want to check for  
- Returns - Boolean: true if the list contains all the values in the collection, false otherwise  
- Throws - Error:  
  - `'... is not a collection'`  
- Example:  
```kt  
`['object', 81, 96, 'case'].containsAll(['foo', 'bar']);`  
```  
  
### `<List>.clear()`  
- Description: This allows you to clear all the values the list  
- Example:  
```kt  
`['object', 81, 96, 'case'].clear();`  
```  
  
### `<List>.isEmpty()`  
- Description: This allows you to check if the list is empty  
- Returns - Boolean: true if the list is empty, false otherwise  
- Example:  
```kt  
`['object', 81, 96, 'case'].isEmpty();`  
```  
  
### `<List>.insert(value, index)`  
- Description: This allows you to insert a value at a specific index  
- Parameters:  
  - Value (`value`): the value you want to insert  
  - Number (`index`): the index you want to insert the value at  
- Returns - List: the list  
- Throws - Error:  
  - `'Index is out of bounds'`  
- Example:  
```kt  
`['object', 81, 96, 'case'].insert('foo', 1);`  
```  
  
### `<List>.concat(otherList)`  
- Deprecated: You should use `<List>.addAll(collection)` instead  
- Description: This allows you to concatenate two lists  
- Parameter - List (`otherList`): the list you want to concatenate with  
- Returns - List: the concatenated list  
- Example:  
```kt  
`['object', 81, 96, 'case'].concat(['foo', 'bar']);`  
```  
  
### `<List>.remove(index)`  
- Description: This allows you to remove the value at a specific index  
- Parameter - Number (`index`): the index of the value you want to remove  
- Returns - Value: the value that was removed  
- Throws - Error:  
  - `'Index is out of bounds'`  
- Example:  
```kt  
`['object', 81, 96, 'case'].remove(1);`  
```  
  
### `<List>.contains(value)`  
- Description: This allows you to check if the list contains a value  
- Parameter - Value (`value`): the value you want to check for  
- Returns - Boolean: true if the list contains the value, false otherwise  
- Example:  
```kt  
`['object', 81, 96, 'case'].contains('foo');`  
```  
  
### `<List>.addAll(collection)`  
- Description: This allows you to add all the values in a collection to the list  
- Parameter - Collection (`collection`): the collection you want to add  
- Returns - List: the list  
- Throws - Error:  
  - `'... is not a collection'`  
- Example:  
```kt  
`['object', 81, 96, 'case'].addAll(['foo', 'bar']);`  
```  
  
### `<List>.get(index)`  
- Description: This allows you to get the value at a specific index  
- Parameter - Number (`index`): the index of the value you want to get  
- Returns - Value: the value at the index  
- Throws - Error:  
  - `'Index is out of bounds'`  
- Example:  
```kt  
`['object', 81, 96, 'case'].get(1);`  
```  
  
### `<List>.toString()`  
- Description: This converts the list to a string and evaluating any collections inside it  
- Returns - String: the string representation of the set  
- Example:  
```kt  
`['object', 81, 96, 'case'].toString();`  
```  
  
### `<List>.indexOf(value)`  
- Description: This allows you to get the index of a value in the list  
- Parameter - Value (`value`): the value you want to check for  
- Returns - Number: the index of the value, -1 if the value is not in the list  
- Example:  
```kt  
`['object', 81, 96, 'case'].indexOf('case');`  
```  
  
### `<List>.append(value)`  
- Description: This allows you to append a value to the end of the list  
- Parameter - Value (`value`): the value you want to append  
- Returns - List: the list  
- Example:  
```kt  
`['object', 81, 96, 'case'].append('foo');`  
```  
  
  
  
# Map class  
Map class for Arucas.  
  
This class cannot be constructed since it has a literal, `{}`  
  
Class does not need to be imported.  
  
Fully Documented.  
  
## Methods  
  
### `<Map>.getValues()`  
- Description: This allows you to get the values in the map  
- Returns - List: a complete list of all the values  
- Example:  
```kt  
{'key': 'value', 'key2', 'value2'}.getValues();  
```  
  
### `<Map>.containsKey(key)`  
- Description: This allows you to check if the map contains a specific key  
- Parameter - Value (`key`): the key you want to check  
- Returns - Boolean: true if the map contains the key, false otherwise  
- Example:  
```kt  
{'key': 'value'}.containsKey('key');  
```  
  
### `<Map>.putAll(another map)`  
- Description: This allows you to put all the keys and values of another map into this map  
- Parameter - Map (`another map`): the map you want to merge into this map  
- Example:  
```kt  
{'key': 'value'}.putAll({'key2': 'value2'});  
```  
  
### `<Map>.get(key)`  
- Description: This allows you to get the value of a key in the map  
- Parameter - Value (`key`): the key you want to get the value of  
- Returns - Value: the value of the key, will return null if non-existent  
- Example:  
```kt  
{'key': 'value'}.get('key');  
```  
  
### `<Map>.isEmpty()`  
- Description: This allows you to check if the map is empty  
- Returns - Boolean: true if the map is empty, false otherwise  
- Example:  
```kt  
{'key': 'value'}.isEmpty();  
```  
  
### `<Map>.clear()`  
- Description: This allows you to clear the map of all the keys and values  
- Example:  
```kt  
{'key': 'value'}.clear();  
```  
  
### `<Map>.toString()`  
- Description: This allows you to get the string representation of the map and evaluating any collections inside it  
- Returns - String: the string representation of the map  
- Example:  
```kt  
{'key': []}.toString();  
```  
  
### `<Map>.getKeys()`  
- Description: This allows you to get the keys in the map  
- Returns - List: a complete list of all the keys  
- Example:  
```kt  
{'key': 'value', 'key2', 'value2'}.getKeys();  
```  
  
### `<Map>.putIfAbsent(key, value)`  
- Description: This allows you to put a key and value in the map if it doesn't exist  
- Parameters:  
  - Value (`key`): the key you want to put  
  - Value (`value`): the value you want to put  
- Returns - Value: the previous value associated with the key, null if none  
- Example:  
```kt  
{'key': 'value'}.putIfAbsent('key2', 'value2');  
```  
  
### `<Map>.remove(key)`  
- Description: This allows you to remove a key and its value from the map  
- Parameter - Value (`key`): the key you want to remove  
- Returns - Value: the value associated with the key, null if none  
- Example:  
```kt  
{'key': 'value'}.remove('key');  
```  
  
### `<Map>.put(key, value)`  
- Description: This allows you to put a key and value in the map  
- Parameters:  
  - Value (`key`): the key you want to put  
  - Value (`value`): the value you want to put  
- Returns - Value: the previous value associated with the key, null if none  
- Example:  
```kt  
{'key': 'value'}.put('key2', 'value2');  
```  
  
## Static Methods  
  
### `Map.unordered()`  
- Description: This function allows you to create an unordered map  
- Returns - Map: an unordered map  
- Example:  
```kt  
Map.unordered();  
```  
  
  
# Math class  
Math class for Arucas.  
  
Provides many basic math functions. This is a utility class, and cannot be constructed.  
  
Class does not need to be imported.  
  
Fully Documented.  
  
## Static Members  
  
### `Math.root2`  
- Description: The value of root 2  
- Type: Number  
- Assignable: false  
- Example:  
```kt  
Math.root2;  
```  
### `Math.e`  
- Description: The value of e  
- Type: Number  
- Assignable: false  
- Example:  
```kt  
Math.e;  
```  
### `Math.pi`  
- Description: The value of pi  
- Type: Number  
- Assignable: false  
- Example:  
```kt  
Math.pi;  
```  
  
## Static Methods  
  
### `Math.tan(num)`  
- Description: Returns the tangent of a number  
- Parameter - Number (`num`): the number to get the tangent of  
- Returns - Number: the tangent of the number  
- Example:  
```kt  
Math.tan(Math.pi);  
```  
  
### `Math.cosec(num)`  
- Description: Returns the cosecant of a number  
- Parameter - Number (`num`): the number to get the cosecant of  
- Returns - Number: the cosecant of the number  
- Example:  
```kt  
Math.cosec(Math.pi);  
```  
  
### `Math.mod(num1, num2)`  
- Description: Returns the remainder of a division  
- Parameters:  
  - Number (`num1`): the number to divide  
  - Number (`num2`): the divisor  
- Returns - Number: the remainder of the division  
- Example:  
```kt  
Math.mod(5, 2);  
```  
  
### `Math.max(num1, num2)`  
- Description: Returns the largest number  
- Parameters:  
  - Number (`num1`): the first number to compare  
  - Number (`num2`): the second number to compare  
- Returns - Number: the largest number  
- Example:  
```kt  
Math.max(5, 2);  
```  
  
### `Math.log(num)`  
- Description: Returns the natural logarithm of a number  
- Parameter - Number (`num`): the number to get the logarithm of  
- Returns - Number: the natural logarithm of the number  
- Example:  
```kt  
Math.log(Math.e);  
```  
  
### `Math.log(base, num)`  
- Description: Returns the logarithm of a number with a specified base  
- Parameters:  
  - Number (`base`): the base  
  - Number (`num`): the number to get the logarithm of  
- Returns - Number: the logarithm of the number  
- Example:  
```kt  
Math.log(2, 4);  
```  
  
### `Math.log10(num)`  
- Description: Returns the base 10 logarithm of a number  
- Parameter - Number (`num`): the number to get the logarithm of  
- Returns - Number: the base 10 logarithm of the number  
- Example:  
```kt  
Math.log10(100);  
```  
  
### `Math.cos(num)`  
- Description: Returns the cosine of a number  
- Parameter - Number (`num`): the number to get the cosine of  
- Returns - Number: the cosine of the number  
- Example:  
```kt  
Math.cos(Math.pi);  
```  
  
### `Math.cot(num)`  
- Description: Returns the cotangent of a number  
- Parameter - Number (`num`): the number to get the cotangent of  
- Returns - Number: the cotangent of the number  
- Example:  
```kt  
Math.cot(Math.pi);  
```  
  
### `Math.toDegrees(num)`  
- Description: Converts a number from radians to degrees  
- Parameter - Number (`num`): the number to convert  
- Returns - Number: the number in degrees  
- Example:  
```kt  
Math.toDegrees(Math.pi);  
```  
  
### `Math.ceil(num)`  
- Description: Rounds a number up to the nearest integer  
- Parameter - Number (`num`): the number to round  
- Returns - Number: the rounded number  
- Example:  
```kt  
Math.ceil(3.5);  
```  
  
### `Math.toRadians(num)`  
- Description: Converts a number from degrees to radians  
- Parameter - Number (`num`): the number to convert  
- Returns - Number: the number in radians  
- Example:  
```kt  
Math.toRadians(90);  
```  
  
### `Math.arccos(num)`  
- Description: Returns the arc cosine of a number  
- Parameter - Number (`num`): the number to get the arc cosine of  
- Returns - Number: the arc cosine of the number  
- Example:  
```kt  
Math.arccos(Math.cos(Math.pi));  
```  
  
### `Math.sec(num)`  
- Description: Returns the secant of a number  
- Parameter - Number (`num`): the number to get the secant of  
- Returns - Number: the secant of the number  
- Example:  
```kt  
Math.sec(Math.pi);  
```  
  
### `Math.abs(num)`  
- Description: Returns the absolute value of a number  
- Parameter - Number (`num`): the number to get the absolute value of  
- Returns - Number: the absolute value of the number  
- Example:  
```kt  
Math.abs(-3);  
```  
  
### `Math.min(num1, num2)`  
- Description: Returns the smallest number  
- Parameters:  
  - Number (`num1`): the first number to compare  
  - Number (`num2`): the second number to compare  
- Returns - Number: the smallest number  
- Example:  
```kt  
Math.min(5, 2);  
```  
  
### `Math.round(num)`  
- Description: Rounds a number to the nearest integer  
- Parameter - Number (`num`): the number to round  
- Returns - Number: the rounded number  
- Example:  
```kt  
Math.round(3.5);  
```  
  
### `Math.arctan(num)`  
- Description: Returns the arc tangent of a number  
- Parameter - Number (`num`): the number to get the arc tangent of  
- Returns - Number: the arc tangent of the number  
- Example:  
```kt  
Math.arctan(Math.tan(Math.pi));  
```  
  
### `Math.sqrt(num)`  
- Description: Returns the square root of a number  
- Parameter - Number (`num`): the number to square root  
- Returns - Number: the square root of the number  
- Example:  
```kt  
Math.sqrt(9);  
```  
  
### `Math.sin(num)`  
- Description: Returns the sine of a number  
- Parameter - Number (`num`): the number to get the sine of  
- Returns - Number: the sine of the number  
- Example:  
```kt  
Math.sin(Math.pi);  
```  
  
### `Math.floor(num)`  
- Description: Rounds a number down to the nearest integer  
- Parameter - Number (`num`): the number to round  
- Returns - Number: the rounded number  
- Example:  
```kt  
Math.floor(3.5);  
```  
  
### `Math.arcsin(num)`  
- Description: Returns the arc sine of a number  
- Parameter - Number (`num`): the number to get the arc sine of  
- Returns - Number: the arc sine of the number  
- Example:  
```kt  
Math.arcsin(Math.sin(Math.pi));  
```  
  
### `Math.clamp(value, min, max)`  
- Description: Clamps a value between a minimum and maximum  
- Parameters:  
  - Number (`value`): the value to clamp  
  - Number (`min`): the minimum  
  - Number (`max`): the maximum  
- Returns - Number: the clamped value  
- Example:  
```kt  
Math.clamp(10, 2, 8);  
```  
  
  
# Network class  
Network class for Arucas.  
  
Allows you to do http requests. This is a utility class and cannot be constructed.  
  
Import with `import Network from util.Network;`  
  
Fully Documented.  
  
## Static Methods  
  
### `Network.downloadFile(url, file)`  
- Description: Downloads a file from an url to a file  
- Parameters:  
  - String (`url`): the url to download from  
  - File (`file`): the file to download to  
- Returns - Boolean: whether the download was successful  
- Example:  
```kt  
Network.downloadFile('https://arucas.com', new File('dir/downloads'));  
```  
  
### `Network.openUrl(url)`  
- Description: Opens an url in the default browser  
- Parameter - String (`url`): the url to open  
- Throws - Error:  
  - `'Failed to open url ...'`  
- Example:  
```kt  
Network.openUrl('https://google.com');  
```  
  
### `Network.requestUrl(url)`  
- Description: Requests an url and returns the response  
- Parameter - String (`url`): the url to request  
- Returns - String: the response from the url  
- Throws - Error:  
  - `'Failed to request data from ...'`  
- Example:  
```kt  
Network.requestUrl('https://google.com');  
```  
  
  
# Null class  
Null class for Arucas.  
  
This class cannot be constructed since null has a literal `null`.  
  
Class does not need to be imported.  
  
Fully Documented.  
  
  
  
# Number class  
Number class for Arucas.  
  
This class cannot be constructed as it has a literal representation. For math related functions see the Math class.  
  
Class does not need to be imported.  
  
Fully Documented.  
  
## Methods  
  
### `<Number>.isInfinite()`  
- Description: This allows you to check if a number is infinite  
- Returns - Boolean: true if the number is infinite  
- Example:  
```kt  
(1/0).isInfinite();  
```  
  
### `<Number>.round()`  
- Description: This allows you to round a number to the nearest integer  
- Returns - Number: the rounded number  
- Example:  
```kt  
3.5.round();  
```  
  
### `<Number>.absolute()`  
- Deprecated: You should use `Math.abs(num)`  
- Description: This allows you to get the absolute value of a number  
- Returns - Number: the absolute value of the number  
- Example:  
```kt  
(-5).absolute();  
```  
  
### `<Number>.toDegrees()`  
- Deprecated: You should use `Math.toDegrees(num)`  
- Description: This allows you to convert a number in radians to degrees  
- Returns - Number: the number in degrees  
- Example:  
```kt  
Math.pi.toDegrees();  
```  
  
### `<Number>.toRadians()`  
- Deprecated: You should use `Math.toRadians(num)`  
- Description: This allows you to convert a number in degrees to radians  
- Returns - Number: the number in radians  
- Example:  
```kt  
5.toRadians();  
```  
  
### `<Number>.ceil()`  
- Description: This allows you to round a number up to the nearest integer  
- Returns - Number: the rounded number  
- Example:  
```kt  
3.5.ceil();  
```  
  
### `<Number>.isNaN()`  
- Description: This allows you to check if a number is not a number  
- Returns - Boolean: true if the number is not a number  
- Example:  
```kt  
(0/0).isNaN();  
```  
  
### `<Number>.floor()`  
- Description: This allows you to round a number down to the nearest integer  
- Returns - Number: the rounded number  
- Example:  
```kt  
3.5.floor();  
```  
  
### `<Number>.modulus(otherNumber)`  
- Deprecated: You should use `Math.mod(num1, num2)`  
- Description: This allows you to get the modulus of two numbers  
- Parameter - Number (`otherNumber`): the divisor  
- Returns - Number: the modulus of the two numbers  
- Example:  
```kt  
5.modulus(2);  
```  
  
  
  
# Object class  
Object class for Arucas.  
  
This is the base class for every other class in Arucas.  
  
Class does not need to be imported.  
  
Fully Documented.  
  
## Methods  
  
### `<Object>.hashCode()`  
- Description: This returns the hashcode of the value, this is mainly used for maps and sets  
- Returns - Number: the hashcode of the value  
- Example:  
```kt  
'thing'.hashCode();  
```  
  
### `<Object>.equals(other)`  
- Deprecated: You should use '=='  
- Description: This checks whether the value is equal to another value  
- Parameter - Value (`other`): the other value you want to check against  
- Returns - Boolean: whether the values are equal  
- Example:  
```kt  
10.equals(20);  
```  
  
### `<Object>.toString()`  
- Description: This returns the string representation of the value  
- Returns - String: the string representation of the value  
- Example:  
```kt  
[10, 11, 12].toString();  
```  
  
### `<Object>.copy()`  
- Description: This returns a copy of the value, some values might just return themselves  
- Returns - Value: the copy of the value  
- Example:  
```kt  
10.copy();  
```  
  
### `<Object>.getValueType()`  
- Deprecated: You should use 'Type.of(<Value>).getName()'  
- Description: This returns the name of the type of the value  
- Returns - String: the name of the type of value  
- Example:  
```kt  
10.getValueType();  
```  
  
### `<Object>.instanceOf(type)`  
- Description: This checks whether this value is an instance of another type  
- Parameter - Type (`type`): the other type you want to check against  
- Returns - Boolean: whether the value is of that type  
- Example:  
```kt  
10.instanceOf(String.type);  
```  
  
  
  
# Set class  
Set class for Arucas.  
  
Sets are collections of unique values. Similar to maps, without the values.  
An instance of the class can be created by using `Set.of(values...)`  
  
Class does not need to be imported.  
  
Fully Documented.  
  
## Methods  
  
### `<Set>.add(value)`  
- Description: This allows you to add a value to the set  
- Parameter - Value (`value`): the value you want to add to the set  
- Returns - Boolean: whether the value was successfully added to the set  
- Example:  
```kt  
Set.of().add('object');  
```  
  
### `<Set>.contains(value)`  
- Description: This allows you to check whether a value is in the set  
- Parameter - Value (`value`): the value that you want to check in the set  
- Returns - Boolean: whether the value is in the set  
- Example:  
```kt  
Set.of('object').contains('object');  
```  
  
### `<Set>.addAll(collection)`  
- Description: This allows you to add all the values in a collection into the set  
- Parameter - Collection (`collection`): the collection of values you want to add  
- Returns - Set: the modified set  
- Throws - Error:  
  - `'... is not a collection'`  
- Example:  
```kt  
Set.of().addAll(Set.of('object', 81, 96, 'case'));  
```  
  
### `<Set>.containsAll(collection)`  
- Description: This allows you to check whether a collection of values are all in the set  
- Parameter - Collection (`collection`): the collection of values you want to check in the set  
- Returns - Boolean: whether all the values are in the set  
- Throws - Error:  
  - `'... is not a collection'`  
- Example:  
```kt  
Set.of('object').containsAll(Set.of('object', 81, 96, 'case'));  
```  
  
### `<Set>.get(value)`  
- Description: This allows you to get a value from in the set.  
The reason this might be useful is if you want to retrieve something  
from the set that will have the same hashcode but be in a different state  
as the value you are passing in  
- Parameter - Value (`value`): the value you want to get from the set  
- Returns - Value: the value you wanted to get, null if it wasn't in the set  
- Example:  
```kt  
Set.of('object').get('object');  
```  
  
### `<Set>.clear()`  
- Description: This removes all values from inside the set  
- Example:  
```kt  
Set.of('object').clear();  
```  
  
### `<Set>.isEmpty()`  
- Description: This allows you to check whether the set has no values  
- Returns - Boolean: whether the set is empty  
- Example:  
```kt  
Set.of().isEmpty();  
```  
  
### `<Set>.toString()`  
- Description: This converts the set to a string and evaluating any collections inside it  
- Returns - String: the string representation of the set  
- Example:  
```kt  
Set.of('object').toString();  
```  
  
### `<Set>.remove(value)`  
- Description: This allows you to remove a value from the set  
- Parameter - Value (`value`): the value you want to remove from the set  
- Returns - Boolean: whether the value was removed from the set  
- Example:  
```kt  
Set.of('object').remove('object');  
```  
  
## Static Methods  
  
### `Set.unordered()`  
- Description: This creates an unordered set  
- Returns - Set: the unordered set  
- Example:  
```kt  
Set.unordered();  
```  
  
### `Set.of(values...)`  
- Description: This allows you to create a set with an arbitrary number of values  
- Parameter - Value (`values...`): the values you want to add to the set  
- Returns - Set: the set you created  
- Example:  
```kt  
Set.of('object', 81, 96, 'case');  
```  
  
  
# String class  
String class for Arucas.  
  
This class cannot be constructed since strings have a literal. Strings are immutable.  
  
Class does not need to be imported.  
  
Fully Documented.  
  
## Methods  
  
### `<String>.uppercase()`  
- Description: This makes the string uppercase  
- Returns - String: the uppercase string  
- Example:  
```kt  
'hello'.uppercase();  
```  
  
### `<String>.lowercase()`  
- Description: This makes the string lowercase  
- Returns - String: the lowercase string  
- Example:  
```kt  
'HELLO'.lowercase();  
```  
  
### `<String>.format(values...)`  
- Description: This formats the string with the given parameters, which replace '%s' in the string  
- Parameter - Value (`values...`): the values to add, these will be converted to strings  
- Returns - String: the formatted string  
- Throws - Error:  
  - `'You are missing values to be formatted'`  
- Example:  
```kt  
'%s %s'.format('hello', 'world');  
```  
  
### `<String>.toList()`  
- Description: This makes a list of all the characters in the string  
- Returns - List: the list of characters  
- Example:  
```kt  
'hello'.toList();  
```  
  
### `<String>.matches(regex)`  
- Description: This checks if the string matches the given regex  
- Parameter - String (`regex`): the regex to check the string with  
- Returns - Boolean: true if the string matches the given regex  
- Example:  
```kt  
'hello'.matches('[a-z]*');  
```  
  
### `<String>.replaceAll(regex, replace)`  
- Description: This replaces all the instances of a regex with the replace string  
- Parameters:  
  - String (`regex`): the regex you want to replace  
  - String (`replace`): the string you want to replace it with  
- Returns - String: the modified string  
- Example:  
```kt  
'hello'.replaceAll('l', 'x');  
```  
  
### `<String>.capitalise()`  
- Description: This capitalises the first letter of the string  
- Returns - String: the capitalised string  
- Example:  
```kt  
'foo'.capitalise();  
```  
  
### `<String>.contains(string)`  
- Description: This checks if the string contains the given string  
- Parameter - String (`string`): the string you want to check for  
- Returns - Boolean: true if the string contains the given string  
- Example:  
```kt  
'hello'.contains('he');  
```  
  
### `<String>.split(regex)`  
- Description: This splits the string into a list of strings based on a regex  
- Parameter - String (`regex`): the regex to split the string with  
- Returns - List: the list of strings  
- Example:  
```kt  
'foo/bar/baz'.split('/');  
```  
  
### `<String>.strip()`  
- Description: This strips the whitespace from the string  
- Returns - String: the stripped string  
- Example:  
```kt  
'  hello  '.strip();  
```  
  
### `<String>.subString(from, to)`  
- Description: This returns a substring of the string  
- Parameters:  
  - Number (`from`): the start index  
  - Number (`to`): the end index  
- Returns - String: the substring  
- Example:  
```kt  
'hello'.subString(1, 3);  
```  
  
### `<String>.find(regex)`  
- Description: This finds all instances of the regex in the string  
- Parameter - String (`regex`): the regex to search the string with  
- Returns - List: the list of all instances of the regex in the string  
- Example:  
```kt  
'hello'.find('[a-z]*');  
```  
  
### `<String>.endsWith(string)`  
- Description: This checks if the string ends with the given string  
- Parameter - String (`string`): the string to check the string with  
- Returns - Boolean: true if the string ends with the given string  
- Example:  
```kt  
'hello'.endsWith('he');  
```  
  
### `<String>.toNumber()`  
- Description: This tries to convert the string to a number  
- Returns - Number: the number value  
- Throws - Error:  
  - `'Cannor parse ... as a number'`  
- Example:  
```kt  
'0xFF'.toNumber();  
```  
  
### `<String>.startsWith(string)`  
- Description: This checks if the string starts with the given string  
- Parameter - String (`string`): the string to check the string with  
- Returns - Boolean: true if the string starts with the given string  
- Example:  
```kt  
'hello'.startsWith('he');  
```  
  
  
  
# Thread class  
Thread class for Arucas.  
  
This class allows you to create threads for asynchronous execution.  
  
Class does not need to be imported.  
  
Fully Documented.  
  
## Methods  
  
### `<Thread>.getAge()`  
- Description: This gets the age of the thread in milliseconds  
- Returns - Number: the age of the thread  
- Example:  
```kt  
Thread.getCurrentThread().getAge();  
```  
  
### `<Thread>.isAlive()`  
- Description: This checks if the thread is alive (still running)  
- Returns - Boolean: true if the thread is alive, false if not  
- Example:  
```kt  
Thread.getCurrentThread().isAlive();  
```  
  
### `<Thread>.getName()`  
- Description: This gets the name of the thread  
- Returns - String: the name of the thread  
- Example:  
```kt  
Thread.getCurrentThread().getName();  
```  
  
### `<Thread>.stop()`  
- Description: This stops the thread from executing, anything that was running will be instantly stopped  
- Throws - Error:  
  - `'Thread is not alive'`  
- Example:  
```kt  
Thread.getCurrentThread().stop();  
```  
  
## Static Methods  
  
### `Thread.freeze()`  
- Description: This freezes the current thread, stops anything else from executing on the thread  
- Example:  
```kt  
Thread.freeze();  
```  
  
### `Thread.runThreaded(function)`  
- Description: This starts a new thread and runs a function on it, the thread will   
terminate when it finishes executing the function, threads will stop automatically   
when the program stops, you are also able to stop threads by using the Thread value  
- Parameter - Function (`function`): the function you want to run on a new thread  
- Returns - Thread: the new thread  
- Example:  
```kt  
Thread.runThreaded(fun() {  
 print("Running asynchronously!");});  
```  
  
### `Thread.runThreaded(name, function)`  
- Description: This starts a new thread with a specific name and runs a function on it  
- Parameters:  
  - String (`name`): the name of the thread  
  - Function (`function`): the function you want to run on a new thread  
- Returns - Thread: the new thread  
- Example:  
```kt  
Thread.runThreaded("MyThread", fun() {  
 print("Running asynchronously on MyThread!");});  
```  
  
### `Thread.getCurrentThread()`  
- Description: This gets the current thread that the code is running on  
- Returns - Thread: the current thread  
- Throws - Error:  
  - `'Thread is not safe to get'`  
- Example:  
```kt  
Thread.getCurrentThread();  
```  
  
  
# Type class  
Type class for Arucas.  
  
This class lets you get the type of a class or value.  
  
Class does not need to be imported.  
  
Fully Documented.  
  
## Methods  
  
### `<Type>.getStaticMethod(name, parameters)`  
- Description: This gets the static method of the type  
- Parameters:  
  - String (`name`): the name of the method  
  - Number (`parameters`): the number of parameters for the method  
- Returns - Function: the static method of the type  
- Example:  
```kt  
String.type.getStaticMethod('nonExistent', 0);  
```  
  
### `<Type>.getName()`  
- Description: This gets the name of the type  
- Returns - String: the name of the type  
- Example:  
```kt  
String.type.getName();  
```  
  
### `<Type>.getConstructor(parameters)`  
- Description: This gets the constructor of the type  
- Parameter - Number (`parameters`): the number of parameters for the constructor  
- Returns - Function: the constructor of the type  
- Example:  
```kt  
String.type.getConstructor(0);  
```  
  
### `<Type>.instanceOf(type)`  
- Description: This checks whether a type is a subtype of another type  
- Parameter - Type (`type`): the other type you want to check against  
- Returns - Boolean: whether the type is of that type  
- Example:  
```kt  
Type.of('').instanceOf(Number.type);  
```  
  
## Static Methods  
  
### `Type.of(value)`  
- Description: This gets the specific type of a value  
- Parameter - Value (`value`): the value you want to get the type of  
- Returns - Type: the type of the value  
- Example:  
```kt  
Type.of(0);  
```

#

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

#

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
All annotated methods' first parameter **must** be of type `Context`, after will be the parameters passed in by Arucas to call that method. Methods must return `Value` or a subclass of `Value`, you can return the class type to be able to return `this`, returning void is also valid, it just returns `NullValue.NULL`.
All annotated fields **must** be of type `Value`, unless the field is final since Arucas is not statically typed and you will be able to assign any `Value` to a field. Final fields are allowed, they will be able to be accessed in Arucas but not be able to be assigned to.
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
