package com.example.messenger.messenger;

/**
 * Created by Joubert on 17/05/2015.
 */
public class Chat {
    //private variables

    int _id;
    String _user;
    String _chat;

    // Empty constructor
    public Chat(){

    }
    // constructor
    public Chat(int id, String user, String chat){
        this._id = id;
        this._user = user;
        this._chat = chat;
    }
    // constructor
    public Chat(String user, String chat){
        this._user = user;
        this._chat = chat;
    }
    // getting ID
    public int getID(){
        return this._id;
    }
    // setting id
    public void setID(int id){
        this._id = id;
    }
    // getting User
    public String getUser(){
        return this._user;
    }
    // setting User
    public void setUser(String user){
        this._user = user;
    }
    // getting name
    public String getChat(){
        return this._chat;
    }
    // setting name
    public void setChat(String chat){
        this._chat = chat;
    }
}
