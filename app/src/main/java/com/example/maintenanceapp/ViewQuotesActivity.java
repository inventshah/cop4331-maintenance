package com.example.maintenanceapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import models.WorkOrder;

public class ViewQuotesActivity extends AppCompatActivity {

    WorkOrder workOrder;
    ImageButton btndownArrow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewquotes);
        workOrder = (WorkOrder) this.getIntent().getExtras().get("workOrder");
        btndownArrow = findViewById(R.id.viewquotes_downarrow);
        btndownArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.idle,R.anim.to_bottom);
    }
}
