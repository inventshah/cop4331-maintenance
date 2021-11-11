package com.example.maintenanceapp.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.maintenanceapp.NewWorkOrderActivity;
import com.example.maintenanceapp.R;
import com.example.maintenanceapp.WorkOrderAdapter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import models.*;
// TODO implement refresh page feature, and Handyman multiple landlord registration

public class HomeFragment extends Fragment {

    Button newWorkOrderBtn;
    private RecyclerView rvWorkOrders;
    private WorkOrderAdapter adapter;
    private LinkedList<WorkOrder> allWorkOrders;
    ParseObject role;

    public HomeFragment() {/* Required empty public constructor*/}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        role = (ParseObject) this.getActivity().getIntent().getExtras().get("role");
        rvWorkOrders = view.findViewById(R.id.rvWorkorders);
        allWorkOrders = new LinkedList<>();
        adapter = new WorkOrderAdapter(getContext(), allWorkOrders);
        rvWorkOrders.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false));
        rvWorkOrders.setAdapter(adapter);
        newWorkOrderBtn = view.findViewById(R.id.newWorkOrderBtn);
        newWorkOrderBtn.setVisibility(View.INVISIBLE);
        queryWorkOrders(view);
        newWorkOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go to create new work order page
                Intent intent = new Intent(getContext(), NewWorkOrderActivity.class);
                intent.putExtra("role", role);
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

    public void queryWorkOrders(View view) {
        // Hides visibility of NewWorkOrder Button if the current user isn't a Tenant

        if(role instanceof Tenant) {newWorkOrderBtn.setVisibility(View.VISIBLE);}

        ParseQuery<WorkOrder> query = ParseQuery.getQuery(WorkOrder.class);

        if (role instanceof Tenant)
            query.whereContains("tenant", role.getObjectId());

        else if(role instanceof Landlord)
            query.whereContains("landlord", role.getObjectId());

        else if(role instanceof Handyman)
        {
            query.whereEqualTo(WorkOrder.KEY_STATUS, false);
            query.whereContainedIn("landlord",  ((Handyman) role).getLandlords());
        }

        query.findInBackground(new FindCallback<WorkOrder>() {
            @Override
            public void done(List<WorkOrder> workOrders, ParseException e) {
                if (e != null) {
                    Log.e("Error", e.getMessage());
                    return;
                }

                allWorkOrders.clear();
                for (WorkOrder wo: workOrders)
                    allWorkOrders.push(wo);
                adapter.notifyDataSetChanged();
            }
        });

    }
}