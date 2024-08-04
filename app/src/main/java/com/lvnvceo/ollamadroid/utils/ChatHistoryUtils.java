package com.lvnvceo.ollamadroid.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lvnvceo.ollamadroid.DateAdapter2;
import com.lvnvceo.ollamadroid.ollama.ChatItem;
import com.lvnvceo.ollamadroid.ollama.ChatItem2;
import com.lvnvceo.ollamadroid.ollama.DateItem;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ChatHistoryUtils {
    private SharedPreferences sharedPreferences;
    private Gson gson;

    private final Context context;
    private static final String PREFERENCES_KEY = "ChatApp";
    private static final String TAG = "HistoryActivity";

    private static final String FAVORITES_PREFS = "favorites_prefs";
    private static final String FAVORITES_KEY = "favorites_key";

    private DateAdapter2 adapter;
    public ChatHistoryUtils(Context context){
        this.sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        this.gson = new Gson();
        this.context=context;
    }
    // 保存历史聊天
    public void saveChatHistory(List<String[]> chatHistory,String keyName,String question,String message) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String[]chat=new String[]{question,message};
        chatHistory.add(chat);
        String json = gson.toJson(chatHistory);
        editor.putString(keyName, json);
        editor.apply(); // 使用 apply() 异步保存数据
    }

    // 读取聊天
    public List<String[]> loadChatHistory(String keyName) {
        String json = sharedPreferences.getString(keyName, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<String[]>>() {}.getType();
        return gson.fromJson(json, type);
    }
    // 获取当前日期
    public String getChatHistoryKey() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    // 使用chatItem
    public List<ChatItem> getChatItemsFromSharedPreferences() {
        Map<String, ?> allEntries = sharedPreferences.getAll();
        List<ChatItem> chatItems = new ArrayList<>();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String date = entry.getKey();
            String messagesJson = (String) entry.getValue();

            chatItems.add(new ChatItem(date, true)); // 添加日期项

            // 解析消息列表
            Type type = new TypeToken<List<String[]>>() {}.getType();
            List<String[]> messages = gson.fromJson(messagesJson, type);
            for (String[] message : messages) {
                chatItems.add(new ChatItem(message[0],message[1]));
            }
        }
        // todo:调试代码
//        for(ChatItem chatItem:chatItems){
//            System.out.println(chatItem.getInput()+": "+chatItem.getMessage());
//        }
        return chatItems;
    }
    // 使用DateItem
    public List<Object> getChatItemsFromSharedPreferences2() {
        Map<String, ?> allEntries = sharedPreferences.getAll();
        List<Object> items = new ArrayList<>();

        // 将聊天记录按日期分组
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String date = entry.getKey();
            String messagesJson = (String) entry.getValue();
            Log.d("HistoryActivity", "Loading data for date: " + date + ", messagesJson: " + messagesJson);

            items.add(new DateItem(date)); // 添加日期项
            // 解析消息列表
            Type type = new TypeToken<List<String[]>>() {}.getType();
            List<String[]> messages = gson.fromJson(messagesJson, type);

            Log.d(TAG, "Parsed messages: " + messages.size());

            for (String[] message : messages) {
                if (message.length >= 2) {
                    items.add(new ChatItem2(message[0], message[1]));
                    Log.d(TAG, "Adding ChatItem2: Question = " + message[0] + ", Answer = " + message[1]);
                }
                else {
                    Log.d(TAG, "Invalid message length: " + message.length);
                }
            }
        }

        // 添加详细日志
        for (Object item : items) {
            if (item instanceof DateItem) {
                Log.d("HistoryActivity", "DateItem: " + ((DateItem) item).getDate());
            } else if (item instanceof ChatItem2) {
                ChatItem2 chatItem = (ChatItem2) item;
                Log.d("HistoryActivity", "ChatItem: Question = " + chatItem.getQuestion() + ", Answer = " + chatItem.getAnswer());
            }
        }
        Log.d(TAG, "Loaded chat items: " + items.size());
        return items;
    }

    public void clearChatHistoryByDate(String date) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(date);
        // 提交更改
        editor.apply();



    }
    public void clearChatHistoryAll() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        if (adapter != null) {
            adapter.updateItems(new ArrayList<>());
            Log.d(TAG, "All chat history deleted.");
        } else {
            Log.e(TAG, "Adapter is null when trying to clear chat history.");
        }

        editor.apply();

        // 更新适配器的数据集并通知数据已更改

    }


    public void clearCollectAll() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FAVORITES_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();

        editor.apply();
    }

}
