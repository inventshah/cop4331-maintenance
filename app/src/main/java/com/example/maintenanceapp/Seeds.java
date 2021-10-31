package com.example.maintenanceapp;
import android.os.Bundle;
import android.util.Log;
import models.*;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

// This is a seed activity, we can modify it to insert data so we have data to work with
public class Seeds extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        finish();
    }
    public void insert()
    {

    }
    public void query()
    {

        // Note: if we are going to access the User Class put "_User" as the getQuery() param
        ParseQuery<WorkOrder> query = ParseQuery.getQuery(WorkOrder.class);

        // Tells parse to populate the tenant field in the workorder objects we are getting back
        // since the field only holds a pointer, for fields that aren't pointers Parse already
        // has the value there
        // For things like arrays/lists of objects we do the same thing, etc
        query.include(WorkOrder.KEY_TENANT);

        query.findInBackground((objects, e) -> {
            if(e == null)
            {
                Log.i("Tenant", "list size " + objects.size());
                for(WorkOrder wo : objects)
                {
                    WorkOrder workOrder = wo;
                    if(wo.getTenant() == null)
                        continue;
                    Tenant t = wo.getTenant();
                    Log.i("Tenant", "got tenant in work order " + t.toString() + " " + t.getObjectId());
                }
            }
            else{
                Log.i("Error", e.getMessage());
            }
        });
    }

}
