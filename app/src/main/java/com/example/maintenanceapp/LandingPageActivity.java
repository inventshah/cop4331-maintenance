package com.example.maintenanceapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

public class LandingPageActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstance) {

        super.onCreate(savedInstance);
        setContentView(R.layout.activity_landingpage);

        if(ParseUser.getCurrentUser() != null)
            goMainActivity();
        else
            goLoginActivity();
    }

    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        List<ParseObject> res = (List<ParseObject>) ParseUser.getCurrentUser().get("role");
        res.get(0).fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                intent.putExtra("role", res.get(0));
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

    }

    private void goLoginActivity()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}
