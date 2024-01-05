package org.homs.lechugascript.compiled;

import org.homs.lechugascript.parser.Parser;
import org.homs.lechugascript.parser.ast.Ast;
import org.homs.lechugascript.tokenizer.Tokenizer;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class Compiler {

    private final String template;
    private final String targetDirectory;

    public Compiler(String template, String targetDirectory) {
        this.template = template;
        this.targetDirectory = targetDirectory;
    }

    public Class<?> compile(String packageName, String className, String program, String sourceDesc) throws IOException, ClassNotFoundException {

        Tokenizer tokenizer = new Tokenizer(program, sourceDesc);
        Parser parser = new Parser(tokenizer);
        List<Ast> asts = parser.parse();

        var visitor = new AstVisitor();

        String returnVarname = "null";
        for (var ast : asts) {
            returnVarname = visitor.visit(ast);
        }

        String source = template
                .replace("{packageName}", packageName)
                .replace("{className}", className)
                .replace("{visitor}", visitor.toString())
                .replace("{returnVarname}", returnVarname);

        // Save source in .java file.
        File root = new File(targetDirectory);

        File sourceFile = new File(root, packageName.replace('.', '/') + "/" + className + ".java");
        if(sourceFile.exists()) {
            sourceFile.delete();
        }
        sourceFile.getParentFile().mkdirs();
        Files.write(sourceFile.toPath(), source.getBytes(StandardCharsets.UTF_8));

        // Compile source file.
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, System.out, System.err, /*"-g:none",*/ sourceFile.getPath());

        // Load and instantiate compiled class.
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{root.toURI().toURL()});
        Class<?> cls = Class.forName(packageName + "." + className, true, classLoader);

        return cls;

//        // invoke
//        Object instance = cls.getDeclaredConstructor().newInstance();
//        var result = instance.getClass().getDeclaredMethod("test").invoke(instance);
//        System.out.println("=> " + result);
    }
}
