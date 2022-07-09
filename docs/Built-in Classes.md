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
  
### `<Collector>.flatten()`  
- Description: If there are values in the collector that are collections they will be expanded,   
collections inside collections are not flattened, you would have to call this method again  
- Returns - Collector: a new Collector with the expanded values  
- Example:  
```kt  
Collector.of([1, 2, [3, 4]]).flatten();  
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
  
### `<Collector>.map(mapper)`  
- Description: This maps the values in Collector to a new value  
- Parameter - Function (`mapper`): a function that takes a value and returns a new value  
- Returns - Collector: a new Collector with the mapped values  
- Example:  
```kt  
Collector.of([1, 2, 3]).map(fun(value) {  
    return value * 2;});  
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
  
### `<Collector>.toSet()`  
- Description: This puts all the values in the collector into a set and returns it  
- Returns - Set: a set with all the values in the collector  
- Example:  
```kt  
Collector.of([1, 2, 3]).toSet();  
```  
  
## Static Methods  
  
### `Collector.isCollection(value)`  
- Description: This checks if the value is a collection  
- Parameter - Value (`value`): the value you want to check  
- Returns - Boolean: true if the value is a collection  
- Example:  
```kt  
Collector.isCollection([1, 2, 3]);  
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
  
### `<Error>.getValue()`  
- Description: This returns the value that is related to the error  
- Returns - Value: the value that is related to the error  
- Example:  
```kt  
error.getValue();  
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
  
### `<File>.createDirectory()`  
- Description: This creates all parent directories of the file if they don't already exist  
- Returns - Boolean: true if the directories were created  
- Throws - Error:  
  - `'...'`  
- Example:  
```kt  
file.createDirectory()  
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
  
### `<File>.exists()`  
- Description: This returns if the file exists  
- Returns - Boolean: true if the file exists  
- Throws - Error:  
  - `'Could not check file: ...'`  
- Example:  
```kt  
file.exists()  
```  
  
### `<File>.getAbsolutePath()`  
- Description: This returns the absolute path of the file  
- Returns - String: the absolute path of the file  
- Example:  
```kt  
file.getAbsolutePath()  
```  
  
### `<File>.getName()`  
- Description: This returns the name of the file  
- Returns - String: the name of the file  
- Example:  
```kt  
File.getName()  
```  
  
### `<File>.getPath()`  
- Description: This returns the path of the file  
- Returns - String: the path of the file  
- Example:  
```kt  
file.getPath()  
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
  
### `<File>.open()`  
- Description: This opens the file (as in opens it on your os)  
- Example:  
```kt  
file.open()  
```  
  
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
  
### `<File>.write(string)`  
- Description: This writes a string to a file  
- Parameter - String (`string`): the string to write to the file  
- Throws - Error:  
  - `'There was an error writing the file: ...'`  
- Example:  
```kt  
file.write('Hello World!')  
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
  
### `<Java>.getField(fieldName)`  
- Deprecated: You should call the method directly on the value: Java.constructClass('me.senseiwells.impl.Test').A;  
- Description: This returns the Java wrapped value of the specified field  
- Parameter - String (`fieldName`): the name of the field  
- Returns - Java: the Java wrapped value of the field  
- Example:  
```kt  
Java.constructClass('me.senseiwells.impl.Test').getField('A');  
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
  
### `<Java>.toArucas()`  
- Description: This converts the Java value to an Arucas Value  
- Returns - Value: the Value in Arucas, this may still be of Java value if the value cannot be converted into an Arucas value, values like Strings, Numbers, Lists, etc... will be converted  
- Example:  
```kt  
Java.valueOf([1, 2, 3]).toArucas();  
```  
  
## Static Methods  
  
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
  
### `Java.booleanArray(size)`  
- Description: Creates a Java boolean array with a given size, the array is filled with false   
by default and can be filled with only booleans  
- Parameter - Number (`size`): the size of the array  
- Returns - Java: the Java boolean array  
- Example:  
```kt  
Java.booleanArray(10);  
```  
  
