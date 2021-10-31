package com.example.maintenanceapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;
import models.*;

public class NewWorkOrderActivity extends AppCompatActivity
{
    Toast toast;
    Button submitBtn;
    EditText editTextDescription;
    EditText editTextTitle;
    EditText editTextLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newworkorder);

        editTextDescription = findViewById(R.id.textDescription);
        editTextTitle = findViewById(R.id.textTitle);
        editTextLocation = findViewById(R.id.textLocation);
        submitBtn = findViewById(R.id.btnSubmit);
        submitBtn.setOnClickListener(view -> {

            String description = editTextDescription.getText().toString();
            String location = editTextLocation.getText().toString();
            String title = editTextTitle.getText().toString();
            submitWorkOrder(description, location, title);

        });
    }

    public void submitWorkOrder(String description, String location, String title) {

        List<ParseObject> role = (ArrayList<ParseObject>) ParseUser.getCurrentUser().get("role");

        try{
            Tenant tenant = (Tenant) role.get(0).fetchIfNeeded();

            if(tenant.createWorkOrder(title, description, location, tenant, tenant.getLandlord()))
                toast = Toast.makeText(getBaseContext(), "Successful Submission", Toast.LENGTH_SHORT);
            else
                toast = Toast.makeText(getBaseContext(), "Failed Submission", Toast.LENGTH_SHORT);

            toast.show();
            finish();

        }catch (ParseException e){
            Log.e("Error" ,e.getMessage());
        }
    }


}
