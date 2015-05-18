package com.example.messenger.messenger;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ChatListTab extends Fragment {
	ArrayList<String> list = new ArrayList<String>();

	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	 
	        View chatlisttab = inflater.inflate(R.layout.chat_list_frag, container, false);

		 	DatabaseHandler db = new DatabaseHandler(getActivity());
		 	db.addContact(new Contact(LoginActivity.sharedPref.getString("username", "default")+"@localhost", "pedro@localhost", true));
		 	db.addContact(new Contact(LoginActivity.sharedPref.getString("username", "default")+"@localhost", "juan@localhost", true));
		 	db.addContact(new Contact(LoginActivity.sharedPref.getString("username", "default")+"@localhost", "ricardo@localhost", true));
		 	db.addContact(new Contact(LoginActivity.sharedPref.getString("username", "default")+"@localhost", "pedro@localhost", true));
		 	db.close();

		 	DatabaseHandler dbb = new DatabaseHandler(getActivity());
		 	List<Contact> contacts = dbb.getAllContacts();
		 	dbb.close();

		 	for(int i = 0; i < contacts.size(); i++){
				list.add(contacts.get(i).getContact());
			}

			 String [] status = new String [] {"Online", "Busy", "At the phone", "At Work", "Driving", "Offline"} ;
			 ListView listView = (ListView) chatlisttab.findViewById(R.id.chatlistlv);

			 listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list));

			 listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			 public void onItemClick(AdapterView<?> parent, View view,
									 int position, long id) {
				 // When clicked, show a toast with the TextView text
				 String state;
				 state = (String) parent.getItemAtPosition(position);

				 Toast.makeText(getActivity(),
							 "Clicked on: " + state, Toast.LENGTH_LONG)
							 .show();

			 }
		 });

	        ((TextView)chatlisttab.findViewById(R.id.textView)).setText("iOS");
	        return chatlisttab;
}}
