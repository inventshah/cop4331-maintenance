package com.example.maintenanceapp;
import models.*;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.parse.ParseUser;


public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    Button loginBtn;
    Button signupBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // If user is already logged in
        if(ParseUser.getCurrentUser() != null)
        {
            Log.i("Current User", "Already logged in");
            goMainActivity();

        }

//        Seeds the database to have data to work with, and return to LoginActivity//
        //Intent intent = new Intent(this.getBaseContext(), Seeds.class);
        //startActivity(intent);

        // Extract info from the UI
        editTextUsername = findViewById(R.id.Username);
        editTextPassword = findViewById(R.id.Password);
        loginBtn = findViewById(R.id.LoginBtn);
        signupBtn = findViewById(R.id.SignUpBtn);

        // Set event listeners
        loginBtn.setOnClickListener(view -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            if(username.length() > 0 && password.length() > 0)
                login(username, password);
        });
        signupBtn.setOnClickListener(view -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            if(username.length() > 0 && password.length() > 0)
                signup(username, password);
        });
    }

    public void login(String username, String password)
    {
        ParseUser.logInInBackground(username, password, (user, e) -> {

            // Login success
            if(e == null)
            {
                Toast toast = Toast.makeText(getApplicationContext(), "Successfully Logged In",
                        Toast.LENGTH_SHORT);
                toast.show();
                goMainActivity();

            }
            // Login failure
            else
            {
                Toast toast = Toast.makeText(getApplicationContext(), "Incorrect Username or Password",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        editTextUsername.setText("");
        editTextPassword.setText("");
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    // General Signup template
    public void signup(String username, String password)
    {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(e -> {
            if (e == null) {

                Tenant tenant = new Tenant();
                tenant.setUser(user); // tenant object references its user object in the user table
                tenant.saveInBackground();

                user.put("role", tenant); // sets the role field to be a reference to a Tenant Object in the Tenant table
                user.saveInBackground();

                // Note: each user can only have one role, i.e only a single reference to a Tenant,
                // Landlord, or Handyman Object. And each of those role objects can only have one reference
                // to a User.
                Toast toast = Toast.makeText(getApplicationContext(), "Successfully Signed Up",
                        Toast.LENGTH_SHORT);
                toast.show();
                goMainActivity();

            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Failed Sign Up",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
