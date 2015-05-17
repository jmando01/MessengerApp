package com.example.messenger.messenger;

/**
 * Created by Joubert on 17/05/2015.
 */
public class MessageArchive {
    //private variables
    int _id;
    private String _fromjid;
    private String _tojid;
    private String _sentdate;
    private String _body;

    // Empty constructor
    public MessageArchive(){

    }

    // constructor
    public MessageArchive(int id, String fromjid, String tojid, String sentdate, String body){
        this._id = id;
        this._fromjid = fromjid;
        this._tojid = tojid;
        this._sentdate = sentdate;
        this._body = body;
    }

    // constructor
    public MessageArchive(String fromjid, String tojid, String sentdate, String body){
        this._fromjid = fromjid;
        this._tojid = tojid;
        this._sentdate = sentdate;
        this._body = body;
    }

    // getting ID
    public int getID(){
        return this._id;
    }

    // setting ID
    public void setID(int id){
        this._id = id;
    }

    // getting FromJID
    public String getFromJid(){
        return this._fromjid;
    }

    // setting FromJIF
    public void setFromJid(String fromjid){
        this._fromjid = fromjid;
    }

    // getting ToJID
    public String getToJid(){
        return this._tojid;
    }

    // setting ToJID
    public void setToJid(String tojid){
        this._tojid = tojid;
    }

    // getting SentDate
    public String getSentDate(){
        return this._sentdate;
    }

    // setting SentDate
    public void setSentDate(String sentdate){
        this._sentdate = sentdate;
    }

    // getting Body
    public String getBody(){
        return this._body;
    }

    // setting Body
    public void setBody(String body){
        this._body = body;
    }
}
