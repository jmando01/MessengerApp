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
 * En ves de hacer el proceso de Reconnect simplemente se podria llamar a connect neuvamente.
 */
public class Network extends Application {

    public AbstractXMPPConnection connection;

    private String HOST = "192.168.1.4";
    private String SERVICE = "localhost";
    private int PORT = 5222;
    private String RESOURCE = "Home";
    private SharedPreferences.Editor editor;
    private ConnectionListener connectionListener;
    private boolean reconnection = false;
    private Timer timer;

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

    public String login (String username, String password, boolean autoLogin, boolean reconnectionTimer){
        if((connection == null) || (!connection.isConnected() && !reconnection)){
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

            if(autoLogin && !reconnectionTimer){
                Log.d("Network", "Autologin");
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
                    setPresence(LoginActivity.sharedPref.getString("status", "Available"));

                    if(!autoLogin){
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
                Log.d("Network", "Error connecting");
                e.getStackTrace();

                if(autoLogin && connectionListener == null){
                    startReconnectionTimer();
                }

                return "Error connecting to our services.";
            }
        } else {
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
        Log.d("Network","Reconnection Timer Started");

        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("Network", "Reconnection Timer Started..Connecting");
                login(LoginActivity.sharedPref.getString("username", "default"), LoginActivity.sharedPref.getString("password", "default"), true, true);
            }
        }, 12 * 1000);
    }

    public void setConnectionListener() {

        connection.addConnectionListener(connectionListener = new ConnectionListener() {
            @Override
            public void connected(XMPPConnection connection) {
                Log.d("Network", "connected");
            }

            @Override
            public void authenticated(XMPPConnection connection, boolean resumed) {
                Log.d("Network", "authenticated");
            }

            @Override
            public void connectionClosed() {
                Log.d("Network", "connection closed");
            }

            @Override
            public void connectionClosedOnError(Exception e) {
                Log.d("Network", "connectionClosedOnError");
            }

            @Override
            public void reconnectionSuccessful() {
                Log.d("Network", "reconnectionSuccessful");
                reconnection = false;
            }

            @Override
            public void reconnectingIn(int seconds) {
                Log.d("Network", "reconnectingIn: " + seconds);
                reconnection = true;
            }

            @Override
            public void reconnectionFailed(Exception e) {
                Log.d("Network", "reconnectionFailed");
                e.getStackTrace();
            }
        });
    }

    public boolean setPresence(String status){
        // Create a new presence.
        Presence presence = new Presence(Presence.Type.available);
        presence.setStatus(status);
        // Send the packet.
        try {
            connection.sendStanza(presence);
            Log.d("Network", "Changed status to: "+ status);
            return true;
        } catch (SmackException.NotConnectedException e) {
            Log.d("Network", "Error changing status");
            e.printStackTrace();
            return false;
        }
    }

    public void disconect(){
        new Thread(new Runnable() {
            public void run() {
                try {
                    // Log into the server
                    connection.disconnect();
                    if(connectionListener != null){
                        connection.removeConnectionListener(connectionListener);
                    }
                    if(timer != null){
                        timer.cancel();
                    }
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
