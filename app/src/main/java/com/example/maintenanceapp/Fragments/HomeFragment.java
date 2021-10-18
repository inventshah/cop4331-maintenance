package com.example.maintenanceapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.maintenanceapp.R;


public class HomeFragment extends Fragment {

    Button newWorkOrderBtn;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        newWorkOrderBtn = view.findViewById(R.id.newWorkOrderBtn);
        newWorkOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getContext(), "Clicked create new woekorder btn",
                        Toast.LENGTH_SHORT);
                toast.show();
                // go to new work order page

            }
        });

        // render all work orders associated with the user on this page
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}