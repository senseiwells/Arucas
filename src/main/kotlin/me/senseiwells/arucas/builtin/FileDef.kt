package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.ClassDoc
import me.senseiwells.arucas.api.docs.ConstructorDoc
import me.senseiwells.arucas.api.docs.FunctionDoc
import me.senseiwells.arucas.classes.ClassInstance
import me.senseiwells.arucas.classes.CreatableDefinition
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.utils.Arguments
import me.senseiwells.arucas.utils.BuiltInFunction
import me.senseiwells.arucas.utils.ConstructorFunction
import me.senseiwells.arucas.utils.MemberFunction
import me.senseiwells.arucas.utils.Util.Types.FILE
import me.senseiwells.arucas.utils.Util.Types.STRING
import java.io.File

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
        examples = ["new File('foo/bar/script.arucas')"]
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
        desc = ["This returns the file of the working directory"],
        returns = [FILE, "the file of the working directory"],
        examples = ["File.getDirectory()"]
    )
    @Suppress("UNUSED_PARAMETER")
    private fun getDirectory(arguments: Arguments): ClassInstance {
        val filePath = System.getProperty("user.dir")
        return this.create(File(filePath))
    }

    override fun defineMethods(): List<MemberFunction> {
        return listOf(

        )
    }
}