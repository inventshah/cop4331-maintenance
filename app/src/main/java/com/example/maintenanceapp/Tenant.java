package com.example.maintenanceapp;

import com.parse.ParseUser;

// TODO: finish this class
public class Tenant extends ParseUser {

    public Tenant(String username, String password){
        super.setUsername(username);
        super.setPassword(password);
    }
}
