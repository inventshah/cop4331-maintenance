package com.example.maintenanceapp;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.parse.ParseFile;
import com.parse.ParseObject;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import models.*;

public class ShowWorkOrderActivity extends AppCompatActivity {

    ParseObject role;
    WorkOrder workOrder;
    private TextView tvTitle;
    private ImageView ivImage;
    private TextView tvStatus;
    private TextView tvDescription;
    private Button btnGeneric;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detailedworkorder);
        role = (ParseObject) getIntent().getExtras().get("role");
        workOrder = (WorkOrder) getIntent().getExtras().get("workOrder");
        tvTitle = findViewById(R.id.detailedWO_tvTitle);
        ivImage= findViewById(R.id.detailedWO_ivImage);
        tvStatus = findViewById(R.id.detailedWO_tvStatus);
        btnGeneric = findViewById(R.id.detailedWO_btnGeneric);
        tvDescription = findViewById(R.id.detailedWO_tvDescription);

        tvTitle.setText(workOrder.getTitle());
        tvStatus.setText(workOrder.getStatus() ? "true" : "false");
        tvDescription.setText(workOrder.getDescription());
        ParseFile image = workOrder.getAttachment();
        if (image != null)
            Glide.with(getBaseContext()).load(image.getUrl()).transform(new CenterCrop(), new RoundedCornersTransformation(35, 0))
                    .into(ivImage);

        if(!(role instanceof Tenant)) btnGeneric.setVisibility(View.VISIBLE);

        btnGeneric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(role instanceof Landlord)
                {
                    // view all quotes
                    Log.i("Quote", "getting/viewing all quotes");
                }
                else if(role instanceof Handyman)
                {
                    // give a quote
                    Log.i("Quote", "giving a quote");
                }

            }
        });

    }
}
