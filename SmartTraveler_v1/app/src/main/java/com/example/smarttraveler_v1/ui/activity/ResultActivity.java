package com.example.smarttraveler_v1.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ctc.wstx.shaded.msv_core.writer.relaxng.Context;
import com.example.smarttraveler_v1.R;

import java.util.ArrayList;

/**
 * Activity to display the calculated travel route and costs.
 * This activity presents the trip details in a structured grid layout.
 */
public class ResultActivity extends AppCompatActivity {

    private GridLayout resultGrid;
    private TextView tvCost;
    private Button btnBack;

    /**
     * Called when the activity is created.
     * Retrieves travel data from the intent and displays it in a GridLayout.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultGrid = findViewById(R.id.result_grid);
        tvCost = findViewById(R.id.tv_cost);
        btnBack = findViewById(R.id.btn_back);

        // Retrieve data from the intent
        Intent intent = getIntent();
        ArrayList<ArrayList<String>> result = (ArrayList<ArrayList<String>>) intent.getSerializableExtra("result");
        double cost = intent.getDoubleExtra("cost", 0.0);
        ArrayList<Double> costSeparated = (ArrayList<Double>) intent.getSerializableExtra("costSeparated");

        // Display total cost
        tvCost.setText(String.format("Approx. Price 💵：%.1f €", cost));

        // Populate the grid layout with travel data
        if (result != null && costSeparated != null) {
            for (int i = 0; i < result.size(); i++) {
                addGridItem(result.get(i).get(0)); // Country
                addGridItem(result.get(i).get(1)); // City

                // Insert flight cost (except for the last location)
                if (i < costSeparated.size()) {
                    addCostItem(String.format("Cost: %.1f €", costSeparated.get(i)));
                }
            }
        }

        // Set up the back button
        btnBack.setOnClickListener(v -> {
            Intent backIntent = new Intent(ResultActivity.this, MainActivity.class);
            startActivity(backIntent);
            finish();
        });
    }

    /**
     * Adds a text item (country or city) to the GridLayout.
     *
     * @param text The text to be displayed.
     */
    private void addGridItem(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(16);
        textView.setPadding(12, 12, 12, 12);
        textView.setBackground(ContextCompat.getDrawable(this,R.drawable.round_corner_bg));
        textView.setTextColor(getResources().getColor(android.R.color.white, getTheme()));

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
        params.setMargins(8, 8, 8, 8);
        textView.setLayoutParams(params);

        resultGrid.addView(textView);
    }

    /**
     * Adds a flight cost item to the GridLayout (spanning two columns).
     *
     * @param text The formatted flight cost text.
     */
    private void addCostItem(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(20);
        textView.setPadding(12, 12, 12, 12);
        textView.setBackgroundResource(android.R.color.transparent);
        textView.setTextColor(getResources().getColor(android.R.color.holo_orange_light, getTheme()));
        textView.setGravity(android.view.Gravity.CENTER);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(0, 2, 1f); // Spanning 2 columns
        params.setMargins(8, 4, 8, 12);
        textView.setLayoutParams(params);

        resultGrid.addView(textView);
    }
}
