package com.maniu.merber;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.maniu.annotation.BindPath;

@BindPath("meber/MerberActivity1")
public class MerberActivity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
    }
}