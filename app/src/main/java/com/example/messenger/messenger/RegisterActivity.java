package com.example.messenger.messenger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Joubert on 13/05/2015.
 */

public class RegisterActivity extends Activity {

    private EditText usernameEdit;
    private EditText passwordEdit;
    private EditText rePasswordEdit;
    private EditText emailEdit;

    private String username;
    private String password;
    private String rePassword;
    private String email;
    private long mLastClickTime;

    private AbstractXMPPConnection connection;

    private String HOST = "192.168.1.4";
    private String SERVICE = "localhost";
    private int PORT = 5222;
    private String RESOURCE = "Home";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void registerbtn(View view){

        // mis-clicking prevention, using threshold of X ms
        if (SystemClock.elapsedRealtime() - mLastClickTime < 3000){
            return;
        }

        mLastClickTime = SystemClock.elapsedRealtime();

        usernameEdit = (EditText) findViewById(R.id.username);
        passwordEdit = (EditText) findViewById(R.id.password);
        rePasswordEdit = (EditText) findViewById(R.id.repassword);
        emailEdit = (EditText) findViewById(R.id.email);

        username = usernameEdit.getText().toString();
        password = passwordEdit.getText().toString();
        rePassword = rePasswordEdit.getText().toString();
        email = emailEdit.getText().toString();

        Pattern pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher match = pattern.matcher(username);
        boolean usernameOK = match.find();

        if(((Network) this.getApplication()).isNetworkConnected()){
            Log.d("RegisterActivity",  "networkOK");
            if(!usernameOK){
                Log.d("RegisterActivity",  "usernameOK");
                if(password.equals(rePassword) && password.length() >= 6){
                    Log.d("RegisterActivity", "PasswordsOK");
                    register();
                }else{
                    //Passwords does not match...
                    Log.d("RegisterActivity", "Passwords Dot Not Match & Has to be more than 6 characters");
                    ((Network) getApplication()).showAlertDialog("Password Mismatch!", "The passwords are not the same. The password has to be 6 character or more.", RegisterActivity.this);
                    clearEditText(passwordEdit);
                    clearEditText(rePasswordEdit);
                }
            }else{
                //Invalid username...
                Log.d("RegisterActivity", "Invalid Username");
                ((Network) getApplication()).showAlertDialog("Invalid Username!", "The username can not contain special characters.", RegisterActivity.this);
                clearEditText(usernameEdit);
            }
        }else{
            //No network available
            Log.d("RegisterActivity", "No Network Available");
            ((Network) getApplication()).showAlertDialog("No Network Available!", "Your device is not connected to the Internet.", RegisterActivity.this);
        }
    }

    private void register(){

        // Create the configuration for this new connection
        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
        //configBuilder.setResource(RESOURCE);
        configBuilder.setServiceName(SERVICE);
        configBuilder.setHost(HOST);
        configBuilder.setPort(PORT);
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);

        connection = new XMPPTCPConnection(configBuilder.build());

        new Thread(new Runnable() {
            public void run() {
                try {
                    // Connect to the server
                    connection.connect();
                    Log.d("RegisterActivity", "Successfully connected to: " + connection.getHost());

                    AccountManager am = AccountManager.getInstance(connection);
                    am.sensitiveOperationOverInsecureConnection(true);//Esta parte en un futuro se debe quitar cuando las conexiones sean seguras...

                    Map<String, String> mp = new HashMap<String, String>();

                    // adding or set elements in Map by put method key and value
                    // pair
                    mp.put("username", username);
                    mp.put("password", password);
                    mp.put("name", username);
                    mp.put("email", email);

                    try {
                        am.createAccount(username, password, mp);
                        Log.d("RegisterActivity", "RegisterOK");
                        connection.disconnect();
                        Log.d("RegisterActivity", "disconnectOK");

                        if(((Network) RegisterActivity.this.getApplication()).isNetworkConnected()){
                            Log.d("RegisterActivity",  "networkOK");
                            ((Network) RegisterActivity.this.getApplication()).login(username, password, true, false);
                        }else{
                            //No network available
                            Log.d("LoginActivity", "No Network Available");
                            ((Network) getApplication()).showAlertDialog("No Network Available!", "Your device is not connected to the Internet.", RegisterActivity.this);
                        }

                    } catch (SmackException.NoResponseException e) {
                        Log.d("RegisterActivity", "NoResponseException");
                        e.printStackTrace();
                    } catch (XMPPException.XMPPErrorException e) {
                        Log.d("RegisterActivity", "XMPPErrorException");
                        //User Already Exists Or Invalid beacause it has a space...
                        ((Network) getApplication()).showAlertDialog("Invalid Username!", "This username already exist or could contain a space.", RegisterActivity.this);
                        clearEditText(usernameEdit);
                        e.printStackTrace();
                    } catch (SmackException.NotConnectedException e) {
                        Log.d("RegisterActivity", "NotConnectedException");
                        e.printStackTrace();
                    }

                }catch (SmackException | IOException | XMPPException e){
                    Log.d("RegisterActivity",  "Error connecting");
                    e.getStackTrace();
                }
            }
        }).start();
    }

    public void clearEditText(EditText editText){
        editText.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
