package com.example.maintenanceapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.maintenanceapp.Fragments.HomeFragment;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

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

        if((!(role instanceof Tenant)) || workOrder.getRating() == 0)
            btnGeneric.setVisibility(View.VISIBLE);

        btnGeneric.setText(role instanceof Tenant ? "Give Rating" : role instanceof Handyman ? "Give Quote" : "View Quotes");
        btnGeneric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(role instanceof Landlord)
                {
                    Intent intent = new Intent(getBaseContext(), ViewQuotesActivity.class);
                    intent.putExtra("workOrder",workOrder);
                    startActivity(intent);
                    overridePendingTransition(R.anim.from_bottom, R.anim.idle);
                }
                else if(role instanceof Handyman || role instanceof Tenant)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShowWorkOrderActivity.this);
                    builder.setTitle(role instanceof Handyman ? "Specify Amount" : "Give Rating");

                    View viewInflated = LayoutInflater.from(getBaseContext()).inflate(R.layout.dialog_givequote, null);
                    EditText input = (EditText) viewInflated.findViewById(R.id.dialogGiveQuote_amount);
                    builder.setView(viewInflated);

                    // default button methods
                    builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                    // Add logic to dialog buttons
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                           if(role instanceof Handyman)
                           {
                               String s = input.getText().toString();
                               if(s.matches("[0-9]+\\.[0-9][0-9]"))
                               {
                                   // add a quote to all quotes list on workorder object
                                   Quote q = new Quote();
                                   q.setAmount(Double.parseDouble(s));
                                   q.setHandyman((Handyman) role);
                                   q.saveInBackground(new SaveCallback() {
                                       @Override
                                       public void done(ParseException e) {
                                           List<Quote> quotes = workOrder.getQuotes();
                                           quotes.add(q);
                                           workOrder.setQuotes(quotes);
                                           workOrder.saveInBackground();
                                       }
                                   });
                                   dialog.dismiss();
                               }
                           }
                           else
                           {
                               String s = input.getText().toString();
                               int rating = Integer.parseInt(s);
                               if(rating <= 5 && rating >= 1)
                               {
                                   workOrder.setRating(rating);
                                   workOrder.saveInBackground();
                                   dialog.dismiss();

                               }
                           }
                            MainActivity.bottomNavigationView.setSelectedItemId(R.id.action_home);
                            finish();

                        }
                    });

                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.cancel();
                        }
                    });
                }
            }
        });

    }
}