### `Java.booleanOf(bool)`  
- Description: Creates a Java value boolean, to be used in Java  
- Parameter - Boolean (`bool`): the boolean to convert to a Java boolean  
- Returns - Java: the boolean in Java wrapper  
- Example:  
```kt  
Java.booleanOf(true);  
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
  
### `Java.byteOf(num)`  
- Description: Creates a Java value byte, to be used in Java since bytes cannot be explicitly declared in Arucas  
- Parameter - Number (`num`): the number to convert to a Java byte  
- Returns - Java: the byte in Java wrapper  
- Example:  
```kt  
Java.byteOf(0xFF);  
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
  
### `Java.charArray(size)`  
- Description: Creates a Java char array with a given size, the array is filled with null's   
(null characters) by default and can be filled with only chars  
- Parameter - Number (`size`): the size of the array  
- Returns - Java: the Java char array  
- Example:  
```kt  
Java.charArray(10);  
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
  
### `Java.classFromName(className)`  
- Description: Gets a Java class from the name of the class  
- Parameter - String (`className`): the name of the class you want to get  
- Returns - Java: the Java `Class<?>` value wrapped in the Java wrapper  
- Throws - Error:  
  - `'No such class with ...'`  
- Example:  
```kt  
Java.classFromName('java.util.ArrayList');  
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
  
### `Java.consumerOf(function)`  
- Description: Creates a Java Consumer object from a given function  
- Parameter - Function (`function`): the function to be executed, this must have one parameter and any return values will be ignored, the parameter type is unknown at compile time  
- Returns - Java: the Java Consumer object  
- Example:  
```kt  
Java.consumerOf(fun(something) {  
    print(something);});  
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
  
### `Java.doubleOf(num)`  
- Description: Creates a Java value double, to be used in Java  
- Parameter - Number (`num`): the number to convert to a Java double  
- Returns - Java: the double in Java wrapper  
- Example:  
```kt  
Java.doubleOf(1.0);  
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
  
### `Java.floatOf(num)`  
- Description: Creates a Java value float, to be used in Java, since floats cannot be explicitly declared in Arucas  
- Parameter - Number (`num`): the number to convert to a Java float  
- Returns - Java: the float in Java wrapper  
- Example:  
```kt  
Java.floatOf(1.0);  
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
  
### `Java.intArray(size)`  
- Description: Creates a Java int array with a given size, the array is filled with 0's   
by default and can be filled with only ints  
- Parameter - Number (`size`): the size of the array  
- Returns - Java: the Java int array  
- Example:  
```kt  
Java.intArray(10);  
```  
  
### `Java.intOf(num)`  
- Description: Creates a Java value int, to be used in Java since ints cannot be explicitly declared in Arucas  
- Parameter - Number (`num`): the number to convert to a Java int  
- Returns - Java: the int in Java wrapper  
- Example:  
```kt  
Java.intOf(0xFF);  
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
  
### `Java.longOf(num)`  
- Description: Creates a Java value long, to be used in Java since longs cannot be explicitly declared in Arucas  
- Parameter - Number (`num`): the number to convert to a Java long  
- Returns - Java: the long in Java wrapper  
- Example:  
```kt  
Java.longOf(1000000000.0);  
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
  
### `Java.shortArray(size)`  
- Description: Creates a Java short array with a given size, the array is filled with 0's   
by default and can be filled with only shorts  
- Parameter - Number (`size`): the size of the array  
- Returns - Java: the Java short array  
- Example:  
```kt  
Java.shortArray(10);  
```  
  
### `Java.shortOf(num)`  
- Description: Creates a Java value short, to be used in Java since shorts cannot be explicitly declared in Arucas  
- Parameter - Number (`num`): the number to convert to a Java short  
- Returns - Java: the short in Java wrapper  
- Example:  
```kt  
Java.shortOf(0xFF);  
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
  
### `Java.valueOf(value)`  
- Description: Converts any Arucas value into a Java value then wraps it in the Java wrapper and returns it  
- Parameter - Value (`value`): any value to get the Java value of  
- Example:  
```kt  
Java.valueOf('Hello World!');  
```  
  
  
# Json class  
Json class for Arucas.  
  
