package com.example.messenger.messenger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
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

	private ArrayList<Chat> temp;
	private Handler mHandler = new Handler();
	public static ChatListBaseAdapter adapter = null;
	public static ArrayList<Chat> chats;
	public static Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View chatListTab = inflater.inflate(R.layout.chat_list_frag, container, false);

		temp = new ArrayList<Chat>();
		chats = new ArrayList<Chat>();

		context = getActivity().getApplication();

		DatabaseHandler db = new DatabaseHandler(context);
		temp = (ArrayList<Chat>) db.getAllChats();
		db.close();

		for(int i = 0; i < temp.size(); i ++){
			if(temp.get(i).getUser().equals(LoginActivity.sharedPref.getString("username", "default"))){
				Log.d("ChatTabList", "Chat List: " + temp.get(i).getChat());
				chats.add(temp.get(i));
			}
		}

		final ListView lv1 = (ListView) chatListTab.findViewById(R.id.chatListLv);
		adapter = new ChatListBaseAdapter(getActivity(), chats);
		lv1.setAdapter(adapter);

		lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {

				Chat chat = chats.get(position);

			}
		});

		lv1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {

				Chat chat = chats.get(position);

				CharSequence[] items = {"Send a Message", "Delete Chat"};

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("Make your selection");
				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						if (item == 0) {
							//Aqui debe ir el codigo para enviar un mensaje al contacto.
						}
						if (item == 1) {
							//Aqui va el codigo para eliminar un chat
						}
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
				return true; //Esto sirve para que no se abran ambos metodos de ontouch
			}
		});

		return chatListTab;
	}
}
