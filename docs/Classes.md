# Boolean class
Boolean class for Arucas

This is the boolean type, representing either true or false.
This class cannot be instantiated, or extended
Class does not need to be imported



# Collection class
Collection class for Arucas

This class is used to represent a collection of objects,
this class is used internally as the parent of maps, lists, and sets.
This cannot be instantiated directly.
All collections inherit Iterable, and thus can be iterated over
Class does not need to be imported

## Constructors

### `new Collection()`
- Description: This creates a collection, this cannot be called directly, only from child classes
- Example:
```kotlin
class ChildCollection: Collection {
    ChildCollection(): super();
    
    fun size() {
        return 0;
    }
}
```

## Methods

### `<Collection>.isEmpty()`
- Description: This allows you to check if the collection is empty
- Returns - Boolean: true if the collection is empty
- Example:
```kotlin
['object', 81, 96, 'case'].isEmpty(); // false
```

### `<Collection>.size()`
- Description: This allows you to get the size of the collection
- Returns - Number: the size of the list
- Example:
```kotlin
['object', 81, 96, 'case'].size();
```



# Enum class
Enum class for Arucas

This class is the super class of all enums in Arucas.
Enums cannot be instantiated or extended
Class does not need to be imported

## Methods

### `<Enum>.getName()`
- Description: This allows you to get the name of an enum value
- Returns - String: the name of the enum value
- Example:
```kotlin
enum.getName();
```

### `<Enum>.ordinal()`
- Description: This allows you to get the ordinal of the enum value
- Returns - Number: the ordinal of the enum value
- Example:
```kotlin
enum.ordinal();
```



# Error class
Error class for Arucas

This class is used for errors, and this is the only type that can be thrown.
You are able to extend this class to create your own error types
Class does not need to be imported

## Constructors

### `new Error()`
- Description: This creates a new Error value with no message
- Example:
```kotlin
new Error();
```

### `new Error(details)`
- Description: This creates a new Error value with the given details as a message
- Parameter - String (`details`): the details of the error
- Example:
```kotlin
new Error('This is an error');
```

### `new Error(details, value)`
- Description: This creates a new Error value with the given details as a message and the given value
- Parameters:
  - String (`details`): the details of the error
  - Object (`value`): the value that is related to the error
- Example:
```kotlin
new Error('This is an error', [1, 2, 3]);
```

## Methods

### `<Error>.getDetails()`
- Description: This returns the raw message of the error
- Returns - String: the details of the error
- Example:
```kotlin
error.getDetails();
```

### `<Error>.getValue()`
- Description: This returns the value that is related to the error
- Returns - Object: the value that is related to the error
- Example:
```kotlin
error.getValue();
```



# File class
File class for Arucas

This class allows you to read and write files
Class does not need to be imported

## Constructors

### `new File(path)`
- Description: This creates a new File object with set path
- Parameter - String (`path`): the path of the file
- Example:
```kotlin
new File('foo/bar/script.arucas');
```

## Methods

### `<File>.createDirectory()`
- Description: This creates all parent directories of the file if they don't already exist
- Returns - Boolean: true if the directories were created
- Example:
```kotlin
file.createDirectory();
```

### `<File>.delete()`
- Description: This deletes the file
- Returns - Boolean: true if the file was deleted
- Example:
```kotlin
file.delete();
```

### `<File>.exists()`
- Description: This returns if the file exists
- Returns - Boolean: true if the file exists
- Example:
```kotlin
file.exists();
```

### `<File>.getAbsolutePath()`
- Description: This returns the absolute path of the file
- Returns - String: the absolute path of the file
- Example:
```kotlin
file.getAbsolutePath();
```

### `<File>.getName()`
- Description: This returns the name of the file
- Returns - String: the name of the file
- Example:
```kotlin
File.getName();
```

### `<File>.getPath()`
- Description: This returns the path of the file
- Returns - String: the path of the file
- Example:
```kotlin
file.getPath();
```

### `<File>.getSubFiles()`
- Description: This returns a list of all the sub files in the directory
- Returns - List: a list of all the sub files in the directory
- Example:
```kotlin
file.getSubFiles();
```

### `<File>.open()`
- Description: This opens the file (as in opens it on your os)
- Example:
```kotlin
file.open();
```

### `<File>.read()`
- Description: This reads the file and returns the contents as a string
- Returns - String: the contents of the file
- Example:
```kotlin
file.read();
```

### `<File>.resolve(filePath)`
- Description: This gets a resolves file object from the current one
- Parameter - String (`filePath`): the relative file path
- Returns - File: the resolved file
- Example:
```kotlin
file.resolve('child.txt');
```

### `<File>.write(string)`
- Description: This writes a string to a file
- Parameter - String (`string`): the string to write to the file
- Example:
```kotlin
file.write('Hello World!');
```

## Static Methods

### `File.getDirectory()`
- Description: This returns the file of the working directory
- Returns - File: the file of the working directory
- Example:
```kotlin
File.getDirectory();
```



# Function class
Function class for Arucas

This class is used for functions, and this is the only type that can be called.
You are able to extend this class and implement an 'invoke' method to create
your own function types, this class cannot be instantiated directly
Class does not need to be imported

## Constructors

### `new Function()`
- Description: This creates a function, this cannot be called directly, only from child classes
- Example:
```kotlin
class ChildFunction: Function {
    ChildFunction(): super();
}
```



# Future class
Future class for Arucas

This class is used to represent values that are in the future.
More precisely values that are being evaluated on another thread,
this allows you to access those values once they've been processed
Class does not need to be imported

## Methods

### `<Future>.await()`
- Description: This blocks the current thread until the future has
been completed and then returns the value of the future
- Returns - Object: The value of the future
- Example:
```kotlin
future.await();
```

### `<Future>.isComplete()`
- Description: This returns whether the future has been completed
- Returns - Boolean: Whether the future has been completed
- Example:
```kotlin
future.isComplete();
```

## Static Methods

### `Future.completed(value)`
- Description: This returns a future that with a complete value
- Parameter - Object (`value`): The value to complete the future with
- Returns - Future: The future that has been completed with the value
- Example:
```kotlin
future = Future.completed(true);
```



# Iterable class
Iterable class for Arucas

This class represents an object that can be iterated over.
This class is used internally to denote whether an object can be
iterated over inside a foreach loop
Class does not need to be imported

## Constructors

### `new Iterable()`
- Description: This creates an iterable, this cannot be called directly, only from child classes
- Example:
```kotlin
class IterableImpl: Iterable {
    IterableImpl(): super();
    
    fun iterator() {
        // Example
        return [].iterator();
    }
}
```

## Methods

### `<Iterable>.iterator()`
- Description: This gets the generated iterator
- Returns - Iterator: the generated iterator
- Example:
```kotlin
iterable = [];
i = iterable.iterator();
while (i.hasNext()) {
    next = i.next();
}

// Or just, compiles to above
foreach (next : iterable); 
```



# Iterator class
Iterator class for Arucas

This class represents an object that iterates.
This is what is used internally to iterate in a
foreach loop and you can create your own iterators
to use be able to use them inside a foreach
Class does not need to be imported

## Constructors

### `new Iterator()`
- Description: This creates an iterator, this cannot be called directly, only from child classes
- Example:
```kotlin
class IteratorImpl: Iterator {
    IteratorImpl(): super();
    
    fun hasNext() {
        return false;
    }
    
    fun next() {
        throw new Error("Nothing next");
    }
}
```

## Methods

### `<Iterator>.hasNext()`
- Description: Checks whether the iterator has a next item to iterate
- Returns - Boolean: whether there are items left to iterate
- Example:
```kotlin
iterator = [].iterator();
iterator.hasNext();
```

