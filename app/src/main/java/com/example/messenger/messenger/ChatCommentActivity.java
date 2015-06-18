package com.example.messenger.messenger;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;


public class ChatCommentActivity extends Activity {

    private EditText editText;
    private Handler mHandler = new Handler();

    public static ListView lv;
    public static boolean isRunning;
    public static ChatCommentBaseAdapter adapter;
    public static String contact = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_comment);

        Intent intent = getIntent();
        contact = intent.getStringExtra("contact");
        editText = (EditText) findViewById(R.id.editText);

        lv = (ListView) findViewById(R.id.chatCommentLv);
        lv.setDivider(null);
        adapter = new ChatCommentBaseAdapter(getApplicationContext(), R.layout.chat_comment_view);
        lv.setAdapter(adapter);

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        List<MessageArchive> messages = db.getAllMessages();
        db.close();

        for (MessageArchive cn : messages) {
            if(cn.getFromJid().equals(contact) && cn.getToJid().equals(LoginActivity.sharedPref.getString("username", "default"))){
                adapter.add(new ChatComment(true, cn.getBody(), cn.getSentDate()));
            }
            if(cn.getFromJid().equals(LoginActivity.sharedPref.getString("username", "default")) && cn.getToJid().equals(contact)){
                adapter.add(new ChatComment(false, cn.getBody(), cn.getSentDate()));
            }
        }

        DatabaseHandler dbb = new DatabaseHandler(getApplicationContext());
        if(dbb.getChatsCount() > 0){
            ChatListTab.setChatUpdate(contact, dbb.getChat(dbb.getChatID(contact)).getBody(),dbb.getChat(dbb.getChatID(contact)).getSentDate() , 0);
        }
        dbb.close();

        lv.setSelection(lv.getAdapter().getCount()-1);
    }

    public static void setChatCommentChanged(String message, String sentDate){
        adapter.add(new ChatComment(true, message, sentDate));
        lv.setSelection(lv.getAdapter().getCount()-1);
    }

    public void sendBtn(View view){
        final String message = editText.getText().toString();

        if(!message.equals("")) {
            new Thread(new Runnable() {
                public void run() {
                    ((Network) getApplicationContext()).sendMessage(contact, editText.getText().toString());
                    mHandler.post(new Runnable() {
                        public void run() {
                            editText.setText("");
                        }
                    });
                }
            }).start();
        }
    }

    @Override
    protected void onResume(){
        Log.d("ChatCommentActivity", "onResume");
        isRunning = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("ChatCommentActivity", "onPause");
        isRunning = false;
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
