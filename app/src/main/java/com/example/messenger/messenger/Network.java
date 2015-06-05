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
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.DefaultExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Joubert on 09/05/2015.
 *****Enviar mensajes y logica demas...
 *****Hay que terminar la parte de la horas.
 *****Hacer un buscador para los contactos.
 ***** PONER UN AVISO DE RECONEXION.
 *
 * Hacer la sincronizacion de contactos.
 * Si vuelvo a agregar el contacto que me borro nos podemos ver otra vez.
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
    private ArrayList<Contact> contacts;
    private ArrayList<com.example.messenger.messenger.Chat> chats;

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

    public String login(String username, String password, boolean autoLogin){
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
                    roster = Roster.getInstanceFor(connection);
                    roster.setSubscriptionMode(Roster.SubscriptionMode.manual);
                    Log.d("Connect", "Roster subscription mode set to: " + roster.getSubscriptionMode());
                    setRosterListener();
                    setSubscriptionListener();
                    setPresence(LoginActivity.sharedPref.getString("status", "Available"));
                    setChatMessageListener();

                    if(!autoLogin){
                        Intent intent = new Intent(getApplicationContext(), ChatListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        LoginActivity.activity.finish();
                    }
                    return "success";
                }catch(SmackException | IOException | XMPPException e){
                    Log.d("Network", "Error logging in");
                    disconect();
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
            return "success";
        }
    }

    public void startReconnectionTimer(){
        Log.d("Network","Reconnection Timer Started");

        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("Network", "Reconnection Timer Started..Connecting");
                login(LoginActivity.sharedPref.getString("username", "default"), LoginActivity.sharedPref.getString("password", "default"), true);
            }
        }, 6 * 1000);
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
                Log.d("Network", "Entries added: " + addresses);
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
                //here we have the logic for presence changed
                Log.d("Network", "Presence Changed: " + presence);
                if (presence.getType() == Presence.Type.available || presence.getType() == Presence.Type.unavailable) {
                    String from = presence.getFrom().toString();

                    if (from.contains("/")) {
                        from = presence.getFrom().substring(0, presence.getFrom().indexOf('/'));
                    }

                    final String finalFrom = from;

                    mHandler.post(new Runnable() {
                        public void run() {
                            if (presence.getType() == Presence.Type.available) {
                                Log.d("Network", "Presence Changed User: " + finalFrom + " Status: " + presence.getStatus());
                                ContactListTab.setPresenceUpdate(finalFrom, presence.getStatus());
                            }
                            if (presence.getType() == Presence.Type.unavailable) {
                                Log.d("Network", "Presence Changed User: " + presence.getFrom() + " Status: Offline");
                                ContactListTab.setPresenceUpdate(finalFrom, "Offline");
                            }
                        }
                    });
                }
            }
        });
    }

    public void setSubscriptionListener(){
        connection.addAsyncStanzaListener(new StanzaListener() {
            @Override
            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                Log.e("Network", "something called: " + packet);
                final Presence presence = (Presence) packet;
                //Here we have the logic for subcribers
                if (presence.getType() == Presence.Type.subscribe) {
                    Log.d("Network", "Incoming Packet type subscribe from: " + presence.getFrom());
                    //from new user
                    if (roster.getEntry(presence.getFrom()) == null) {
                        Log.d("Network", "Roster not found!");
                        //save request locally for later accept/reject
                        //later accept will send back a subscribe & subscribed presence to user with fromId
                        //or accept immediately by sending back subscribe and unsubscribed right now
                        Presence subscribe = new Presence(Presence.Type.subscribe);
                        subscribe.setTo(presence.getFrom());
                        Presence subscribed = new Presence(Presence.Type.subscribed);
                        subscribed.setTo(presence.getFrom());

                        try {
                            connection.sendStanza(subscribe);
                            connection.sendStanza(subscribed);
                            Log.d("Network", "Subscribe and Subscribed Sent");
                            contacts = new ArrayList<Contact>();

                            DatabaseHandler dbb = new DatabaseHandler(getApplicationContext());
                            contacts = (ArrayList<Contact>) dbb.getAllContacts();
                            dbb.close();

                            boolean found = false;

                            for (int i = 0; i < contacts.size(); i++) {
                                if (contacts.get(i).getContact().equals(presence.getFrom())) {
                                    found = true;
                                }
                            }

                            if (!found) {
                                DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                                db.addContact(new Contact(LoginActivity.sharedPref.getString("username", "default"), presence.getFrom(), "<Pending>"));
                                db.close();

                                mHandler.post(new Runnable() {
                                    public void run() {
                                        ContactListTab.setContactListChanged(presence.getFrom());
                                    }
                                });
                                Log.d("Network", "User: " + presence.getFrom() + " has been added.");
                            } else {
                                Log.d("Network", "This contact already exist");
                            }
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }
                    }
                    //from a user that previously accepted your request
                    else {
                        Log.d("Network", "Roster found!");
                        //send back subscribed presence to user with fromId
                        Presence subscribed = new Presence(Presence.Type.subscribed);
                        subscribed.setTo(presence.getFrom());
                        try {
                            connection.sendStanza(subscribed);
                            Log.d("Network", "Subscribed Sent");
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (presence.getType() == Presence.Type.unsubscribe) {
                    Log.d("Network", "Unsubcribe Arrived");
                }
                if (presence.getType() == Presence.Type.unsubscribed) {
                    Log.d("Network", "Unsubcribed Arrived");
                }

            }
        }, new StanzaTypeFilter(Presence.class));
    }

    public String addRoster(final String contact){
        //Lo que esta en null es para saber si pertenece a un grupo
        try {
            contacts = new ArrayList<Contact>();

            DatabaseHandler dbb = new DatabaseHandler(getApplicationContext());
            contacts = (ArrayList<Contact>) dbb.getAllContacts();
            dbb.close();

            boolean found = false;

            for(int i = 0; i < contacts.size(); i ++){
                if(contacts.get(i).getContact().equals(contact)){
                    found = true;
                }
            }

            if(!found){
                roster.createEntry(contact, contact, null);

                DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                db.addContact(new Contact(LoginActivity.sharedPref.getString("username", "default"), contact, "<Pending>"));
                db.close();

                mHandler.post(new Runnable() {
                    public void run() {
                        ContactListTab.setContactListChanged(contact);
                    }
                });
                Log.d("Network", "User: " + contact + " has been added.");
                return "success";
            }else{
                Log.d("Network", "This contact already exist");
                return "This contact alerady exists";
            }
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

    public String removeRoster(String contact){
        try {
            if (roster.getEntry(contact) != null) {
                Message msg = new Message(contact, Message.Type.chat);
                msg.setFrom(LoginActivity.sharedPref.getString("username", "default"));
                msg.setBody("<customevent=?removeroster>");
                connection.sendStanza(msg);
                roster.removeEntry(roster.getEntry(contact));
                Log.d("Network", "User: " + contact + " has been removed from roster");
                return "success";
            }
            return "success";
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

    public String setPresence(String status){
        // Create a new presence.
        Presence presence = new Presence(Presence.Type.available);
        presence.setStatus(status);
        // Send the packet.
        try {
            connection.sendStanza(presence);
            Log.d("Network", "Changed status to: " + status);
            return "success";
        } catch (SmackException.NotConnectedException e) {
            Log.d("Network", "Error changing status");
            e.printStackTrace();
            return "NotConnectedException";
        }
    }

    public void sendMessage(final String contact, final String message){

        Message msg = new Message(contact, Message.Type.chat);
        msg.setFrom(LoginActivity.sharedPref.getString("username", "default"));
        msg.setBody(message);
        try {
            connection.sendStanza(msg);
            chats = new ArrayList<com.example.messenger.messenger.Chat>();

            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
            chats = (ArrayList<com.example.messenger.messenger.Chat>) db.getAllChats();
            db.close();

            boolean found = false;

            for(int i = 0; i < chats.size(); i ++){
                if(chats.get(i).getChat().equals(contact)){
                    found = true;
                }
            }

            if(!found){
                DatabaseHandler dbb = new DatabaseHandler(getApplicationContext());
                dbb.addChat(new com.example.messenger.messenger.Chat(LoginActivity.sharedPref.getString("username", "default"), contact, message, "now", 0));
                dbb.close();

                mHandler.post(new Runnable() {
                    public void run() {
                        ChatListTab.setChatListChanged(contact, message, "now", 0);
                    }
                });

            }else{
                mHandler.post(new Runnable() {
                    public void run() {
                        ChatListTab.setChatUpdate(contact, message, "now", 0);
                    }
                });
            }
        } catch (SmackException.NotConnectedException e) {
            Log.d("Network", "Error sending message");
            e.printStackTrace();
        }
    }

    public void setChatMessageListener(){
        ChatManager.getInstanceFor(connection).addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {
                chat.addMessageListener(new ChatMessageListener() {
                    @Override
                    public void processMessage(Chat chat, final Message message) {
                        if (message.getType() == Message.Type.chat || message.getType() == Message.Type.normal) {
                            if (message.getBody() != null && !message.getBody().equals("<customevent=?removeroster>")) {
                                Log.d("Network", message.getFrom().substring(0, message.getFrom().indexOf("/")) + " : " + message.getBody());

                                final String contact = message.getFrom().substring(0, message.getFrom().indexOf("/"));

                                chats = new ArrayList<com.example.messenger.messenger.Chat>();

                                DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                                chats = (ArrayList<com.example.messenger.messenger.Chat>) db.getAllChats();
                                db.close();

                                boolean found = false;

                                for (int i = 0; i < chats.size(); i++) {
                                    if (chats.get(i).getChat().equals(contact)) {
                                        found = true;
                                    }
                                }

                                if (!found) {
                                    mHandler.post(new Runnable() {
                                        public void run() {
                                            if (ChatActivity.isRunning) {
                                                DatabaseHandler dbb = new DatabaseHandler(getApplicationContext());
                                                dbb.addChat(new com.example.messenger.messenger.Chat(LoginActivity.sharedPref.getString("username", "default"), contact, message.getBody(), "now", 0));
                                                ChatListTab.setChatListChanged(contact, message.getBody(), "now", 0);
                                                dbb.close();
                                            } else {
                                                DatabaseHandler dbb = new DatabaseHandler(getApplicationContext());
                                                dbb.addChat(new com.example.messenger.messenger.Chat(LoginActivity.sharedPref.getString("username", "default"), contact, message.getBody(), "now", 1));
                                                ChatListTab.setChatListChanged(contact, message.getBody(), "now", 1);
                                                dbb.close();
                                            }
                                        }
                                    });
                                } else {
                                    mHandler.post(new Runnable() {
                                        public void run() {
                                            if (ChatActivity.isRunning) {
                                                ChatListTab.setChatUpdate(contact, message.getBody(), "now", getChatCounter(contact));
                                            } else {
                                                ChatListTab.setChatUpdate(contact, message.getBody(), "now", getChatCounter(contact));
                                            }
                                        }
                                    });
                                }
                            }
                            //esta parte elimina un contacto que nos elimino.
                            if(message.getBody().equals("<customevent=?removeroster>")){

                                final String contact = message.getFrom().substring(0, message.getFrom().indexOf("/"));

                                chats = new ArrayList<com.example.messenger.messenger.Chat>();

                                DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                                chats = (ArrayList<com.example.messenger.messenger.Chat>) db.getAllChats();
                                db.close();

                                boolean found = false;

                                for (int i = 0; i < chats.size(); i++) {
                                    if (chats.get(i).getChat().equals(contact)) {
                                        found = true;
                                    }
                                }

                                if(found){
                                    mHandler.post(new Runnable() {
                                        public void run() {
                                            ChatListTab.setRemoveChatFromChatList(contact);
                                        }
                                    });

                                }

                                contacts = new ArrayList<Contact>();

                                DatabaseHandler dbb = new DatabaseHandler(getApplicationContext());
                                contacts = (ArrayList<Contact>) dbb.getAllContacts();
                                dbb.close();

                                found = false;

                                for (int i = 0; i < contacts.size(); i++) {
                                    if (contacts.get(i).getContact().equals(contact)) {
                                        found = true;
                                    }
                                }

                                if(found){
                                    mHandler.post(new Runnable() {
                                        public void run() {
                                            ContactListTab.setRemoveContactFromContactList(contact);
                                        }
                                    });
                                }
                                removeRoster(message.getFrom());
                            }
                        }
                    }
                });
            }
        });

        /* if(message.getType() == Message.Type.chat) {
      //single chat message
   } else if(message.getType() == Message.Type.groupchat) {
      //group chat message
   } else if(message.getType() == Message.Type.error) {
      //error message
   }*/
    }

    public int getChatCounter(String chat){
        com.example.messenger.messenger.Chat updateCounter = new com.example.messenger.messenger.Chat();
        int counter = 0;

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        updateCounter = db.getChat(db.getChatID(chat));

        counter = updateCounter.getCounter();
        counter = counter + 1;

        updateCounter.setCounter(counter);

        db.updateChat(updateCounter);
        db.close();

        return counter;
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