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
import android.widget.Toast;

import com.example.maintenanceapp.NewWorkOrderActivity;
import com.example.maintenanceapp.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

import models.Tenant;

// TODO : Create buttons where users can see past workorders etc, render them via a scroll view

public class HomeFragment extends Fragment {

    Button newWorkOrderBtn;
    public HomeFragment() {/* Required empty public constructor*/}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Hides visibility of NewWorkOrder Button if the current user isn't a Tenant
        List<ParseObject> role = (List<ParseObject>) ParseUser.getCurrentUser().get("role");
        role.get(0).fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null)
                    newWorkOrderBtn.setVisibility(!(object instanceof Tenant) ? View.INVISIBLE : View.VISIBLE);
                else
                    Log.e("Error", e.getMessage());
            }
        });

        newWorkOrderBtn = view.findViewById(R.id.newWorkOrderBtn);
        newWorkOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Go to create new work order page
                Intent intent = new Intent(getContext(), NewWorkOrderActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}