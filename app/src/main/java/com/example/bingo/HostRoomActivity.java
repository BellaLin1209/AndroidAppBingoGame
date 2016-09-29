package com.example.bingo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bingo.MySQL.MySQL_update;

/**
 * 新增808
 * 待寫880 : 判斷可能連線數目  (注意)FREE保送數
 * 2016/09/11修改  by  Bella
 **/
public class HostRoomActivity extends Activity {
    private EditText chatlist;            //聊天記錄
    private EditText userlist;            //用戶列表
    private EditText numlist;            //骰子訊息

    private Button getNumBtn;            //擲骰子
    private Button statusBtn;            //開始遊戲
    private Button assignedBtn;         //指派骰子

    private TextView prepareUser;        //玩家列表:加入人數


    //個頻道名單
    ArrayList<String> UserChannel1 = new ArrayList<String>();
    ArrayList<String> UserChannel2 = new ArrayList<String>();
    ArrayList<String> UserChannel3 = new ArrayList<String>();
    ArrayList<String> UserChannel4 = new ArrayList<String>();
    ArrayList<String> UserChannel5 = new ArrayList<String>();
    //連線傳輸設定
    private Socket s;
    private InputStream inStream;
    private OutputStream outStream;
    private RecvRunable recvRunable = new RecvRunable();
    private RefleshHandler recvhandler = new RefleshHandler();
    private RecvThread recvThread = new RecvThread();


    private String serverIP = "140.123.174.165"; //服務器IP地址預設
    private String userName = "host";//預設
    private String channel = "";//頻道(遊戲房)
    private String account = "";//公司account

    //防呆
    public Boolean islogin = false;
    public Boolean log = false;//看伺服器有否回應
    //人數
    public int login_people = 0;
    //準備人數
    public int is_prepare = 0;
    //加入名單
    public ArrayList<String> interUser = new ArrayList<String>();
    //骰子權限用戶
    public ArrayList<String> chanceUser = new ArrayList<String>();
    //紀錄骰子骰的次數
    public int count = 0;
    //訂好骰子順序
    int[] Dnum = new int[25];
    //計算最佳可能性從25開始減少
    int bestPosible = 25;
    //計算最佳可能性連線可能
    int bestLine = 0;
    //連線顏色(紅橙黃綠藍)
    int lineColor = 0;

    //以下為game設定
    public TextView one1, one2, one3, one4, one5;
    public TextView two1, two2, two3, two4, two5;
    public TextView three1, three2, three3, three4, three5;
    public TextView four1, four2, four3, four4, four5;
    public TextView five1, five2, five3, five4, five5;

    public Boolean c1 = false;
    public Boolean c2 = false;
    public Boolean c3 = false;
    public Boolean c4 = false;
    public Boolean c5 = false;
    public Boolean c6 = false;
    public Boolean c7 = false;
    public Boolean c8 = false;
    public Boolean c9 = false;
    public Boolean c10 = false;
    public Boolean c11 = false;
    public Boolean c12 = false;
    public Boolean c13 = false;
    public Boolean c14 = false;
    public Boolean c15 = false;
    public Boolean c16 = false;
    public Boolean c17 = false;
    public Boolean c18 = false;
    public Boolean c19 = false;
    public Boolean c20 = false;
    public Boolean c21 = false;
    public Boolean c22 = false;
    public Boolean c23 = false;
    public Boolean c24 = false;
    public Boolean c25 = false;

    public Boolean inIf1 = true;
    public Boolean inIf2 = true;
    public Boolean inIf3 = true;
    public Boolean inIf4 = true;
    public Boolean inIf5 = true;
    public Boolean inIf6 = true;
    public Boolean inIf7 = true;
    public Boolean inIf8 = true;
    public Boolean inIf9 = true;
    public Boolean inIf10 = true;
    public Boolean inIf11 = true;
    public Boolean inIf12 = true;

    //給用戶擲骰子權限
    public Boolean giveChance = false;

    //判斷下一個的可能性
    public int possible1 = 0;
    public int possible2 = 0;
    public int possible3 = 0;
    public int possible4 = 0;
    public int possible5 = 0;
    public int possible6 = 0;
    public int possible7 = 0;
    public int possible8 = 0;
    public int possible9 = 0;
    public int possible10 = 0;
    public int possible11 = 0;
    public int possible12 = 0;


