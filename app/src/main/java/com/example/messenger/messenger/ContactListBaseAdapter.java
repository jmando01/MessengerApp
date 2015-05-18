package com.example.messenger.messenger;

/**
 * Created by Joubert on 17/05/2015.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Joubert on 03/05/2015.
 */
public class ContactListBaseAdapter  extends BaseAdapter {

    private static ArrayList<Contact> contactArrayList;

    private Integer[] imgid = {
            R.drawable.ic_action_person,
            //R.drawable.ic_priva,
    };

    private LayoutInflater l_Inflater;

    public ContactListBaseAdapter(Context context, ArrayList<Contact> results) {
        contactArrayList = results;
        l_Inflater = LayoutInflater.from(context);
    }

    public void removeContact(Contact contact){
        contactArrayList.remove(contact);
        notifyDataSetChanged();
    }

    public void addContact(Contact contact) {
        contactArrayList.add(contact);
        notifyDataSetChanged();
    }

    public int getCount() {
        return contactArrayList.size();
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
            convertView = l_Inflater.inflate(R.layout.contact_view, null);
            holder = new ViewHolder();
            holder.txt_contact = (TextView) convertView.findViewById(R.id.contact);
            holder.txt_status = (TextView) convertView.findViewById(R.id.status);
            holder.image = (ImageView) convertView.findViewById(R.id.image);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txt_contact.setText((contactArrayList.get(position).getContact()));
        holder.txt_status.setText((contactArrayList.get(position).getStatus()));
        holder.image.setImageResource(imgid[0]);

        return convertView;
    }

    static class ViewHolder {
        TextView txt_contact;
        TextView txt_status;
        ImageView image;
    }
}
