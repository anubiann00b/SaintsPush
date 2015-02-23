package com.saintsrobotics.saintspush;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MessageAdapter extends BaseAdapter {

    List<String> messages;

    Context context;
    SharedPreferences prefs;

    public MessageAdapter(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);

        List<String> cached = Arrays.asList(prefs.getString("messages", "\n").split("\n"));
        Collections.reverse(cached);

        messages = new LinkedList<>(cached);
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.message, null);
        }

        TextView clientIp = (TextView) view.findViewById(R.id.message_text);
        clientIp.setText(messages.get(position));

        return view;
    }

    public void add(String message) {
        messages.add(0, message);
        prefs.edit().putString("messages", prefs.getString("messages", "") + "\n" + message).commit();
        this.notifyDataSetChanged();
    }
}
