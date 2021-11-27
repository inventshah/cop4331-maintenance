package com.example.maintenanceapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import models.Handyman;
import models.Quote;
import models.WorkOrder;

public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.ViewHolder> {
    public Context context;
    private List<Quote> quotes;

    public QuoteAdapter(Context context, List<Quote> quotes) {
        this.context = context;
        this.quotes = quotes;
    }

    public QuoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.activity_quote, parent, false);
        return new QuoteAdapter.ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull QuoteAdapter.ViewHolder holder, int position) {
        Quote quote = quotes.get(position);
        try {
            holder.bind(quote, context);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return quotes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvPrice;
        private TextView tvHandymanName;
        private TextView tvDateOfCompletion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvHandymanName = itemView.findViewById(R.id.tvHMName);
            tvDateOfCompletion = itemView.findViewById(R.id.tvDateOfScheduledCompletion);
        }

        public void bind(Quote quote, Context context) throws ParseException {

            quote.getHandyman().fetchInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject handyman, ParseException e) {
                    if(e != null){
                        Log.e("Error", e.getMessage());
                        return;
                    }
                    ((Handyman) handyman).getUser().fetchInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject user, ParseException e) {
                            if(e != null){
                                Log.e("Error", e.getMessage());
                                return;
                            }
                            tvHandymanName.setText(user.getString("name"));
                        }
                    });
                }
            });
            tvPrice.setText(String.valueOf(quote.getAmount()));
            tvDateOfCompletion.setText(quote.getDate());
        }
    }
}
