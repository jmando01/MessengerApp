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

public class Ios extends Fragment {
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	 
	        View ios = inflater.inflate(R.layout.ios_frag, container, false);

			 String [] status = new String [] {"Online", "Busy", "At the phone", "At Work", "Driving", "Offline"} ;
			 ListView listView = (ListView) ios.findViewById(R.id.chatlistlv);

			 listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, status));

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

	        ((TextView)ios.findViewById(R.id.textView)).setText("iOS");
	        return ios;
}}