### `<Iterator>.next()`
- Description: Gets the next item in the iterator, may throw if there is no next item
- Returns - Object: the next item
- Example:
```kotlin
iterator = [10, 20].iterator();
iterator.next(); // 10
iterator.next(); // 20
```



# Java class
Java class for Arucas

This class wraps Java values allowing for interactions between Java and Arucas.
This class cannot be instantiated or extended but you can create Java values by
using the static method 'Java.valueOf()' to convert Arucas to Java
Import with `import Java from util.Internal;`

## Methods

### `<Java>.callMethod(methodName, parameters...)`
- Deprecated: You should call the method directly on the value: Java.valueOf('').isBlank();
- Description: This calls the specified method with the specified parameters, calling the method
with this function has no benefits unless you are calling a function that also is
native to Arucas. For example `object.copy()` will use the Arucas 'copy' function.
But this is extremely rare so almost all of the time you should all the method normally.
- Parameters:
  - String (`methodName`): the name of the method
  - Object (`parameters...`): the parameters to call the method with
- Returns - Java: the return value of the method call wrapped in the Java wrapper
- Example:
```kotlin
Java.valueOf('').callMethod('isBlank');
```

### `<Java>.getField(fieldName)`
- Deprecated: You should call the method directly on the value: `Java.constructClass('me.senseiwells.impl.Test').A;`
- Description: This returns the Java wrapped value of the specified field.
There is no reason for you to be using this method, it will be removed in future versions
- Parameter - String (`fieldName`): the name of the field
- Returns - Java: the Java wrapped value of the field
- Example:
```kotlin
Java.constructClass('me.senseiwells.impl.Test').getField('A');
```

### `<Java>.getMethodDelegate(methodName, parameters)`
- Deprecated: Consider wrapping the method in a lambda instead
- Description: This returns a method delegate for the specified method name and parameters.
This should be avoided and replaced with a Arucas function wrapping the call instead.
For example: `delegate = (fun() { Java.valueOf('').isBlank(); });`.
Another thing to note is that the parameter count parameter is no longer
used and ignored internally, instead the parameters are calculated when you
call the delegate. The parameter remains for backwards compatability.
- Parameters:
  - String (`methodName`): the name of the method
  - Number (`parameters`): the number of parameters
- Returns - Function: the function containing the Java method delegate
- Example:
```kotlin
Java.valueOf('string!').getMethodDelegate('isBlank', 0);
```

### `<Java>.setField(fieldName, value)`
- Deprecated: You should assign the value directly on the value: Java.constructClass('me.senseiwells.impl.Test').A = 'Hello';
- Description: This sets the specified field to the specified value
There is no reason for you to be using this method, it will be removed in future versions
- Parameters:
  - String (`fieldName`): the name of the field
  - Object (`value`): the value to set the field to, the value type must match the type of the field
- Example:
```kotlin
Java.constructClass('me.senseiwells.impl.Test').setField('A', 'Hello');
```

### `<Java>.toArucas()`
- Description: This converts the Java value to an Arucas Value if possible, this may still
be of a Java value if it cannot be converted. For example, Strings, Numbers, Lists
will be converted but 
- Returns - Object: the Value in Arucas, this may still be of Java value if the value cannot be converted into an Arucas value, values like Strings, Numbers, Lists, etc... will be converted
- Example:
```kotlin
Java.valueOf([1, 2, 3]).toArucas();
```

## Static Methods

### `Java.arrayOf(values...)`
- Description: Creates a Java Object array with a given values, this will be the size of the array,
this cannot be used to create primitive arrays
- Parameter - Object (`values...`): the values to add to the array
- Returns - Java: the Java Object array
- Example:
```kotlin
Java.arrayOf(1, 2, 3, 'string!', false);
```

### `Java.booleanArray(size)`
- Description: Creates a Java boolean array with a given size, the array is filled with false
by default and can be filled with only booleans
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java boolean array
- Example:
```kotlin
Java.booleanArray(10);
```

### `Java.booleanOf(bool)`
- Description: Creates a Java value boolean, to be used in Java
- Parameter - Boolean (`bool`): the boolean to convert to a Java boolean
- Returns - Java: the boolean in Java wrapper
- Example:
```kotlin
Java.booleanOf(true);
```

### `Java.byteArray(size)`
- Description: Creates a Java byte array with a given size, the array is filled with 0's
by default and can be filled with only bytes
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java byte array
- Example:
```kotlin
Java.byteArray(10);
```

### `Java.byteOf(num)`
- Description: Creates a Java value byte, to be used in Java
- Parameter - Number (`num`): the number to convert to a Java byte
- Returns - Java: the byte in Java wrapper
- Example:
```kotlin
Java.byteOf(1);
```

### `Java.callStaticMethod(className, methodName, parameters...)`
- Deprecated: You should use 'Java.classOf(name)' then call the static method
- Description: Calls a static method of a Java class.
This should be avoided and instead use 'classOf' to get the
instance of the class then call the static method on that
- Parameters:
  - String (`className`): the name of the class
  - String (`methodName`): the name of the method
  - Object (`parameters...`): any parameters to call the method with
- Returns - Java: the return value of the method wrapped in the Java wrapper
- Example:
```kotlin
Java.callStaticMethod('java.lang.Integer', 'parseInt', '123');
```

### `Java.charArray(size)`
- Description: Creates a Java char array with a given size, the array is filled with null characters's
by default and can be filled with only chars
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java char array
- Example:
```kotlin
Java.charArray(10);
```

### `Java.charOf(char)`
- Description: Creates a Java value char, to be used in Java
- Parameter - String (`char`): the char to convert to a Java char
- Returns - Java: the char in Java wrapper
- Example:
```kotlin
Java.charOf('a');
```

### `Java.classFromName(className)`
- Deprecated: You should use 'Java.classOf(name)' instead
- Description: Gets a Java class from the name of the class
- Parameter - String (`className`): the name of the class you want to get
- Returns - Java: the Java Class<?> value wrapped in the Java wrapper
- Example:
```kotlin
Java.classFromName('java.util.ArrayList');
```

### `Java.classOf(className)`
- Description: Gets a Java class from the name of the class
- Parameter - String (`className`): the name of the class you want to get
- Returns - JavaClass: the Java class value which can be used as a class reference
- Example:
```kotlin
Java.classOf('java.util.ArrayList');
```

### `Java.constructClass(className, parameters...)`
- Deprecated: You should use 'Java.classOf(name)' then call the result to construct the class
- Description: This constructs a Java class with specified class name and parameters.
This should be avoided and instead use 'classOf' to get the class
instance then call the constructor on that instance
- Parameters:
  - String (`className`): the name of the class
  - Object (`parameters...`): any parameters to pass to the constructor
- Returns - Java: the constructed Java Object wrapped in the Java wrapper
- Example:
```kotlin
Java.constructClass('java.util.ArrayList');
```

### `Java.consumerOf(function)`
- Description: Creates a Java Consumer object from a given function, it must have one
parameter and any return values will be ignored
- Parameter - Function (`function`): the function to be executed
- Returns - Java: the Java Consumer object
- Example:
```kotlin
Java.consumerOf(fun(something) {
    print(something);
});
```

### `Java.doubleArray(size)`
- Description: Creates a Java double array with a given size, the array is filled with 0's
by default and can be filled with only doubles
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java double array
- Example:
```kotlin
Java.doubleArray(10);
```

### `Java.doubleOf(num)`
- Description: Creates a Java value double, to be used in Java
- Parameter - Number (`num`): the number to convert to a Java double
- Returns - Java: the double in Java wrapper
- Example:
```kotlin
Java.doubleOf(1.0);
```

