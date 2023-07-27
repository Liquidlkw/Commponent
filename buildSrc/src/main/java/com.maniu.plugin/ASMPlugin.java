package com.maniu.plugin;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_8;

import com.android.build.gradle.BaseExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.io.FileOutputStream;

//groovy   和 java
public class ASMPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        BaseExtension baseExtension = project.getExtensions()
                .getByType(BaseExtension.class);

        baseExtension.registerTransform(new ASMTransform());
//
//        byte[] genClassByte = genClass();
//        try {
//             //输出Class字节码文件
//            FileOutputStream fos = new FileOutputStream("C:\\Users\\Liquid\\Desktop\\MNCompont\\buildSrc\\src\\main\\java\\User.class");
//            fos.write(genClassByte);
//            fos.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


    }

    public static byte[] genClass() {
        ClassWriter classWriter = new ClassWriter(0);
        //生成类
        classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, "com/maniu/router/User",
                null, "java/lang/Object", null);
        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC,
                "<init>", "()V", null, null);
        methodVisitor.visitCode();
        //没有javac的帮助不会生成this  为了能够用this
        Label label0 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitLineNumber(3, label0);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object",
                "<init>", "()V", false);
        methodVisitor.visitInsn(RETURN);
        Label label1 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitLocalVariable("this", "Lasm/User;", null, label0,
                label1, 0);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();
        classWriter.visitEnd();
        System.out.println("------------genClass end");
        return classWriter.toByteArray();
    }
}
