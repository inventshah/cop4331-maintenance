package com.example.maintenanceapp;
import android.app.Application;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import models.*;

public class ParseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Register our classes
        ParseObject.registerSubclass(WorkOrder.class);
        ParseObject.registerSubclass(Landlord.class);
        ParseObject.registerSubclass(Handyman.class);
        ParseObject.registerSubclass(Tenant.class);
        ParseObject.registerSubclass(Quote.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(Conversation.class);

        // This sets up our Parse DB on the cloud,
        // Replace appId and clientKey with your own back4app account,
        // we'll try to set up locally if possible
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("h2wW27U8lT9pggJdbkAT8QIvij62mwfZZtzUnz0t")// replace me
                .clientKey("7vLh6X6nPbjPIaTfuFrwibIbAYqwu1JbC4p3CFgA")// replace me
                .server("https://parseapi.back4app.com")// keep me
                .build()
        );
    }
}