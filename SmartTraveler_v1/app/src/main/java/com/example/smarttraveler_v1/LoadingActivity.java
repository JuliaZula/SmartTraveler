package com.example.smarttraveler_v1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarttraveler_v1.util.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LoadingActivity extends AppCompatActivity {
    private String departure;
    private String[] touristSpots;
    private int[] startDateArray, endDateArray;
    private Map<String,String[]> airportsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Intent intent = getIntent();
        departure = intent.getStringExtra("departure");
        touristSpots = intent.getStringArrayExtra("touristSpots");
        startDateArray = intent.getIntArrayExtra("startDateArray");
        endDateArray = intent.getIntArrayExtra("endDateArray");
        airportsMap = (HashMap<String, String[]>) intent.getSerializableExtra("airportsMap");

        new Thread(() -> {
            String startTime = DateTimeFormatter.generateStartOfDay(startDateArray[0], startDateArray[1], startDateArray[2]);
            String endTime = DateTimeFormatter.generateEndOfDay(endDateArray[0], endDateArray[1], endDateArray[2]);

            String[] airports = new String[touristSpots.length + 1];
            airports[0] = departure;
            System.arraycopy(touristSpots, 0, airports, 1, touristSpots.length);

            FlightService flightService = new FlightService(airports, startTime, endTime);
            RouteCalculator routeCalculator = RouteCalculator.createRouteCalculator(flightService);
            routeCalculator.calculate();

            String[] calculateResult = routeCalculator.getVisitSuite();
            double cost = routeCalculator.getApproximateCost();
            double[] costSeparatedArray = routeCalculator.getCostSeparated();
            ArrayList<Double> costSeparated = new ArrayList<>();
            for(double d : costSeparatedArray) {
                Log.d("Cost", d+"");
                costSeparated.add(d);
            }

            ArrayList<ArrayList<String>> result = new ArrayList<>();
            for (String code : calculateResult) {
                result.add(new ArrayList<>(Arrays.asList(Objects.requireNonNull(airportsMap.get(code)))));
            }

            Intent resultIntent = new Intent(LoadingActivity.this, ResultActivity.class);
            resultIntent.putExtra("result", result);
            resultIntent.putExtra("cost", cost);
            resultIntent.putExtra("costSeparated", costSeparated);
            startActivity(resultIntent);

            finish();
        }).start();
    }
}