    public String last = "";
    public int win = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//不休眠
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_room_layout);
        Log.e("BELLA", "onCreate enter1");
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //接TextView ID
        one1 = (TextView) findViewById(R.id.textView1);
        one2 = (TextView) findViewById(R.id.textView2);
        one3 = (TextView) findViewById(R.id.textView3);
        one4 = (TextView) findViewById(R.id.textView4);
        one5 = (TextView) findViewById(R.id.textView5);
        two1 = (TextView) findViewById(R.id.textView6);
        two2 = (TextView) findViewById(R.id.textView7);
        two3 = (TextView) findViewById(R.id.textView8);
        two4 = (TextView) findViewById(R.id.textView9);
        two5 = (TextView) findViewById(R.id.textView10);
        three1 = (TextView) findViewById(R.id.textView11);
        three2 = (TextView) findViewById(R.id.textView12);
        three3 = (TextView) findViewById(R.id.textView13);
        three4 = (TextView) findViewById(R.id.textView14);
        three5 = (TextView) findViewById(R.id.textView15);
        four1 = (TextView) findViewById(R.id.textView16);
        four2 = (TextView) findViewById(R.id.textView17);
        four3 = (TextView) findViewById(R.id.textView18);
        four4 = (TextView) findViewById(R.id.textView19);
        four5 = (TextView) findViewById(R.id.textView20);
        five1 = (TextView) findViewById(R.id.textView21);
        five2 = (TextView) findViewById(R.id.textView22);
        five3 = (TextView) findViewById(R.id.textView23);
        five4 = (TextView) findViewById(R.id.textView24);
        five5 = (TextView) findViewById(R.id.textView25);


        chatlist = (EditText) findViewById(R.id.chatlist);
        userlist = (EditText) findViewById(R.id.userlist);
        numlist = (EditText) findViewById(R.id.numlist);
        prepareUser = (TextView) findViewById(R.id.prepareUser);
        getNumBtn = (Button) findViewById(R.id.getNumBtn);
        statusBtn = (Button) findViewById(R.id.statusBtn);
        assignedBtn = (Button) findViewById(R.id.assignedBtn);
        statusBtn.setOnClickListener(new statusBtnListener());
        getNumBtn.setOnClickListener(new getNumBtnListener());
        assignedBtn.setOnClickListener(new assignedBtnListener());


        //設置chatlist , userlist ;
        chatlist.setCursorVisible(false);
        chatlist.setFocusable(false);
        chatlist.setFocusableInTouchMode(false);
        userlist.setCursorVisible(false);
        userlist.setFocusable(false);
        userlist.setFocusableInTouchMode(false);


        //取得記憶的ip與名稱
        //取得記憶的account
        account = LoginActivity.getConfig(HostRoomActivity.this,
                "Config", "db_account", "");
        serverIP = LoginActivity.getConfig(HostRoomActivity.this,
                "Config",
                "input_ip",
                "140.123.174.165");//最後一欄為預設
        userName = LoginActivity.getConfig(HostRoomActivity.this,
                "Config",
                "input_name", "host");//預設
        channel = LoginActivity.getConfig(HostRoomActivity.this,
                "Config",
                "input_channel",
                "1");//最後一欄為預設


        //註冊信息
        String loginInfo = "0\n" + userName + "\n" + "0\n" + "0";
        try {
            Log.e("BELLA", "try enter");
            s = new Socket(serverIP, 3204); //Port=3204 可自訂
            outStream = s.getOutputStream();
            inStream = s.getInputStream();
            //發送註冊信息
            outStream.write(loginInfo.getBytes("UTF-8"));
            //開啟接收線程
            recvThread.start();

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            Log.e("BELLA", "UnknownHostException enter");
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "連線失敗!請檢查伺服器IP!", Toast.LENGTH_SHORT).show();
            HostRoomActivity.this.finish();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e("BELLA", "IOException enter");
            Toast.makeText(getApplicationContext(), "連線失敗!請檢查連線!", Toast.LENGTH_SHORT).show();
            HostRoomActivity.this.finish();
        } catch (Exception e) {
            Log.e("BELLA", "Exception enter");
            Toast.makeText(getApplicationContext(), "連線失敗!請檢查網路!", Toast.LENGTH_SHORT).show();
            HostRoomActivity.this.finish();
        }


        //初始化設定
        getNumBtn.setEnabled(false);//鎖住擲骰子按鈕
        GetNum();//取得遊戲盤
    }

    /**
     * 連線設定 開始
     **/
    //接收線程
    public class RecvThread extends Thread {
        //stopThread
        private boolean isRunning = true;

        @Override
        public void run() {
            Log.e("BELLA", "RecvThread enter");
            // TODO Auto-generated method stub
            while (isRunning) {
                try {
                    if (inStream.available() > 0) {


                        //接收byte
                        byte[] recvByte = new byte[1024 * 2];
                        int recvCount;
                        if ((recvCount = inStream.read(recvByte)) < 0) {
                            break;
                        }

                        String[] cmdStr = recvByteToString(recvByte, recvCount);

                        for (int i = 0; i < 10; i++) {
                            if (cmdStr[i].compareTo("") != 0) {
                                Message msg = recvhandler.obtainMessage(1, 1, 1, (Object) cmdStr[i]);
                                recvhandler.sendMessage(msg);
                                //chatlist.append(cmdStr[i]);
                            } else
                                break;
                            //chatlist.append(Integer.toString(i));
                        }
                        //如果instream有兩個或者以上協議命令包，只會取到第一個
                        /*if (recvCount(recvByte) > 0)
                        {
							String recvStr = new String(recvByte , 0 , recvCount(recvByte) , "GBK");
							Message msg = recvhandler.obtainMessage(1, 1, 1, (Object)recvStr);
							recvhandler.sendMessage(msg);
						}*/
                    } else {
                        try {
                            Thread.sleep(1000);
                            if (!haveInternet()) {
                                finish();
                            }
                        } catch (Exception e) {
                        }
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    //HostRoomActivity.this.finish();
                }
            }
        }

        public void stopThread() {
            this.isRunning = false;
        }
    }

    //刷新聊天記錄handler  開始
    public class RefleshHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.e("BELLA", "RefleshHandler enter");
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (msg.what != 1)
                return;
            String str = (String) msg.obj;
            String[] splitStr = str.split("\n", 4);
            if (splitStr.length != 4) {
                //System.out.println(splitStr[0]);
                return;
            }
            String type = splitStr[0];
            final String fromuser = splitStr[1];
            final String touser = splitStr[2];
            String data = splitStr[3];

            //當前時間
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            String timeStr = df.format(new Date());

            String hostChannel[] = fromuser.split("_");
            log = true;

            //分析判斷信息type的類型
            switch (type) {
                //type == 1代表是群發
                case "1":
                    if (hostChannel[1].equals(channel)) {
                        //喊號
                        if (touser.equals("number")) {
                            numlist.append(data + "號\n");
                            //選取棋盤數字
                            pickNum(data);
                            getNumBtn.setEnabled(true);//開啟擲骰按鈕
                        }
                        //開始遊戲
                        else if (touser.equals("start")) {
                            statusBtn.setEnabled(false);
                            statusBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_cus));
                            statusBtn.setText("遊戲開始囉!");
                            chatlist.append(fromuser + "喊: 開始遊戲!" + "\n");

                        }
                        //有人喊幾條線
                        else if (touser.equals("line")) {
                            chatlist.append(fromuser + "喊:我" + data + "條線了!\n");
                            //更新每人目前連線數
                            updateLineNum(fromuser, data);
                            //更新用戶列表
                            userlist.getText().clear();
                            for (int i = 0; i < interUser.size(); i++) {
                                userlist.append(interUser.get(i).toString() + "\n");
                                Log.e("Hsiang", interUser.get(i).toString());
                            }
                        }
                        //有人贏了
                        else if (touser.equals("win")) {
                            chatlist.append(fromuser + "喊:" + data + "\n");
                            statusBtn.setEnabled(true);
                            getNumBtn.setEnabled(false);
                            assignedBtn.setEnabled(false);
                            statusBtn.setText("離開");
                            //五條線跳出Dialog
                            bingoDialog("恭喜" + fromuser + "已賓果!");
                        }
                        //廣播誰可以擲骰子
                        else if (touser.equals("chance")) {
                            chatlist.append(fromuser + "喊:" + data + "\n");
                        }
                        //用戶丟骰子
                        else if (touser.equals("userthrow")) {
                            pickNum(data);
                            chatlist.append(fromuser + "喊: " + data + "號\n");
                        } else {
                            chatlist.append("我是沒設定到的訊息~" + fromuser + " " + data + "\n");
                        }
                    }
                    break;
                //type == 2 表示私聊
                case "2":
                    Log.e("BELLA", "case 2");
                    if (data.equals("ready")) {
                        //加入已準備好的玩家名單
                        interUser.add(fromuser + " Ready");

                        is_prepare = interUser.size();
                        //更新用戶列表
                        userlist.getText().clear();
                        for (int i = 0; i < interUser.size(); i++) {
                            userlist.append(interUser.get(i).toString() + "\n");
                            Log.e("Hsiang", interUser.get(i).toString());
                        }
                        prepareUser.setText("玩家列表：" + is_prepare + "/" + login_people + "人");
                    } else if (data.equals("giveMeChance")) {
                        //指派按鈕背景變藍色
                        assignedBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_cus_blue));
                        chatlist.append(fromuser + "差一個有五條線!" + "\n");
                        chanceUser.add(fromuser);
                    }
                    break;
                //type == 4 遊戲開始
                case "4":
                    Log.e("BELLA", "case 4");
                    getNumBtn.setClickable(true);
                    statusBtn.setText("結束遊戲");
                    break;
                //type == 5 有人要賓果了
                case "5":
                    Log.e("BELLA", "case 5");
                    chatlist.append(timeStr + " " + fromuser + "喊賓果!");

                    new AlertDialog.Builder(HostRoomActivity.this)
                            .setTitle("提示視窗")
                            .setMessage(timeStr + "\n" + fromuser + "喊出賓果!是否要給他擲骰子?")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String sendStr = "6\n0\n" + fromuser + "\n0";
                                    try {
                                        byte[] sendByte = sendStr.getBytes("UTF-8");
                                        outStream.write(sendByte);

                                    } catch (Exception e) {
                                        // TODO: handle exception
                                        HostRoomActivity.this.finish();
                                    }
                                }
                            }).show();

                    break;
                //type == 6 給你喊號
                case "6":
                    Log.e("BELLA", "case 6");
                    //使用者這裡要寫
                    break;
                //type == 10 表示註冊成功
                case "10":
                    Log.e("BELLA", "case 10");
                    chatlist.append(timeStr + "\n系統:Host成功登入\n");
