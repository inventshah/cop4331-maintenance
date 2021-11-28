package com.example.maintenanceapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import org.mindrot.jbcrypt.BCrypt;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import models.Handyman;
import models.Landlord;
import models.Tenant;

// TODO add some validators to check if new Users meet the requirements for a Tenant, Landlord, etc

public class SignUpActivity extends AppCompatActivity {

    Button signupBtn;
    EditText editTextUsername;
    EditText editTextPassword;
    EditText editTextLandlordKey;
    RadioGroup radioGroupRole;
    RadioButton radioBtn;
    EditText editTextName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupBtn = findViewById(R.id.btnSignup);
        editTextUsername = findViewById(R.id.textUsername);
        editTextPassword = findViewById(R.id.textPassword);
        editTextLandlordKey = findViewById(R.id.landlordKey);
        radioGroupRole = findViewById(R.id.radioRole);
        editTextName = findViewById(R.id.textName);
        radioBtn = findViewById(R.id.radioTenant);

        radioGroupRole.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selection = radioGroupRole.getCheckedRadioButtonId();
                editTextLandlordKey.setVisibility(selection == R.id.radioTenant ? View.VISIBLE : View.INVISIBLE);
            }
        });
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString();
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();
                String landlordKey = editTextLandlordKey.getText().toString();
                int id = radioGroupRole.getCheckedRadioButtonId();

                // Check for empty fields
                if(name.length() > 0 && username.length() > 0 && password.length() > 0 &&
                     (id != R.id.radioTenant || landlordKey.length() > 0)
                  )
                    signup(name, username, password, landlordKey, id);
                else
                {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Enter all fields",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    public void signup(String name, String username, String password, String landlordKey, int id)
    {
        ArrayList<ParseObject> role = new ArrayList<>();

        // If selection is tenant
        if (id == R.id.radioTenant)
        {
            // Create and initialize a Tenant  and then create a ParseUser to associate with Tenant
            ParseQuery<Landlord> query = ParseQuery.getQuery(Landlord.class);
            query.whereEqualTo("landlordKey", landlordKey);
            query.findInBackground((objects, e) -> {
                if (e == null && objects.size() > 0)
                {
                    Tenant tenant = new Tenant();
                    tenant.setLandlord(objects.get(0));
                    tenant.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null)
                            {
                                role.add(tenant);
                                finishSignup(role, name, username, password);
                            }
                            else
                                Log.e("Error", e.getMessage());
                        }
                    });
                }
                else
                {
                    Log.e("Error",e.getMessage() != null ? e.getMessage() : "LandlordKey does not match");
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Failed Signed Up",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            });

        }
        // If selection is landlord
        else if (id == R.id.radioLandlord)
        {
            // Create and initialize a Landlord and then create a ParseUser to associate with Landlord
            Landlord landlord = new Landlord();
            landlord.setWorkOrders(new ArrayList<>());
            landlord.setLandlordKey(makeKey());
            landlord.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null)
                    {
                        role.add(landlord);
                        finishSignup(role, name, username, password);
                    }
                    else
                        Log.e("Error", e.getMessage());
                }
            });
        }
        // If selection is handyman
        else
        {
            // Create and initialize a Handyman
            Handyman handyman = new Handyman();
            handyman.setResolvedWorkOrders(new ArrayList<>());
            handyman.setLandlords(new ArrayList<>());
            handyman.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null)
                    {
                        role.add(handyman);
                        finishSignup(role, name, username, password);
                    }
                    else
                        Log.e("Error", e.getMessage());
                }
            });
        }
    }

    private void goMainActivity(ParseObject role) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("role", role);
        startActivity(intent);
        finish();
    }

    private String makeKey() {
        Date date = new Date();
        String hash = BCrypt.hashpw(String.valueOf(date.getTime()), BCrypt.gensalt());
        return hash;
    }

    public void finishSignup(ArrayList<ParseObject> role, String name, String username, String password){

        // Finish creating the ParseUser, because all required information is valid
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.put("name", name);
        user.put("points", (double)0);
        user.put("role", role);

        // TODO: testing acl persmissions
        ParseACL acl = new ParseACL();
        acl.setPublicWriteAccess(true);
        acl.setPublicReadAccess(true);
        user.setACL(acl);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null)
                {
                    role.get(0).put("user", user);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Successfully Signed Up",
                                    Toast.LENGTH_SHORT);
                            toast.show();
                            goMainActivity(role.get(0));
                        }
                    });
                }
                else
                    Log.e("Error", e.getMessage());
            }
        });
    }

}
