package com.example.maintenanceapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.parse.ParseFile;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import models.WorkOrder;

public class WorkOrderAdapter extends RecyclerView.Adapter<WorkOrderAdapter.ViewHolder> {

    Context context;
    private List<WorkOrder> workorders;

    public WorkOrderAdapter(Context context, List<WorkOrder> workorders) {
        this.context = context;
        this.workorders = workorders;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_workorder, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(WorkOrderAdapter.ViewHolder holder, int position) {
        WorkOrder workOrder = workorders.get(position);
        holder.bind(workOrder);
    }

    @Override
    public int getItemCount() {
        return workorders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private ImageView ivPicture;
        private TextView tvStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivPicture = itemView.findViewById(R.id.ivWorkOrderImage);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

        public void bind(WorkOrder workOrder) {
            tvTitle.setText(workOrder.getTitle());
            tvStatus.setText(workOrder.getStatus() ? "true" : "false");
            int radius = 35; // corner radius, higher value = more rounded
            int margin = 0; // crop margin, set to 0 for corners with no crop
            ParseFile image = workOrder.getAttachment();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).transform(new CenterCrop(), new RoundedCornersTransformation(radius, margin))
                        .into(ivPicture);
            }
        }
    }
}