//                    chatlist.append(data + "\n");
                    interUser.add(userName + " Ready");//先把host加進去準備好名單中
                    islogin = true;
                    if (islogin) {
                        Toast.makeText(getApplicationContext(), "成功登入" + channel + "號遊戲房", Toast.LENGTH_SHORT).show();
                    }
                    break;
                //type == 11 表示註冊失敗，該用戶名已被別人註冊
                case "11":
                    Log.e("BELLA", "case 11");
                    chatlist.append(timeStr + "系統:");
                    chatlist.append(data + "\n");
                    Toast.makeText(getApplicationContext(), "暱稱名稱重複囉~請重新登入!", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                //type == 12 表示更新用戶列表
                case "12":
                    Log.e("BELLA", "case 12");
//                    //更新狀態列表
//                    chatlist.append(timeStr + " ");
//                    chatlist.append(data + "\n");
                    //更新用戶列表
                    userlist.getText().clear();
                    String[] login_people_str = data.split("\n");
                    for (int i = 0; i < login_people_str.length; i++) {
                        String[] name_channel = login_people_str[i].split("_");
                        putChannel(name_channel[0], name_channel[1]);
                        //Toast.makeText(UserRoomActivity.this,name_channel[0]+","+name_channel[1],Toast.LENGTH_SHORT).show();
                    }
                    getChannel(channel);
                    login_people = peopleChannel(channel);
                    prepareUser.setText("玩家列表：" + is_prepare + "/" + login_people + "人");
//                    login_people = login_people_str.length;//人數計算
                    break;
                //type == 13表示有用戶進入/離開聊天室
                case "13":
                    Log.e("BELLA", "case 13");
//                    String[] datasplit = data.split(" ");
                    //進入
//                    int index1 = data.indexOf("Welcome");
//                    if (index1 != -1) {
//                        interUser.add(datasplit[1]);
//                        login_people++;
//                    }
                    //離開
                    int index2 = data.indexOf("GoodBye");
                    if (index2 != -1) {
                        //更新用戶列表
//                        interUser.remove(datasplit[1]);
//                        login_people--;
                        prepareUserLeave(data);//準好名單中有人退出
                        //更新用戶列表
                        String name_channel[] = data.split("  |_");
                        removeChannel(name_channel[1], name_channel[2]);
                        Log.e("bye", name_channel[0] + "," + name_channel[1] + "," + name_channel[2]);
                    }
//                    prepareUser.setText("玩家列表：" + is_prepare + "/" + login_people + "人");
                    break;
                //type == 14表示發送的私聊信息出錯
                case "14":
                    Log.e("BELLA", "case 14");
                    //chatlist.append(timeStr + "發送的指派信息出錯\n");
                    break;


            }


        }
    }

    /**
     * 聲明接收線程 ;這種方法使用handler開啟的線程，優先級太高，
     * 會使整個Activity處於阻塞狀態;因此不用這種方法
     * 改用handler處理消息機制，如果接收到數據，就發送一個消息給handler，
     * 然後再由handler對接收的字符串做處理;
     **/
    class RecvRunable implements Runnable {

        public void run() {
            Log.e("BELLA", "RecvRunable enter");
            // TODO Auto-generated method stub

            try {
                if (inStream.available() > 0) {

                    byte[] readBuff = new byte[1024 * 2];
                    if (inStream.read(readBuff) < 0)
                        return;
                    String data = new String(readBuff, 0, recvCount(readBuff), "UTF-8");
                    System.out.println(data);
                    System.out.println(data.length());
                    chatlist.append(data);
                }
                recvhandler.postDelayed(recvRunable, 2000);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * Socket訊息優化 開始
     **/
    //計算從socket inputstream中讀取多少個字符
    public static int recvCount(byte[] in) {
        int i = 0;
        for (i = 0; i < in.length; i++) {
            if (in[i] == 0)
                return i;
        }
        return in.length;
    }

    //優化算法，從instream裡得到協議命令字符串數組;
    public static String[] recvByteToString(byte[] in, int recvCount) {
        String[] cmdStrArr = new String[10];
        int i = recvCount;
        int k;
        int strCount = 0;
        int offset = 0;
        for (k = 0; k < i; k++) {
            if (in[k] == 0) {
                try {
                    cmdStrArr[strCount] = new String(in, offset, k - offset, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                strCount++;
                offset = k + 1;
            }
        }
        //System.out.println(strCount);
        return cmdStrArr;
    }

    //用以計算Cmd的個數
    public static int cmdCount(String[] cmdStrArr) {
        String str = new String();
        int i;
        for (i = 0; i < cmdStrArr.length; i++) {
            if (str.compareTo(cmdStrArr[i]) == 0)
                return i;
        }
        return cmdStrArr.length;
    }


    /**
     * 按鈕監聽器設定 開始
     **/
    //骰子
    public class getNumBtnListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Log.e("BELLA", "getNumBtnListener");

            //random發送的數字
            count++;
            getNumBtn.setText("擲骰子\n" + "喊第" + count + "次");


            //傳送給大家
            String sendStr = "1\n" + userName + "\nnumber\n" + Dnum[count];

            try {
                byte[] sendByte = sendStr.getBytes("UTF-8");
                outStream.write(sendByte);
            } catch (Exception e) {
                // TODO: handle exception
                HostRoomActivity.this.finish();
            }


        }
    }

    //指派骰子手名單
    public class assignedBtnListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Log.e("Hsiang", "getNumBtnListener");
            Log.e("Hsiang chanceUser=", "" + chanceUser.size());


            new AlertDialog.Builder(HostRoomActivity.this)
                    .setTitle("指派名單")
                    .setItems(chanceUser.toArray(new String[chanceUser.size()]), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, final int which) {

                            //廣播指派
                            String sendStr1 = "1\n" + userName + "\nchance\n" + chanceUser.get(which);
                            try {
                                byte[] sendByte1 = sendStr1.getBytes("UTF-8");
                                outStream.write(sendByte1);
                            } catch (Exception e) {
                                // TODO: handle exception
                                Toast.makeText(getApplicationContext(), "指派失敗，請重啟遊戲", Toast.LENGTH_SHORT).show();
                                HostRoomActivity.this.finish();
                            }
                            Toast.makeText(getApplicationContext(), "指派" + chanceUser.get(which), Toast.LENGTH_SHORT).show();
                            chanceUser.clear();//指派完就清空重記
//                            //
//                            new Handler().postDelayed(new Runnable() {
//                                public void run() {
//                                    String sendStr2 = "2\n" + userName + "\n" + chanceUser.get(which) + "\nthrow";
//                                    try {
//                                        byte[] sendByte2 = sendStr2.getBytes("UTF-8");
//                                        outStream.write(sendByte2);
//                                    } catch (Exception e) {
//                                        // TODO: handle exception
//                                        Toast.makeText(getApplicationContext(), "指派失敗，請重啟遊戲", Toast.LENGTH_SHORT).show();
////                                        HostRoomActivity.this.finish();
//                                    }
//                                }
//                            }, 500);

                        }
                    }).show();

            Log.e("Hsiang chanceUser=", "" + chanceUser.size());
        }
    }

    //狀態按鈕監聽器
    public class statusBtnListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            //把擲骰子的數字訂好
            for (int i = 0; i < 25; i++) {
                Dnum[i] = (int) (Math.random() * 25 + 1);        // 將隨機數(1-49)放入 sixNum[i]
                for (int j = 0; j < i; ) {            // 與前數列比較，若有相同則再取亂數
                    if (Dnum[j] == Dnum[i]) {
                        Dnum[i] = (int) (Math.random() * 25 + 1);
                        j = 0;            // 避面重新亂數後又產生相同數字，若出現重覆，迴圈從頭開始重新比較所有數
                    } else j++;            // 若都不重複則下一個數
                }
            }


            if (statusBtn.getText().equals("開始遊戲")) {
                if (login_people == 1) {
                    Toast.makeText(getApplicationContext(), "目前只有你一人!請稍等其他玩家加入!", Toast.LENGTH_SHORT).show();
                } else if (is_prepare != login_people) {
                    Toast.makeText(getApplicationContext(), "還有玩家尚未準備好!請稍等!", Toast.LENGTH_SHORT).show();
                } else {
                    //更新資料庫
                    if (channel.equals("1")) {
                        new MySQL_update(account, "room1", 1).execute();
                    } else if (channel.equals("2")) {
                        new MySQL_update(account, "room2", 1).execute();
                    } else if (channel.equals("3")) {
                        new MySQL_update(account, "room3", 1).execute();
                    } else if (channel.equals("4")) {
                        new MySQL_update(account, "room4", 1).execute();
                    } else if (channel.equals("5")) {
                        new MySQL_update(account, "room5", 1).execute();
                    }

                    getNumBtn.setEnabled(true);
                    assignedBtn.setEnabled(true);
                    getNumBtn.setTextColor(Color.rgb(0, 0, 0));
                    getNumBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_cus_pink));//按紐背景變粉色
                    //    statusBtn.setText("結束遊戲");
                    statusBtn.setEnabled(false);
