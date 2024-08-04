package com.lvnvceo.ollamadroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lvnvceo.ollamadroid.ollama.ChatRequest;
import com.lvnvceo.ollamadroid.ollama.ChatResponse;
import com.lvnvceo.ollamadroid.ollama.Messages;
import com.lvnvceo.ollamadroid.utils.ChatHistoryUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageButton settingsButton;
    private final OkHttpClient okHttpClient = new OkHttpClient();
    private ChatAdapter adapter;
    private final Messages llamaMessages = new Messages();
    private final Gson gson = new Gson();


    // Constants
    private static final String SETTINGS_KEY = "settings";
    private static final String MODEL_KEY = "model";
    private static final String OLLAMA_URL_KEY = "ollama_url";
    private static final String SYSTEM_PROMPT = "system_prompt";
    private List<ChatMessage> messages =new ArrayList<>();
    private TextView modelTextView;

    @Override
    protected void onResume() {
        // 在 Activity 从暂停状态恢复到活动状态时调用。
        super.onResume();

        // 获取 SharedPreferences 对象.   用于读写 SharedPreferences 文件中存储的键值对。
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SETTINGS_KEY, MODE_PRIVATE);

        // 更新 TextView 显示的文本
        //modelTextView.setText(sharedPreferences.getString(MODEL_KEY, ""));

        // 清空 messages 列表
        //messages.clear();

        // 清空 llamaMessages 中的 messages 列表
        //llamaMessages.messages.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recycler_view); //用于显示大数据集
        settingsButton = findViewById(R.id.buttonSettings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //设置布局管理器
        //modelTextView = findViewById(R.id.modelName);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SETTINGS_KEY, MODE_PRIVATE);
        adapter = new ChatAdapter(this,messages);
        recyclerView.setAdapter(adapter); //将adapter与试图绑定
        Button sendButton = findViewById(R.id.button_send);
        Button stopButton = findViewById(R.id.button_stop);
        ImageButton settingsButton=findViewById(R.id.buttonSettings);
        // 跳转到设置界面
        settingsButton.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(ChatActivity.this, SettingsActivity.class); //当前活动对象，目标活动对象
            startActivity(settingsIntent);
        });
        // 返回上一级
        Button backButton=findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            finish();
        });
        // Settings button click listener
        settingsButton.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(ChatActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        });

        // Stop button click listener
        stopButton.setOnClickListener(v -> {
            stopButton.setVisibility(View.INVISIBLE);
            sendButton.setVisibility(View.VISIBLE);
        });

        sendButton.setOnClickListener(v -> {
            stopButton.setVisibility(View.VISIBLE);
            sendButton.setVisibility(View.INVISIBLE);
            //获取用户输入
            EditText editTextMessage = findViewById(R.id.edit_text_message);
            Editable newTextMessage = editTextMessage.getText();
            ChatHistoryUtils chatHistoryUtils=new ChatHistoryUtils(this);
            String nowDate = chatHistoryUtils.getChatHistoryKey();
            //添加消息到adapter
            adapter.addMessage(new ChatMessage(R.drawable.baseline_account_circle_24, sharedPreferences.getString("username", "小艺"), newTextMessage.toString(),true,false,nowDate));
            adapter.addMessage(new ChatMessage(R.drawable.ic_launcher_foreground,"AI", "",false,false,nowDate));
            adapter.notifyItemInserted(messages.size() - 1);
            recyclerView.smoothScrollToPosition(messages.size() - 1);
            String input = editTextMessage.getText().toString();
            // 添加消息到大模型
            llamaMessages.messages.add(new Messages.Message("user", editTextMessage.getText().toString()));
            ChatRequest chatRequest = new ChatRequest(sharedPreferences.getString(MODEL_KEY, ""), llamaMessages.messages,true);
            // 创建请求体和请求
            RequestBody body = RequestBody.create(gson.toJson(chatRequest), MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(sharedPreferences.getString(OLLAMA_URL_KEY, "")+"/api/chat")
                    .post(body)
                    .build();
            // 发送请求
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        handleResponseError(sendButton, stopButton);
                    }

                    try (ResponseBody responseBody = response.body()) {
                        if (responseBody != null) {
                            stopButton.setOnClickListener(v -> {
                                try {
                                    response.close();
                                } catch (NetworkOnMainThreadException ignored)  {}
                                handleResponseError(sendButton,stopButton);
                            });
                            //System.out.println("messages0:  "+messages.get(0));
                            //System.out.println("messages1:  "+messages.get(1));
                            processResponseBody(responseBody,input, messages, sendButton, stopButton);
                        }
                    }
                }
            });
            editTextMessage.setText("");
        });
    }

    private void handleResponseError(Button sendButton, Button stopButton) {
        runOnUiThread(() -> {
            stopButton.setVisibility(View.INVISIBLE);
            sendButton.setVisibility(View.VISIBLE);
        });
    }

    private void processResponseBody(ResponseBody responseBody,String input, List<ChatMessage> messages, Button sendButton, Button stopButton) {
        // 更新textViewMessageContent
        TextView textViewMessageContent = recyclerView.getLayoutManager().getChildAt(((LinearLayoutManager) recyclerView.getLayoutManager()).getChildCount() - 1).findViewById(R.id.message_content);
        ChatResponse chatResponse = new ChatResponse();
        String line;
        StringBuilder fullResponse = new StringBuilder();

        // 构造聊天历史记录类
        ChatHistoryUtils chatHistoryUtils=new ChatHistoryUtils(this);
        // key是时间
        String chatHistoryKey = chatHistoryUtils.getChatHistoryKey();
        // 已经有的聊天记录
        List<String[]> chatHistoryMessages = chatHistoryUtils.loadChatHistory(chatHistoryKey);
        try {
            while ((line = responseBody.source().readUtf8Line()) != null) {
                //System.out.println(messages.size());
                chatResponse = gson.fromJson(line, ChatResponse.class);

                fullResponse.append(chatResponse.message.content);
                runOnUiThread(() -> {
                    textViewMessageContent.setText(fullResponse);
                    recyclerView.smoothScrollToPosition(recyclerView.getScrollBarSize());
                });
            }

            runOnUiThread(() -> {
                textViewMessageContent.setText(fullResponse.toString());
                recyclerView.smoothScrollToPosition(recyclerView.getScrollBarSize());
            });

            if (chatResponse.done) {
                //saveStringData("history",fullResponse.toString());
                chatHistoryUtils.saveChatHistory(chatHistoryMessages,chatHistoryKey,input,fullResponse.toString());

                //chatHistoryUtils.getChatItemsFromSharedPreferences();
                // todo 测试代码待删除 删除全部
                //chatHistoryUtils.clearChatHistoryAll();
                // todo 测试代码待删除
                //System.out.println(chatHistoryKey);
                //List<String[]> chatHistory = chatHistoryUtils.loadChatHistory(chatHistoryKey);
                // todo 测试代码待删除
                //System.out.println("chatHistory:   "+chatHistory.get(0)[0]);
                addMessage(chatResponse,fullResponse.toString(),stopButton,sendButton);

            }
        } catch (IOException | NumberFormatException | IllegalStateException e) {
            addMessage(chatResponse,fullResponse.toString(),stopButton,sendButton);
            e.printStackTrace();
        }
    }
    private void addMessage(ChatResponse finalChatResponse, String fullResponse,Button stopButton, Button sendButton) {
        runOnUiThread(() -> {
            adapter.removeMessage(adapter.getItemCount() - 1);
            ChatHistoryUtils chatHistoryUtils=new ChatHistoryUtils(this);
            String nowDate = chatHistoryUtils.getChatHistoryKey();
            adapter.addMessage(new ChatMessage(R.drawable.ic_launcher_foreground, "AI", fullResponse,false,false,nowDate));
            llamaMessages.messages.add(new Messages.Message(finalChatResponse.message.role, fullResponse));
            stopButton.setVisibility(View.INVISIBLE);
            sendButton.setVisibility(View.VISIBLE);
        });
    }

}
