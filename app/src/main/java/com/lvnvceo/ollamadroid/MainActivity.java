package com.lvnvceo.ollamadroid;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;

import android.os.Bundle;

import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    /*
    该文件主要负责应用的主要UI和用户交互逻辑
    extends AppCompatActivity标明是一个活动类
    * */

    private OkHttpClient client = new OkHttpClient();
    ImageView connectionStatusImage;
    TextView connectionStatusText;
    Button goToChatButton;
    private EditText editOllamaURL;

    private DrawerLayout drawerLayout; //定义抽屉菜单

    @Override
    protected void onResume() {
        super.onResume();
        getConnectionStatus();
    }

    // onCreate活动创建时调用
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //调用父类的onCreate方法，以确保活动正确初始化。
        setContentView(R.layout.activity_main); //设置活动的内容视图为res/layout/activity_main.xml布局文件。

        ImageButton settingsButton = findViewById(R.id.buttonSettings); //设置按钮
        ImageButton menuButton = findViewById(R.id.buttonMenu); //菜单按钮
        connectionStatusImage = findViewById(R.id.imageConnectionStatus);
        connectionStatusText = findViewById(R.id.textConnectionStatus);
        goToChatButton = findViewById(R.id.goToChatButton);

        drawerLayout = findViewById(R.id.drawer_layout);



        // 获取屏幕宽度
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;

        // 设置 NavigationView 的宽度为屏幕宽度的三分之一
        NavigationView navigationView = findViewById(R.id.nav_view);
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) navigationView.getLayoutParams();
        params.width = screenWidth / 3;
        navigationView.setLayoutParams(params);

        // 菜单按钮点击事件
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println("history: "+loadStringData("history"));
                // 打开或关闭抽屉
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });


        // 菜单具体点击逻辑
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_collect) {
                    //todo 处理 collect 按钮点击事件
                    Intent collectIntent = new Intent(MainActivity.this, CollectActivity.class);
                    startActivity(collectIntent);
                } else if (itemId == R.id.nav_history) {
                    //todo 处理 history 按钮点击事件
                    Intent collectIntent = new Intent(MainActivity.this, HistoryActivity.class);
                    startActivity(collectIntent);
                } else if (itemId == R.id.nav_model_choose) {
                    //todo 处理 model_choose 按钮点击事件
                    Intent collectIntent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(collectIntent);
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        settingsButton.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class); //当前活动对象，目标活动对象
            startActivity(settingsIntent);
        });
        goToChatButton.setOnClickListener(v -> {
            Intent chatIntent = new Intent(MainActivity.this,ChatActivity.class);
            startActivity(chatIntent);
        });
        getConnectionStatus(); //检查当前的连接状态
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private void displayError() {
        connectionStatusImage.setImageResource(R.drawable.close_fill0_wght700_grad200_opsz48);
        connectionStatusText.setText(R.string.connection_test_failed);
        goToChatButton.setVisibility(View.INVISIBLE);
    }
    private void displayOK() {
        connectionStatusImage.setImageResource(R.drawable.check_fill0_wght700_grad200_opsz48);
        connectionStatusText.setText(R.string.connection_test_successful);
        goToChatButton.setVisibility(View.VISIBLE);

    }
    private void getConnectionStatus() {
        // 获取存储在 SharedPreferences 中的 URL
        String ollamaURLStr = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE).getString("ollama_url", null);

        // 声明 URL 对象
        URL ollamaURL;
        try {
            // 尝试将字符串转换为 URL 对象
            ollamaURL = new URL(ollamaURLStr);
        } catch (MalformedURLException ignored) {
            // 如果 URL 无效，显示 Snackbar 提示
            Snackbar.make(findViewById(R.id.textConnectionStatus),"Invalid URL",Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }

        // 如果 URL 没有主机部分，直接返回
        if(ollamaURL.getHost().equals("")) {
            return;
        }

        // 创建一个 HTTP 请求对象
        Request request = new Request.Builder()
                .url(ollamaURL)
                .build();

        // 使用 OkHttp 客户端发送请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // 请求失败时运行的代码
                runOnUiThread(() -> {
                    displayError();
                    e.printStackTrace();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // 请求成功时运行的代码
                if(!response.isSuccessful()) {
                    runOnUiThread(() -> {
                        displayError();
                    });
                    throw new IOException("Unexpected code " + response);
                } else {
                    runOnUiThread(() -> {
                        displayOK();
                    });
                }
            }
        });
    }
    public void saveStringData(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply(); // 使用 apply() 异步保存数据
    }
    // 从 SharedPreferences 中读取聊天记录
    public String loadStringData(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        return sharedPreferences.getString(key, ""); // 默认值为空字符串
    }
}