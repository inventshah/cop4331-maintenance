package com.example.maintenanceapp;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import models.*;

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
        fetchQuotes();
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

                // Deleting Quotes
                case ItemTouchHelper.LEFT:
                    Quote quote = allQuotes.get(position);
                    allQuotes.remove(position);
                    adapter.notifyDataSetChanged();
                    quote.deleteInBackground();
                    workOrder.setQuotes(allQuotes);
                    workOrder.saveInBackground();
                    break;

                // Approving Quote
                case ItemTouchHelper.RIGHT:

                    // Remove all other quotes, keep only the approved quote
                    Quote approvedQuote = allQuotes.get(position);
                    for(Quote q : allQuotes)
                        if(!q.equals(approvedQuote))
                            q.deleteInBackground();
                    allQuotes.clear();
                    allQuotes.add(approvedQuote);
                    adapter.notifyDataSetChanged();

                    // Set final quote for workorder and set the corresponding handyman that is going
                    // to fix the issue
                    workOrder.setQuotes(allQuotes);
                    workOrder.setFinalQuote(approvedQuote);
                    workOrder.setHandyman(approvedQuote.getHandyman());
                    workOrder.saveInBackground();

                    // Give points to tenant, since workorder has been approved and assigned a
                    // handyman
                    workOrder.getTenant().fetchInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject tenant, ParseException e) {
                            ((Tenant) tenant).getUser().fetchInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject user, ParseException e) {
                                    if(e != null)
                                    {
                                        Log.e("Error", e.getMessage());
                                        return;
                                    }
                                    double points =  user.getNumber("points").doubleValue();
                                    Log.i("Points", ""+points +"" + (points+1.0));
                                    user.put("points", points+1);
                                    //user.increment("points");
                                    user.saveInBackground();
                                }
                            });
                        }
                    });
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

    public void fetchQuotes()
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
                for (Quote q: wo.getQuotes())
                    allQuotes.add(q);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
