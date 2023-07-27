package com.maniu.merber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.maniu.annotation.BindPath;
import com.maniu.arouter.ARouter;

@BindPath("merber/MerberActivity")
public class MerberActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
    }

    public void jumpLogin(View view) {
//        Intent intent = new Intent(this, LoginActivity.class);
        ARouter.getInstance().jumpActivity(this, "login/login", null);
    }
}