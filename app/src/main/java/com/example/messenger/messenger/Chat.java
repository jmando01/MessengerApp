package com.example.messenger.messenger;

/**
 * Created by Joubert on 17/05/2015.
 */
public class Chat {
    //private variables

    int _id;
    private String _user;
    private String _chat;
    private String _body;
    private String _sentDate;
    private int _counter;

    // Empty constructor
    public Chat(){

    }

    // constructor
    public Chat(int id, String user, String chat, String body, String sentDate, int counter){
        this._id = id;
        this._user = user;
        this._chat = chat;
        this._body = body;
        this._sentDate = sentDate;
        this._counter = counter;
    }

    // constructor
    public Chat(String user, String chat, String body, String sentDate, int counter){
        this._user = user;
        this._chat = chat;
        this._body = body;
        this._sentDate = sentDate;
        this._counter = counter;
    }

    public int getID(){
        return this._id;
    }
    public void setID(int id){
        this._id = id;
    }
    public String getUser(){
        return this._user;
    }
    public void setUser(String user){
        this._user = user;
    }
    public String getChat(){
        return this._chat;
    }
    public void setChat(String chat){
        this._chat = chat;
    }
    public String getBody() {
        return _body;
    }
    public void setBody(String body) {
        this._body = body;
    }
    public String getSentDate() {
        return _sentDate;
    }
    public void setSentDate(String sentDate) {
        this._sentDate = sentDate;
    }
    public int getCounter(){
        return _counter;
    }
    public void setCounter(int counter){
        this._counter = counter;
    }
}
