package com.example.maintenanceapp;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ParseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

       /* TODO: still need to look into if we need to setup parse again in another
         TODO:  activity if we want to query in that other activity */

        // This sets up our parse,
        // Replace appId and clientKey with your own back4app account,
        // we'll try to set up locally if possible
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("h2wW27U8lT9pggJdbkAT8QIvij62mwfZZtzUnz0t")// replace me
                .clientKey("7vLh6X6nPbjPIaTfuFrwibIbAYqwu1JbC4p3CFgA")// replace me
                .server("https://parseapi.back4app.com")// keep me
                .build()
        );

        // Tells parse to query from the B4aVehicle class
        ParseQuery<ParseObject> query = ParseQuery.getQuery("B4aVehicle");

        query.whereEqualTo("name", "Corolla");  // apply query restraint: get objects where name=Corolla
        // We can stack more constraints like this, and so on
            // query.whereGreaterThan("playerAge", 18);

        // findInBackground() should create its own thread, no need to create another thread,
        // we create threads for async tasks to avoid freezing up the mainUI thread, if we
        // do all our async stuff on mainUI it can freeze up our app/ui.

        // Each query method has its own parameter specified in docs, in this case the params
        // are a list of our objects that we get back 'list', and error/exception object 'e'
        query.findInBackground((list, e) -> {
            if (e == null) {
                for(ParseObject o : list)
                    Log.v("score",""+ o.getObjectId() + " " + o.get("name"));
                Log.v("score", "Retrieved " + list.size() + " vehicles");
            } else {
                Log.v("score", "Error: " + e.getMessage());
            }
        });

    }
}