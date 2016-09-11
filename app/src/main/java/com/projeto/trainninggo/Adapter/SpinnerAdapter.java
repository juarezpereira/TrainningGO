package com.projeto.trainninggo.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.projeto.domain.Model.Esporte;
import com.projeto.trainninggo.R;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter{

    private List<Esporte> mList;
    private LayoutInflater mLayoutInflater;

    public SpinnerAdapter(Context context, List<Esporte> objects) {
        super(context, R.layout.spinner_item_simple, objects);
        this.mList = objects;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomDropDownView(position, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent);
    }

    public View getCustomView(int position, ViewGroup parent){
        View rom = mLayoutInflater.inflate(R.layout.spinner_item_simple, parent, false);
        TextView textView = (TextView) rom;
        textView.setText(mList.get(position).getName());

        return rom;
    }

    public View getCustomDropDownView(int position, ViewGroup parent){
        View row = mLayoutInflater.inflate(android.R.layout.simple_spinner_dropdown_item,parent, false);

        TextView textView = (TextView)row;
        textView.setText(mList.get(position).getName());

        if(position % 2 == 1){
                textView.setBackgroundColor(Color.parseColor("#C8E6C9"));
        }else{
            if (position == 0){
                textView.setTextColor(Color.LTGRAY);
            }
            textView.setBackgroundColor(Color.WHITE);
        }

        return row;
    }

}
