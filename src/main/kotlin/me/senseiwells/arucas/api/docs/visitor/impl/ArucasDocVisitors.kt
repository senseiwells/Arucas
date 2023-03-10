package me.senseiwells.arucas.api.docs.visitor.impl

import me.senseiwells.arucas.api.ArucasAPI
import me.senseiwells.arucas.api.docs.visitor.ArucasDocParser
import me.senseiwells.arucas.utils.FileUtils.ensureExists
import java.nio.file.Files
import java.nio.file.Path

object ArucasDocVisitors {
    @JvmStatic
    fun generateDefault(path: Path, api: ArucasAPI) {
        val jsonVisitor = JsonDocVisitor()
        val markdownVisitor = MarkdownDocVisitor()
        val vscVisitor = VSCSnippetDocVisitor()
        ArucasDocParser(api).addVisitors(jsonVisitor, markdownVisitor, vscVisitor).parse()
        val jsonPath = path.ensureExists().resolve("json").ensureExists()
        val mdPath = path.resolve("markdown").ensureExists()
        val snippetPath = path.resolve("snippets").ensureExists()
        Files.writeString(snippetPath.resolve("ArucasSnippets.json"), vscVisitor.getJson())
        Files.writeString(jsonPath.resolve("AllDocs.json"), jsonVisitor.getJson())
        api.generateNativeFiles(path.resolve("libs"))
        Files.writeString(mdPath.resolve("Extensions.md"), markdownVisitor.getExtensions())
        Files.writeString(mdPath.resolve("Classes.md"), markdownVisitor.getClasses())
    }
}