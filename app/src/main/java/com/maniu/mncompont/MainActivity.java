package com.maniu.mncompont;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.maniu.arouter.ARouter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startActivityManiu(View view) {
        ARouter.getInstance().jumpActivity(this,"merber/MerberActivity", null);

    }
}