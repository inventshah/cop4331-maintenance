package com.example.maintenanceapp;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.parse.ParseUser;


// TODO make the layout page for signup/login

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

        // Else login in or sign up
        else
        {
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
    }

    public void login(String username, String password)
    {
        Log.i("Attempt login", "attempt login" + username + " " + password);
        // 'e' is our exception object, 'user' is the user object
        ParseUser.logInInBackground(username, password, (user, e) -> {

            // Login success
            if(user != null)
            {
                Log.i("Login Success", "login Success");
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
        Log.i("Attempt signup", "attempt signup" + username + " " + password);
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(e -> {
            if (e == null) {
                Log.i("SignUp success", "sign up success");
                // Hooray! Let them use the app now.
                // go somewhere else
            } else {
                Log.i("SignUp err", "sign up err" + e.getMessage());
                // Sign up didn't succeed. Look at the ParseException
                // to figure out what went wrong
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
