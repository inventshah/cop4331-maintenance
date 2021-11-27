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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.maintenanceapp.Fragments.HomeFragment;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import models.Landlord;
import models.WorkOrder;

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
        private TextView tvStatus;
        private Button btnDeleteWorkOrder;
        private Button btnMoreInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivPicture = itemView.findViewById(R.id.ivWorkOrderImage);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnDeleteWorkOrder = itemView.findViewById(R.id.btnDeleteWorkOrder);
            btnMoreInfo = itemView.findViewById(R.id.btnMoreInfo);
        }

        public void bind(WorkOrder workOrder, ParseObject role, Context context, HomeFragment fragment) {
            tvTitle.setText(workOrder.getTitle());
            tvStatus.setText(workOrder.getStatus() ? "true" : "false");

            int radius = 35; // corner radius, higher value = more rounded
            int margin = 0; // crop margin, set to 0 for corners with no crop

            ParseFile image = workOrder.getAttachment();
            if (image != null)
                Glide.with(context).load(image.getUrl()).transform(new CenterCrop(), new RoundedCornersTransformation(radius, margin))
                        .into(ivPicture);


            List<ParseObject> res = (List<ParseObject>) ParseUser.getCurrentUser().get("role");
            res.get(0).fetchInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {

                    if(res.get(0) instanceof Landlord) {btnDeleteWorkOrder.setVisibility(View.VISIBLE);}

                    btnDeleteWorkOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            alert.setTitle("Are you sure you want to delete this work order?");

                            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
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

        }
    }
}