### `Java.floatArray(size)`
- Description: Creates a Java float array with a given size, the array is filled with 0's
by default and can be filled with only floats
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java float array
- Example:
```kotlin
Java.floatArray(10);
```

### `Java.floatOf(num)`
- Description: Creates a Java value float, to be used in Java
- Parameter - Number (`num`): the number to convert to a Java float
- Returns - Java: the float in Java wrapper
- Example:
```kotlin
Java.floatOf(1.0);
```

### `Java.functionOf(function)`
- Description: Creates a Java Function object from a given function
- Parameter - Function (`function`): the function to be executed, this must have one parameter and must return a value
- Returns - Java: the Java Function object
- Example:
```kotlin
Java.functionOf(fun(something) {
    return something;
});
```

### `Java.getStaticField(className, fieldName)`
- Deprecated: You should use 'Java.classOf(name)' then access the static field
- Description: Gets a static field Java value from a Java class
- Parameters:
  - String (`className`): the name of the class
  - String (`fieldName`): the name of the field
- Returns - Java: the Java value of the field wrapped in the Java wrapper
- Example:
```kotlin
Java.getStaticField('java.lang.Integer', 'MAX_VALUE');
```

### `Java.getStaticMethodDelegate(className, methodName, parameters)`
- Deprecated: You should use 'Java.classOf(name)' then wrap the static method
- Description: Gets a static method delegate from a Java class, this should
be avoided and instance use 'classOf' to get the class instance
and then call the method on that class instance. The parameter count
parameter is no longer used internally but remains for backwards compatibility
- Parameters:
  - String (`className`): the name of the class
  - String (`methodName`): the name of the method
  - Number (`parameters`): the number of parameters
- Returns - Function: the delegated Java method in an Arucas Function
- Example:
```kotlin
Java.getStaticMethodDelegate('java.lang.Integer', 'parseInt', 1);
```

### `Java.intArray(size)`
- Description: Creates a Java int array with a given size, the array is filled with 0's
by default and can be filled with only ints
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java int array
- Example:
```kotlin
Java.intArray(10);
```

### `Java.intOf(num)`
- Description: Creates a Java value int, to be used in Java
- Parameter - Number (`num`): the number to convert to a Java int
- Returns - Java: the int in Java wrapper
- Example:
```kotlin
Java.intOf(1);
```

### `Java.longArray(size)`
- Description: Creates a Java long array with a given size, the array is filled with 0's
by default and can be filled with only longs
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java long array
- Example:
```kotlin
Java.longArray(10);
```

### `Java.longOf(num)`
- Description: Creates a Java value long, to be used in Java
- Parameter - Number (`num`): the number to convert to a Java long
- Returns - Java: the long in Java wrapper
- Example:
```kotlin
Java.longOf(1);
```

### `Java.objectArray(size)`
- Description: Creates a Java Object array with a given size, the array is filled with null values
by default and can be filled with any Java values, this array cannot be expanded
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java Object array
- Example:
```kotlin
Java.arrayWithSize(10);
```

### `Java.predicateOf(function)`
- Description: Creates a Java Predicate object from a given function
- Parameter - Function (`function`): the function to be executed, this must have one parameter and must return a boolean
- Returns - Java: the Java Predicate object
- Example:
```kotlin
Java.predicateOf(fun(something) {
    return something == 'something';
});
```

### `Java.runnableOf(function)`
- Description: Creates a Java Runnable object from a given function, this must
have no paramters and any return values will be ignored
- Parameter - Function (`function`): the function to be executed
- Returns - Java: the Java Runnable object
- Example:
```kotlin
Java.runnableOf(fun() {
    print('runnable');
});
```

### `Java.setStaticField(className, fieldName, newValue)`
- Deprecated: You should use 'Java.classOf(name)' then assign the static field
- Description: Sets a static field in a Java class with a new value
- Parameters:
  - String (`className`): the name of the class
  - String (`fieldName`): the name of the field
  - Object (`newValue`): the new value
- Example:
```kotlin
// Obviously this won't work, but it's just an example
Java.setStaticField('java.lang.Integer', 'MAX_VALUE', Java.intOf(100));"
```

### `Java.shortArray(size)`
- Description: Creates a Java short array with a given size, the array is filled with 0's
by default and can be filled with only shorts
- Parameter - Number (`size`): the size of the array
- Returns - Java: the Java short array
- Example:
```kotlin
Java.shortArray(10);
```

### `Java.shortOf(num)`
- Description: Creates a Java value short, to be used in Java
- Parameter - Number (`num`): the number to convert to a Java short
- Returns - Java: the short in Java wrapper
- Example:
```kotlin
Java.shortOf(1);
```

### `Java.supplierOf(function)`
- Description: Creates a Java Supplier object from a given function
- Parameter - Function (`function`): the function to be executed, this must have no parameters and must return (supply) a value
- Returns - Java: the Java Supplier object
- Example:
```kotlin
Java.supplierOf(fun() {
    return 'supplier';
});
```

### `Java.valueOf(value)`
- Description: Converts any Arucas value into a Java value then wraps it in the Java wrapper and returns it
- Parameter - Object (`value`): any value to get the Java value of
- Returns - Java: the Java wrapper value, null if argument was null
- Example:
```kotlin
Java.valueOf('Hello World!');
```



# JavaClass class
JavaClass class for Arucas

This class 'acts' as a Java class. You are able to call this class which
will invoke the Java class' constructor, and access and assign the static
fields of the class. This class cannot be instantiated or extended.
Import with `import JavaClass from util.Internal;`



# Json class
Json class for Arucas

This class allows you to create and manipulate JSON objects.
This class cannot be instantiated or extended
Import with `import Json from util.Json;`

## Methods

### `<Json>.getValue()`
- Description: This converts the Json back into an object
- Returns - Object: the Value parsed from the Json
- Example:
```kotlin
json.getValue();
```

### `<Json>.writeToFile(file)`
- Description: This writes the Json to a file
if the file given is a directory or cannot be
written to, an error will be thrown
- Parameter - File (`file`): the file that you want to write to
- Example:
```kotlin
json.writeToFile(new File('D:/cool/realDirectory'));
```

## Static Methods

### `Json.fromFile(file)`
- Description: This will read a file and parse it into a Json
- Parameter - File (`file`): the file that you want to parse into a Json
- Returns - Json: the Json parsed from the file
- Example:
```kotlin
Json.fromFile(new File('this/path/is/an/example.json'));
```

### `Json.fromList(list)`
- Description: This converts a list into a Json, an important thing to note is that
any values that are not Numbers, Booleans, Lists, Maps, or Null will use their
toString() member to convert them to a string
- Parameter - List (`list`): the list that you want to parse into a Json
- Returns - Json: the Json parsed from the list
- Example:
```kotlin
Json.fromList(['value', 1, true]);
```

### `Json.fromMap(map)`
- Description: This converts a map into a Json, an important thing to note is that
any values that are not Numbers, Booleans, Lists, Maps, or Null will use their
toString() member to convert them to a string
- Parameter - Map (`map`): the map that you want to parse into a Json
- Returns - Json: the Json parsed from the map
- Example:
```kotlin
Json.fromMap({'key': ['value1', 'value2']});
```

### `Json.fromString(string)`
- Description: This converts a string into a Json provided it is formatted correctly,
otherwise throwing an error
- Parameter - String (`string`): the string that you want to parse into a Json
- Returns - Json: the Json parsed from the string
- Example:
```kotlin
Json.fromString('{"key":"value"}');
```



# List class
List class for Arucas

This class is used for collections of ordered elements
Class does not need to be imported

