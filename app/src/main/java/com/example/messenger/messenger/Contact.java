package com.example.messenger.messenger;

/**
 * Created by Joubert on 17/05/2015.
 */
public class Contact {

    //private variables
    private int _id;
    private String _user;
    private String _contact;
    private String _status;

    // Empty constructor
    public Contact(){

    }
    // constructor
    public Contact(int id, String user, String contact, String status){
        this._id = id;
        this._user = user;
        this._contact = contact;
        this._status = status;
    }

    // constructor
    public Contact(String user, String contact, String status){
        this._user = user;
        this._contact = contact;
        this._status = status;
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

    public String getContact(){
        return this._contact;
    }

    public void setContact(String contact){
        this._contact = contact;
    }

    public String getStatus(){
        return this._status;
    }

    public void setStatus(String status){
        this._status = status;
    }
}