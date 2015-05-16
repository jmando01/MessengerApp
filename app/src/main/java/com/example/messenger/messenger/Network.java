package com.example.messenger.messenger;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Joubert on 09/05/2015.
 */
public class Network extends Application {

    public AbstractXMPPConnection connection;

    private String HOST = "192.168.1.4";
    private String SERVICE = "localhost";
    private int PORT = 5222;
    private String RESOURCE = "Home";
    private SharedPreferences.Editor editor;

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    public void connect(){

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
                    Log.d("Network", "Successfully connected to: " + connection.getHost());
                }catch (SmackException | IOException | XMPPException e){
                    Log.d("Network",  "Error connecting");
                    e.getStackTrace();
                }
            }
        }).start();
    }

    public String login (String username, String password, boolean autoLogin, boolean reconnecting){
        if(connection == null || !connection.isConnected()){
            // Create the configuration for this new connection
            XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
            configBuilder.setUsernameAndPassword(username+"@localhost", password);
            //configBuilder.setResource(RESOURCE);
            configBuilder.setServiceName(SERVICE);
            configBuilder.setHost(HOST);
            configBuilder.setPort(PORT);
            configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            ReconnectionManager.setEnabledPerDefault(true);

            connection = new XMPPTCPConnection(configBuilder.build());

            if(autoLogin && !reconnecting){
                Intent intent = new Intent(getApplicationContext(), ChatListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                LoginActivity.activity.finish();
            }

            try {
                // Connect to the server
                connection.connect();
                Log.d("Network", "Successfully connected to: " + connection.getHost());
                try {
                    // Log into the server
                    connection.login();
                    Log.d("Network", "Logged in as: " + connection.getUser());

                    editor = LoginActivity.sharedPref.edit();
                    editor.putString("username", username);
                    editor.putString("password", password);
                    editor.putBoolean("autoLogin", true);
                    editor.commit();

                    setConnectionListener();

                    if(!autoLogin  && reconnecting){
                        Intent intent = new Intent(getApplicationContext(), ChatListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        LoginActivity.activity.finish();
                    }
                    return null;
                }catch(SmackException | IOException | XMPPException e){
                    Log.d("Network", "Error logging in");
                    e.getStackTrace();
                    return "Your username or password is wrong.";
                }
            }catch (SmackException | IOException | XMPPException e){
                Log.d("Network",  "Error connecting");
                e.getStackTrace();
                startReconnectionTimer();
                return "Error connecting to our services.";
            }
        }else{
            Log.d("Network", "Already connected");
            Log.d("Network", "Logged in as: " + connection.getUser());

            Intent intent = new Intent(getApplicationContext(), ChatListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            LoginActivity.activity.finish();
            return "The is already a connection";
        }
    }

    public void startReconnectionTimer(){

        new CountDownTimer(6000, 1000) {
            public void onTick(final long millisUntilFinished) {
                Log.d("ChatEntryActivity","ReconnectinIn: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                login (LoginActivity.sharedPref.getString("username", "default"), LoginActivity.sharedPref.getString("password", "default"), true, true);
            }
        }.start();
    }

    public void setConnectionListener() {

        connection.addConnectionListener(new ConnectionListener() {
            @Override
            public void connected(XMPPConnection connection) {
                Log.d("Network",  "connected");
            }

            @Override
            public void authenticated(XMPPConnection connection, boolean resumed) {
                Log.d("Network",  "authenticated");
            }

            @Override
            public void connectionClosed() {
                Log.d("Network",  "connection closed");
            }

            @Override
            public void connectionClosedOnError(Exception e) {
                Log.d("Network",  "connectionClosedOnError");
            }

            @Override
            public void reconnectionSuccessful() {
                Log.d("Network",  "reconnectionSuccessful");
            }

            @Override
            public void reconnectingIn(int seconds) {
                Log.d("Network",  "reconnectingIn: " + seconds);
            }

            @Override
            public void reconnectionFailed(Exception e) {
                Log.d("Network",  "reconnectionFailed");
                e.getStackTrace();
            }
        });
    }

    public void disconect(){
        new Thread(new Runnable() {
            public void run() {
                try {
                    // Log into the server
                    connection.disconnect();
                    Log.d("Network",  "Disconnected");
                }catch(Exception e){
                    Log.d("Network",  "Error disconnecting");
                    e.getStackTrace();
                }
            }
        }).start();
    }

    public void showAlertDialog(String title, String message, Context context){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setNeutralButton("Got it!",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
