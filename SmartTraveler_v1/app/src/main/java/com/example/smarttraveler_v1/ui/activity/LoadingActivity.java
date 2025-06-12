package com.example.smarttraveler_v1.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarttraveler_v1.R;
import com.example.smarttraveler_v1.core.RouteCalculator;
import com.example.smarttraveler_v1.network.FlightService;
import com.example.smarttraveler_v1.utils.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * LoadingActivity is responsible for processing travel data and calculating the optimal travel route.
 * This activity retrieves intent data, validates it, and runs background calculations.
 */
public class LoadingActivity extends AppCompatActivity {

    private String departure;
    private String[] touristSpots;
    private int[] startDateArray, endDateArray;
    private Map<String, String[]> airportsMap;

    /**
     * Called when the activity is first created.
     * Retrieves intent data and initiates the travel route calculation process.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // Retrieve data from the intent
        Intent intent = getIntent();
        Log.d("LoadingActivity", "Intent received: " + intent.getExtras());
        departure = intent.getStringExtra("departure");
        touristSpots = intent.getStringArrayExtra("touristSpots");
        startDateArray = intent.getIntArrayExtra("startDateArray");
        endDateArray = intent.getIntArrayExtra("endDateArray");
        airportsMap = (HashMap<String, String[]>) intent.getSerializableExtra("airportsMap");

        // Validate intent data
        validateIntentData();

        // Start background processing
        new Thread(this::processTravelData).start();
    }

    /**
     * Validates the received intent data.
     * If required data is missing, logs errors and closes the activity.
     */
    private void validateIntentData() {
        if (departure == null) Log.e("LoadingActivity", "departure is missing!");
        if (touristSpots == null) Log.e("LoadingActivity", "touristSpots is missing!");
        if (startDateArray == null) Log.e("LoadingActivity", "startDateArray is missing!");
        if (endDateArray == null) Log.e("LoadingActivity", "endDateArray is missing!");
        if (airportsMap == null) Log.e("LoadingActivity", "airportsMap is missing!");

        if (departure == null || touristSpots == null || startDateArray == null || endDateArray == null || airportsMap == null) {
            Log.e("LoadingActivity", "Intent data is missing!");
            finish();
        }

        if (startDateArray.length < 3 || endDateArray.length < 3) {
            Log.e("LoadingActivity", "Invalid date array length!");
            finish();
        }
    }

    /**
     * Processes the travel data by generating flight details and calculating the optimal route.
     * The calculated result is passed to the {@link ResultActivity}.
     */
    private void processTravelData() {
        try {
            // Convert start and end dates to formatted strings
            String startTime = DateTimeFormatter.generateStartOfDay(startDateArray[0], startDateArray[1], startDateArray[2]);
            String endTime = DateTimeFormatter.generateEndOfDay(endDateArray[0], endDateArray[1], endDateArray[2]);

            // Prepare airport list
            String[] airports = new String[touristSpots.length + 1];
            airports[0] = departure;
            System.arraycopy(touristSpots, 0, airports, 1, touristSpots.length);

            // Initialize FlightService and RouteCalculator
            FlightService flightService = new FlightService(airports, startTime, endTime);
            RouteCalculator routeCalculator = RouteCalculator.createRouteCalculator(flightService);
            routeCalculator.calculate();

            // Retrieve calculation results
            String[] calculateResult = routeCalculator.getVisitSuite();
            double cost = routeCalculator.getApproximateCost();
            double[] costSeparatedArray = routeCalculator.getCostSeparated();

            // Convert costSeparatedArray to an ArrayList for Intent transmission
            ArrayList<Double> costSeparated = new ArrayList<>();
            for (double d : costSeparatedArray) {
                Log.d("Cost", d + "");
                costSeparated.add(d);
            }

            // Map route calculation results to human-readable locations
            ArrayList<ArrayList<String>> result = new ArrayList<>();
            for (String code : calculateResult) {
                if (airportsMap.containsKey(code)) {
                    result.add(new ArrayList<>(Arrays.asList(airportsMap.get(code))));
                } else {
                    Log.w("LoadingActivity", "Code not found in airportsMap: " + code);
                    result.add(new ArrayList<>(Arrays.asList("Unknown")));
                }
            }

            // Start ResultActivity with the computed travel results
            Intent resultIntent = new Intent(LoadingActivity.this, ResultActivity.class);
            resultIntent.putExtra("result", result);
            resultIntent.putExtra("cost", cost);
            resultIntent.putExtra("costSeparated", costSeparated);
            resultIntent.putExtra("start_date", startTime.substring(0, 10));  // get "YYYY-MM-DD"
            resultIntent.putExtra("end_date", endTime.substring(0, 10));
            Log.d("DateIntent", "Sending start_date = " + startTime + ", end_date = " + endTime);


            startActivity(resultIntent);

            // Delay activity finish to allow UI transition
            new Handler().postDelayed(this::finish, 500);
        } catch (Exception e) {
            Log.e("LoadingActivity", "Error in background thread", e);
            runOnUiThread(this::finish); // Ensure UI thread handles failure properly
        }
    }
}
