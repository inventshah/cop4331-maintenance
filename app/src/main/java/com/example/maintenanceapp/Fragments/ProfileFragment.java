package com.example.maintenanceapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.example.maintenanceapp.LoginActivity;
import com.example.maintenanceapp.R;
import com.example.maintenanceapp.RegisterLandlordActivity;
import com.parse.ParseObject;
import com.parse.ParseUser;

import models.Handyman;


public class ProfileFragment extends Fragment {

    ParseObject role;
    Button logout;
    Button registerLandlord;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        role = (ParseObject) this.getActivity().getIntent().getExtras().get("role");
        registerLandlord = view.findViewById(R.id.btnRegisterLandlord);
        registerLandlord.setVisibility(!(role instanceof Handyman) ? View.INVISIBLE: View.VISIBLE);
        logout = view.findViewById(R.id.btnLogout);

        registerLandlord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), RegisterLandlordActivity.class);
                intent.putExtra("role", role);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                if(ParseUser.getCurrentUser() != null)
                   Log.e("Logout Error", "logout err");

                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}