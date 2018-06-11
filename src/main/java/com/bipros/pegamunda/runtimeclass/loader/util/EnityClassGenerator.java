package com.bipros.pegamunda.runtimeclass.loader.util;

import com.bipros.pegamunda.baserepo.Calculator;
import com.bipros.pegamunda.entity.BaseEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.springframework.asm.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import java.util.Arrays;
import java.util.List;

import static org.springframework.asm.Opcodes.*;

public class EnityClassGenerator {

    /*@Autowired
    EntityManager entityManager;*/

    /*public EntityManager getEntityManager() {
        return entityManager;
    }*/

    public static final String packageName = "com.bipros.pegamunda.generated.enity";
    public static final String packageNameDir = packageName.replace(".", "/");

    public static ClassWriter generateClass(String className) {
        final ClassWriter WRITER = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassWriter classWriter = createClassWriter(WRITER, className);
        constructorCreater(WRITER);
        createMethod(WRITER);
        createFields(WRITER);
        //Create An Entity Class
        createDatabaseTable(className);
        return WRITER;
    }

    private static void createDatabaseTable(String className) {
        /*String SQL_QUERY = "CREATE TABLE IF NOT EXISTS "+className+" (firstName text, lastName text, age text);";
        try(Session session = new EnityClassGenerator().getEntityManager().unwrap(Session.class)) {
            Transaction transaction = session.beginTransaction();
            NativeQuery sqlQuery = session.createSQLQuery(SQL_QUERY);
            sqlQuery.executeUpdate();
            transaction.commit();
        } catch (Exception e) {

        }*/


    }

    private static void createFields(ClassWriter writer) {
        //Expect list of String which will represent as fields
        List<String> fields = Arrays.asList("firstName:String", "lastName:String", "age:String");
        FieldVisitor fieldVisitor = null;
        for (String field: fields) {
            String[] fieldSplit = field.split(":");
            String descriptor = Type.getDescriptor(String.class);
            if (fieldSplit[1].equalsIgnoreCase("integer")) {
                descriptor = Type.getDescriptor(Integer.class);
            }
            fieldVisitor = writer.visitField(ACC_PUBLIC, fieldSplit[0], descriptor, null, null);

            //Create getter and setter method for each filed
            createGetterMethod(field, writer);
            createSetterMethod(field, writer);
        }
        //FieldVisitor fieldVisitor = writer.visitField(ACC_PUBLIC, "firstName", Type.getDescriptor(String.class), null, null);
        fieldVisitor.visitEnd();
    }

    private static void createSetterMethod(String field, ClassWriter writer) {
        String splittedField[] =  field.split(":");
        String methodName = "set"+splittedField[0].substring(0,1).toUpperCase()+splittedField[0].substring(1, splittedField[0].length());
        Type type = Type.CHAR_TYPE;
        if (splittedField[1].equalsIgnoreCase("integer")) {
            type = Type.INT_TYPE;
        }

        MethodVisitor methodVisitor = writer.visitMethod(
                ACC_PUBLIC,
                methodName,
                Type.getMethodDescriptor(type, type),
                null,
                null
        );

        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(ILOAD, 0);
        methodVisitor.visitVarInsn(ILOAD, 1);
        methodVisitor.visitFieldInsn(PUTFIELD, Type.getInternalName(String.class), splittedField[0], Type.getDescriptor(String.class));
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitEnd();

        writer.visitEnd();


    }

    private static void createGetterMethod(String field, ClassWriter writer) {
        String splittedField[] =  field.split(":");
        String methodName = "get"+splittedField[0].substring(0,1).toUpperCase()+splittedField[0].substring(1, splittedField[0].length());
        Type type = Type.CHAR_TYPE;
        if (splittedField[1].equalsIgnoreCase("integer")) {
            type = Type.INT_TYPE;
        }

        MethodVisitor methodVisitor = writer.visitMethod(
                ACC_PUBLIC,
                methodName,
                Type.getMethodDescriptor(type, type),
                null,
                null
        );
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(ILOAD, 0);
        methodVisitor.visitFieldInsn(GETFIELD, Type.getInternalName(String.class), splittedField[0], Type.getDescriptor(String.class));
        methodVisitor.visitInsn(ARETURN);
        writer.visitEnd();
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
        writer.visit(V1_7,                              // Java 1.7
                ACC_PUBLIC,                         // public class
                packageNameDir + "/" + className,    // package and name
                null,                               // signature (null means not generic)
                BaseEntity.class.getName().replace(".", "/"),                 // superclass
                new String[]{Calculator.class.getName().replace(".", "/")}); // interfaces
        /**
         * Add annotation @Entity to the class
         */
        String descriptor = Type.getDescriptor(Entity.class);
        AnnotationVisitor annotationVisitor = writer.visitAnnotation(descriptor, true);
        annotationVisitor.visitEnd();
        return writer;
    }


}
