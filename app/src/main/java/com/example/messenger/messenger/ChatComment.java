package com.example.messenger.messenger;

/**
 * Created by Joubert on 05/06/2015.
 */
public class ChatComment {
    private boolean side;
    private String comment;
    private String date;

    public ChatComment(boolean side, String comment, String date) {
        super();
        this.side = side;
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

    // getting side
    public boolean getSide(){
        return this.side;
    }
    // setting side
    public void setSide(boolean side){
        this.side = side;
    }
}