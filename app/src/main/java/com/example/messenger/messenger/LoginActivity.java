package com.example.messenger.messenger;
/**
 * Created by Joubert on 13/05/2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;


public class LoginActivity extends Activity {

    private EditText usernameEdit;
    private EditText passwordEdit;
    private String username;
    private String password;
    private long mLastClickTime;
    private ProgressDialog pDialog;

    public static Activity activity;
    public static SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        activity = this;

        sharedPref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if(sharedPref.getBoolean("autoLogin", false)){

            Log.d("LoginActivity", "Autologin");

            Intent intent = new Intent(LoginActivity.this, ChatListActivity.class);
            startActivity(intent);

            new Thread(new Runnable() {
                public void run() {

                    username = sharedPref.getString("username", "default");
                    password = sharedPref.getString("password", "default");

                    ((Network) getApplication()).login(username, password, true);

                }
            }).start();

            finish();
        }
    }

    public void loginbtn(View view){
        // mis-clicking prevention, using threshold of 1000 ms
        if (SystemClock.elapsedRealtime() - mLastClickTime < 3000){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        new AttemptLogin().execute();
    }

    public void registerbtn(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    class AttemptLogin extends AsyncTask<Void, Void, String> {

        private String notification = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Logging In...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... args) {
            // TODO Auto-generated method stub

            usernameEdit = (EditText) findViewById(R.id.username);
            passwordEdit = (EditText) findViewById(R.id.password);
            username = usernameEdit.getText().toString();
            password = passwordEdit.getText().toString();

            if(((Network) getApplication()).isNetworkConnected()){
                Log.d("LoginActivity",  "networkOK");
                notification = ((Network) getApplication()).login(username+Network.SERVICE, password, false);
            }else{
                //No network available
                Log.d("LoginActivity", "No Network Available");
                return "No Network Available. Verify is you are connected to Internet and try again.";
            }

            return notification;

        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String notification) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if(!notification.equals("success")){
                ((Network) getApplication()).showAlertDialog("Notification!", notification, LoginActivity.this);
            }
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d("LoginActivity","onPause");
        if(pDialog != null){
            pDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
