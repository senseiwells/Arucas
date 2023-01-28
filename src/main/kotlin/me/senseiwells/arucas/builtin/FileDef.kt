package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.ClassDoc
import me.senseiwells.arucas.api.docs.ConstructorDoc
import me.senseiwells.arucas.api.docs.FunctionDoc
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.classes.CreatableDefinition
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.utils.Arguments
import me.senseiwells.arucas.utils.BuiltInFunction
import me.senseiwells.arucas.utils.ConstructorFunction
import me.senseiwells.arucas.utils.MemberFunction
import me.senseiwells.arucas.utils.Util.Types.BOOLEAN
import me.senseiwells.arucas.utils.Util.Types.FILE
import me.senseiwells.arucas.utils.Util.Types.LIST
import me.senseiwells.arucas.utils.Util.Types.STRING
import me.senseiwells.arucas.utils.impl.ArucasList
import java.awt.Desktop
import java.io.File
import java.io.IOException
import java.nio.file.Files

@ClassDoc(
    name = FILE,
    desc = ["This class allows you to read and write files"]
)
class FileDef(interpreter: Interpreter): CreatableDefinition<File>(FILE, interpreter) {
    override fun defineConstructors(): List<ConstructorFunction> {
        return listOf(
            ConstructorFunction.of(1, this::construct)
        )
    }

    @ConstructorDoc(
        desc = ["This creates a new File object with set path"],
        params = [STRING, "path", "the path of the file"],
        examples = ["new File('foo/bar/script.arucas');"]
    )
    private fun construct(arguments: Arguments) {
        val instance = arguments.next()
        val pathAsString = arguments.nextPrimitive(StringDef::class)
        instance.setPrimitive(this, File(pathAsString))
    }

    override fun defineStaticMethods(): List<BuiltInFunction> {
        return listOf(
            BuiltInFunction.of("getDirectory", this::getDirectory)
        )
    }

    @FunctionDoc(
        isStatic = true,
        name = "getDirectory",
        desc = ["This returns the file of user directory"],
        returns = [FILE, "the file of the working directory"],
        examples = ["File.getDirectory();"]
    )
    @Suppress("UNUSED_PARAMETER")
    private fun getDirectory(arguments: Arguments): ClassInstance {
        val filePath = System.getProperty("user.dir")
        return this.create(File(filePath))
    }

    override fun defineMethods(): List<MemberFunction> {
        return listOf(
            MemberFunction.of("getName", this::getName),
            MemberFunction.of("read", this::read),
            MemberFunction.of("write", 1, this::write),
            MemberFunction.of("delete", this::delete),
            MemberFunction.of("exists", this::exists),
            MemberFunction.of("getSubFiles", this::getSubFiles),
            MemberFunction.of("resolve", 1, this::resolve),
            MemberFunction.of("createDirectory", this::createDirectory),
            MemberFunction.of("getPath", this::getPath),
            MemberFunction.of("getAbsolutePath", this::getAbsolutePath),
            MemberFunction.of("open", this::open)
        )
    }

    @FunctionDoc(
        name = "getName",
        desc = ["This returns the name of the file"],
        returns = [STRING, "the name of the file"],
        examples = ["File.getName();"]
    )
    private fun getName(arguments: Arguments): String {
        val file = arguments.nextPrimitive(this)
        return file.name
    }

    @FunctionDoc(
        name = "read",
        desc = ["This reads the file and returns the contents as a string"],
        returns = [STRING, "the contents of the file"],
        examples = ["file.read();"]
    )
    private fun read(arguments: Arguments): String {
        val file = arguments.nextPrimitive(this)
        try {
            return Files.readString(file.toPath())
        } catch (e: IOException) {
            runtimeError("There was an error reading the file '$file'", e)
        }
    }

    @FunctionDoc(
        name = "write",
        desc = ["This writes a string to a file"],
        params = [STRING, "string", "the string to write to the file"],
        examples = ["file.write('Hello World!');"]
    )
    private fun write(arguments: Arguments) {
        val file = arguments.nextPrimitive(this)
        val string = arguments.nextPrimitive(StringDef::class)
        try {
            file.writeText(string)
        } catch (e: IOException) {
            runtimeError("There was an error writing the file '$file'", e)
        }
    }

    @FunctionDoc(
        name = "delete",
        desc = ["This deletes the file"],
        returns = [BOOLEAN, "true if the file was deleted"],
        examples = ["file.delete();"]
    )
    private fun delete(arguments: Arguments): Boolean {
        val file = arguments.nextPrimitive(this)
        return file.delete()
    }

    @FunctionDoc(
        name = "exists",
        desc = ["This returns if the file exists"],
        returns = [BOOLEAN, "true if the file exists"],
        examples = ["file.exists();"]
    )
    private fun exists(arguments: Arguments): Boolean {
        val file = arguments.nextPrimitive(this)
        return file.exists()
    }

    @FunctionDoc(
        name = "getSubFiles",
        desc = ["This returns a list of all the sub files in the directory"],
        returns = [LIST, "a list of all the sub files in the directory"],
        examples = ["file.getSubFiles();"]
    )
    private fun getSubFiles(arguments: Arguments): ArucasList {
        val file = arguments.nextPrimitive(this)
        val files = file.listFiles()
        val list = ArucasList()
        files ?: return list
        for (subFile in files) {
            list.add(this.create(subFile))
        }
        return list
    }

    @FunctionDoc(
        name = "resolve",
        desc = ["This gets a resolves file object from the current one"],
        params = [STRING, "filePath", "the relative file path"],
        returns = [FILE, "the resolved file"],
        examples = ["file.resolve('child.txt');"]
    )
    private fun resolve(arguments: Arguments): File {
        val file = arguments.nextPrimitive(this)
        val string = arguments.nextPrimitive(StringDef::class)
        return file.resolve(string)
    }

    @FunctionDoc(
        name = "createDirectory",
        desc = ["This creates all parent directories of the file if they don't already exist"],
        returns = [BOOLEAN, "true if the directories were created"],
        examples = ["file.createDirectory();"]
    )
    private fun createDirectory(arguments: Arguments): Boolean {
        val file = arguments.nextPrimitive(this)
        return file.mkdirs()
    }

    @FunctionDoc(
        name = "getPath",
        desc = ["This returns the path of the file"],
        returns = [STRING, "the path of the file"],
        examples = ["file.getPath();"]
    )
    private fun getPath(arguments: Arguments): String {
        val file = arguments.nextPrimitive(this)
        return file.path
    }

    @FunctionDoc(
        name = "getAbsolutePath",
        desc = ["This returns the absolute path of the file"],
        returns = [STRING, "the absolute path of the file"],
        examples = ["file.getAbsolutePath();"]
    )
    private fun getAbsolutePath(arguments: Arguments): String {
        val file = arguments.nextPrimitive(this)
        return file.absolutePath
    }

    @FunctionDoc(
        name = "open",
        desc = ["This opens the file (as in opens it on your os)"],
        examples = ["file.open();"]
    )
    private fun open(arguments: Arguments) {
        val file = arguments.nextPrimitive(this)
        try {
            Desktop.getDesktop().open(file)
        } catch (e: Exception) {
            runtimeError("An error occurred while opening the file '$file'")
        }
    }
}