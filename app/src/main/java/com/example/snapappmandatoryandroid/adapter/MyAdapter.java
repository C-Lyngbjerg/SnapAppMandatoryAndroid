package com.example.snapappmandatoryandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.snapappmandatoryandroid.R;
import com.example.snapappmandatoryandroid.model.Snap;

import java.util.List;

public class MyAdapter extends BaseAdapter {
    private List<Snap> snaps;
    private LayoutInflater layoutInflater; // Can 'inflate' layout files

    public MyAdapter(List<Snap> snaps, Context context) {
        this.snaps = snaps;
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return snaps.size();
    }

    @Override
    public Object getItem(int position) {
        return snaps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        if(view ==null){
            view = layoutInflater.inflate(R.layout.myrow, null);
        }
        // make layout .xml file first
        TextView textView = view.findViewById(R.id.textView1);
        if(textView != null) {
            textView.setText(snaps.get(i).getText()); // Later i will connect to the items list
        }

        return textView; //linearLayout;
    }
}