## Constructors

### `new List()`
- Description: This creates a list, this cannot be called directly, only from child classes
- Example:
```kotlin
class ChildList: List {
    ChildList(): super();
}
```

## Methods

### `<List>.addAll(collection)`
- Description: This allows you to add all the values in another collection to the list
- Parameter - Collection (`collection`): the collection you want to add to the list
- Returns - List: the list
- Example:
```kotlin
['object', 81, 96, 'case'].addAll(['foo', 'object']); // ['object', 81, 96, 'case', 'foo', 'object']
```

### `<List>.append(value)`
- Description: This allows you to append a value to the end of the list
- Parameter - Object (`value`): the value you want to append
- Returns - List: the list
- Example:
```kotlin
['object', 81, 96, 'case'].append('foo'); // ['object', 81, 96, 'case', 'foo']
```

### `<List>.clear()`
- Description: This allows you to clear the list
- Example:
```kotlin
['object', 81, 96, 'case'].clear(); // []
```

### `<List>.contains(value)`
- Description: This allows you to check if the list contains a specific value
- Parameter - Object (`value`): the value you want to check
- Returns - Boolean: true if the list contains the value
- Example:
```kotlin
['object', 81, 96, 'case'].contains('case'); // true
```

### `<List>.containsAll(collection)`
- Description: This allows you to check if the list contains all the values in another collection
- Parameter - Collection (`collection`): the collection you want to check agains
- Returns - Boolean: true if the list contains all the values in the collection
- Example:
```kotlin
['object', 81, 96, 'case'].containsAll(['foo', 'object']); // false
```

### `<List>.filter(predicate)`
- Description: This filters the list using the predicate, a function that either returns
true or false, based on the element on whether it should be kept or not,
and returns a new list with the filtered elements
- Parameter - Function (`predicate`): a function that takes a value and returns Boolean
- Returns - List: the filtered collection
- Example:
```kotlin
(list = [1, 2, 3]).filter(fun(v) {
    return v > 1;
});
// list = [2, 3]
```

### `<List>.flatten()`
- Description: If there are any objects in the list that are collections they will
be expanded and added to the list. However collections inside those
collections will not be flattened, this is returned as a new list
- Returns - List: the flattened list
- Example:
```kotlin
(list = [1, 2, 3, [4, 5], [6, [7]]]).flatten();
// list = [1, 2, 3, 4, 5, 6, [7]]
```

### `<List>.get(index)`
- Description: This allows you to get the value at a specific index, alternative to bracket accessor,
this will throw an error if the index given is out of bounds
- Parameter - Number (`index`): the index of the value you want to get
- Returns - Object: the value at the index
- Example:
```kotlin
['object', 81, 96, 'case'].get(1); // 81
```

### `<List>.indexOf(value)`
- Description: This allows you to get the index of a specific value
- Parameter - Object (`value`): the value you want to get the index of
- Returns - Number: the index of the value
- Example:
```kotlin
['object', 81, 96, 'case', 81].indexOf(81); // 1
```

### `<List>.insert(value, index)`
- Description: This allows you to insert a value at a specific index, this will throw an error if the index is out of bounds
- Parameters:
  - Object (`value`): the value you want to insert
  - Number (`index`): the index you want to insert the value at
- Returns - List: the list
- Example:
```kotlin
['object', 81, 96, 'case'].insert('foo', 1); // ['object', 'foo', 81, 96, 'case']
```

### `<List>.lastIndexOf(value)`
- Description: This allows you to get the last index of a specific value
- Parameter - Object (`value`): the value you want to get the last index of
- Returns - Number: the last index of the value
- Example:
```kotlin
['object', 81, 96, 'case', 96].lastIndexOf(96); // 4
```

### `<List>.map(mapper)`
- Description: This maps the list using the mapper, a function that takes a value and
returns a new value, and returns a new list with the mapped elements
- Parameter - Function (`mapper`): a function that takes a value and returns a new value
- Returns - List: the mapped collection
- Example:
```kotlin
(list = [1, 2, 3]).map(fun(v) {
    return v * 2;
});
// list = [2, 4, 6]
```

### `<List>.prepend(value)`
- Description: This allows you to prepend a value to the beginning of the list
- Parameter - Object (`value`): the value you want to prepend
- Returns - List: the list
- Example:
```kotlin
['object', 81, 96].prepend('foo'); // ['foo', 'object', 81, 96]
```

### `<List>.reduce(reducer)`
- Description: This reduces the list using the reducer, a function that takes an
accumulated value and a new value and returns the next accumulated value
- Parameter - Function (`reducer`): a function that takes a value and returns a new value
- Returns - Object: the reduced value
- Example:
```kotlin
// a will start at 1 and b at 2
// next accumulator will be 3
// a will be 3 and b will be 3 = 6
(list = [1, 2, 3]).reduce(fun(a, b) {
    return a + b;
});
// 6
```

### `<List>.remove(index)`
- Description: This allows you to remove the value at a specific index, alternative to bracket assignment.
This will throw an error if the index is out of bounds
- Parameter - Number (`index`): the index of the value you want to remove
- Returns - Object: the value that was removed
- Example:
```kotlin
['object', 81, 96, 'case'].remove(1); // 81
```

### `<List>.removeAll(collection)`
- Description: This allows you to remove all the values in another collection from the list
- Parameter - Collection (`collection`): the collection you want to remove from the list
- Returns - List: the list
- Example:
```kotlin
['object', 81, 96, 'case'].removeAll(['foo', 'object']); // [81, 96, 'case']
```

### `<List>.retainAll(list)`
- Description: This allows you to retain only the values that are in both lists
- Parameter - List (`list`): the list you want to retain values from
- Returns - List: the list
- Example:
```kotlin
['object', 81, 96, 'case'].retainAll(['case', 'object', 54]); // ['object', 'case']
```

### `<List>.reverse()`
- Description: This allows you to reverse the list
- Returns - List: the reversed list
- Example:
```kotlin
['a', 'b', 'c', 'd'].reverse(); // ['d', 'c', 'b', 'a']
```

### `<List>.set(value, index)`
- Description: This allows you to set the value at a specific index, alternative to bracket assignment,
this will throw an erroor if the index given is out of bounds
- Parameters:
  - Object (`value`): the value you want to set
  - Number (`index`): the index you want to set the value at
- Returns - List: the list
- Example:
```kotlin
['object', 81, 96, 'case'].set('foo', 1); // ['object', 'foo', 96, 'case']
```

### `<List>.shuffle()`
- Description: This allows you to shuffle the list
- Returns - List: the shuffled list
- Example:
```kotlin
['a', 'b', 'c', 'd'].shuffle(); // some random order ¯\_(ツ)_/¯
```

### `<List>.sort()`
- Description: This allows you to sort the list using the elements compare method
- Returns - List: the sorted list
- Example:
```kotlin
['d', 'a', 'c', 'b'].sort(); // ['a', 'b', 'c', 'd']
```

### `<List>.sort(comparator)`
- Description: This allows you to sort the list using a comparator function
- Parameter - Function (`comparator`): the comparator function
- Returns - List: the sorted list
- Example:
```kotlin
[6, 5, 9, -10].sort(fun(a, b) { return a - b; }); // [-10, 5, 6, 9]
```



# Map class
Map class for Arucas

This class is used to create a map of objects, using keys and values.
This class cannot be directly instantiated, but can be extended to create a map of your own type.
Class does not need to be imported

## Constructors

### `new Map()`
- Description: This creates an empty map, this cannot be called directly, only from child classes
- Example:
```kotlin
class ChildMap: Map {
    ChildMap(): super();
}
```

