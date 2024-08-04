package com.lvnvceo.ollamadroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.gson.reflect.TypeToken;
import com.lvnvceo.ollamadroid.utils.ChatHistoryUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<ChatMessage> favoriteMessages;

    private static final String FAVORITES_PREFS = "favorites_prefs";
    private static final String FAVORITES_KEY = "favorites_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        ImageButton settingsButton=findViewById(R.id.buttonDelete);
        // todo:待显示
        // 跳转到设置界面
        settingsButton.setOnClickListener(v -> {
            showDeleteConfirmationDialog();
        });
        // 返回上一级
        Button backButton=findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            finish();
        });
        // 显示收藏界面
        recyclerView = findViewById(R.id.recycler_view_collect);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        favoriteMessages = loadFavorites();

        //System.out.println(favoriteMessages);

        adapter = new ChatAdapter(this, favoriteMessages);
        recyclerView.setAdapter(adapter);

    }
    private List<ChatMessage> loadFavorites() {
        SharedPreferences sharedPreferences = getSharedPreferences(FAVORITES_PREFS, Context.MODE_PRIVATE);
        //System.out.println(sharedPreferences.getAll());
        Set<String> favorites = sharedPreferences.getStringSet(FAVORITES_KEY, new HashSet<>());
        List<ChatMessage> messages = new ArrayList<>();
        for (String json : favorites) {
            messages.add(ChatMessage.fromJson(json));
        }

        Collections.sort(messages, (m1, m2) -> {
            return Long.compare(m1.getCurrentTimeMillis(), m2.getCurrentTimeMillis());
        });
        return messages;
    }

    private void showDeleteConfirmationDialog() {
        ChatHistoryUtils chatHistoryUtils=new ChatHistoryUtils(this);
        new AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("您确定要删除全部收藏记录吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        chatHistoryUtils.clearCollectAll();
                        recreate();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}