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