## Methods

### `<Map>.clear()`
- Description: This allows you to clear the map of all the keys and values
- Example:
```kotlin
(map = {'key': 'value'}).clear(); // map = {}
```

### `<Map>.containsKey(key)`
- Description: This allows you to check if the map contains a specific key
- Parameter - Object (`key`): the key you want to check
- Returns - Boolean: true if the map contains the key, false otherwise
- Example:
```kotlin
{'key': 'value'}.containsKey('key'); // true
```

### `<Map>.containsValue(value)`
- Description: This allows you to check if the map contains a specific value
- Parameter - Object (`value`): the value you want to check
- Returns - Boolean: true if the map contains the value, false otherwise
- Example:
```kotlin
{'key': 'value'}.containsValue('foo'); // false
```

### `<Map>.get(key)`
- Description: This allows you to get the value of a key in the map
- Parameter - Object (`key`): the key you want to get the value of
- Returns - Object: the value of the key, will return null if non-existent
- Example:
```kotlin
{'key': 'value'}.get('key'); // 'value'
```

### `<Map>.getKeys()`
- Description: This allows you to get the keys in the map
- Returns - List: a complete list of all the keys
- Example:
```kotlin
{'key': 'value', 'key2': 'value2'}.getKeys(); // ['key', 'key2']
```

### `<Map>.getValues()`
- Description: This allows you to get the values in the map
- Returns - List: a complete list of all the values
- Example:
```kotlin
{'key': 'value', 'key2': 'value2'}.getValues(); // ['value', 'value2']
```

### `<Map>.map(remapper)`
- Description: This allows you to map the values in the map and returns a new map
- Parameter - Function (`remapper`): the function you want to map the values with
- Returns - Map: a new map with the mapped values
- Example:
```kotlin
map = {'key': 'value', 'key2': 'value2'}
map.map(fun(k, v) {
    return [v, k];
});
// map = {'value': 'key', 'value2': 'key2'}
```

### `<Map>.put(key, value)`
- Description: This allows you to put a key and value in the map
- Parameters:
  - Object (`key`): the key you want to put
  - Object (`value`): the value you want to put
- Returns - Object: the previous value associated with the key, null if none
- Example:
```kotlin
{'key': 'value'}.put('key2', 'value2'); // null
```

### `<Map>.putAll(another map)`
- Description: This allows you to put all the keys and values of another map into this map
- Parameter - Map (`another map`): the map you want to merge into this map
- Example:
```kotlin
(map = {'key': 'value'}).putAll({'key2': 'value2'}); // map = {'key': 'value', 'key2': 'value2'}
```

### `<Map>.putIfAbsent(key, value)`
- Description: This allows you to put a key and value in the map if it doesn't exist
- Parameters:
  - Object (`key`): the key you want to put
  - Object (`value`): the value you want to put
- Example:
```kotlin
(map = {'key': 'value'}).putIfAbsent('key2', 'value2'); // map = {'key': 'value', 'key2': 'value2'}
```

### `<Map>.remove(key)`
- Description: This allows you to remove a key and its value from the map
- Parameter - Object (`key`): the key you want to remove
- Returns - Object: the value associated with the key, null if none
- Example:
```kotlin
{'key': 'value'}.remove('key'); // 'value'
```

## Static Methods

### `Map.unordered()`
- Description: This function allows you to create an unordered map
- Returns - Map: an unordered map
- Example:
```kotlin
Map.unordered();
```



# Math class
Math class for Arucas

Provides many basic math functions. This is a utility class, and cannot be constructed
Class does not need to be imported

## Static Fields

### `Math.e`
- Description: The value of e
- Type: Number
- Assignable: false
- Example:
```kotlin
Math.e;
```
### `Math.pi`
- Description: The value of pi
- Type: Number
- Assignable: false
- Example:
```kotlin
Math.pi;
```
### `Math.root2`
- Description: The value of root 2
- Type: Number
- Assignable: false
- Example:
```kotlin
Math.root2;
```

## Static Methods

### `Math.abs(num)`
- Description: Returns the absolute value of a number
- Parameter - Number (`num`): the number to get the absolute value of
- Returns - Number: the absolute value of the number
- Example:
```kotlin
Math.abs(-3);
```

### `Math.arccos(num)`
- Description: Returns the arc cosine of a number
- Parameter - Number (`num`): the number to get the arc cosine of
- Returns - Number: the arc cosine of the number
- Example:
```kotlin
Math.arccos(Math.cos(Math.pi));
```

### `Math.arcsin(num)`
- Description: Returns the arc sine of a number
- Parameter - Number (`num`): the number to get the arc sine of
- Returns - Number: the arc sine of the number
- Example:
```kotlin
Math.arcsin(Math.sin(Math.pi));
```

### `Math.arctan(num)`
- Description: Returns the arc tangent of a number
- Parameter - Number (`num`): the number to get the arc tangent of
- Returns - Number: the arc tangent of the number
- Example:
```kotlin
Math.arctan(Math.tan(Math.pi));
```

### `Math.arctan2(y, x)`
- Description: Returns the angle theta of the polar coordinates (r, theta) that correspond to the rectangular
coordinates (x, y) by computing the arc tangent of the value y / x
- Parameters:
  - Number (`y`): the ordinate coordinate
  - Number (`x`): the abscissa coordinate
- Returns - Number: the theta component of the point (r, theta)
- Example:
```kotlin
Math.arctan2(Math.tan(Math.pi), Math.cos(Math.pi)); // -3.141592
```

### `Math.ceil(num)`
- Description: Rounds a number up to the nearest integer
- Parameter - Number (`num`): the number to round
- Returns - Number: the rounded number
- Example:
```kotlin
Math.ceil(3.5);
```

### `Math.clamp(value, min, max)`
- Description: Clamps a value between a minimum and maximum
- Parameters:
  - Number (`value`): the value to clamp
  - Number (`min`): the minimum
  - Number (`max`): the maximum
- Returns - Number: the clamped value
- Example:
```kotlin
Math.clamp(10, 2, 8);
```

### `Math.cos(num)`
- Description: Returns the cosine of a number
- Parameter - Number (`num`): the number to get the cosine of
- Returns - Number: the cosine of the number
- Example:
```kotlin
Math.cos(Math.pi);
```

### `Math.cosec(num)`
- Description: Returns the cosecant of a number
- Parameter - Number (`num`): the number to get the cosecant of
- Returns - Number: the cosecant of the number
- Example:
```kotlin
Math.cosec(Math.pi);
```

### `Math.cosh(num)`
- Description: Returns the hyperbolic cosine of a number
- Parameter - Number (`num`): the number to get the hyperbolic cosine of
- Returns - Number: the hyperbolic cosine of the number
- Example:
```kotlin
Math.cosh(1);
```

### `Math.cot(num)`
- Description: Returns the cotangent of a number
- Parameter - Number (`num`): the number to get the cotangent of
- Returns - Number: the cotangent of the number
- Example:
```kotlin
Math.cot(Math.pi);
```

### `Math.floor(num)`
- Description: Rounds a number down to the nearest integer
- Parameter - Number (`num`): the number to round
- Returns - Number: the rounded number
- Example:
```kotlin
Math.floor(3.5);
```

### `Math.lerp(start, end, delta)`
- Description: Linear interpolation between two numbers
- Parameters:
  - Number (`start`): the first number
  - Number (`end`): the second number
  - Number (`delta`): the interpolation factor
- Returns - Number: the interpolated number
- Example:
```kotlin
Math.lerp(0, 10, 0.5);
```

