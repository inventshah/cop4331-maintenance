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

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;


public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    Button loginBtn;
    Button signupBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    public void login(String username, String password)
    {
        ParseUser.logInInBackground(username, password, (user, e) -> {
            if (user != null)
            {
                Toast toast = Toast.makeText(getApplicationContext(), "Welcome Back " + user.get("name"),
                        Toast.LENGTH_SHORT);
                toast.show();
                goMainActivity();
            }
            else
            {
                Toast toast = Toast.makeText(getApplicationContext(), "Incorrect Username or Password",
                        Toast.LENGTH_SHORT);
                toast.show();
                Log.e("Error", e.getMessage());
            }
        });
        editTextUsername.setText("");
        editTextPassword.setText("");
    }

    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        List<ParseObject> res = (List<ParseObject>) ParseUser.getCurrentUser().get("role");
        res.get(0).fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                intent.putExtra("role", res.get(0));
                startActivity(intent);
                finish();
            }
        });
    }
}