This class allows you to create and manipulate JSON objects.  
  
Import with `import Json from util.Json;`  
  
Fully Documented.  
  
## Methods  
  
### `<Json>.getValue()`  
- Description: This converts the Json back into a Value  
- Returns - Value: the Value parsed from the Json  
- Example:  
```kt  
json.getValue();  
```  
  
### `<Json>.writeToFile(file)`  
- Description: This writes the Json to a file  
- Parameter - File (`file`): the file that you want to write to  
- Throws - Error:  
  - `'There was an error writing the file: ...'`  
- Example:  
```kt  
json.writeToFile(new File('D:/cool/realDirectory'));  
```  
  
## Static Methods  
  
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
  
### `<List>.append(value)`  
- Description: This allows you to append a value to the end of the list  
- Parameter - Value (`value`): the value you want to append  
- Returns - List: the list  
- Example:  
```kt  
`['object', 81, 96, 'case'].append('foo');`  
```  
  
### `<List>.clear()`  
- Description: This allows you to clear all the values the list  
- Example:  
```kt  
`['object', 81, 96, 'case'].clear();`  
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
  
### `<List>.contains(value)`  
- Description: This allows you to check if the list contains a value  
- Parameter - Value (`value`): the value you want to check for  
- Returns - Boolean: true if the list contains the value, false otherwise  
- Example:  
```kt  
`['object', 81, 96, 'case'].contains('foo');`  
```  
  
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
  
### `<List>.indexOf(value)`  
- Description: This allows you to get the index of a value in the list  
- Parameter - Value (`value`): the value you want to check for  
- Returns - Number: the index of the value, -1 if the value is not in the list  
- Example:  
```kt  
`['object', 81, 96, 'case'].indexOf('case');`  
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
  
### `<List>.isEmpty()`  
- Description: This allows you to check if the list is empty  
- Returns - Boolean: true if the list is empty, false otherwise  
- Example:  
```kt  
`['object', 81, 96, 'case'].isEmpty();`  
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
  
### `<List>.toString()`  
- Description: This converts the list to a string and evaluating any collections inside it  
- Returns - String: the string representation of the set  
- Example:  
```kt  
`['object', 81, 96, 'case'].toString();`  
```  
  
  
  
# Map class  
Map class for Arucas.  
  
This class cannot be constructed since it has a literal, `{}`  
  
Class does not need to be imported.  
  
Fully Documented.  
  
## Methods  
  
### `<Map>.clear()`  
- Description: This allows you to clear the map of all the keys and values  
- Example:  
```kt  
{'key': 'value'}.clear();  
```  
  
### `<Map>.containsKey(key)`  
- Description: This allows you to check if the map contains a specific key  
- Parameter - Value (`key`): the key you want to check  
- Returns - Boolean: true if the map contains the key, false otherwise  
- Example:  
```kt  
{'key': 'value'}.containsKey('key');  
```  
  
### `<Map>.get(key)`  
- Description: This allows you to get the value of a key in the map  
- Parameter - Value (`key`): the key you want to get the value of  
- Returns - Value: the value of the key, will return null if non-existent  
- Example:  
```kt  
{'key': 'value'}.get('key');  
```  
  
### `<Map>.getKeys()`  
- Description: This allows you to get the keys in the map  
- Returns - List: a complete list of all the keys  
- Example:  
```kt  
{'key': 'value', 'key2', 'value2'}.getKeys();  
```  
  
### `<Map>.getValues()`  
- Description: This allows you to get the values in the map  
- Returns - List: a complete list of all the values  
- Example:  
```kt  
{'key': 'value', 'key2', 'value2'}.getValues();  
```  
  
### `<Map>.isEmpty()`  
- Description: This allows you to check if the map is empty  
- Returns - Boolean: true if the map is empty, false otherwise  
- Example:  
```kt  
{'key': 'value'}.isEmpty();  
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
  
