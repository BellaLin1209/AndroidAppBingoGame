package com.example.bingo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//
public class SettingActivity extends Activity {

    TextView tv_logout;
    LinearLayout ly_logout, ly_register, ly_setserver, ly_howtouse, ly_about, ly_contact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        tv_logout = (TextView) findViewById(R.id.tv_logout);
        ly_logout = (LinearLayout) findViewById(R.id.ly_logout);
        ly_register = (LinearLayout) findViewById(R.id.ly_register);
        ly_setserver = (LinearLayout) findViewById(R.id.ly_setserver);
        ly_howtouse = (LinearLayout) findViewById(R.id.ly_howtouse);
        ly_about = (LinearLayout) findViewById(R.id.ly_about);
        ly_contact = (LinearLayout) findViewById(R.id.ly_contact);
        ly_logout.setOnClickListener(click);
        ly_register.setOnClickListener(click);
        ly_setserver.setOnClickListener(click);
        ly_howtouse.setOnClickListener(click);
        ly_about.setOnClickListener(click);
        ly_contact.setOnClickListener(click);


        //判斷是否已登入
        String loginstatus = getConfig(SettingActivity.this,
                "Config", "HostLogin", "false");//最後一欄為預設

        if (loginstatus.equals("false")) {
            ly_logout.setVisibility(View.GONE);
            ly_register.setVisibility(View.GONE);
        } else {
            ly_logout.setVisibility(View.VISIBLE);
            ly_register.setVisibility(View.VISIBLE);
        }


    }

    public View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ly_logout:
                cleanConfig(SettingActivity.this,
                        "Config");
                tv_logout.setText("已登出");

                break;
            case R.id.ly_register:
                Intent intent = new Intent();
                intent.setClass(SettingActivity.this, RegisterActivity.class);
                SettingActivity.this.startActivity(intent);
                finish();
                break;
            case R.id.ly_setserver:

                //AlertDialog 跳出詢問公司視窗
//                    Handler myHandler = new Handler();
//                    myHandler.postDelayed(myListAlertDialog, 1 * 1000);//幾秒後(delaySec)呼叫runTimerStop這個Runnable，再由這個Runnable去呼叫你想要做的事情

                break;
            case R.id.ly_howtouse:
                break;
            case R.id.ly_about:
                                    //改成關於我們
                new AlertDialog.Builder(SettingActivity.this)

                        .setTitle("關於")
                        .setMessage(R.string.about_content2)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();

                break;
            case R.id.ly_contact:
                break;

        }

        }
    };

    public static void setInsertText(String text) {
        //ins_ifo.setText(text);
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

    //初始化設定檔
    public static void cleanConfig(Context context, String name) {
        //初始化
        SharedPreferences settings = context.getSharedPreferences(name, 0);
        SharedPreferences.Editor PE = settings.edit();
        PE.clear();
        PE.commit();
    }
}
