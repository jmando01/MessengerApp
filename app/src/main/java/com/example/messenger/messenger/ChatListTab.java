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

		return chatlisttab;
	}
}
