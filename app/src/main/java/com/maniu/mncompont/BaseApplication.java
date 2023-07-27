package com.maniu.mncompont;

import android.app.Application;

import com.maniu.arouter.ARouter;


public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        IRouter iRouter = new merberRouter();
//        iRouter.putActivity();
//
//        iRouter = new loginRouter();
//        iRouter.putActivity();

        ARouter.getInstance().init();
        ARouter.getInstance().setContext(this);
    }
}