### `<Map>.putAll(another map)`  
- Description: This allows you to put all the keys and values of another map into this map  
- Parameter - Map (`another map`): the map you want to merge into this map  
- Example:  
```kt  
{'key': 'value'}.putAll({'key2': 'value2'});  
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
  
### `<Map>.toString()`  
- Description: This allows you to get the string representation of the map and evaluating any collections inside it  
- Returns - String: the string representation of the map  
- Example:  
```kt  
{'key': []}.toString();  
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
### `Math.root2`  
- Description: The value of root 2  
- Type: Number  
- Assignable: false  
- Example:  
```kt  
Math.root2;  
```  
  
## Static Methods  
  
### `Math.abs(num)`  
- Description: Returns the absolute value of a number  
- Parameter - Number (`num`): the number to get the absolute value of  
- Returns - Number: the absolute value of the number  
- Example:  
```kt  
Math.abs(-3);  
```  
  
### `Math.arccos(num)`  
- Description: Returns the arc cosine of a number  
- Parameter - Number (`num`): the number to get the arc cosine of  
- Returns - Number: the arc cosine of the number  
- Example:  
```kt  
Math.arccos(Math.cos(Math.pi));  
```  
  
### `Math.arcsin(num)`  
- Description: Returns the arc sine of a number  
- Parameter - Number (`num`): the number to get the arc sine of  
- Returns - Number: the arc sine of the number  
- Example:  
```kt  
Math.arcsin(Math.sin(Math.pi));  
```  
  
### `Math.arctan(num)`  
- Description: Returns the arc tangent of a number  
- Parameter - Number (`num`): the number to get the arc tangent of  
- Returns - Number: the arc tangent of the number  
- Example:  
```kt  
Math.arctan(Math.tan(Math.pi));  
```  
  
### `Math.ceil(num)`  
- Description: Rounds a number up to the nearest integer  
- Parameter - Number (`num`): the number to round  
- Returns - Number: the rounded number  
- Example:  
```kt  
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
```kt  
Math.clamp(10, 2, 8);  
```  
  
### `Math.cos(num)`  
- Description: Returns the cosine of a number  
- Parameter - Number (`num`): the number to get the cosine of  
- Returns - Number: the cosine of the number  
- Example:  
```kt  
Math.cos(Math.pi);  
```  
  
### `Math.cosec(num)`  
- Description: Returns the cosecant of a number  
- Parameter - Number (`num`): the number to get the cosecant of  
- Returns - Number: the cosecant of the number  
- Example:  
```kt  
Math.cosec(Math.pi);  
```  
  
### `Math.cot(num)`  
- Description: Returns the cotangent of a number  
- Parameter - Number (`num`): the number to get the cotangent of  
- Returns - Number: the cotangent of the number  
- Example:  
```kt  
Math.cot(Math.pi);  
```  
  
### `Math.floor(num)`  
- Description: Rounds a number down to the nearest integer  
- Parameter - Number (`num`): the number to round  
- Returns - Number: the rounded number  
- Example:  
```kt  
Math.floor(3.5);  
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
  
### `Math.round(num)`  
- Description: Rounds a number to the nearest integer  
- Parameter - Number (`num`): the number to round  
- Returns - Number: the rounded number  
- Example:  
```kt  
Math.round(3.5);  
```  
  
### `Math.sec(num)`  
- Description: Returns the secant of a number  
- Parameter - Number (`num`): the number to get the secant of  
- Returns - Number: the secant of the number  
- Example:  
```kt  
Math.sec(Math.pi);  
```  
  
### `Math.sin(num)`  
- Description: Returns the sine of a number  
- Parameter - Number (`num`): the number to get the sine of  
- Returns - Number: the sine of the number  
- Example:  
```kt  
Math.sin(Math.pi);  
```  
  
### `Math.sqrt(num)`  
- Description: Returns the square root of a number  
- Parameter - Number (`num`): the number to square root  
- Returns - Number: the square root of the number  
- Example:  
```kt  
Math.sqrt(9);  
```  
  
