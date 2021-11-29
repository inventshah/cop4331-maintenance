package com.example.maintenanceapp;

import com.parse.Parse;
import com.parse.ParseObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ChatApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Use for monitoring Parse network traffic
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        // any network interceptors must be added with the Configuration Builder given this syntax
        builder.networkInterceptors().add(httpLoggingInterceptor);

        // Set applicationId and server based on the values in the Back4App settings.
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("h2wW27U8lT9pggJdbkAT8QIvij62mwfZZtzUnz0t")
                .clientKey("7vLh6X6nPbjPIaTfuFrwibIbAYqwu1JbC4p3CFgA")
                .clientBuilder(builder)
                .server("https://parseapi.back4app.com").build());
    }
}