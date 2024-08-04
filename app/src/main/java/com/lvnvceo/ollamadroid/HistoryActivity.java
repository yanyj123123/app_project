package com.lvnvceo.ollamadroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.lvnvceo.ollamadroid.ollama.ChatItem;
import com.lvnvceo.ollamadroid.utils.ChatHistoryUtils;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();
    private static final String PREFERENCES_KEY = "ChatApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ImageButton deleteButton=findViewById(R.id.buttonDelete);

        // 删除历史记录
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
        // 返回上一级
        Button backButton=findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            finish();
        });
        // 显示聊天记录
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 获取 SharedPreferences 实例
        sharedPreferences = getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);
        ChatHistoryUtils chatHistoryUtils=new ChatHistoryUtils(this);
        // todo 通过ChatItem获取聊天记录
        List<ChatItem> items = chatHistoryUtils.getChatItemsFromSharedPreferences();
        // todo 通过DateItem获取聊天记录
        List<Object> items2 = chatHistoryUtils.getChatItemsFromSharedPreferences2();

        DateAdapter2 adapter = new DateAdapter2(items2);
        recyclerView.setAdapter(adapter);
    }
    private void showDeleteConfirmationDialog() {
        ChatHistoryUtils chatHistoryUtils=new ChatHistoryUtils(this);
        new AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("您确定要删除全部历史记录吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        chatHistoryUtils.clearChatHistoryAll();
                        recreate();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }


}