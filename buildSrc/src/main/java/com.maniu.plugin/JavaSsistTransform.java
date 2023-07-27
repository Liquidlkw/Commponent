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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;


public class JavaSsistTransform extends Transform {
    ClassPool pool = ClassPool.getDefault();

    /**
     * Transform Task 命名，该返回名不是作为最后的命名，任务会自动补全该名字
     * Transform 文件夹名称
     **/
    @Override
    public String getName() {
        return "JavaSsistTransform";
    }

    /**
     * Transform 需要输入的内容类型
     * CLASSES Java代码
     * RESOURCES Java Resource 资源
     */
    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }


    /**
     * Transform范围
     * <p>
     * PROJECT 当前项目内容
     * SUB_PROJECTS 子项目内容
     * EXTERNAL_LIBRARIES 外部依赖库
     * TESTED_CODE 测试代码
     * PROVIDED_ONLY provider 方式的本地或者远程依赖
     * PROJECT_LOCAL_DEPS 项目本地依赖（local jars)
     * SUB_PROJECTS_LOCAL_DEPS 子项目的本地依赖（local jars）
     */
    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    /**
     * 是否支持增量编译
     */
    @Override
    public boolean isIncremental() {
        return false;
    }

    /**
     * 中转函数
     */
    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        //输入地=javac
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        //输出地=JavaSsistTransform
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();

        for (TransformInput input : inputs) {
            for (JarInput directoryInput : input.getJarInputs()) {
                //JAR
                File dest = outputProvider.getContentLocation(
                        directoryInput.getName(), directoryInput.getContentTypes(),
                        directoryInput.getScopes(),
                        Format.JAR
                );
                FileUtils.copyFile(directoryInput.getFile(), dest);
            }
            //---------------------------------------------------------------------------------
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                // classes
                //输入路径
                System.out.println("src=" + directoryInput.getFile().getAbsolutePath());
                //outputProvider   ----> 输出地
                File dest = outputProvider.getContentLocation(
                        directoryInput.getName(),
                        directoryInput.getContentTypes(),
                        directoryInput.getScopes(),
                        Format.DIRECTORY
                );
                System.out.println("dest=" + dest.getAbsolutePath());

                try {
                    String preFileName = directoryInput.getFile().getAbsolutePath();
                    pool.insertClassPath(directoryInput.getFile().getAbsolutePath());
                    //通过classes父目录  找到Router 类
                    findTarget(directoryInput.getFile(), preFileName);

                    //拷贝
                    FileUtils.copyDirectory(directoryInput.getFile(), dest);
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }


            }


        }


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
            if (filePath.contains("Router")) {
                System.out.println("0000000000000000000");
                modify(clazz, fileName);
            }

        }
    }


    /**
     * 修改Router init
     * Javassist提供了两种级别的API：源级别和字节码级别。如果用户使用源代码级API，他们可以不需要了解Java字节码的规范的前提下编辑类文件。
     * ASM是一个通用的Java字节码操作和分析框架。它可以直接以二进制形式修改现有类或动态生成类。
     */
    private void modify(File clazz, String fileName) {
        System.out.println("clazz   " + clazz.getAbsolutePath());
        String filePath = clazz.getAbsolutePath();
        String className = filePath.replace(fileName, "").replace("\\", ".")
                .replace("/", ".");
        //全类名
        String name = className.replace(".class", "")
                .substring(1);
        System.out.println("name  " + name);

        try {
            //目的 修改源码
            CtClass ctClass = pool.get(name);
            CtMethod ctMethod = ctClass.getDeclaredMethod("init");
            String body = "com.example.transform_demo.ActivityUtil.putActivity();";
            System.out.println("-->david insertBefore  " + name);
            ctMethod.insertBefore(body);
            System.out.println("-->david after  " + name);
            ctClass.writeFile(fileName);
            ctClass.detach();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
