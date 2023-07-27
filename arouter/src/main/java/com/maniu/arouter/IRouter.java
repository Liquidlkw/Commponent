package com.maniu.arouter;

import android.app.Activity;

import java.util.Map;

public interface IRouter {
    void putActivity(Map<String, Class<? extends Activity>> routes);
}
