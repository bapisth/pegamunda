package com.bipros.pegamunda.runtimeclass.loader.util;

import com.bipros.pegamunda.baserepo.Calculator;
import org.springframework.asm.ClassWriter;
import org.springframework.asm.MethodVisitor;

import static org.springframework.asm.Opcodes.*;

public class ClassGenerator {
    private static final ClassWriter WRITER = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    public static final String packageName = "com.bipros.pegamunda.generated";
    public static final String packageNameDir = packageName.replace(".", "/");

    public static ClassWriter generateClass(String className) {
        ClassWriter classWriter = createClassWriter(WRITER, className);
        constructorCreater(WRITER);
        createMethod(WRITER);
        return WRITER;
    }

    private static void createMethod(ClassWriter classWriter) {
        /* Build 'add' method */
        MethodVisitor mv = classWriter.visitMethod(
                ACC_PUBLIC,                         // public method
                "add",                              // name
                "(II)I",                            // descriptor
                null,                               // signature (null means not generic)
                null);                              // exceptions (array of strings)
        mv.visitCode();
        mv.visitVarInsn(ILOAD, 1);                  // Load int value onto stack
        mv.visitVarInsn(ILOAD, 2);                  // Load int value onto stack
        mv.visitInsn(IADD);                         // Integer add from stack and push to stack
        mv.visitInsn(IRETURN);                      // Return integer from top of stack
        mv.visitMaxs(2, 3);                         // Specify max stack and local vars
        classWriter.visitEnd();                              // Finish the class definition
    }

    private static void constructorCreater(ClassWriter classWriter) {
        MethodVisitor con = classWriter.visitMethod(
                ACC_PUBLIC,                         // public method
                "<init>",                           // method name
                "()V",                              // descriptor
                null,                               // signature (null means not generic)
                null);                              // exceptions (array of strings)
        con.visitCode();                            // Start the code for this method
        con.visitVarInsn(ALOAD, 0);                 // Load "this" onto the stack
        con.visitMethodInsn(INVOKESPECIAL,          // Invoke an instance method (non-virtual)
                Object.class.getName().replace(".", "/"),                 // Class on which the method is defined
                "<init>",                           // Name of the method
                "()V",                              // Descriptor
                false);                             // Is this class an interface?
        con.visitInsn(RETURN);                      // End the constructor method
        con.visitMaxs(1, 1);
    }

    private static ClassWriter createClassWriter(ClassWriter writer, String className) {

        WRITER.visit(V1_7,                              // Java 1.7
                ACC_PUBLIC,                         // public class
                packageNameDir+"/"+className,    // package and name
                null,                               // signature (null means not generic)
                Object.class.getName().replace(".", "/"),                 // superclass
                new String[]{Calculator.class.getName().replace(".", "/")}); // interfaces
        return WRITER;
    }


}
