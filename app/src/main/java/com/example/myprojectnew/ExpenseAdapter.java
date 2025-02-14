package com.example.myprojectnew;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {


    Context context;
    ArrayList<Expense_recycle> list;

    public ExpenseAdapter(Context context,ArrayList<Expense_recycle> list){
        this.context=context;
        this.list=list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.expense_recycler_data,parent,false);
        return  new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Expense_recycle data = list.get(position);
        holder.amount.setText(String.valueOf(data.getAmount()));
        holder.category.setText(String.valueOf(data.getCategory()));
        holder.note.setText(String.valueOf(data.getNote()));
        holder.date.setText(String.valueOf(data.getDate()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView amount, date, note, category;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            amount=itemView.findViewById(R.id.amount_data);
            date=itemView.findViewById(R.id.date_data);
            category=itemView.findViewById(R.id.category_data);
            note=itemView.findViewById(R.id.note_data);

        }
    }

}
