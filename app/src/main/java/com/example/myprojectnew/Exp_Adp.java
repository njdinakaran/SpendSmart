package com.example.myprojectnew;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Exp_Adp extends ArrayAdapter<ItemList> {

    public Exp_Adp(@NonNull Context context, ArrayList<ItemList> itemList) {
        super(context, 0, itemList);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_dropdown,parent,false);
        }
        ItemList item= getItem(position);
        ImageView dropimg=convertView.findViewById(R.id.imageDropdown);
        TextView droptext=convertView.findViewById(R.id.textDropdown);

        if (item != null) {
            dropimg.setImageResource(item.getItemImage());
            droptext.setText(item.getItemName());
        }
        return convertView;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_spinner_layout,parent,false);
        }
        ItemList item= getItem(position);
        ImageView spinnerimg=convertView.findViewById(R.id.imagespinner);
        TextView spinnertext=convertView.findViewById(R.id.textspinner);

        if (item != null) {
            spinnerimg.setImageResource(item.getItemImage());
            spinnertext.setText(item.getItemName());
        }
        return convertView;

    }
}
