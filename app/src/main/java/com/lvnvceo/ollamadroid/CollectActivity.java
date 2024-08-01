package com.lvnvceo.ollamadroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CollectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        ImageButton settingsButton=findViewById(R.id.buttonSettings);
        // todo:待显示
        // 跳转到设置界面
        settingsButton.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(CollectActivity.this, SettingsActivity.class); //当前活动对象，目标活动对象
            startActivity(settingsIntent);
        });
        // 返回上一级
        Button backButton=findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            finish();
        });
    }
}