### `Math.tan(num)`  
- Description: Returns the tangent of a number  
- Parameter - Number (`num`): the number to get the tangent of  
- Returns - Number: the tangent of the number  
- Example:  
```kt  
Math.tan(Math.pi);  
```  
  
### `Math.toDegrees(num)`  
- Description: Converts a number from radians to degrees  
- Parameter - Number (`num`): the number to convert  
- Returns - Number: the number in degrees  
- Example:  
```kt  
Math.toDegrees(Math.pi);  
```  
  
### `Math.toRadians(num)`  
- Description: Converts a number from degrees to radians  
- Parameter - Number (`num`): the number to convert  
- Returns - Number: the number in radians  
- Example:  
```kt  
Math.toRadians(90);  
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
  
### `<Number>.absolute()`  
- Deprecated: You should use `Math.abs(num)`  
- Description: This allows you to get the absolute value of a number  
- Returns - Number: the absolute value of the number  
- Example:  
```kt  
(-5).absolute();  
```  
  
### `<Number>.ceil()`  
- Description: This allows you to round a number up to the nearest integer  
- Returns - Number: the rounded number  
- Example:  
```kt  
3.5.ceil();  
```  
  
### `<Number>.floor()`  
- Description: This allows you to round a number down to the nearest integer  
- Returns - Number: the rounded number  
- Example:  
```kt  
3.5.floor();  
```  
  
### `<Number>.isInfinite()`  
- Description: This allows you to check if a number is infinite  
- Returns - Boolean: true if the number is infinite  
- Example:  
```kt  
(1/0).isInfinite();  
```  
  
### `<Number>.isNaN()`  
- Description: This allows you to check if a number is not a number  
- Returns - Boolean: true if the number is not a number  
- Example:  
```kt  
(0/0).isNaN();  
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
  
### `<Number>.round()`  
- Description: This allows you to round a number to the nearest integer  
- Returns - Number: the rounded number  
- Example:  
```kt  
3.5.round();  
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
  
  
  
# Object class  
Object class for Arucas.  
  
This is the base class for every other class in Arucas.  
  
Class does not need to be imported.  
  
Fully Documented.  
  
## Methods  
  
### `<Object>.copy()`  
- Description: This returns a copy of the value, some values might just return themselves  
- Returns - Value: the copy of the value  
- Example:  
```kt  
10.copy();  
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
  
### `<Object>.getValueType()`  
- Deprecated: You should use 'Type.of(`<Value>`).getName()'  
- Description: This returns the name of the type of the value  
- Returns - String: the name of the type of value  
- Example:  
```kt  
10.getValueType();  
```  
  
### `<Object>.hashCode()`  
- Description: This returns the hashcode of the value, this is mainly used for maps and sets  
- Returns - Number: the hashcode of the value  
- Example:  
```kt  
'thing'.hashCode();  
```  
  
### `<Object>.instanceOf(type)`  
- Description: This checks whether this value is an instance of another type  
- Parameter - Type (`type`): the other type you want to check against  
- Returns - Boolean: whether the value is of that type  
- Example:  
```kt  
10.instanceOf(String.type);  
```  
  
### `<Object>.toString()`  
- Description: This returns the string representation of the value  
- Returns - String: the string representation of the value  
- Example:  
```kt  
[10, 11, 12].toString();  
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
  
### `<Set>.clear()`  
- Description: This removes all values from inside the set  
- Example:  
```kt  
Set.of('object').clear();  
```  
  
### `<Set>.contains(value)`  
- Description: This allows you to check whether a value is in the set  
- Parameter - Value (`value`): the value that you want to check in the set  
- Returns - Boolean: whether the value is in the set  
- Example:  
```kt  
Set.of('object').contains('object');  
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
  
### `<Set>.isEmpty()`  
- Description: This allows you to check whether the set has no values  
- Returns - Boolean: whether the set is empty  
- Example:  
```kt  
Set.of().isEmpty();  
```  
  
