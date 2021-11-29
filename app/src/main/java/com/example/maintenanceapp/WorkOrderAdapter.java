package com.example.maintenanceapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.maintenanceapp.Fragments.HomeFragment;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import models.*;

public class WorkOrderAdapter extends RecyclerView.Adapter<WorkOrderAdapter.ViewHolder> {

    public Context context;
    public ParseObject role;
    public HomeFragment fragment;
    private List<WorkOrder> workorders;

    public WorkOrderAdapter(Context context, List<WorkOrder> workorders, ParseObject role, HomeFragment fragment) {
        this.context = context;
        this.workorders = workorders;
        this.role = role;
        this.fragment = fragment;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_workorder, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(WorkOrderAdapter.ViewHolder holder, int position) {
        WorkOrder workOrder = workorders.get(position);
        holder.bind(workOrder, this.role, context, fragment);
    }

    @Override
    public int getItemCount() {
        return workorders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private ImageView ivPicture;
        private Button btnDeleteWorkOrder;
        private Button btnMoreInfo;
        private Button btnResolveWorkOrder;
        private TextView tvCompletionDate;
        private TextView tvQuote;
        private TextView tvQuoteLabel;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivPicture = itemView.findViewById(R.id.ivWorkOrderImage);
            btnDeleteWorkOrder = itemView.findViewById(R.id.btnDeleteWorkOrder);
            btnMoreInfo = itemView.findViewById(R.id.btnMoreInfo);
            btnResolveWorkOrder = itemView.findViewById(R.id.btnResolveWorkOrder);
            tvCompletionDate = itemView.findViewById(R.id.activityWorkOrder_tvCompletionDate);
            tvQuote = itemView.findViewById(R.id.tvQuote);
            tvQuoteLabel = itemView.findViewById(R.id.tvQuoteLabel);
        }

        public void bind(WorkOrder workOrder, ParseObject role, Context context, HomeFragment fragment) {
            tvTitle.setText(workOrder.getTitle());

            int radius = 35; // corner radius, higher value = more rounded
            int margin = 0; // crop margin, set to 0 for corners with no crop

            ParseFile image = workOrder.getAttachment();
            if (image != null)
                Glide.with(context).load(image.getUrl()).transform(new CenterCrop(), new RoundedCornersTransformation(radius, margin))
                        .into(ivPicture);

            if(role instanceof Landlord)
                btnDeleteWorkOrder.setVisibility(View.VISIBLE);
            else if(role instanceof Handyman && workOrder.getFinalQuote() != null && workOrder.getStatus() == false)
                btnResolveWorkOrder.setVisibility(View.VISIBLE);
            else
            {
                btnDeleteWorkOrder.setVisibility(View.INVISIBLE);
                btnResolveWorkOrder.setVisibility(View.INVISIBLE);
            }

            if(workOrder.getFinalQuote() != null)
            {
                workOrder.getFinalQuote().fetchInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject finalQuote, ParseException e) {
                        tvCompletionDate.setText(((Quote) finalQuote).getDate());
                        if(!(role instanceof Tenant))
                        {
                            tvQuote.setVisibility(View.VISIBLE);
                            tvQuoteLabel.setVisibility(View.VISIBLE);
                            tvQuote.setText("$"+((Quote) finalQuote).getAmount());
                        }
                    }
                });
            }
            else
            {
                if(role instanceof Tenant)
                {
                    tvQuote.setVisibility(View.INVISIBLE);
                    tvQuoteLabel.setVisibility(View.INVISIBLE);
                }
                tvQuote.setText("TBD");
                tvCompletionDate.setText("TBD");
            }

            btnDeleteWorkOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Are you sure you want to delete this work order?");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            for(Quote q : workOrder.getQuotes())
                                q.deleteInBackground();

                            workOrder.deleteInBackground(new DeleteCallback(){
                                @Override
                                public void done(ParseException e) {
                                    int btnId = fragment.radioGroup.getCheckedRadioButtonId();
                                    fragment.filterWorkorders(btnId);
                                }
                            });
                        }
                    });
                    alert.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {}
                            });
                    alert.show();
                }
            });
            btnMoreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ShowWorkOrderActivity.class);
                    intent.putExtra("workOrder", workOrder);
                    intent.putExtra("role", role);
                    context.startActivity(intent);
                }
            });
            btnResolveWorkOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    workOrder.setStatus(true);
                    workOrder.saveInBackground();
                    notifyDataSetChanged();
                }
            });
        }
    }
}
