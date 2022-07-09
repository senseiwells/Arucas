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

Now that you've implemented Arucas, let's have a go at implementing some wrapper classes. These are classes in Java that Arucas can directly access and use as if it were an actual class in Arucas.

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
All annotated fields **must** be of type `Value`, unless the field is final, since Arucas is not statically typed, and you will be able to assign any `Value` to a field. Final fields are allowed, they will be able to be accessed in Arucas but not be able to be assigned to.
Both fields and methods can be static, and this will be reflected in Arucas too.
You are also able to create constructors and operator methods, these cannot be static.
Constructors have the same rules as methods, except they must return `void`.
Operator methods have the same rules as methods, but can only be assigned to valid operations.
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


