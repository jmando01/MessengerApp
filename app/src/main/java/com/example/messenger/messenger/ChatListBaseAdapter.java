package com.example.messenger.messenger;

/**
 * Created by Joubert on 19/05/2015.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatListBaseAdapter  extends BaseAdapter {

    private static ArrayList<ChatList> chatArrayList;

    private Integer[] imgid = {
            R.drawable.ic_action_person,
            //R.drawable.ic_priva,
    };

    private LayoutInflater l_Inflater;

    public ChatListBaseAdapter(Context context, ArrayList<ChatList> results) {
        chatArrayList = results;
        l_Inflater = LayoutInflater.from(context);
    }

    public void removeChat(ChatList chat){
        chatArrayList.remove(chat);
        notifyDataSetChanged();
    }

    public void addChat(ChatList chat) {
        chatArrayList.add(chat);
        notifyDataSetChanged();
    }

    public int getCount() {
        return chatArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = l_Inflater.inflate(R.layout.chat_view, null);
            holder = new ViewHolder();
            holder.txt_chat = (TextView) convertView.findViewById(R.id.chat);
            holder.txt_body = (TextView) convertView.findViewById(R.id.body);
            holder.txt_sentDate = (TextView) convertView.findViewById(R.id.sentDate);
            holder.txt_counter = (TextView) convertView.findViewById(R.id.counter);
            holder.image = (ImageView) convertView.findViewById(R.id.image);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txt_chat.setText((chatArrayList.get(position).getChat()));
        holder.txt_body.setText((chatArrayList.get(position).getBody()));
        holder.txt_sentDate.setText((chatArrayList.get(position).getSentDate()));
        if(String.valueOf(chatArrayList.get(position).getCounter()).equals("0")){
            holder.txt_counter.setText(" ");
        }else{
            holder.txt_counter.setText((String.valueOf(chatArrayList.get(position).getCounter())));
        }
        holder.image.setImageResource(imgid[0]);

        return convertView;
    }

    static class ViewHolder {
        TextView txt_chat;
        TextView txt_body;
        TextView txt_sentDate;
        TextView txt_counter;
        ImageView image;
    }
}

