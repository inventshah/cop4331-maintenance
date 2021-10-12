package com.example.maintenanceapp;
import android.app.Application;
import com.parse.Parse;
import com.parse.ParseObject;
import models.*;

public class ParseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Register our classes
        ParseObject.registerSubclass(Property.class);
        ParseObject.registerSubclass(WorkOrder.class);
        ParseObject.registerSubclass(Landlord.class);
        ParseObject.registerSubclass(Handyman.class);
        ParseObject.registerSubclass(Tenant.class);

        // This sets up our Parse DB on the cloud,
        // Replace appId and clientKey with your own back4app account,
        // we'll try to set up locally if possible
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("cvtDgSteXYSOSjlmbqDbC4Z2j8BrGXWWkQlQMafC\n")
                .clientKey("JqNQCSZXfurZcqhYZhMEcM9LGkSPtWpmOtEO2TzM")
                .server("https://parseapi.back4app.com")
                .build()
        );

//         This Tells parse to query from the B4aVehicle class
//         ParseQuery<ParseObject> query = ParseQuery.getQuery("B4aVehicle");
//
//         query.whereEqualTo("name", "Corolla");  // apply query restraint: get objects where name=Corolla
//         We can stack more constraints like this, and so on
//             query.whereGreaterThan("playerAge", 18);
//
//         findInBackground() should create its own thread, no need to create another thread,
//         we create threads for async tasks to avoid freezing up the mainUI thread
//
//         Each query method has its own parameter specified in docs, in this case the params
//         are a list of our objects that we get back 'list', and error/exception object 'e'
//        query.findInBackground((list, e) -> {
//            if (e == null) {
//                for(ParseObject o : list)
//                    Log.v("Object",""+ o.getObjectId() + " " + o.get("name"));
//                Log.v("Object", "Retrieved " + list.size() + " vehicles");
//
//            } else {
//                Log.v("Object", "Error: " + e.getMessage());
//            }
//        });

    }
}