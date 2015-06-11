package com.example.messenger.messenger;

/**
 * Created by Joubert on 17/05/2015.
 */
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "localDBManager";
    // Message table name
    private static final String MESSAGE_ARCHIVE_TABLE = "messageArchive";
    // Contacts Table
    private static final String CONTACTS_TABLE = "contacts";
    // Chat Contacts Table
    private static final String CHATS_TABLE = "chat_contacts";

    // Message Archive Table Columns
    private static final String KEY_ID = "id";
    private static final String KEY_FROMJID = "fromjid";
    private static final String KEY_TOJID = "tojid";
    private static final String KEY_SENTDATE = "sentdate";
    private static final String KEY_BODY = "body";

    //Contacts Table Columns
    private static final String KEY_USER = "user";
    private static final String KEY_CONTACT = "contact";
    private static final String KEY_STATUS = "status";

    //Chats Table Colums
    private static final String KEY_CHAT = "chat";
    private static final String KEY_COUNTER = "counter";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Message Archive Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MESSAGE_ARCHIVE_TABLE = "CREATE TABLE " + MESSAGE_ARCHIVE_TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_FROMJID + " TEXT,"
                + KEY_TOJID + " TEXT,"
                + KEY_BODY + " TEXT,"
                + KEY_SENTDATE + " TEXT " + ")";
        db.execSQL(CREATE_MESSAGE_ARCHIVE_TABLE);

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + CONTACTS_TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_USER + " TEXT,"
                + KEY_CONTACT + " TEXT,"
                + KEY_STATUS + " TEXT " + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

        String CREATE_CHAT_CONTACTS_TABLE = "CREATE TABLE " + CHATS_TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_USER + " TEXT,"
                + KEY_CHAT + " TEXT,"
                + KEY_BODY + " TEXT,"
                + KEY_SENTDATE + " TEXT,"
                + KEY_COUNTER + " INTEGER " + ")";
        db.execSQL(CREATE_CHAT_CONTACTS_TABLE);
    }

    // Upgrading Database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_ARCHIVE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CHATS_TABLE);
        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new message
    void addMessage(MessageArchive message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FROMJID, message.getFromJid());
        values.put(KEY_TOJID, message.getToJid());
        values.put(KEY_BODY, message.getBody());
        values.put(KEY_SENTDATE, message.getSentDate());

        // Inserting Row
        db.insert(MESSAGE_ARCHIVE_TABLE, null, values);
        db.close();
    }

    // Adding new contact
    void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER, contact.getUser());
        values.put(KEY_CONTACT, contact.getContact());
        values.put(KEY_STATUS, contact.getStatus());
        // Inserting Row
        db.insert(CONTACTS_TABLE, null, values);
        db.close();
    }

    // Adding new chat
    void addChat(ChatList chat) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER, chat.getUser());
        values.put(KEY_CHAT, chat.getChat());
        values.put(KEY_BODY, chat.getBody());
        values.put(KEY_SENTDATE, chat.getSentDate());
        values.put(KEY_COUNTER, chat.getCounter());

        // Inserting Row
        db.insert(CHATS_TABLE, null, values);
        db.close();
    }

    // Getting single message
    MessageArchive getMessage(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(MESSAGE_ARCHIVE_TABLE, new String[]{KEY_ID,
                        KEY_FROMJID, KEY_TOJID, KEY_BODY, KEY_SENTDATE}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        MessageArchive message = new MessageArchive(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        // return message
        cursor.close();
        db.close();
        return message;
    }

    // Getting single contact
    Contact getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(CONTACTS_TABLE, new String[]{KEY_ID,
                        KEY_USER, KEY_CONTACT, KEY_STATUS}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));
        // return contact
        cursor.close();
        db.close();
        return contact;
    }

    // Getting single Chat
    ChatList getChat(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(CHATS_TABLE, new String[]{KEY_ID,
                        KEY_USER, KEY_CHAT, KEY_BODY, KEY_SENTDATE,
                        KEY_COUNTER}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        ChatList chat = new ChatList(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3),
                cursor.getString(4), Integer.parseInt(cursor.getString(5)));
        // return contact
        cursor.close();
        db.close();
        return chat;
    }

    // Getting All messages
    public List<MessageArchive> getAllMessages() {
        List<MessageArchive> messageList = new ArrayList<MessageArchive>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + MESSAGE_ARCHIVE_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MessageArchive message = new MessageArchive();
                message.setID(Integer.parseInt(cursor.getString(0)));
                message.setFromJid(cursor.getString(1));
                message.setToJid(cursor.getString(2));
                message.setBody(cursor.getString(3));
                message.setSentDate(cursor.getString(4));

                // Adding contact to list
                messageList.add(message);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return contact list
        return messageList;
    }

    // Getting All Contacts
    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + CONTACTS_TABLE + " WHERE user = '"+ LoginActivity.sharedPref.getString("username", "default") +"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setUser(cursor.getString(1));
                contact.setContact(cursor.getString(2));
                contact.setStatus(cursor.getString(3));

                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return contact list
        return contactList;
    }

    // Getting All Chats
    public List<ChatList> getAllChats() {
        List<ChatList> chatList = new ArrayList<ChatList>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + CHATS_TABLE + " WHERE user = '"+ LoginActivity.sharedPref.getString("username", "default") +"'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ChatList chat = new ChatList();
                chat.setID(Integer.parseInt(cursor.getString(0)));
                chat.setUser(cursor.getString(1));
                chat.setChat(cursor.getString(2));
                chat.setBody(cursor.getString(3));
                chat.setSentDate(cursor.getString(4));
                chat.setCounter(Integer.parseInt(cursor.getString(5)));
                // Adding contact to list
                chatList.add(chat);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return contact list
        return chatList;
    }

    // Updating single message
    public void updateMessage(MessageArchive message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FROMJID, message.getFromJid());
        values.put(KEY_TOJID, message.getToJid());
        values.put(KEY_BODY, message.getBody());
        values.put(KEY_SENTDATE, message.getSentDate());
        // updating row
        db.update(MESSAGE_ARCHIVE_TABLE, values, KEY_ID + " = ?",
                new String[] { String.valueOf(message.getID())});
        db.close();
    }

    // Updating single contact
    public void updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER, contact.getUser());
        values.put(KEY_CONTACT, contact.getContact());
        values.put(KEY_STATUS, contact.getStatus());
        // updating row
        db.update(CONTACTS_TABLE, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID())});
        db.close();
    }

    // Updating single chat
    public void updateChat(ChatList chat) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER, chat.getUser());
        values.put(KEY_CHAT, chat.getChat());
        values.put(KEY_BODY, chat.getBody());
        values.put(KEY_SENTDATE, chat.getSentDate());
        values.put(KEY_COUNTER, chat.getCounter());
        // updating row
        db.update(CHATS_TABLE, values, KEY_ID + " = ?",
                new String[]{String.valueOf(chat.getID()) });
        db.close();
    }

    // Deleting single message
    public void deleteMessage(MessageArchive message) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MESSAGE_ARCHIVE_TABLE, KEY_ID + " = ?",
                new String[]{String.valueOf(message.getID())});
        db.close();
    }

    // Deleting single contact
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CONTACTS_TABLE, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID())});
        db.close();
    }

    // Deleting single chat
    public void deleteChat(ChatList chat) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CHATS_TABLE, KEY_ID + " = ?",
                new String[]{String.valueOf(chat.getID())});
        db.close();
    }

    // Getting messages Count
    public int getMessagesCount() {
        String countQuery = "SELECT  * FROM " + MESSAGE_ARCHIVE_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        // return count
        int count;
        count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + CONTACTS_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        // return count
        int count;
        count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    // Getting Chats Count
    public int getChatsCount() {
        String countQuery = "SELECT  * FROM " + CHATS_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        // return count
        int count;
        count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    // Getting contact ID
    public int getContactID(String contact) {
        String selectQuery = "SELECT *  FROM " + CONTACTS_TABLE + " WHERE user = '" + LoginActivity.sharedPref.getString("username", "default") + "' AND contact = '" + contact + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int ID = 0;
        if (cursor.moveToFirst()) {
            ID = Integer.parseInt(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return ID;
    }

    // Getting chat ID
    public int getChatID(String chat) {
        String selectQuery = "SELECT *  FROM " + CHATS_TABLE + " WHERE user = '" + LoginActivity.sharedPref.getString("username", "default") + "' AND chat = '" + chat + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int ID = 0;
        if (cursor.moveToFirst()) {
            ID = Integer.parseInt(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return ID;
    }
}