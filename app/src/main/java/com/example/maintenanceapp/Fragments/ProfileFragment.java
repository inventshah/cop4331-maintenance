package com.example.maintenanceapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.maintenanceapp.LoginActivity;
import com.example.maintenanceapp.R;
import com.example.maintenanceapp.RegisterLandlordActivity;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import models.Handyman;
import models.Landlord;
import models.Tenant;


public class ProfileFragment extends Fragment {

    ParseObject role;
    private TextView tvFullName;
    private TextView tvUsername;
    private TextView tvEmail;
    private TextView tvPoints;
    private TextView tvLandlordKey;
    Spinner spProfileOptions;
    public static final String TAG = "ProfileFragment";
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        role = (ParseObject) this.getActivity().getIntent().getExtras().get("role");
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.profile_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        tvUsername = view.findViewById(R.id.tvProfileUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvFullName = view.findViewById(R.id.tvNameOfUser);
        tvPoints = view.findViewById(R.id.tvPoints);
        spProfileOptions = view.findViewById(R.id.spProfileOptions);
        tvLandlordKey = view.findViewById(R.id.tvLandlordKey);
        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        tvEmail.setText(ParseUser.getCurrentUser().getEmail());
        tvFullName.setText(ParseUser.getCurrentUser().getString("name"));
        tvPoints.setText("Points: "+(Math.round(role.getDouble("points")*100.0)/100.0));

        if (role instanceof Landlord)
        {
            tvLandlordKey.setVisibility(View.VISIBLE);
            tvLandlordKey.setText(((Landlord) role).getLandLordKey().toString());
        }

        // Create and set array adapters for each spinner
        ArrayAdapter<String> profileOptionsAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(role instanceof Handyman ? R.array.postOptionsHM : R.array.postOptions));

        spProfileOptions.setAdapter(profileOptionsAdapter);
        spProfileOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("Log out"))
                {
                    ParseUser.logOut();
                    if (ParseUser.getCurrentUser() != null)
                        Log.e("Logout Error", "logout err");

                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else if (selectedItem.equals("Register Landlord")) {
                    Intent intent = new Intent(getContext(), RegisterLandlordActivity.class);
                    intent.putExtra("role", role);
                    startActivity(intent);
                }
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}