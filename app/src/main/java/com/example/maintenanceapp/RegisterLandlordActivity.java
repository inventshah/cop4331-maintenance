package com.example.maintenanceapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import models.Handyman;
import models.Landlord;
import models.WorkOrder;

public class RegisterLandlordActivity extends AppCompatActivity {

    ParseObject role;
    EditText etlandlordKey;
    Button btnAddLandlord;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerlandlord);

        role = (ParseObject) getIntent().getExtras().get("role");
        btnAddLandlord = findViewById(R.id.btnAddLandlord);
        etlandlordKey = findViewById(R.id.registerLandlord_etLandlordKey);

        btnAddLandlord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String landlordKey = etlandlordKey.getText().toString();
                if(landlordKey.length() > 0)
                {
                    List<ParseObject> landlords = ((Handyman) role).getLandlords();

                    ParseQuery<Landlord> query = ParseQuery.getQuery(Landlord.class);
                    query.whereContains("landlordKey", landlordKey);

                    query.getFirstInBackground(new GetCallback<Landlord>() {
                        @Override
                        public void done(Landlord object, ParseException e) {
                            if(e == null)
                            {
                                landlords.add(object);
                                ((Handyman) role).setLandlords(landlords);
                                role.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e == null)
                                        {
                                            etlandlordKey.setText("");
                                            MainActivity.bottomNavigationView.setSelectedItemId(R.id.action_home);
                                            goMainActivity();
                                            finish();
                                        }
                                        else
                                            Log.e("Error", e.getMessage());
                                    }
                                });
                            }
                            else
                                Log.e("Error", e.getMessage());
                        }
                    });
                }
            }
        });
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
