package com.example.maintenanceapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.material.snackbar.Snackbar;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import models.Quote;
import models.WorkOrder;

public class ViewQuotesActivity extends AppCompatActivity {

    WorkOrder workOrder;
    private RecyclerView rvQuotes;
    private QuoteAdapter adapter;
    private List<Quote> allQuotes;
    public SwipeRefreshLayout swipeContainer;
    ImageButton btndownArrow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewquotes);
        rvQuotes = findViewById(R.id.rvQuotes);
        allQuotes = new ArrayList<>();
        adapter = new QuoteAdapter(getApplicationContext(), allQuotes);
        rvQuotes.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        rvQuotes.setAdapter(adapter);
        workOrder = (WorkOrder) this.getIntent().getExtras().get("workOrder");
        btndownArrow = findViewById(R.id.viewquotes_downarrow);
        btndownArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getQuotes();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvQuotes);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            switch (direction) {
                case ItemTouchHelper.LEFT:
                    Quote quote = allQuotes.get(position);
                    allQuotes.remove(position);
                    adapter.notifyDataSetChanged();
                    quote.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                        }
                    });
                    workOrder.setQuotes(allQuotes);
                    workOrder.saveInBackground();
                    break;
                case ItemTouchHelper.RIGHT:
                    for (int i = 0; i < allQuotes.size(); i++) {
                        if (i != position)
                            allQuotes.remove(allQuotes.get(i));
                    }
                    onBackPressed();
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_outline_24)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green))
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_check_circle_outline_24)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.idle,R.anim.to_bottom);
    }

    public void getQuotes()
    {
        ParseQuery<WorkOrder> query = new ParseQuery<>(WorkOrder.class);
        query.whereEqualTo("objectId", workOrder.getObjectId());
        query.include("quotes");
        query.getFirstInBackground(new GetCallback<WorkOrder>() {
            @Override
            public void done(WorkOrder wo, ParseException e) {
                if (e != null)
                {
                    Log.e("Error", e.getMessage());
                    return;
                }
                allQuotes.clear();
                for (Quote q: wo.getQuotes()) {
                    allQuotes.add(q);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