### `Math.ln(num)`
- Description: Returns the natural logarithm of a number
- Parameter - Number (`num`): the number to get the logarithm of
- Returns - Number: the natural logarithm of the number
- Example:
```kotlin
Math.ln(Math.e);
```

### `Math.log(base, num)`
- Description: Returns the logarithm of a number with a specified base
- Parameters:
  - Number (`base`): the base
  - Number (`num`): the number to get the logarithm of
- Returns - Number: the logarithm of the number
- Example:
```kotlin
Math.log(2, 4);
```

### `Math.log10(num)`
- Description: Returns the base 10 logarithm of a number
- Parameter - Number (`num`): the number to get the logarithm of
- Returns - Number: the base 10 logarithm of the number
- Example:
```kotlin
Math.log10(100);
```

### `Math.max(num1, num2)`
- Description: Returns the largest number
- Parameters:
  - Number (`num1`): the first number to compare
  - Number (`num2`): the second number to compare
- Returns - Number: the largest number
- Example:
```kotlin
Math.max(5, 2);
```

### `Math.min(num1, num2)`
- Description: Returns the smallest number
- Parameters:
  - Number (`num1`): the first number to compare
  - Number (`num2`): the second number to compare
- Returns - Number: the smallest number
- Example:
```kotlin
Math.min(5, 2);
```

### `Math.mod(num1, num2)`
- Description: Returns the modulus of a division
- Parameters:
  - Number (`num1`): the number to divide
  - Number (`num2`): the divisor
- Returns - Number: the modulus of the division
- Example:
```kotlin
Math.mod(5, 2);
```

### `Math.rem(num1, num2)`
- Description: Returns the remainder of a division
- Parameters:
  - Number (`num1`): the number to divide
  - Number (`num2`): the divisor
- Returns - Number: the remainder of the division
- Example:
```kotlin
Math.rem(5, 2);
```

### `Math.round(num)`
- Description: Rounds a number to the nearest integer
- Parameter - Number (`num`): the number to round
- Returns - Number: the rounded number
- Example:
```kotlin
Math.round(3.5);
```

### `Math.sec(num)`
- Description: Returns the secant of a number
- Parameter - Number (`num`): the number to get the secant of
- Returns - Number: the secant of the number
- Example:
```kotlin
Math.sec(Math.pi);
```

### `Math.signum(num)`
- Description: Returns the sign of a number, 1 if the number is positive,
-1 if the number is negative, and 0 if the number is 0
- Parameter - Number (`num`): the number to get the sign of
- Returns - Number: the sign of the number
- Example:
```kotlin
Math.signum(3);
```

### `Math.sin(num)`
- Description: Returns the sine of a number
- Parameter - Number (`num`): the number to get the sine of
- Returns - Number: the sine of the number
- Example:
```kotlin
Math.sin(Math.pi);
```

### `Math.sinh(num)`
- Description: Returns the hyperbolic sine of a number
- Parameter - Number (`num`): the number to get the hyperbolic sine of
- Returns - Number: the hyperbolic sine of the number
- Example:
```kotlin
Math.sinh(1);
```

### `Math.sqrt(num)`
- Description: Returns the square root of a number
- Parameter - Number (`num`): the number to square root
- Returns - Number: the square root of the number
- Example:
```kotlin
Math.sqrt(9);
```

### `Math.tan(num)`
- Description: Returns the tangent of a number
- Parameter - Number (`num`): the number to get the tangent of
- Returns - Number: the tangent of the number
- Example:
```kotlin
Math.tan(Math.pi);
```

### `Math.tanh(num)`
- Description: Returns the hyperbolic tangent of a number
- Parameter - Number (`num`): the number to get the hyperbolic tangent of
- Returns - Number: the hyperbolic tangent of the number
- Example:
```kotlin
Math.tanh(1);
```

### `Math.toDegrees(num)`
- Description: Converts a number from radians to degrees
- Parameter - Number (`num`): the number to convert
- Returns - Number: the number in degrees
- Example:
```kotlin
Math.toDegrees(Math.pi);
```

### `Math.toRadians(num)`
- Description: Converts a number from degrees to radians
- Parameter - Number (`num`): the number to convert
- Returns - Number: the number in radians
- Example:
```kotlin
Math.toRadians(90);
```



# Network class
Network class for Arucas

Allows you to do http requests. This is a utility class and cannot be constructed.
Import with `import Network from util.Network;`

## Static Methods

### `Network.downloadFile(url, file)`
- Description: Downloads a file from an url to a file
- Parameters:
  - String (`url`): the url to download from
  - File (`file`): the file to download to
- Returns - Boolean: whether the download was successful
- Example:
```kotlin
Network.downloadFile('https://arucas.com', new File('dir/downloads'));
```

### `Network.openUrl(url)`
- Description: Opens an url in the default browser
- Parameter - String (`url`): the url to open
- Example:
```kotlin
Network.openUrl('https://google.com');
```

### `Network.requestUrl(url)`
- Description: Requests an url and returns the response
- Parameter - String (`url`): the url to request
- Returns - String: the response from the url
- Example:
```kotlin
Network.requestUrl('https://google.com');
```



# Null class
Null class for Arucas

This class is used for the null object,
this cannot be instantiated or extended
Class does not need to be imported



# Number class
Number class for Arucas

This class cannot be constructed as it has a literal representation.
For math related functions see the Math class.
Class does not need to be imported

## Methods

### `<Number>.ceil()`
- Description: This allows you to round a number up to the nearest integer
- Returns - Number: the rounded number
- Example:
```kotlin
3.5.ceil();
```

### `<Number>.floor()`
- Description: This allows you to round a number down to the nearest integer
- Returns - Number: the rounded number
- Example:
```kotlin
3.5.floor();
```

### `<Number>.isInfinite()`
- Description: This allows you to check if a number is infinite
- Returns - Boolean: true if the number is infinite
- Example:
```kotlin
(1/0).isInfinite();
```

### `<Number>.isNaN()`
- Description: This allows you to check if a number is not a number
- Returns - Boolean: true if the number is not a number
- Example:
```kotlin
(0/0).isNaN();
```

### `<Number>.round()`
- Description: This allows you to round a number to the nearest integer
- Returns - Number: the rounded number
- Example:
```kotlin
3.5.round();
```



# Object class
Object class for Arucas

This is the base class for every other class in Arucas.
This class cannot be instantiated from, you can extend it
however every class already extends this class by default
Class does not need to be imported

## Methods

### `<Object>.copy()`
- Description: This returns a copy of the value if implemented.
Some objects that are immutable, such as Strings and Numbers
will not be copied, and will return the same instance.
Any object that has not implemented the copy method will also
return the same instance
- Returns - Object: a copy of the value
- Example:
```kotlin
[10, 11, 12].copy(); // [10, 11, 12]
```

### `<Object>.hashCode()`
- Description: This returns the hash code of the value, mainly used for maps and sets
the hash code of an object must remain consistent for objects to be able
to be used as keys in a map or set. If two objects are equal, they must
have the same hash code
- Returns - Number: the hash code of the value
- Example:
```kotlin
[10, 11, 12].hashCode(); // -1859087
```

### `<Object>.instanceOf(type)`
- Description: This returns true if the value is an instance of the given type
- Parameter - Type (`type`): the type to check against
- Returns - Boolean: true if the value is an instance of the given type
- Example:
```kotlin
[10, 11, 12].instanceOf(List.type); // true
```

### `<Object>.toString()`
- Description: This returns the string representation of the value
- Returns - String: the string representation of the value
- Example:
```kotlin
[10, 11, 12].toString(); // [10, 11, 12]
```

### `<Object>.uniqueHash()`
- Description: This returns the unique hash of the value, this is different for every instance of a value
- Returns - Number: the unique hash of the value
- Example:
```kotlin
'thing'.uniqueHash();
```



