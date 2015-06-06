package com.example.messenger.messenger;

/**
 * Created by Joubert on 05/06/2015.
 */
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ChatCommentBaseAdapter extends ArrayAdapter<ChatComment> {

    private TextView chatComment;
    private TextView sentDate;
    private List<ChatComment> chatComments = new ArrayList<ChatComment>();
    private RelativeLayout wrapper;

    @Override
    public void add(ChatComment object) {
        chatComments.add(object);
        super.add(object);
    }

    public void removeItem(ChatComment item){
        chatComments.remove(item);
        notifyDataSetChanged();
    }

    public ChatCommentBaseAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public int getCount() {
        return this.chatComments.size();
    }

    public ChatComment getItem(int index) {
        return this.chatComments.get(index);
    }
    public List<ChatComment> getItemList(){
        return chatComments;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.chat_comment_view, parent, false);
        }


        wrapper = (RelativeLayout) row.findViewById(R.id.wrapper);
        ChatComment comment = getItem(position);
        chatComment = (TextView) row.findViewById(R.id.comment);
        sentDate = (TextView) row.findViewById(R.id.sentDate);
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        chatComment.setMaxWidth(width - 200);
        chatComment.setText(comment.getComment());
        chatComment.setBackgroundResource(comment.getSide() ? R.drawable.bubble_grey_left : R.drawable.bubble_red_right);
        sentDate.setText(comment.getDate());
        wrapper.setGravity(comment.getSide() ? Gravity.LEFT : Gravity.RIGHT);

        return row;
    }
}
