package com.example.bingo.MySQL;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by bellalin on 2015/7/15.
 */
public class MySQL_update extends AsyncTask<Void, Void, Void> {

    String account;// ,password,email,com_name,com_intro;
    int room1, room2, room3, room4, room5;
    String result = null;
    String text = "";

    String roomName;
    int roomStatus;

//    public MySQL_update(String acc, int r1, int r2, int r3, int r4, int r5) {
//        Log.e("log_tag MySQL_update", "MySQL_update in ");
//        account = acc;
//        room1 = r1;
//        room2 = r2;
//        room3 = r3;
//        room4 = r4;
//        room5 = r5;
//    }
    public MySQL_update(String acc, String room, int status) {
        Log.e("log_tag MySQL_update", "MySQL_update in ");
        account = acc;
        roomName = room ;
        roomStatus = status;
    }

    @Override
    protected Void doInBackground(Void... params) {
        // TODO Auto-generated method stub
        Log.e("log_tag MySQL_update", "doInBackground in ");


        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("account", account));
        nameValuePairs.add(new BasicNameValuePair("roomName", roomName + ""));
        nameValuePairs.add(new BasicNameValuePair("roomStatus", roomStatus + ""));
//        nameValuePairs.add(new BasicNameValuePair("room3", room3 + ""));
//        nameValuePairs.add(new BasicNameValuePair("room4", room4 + ""));
//        nameValuePairs.add(new BasicNameValuePair("room5", room5 + ""));


        InputStream is = null;
        HttpClient httpclient = null;
        try {
            httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(
                    "http://gingomultiplayergame.comxa.com/bingo/update_Room.php");//http://gingomultiplayergame.comxa.com/bingo/insertLogin.php
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            Log.e("log_tag MySQL_update", "pass 1 connection success ");
        } catch (Exception e) {

            Log.e("log_tag MySQL_update", "Fail 1" + e.toString());
        }

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
            Log.e("log_tag MySQL_update", "pass 2:success:" + result);

        } catch (Exception e) {
            Log.e("log_tag MySQL_update", "Fail 2:" + e.toString());
        }
        try {
            System.out.println("result結果=" + result);
            JSONObject json_data = new JSONObject(result);
            int code = (json_data.getInt("success"));
            if (code == 1) {
                text = "新增申請成功!!";
            } else {
                text = "新增申請失敗!!";
            }
        } catch (Exception e) {
            Log.e("log_tag MySQL_update", "Fail 3:" + e.toString());

        }
        httpclient.getConnectionManager().shutdown();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.e("log_tag MySQL_update", "MySQL_update: " + text);
        super.onPostExecute(aVoid);
    }
}
