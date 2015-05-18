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
import android.os.Handler;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Joubert on 09/05/2015.
 * Hay que terminar el metodo de borrar un roster. Hay que terminar lo que sigue primero.
 * Hay que agregar el metodo de onlongtouch para que aparezcan opciones. y asi poder borrar contacto por ahora...
 * verificar para todas las cuentas
 * hacer una revision general..
 * Empezar de atras pa lante
 */
public class Network extends Application {

    public AbstractXMPPConnection connection;

    private String HOST = "192.168.1.4";
    private String OPENFIRESERVICE = "localhost";
    private String RESOURCE = "Home";
    private int PORT = 5222;
    private boolean clreconnection = false;
    private SharedPreferences.Editor editor;
    private ConnectionListener connectionListener;
    private Timer timer;
    private Roster roster;
    private Handler mHandler = new Handler();
    public static String SERVICE = "@localhost";

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    public String login(String username, String password, boolean autoLogin, boolean reconnectionTimer){
        if((connection == null) || (!connection.isConnected() && !clreconnection)){
            // Create the configuration for this new connection
            XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
            configBuilder.setUsernameAndPassword(username, password);
            //configBuilder.setResource(RESOURCE);
            configBuilder.setServiceName(OPENFIRESERVICE);
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
                    Log.d("Network", "Username: " + username);
                    Log.d("Network", "Logged in as: " + connection.getUser());

                    editor = LoginActivity.sharedPref.edit();
                    editor.putString("username", username);
                    editor.putString("password", password);
                    editor.putBoolean("autoLogin", true);
                    editor.commit();

                    setConnectionListener();
                    setPresence(LoginActivity.sharedPref.getString("status", "Available"));

                    roster = Roster.getInstanceFor(connection);
                    Log.d("Connect", "Roster subscription mode set to: " + roster.getSubscriptionMode());
                    setRosterListener();

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
                Log.d("Network", "Error connecting... ");
                e.getStackTrace();

                if(autoLogin && connectionListener == null){
                    Log.d("Network", "Attempting to recconect Timer");
                    startReconnectionTimer();
                }

                return "Error connecting to our services.";
            }
        } else {
            Log.d("Network", "Already connected");
            Log.d("Network", "Username: " + username);
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
                clreconnection = false;
            }

            @Override
            public void reconnectingIn(int seconds) {
                Log.d("Network", "reconnectingIn: " + seconds);
                clreconnection = true;
            }

            @Override
            public void reconnectionFailed(Exception e) {
                Log.d("Network", "reconnectionFailed");
                e.getStackTrace();
            }
        });
    }

    public void setRosterListener(){
        roster.addRosterListener(new RosterListener() {
            @Override
            public void entriesAdded(Collection<String> addresses) {
                Log.d("Network", "Entries Added: " + addresses.toString());
                final String entry = addresses.toString().substring(1, addresses.toString().length() - 1);
                //Existe la posibilidad de hacer un metodo para aceptar los usuarios si queremos.

                DatabaseHandler dbb = new DatabaseHandler(getApplicationContext());
                List<Contact> contacts = dbb.getAllContacts();
                dbb.close();

                boolean found = false;

                for(int i = 0; i < contacts.size(); i++){
                    if(contacts.get(i).getContact().equals(entry) && contacts.get(i).getUser().equals(LoginActivity.sharedPref.getString("username", "default"))){
                        found = true;
                    }
                }

                if(!found){
                    addRoster(entry);//Ya tiene el service
                    DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                    db.addContact(new Contact(LoginActivity.sharedPref.getString("username", "default"), entry, " "));
                    db.close();

                    mHandler.post(new Runnable() {
                        public void run() {
                            ContactListTab.setContactListChanged(entry);
                        }
                    });
                }
            }

            @Override
            public void entriesUpdated(Collection<String> addresses) {
                Log.d("Network", "Entries Updated: " + addresses);
            }

            @Override
            public void entriesDeleted(Collection<String> addresses) {
                Log.d("Network", "Entries Deleted: " + addresses);
            }

            @Override
            public void presenceChanged(final Presence presence) {
                Log.d("Network", "Presence Changed User: " + presence.getFrom() + " Status: " + presence.getStatus());

                String from = presence.getFrom().toString();

                if(from.contains("/")){
                    from = presence.getFrom().substring(0, presence.getFrom().indexOf('/'));
                }

                final String finalFrom = from;

                mHandler.post(new Runnable() {
                    public void run() {
                        try {
                            ContactListTab.setPresenceChanged(finalFrom, presence.getStatus() == null ? "Offline" : presence.getStatus().toString());
                        }catch(Exception e){
                            Log.d("Network", "Presence Changed  ERROR");
                            e.getStackTrace();
                        }
                    }
                });
            }
        });
    }

    public String addRoster(String username){
        //Lo que esta en null es para saber si pertenece a un grupo
        try {
            roster.createEntry(username, username, null);
            return null;
        } catch (SmackException.NotLoggedInException e) {
            Log.d("Network", "NotLoggedInException");
            e.printStackTrace();
            return "User not logged in. Wait for reconnection.";
        } catch (SmackException.NoResponseException e) {
            Log.d("Network", "NoResponseException");
            e.printStackTrace();
            return "There is no response from server.";
        } catch (XMPPException.XMPPErrorException e) {
            Log.d("Network", "XMPPErrorException");
            e.printStackTrace();
            return "General error has ocurred.";
        } catch (SmackException.NotConnectedException e) {
            Log.d("Network", "NotConnectedException");
            e.printStackTrace();
            return "You are not connected to Internet.";
        }
    }

    public String removeRoster(String username){
        try {
            roster.removeEntry(roster.getEntry(username));
            return null;
        } catch (SmackException.NotLoggedInException e) {
            Log.d("Network", "NotLoggedInException");
            e.printStackTrace();
            return "User not logged in. Wait for reconnection.";
        } catch (SmackException.NoResponseException e) {
            Log.d("Network", "NoResponseException");
            e.printStackTrace();
            return "There is no response from server.";
        } catch (XMPPException.XMPPErrorException e) {
            Log.d("Network", "XMPPErrorException");
            e.printStackTrace();
            return "General error has ocurred.";
        } catch (SmackException.NotConnectedException e) {
            Log.d("Network", "NotConnectedException");
            e.printStackTrace();
            return "You are not connected to Internet.";
        }
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
                    clreconnection = false;
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
                .setNeutralButton("Got it!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}