package com.example.bingo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends Activity {
    Button register ;
    EditText hostId,hostPassword ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        hostId = (EditText)findViewById(R.id.ed_id);
        hostPassword = (EditText)findViewById(R.id.ed_password);
        register = (Button)findViewById(R.id.btn_ok);
        register.setOnClickListener(insert);
    }
    public View.OnClickListener insert = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String id="";
            String password="";
            id = hostId.getText().toString() ;
            password=hostPassword.getText().toString();

            if(id.equals("") || password.equals("")){
                Toast.makeText(SettingActivity.this, "沒有輸入完整喔", Toast.LENGTH_SHORT).show();
            }else {
//                new MySQL_Login(id,password ).execute() ;
//                Intent intent = new Intent();
//                intent.setClass(SettingActivity.this, LoginActivity.class);
//                startActivity(intent);

            }

        }
    } ;
    public static void setInsertText( String text )
    {
        //ins_ifo.setText(text);
    }
}
