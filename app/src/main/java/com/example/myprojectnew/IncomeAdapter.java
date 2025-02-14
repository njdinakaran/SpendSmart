package com.example.myprojectnew;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.ViewHolderexp> {


    Context context;
    ArrayList<Income_recycle> list;

    public IncomeAdapter(Context context,ArrayList<Income_recycle> list){
        this.context=context;
        this.list=list;
    }


    @NonNull
    @Override
    public ViewHolderexp onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.income_recycler_data,parent,false);
        return  new ViewHolderexp(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderexp holder, int position) {

        Income_recycle data = list.get(position);
        holder.amount.setText(String.valueOf(data.getAmount()));
        holder.note.setText(String.valueOf(data.getNote()));
        holder.date.setText(String.valueOf(data.getDate()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolderexp extends RecyclerView.ViewHolder {
        TextView amount, date, note;

        public ViewHolderexp(@NonNull View itemView) {
            super(itemView);
            amount=itemView.findViewById(R.id.amount_data);
            date=itemView.findViewById(R.id.date_data);
            note=itemView.findViewById(R.id.note_data);

        }
    }

}
