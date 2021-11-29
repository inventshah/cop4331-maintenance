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
import android.widget.Toast;

import com.example.maintenanceapp.ConversationMenuActivity;
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

public class HomeFragment extends Fragment {

    Button newWorkOrderBtn;
    ImageView ivMessage;
    private RecyclerView rvWorkOrders;
    public RadioGroup radioGroup;
    RadioButton radioPending, radioApproved, radioResolved, radioToDo;
    private WorkOrderAdapter adapter;
    private LinkedList<WorkOrder> allWorkOrders;
    public SwipeRefreshLayout swipeContainer;
    ParseObject role;

    public HomeFragment() {/* Required empty public constructor*/}

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
        ivMessage = view.findViewById(R.id.ivMessage);

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
        ivMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ConversationMenuActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        role = (ParseObject) this.getActivity().getIntent().getExtras().get("role");

        // Inflate the layout for this fragment
        if (role instanceof Tenant)
            return inflater.inflate(R.layout.fragment_home_tenant, container, false);
        else if (role instanceof Landlord)
            return inflater.inflate(R.layout.fragment_home_landlord, container, false);
        else
            return inflater.inflate(R.layout.fragment_home_handyman, container, false);
    }

    public void filterWorkorders(int buttonId) {
        ParseQuery<WorkOrder> query = new ParseQuery<WorkOrder>(WorkOrder.class);

        if (role instanceof Handyman) {

            // Get all workorders that are not completed under handyman's landlord(s) properties
            if (buttonId == radioPending.getId())
            {
                Toast.makeText(getContext(), "Pending WorkOrders", Toast.LENGTH_SHORT).show();
                query.whereEqualTo("status", false);
                query.whereDoesNotExist("handyman");
                query.whereContainedIn("landlord", ((Handyman) role).getLandlords());
            }

            // Get all workorders that assigned to be completed by current handyman
            else if (buttonId == radioToDo.getId())
            {
                Toast.makeText(getContext(), "ToDo WorkOrders", Toast.LENGTH_SHORT).show();
                query.whereEqualTo("status", false);
                query.whereEqualTo("handyman", role);
            }

            // Get all completed workorders, completed by the current handyman
            else {
                Toast.makeText(getContext(), "Resolved WorkOrders", Toast.LENGTH_SHORT).show();
                query.whereEqualTo("status", true);
                query.whereEqualTo("handyman", role);
            }

        }
        // Get all workorders associated with each landlord or tenant respectively
        else
        {
            query.whereEqualTo(((role instanceof Tenant) ? "tenant" : "landlord"), role);
            if (buttonId == radioPending.getId())
            {
                Toast.makeText(getContext(), "Pending WorkOrders", Toast.LENGTH_SHORT).show();
                query.whereDoesNotExist("handyman");
            }

            else if (buttonId == radioApproved.getId())
            {
                Toast.makeText(getContext(), "Approved WorkOrders", Toast.LENGTH_SHORT).show();
                query.whereExists("handyman");
                query.whereEqualTo("status", false);
            }
            else
            {
                Toast.makeText(getContext(), "Resolved WorkOrders", Toast.LENGTH_SHORT).show();
                query.whereEqualTo("status",true);
            }

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