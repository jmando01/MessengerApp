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

	private ArrayList<Contact> temp;
	private Handler mHandler = new Handler();
	public static ContactListBaseAdapter adapter = null;
	public static ArrayList<Contact> contacts;
	public static Context context;

	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {

		 View contactListTab = inflater.inflate(R.layout.contact_list_frag, container, false);

		 temp = new ArrayList<Contact>();
		 contacts = new ArrayList<Contact>();

		 context = getActivity();

		 DatabaseHandler db = new DatabaseHandler(getActivity());
		 temp = (ArrayList<Contact>) db.getAllContacts();
		 db.close();

		 for(int i = 0; i < temp.size(); i ++){
			 if(temp.get(i).getUser().equals(LoginActivity.sharedPref.getString("username", "default"))){
				contacts.add(temp.get(i));
			 }
		 }

		 final ListView lv1 = (ListView) contactListTab.findViewById(R.id.contactListLv);
		 adapter = new ContactListBaseAdapter(getActivity(), contacts);
		 lv1.setAdapter(adapter);

		 lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			 @Override
			 public void onItemClick(AdapterView<?> a, View v, int position, long id) {

				 Contact contact = contacts.get(position);

			 }
		 });

		 lv1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			 @Override
			 public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {

				 final Contact contact = contacts.get(position);

				 CharSequence[] items = {"Send a Message", "Delete Contact"};

				 AlertDialog.Builder builder = new AlertDialog.Builder(context);
				 builder.setTitle("Make your selection");
				 builder.setItems(items, new DialogInterface.OnClickListener() {
					 public void onClick(DialogInterface dialog, int item) {
						 if (item == 0) {
							 //Aqui debe ir el codigo para enviar un mensaje al contacto.
						 }
						 if (item == 1) {

							 if (((Network) context.getApplicationContext()).connection.isConnected()) {
								 //Aqui debe ir el codigo para borrar un contacto.

								 new Thread(new Runnable() {
									 public void run() {
										 final String notification;

										 notification = ((Network) context.getApplicationContext()).removeRoster(contact.getContact());

										 mHandler.post(new Runnable() {
											 public void run() {
												 if (!notification.equals("success")) {
													 ((Network) context.getApplicationContext()).showAlertDialog("Notification!", notification, context.getApplicationContext());
												 }
											 }
										 });
									 }
								 }).start();
							 }else{
								 Toast.makeText(context,
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
}

	 

