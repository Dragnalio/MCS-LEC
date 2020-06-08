package com.example.myapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication.Activities.DetailActivity;
import com.example.myapplication.Objects.Transaction;
import com.example.myapplication.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder>{

    private Context mContext;
    private ArrayList<Transaction> transactions;

    public AccountAdapter(Context context, ArrayList<Transaction> transactions){
        mContext = context;
        this.transactions = transactions;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout layout;
        ImageView imageView;
        TextView tvName, tvBalance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.item_account_layout);
            tvName = itemView.findViewById(R.id.item_account_name);
            tvBalance = itemView.findViewById(R.id.item_account_balance);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tvName.setText(transactions.get(position).getName());
        holder.tvBalance.setText(transactions.get(position).getPrice());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                //passing data
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }
}