# Set class
Set class for Arucas

Sets are collections of unique values. Similar to maps, without the values.
An instance of the class can be created by using `Set.of(values...)`
Class does not need to be imported

## Constructors

### `new Set()`
- Description: This creates an empty set
- Example:
```kotlin
new Set();
```

## Methods

### `<Set>.add(value)`
- Description: This allows you to add a value to the set
- Parameter - Object (`value`): the value you want to add to the set
- Returns - Boolean: whether the value was successfully added to the set
- Example:
```kotlin
Set.of().add('object');
```

### `<Set>.addAll(collection)`
- Description: This allows you to add all the values in a collection into the set
- Parameter - Collection (`collection`): the collection of values you want to add
- Returns - Set: the modified set
- Example:
```kotlin
Set.of().addAll(Set.of('object', 81, 96, 'case'));
```

### `<Set>.clear()`
- Description: This removes all values from inside the set
- Example:
```kotlin
Set.of('object').clear();
```

### `<Set>.contains(value)`
- Description: This allows you to check whether a value is in the set
- Parameter - Object (`value`): the value that you want to check in the set
- Returns - Boolean: whether the value is in the set
- Example:
```kotlin
Set.of('object').contains('object');
```

### `<Set>.containsAll(collection)`
- Description: This allows you to check whether a collection of values are all in the set
- Parameter - Collection (`collection`): the collection of values you want to check in the set
- Returns - Boolean: whether all the values are in the set
- Example:
```kotlin
Set.of('object').containsAll(Set.of('object', 81, 96, 'case'));
```

### `<Set>.filter(function)`
- Description: This allows you to filter the set
- Parameter - Function (`function`): the function you want to filter the set by
- Returns - Set: the filtered set
- Example:
```kotlin
Set.of(-9, 81, 96, 15).filter(fun(value) { return value > 80; });
```

### `<Set>.get(value)`
- Description: This allows you to get a value from in the set.
The reason this might be useful is if you want to retrieve something
from the set that will have the same hashcode but be in a different state
as the value you are passing in
- Parameter - Object (`value`): the value you want to get from the set
- Returns - Object: the value you wanted to get, null if it wasn't in the set
- Example:
```kotlin
Set.of('object').get('object');
```

### `<Set>.map(function)`
- Description: This allows you to map the set
- Parameter - Function (`function`): the function you want to map the set by
- Returns - Set: the mapped set
- Example:
```kotlin
Set.of(-9, 81, 96, 15).map(fun(value) { return value * 2; });
```

### `<Set>.reduce(function)`
- Description: This allows you to reduce the set
- Parameter - Function (`function`): the function you want to reduce the set by
- Returns - Object: the reduced set
- Example:
```kotlin
Set.of(-9, 81, 96, 15).reduce(fun(value, next) { return value + next; });
```

### `<Set>.remove(value)`
- Description: This allows you to remove a value from the set
- Parameter - Object (`value`): the value you want to remove from the set
- Returns - Boolean: whether the value was removed from the set
- Example:
```kotlin
Set.of('object').remove('object');
```

### `<Set>.removeAll(value)`
- Description: This allows you to remove all values in a collection from the set
- Parameter - Collection (`value`): the values you want to remove from the set
- Returns - Set: the set with the values removed
- Example:
```kotlin
Set.of('object', 'object').removeAll(Set.of('object'));
```

### `<Set>.toList()`
- Description: This returns a list of all the values in the set
- Returns - List: the list of values in the set
- Example:
```kotlin
Set.of('object', 81, 96, 'case').toList();
```

## Static Methods

### `Set.of(values...)`
- Description: This allows you to create a set with an arbitrary number of values
- Parameter - Object (`values...`): the values you want to add to the set
- Returns - Set: the set you created
- Example:
```kotlin
Set.of('object', 81, 96, 'case');
```

### `Set.unordered()`
- Description: This creates an unordered set
- Returns - Set: the unordered set
- Example:
```kotlin
Set.unordered();
```



# String class
String class for Arucas

This class represents an array of characters to form a string.
This class cannot be instantiated directly, instead use the literal
by using quotes. Strings are immutable in Arucas.
Class does not need to be imported

## Constructors

### `new String()`
- Description: This creates a new string object, not from the string pool, with the given string.
This cannot be called directly, only from child classes
- Example:
```kotlin
class ChildString: String {
    ChildString(): super("example");
}
```

## Methods

### `<String>.capitalize()`
- Description: This returns the string in capitalized form
- Returns - String: the string in capitalized form
- Example:
```kotlin
'hello'.capitalize(); // 'Hello'
```

### `<String>.chars()`
- Description: This makes a list of all the characters in the string
- Returns - List: the list of characters
- Example:
```kotlin
'hello'.chars(); // ['h', 'e', 'l', 'l', 'o']
```

### `<String>.contains(string)`
- Description: This returns whether the string contains the given string
- Parameter - String (`string`): the string to check
- Returns - Boolean: whether the string contains the given string
- Example:
```kotlin
'hello'.contains('lo'); // true
```

### `<String>.endsWith(string)`
- Description: This returns whether the string ends with the given string
- Parameter - String (`string`): the string to check
- Returns - Boolean: whether the string ends with the given string
- Example:
```kotlin
'hello'.endsWith('lo'); // true
```

### `<String>.find(regex)`
- Description: This finds all matches of the regex in the string,
this does not find groups, for that use `<String>.findGroups(regex)`
- Parameter - String (`regex`): the regex to search the string with
- Returns - List: the list of all instances of the regex in the string
- Example:
```kotlin
'102i 1i'.find('([\\d+])i'); // ['2i', '1i']
```

### `<String>.findAll(regex)`
- Description: This finds all matches and groups of a regex in the matches in the string
the first group of each match will be the complete match and following
will be the groups of the regex, a group may be empty if it doesn't exist
- Parameter - String (`regex`): the regex to search the string with
- Returns - List: a list of match groups, which is a list containing matches
- Example:
```kotlin
'102i 1i'.findAll('([\\d+])i'); // [['2i', '2'], ['1i', '1']]
```

### `<String>.format(objects...)`
- Description: This formats the string using the given arguments.
This internally uses the Java String.format() method.
For how to use see here: https://www.javatpoint.com/java-string-format
- Parameter - Object (`objects...`): the objects to insert
- Returns - String: the formatted string
- Example:
```kotlin
'%s %s'.format('hello', 'world'); // 'hello world'
```

### `<String>.length()`
- Description: This returns the length of the string
- Returns - Number: the length of the string
- Example:
```kotlin
'hello'.length(); // 5
```

### `<String>.lowercase()`
- Description: This returns the string in lowercase
- Returns - String: the string in lowercase
- Example:
```kotlin
'HELLO'.lowercase(); // 'hello'
```

### `<String>.matches(regex)`
- Description: This returns whether the string matches the given regex
- Parameter - String (`regex`): the regex to match the string with
- Returns - Boolean: whether the string matches the given regex
- Example:
```kotlin
'foo'.matches('f.*'); // true
```

### `<String>.replaceAll(regex, replacement)`
- Description: This replaces all the instances of a regex with the replace string
- Parameters:
  - String (`regex`): the regex you want to replace
  - String (`replacement`): the string you want to replace it with
- Returns - String: the modified string
- Example:
```kotlin
'hello'.replaceAll('l', 'x'); // 'hexxo'
```

### `<String>.replaceFirst(regex, replacement)`
- Description: This replaces the first instance of a regex with the replace string
- Parameters:
  - String (`regex`): the regex you want to replace
  - String (`replacement`): the string you want to replace it with