//                statusBtn.setText("結束遊戲");


                    String sendStr = "1\n" + userName + "\nstart\n0";
                    try {
                        byte[] sendByte = sendStr.getBytes("UTF-8");
                        outStream.write(sendByte);
                    } catch (Exception e) {
                        // TODO: handle exception
                        HostRoomActivity.this.finish();
                    }
                }
            } else if (statusBtn.getText().equals("離開")) {
                HostRoomActivity.this.finish();
            }
        }
    }


    /**
     * 遊戲設定 開始
     **/
    // 25個數字放入遊戲盤中
    public void GetNum() {

        int[][] TwentyFiveNum = new int[5][5];
        TwentyFiveNum = GetBingoCard();//取得遊戲盤 75個數字

        one1.setText(TwentyFiveNum[0][0] + "");
        one2.setText(TwentyFiveNum[0][1] + "");
        one3.setText(TwentyFiveNum[0][2] + "");
        one4.setText(TwentyFiveNum[0][3] + "");
        one5.setText(TwentyFiveNum[0][4] + "");
        two1.setText(TwentyFiveNum[1][0] + "");
        two2.setText(TwentyFiveNum[1][1] + "");
        two3.setText(TwentyFiveNum[1][2] + "");
        two4.setText(TwentyFiveNum[1][3] + "");
        two5.setText(TwentyFiveNum[1][4] + "");
        three1.setText(TwentyFiveNum[2][0] + "");
        three2.setText(TwentyFiveNum[2][1] + "");
        three3.setText(TwentyFiveNum[2][2] + "\n保送");//保送數為0
        three4.setText(TwentyFiveNum[2][3] + "");
        three5.setText(TwentyFiveNum[2][4] + "");
        four1.setText(TwentyFiveNum[3][0] + "");
        four2.setText(TwentyFiveNum[3][1] + "");
        four3.setText(TwentyFiveNum[3][2] + "");
        four4.setText(TwentyFiveNum[3][3] + "");
        four5.setText(TwentyFiveNum[3][4] + "");
        five1.setText(TwentyFiveNum[4][0] + "");
        five2.setText(TwentyFiveNum[4][1] + "");
        five3.setText(TwentyFiveNum[4][2] + "");
        five4.setText(TwentyFiveNum[4][3] + "");
        five5.setText(TwentyFiveNum[4][4] + "");
    }

    /**
     * 我新增了新的75個數的取數  ~~~~~ bella
     **/
    // 取得棋盤數字75數
    public int[][] GetBingoCard() {

        int[][] RowNum = new int[5][5]; // 五行五數


        // 第一直行(01 ~ 15)
        for (int i = 0; i < 5; i++) {
            RowNum[0][i] = (int) (Math.random() * 15 + 1); // 將隨機數(1-15)放入
            for (int j = 0; j < i; ) { // 與前數比較，若有相同則再取亂數
                if (RowNum[0][j] == RowNum[0][i]) {
                    RowNum[0][i] = (int) (Math.random() * 15 + 1);
                    j = 0; // 避面重新亂數後又產生相同數字，若出現重覆，迴圈從頭開始重新比較所有數
                } else
                    j++; // 若都不重複則下一個數
            }
        }

        // 第二直行(16 ~ 30)
        for (int i = 0; i < 5; i++) {
            RowNum[1][i] = (int) (Math.random() * 15 + 16); // 將隨機數(16-30)放入
            for (int j = 0; j < i; ) { // 與前數比較，若有相同則再取亂數
                if (RowNum[1][j] == RowNum[1][i]) {
                    RowNum[1][i] = (int) (Math.random() * 15 + 16);
                    j = 0; // 避面重新亂數後又產生相同數字，若出現重覆，迴圈從頭開始重新比較所有數
                } else
                    j++; // 若都不重複則下一個數
            }
        }
        // 第三直行(31 ~ 45)...中間(FREE)保送
        for (int i = 0; i < 5; i++) {
            RowNum[2][i] = (int) (Math.random() * 15 + 31); // 將隨機數(31-45)放入
            for (int j = 0; j < i; ) { // 與前數比較，若有相同則再取亂數
                if (RowNum[2][j] == RowNum[2][i]) {
                    RowNum[2][i] = (int) (Math.random() * 15 + 31);
                    j = 0; // 避面重新亂數後又產生相同數字，若出現重覆，迴圈從頭開始重新比較所有數
                } else
                    j++; // 若都不重複則下一個數
            }
        }
        /** free中間(FREE)保送**/
        RowNum[2][2] = 0;


        // 第四直行(46 ~ 60)
        for (int i = 0; i < 5; i++) {
            RowNum[3][i] = (int) (Math.random() * 15 + 46); // 將隨機數(46-60)放入
            for (int j = 0; j < i; ) { // 與前數比較，若有相同則再取亂數
                if (RowNum[3][j] == RowNum[3][i]) {
                    RowNum[3][i] = (int) (Math.random() * 15 + 46);
                    j = 0; // 避面重新亂數後又產生相同數字，若出現重覆，迴圈從頭開始重新比較所有數
                } else
                    j++; // 若都不重複則下一個數
            }
        }

        // 第五直行 (61 ~ 75)
        for (int i = 0; i < 5; i++) {
            RowNum[4][i] = (int) (Math.random() * 15 + 61); // 將隨機數(61-75)放入
            for (int j = 0; j < i; ) { // 與前數比較，若有相同則再取亂數
                if (RowNum[4][j] == RowNum[4][i]) {
                    RowNum[4][i] = (int) (Math.random() * 15 + 61);
                    j = 0; // 避面重新亂數後又產生相同數字，若出現重覆，迴圈從頭開始重新比較所有數
                } else
                    j++; // 若都不重複則下一個數
            }
        }

        return RowNum;
    }

    /**
     * 判斷可能連線數目, 還沒寫  ~~~~~ bella
     **/
    //圈選擲出的數字
    public void pickNum(String data) {
        if (one1.getText().toString().equals(data)) {
            one1.setEnabled(false);
            one1.setTextColor(Color.GRAY);
            one1.setBackgroundColor(Color.BLACK);
            last = one1.getText().toString();
            c1 = true;
            possible1++;
            possible6++;
            possible11++;
            bestPosible--;
            checkBingo();
        } else if (one2.getText().toString().equals(data)) {
            one2.setEnabled(false);
            one2.setTextColor(Color.GRAY);
            one2.setBackgroundColor(Color.BLACK);
            last = one2.getText().toString();
            c2 = true;
            possible2++;
            possible6++;
            bestPosible--;
            checkBingo();
        } else if (one3.getText().toString().equals(data)) {
            one3.setEnabled(false);
            one3.setTextColor(Color.GRAY);
            one3.setBackgroundColor(Color.BLACK);
            last = one3.getText().toString();
            c3 = true;
            possible3++;
            possible6++;
            bestPosible--;
            checkBingo();
        } else if (one4.getText().toString().equals(data)) {
            one4.setEnabled(false);
            one4.setTextColor(Color.GRAY);
            one4.setBackgroundColor(Color.BLACK);
            last = one4.getText().toString();
            c4 = true;
            possible4++;
            possible6++;
            bestPosible--;
            checkBingo();
        } else if (one5.getText().toString().equals(data)) {
            one5.setEnabled(false);
            one5.setTextColor(Color.GRAY);
            one5.setBackgroundColor(Color.BLACK);
            last = one5.getText().toString();
            c5 = true;
            possible5++;
            possible6++;
            possible12++;
            bestPosible--;
            checkBingo();
        } else if (two1.getText().toString().equals(data)) {
            two1.setEnabled(false);
            two1.setTextColor(Color.GRAY);
            two1.setBackgroundColor(Color.BLACK);
            last = two1.getText().toString();
            c6 = true;
            possible1++;
            possible7++;
            bestPosible--;
            checkBingo();
        } else if (two2.getText().toString().equals(data)) {
            two2.setEnabled(false);
            two2.setTextColor(Color.GRAY);
            two2.setBackgroundColor(Color.BLACK);
            last = two2.getText().toString();
            c7 = true;
            possible2++;
            possible7++;
            possible11++;
            bestPosible--;
            checkBingo();
        } else if (two3.getText().toString().equals(data)) {
            two3.setEnabled(false);
            two3.setTextColor(Color.GRAY);
            two3.setBackgroundColor(Color.BLACK);
            last = two3.getText().toString();
            c8 = true;
            possible3++;
            possible7++;
            bestPosible--;
            checkBingo();
        } else if (two4.getText().toString().equals(data)) {
            two4.setEnabled(false);
            two4.setTextColor(Color.GRAY);
            two4.setBackgroundColor(Color.BLACK);
            last = two4.getText().toString();
            c9 = true;
            possible4++;
            possible7++;
            possible12++;
            bestPosible--;
            checkBingo();
        } else if (two5.getText().toString().equals(data)) {
            two5.setEnabled(false);
            two5.setTextColor(Color.GRAY);
            two5.setBackgroundColor(Color.BLACK);
            last = two5.getText().toString();
            c10 = true;
            possible5++;
            possible7++;
            bestPosible--;
            checkBingo();
        } else if (three1.getText().toString().equals(data)) {
            three1.setEnabled(false);
            three1.setTextColor(Color.GRAY);
            three1.setBackgroundColor(Color.BLACK);
            last = three1.getText().toString();
            c11 = true;
            possible1++;
            possible8++;
            bestPosible--;
            checkBingo();
        } else if (three2.getText().toString().equals(data)) {
            three2.setEnabled(false);
            three2.setTextColor(Color.GRAY);
            three2.setBackgroundColor(Color.BLACK);
            last = three2.getText().toString();
            c12 = true;
            possible2++;
            possible8++;
            bestPosible--;
            checkBingo();
        } else if (three3.getText().toString().equals(data)) {
            three3.setEnabled(false);
            three3.setTextColor(Color.GRAY);
            three3.setBackgroundColor(Color.BLACK);
            last = three3.getText().toString();
            c13 = true;
            possible3++;
            possible8++;
            possible11++;
            possible12++;
            bestPosible--;
            checkBingo();
        } else if (three4.getText().toString().equals(data)) {
            three4.setEnabled(false);
            three4.setTextColor(Color.GRAY);
            three4.setBackgroundColor(Color.BLACK);
            last = three4.getText().toString();
            c14 = true;
            possible4++;
            possible8++;
            bestPosible--;
            checkBingo();
        } else if (three5.getText().toString().equals(data)) {
            three5.setEnabled(false);
            three5.setTextColor(Color.GRAY);
            three5.setBackgroundColor(Color.BLACK);
            last = three5.getText().toString();
            c15 = true;
            possible5++;
            possible8++;
            bestPosible--;
            checkBingo();
        } else if (four1.getText().toString().equals(data)) {
            four1.setEnabled(false);
            four1.setTextColor(Color.GRAY);
            four1.setBackgroundColor(Color.BLACK);
            last = four1.getText().toString();
            c16 = true;
            possible1++;
            possible9++;
            bestPosible--;
            checkBingo();
        } else if (four2.getText().toString().equals(data)) {
            four2.setEnabled(false);
            four2.setTextColor(Color.GRAY);
            four2.setBackgroundColor(Color.BLACK);
            last = four2.getText().toString();
            c17 = true;
            possible2++;
            possible9++;
            possible12++;
            bestPosible--;
            checkBingo();
        } else if (four3.getText().toString().equals(data)) {
            four3.setEnabled(false);
            four3.setTextColor(Color.GRAY);
            four3.setBackgroundColor(Color.BLACK);
            last = four3.getText().toString();
            c18 = true;
            possible3++;
            possible9++;
            bestPosible--;
            checkBingo();
        } else if (four4.getText().toString().equals(data)) {
            four4.setEnabled(false);
            four4.setTextColor(Color.GRAY);
            four4.setBackgroundColor(Color.BLACK);
            last = four4.getText().toString();
            c19 = true;
            possible4++;
            possible9++;
            possible11++;
            bestPosible--;
            checkBingo();
        } else if (four5.getText().toString().equals(data)) {
            four5.setEnabled(false);
            four5.setTextColor(Color.GRAY);
            four5.setBackgroundColor(Color.BLACK);
            last = four5.getText().toString();
            c20 = true;
            possible5++;
            possible9++;
            bestPosible--;
            checkBingo();
        } else if (five1.getText().toString().equals(data)) {
            five1.setEnabled(false);
            five1.setTextColor(Color.GRAY);
            five1.setBackgroundColor(Color.BLACK);
            last = five1.getText().toString();
            c21 = true;
            possible1++;
            possible10++;
            possible12++;
            bestPosible--;
            checkBingo();
        } else if (five2.getText().toString().equals(data)) {
            five2.setEnabled(false);
            five2.setTextColor(Color.GRAY);
            five2.setBackgroundColor(Color.BLACK);
            last = five2.getText().toString();
            c22 = true;
            possible2++;
            possible10++;
            bestPosible--;
            checkBingo();
        } else if (five3.getText().toString().equals(data)) {
            five3.setEnabled(false);
            five3.setTextColor(Color.GRAY);
            five3.setBackgroundColor(Color.BLACK);
            last = five3.getText().toString();
            c23 = true;
            possible3++;
            possible10++;
            bestPosible--;
            checkBingo();
        } else if (five4.getText().toString().equals(data)) {
            five4.setEnabled(false);
            five4.setTextColor(Color.GRAY);
            five4.setBackgroundColor(Color.BLACK);
            last = five4.getText().toString();
            c24 = true;
            possible4++;
            possible10++;
            bestPosible--;
            checkBingo();
        } else if (five5.getText().toString().equals(data)) {
            five5.setEnabled(false);
            five5.setTextColor(Color.GRAY);
            five5.setBackgroundColor(Color.BLACK);
            last = five5.getText().toString();
            c25 = true;
            possible5++;
            possible10++;
            possible11++;
            bestPosible--;
            checkBingo();
        }

    }

    //判斷是否有連線
    public void checkBingo() {
        //連線判斷五個為一條並且若為4增加可能性的線1條
        if (possible1 == 5) {
            if (inIf1) {
                bestLine--;
                inIf1 = false;
                win();
                one1.setBackgroundColor(lineColor);
                two1.setBackgroundColor(lineColor);
                three1.setBackgroundColor(lineColor);
                four1.setBackgroundColor(lineColor);
                five1.setBackgroundColor(lineColor);
            }
        } else if (possible1 == 4) {
            bestLine++;
        }
        if (possible2 == 5) {
            if (inIf2) {
                bestLine--;
                inIf2 = false;
                win();
                one2.setBackgroundColor(lineColor);
                two2.setBackgroundColor(lineColor);
                three2.setBackgroundColor(lineColor);
                four2.setBackgroundColor(lineColor);
                five2.setBackgroundColor(lineColor);
            }
        } else if (possible2 == 4) {
            bestLine++;
        }
        if (possible3 == 5) {
            if (inIf3) {
                bestLine--;
                inIf3 = false;
                win();
                one3.setBackgroundColor(lineColor);
                two3.setBackgroundColor(lineColor);
                three3.setBackgroundColor(lineColor);
                four3.setBackgroundColor(lineColor);
                five3.setBackgroundColor(lineColor);
            }
        } else if (possible3 == 4) {
            bestLine++;
        }
        if (possible4 == 5) {
            if (inIf4) {
                bestLine--;
                inIf4 = false;
                win();
                one4.setBackgroundColor(lineColor);
                two4.setBackgroundColor(lineColor);
                three4.setBackgroundColor(lineColor);
                four4.setBackgroundColor(lineColor);
                five4.setBackgroundColor(lineColor);
            }
        } else if (possible4 == 4) {
            bestLine++;
        }
        if (possible5 == 5) {
            if (inIf5) {
                bestLine--;
                inIf5 = false;
                win();
                one5.setBackgroundColor(lineColor);
                two5.setBackgroundColor(lineColor);
                three5.setBackgroundColor(lineColor);
                four5.setBackgroundColor(lineColor);
                five5.setBackgroundColor(lineColor);
            }
        } else if (possible5 == 4) {
            bestLine++;
        }
        if (possible6 == 5) {
            if (inIf6) {
                bestLine--;
                inIf6 = false;
                win();
                one1.setBackgroundColor(lineColor);
                one2.setBackgroundColor(lineColor);
                one3.setBackgroundColor(lineColor);
                one4.setBackgroundColor(lineColor);
                one5.setBackgroundColor(lineColor);
            }
        } else if (possible6 == 4) {
            bestLine++;
        }
        if (possible7 == 5) {
            if (inIf7) {
                bestLine--;
                inIf7 = false;
                win();
                two1.setBackgroundColor(lineColor);
                two2.setBackgroundColor(lineColor);
                two3.setBackgroundColor(lineColor);
                two4.setBackgroundColor(lineColor);
                two5.setBackgroundColor(lineColor);
            }
        } else if (possible7 == 4) {
            bestLine++;
        }
        if (possible8 == 5) {
            if (inIf8) {
                bestLine--;
                inIf8 = false;
                win();
                three1.setBackgroundColor(lineColor);
                three2.setBackgroundColor(lineColor);
                three3.setBackgroundColor(lineColor);
                three4.setBackgroundColor(lineColor);
                three5.setBackgroundColor(lineColor);
            }
        } else if (possible8 == 4) {
            bestLine++;
        }
        if (possible9 == 5) {
            if (inIf9) {
                bestLine--;
                inIf9 = false;
                win();
                four1.setBackgroundColor(lineColor);
                four2.setBackgroundColor(lineColor);
                four3.setBackgroundColor(lineColor);
                four4.setBackgroundColor(lineColor);
                four5.setBackgroundColor(lineColor);
            }
        } else if (possible9 == 4) {
            bestLine++;
        }
        if (possible10 == 5) {
            if (inIf10) {
                bestLine--;
                inIf10 = false;
                win();
                five1.setBackgroundColor(lineColor);
                five2.setBackgroundColor(lineColor);
                five3.setBackgroundColor(lineColor);
                five4.setBackgroundColor(lineColor);
                five5.setBackgroundColor(lineColor);
            }
        } else if (possible10 == 4) {
            bestLine++;
        }
        if (possible11 == 5) {
            if (inIf11) {
                bestLine--;
                inIf11 = false;
                win();
                one1.setBackgroundColor(lineColor);
                two2.setBackgroundColor(lineColor);
                three3.setBackgroundColor(lineColor);
                four4.setBackgroundColor(lineColor);
                five5.setBackgroundColor(lineColor);
            }
        } else if (possible11 == 4) {
            bestLine++;
        }
        if (possible12 == 5) {
            if (inIf12) {
                bestLine--;
                inIf12 = false;
                win();
                one5.setBackgroundColor(lineColor);
                two4.setBackgroundColor(lineColor);
                three3.setBackgroundColor(lineColor);
                four2.setBackgroundColor(lineColor);
                five1.setBackgroundColor(lineColor);
            }
        } else if (possible12 == 4) {
            bestLine++;
        }

        switch (win) {
            case 2://連線判斷如果兩條線可以變五條的
                if (bestPosible <= 4)
                    Toast.makeText(HostRoomActivity.this, "我三條線我差一個就要五條線了!!", Toast.LENGTH_SHORT).show();
                break;
            case 3://連線判斷如果三條可以變五條的
                if ((bestPosible == 8 && bestLine == 3) || (bestPosible <= 5 && bestLine >= 3))
                    Toast.makeText(HostRoomActivity.this, "我三條線我差一個就要五條線了!!", Toast.LENGTH_SHORT).show();
                break;
            case 4://連線判斷如果四條可以變五條以上的
                if (bestLine > 0)
                    Toast.makeText(HostRoomActivity.this, "我四條線我差一個就要五條線了!!", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    //判斷連成幾條線+滿五條則獲勝
    public void win() {
        String sendStr = "";
        win++;
        if (win == 1) {
            //連線顏色紅
            lineColor = Color.RED;
            sendStr = "1\n" + userName + "\nline\n" + win;
            try {
                byte[] sendByte = sendStr.getBytes("UTF-8");
                outStream.write(sendByte);

            } catch (Exception e) {
                // TODO: handle exception
                HostRoomActivity.this.finish();
            }

        } else if (win == 2) {
            lineColor = Color.RED;
            //連線顏色橙
//            lineColor = Color.rgb(255, 165, 0);
            sendStr = "1\n" + userName + "\nline\n" + win;
            try {
                byte[] sendByte = sendStr.getBytes("UTF-8");
                outStream.write(sendByte);

            } catch (Exception e) {
                // TODO: handle exception
                HostRoomActivity.this.finish();
            }
        } else if (win == 3) {
            lineColor = Color.RED;
//            //連線顏色黃色
//            lineColor = Color.YELLOW;
            sendStr = "1\n" + userName + "\nline\n" + win;
            try {
                byte[] sendByte = sendStr.getBytes("UTF-8");
                outStream.write(sendByte);
            } catch (Exception e) {
                // TODO: handle exception
                HostRoomActivity.this.finish();
            }
        } else if (win == 4) {//通知導遊四條線
            lineColor = Color.RED;
//            //連線顏色綠
//            lineColor = Color.GREEN;
            sendStr = "1\n" + userName + "\nline\n" + win;
            try {
                byte[] sendByte = sendStr.getBytes("UTF-8");
                outStream.write(sendByte);

            } catch (Exception e) {
                // TODO: handle exception
                HostRoomActivity.this.finish();
            }

        } else if (win == 5) {
            lineColor = Color.RED;
//            //連線顏色藍
//            lineColor = Color.BLUE;
            sendStr = "1\n" + userName + "\nwin\n" + userName + "BINGO!";
            try {
                byte[] sendByte = sendStr.getBytes("UTF-8");
                outStream.write(sendByte);

            } catch (Exception e) {
                // TODO: handle exception
                HostRoomActivity.this.finish();
            }
            Toast.makeText(getApplicationContext(), "you win", Toast.LENGTH_SHORT).show();
            one1.setEnabled(false);
            one2.setEnabled(false);
            one3.setEnabled(false);
            one4.setEnabled(false);
            one5.setEnabled(false);
            two1.setEnabled(false);
            two2.setEnabled(false);
            two3.setEnabled(false);
            two4.setEnabled(false);
            two5.setEnabled(false);
            three1.setEnabled(false);
            three2.setEnabled(false);
            three3.setEnabled(false);
            three4.setEnabled(false);
            three5.setEnabled(false);
            four1.setEnabled(false);
            four2.setEnabled(false);
            four3.setEnabled(false);
            four4.setEnabled(false);
            four5.setEnabled(false);
            five1.setEnabled(false);
            five2.setEnabled(false);
            five3.setEnabled(false);
            five4.setEnabled(false);
            five5.setEnabled(false);
        } else {

            win = 0;
            one1.setEnabled(false);
            one2.setEnabled(false);
            one3.setEnabled(false);
            one4.setEnabled(false);
            one5.setEnabled(false);
            two1.setEnabled(false);
            two2.setEnabled(false);
            two3.setEnabled(false);
            two4.setEnabled(false);
            two5.setEnabled(false);
            three1.setEnabled(false);
            three2.setEnabled(false);
            three3.setEnabled(false);
            three4.setEnabled(false);
            three5.setEnabled(false);
            four1.setEnabled(false);
            four2.setEnabled(false);
            four3.setEnabled(false);
            four4.setEnabled(false);
            four5.setEnabled(false);
            five1.setEnabled(false);
            five2.setEnabled(false);
            five3.setEnabled(false);
            five4.setEnabled(false);
            five5.setEnabled(false);
            HostRoomActivity.this.finish();
        }
    }

    //判斷是否準備名單中的人數離開了
    public void prepareUserLeave(String userName) {
        String[] user = userName.split(" ");
        Log.e("Hsiang", user[2]);
        userName = user[2] + " Ready";
        for (int i = 0; i < interUser.size(); i++) {
            if (userName.equals(interUser.get(i).toString())) {
                interUser.remove(i);
                is_prepare = interUser.size();
                Log.e("Hsiang", "準備好的人數為" + is_prepare);
            }
        }
    }

    //Bingo時會跳出的視窗
    public void bingoDialog(String data) {
        new AlertDialog.Builder(HostRoomActivity.this)
                .setTitle("恭喜BINGO")
                .setMessage(data)
                .setPositiveButton("離開", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HostRoomActivity.this.finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    //更新目前每人的條數
    public void updateLineNum(String fromuser, String data) {
        for (int i = 0; i < interUser.size(); i++) {
            if (interUser.get(i).equals(fromuser + " Ready")) {
                interUser.set(i, fromuser + " 已" + data + "條");
            } else if (interUser.get(i).equals(fromuser + " 已1條")) {
                interUser.set(i, fromuser + " 已" + data + "條");
            } else if (interUser.get(i).equals(fromuser + " 已2條")) {
                interUser.set(i, fromuser + " 已" + data + "條");
            } else if (interUser.get(i).equals(fromuser + " 已3條")) {
                interUser.set(i, fromuser + " 已" + data + "條");
            } else if (interUser.get(i).equals(fromuser + " 已4條")) {
                interUser.set(i, fromuser + " 已賓果");


            }
        }
    }

    /**
     * 其他程式系統設定
     **/
    //偵測按下退出按鍵
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //確定按下退出鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            AlertDialog.Builder ad = new AlertDialog.Builder(HostRoomActivity.this); //創建訊息方塊
            ad.setTitle("離開訊息");
            ad.setMessage("遊戲尚在進行中，您確定要離開?");
            ad.setPositiveButton("是", new DialogInterface.OnClickListener() { //按"是",則退出應用程式

                public void onClick(DialogInterface dialog, int i) {
                    //更新資料庫
                    if (channel.equals("1")) {
                        new MySQL_update(account, "room1", -1).execute();
                    } else if (channel.equals("2")) {
                        new MySQL_update(account, "room2", -1).execute();
                    } else if (channel.equals("3")) {
                        new MySQL_update(account, "room3", -1).execute();
                    } else if (channel.equals("4")) {
                        new MySQL_update(account, "room4", -1).execute();
                    } else if (channel.equals("5")) {
                        new MySQL_update(account, "room5", -1).execute();
                    }
                    HostRoomActivity.this.finish();//關閉activity
                }

            });
            ad.setNegativeButton("否", new DialogInterface.OnClickListener() { //按"否",則不執行任何操作
                public void onClick(DialogInterface dialog, int i) {
                }
            });
            ad.show();//顯示訊息視窗
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //判斷有沒有開網路
    private boolean haveInternet() {
        boolean result = false;
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            result = false;
        } else {
            if (!info.isAvailable()) {
                result = false;
            } else {
                result = true;
            }
        }
        return result;
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        try {
            s.close();
            //更新資料庫
            if (channel.equals("1")) {
                new MySQL_update(account, "room1", -1).execute();
            } else if (channel.equals("2")) {
                new MySQL_update(account, "room2", -1).execute();
            } else if (channel.equals("3")) {
                new MySQL_update(account, "room3", -1).execute();
            } else if (channel.equals("4")) {
                new MySQL_update(account, "room4", -1).execute();
            } else if (channel.equals("5")) {
                new MySQL_update(account, "room5", -1).execute();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {

        }
        Log.e("BELLA", "onStop");
        recvThread.stopThread();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            s.close();
            //更新資料庫
            if (channel.equals("1")) {
                new MySQL_update(account, "room1", -1).execute();
            } else if (channel.equals("2")) {
                new MySQL_update(account, "room2", -1).execute();
            } else if (channel.equals("3")) {
                new MySQL_update(account, "room3", -1).execute();
            } else if (channel.equals("4")) {
                new MySQL_update(account, "room4", -1).execute();
            } else if (channel.equals("5")) {
                new MySQL_update(account, "room5", -1).execute();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
        }
        Log.e("BELLA", "onDestroy");
        recvThread.stopThread();
    }

    //放人勿進頻道
    public void putChannel(String userName, String channel) {
        Log.e("GetUserName,channel", userName + "," + channel);
        switch (channel) {
            case "1":
                UserChannel1.add(userName);
                break;
            case "2":
                UserChannel2.add(userName);
                break;
            case "3":
                UserChannel3.add(userName);
                break;
            case "4":
                UserChannel4.add(userName);
                break;
            case "5":
                UserChannel5.add(userName);
                break;
            default:
                break;
        }
    }

    //取得個頻道的人
    public void getChannel(String channel) {
        switch (channel) {
            case "1":
                //檢測重複
                HashSet duplicate1 = new HashSet(UserChannel1);
                UserChannel1.clear();
                UserChannel1.addAll(duplicate1);
                Log.e("UserChannel1", UserChannel1.get(0));
                for (int i = 0; i < UserChannel1.size(); i++) {
                    userlist.append(UserChannel1.get(i) + "\n");
                    Log.e("UserChannel1", UserChannel1.size() + "個");
                }
                break;
            case "2":
                //檢測重複
                HashSet duplicate2 = new HashSet(UserChannel2);
                UserChannel2.clear();
                UserChannel2.addAll(duplicate2);
                Log.e("UserChannel2", "add");
                for (int i = 0; i < UserChannel2.size(); i++) {
                    userlist.append(UserChannel2.get(i) + "\n");
                    Log.e("UserChannel2", UserChannel2.size() + "個");
                }
                break;
            case "3":
                //檢測重複
                HashSet duplicate3 = new HashSet(UserChannel3);
                UserChannel3.clear();
                UserChannel3.addAll(duplicate3);
                Log.e("UserChannel3", "add");
                for (int i = 0; i < UserChannel3.size(); i++) {
                    userlist.append(UserChannel3.get(i) + "\n");
                    Log.e("UserChannel3", UserChannel3.size() + "個");
                }
                break;
            case "4":
                //檢測重複
                HashSet duplicate4 = new HashSet(UserChannel4);
                UserChannel4.clear();
                UserChannel4.addAll(duplicate4);
                Log.e("UserChannel4", "add");
                for (int i = 0; i < UserChannel4.size(); i++) {
                    userlist.append(UserChannel4.get(i) + "\n");
                    Log.e("UserChannel4", UserChannel4.size() + "個");
                }
                break;
            case "5":
                //檢測重複
                HashSet duplicate5 = new HashSet(UserChannel5);
                UserChannel5.clear();
                UserChannel5.addAll(duplicate5);
                Log.e("UserChannel5", "add");
                for (int i = 0; i < UserChannel5.size(); i++) {
                    userlist.append(UserChannel5.get(i) + "\n");
                    Log.e("UserChannel5", UserChannel5.size() + "個");
                }
                break;
            default:
                break;
        }
    }

    //刪除頻道的人
    public void removeChannel(String userName, String channel) {
        switch (channel) {
            case "1":
                for (int i = 0; i < UserChannel1.size(); i++) {
                    if (userName.equals(UserChannel1.get(i))) {
                        UserChannel1.remove(i);
                        break;
                    }
                }
                break;
            case "2":
                for (int i = 0; i < UserChannel2.size(); i++) {
                    if (userName.equals(UserChannel2.get(i))) {
                        UserChannel2.remove(i);
                        break;
                    }
                }
                break;
            case "3":
                for (int i = 0; i < UserChannel3.size(); i++) {
                    if (userName.equals(UserChannel3.get(i))) {
                        UserChannel3.remove(i);
                        break;
                    }
                }
                break;
            case "4":
                for (int i = 0; i < UserChannel4.size(); i++) {
                    if (userName.equals(UserChannel4.get(i))) {
                        UserChannel4.remove(i);
                        break;
                    }
                }
                break;
            case "5":
                for (int i = 0; i < UserChannel5.size(); i++) {
                    if (userName.equals(UserChannel5.get(i))) {
                        UserChannel5.remove(i);
                        break;
                    }
                }
                break;
            default:
                break;
        }
    }

    //檢測頻道人數
    public int peopleChannel(String channel) {
        int getpeople = 0;
        switch (channel) {
            case "1":
                getpeople = UserChannel1.size();
                break;
            case "2":
                getpeople = UserChannel2.size();
                break;
            case "3":
                getpeople = UserChannel3.size();
                break;
            case "4":
                getpeople = UserChannel4.size();
                break;
            case "5":
                getpeople = UserChannel5.size();
                break;
            default:
                break;
        }
        return getpeople;
    }
}
