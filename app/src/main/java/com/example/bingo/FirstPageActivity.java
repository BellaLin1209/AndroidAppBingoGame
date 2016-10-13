package com.example.bingo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by Bella on 2016/10/11.
 */

public class FirstPageActivity extends Activity {

    Button btn_startgame;
    LinearLayout layoutpage;
    String isFirstIn = "false";//判斷是否第一次開啟程式

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firstpage_layout);
        Log.e("log_tag", "LogingActivity onCreate in");

        layoutpage = (LinearLayout) findViewById(R.id.layoutpage);
        btn_startgame = (Button) findViewById(R.id.btn_startgame);

        //判斷是否為第一次開啟程式
        isFirstIn = getConfig(FirstPageActivity.this,
                "Config", "isFirstIn", "");//取得記憶是否為第一次登入

        if (isFirstIn.equals("false")) {

            //記憶帳號,密碼,email,公司名稱,介紹,ip位置
            setConfig(FirstPageActivity.this, "Config", "isFirstIn", "true");


            //開啟遊戲介紹頁面
            layoutpage.setVisibility(View.VISIBLE);
            btn_startgame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //換頁囉~
                    Intent intent = new Intent();
                    intent.setClass(FirstPageActivity.this, LoginActivity.class);
                    FirstPageActivity.this.startActivity(intent);
                    finish();
                }
            });
        } else {

            //換頁囉~
            Intent intent = new Intent();
            intent.setClass(FirstPageActivity.this, LoginActivity.class);
            FirstPageActivity.this.startActivity(intent);
            finish();

        }


    }

    /**
     * SharedPreferences暫時記憶 開始
     **/
    //設定檔儲存
    public static void setConfig(Context context, String name, String key,
                                 String value) {
        SharedPreferences settings = context.getSharedPreferences(name, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.putString(key, value);
        PE.commit();
    }

    //設定檔讀取
    public static String getConfig(Context context, String name, String
            key, String def) {
        SharedPreferences settings = context.getSharedPreferences(name, 0);
        return settings.getString(key, def);
    }
}
