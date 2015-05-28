package com.example.messenger.messenger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ContactListTab extends Fragment {


	private ArrayList<Chat> tempChats;
	private Handler mHandler = new Handler();
	private static ArrayList<Contact> tempContacts;
	public static ContactListBaseAdapter adapter = null;
	public static ArrayList<Contact> contacts;
	public static ArrayList<Chat> chats;
	public static Context context;

	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {

		 View contactListTab = inflater.inflate(R.layout.contact_list_frag, container, false);

		 context = getActivity().getApplicationContext();

		 refreshContactList();

		 final ListView lv1 = (ListView) contactListTab.findViewById(R.id.contactListLv);
		 adapter = new ContactListBaseAdapter(getActivity(), contacts);
		 lv1.setAdapter(adapter);

		 lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			 @Override
			 public void onItemClick(AdapterView<?> a, View v, int position, long id) {

				 Contact contact = contacts.get(position);

				 /*tempChats = new ArrayList<Chat>();
				 chats = new ArrayList<Chat>();

				 DatabaseHandler db = new DatabaseHandler(context);
				 tempChats = (ArrayList<Chat>) db.getAllChats();
				 db.close();

				 for(int i = 0; i < tempChats.size(); i ++){
					 if(tempChats.get(i).getUser().equals(LoginActivity.sharedPref.getString("username", "default"))){
						 Log.d("ChatTabList", "Chat List: " + tempChats.get(i).getChat());
						 chats.add(tempChats.get(i));
					 }
				 }

				 boolean found = false;

				 for(int i = 0; i < tempChats.size(); i ++){
					 if(tempChats.get(i).getChat().equals(contact.getContact())){
						 found = true;
					 }
				 }

				 if(!found){
					 DatabaseHandler dbb = new DatabaseHandler(context);
					 dbb.addChat(new Chat(contact.getUser(), contact.getContact(), " ", " ", " "));
					 dbb.close();
				 }*/
			 }
		 });

		 lv1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			 @Override
			 public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {

				 final Contact contact = contacts.get(position);

				 CharSequence[] items = {"Send a Message", "Delete Contact"};

				 AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				 builder.setTitle("Make your selection");
				 builder.setItems(items, new DialogInterface.OnClickListener() {
					 public void onClick(DialogInterface dialog, int item) {
						 if (item == 0) {
							 //Aqui debe ir el codigo para enviar un mensaje al contacto.
						 }
						 if (item == 1) {

							 if (((Network) getActivity().getApplicationContext()).connection.isConnected()) {
								 //Aqui debe ir el codigo para borrar un contacto.

								 new Thread(new Runnable() {
									 public void run() {
										 final String notification;

										 notification = ((Network) getActivity().getApplicationContext()).removeRoster(contact.getContact());

										 mHandler.post(new Runnable() {
											 public void run() {
												 if (!notification.equals("success")) {
													 ((Network) getActivity().getApplicationContext()).showAlertDialog("Notification!", notification, getActivity().getApplicationContext());
												 }else{
													 setRemoveContactFromContactList(contact.getContact());
												 }
											 }
										 });
									 }
								 }).start();
							 }else{
								 Toast.makeText(getActivity(),
										 "Please wait for reconnection", Toast.LENGTH_LONG)
										 .show();
							 }
						 }
					 }
				 });
				 AlertDialog alert = builder.create();
				 alert.show();
				 return true; //Esto sirve para que no se abran ambos metodos de ontouch
			 }
		 });

		 return contactListTab;
	 }

	public static void setPresenceChanged(String contact, String status){

		Log.d("ContactTabList", "Presence Changed Contact: " + contact + " Status: " + status);
		for(int i = 0; i < contacts.size(); i++){
			if(contact.equals(contacts.get(i).getContact())){
				contacts.get(i).setStatus(status);
				DatabaseHandler db = new DatabaseHandler(context);
				db.updateContact(new Contact(contacts.get(i).getID(), contacts.get(i).getUser(), contacts.get(i).getContact(), status));
				db.close();
			}
		}
		adapter.notifyDataSetChanged();
	}
	//aun no se implementa
	public static void setContactListChanged(String contact){
		Log.d("ContactTabList", "Contact List Changed: " + contact);
		contacts.add(new Contact(LoginActivity.sharedPref.getString("username", "default"), contact, " "));
		adapter.notifyDataSetChanged();
	}

	public void setRemoveContactFromContactList(String contact){

		for(int i = 0; i < contacts.size(); i++){
			if(contact.equals(contacts.get(i).getContact())){

				DatabaseHandler db = new DatabaseHandler(context);
				//para borrar un usuario solo es necesario el ID
				db.deleteContact(new Contact(db.getContactID(contact), "", "", ""));
				db.close();

				contacts.remove(i);
				adapter.notifyDataSetChanged();
				Log.d("Network", "User: " + contact + " has been removed from contact list");
			}
		}
	}

	public static void refreshContactList(){
		tempContacts = new ArrayList<Contact>();
		contacts = new ArrayList<Contact>();

		DatabaseHandler db = new DatabaseHandler(context);
		tempContacts = (ArrayList<Contact>) db.getAllContacts();
		db.close();

		for(int i = 0; i < tempContacts.size(); i ++){
			if(tempContacts.get(i).getUser().equals(LoginActivity.sharedPref.getString("username", "default"))){
				contacts.add(tempContacts.get(i));
			}
		}
	}
}

	 

