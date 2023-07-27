package com.maniu.plugin;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ASMTransform extends Transform {
    /**
     * Arouter.class
     */
    static File destFile;

    /**
     * 路由总表
     */
    static ArrayList<String> registerList = new ArrayList<>();

    @Override
    public String getName() {
        return "ASMTransform";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }


    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }


    @Override
    public void transform(TransformInvocation transformInvocation)
            throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        //拿到transform javac中的文件
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        //遍历javac中的文件
        for (TransformInput input : inputs) {
            //---------------------jar---------------------------------------
            for (JarInput directoryInput : input.getJarInputs()) {
                File dest = outputProvider.getContentLocation(
                        directoryInput.getName(), directoryInput.getContentTypes(),
                        directoryInput.getScopes(),
                        Format.JAR
                );
                //处理jar包
                processJar(directoryInput.getFile(), dest);
                FileUtils.copyFile(directoryInput.getFile(), dest);
            }

            //  ------------------------------class------------------------------------
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                String dirName = directoryInput.getName();
                File dest = outputProvider.getContentLocation(
                        dirName, directoryInput.getContentTypes(),
                        directoryInput.getScopes(),
                        Format.DIRECTORY
                );
                FileUtils.copyDirectory(directoryInput.getFile(), dest);
            }
        }


        if (destFile != null) {
            RegisterCodeGenerator1.insertInitCodeTo(registerList, destFile);
        }


    }


    /**
     * 遍历所有jar包中的class
     */
    private void processJar(File src, File dest) throws IOException {
        JarFile file = new JarFile(src);
        Enumeration<JarEntry> enumeration = file.entries();
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = enumeration.nextElement();
            String entryName = jarEntry.getName();
            if (shouldProcessClass(entryName)) {
                System.out.println("-------------processJar---------" + entryName);
                InputStream inputStream = file.getInputStream(jarEntry);
                //检查类是否真的是Router
                checkClass(inputStream);
            } else if (entryName.equals("com/maniu/arouter/ARouter.class")) {
                System.out.println("-------------processJar ARouter---------" + entryName);
                destFile = dest;
            }
        }

    }

    /**
     * 检查是否是实现IRouter接口
     */
    private void checkClass(InputStream inputStream) throws IOException {
        ClassReader cr = new ClassReader(inputStream);
        ScanClassVisitor cv = new ScanClassVisitor(Opcodes.ASM5);
        cr.accept(cv, ClassReader.EXPAND_FRAMES);
        inputStream.close();
    }

    private boolean shouldProcessClass(String entryName) {
        return entryName != null && entryName.startsWith("com/maniu/routers");
    }

    private void findTarget(File clazz, String fileName) {
        if (clazz.isDirectory()) {
            File[] files = clazz.listFiles();
            for (File file : files) {
                findTarget(file, fileName);
            }
        } else {
            String filePath = clazz.getAbsolutePath();

            if (!filePath.endsWith(".class")) {
                return;
            }
            if (filePath.contains("R$") || filePath.contains("R.class")
                    || filePath.contains("BuildConfig.class")) {
                return;
            }
            String path = filePath.replace(fileName, "");
            path = path.replaceAll("\\\\", "/");
            if (shouldProcessClass(path)) {
                try {
                    System.out.println("-------------shouldProcessClass---------" + path);
                    //获得IRouter接口实现类，并记录
                    checkClass(new FileInputStream(filePath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //
    static class ScanClassVisitor extends ClassVisitor {
        public ScanClassVisitor(int api) {
            super(api);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces);
            if (interfaces != null) {
                for (String interfaceName : interfaces) {
                    if (interfaceName.equals("com/maniu/arouter/IRouter")) {
                        // 记录需要注册的apt生成类
                        if (!ASMTransform.registerList.contains(name)) {
                            ASMTransform.registerList.add(name);
                        }
                    }
                }


            }
        }
    }
}
