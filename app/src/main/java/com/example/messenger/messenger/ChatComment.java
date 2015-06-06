package com.example.messenger.messenger;

/**
 * Created by Joubert on 05/06/2015.
 */
public class ChatComment {
    public boolean left;
    public String comment;
    public String date;

    public ChatComment(boolean left, String comment, String date) {
        super();
        this.left = left;
        this.comment = comment;
        this.date = date;
    }

    // getting ID
    public String getComment(){
        return this.comment;
    }

    // setting id
    public void setComment(String comment){
        this.comment = comment;
    }

    // getting date
    public String getDate(){
        return this.date;
    }

    // setting date
    public void setDate(String date){
        this.date = date;
    }
}