- Returns - String: the modified string
- Example:
```kotlin
'hello'.replaceFirst('l', 'x'); // 'hexlo'
```

### `<String>.reverse()`
- Description: This returns the string in reverse
- Returns - String: the string in reverse
- Example:
```kotlin
'hello'.reverse(); // 'olleh'
```

### `<String>.split(regex)`
- Description: This splits the string into a list of strings based on a regex
- Parameter - String (`regex`): the regex to split the string with
- Returns - List: the list of strings
- Example:
```kotlin
'foo/bar/baz'.split('/');
```

### `<String>.startsWith(string)`
- Description: This returns whether the string starts with the given string
- Parameter - String (`string`): the string to check
- Returns - Boolean: whether the string starts with the given string
- Example:
```kotlin
'hello'.startsWith('he'); // true
```

### `<String>.strip()`
- Description: This strips the whitespace from the string
- Returns - String: the stripped string
- Example:
```kotlin
'  hello  '.strip(); // 'hello'
```

### `<String>.subString(from, to)`
- Description: This returns a substring of the string
- Parameters:
  - Number (`from`): the start index (inclusive)
  - Number (`to`): the end index (exclusive)
- Returns - String: the substring
- Example:
```kotlin
'hello'.subString(1, 3); // 'el'
```

### `<String>.toList()`
- Deprecated: Use '<String>.chars()' instead
- Description: This makes a list of all the characters in the string
- Returns - List: the list of characters
- Example:
```kotlin
'hello'.toList(); // ['h', 'e', 'l', 'l', 'o']
```

### `<String>.toNumber()`
- Description: This tries to convert the string to a number.
This method can convert hex or denary into numbers.
If the string is not a number, it will throw an error
- Returns - Number: the number
- Example:
```kotlin
'99'.toNumber(); // 99
```

### `<String>.uppercase()`
- Description: This returns the string in uppercase
- Returns - String: the string in uppercase
- Example:
```kotlin
'hello'.uppercase(); // 'HELLO'
```



# Task class
Task class for Arucas

This class is used to create tasks that can be chained and
run asynchronously. Tasks can be executed as many times as needed
and chained tasks will be executed in the order they are created.
Class does not need to be imported

## Constructors

### `new Task()`
- Description: This creates a new empty task
- Example:
```kotlin
task = new Task();
```

## Methods

### `<Task>.loopIf(boolSupplier)`
- Description: This loops the task, essentially just calling 'task.run', the
task will run async from the original task, the loop will continue
if the function provided returns true
- Parameter - Function (`boolSupplier`): the function to check if the loop should run
- Returns - Task: the task, this allows for chaining
- Example:
```kotlin
task = new Task()
    .then(fun() print("hello"))
    .then(fun() print(" "))
    .then(fun() print("world"))
    .loopIf(fun() true); // Always loop
```

### `<Task>.run()`
- Description: This runs the task asynchronously and returns a future which can be awaited.
The last function in the task will be used as the return value for the future
- Returns - Future: the future value that can be awaited
- Example:
```kotlin
task = new Task()
    .then(fun() print("hello"))
    .then(fun() print(" "))
    .then(fun() print("world"))
    .then(fun() 10);
f = task.run(); // prints 'hello world'
print(f.await()); // prints 10
```

### `<Task>.then(function)`
- Description: This adds a function to the end of the current task.
If this is the last function in the task then the return
value of the function will be the return value of the task.
- Parameter - Function (`function`): the function to run at the end of the task
- Returns - Task: the task, this allows for chaining
- Example:
```kotlin
task = new Task()
    .then(fun() print("hello"))
    .then(fun() print(" "))
    .then(fun() print("world"))
    .then(fun() 10);
f = task.run(); // prints 'hello world'
print(f.await()); // prints 10
```



# Thread class
Thread class for Arucas

This class allows to to create threads for async executions.
This class cannot be instantiated or extended. To create a new
thread use the static method 'Thread.runThreaded()'
Class does not need to be imported

## Methods

### `<Thread>.freeze()`
- Description: This serves the same purpose as 'Thread.freeze()' however this works on the current
thread instance, unlike 'Thread.freeze()' this cannot throw an error.
- Example:
```kotlin
Thread.getCurrentThread().freeze()
```

### `<Thread>.getAge()`
- Description: This gets the age of the thread in milliseconds
- Returns - Number: the age of the thread
- Example:
```kotlin
Thread.getCurrentThread().getAge();
```

### `<Thread>.getName()`
- Description: This gets the name of the thread
- Returns - String: the name of the thread
- Example:
```kotlin
Thread.getCurrentThread().getName();
```

### `<Thread>.isAlive()`
- Description: This checks if the thread is alive (still running)
- Returns - Boolean: true if the thread is alive, false if not
- Example:
```kotlin
Thread.getCurrentThread().isAlive();
```

### `<Thread>.stop()`
- Description: This stops the thread from executing, anything that was running will be instantly stopped.
This method will fail if the thread is not alive
- Example:
```kotlin
Thread.getCurrentThread().stop();
```

### `<Thread>.thaw()`
- Description: This will thaw the thread from its frozen state, if the thread is not frozen then an
error will be thrown
- Example:
```kotlin
Thread.getCurrentThread().thaw();
```

## Static Methods

### `Thread.freeze()`
- Description: This freezes the current thread, stops anything else from executing on the thread.
This may fail if you try to freeze a non Arucas Thread in which case an error will be thrown
- Example:
```kotlin
Thread.freeze();
```

### `Thread.getCurrentThread()`
- Description: This gets the current thread that the code is running on,
this may throw an error if the thread is not safe to get,
which happens when running outside of Arucas Threads
- Returns - Thread: the current thread
- Example:
```kotlin
Thread.getCurrentThread();
```

### `Thread.runThreaded(function)`
- Description: This starts a new thread and runs a function on it, the thread will
terminate when it finishes executing the function, threads will stop automatically
when the program stops, you are also able to stop threads by using the Thread object
- Parameter - Function (`function`): the function you want to run on a new thread
- Returns - Thread: the new thread
- Example:
```kotlin
Thread.runThreaded(fun() {
    print("Running asynchronously!");
});
```

### `Thread.runThreaded(name, function)`
- Description: This starts a new thread with a specific name and runs a function on it
- Parameters:
  - String (`name`): the name of the thread
  - Function (`function`): the function you want to run on a new thread
- Returns - Thread: the new thread
- Example:
```kotlin
Thread.runThreaded("MyThread", fun() {
    print("Running asynchronously on MyThread");
});
```



# Type class
Type class for Arucas

This class lets you get the type of another class
Class does not need to be imported

## Methods

### `<Type>.getName()`
- Description: This gets the name of the type
- Returns - String: the name of the type
- Example:
```kotlin
String.type.getName();
```

### `<Type>.inheritsFrom(type)`
- Description: This checks whether a type is a subtype of another type
- Parameter - Type (`type`): the other type you want to check against
- Returns - Boolean: whether the type is of that type
- Example:
```kotlin
String.type.inheritsFrom(Number.type);
```

### `<Type>.instanceOf(type)`
- Deprecated: Use '<Type>.inheritsFrom(type)'
- Description: This checks whether a type is a subtype of another type
- Parameter - Type (`type`): the other type you want to check against
- Returns - Boolean: whether the type is of that type
- Example:
```kotlin
String.type.instanceOf(Number.type);
```

## Static Methods

### `Type.of(value)`
- Description: This gets the specific type of a value
- Parameter - Object (`value`): the value you want to get the type of
- Returns - Type: the type of the value
- Example:
```kotlin
Type.of(0);
```

