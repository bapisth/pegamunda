package com.bipros.pegamunda.resources;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.lang.model.element.Modifier;
import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@RestController
@RequestMapping(path = "/class")
public class CreateClassResource {

    @GetMapping("/{name}/create")
    public String createClass(@PathVariable("name") String name) {
        String clzzName = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
        String packageName = "com.bipros.pegamunda.resources.genertaed";
        System.out.println(packageName);
        String packageNameDir = packageName.replace(".", "/");

        MethodSpec main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder(clzzName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(main)
                .build();

        JavaFile javaFile = JavaFile.builder("", helloWorld) //aSs
                .build();
        Path directory = null;
        try {

            directory = Paths.get(packageNameDir);
            System.out.println(directory.getFileName());
            javaFile.writeTo(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (directory != null) {
            compileJavaFile(packageNameDir, new File(clzzName + ".java"));
        }

        return "Class Successfully Created!";
    }

    private void compileJavaFile(String fileName, File file) {
        try {
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

            Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file));
            JavaCompiler.CompilationTask task = compiler.getTask(
                    null,
                    fileManager,
                    diagnostics,
                    null,
                    null,
                    compilationUnit);
            if (task.call()) {
                /** Load and execute *************************************************************************************************/
                System.out.println("Yipe");
                // Create a new custom class loader, pointing to the directory that contains the compiled
                // classes, this should point to the top of the package structure!
                Class<?> loadedClass;
                Object obj;
                try (URLClassLoader classLoader = new URLClassLoader(new URL[]{new File("./").toURI().toURL()})) {
                    // Load the class from the classloader by name....
                    loadedClass = classLoader.loadClass("testcompile.HelloWorld");
                    // Create a new instance...
                    obj = loadedClass.newInstance();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }

                /************************************************************************************************* Load and execute **/
            } else {
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    System.out.format("Error on line %d in %s%n",
                            diagnostic.getLineNumber(),
                            diagnostic.getSource().toUri());
                }
            }
            fileManager.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
