package com.example.messenger.messenger;

/**
 * Created by Joubert on 17/05/2015.
 */
public class Contact {

    //private variables
    int _id;
    String _user;
    String _contact;
    boolean _status;

    // Empty constructor
    public Contact(){

    }
    // constructor
    public Contact(int id, String user, String contact, Boolean status){
        this._id = id;
        this._user = user;
        this._contact = contact;
        this._status = status;
    }

    // constructor
    public Contact(String user, String contact, Boolean status){
        this._user = user;
        this._contact = contact;
        this._status = status;
    }
    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting name
    public String getUser(){
        return this._user;
    }

    // setting name
    public void setUser(String user){
        this._user = user;
    }

    // getting phone number
    public String getContact(){
        return this._contact;
    }

    // setting phone number
    public void setContact(String contact){
        this._contact = contact;
    }

    // getting phone number
    public Boolean getStatus(){
        return this._status;
    }

    // setting phone number
    public void setStatus(Boolean status){
        this._status = status;
    }
}