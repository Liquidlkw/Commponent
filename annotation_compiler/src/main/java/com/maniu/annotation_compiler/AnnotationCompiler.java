package com.maniu.annotation_compiler;

import com.google.auto.service.AutoService;
import com.maniu.annotation.BindPath;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

//javac 注解处理器
@AutoService(Processor.class)
@SupportedOptions("moduleName")
public class AnnotationCompiler extends AbstractProcessor {
    //生成代码的工具
    Filer filer;
    private String moduleName;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        Map<String, String> options = processingEnv.getOptions();
        moduleName = options.get("moduleName");
        System.out.println("----moduleName--------" + moduleName);
    }


    //处理什么注解
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(BindPath.class.getCanonicalName());
        return types;
    }

    //jdk版本
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return processingEnv.getSourceVersion();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (!set.isEmpty()) {
            //获得被注解的类集合
            Set<? extends Element> routeElements = roundEnvironment.getElementsAnnotatedWith
                    (BindPath.class);
            generatedClass(routeElements);
            return true;
        }
        return false;


    }

    private void generatedClass(Set<? extends Element> routeElements) {
        StringBuilder sb = new StringBuilder();
        sb.append("package com.maniu.routers;\n");
        sb.append("import android.app.Activity;\n");
        sb.append("import com.maniu.arouter.IRouter;\n");
        sb.append("import java.util.Map;\n");
        for (Element element : routeElements) {
            TypeElement typeElement = (TypeElement) element;
            sb.append("import ");
            sb.append(typeElement.getQualifiedName());
            sb.append(";\n");
        }

        //类
        sb.append("public class ");
        sb.append(moduleName);
        sb.append("Router implements IRouter{\n");
        sb.append("@Override\n");
        sb.append("public void putActivity(Map<String, Class<? extends Activity>> routes){\n");


        for (Element element : routeElements) {
            //获得注解
            BindPath route = element.getAnnotation(BindPath.class);
            //函数体 paths.put(xx,xx.class)
            sb.append("routes.put(\"");
            sb.append(route.value());
            sb.append("\",");
            sb.append(element.getSimpleName());
            sb.append(".class);\n");
        }


        sb.append("}\n");
        sb.append("}");


        try {
            //创建 Java文件
            JavaFileObject sourceFile = filer.createSourceFile("com.maniu.routers." + moduleName +
                    "Router");
            //输出字符串
            OutputStream outputStream = sourceFile.openOutputStream();
            outputStream.write(sb.toString().getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    @Override
//    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
////           1         elemtn
////        MerberActivity   PersonActivity 是   elements
//        Set<? extends Element> elements =roundEnvironment.getElementsAnnotatedWith(BindPath.class);
////        零部件      MerberActivity  长城汽车 的   所有零部件    PersonActivity 比亚迪 的   所有零部件
//        Map<String,String> map = new HashMap<>();
//        for (Element element : elements) {
////写文件
//            TypeElement typeElement = (TypeElement) element;
//            String activityName = typeElement.getQualifiedName().toString();
////            key    meber/PersonActivity.class
//            String key = typeElement.getAnnotation(BindPath.class).value();
//            map.put(key,activityName+".class");
//        }
//        if(map.size() >0){
////写文件
//            //生成工具类  然后写代码  生成代码  + 修改代码  时间戳
//            //工具类的类  加伤moudle名
//            String className = "ActivityUtil"+"Merber";
//            Writer writer = null;
//            try {
//
//                writer= filer.createSourceFile("com.maniu.util." + className).openWriter();
//                StringBuffer stringBuffer = new StringBuffer();
//                stringBuffer.append("package com.maniu.util;\n");
//
//                stringBuffer.append("import com.maniu.arouter.ARouter;\n");
//                stringBuffer.append("import com.maniu.arouter.IRouter;\n");
//                stringBuffer.append("public class " + className + " implements IRouter {\n");
//                stringBuffer.append("@Override\n");
//                stringBuffer.append("public void putActivity() {\n");
//                Iterator<String> iterator = map.keySet().iterator();
//                while (iterator.hasNext()){
//                    String key = iterator.next();
//                    String activityName = map.get(key);
//                    stringBuffer.append("ARouter.getInstance().addActivity(\""+key+"\","+activityName+");");
//
//                }
//                stringBuffer.append("\n}\n}");
//                writer.write(stringBuffer.toString());
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }finally {
//                if(writer!=null){
//                    try {
//                        writer.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//        return false;
//    }
}
