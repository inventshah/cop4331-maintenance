package com.example.maintenanceapp;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import models.WorkOrder;

// TODO:
//  switch(genericbtn)
//      case landlord then 'view quotes'
//      case handyman then 'give quote'

public class DetailedWorkOrderAdapter extends RecyclerView.Adapter<DetailedWorkOrderAdapter.ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvTitle;
        private ImageView ivImage;
        private TextView tvStatus;
        private Button btnGeneric;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.detailedWO_tvTitle);
            ivImage = itemView.findViewById(R.id.detailedWO_ivImage);
            tvStatus = itemView.findViewById(R.id.detailedWO_tvStatus);
            btnGeneric = itemView.findViewById(R.id.detailedWO_btnGeneric);
            //tvTitle = itemView.findViewById(R.id.detailedWO_tvTitle);
        }
        public void bind(WorkOrder workOrder){




        }

    }
}
