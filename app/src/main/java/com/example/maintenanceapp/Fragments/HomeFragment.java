package com.example.maintenanceapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.maintenanceapp.NewWorkOrderActivity;
import com.example.maintenanceapp.R;
import com.example.maintenanceapp.WorkOrderAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.LinkedList;
import java.util.List;

import models.*;
// TODO implement refresh page feature, and Handyman multiple landlord registration

public class HomeFragment extends Fragment {

    Button newWorkOrderBtn;
    ImageView ivPending, ivUnderWorks, ivResolved, ivToDo, ivComplete;
    private RecyclerView rvWorkOrders;
    RadioGroup radioGroup;
    RadioButton radioPending, radioApproved, radioResolved, radioToDo;
    private WorkOrderAdapter adapter;
    private LinkedList<WorkOrder> allWorkOrders;
    public SwipeRefreshLayout swipeContainer;
    ParseObject role;

    public HomeFragment() {/* Required empty public constructor*/}
    // TODO add newWObtn to toolbar radiogroup
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvWorkOrders = view.findViewById(R.id.rvWorkorders);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        allWorkOrders = new LinkedList<>();
        adapter = new WorkOrderAdapter(getContext(), allWorkOrders, role, HomeFragment.this);
        rvWorkOrders.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvWorkOrders.setAdapter(adapter);
        newWorkOrderBtn = view.findViewById(R.id.newWorkOrderBtn);
        radioGroup = view.findViewById(R.id.radioToolbar);
        radioPending = view.findViewById(R.id.rbPending);
        radioApproved = view.findViewById(R.id.rbApproved);
        radioResolved = view.findViewById(R.id.rbResolved);
        radioToDo = view.findViewById(R.id.rbToDo);

        filterWorkorders(radioGroup.getCheckedRadioButtonId());
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = radioGroup.getCheckedRadioButtonId();
                filterWorkorders(id);

            }
        });

        if (newWorkOrderBtn != null)
            newWorkOrderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Go to create new work order page
                    Intent intent = new Intent(getContext(), NewWorkOrderActivity.class);
                    intent.putExtra("role", role);
                    startActivity(intent);
                }
            });
//
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeContainer.setDistanceToTriggerSync(300);
        // Set refresh listener
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                filterWorkorders(radioGroup.getCheckedRadioButtonId());
                swipeContainer.setRefreshing(false);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        role = (ParseObject) this.getActivity().getIntent().getExtras().get("role");
        // Inflate the layout for this fragment

        if (role instanceof Tenant) {
            return inflater.inflate(R.layout.fragment_home_tenant, container, false);
        }
        else if (role instanceof Landlord) {
            return inflater.inflate(R.layout.fragment_home_landlord, container, false);
        }
        else {
            return inflater.inflate(R.layout.fragment_home_handyman, container, false);
        }

    }
    // This function is not necessary anymore, using for reference only, will delete later
    public void queryWorkOrders() {
        // Hides visibility of NewWorkOrder Button if the current user isn't a Tenant
        ParseQuery<WorkOrder> query = ParseQuery.getQuery(WorkOrder.class);

        if (role instanceof Tenant)
            query.whereContains("tenant", role.getObjectId());

        else if (role instanceof Landlord)
            query.whereContains("landlord", role.getObjectId());

        else if (role instanceof Handyman)
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

    public void filterWorkorders(int buttonId) {
        ParseQuery<WorkOrder> query = new ParseQuery<WorkOrder>(WorkOrder.class);
        if (role instanceof Handyman) {
            query.whereEqualTo("handyman", role);
            if (buttonId == radioToDo.getId())
                query.whereEqualTo("status", false);
            else query.whereEqualTo("status",true);
        }
        else {
            query.whereEqualTo(((role instanceof Tenant) ? "tenant" : "landlord"), role);
            if (buttonId == radioPending.getId())
                query.whereDoesNotExist("handyman");
            else if (buttonId == radioApproved.getId())
            {
                query.whereExists("handyman");
                query.whereEqualTo("status", false);
            }
            else query.whereEqualTo("status",true);
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