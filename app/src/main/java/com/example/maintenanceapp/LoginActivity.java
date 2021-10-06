package com.example.maintenanceapp;
import models.*;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    Button loginBtn;
    Button signupBtn;
    Button logoutBtn; // this button is only temporary its only for testing, get rid of it later

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // this is temporary, bc we wont have a logout btn on the login page
        logoutBtn = findViewById(R.id.LogOutBtn);
        logoutBtn.setOnClickListener(view -> {
            logout();
        });

        // If user is already logged in
        if(ParseUser.getCurrentUser() != null)
        {
            Log.i("Current User", "Already logged in");
            // go somewhere else
        }

//        Seeds the database to have data to work with, and return to LoginActivity//
        Intent intent = new Intent(this.getBaseContext(), Seeds.class);
        startActivity(intent);

        // Extract info from the UI
        editTextUsername = findViewById(R.id.Username);
        editTextPassword = findViewById(R.id.Password);
        loginBtn = findViewById(R.id.LoginBtn);
        signupBtn = findViewById(R.id.SignUpBtn);

        // Set event listeners
        loginBtn.setOnClickListener(view -> {
            Log.i("Login","clicked login btn");
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            if(username.length() > 0 && password.length() > 0)
                login(username, password);
        });
        signupBtn.setOnClickListener(view -> {
            Log.i("Login","clicked signup btn");
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            if(username.length() > 0 && password.length() > 0)
                signup(username, password);
        });
    }

    public void login(String username, String password)
    {

        if(ParseUser.getCurrentUser() != null)
        {
            Log.i("Login", "Already logged in, log out to login to other account");
            return;
        }

        // 'e' is our exception object, 'user' is the user object
        ParseUser.logInInBackground(username, password, (user, e) -> {

            // Login success
            if(user != null)
            {
                // make some UI Toast pop up animation on failure
                // go somewhere else

            }
            // Login failure
            else
            {
                Log.i("Login Error", "login error" + e.toString());
                // make some UI Toast pop up animation on success
            }
        });
        editTextUsername.setText("");
        editTextPassword.setText("");
    }

    // General Signup template
    public void signup(String username, String password)
    {
        if(ParseUser.getCurrentUser() != null)
        {
            Log.i("Login", "Already logged in, cannot sign up again!");
            return;
        }

        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(e -> {
            if (e == null) {

                Tenant t = new Tenant();
                t.setUser(user); // references the user object in the user table
                t.saveInBackground();

                // Set a field indicating what type of user, we use this field to indicate
                // whether the user is Tenant, Handyman, etc

                // As far telling what object the role is, we can just get the role object then do
                // instanceof Tenant or instanceof Landlord etc.
                user.put("role", t);
                user.saveInBackground();
                Log.i("SignUp success", "sign up success");

            } else {
                Log.i("SignUp err", "sign up err" + e.getMessage());
            }
        });
    }

    // Temporary function: Logs out current user
    public void logout()
    {
        ParseUser.logOut();
        if(ParseUser.getCurrentUser() == null)
            Log.i("Logout success", "logout success");
        else
            Log.i("Logout err", "logout err");
    }
}
