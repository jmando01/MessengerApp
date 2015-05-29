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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ChatListTab extends Fragment {

	private Handler mHandler = new Handler();
	public static ChatListBaseAdapter adapter = null;
	public static ArrayList<Chat> chats;
	public static Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

				View chatListTab = inflater.inflate(R.layout.chat_list_frag, container, false);

		context = getActivity().getApplicationContext();

		refreshChatList();

		final ListView lv1 = (ListView) chatListTab.findViewById(R.id.chatListLv);
		adapter = new ChatListBaseAdapter(getActivity(), chats);
		lv1.setAdapter(adapter);

		lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {

				Chat chat = chats.get(position);
				Intent intent = new Intent(getActivity(), ChatActivity.class);
				intent.putExtra("contact",chat.getChat());
				startActivity(intent);

			}
		});

		lv1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {

				final Chat chat = chats.get(position);

				CharSequence[] items = {"Send a Message", "Delete Chat"};

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("Make your selection");
				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						if (item == 0) {
							Intent intent = new Intent(getActivity(), ChatActivity.class);
							intent.putExtra("contact",chat.getChat());
							startActivity(intent);
						}
						if (item == 1) {
							setRemoveChatFromChatList(chat.getChat());
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

	public static void setChatListChanged(String chat, String body, String date, String counter) {
		Log.d("ChatTabList", "Chat List Changed: " + chat);
		chats.add(0, new Chat(LoginActivity.sharedPref.getString("username", "default"), chat, body, date, counter));
		adapter.notifyDataSetChanged();
	}

	public static void setChatUpdate(String chat, String body, String date, String counter){

		Log.d("ChatTabList", "Udated Chat: " + chat + " body: " + body);
		for(int i = 0; i < chats.size(); i++){
			if(chat.equals(chats.get(i).getChat())){

				Chat updateChat = new Chat();

				updateChat.setUser(LoginActivity.sharedPref.getString("username", "default"));
				updateChat.setChat(chat);
				updateChat.setBody(body);
				updateChat.setSentDate(date);
				updateChat.setCounter(counter);

				chats.remove(i);
				chats.add(0, updateChat);

				DatabaseHandler db = new DatabaseHandler(context);
				db.updateChat(new Chat(db.getChatID(chat), LoginActivity.sharedPref.getString("username", "default"), chat, body, date, counter));
				db.close();
			}
		}
		adapter.notifyDataSetChanged();
	}

	public static void setRemoveChatFromChatList(String chat){

		for(int i = 0; i < chats.size(); i++){
			if(chat.equals(chats.get(i).getChat())){

				DatabaseHandler db = new DatabaseHandler(context);
				//para borrar un usuario solo es necesario el ID
				db.deleteChat(new Chat(db.getChatID(chat), "", "", "", "", ""));
				db.close();

				chats.remove(i);
				adapter.notifyDataSetChanged();
				Log.d("ChatListTab", "Chat: " + chat + " has been removed from chat list");
			}
		}
	}

	public static void reloadChatList(){
		DatabaseHandler db = new DatabaseHandler(context);
		List<Chat> reloadChats = db.getAllChats();
		for (Chat cn : reloadChats) {
			if(cn.getUser().equals(LoginActivity.sharedPref.getString("username", "default"))){
				db.deleteChat(new Chat(cn.getID(), "", "", "", "", ""));
			}
		}
		for (int i = 0; i<chats.size(); i++){
			db.addChat(new Chat(LoginActivity.sharedPref.getString("username", "default"), chats.get(i).getChat(), chats.get(i).getBody(), chats.get(i).getSentDate(), chats.get(i).getCounter()));
		}
		db.close();
	}

	public static void refreshChatList(){
		chats = new ArrayList<Chat>();

		DatabaseHandler db = new DatabaseHandler(context);
		chats = (ArrayList<Chat>) db.getAllChats();
		db.close();
	}
}
