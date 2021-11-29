package com.example.maintenanceapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import org.mindrot.jbcrypt.BCrypt;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Conversation;
import models.Handyman;
import models.Landlord;
import models.Tenant;

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
                if (name.length() > 0 && username.length() > 0 && password.length() > 0 &&
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
                            if (e == null)
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
                    Log.e("Error", e != null ? e.getMessage() : "LandlordKey does not match");
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
            landlord.setLandlordKey(makeKey());
            landlord.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null)
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
            handyman.setLandlords(new ArrayList<>());
            handyman.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null)
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
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private String makeKey() {
        Date date = new Date();
        String hash = BCrypt.hashpw(String.valueOf(date.getTime()), BCrypt.gensalt());
        return hash;
    }

    public Bitmap convertToBitmap(Drawable drawable) {
        try {
            Bitmap bitmap;
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            // Handle the error
            return null;
        }
    }

    public void finishSignup(ArrayList<ParseObject> role, String name, String username, String password){

        // Finish creating the ParseUser, because all required information is valid
        role.get(0).put("points", 0);
        ParseUser user = new ParseUser();

//        // convert drawable file to byte bitmap then byte array
//        ParseFile file;
//        Drawable d = getResources().getDrawable(R.drawable.ic_baseline_person_24);
//        Bitmap bitmap = convertToBitmap(d);
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//        byte[] bitmapdata = stream.toByteArray();
//
//        // Create the ParseFile
//        file = new ParseFile("profilePicture", bitmapdata);
//        file.saveInBackground();
//        user.put("profilePic",file);


        ParseQuery<ParseUser> query = new ParseQuery<ParseUser>(ParseUser.class);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) return;

                // check for duplicate usernames
                for (ParseUser user: users)
                {
                    if (user.getString("username").equals(username))
                    {
                        Toast.makeText(getApplicationContext(), "Username is taken",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                user.setUsername(username);
                user.setPassword(password);
                user.put("name", name);
                user.put("role", role);
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null)
                        {
                            // Put a reference to a ParseUser Object
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
        });
    }
}
