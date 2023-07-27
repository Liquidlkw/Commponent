package com.maniu.arouter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dalvik.system.DexFile;

public class ARouter {
    //有一个容器  装载了所有模块中的Activity的类对象 路由表
    public static Map<String, Class<? extends Activity>> map;

    @SuppressLint("StaticFieldLeak")
    private static final ARouter aRouter = new ARouter();

    private ARouter() {
        map = new HashMap<>();
    }


    public static ARouter getInstance() {
        return aRouter;
    }

    public void addActivity(String key, Class<? extends Activity> clazz) {
        if (key != null && clazz != null && !map.containsKey(key)) {
            map.put(key, clazz);
        }
    }

    //上下文
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public void init() {

    }


    public void jumpActivity(Activity activity, String key, Bundle bundle) {
        Class<? extends Activity> aClass = map.get(key);

        if (aClass == null) {
            return;
        }
        Intent intent = new Intent(activity, aClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivity(intent);
    }

    /**
     * 通过包名获取这个包下面的所有的类名
     *
     * @param packageName
     * @return
     */
    private List<String> getClassName(String packageName) {
        //创建一个class对象的集合
        List<String> classList = new ArrayList<>();
        try {
            //把当前应有的apk存储路径给dexFile
            DexFile df = new DexFile(context.getPackageCodePath());
//            不会发生类加载  性能    5000次  性能
            Enumeration<String> entries = df.entries();
            while (entries.hasMoreElements()) {
                String className = (String) entries.nextElement();
                if (className.contains(packageName)) {
                    classList.add(className);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classList;
    }

}