### `<Set>.remove(value)`  
- Description: This allows you to remove a value from the set  
- Parameter - Value (`value`): the value you want to remove from the set  
- Returns - Boolean: whether the value was removed from the set  
- Example:  
```kt  
Set.of('object').remove('object');  
```  
  
### `<Set>.toString()`  
- Description: This converts the set to a string and evaluating any collections inside it  
- Returns - String: the string representation of the set  
- Example:  
```kt  
Set.of('object').toString();  
```  
  
## Static Methods  
  
### `Set.of(values...)`  
- Description: This allows you to create a set with an arbitrary number of values  
- Parameter - Value (`values...`): the values you want to add to the set  
- Returns - Set: the set you created  
- Example:  
```kt  
Set.of('object', 81, 96, 'case');  
```  
  
### `Set.unordered()`  
- Description: This creates an unordered set  
- Returns - Set: the unordered set  
- Example:  
```kt  
Set.unordered();  
```  
  
  
# String class  
String class for Arucas.  
  
This class cannot be constructed since strings have a literal. Strings are immutable.  
  
Class does not need to be imported.  
  
Fully Documented.  
  
## Methods  
  
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
  
### `<String>.endsWith(string)`  
- Description: This checks if the string ends with the given string  
- Parameter - String (`string`): the string to check the string with  
- Returns - Boolean: true if the string ends with the given string  
- Example:  
```kt  
'hello'.endsWith('he');  
```  
  
### `<String>.find(regex)`  
- Description: This finds all instances of the regex in the string  
- Parameter - String (`regex`): the regex to search the string with  
- Returns - List: the list of all instances of the regex in the string  
- Example:  
```kt  
'hello'.find('[a-z]*');  
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
  
### `<String>.lowercase()`  
- Description: This makes the string lowercase  
- Returns - String: the lowercase string  
- Example:  
```kt  
'HELLO'.lowercase();  
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
  
### `<String>.split(regex)`  
- Description: This splits the string into a list of strings based on a regex  
- Parameter - String (`regex`): the regex to split the string with  
- Returns - List: the list of strings  
- Example:  
```kt  
'foo/bar/baz'.split('/');  
```  
  
### `<String>.startsWith(string)`  
- Description: This checks if the string starts with the given string  
- Parameter - String (`string`): the string to check the string with  
- Returns - Boolean: true if the string starts with the given string  
- Example:  
```kt  
'hello'.startsWith('he');  
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
  
### `<String>.toList()`  
- Description: This makes a list of all the characters in the string  
- Returns - List: the list of characters  
- Example:  
```kt  
'hello'.toList();  
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
  
### `<String>.uppercase()`  
- Description: This makes the string uppercase  
- Returns - String: the uppercase string  
- Example:  
```kt  
'hello'.uppercase();  
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
  
### `<Thread>.getName()`  
- Description: This gets the name of the thread  
- Returns - String: the name of the thread  
- Example:  
```kt  
Thread.getCurrentThread().getName();  
```  
  
### `<Thread>.isAlive()`  
- Description: This checks if the thread is alive (still running)  
- Returns - Boolean: true if the thread is alive, false if not  
- Example:  
```kt  
Thread.getCurrentThread().isAlive();  
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
  
### `Thread.getCurrentThread()`  
- Description: This gets the current thread that the code is running on  
- Returns - Thread: the current thread  
- Throws - Error:  
  - `'Thread is not safe to get'`  
- Example:  
```kt  
Thread.getCurrentThread();  
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
  
  
# Type class  
Type class for Arucas.  
  
This class lets you get the type of a class or value.  
  
Class does not need to be imported.  
  
Fully Documented.  
  
## Methods  
  
### `<Type>.getConstructor(parameters)`  
- Description: This gets the constructor of the type  
- Parameter - Number (`parameters`): the number of parameters for the constructor  
- Returns - Function: the constructor of the type  
- Example:  
```kt  
String.type.getConstructor(0);  
```  
  
### `<Type>.getName()`  
- Description: This gets the name of the type  
- Returns - String: the name of the type  
- Example:  
```kt  
String.type.getName();  
```  
  
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