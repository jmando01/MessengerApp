package com.example.messenger.messenger;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ContactListTab extends Fragment {
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	 
		    View contactlisttab = inflater.inflate(R.layout.contact_list_frag, container, false);
	        ((TextView)contactlisttab.findViewById(R.id.textView)).setText("Windows");
	        return contactlisttab;
}}
