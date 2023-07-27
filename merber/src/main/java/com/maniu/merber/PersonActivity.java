package com.maniu.merber;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.maniu.annotation.BindPath;

//tinker

//application    extends  Application
//key asm  开发  感觉   架构
//
@BindPath("meber/PersonActivity")
public class PersonActivity extends Activity {//TypeElement
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
    }
}