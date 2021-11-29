package com.example.maintenanceapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.maintenanceapp.Fragments.HomeFragment;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import models.*;

public class ShowWorkOrderActivity extends AppCompatActivity {

    ParseObject role;
    WorkOrder workOrder;
    private TextView tvTitle;
    private ImageView ivImage;
    private TextView tvTenant;
    private TextView tvDescription;
    private TextView tvLocation;
    private Button btnGeneric;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detailedworkorder);
        role = (ParseObject) getIntent().getExtras().get("role");
        workOrder = (WorkOrder) getIntent().getExtras().get("workOrder");
        tvTitle = findViewById(R.id.detailedWO_tvTitle);
        ivImage= findViewById(R.id.detailedWO_ivImage);
        tvTenant = findViewById(R.id.detailedWO_tvTenant);
        tvLocation = findViewById(R.id.detailedWO_tvLocation);
        btnGeneric = findViewById(R.id.detailedWO_btnGeneric);
        tvDescription = findViewById(R.id.detailedWO_tvDescription);
        tvTitle.setText(workOrder.getTitle());
        setTenantName();
        tvDescription.setText(workOrder.getDescription());
        tvLocation.setText(workOrder.getLocation());
        ParseFile image = workOrder.getAttachment();

        if (image != null)
            Glide.with(getBaseContext()).load(image.getUrl()).transform(new CenterCrop(), new RoundedCornersTransformation(35, 0))
                    .into(ivImage);

        // Set Tenant giveRating btn to visible
        if(role instanceof Tenant && workOrder.getHandyman() != null && workOrder.getStatus() && workOrder.getRating() != 0)
            btnGeneric.setVisibility(View.VISIBLE);

        // Set Handyman giveQuote btn to visible or Landlord viewQuotes btn to visible
        if((role instanceof Handyman || role instanceof Landlord) && !workOrder.getStatus() && workOrder.getFinalQuote() == null)
            btnGeneric.setVisibility(View.VISIBLE);

        btnGeneric.setText(role instanceof Tenant ? "Give Rating" : role instanceof Handyman ? "Give Quote" : "View Quotes");
        btnGeneric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Go to View Quotes Activity if role is Landlord
                if (role instanceof Landlord)
                {
                    Intent intent = new Intent(getBaseContext(), ViewQuotesActivity.class);
                    intent.putExtra("workOrder",workOrder);
                    startActivity(intent);
                    overridePendingTransition(R.anim.from_bottom, R.anim.idle);
                }

                // Setup dialog box according if role is either handyman or tenant
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShowWorkOrderActivity.this);
                    builder.setTitle(role instanceof Handyman ? "Specify Amount" : "Give Rating");
                    View viewInflated = null;

                    if (role instanceof Handyman)
                        viewInflated = LayoutInflater.from(getBaseContext()).inflate(R.layout.dialog_givequote, null);
                    else
                        viewInflated = LayoutInflater.from(getBaseContext()).inflate(R.layout.dialog_giverating, null);

                    EditText etAmount =  viewInflated.findViewById(R.id.dialogGiveQuote_etAmount);
                    EditText etScheduledCompletion =  viewInflated.findViewById(R.id.dialogGiveQuote_etScheduledCompletion);
                    EditText etRating = viewInflated.findViewById(R.id.dialogGiveRating_etRating);
                    RadioGroup radioGroup = viewInflated.findViewById(R.id.dialogGiveRating_radioGroup);

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
                           if (role instanceof Handyman)
                           {
                               String amount = etAmount.getText().toString();
                               String scheduledCompletion = etScheduledCompletion.getText().toString();

                               // Check amount and date formatting
                               if (amount.matches("[0-9]+\\.[0-9][0-9]") &&
                                       dateValidation(scheduledCompletion))
                               {
                                   // Add a quote to all quotes list on workorder object
                                   Quote q = new Quote();
                                   q.setAmount(Double.parseDouble(amount));
                                   q.setHandyman((Handyman) role);
                                   q.setDate(scheduledCompletion);
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
                                   MainActivity.bottomNavigationView.setSelectedItemId(R.id.action_home);
                                   finish();
                                   return;
                               }
                           }

                           // Set the rating field if user is a Tenant
                           else {
                               String temp = etRating.getText().toString();
                               if (temp.length() == 0)
                                   return;
                               int rating = Integer.parseInt(temp);

                               // Check if rating is between 1 and 5
                               if (rating <= 5 && rating >= 1)
                               {
                                   workOrder.setRating(rating);
                                   workOrder.saveInBackground();
                                   dialog.dismiss();

                                   // Give points to handyman based on tenant rating
                                   workOrder.getHandyman().fetchInBackground(new GetCallback<ParseObject>() {
                                       @Override
                                       public void done(ParseObject handyman, ParseException e) {
                                           double points = ((Handyman)handyman).getPoints();
                                           ((Handyman)handyman).setPoints(points + (rating > 1 ? rating * 0.2 : 0));
                                           handyman.saveInBackground();
                                       }
                                   });
                                   // Give points to Landlord depending on timeRating
                                   workOrder.getLandlord().fetchInBackground(new GetCallback<ParseObject>() {
                                       @Override
                                       public void done(ParseObject landlord, ParseException e) {
                                           double points = ((Landlord)landlord).getPoints();
                                           ((Landlord)landlord).setPoints(points+(radioGroup.getCheckedRadioButtonId() ==
                                                   R.id.dialogGiveRating_radioBtnYes ? 1:0));
                                           landlord.saveInBackground();
                                       }
                                   });

                                   MainActivity.bottomNavigationView.setSelectedItemId(R.id.action_home);
                                   finish();
                                   return;
                               }
                           }
                            Toast toast = new Toast(getApplicationContext());
                            toast.setText("Incorrect fields!");
                            toast.show();
                        }
                    });

                    // Cancel dialog box
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
    public boolean dateValidation(String date)
    {
        boolean status = false;
        if (date.matches("^([0-9]{2}/[0-9]{2}/[0-9]{4}){1}$")) {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            dateFormat.setLenient(false);
            try {
                dateFormat.parse(date);
                status = true;
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                status = false;
            }
        }
        return status;
    }

    public void setTenantName()
    {
        workOrder.getTenant().fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject tenant, ParseException e) {
                ((Tenant) tenant).getUser().fetchInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject user, ParseException e) {
                        tvTenant.setText(user.getString("name"));
                    }
                });
            }
        });
    }
}
