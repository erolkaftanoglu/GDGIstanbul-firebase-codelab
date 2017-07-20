package com.gdgistanbul.firebasecodelab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gdgistanbul.firebasecodelab.models.Message;

import java.util.ArrayList;

/**
 * Created by burcuturkmen on 20/07/17.
 */

public class MessageAdapter extends BaseAdapter {


    private ArrayList<Message> messageList = new ArrayList<>();

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int i) {
        return messageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.item_message, null, false);
        TextView messageTextView = (TextView) view.findViewById(R.id.messageTextView);
        TextView usernameTextView = (TextView)view.findViewById(R.id.usernameTextView);
        messageTextView.setText(messageList.get(i).getData());
        usernameTextView.setText(messageList.get(i).getUserName());

        return view;
    }

    public boolean add(Message msg) {
        messageList.add(msg);
        notifyDataSetChanged();
        return true;
    }

    public boolean clearAllItems() {
        messageList.clear();
        notifyDataSetChanged();
        return true;
    }
}
