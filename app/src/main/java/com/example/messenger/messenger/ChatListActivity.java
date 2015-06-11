package com.example.messenger.messenger;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ChatListActivity extends FragmentActivity {

    private ViewPager Tab;
    private TabPagerAdapter TabAdapter;
    private ActionBar actionBar;
    private SharedPreferences.Editor editor;
    private Handler mHandler = new Handler();
    public static boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        NotificationManager myNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // remove the notification with the specific id
        myNotificationManager.cancel(111);

        isRunning = true;

        TabAdapter = new TabPagerAdapter(getSupportFragmentManager());

        Tab = (ViewPager)findViewById(R.id.pager);
        Tab.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {

                        actionBar = getActionBar();
                        actionBar.setSelectedNavigationItem(position);                    }
                });
        Tab.setAdapter(TabAdapter);

        actionBar = getActionBar();
        //Enable Tabs on Action Bar
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.TabListener tabListener = new ActionBar.TabListener(){

            @Override
            public void onTabReselected(android.app.ActionBar.Tab tab,
                                        FragmentTransaction ft) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

                Tab.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(android.app.ActionBar.Tab tab,
                                        FragmentTransaction ft) {
                // TODO Auto-generated method stub

            }};
        //Add New Tab
        actionBar.addTab(actionBar.newTab().setText("Updates").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Chats").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Contacts").setTabListener(tabListener));

        Tab.setCurrentItem(1);

    }

    public void logoutBtn(MenuItem item){
        editor = LoginActivity.sharedPref.edit();
        editor.putString("username", "");
        editor.putString("password", "");
        editor.putBoolean("autoLogin", false);
        editor.commit();

        finish();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        new Thread(new Runnable() {
            public void run() {
                ((Network) getApplication()).disconect();
            }
        }).start();
    }

    public void statusBtn(MenuItem item){
        Intent intent = new Intent(this, StatusActivity.class);
        startActivity(intent);
    }

    public void searchBtn(MenuItem item){
        //hay que crear el metodo para buscar los contactos
        ((Network) getApplication()).setNotification("hola, como estas?", "admin2@localhost");
    }

    public void settingsBtn(MenuItem item){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void addContactBtn(MenuItem item){
        if(((Network) getApplicationContext()).connection.isConnected()){

            LinearLayout lila1 = new LinearLayout(ChatListActivity.this);
            lila1.setOrientation(LinearLayout.VERTICAL);

            final EditText addContactEdit = new EditText(ChatListActivity.this);
            addContactEdit.setHint("Add Contact");
            addContactEdit.setGravity(Gravity.CENTER);
            addContactEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

            lila1.addView(addContactEdit);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChatListActivity.this);
            alertDialogBuilder
                    .setTitle("Add a new contact")
                    .setView(lila1)
                            //.setIcon(R.drawable.ic_action_add_person)
                            //.setMessage("Add a new contact")
                    .setCancelable(true)
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("Add Contact!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            final String contact;
                            contact = addContactEdit.getText().toString();

                            Pattern pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
                            Matcher match = pattern.matcher(contact);
                            boolean contactOK = match.find();

                            if(!contactOK && !contact.contains(" ") && !contact.equals("")){
                                new Thread(new Runnable() {
                                    public void run() {
                                        final String notification;
                                        notification = ((Network) getApplication()).addRoster(contact + Network.SERVICE);
                                        mHandler.post(new Runnable() {
                                            public void run() {
                                                if (!notification.equals("success")) {
                                                    ((Network) getApplication()).showAlertDialog("Notification", notification, ChatListActivity.this);
                                                }
                                            }
                                        });
                                    }
                                }).start();

                            }else{
                                ((Network) getApplication()).showAlertDialog("Notification", "This contact has invalid characters", ChatListActivity.this);
                            }
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }else{
            Toast.makeText(getApplicationContext(),
                    "Please wait for reconnection", Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    protected void onPause(){
        isRunning = false;
        ChatListTab.reloadChatList();
        Log.d("ChatListActivity", "onPause");
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        NotificationManager myNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // remove the notification with the specific id
        myNotificationManager.cancel(111);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_list, menu);
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
