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
        Log.d("LoadingActivity", "Intent received: " + intent.getExtras());
        departure = intent.getStringExtra("departure");
        touristSpots = intent.getStringArrayExtra("touristSpots");
        startDateArray = intent.getIntArrayExtra("startDateArray");
        endDateArray = intent.getIntArrayExtra("endDateArray");
        airportsMap = (HashMap<String, String[]>) intent.getSerializableExtra("airportsMap");

        // 检查 `intent` 传递的参数是否为空
        if (departure == null) Log.e("LoadingActivity", "departure is missing!");
        if (touristSpots == null) Log.e("LoadingActivity", "touristSpots is missing!");
        if (startDateArray == null) Log.e("LoadingActivity", "startDateArray is missing!");
        if (endDateArray == null) Log.e("LoadingActivity", "endDateArray is missing!");
        if (airportsMap == null) Log.e("LoadingActivity", "airportsMap is missing!");

        if (departure == null || touristSpots == null || startDateArray == null || endDateArray == null || airportsMap == null) {
            Log.e("LoadingActivity", "Intent data is missing!");
            finish();
            return;
        }

        if (startDateArray.length < 3 || endDateArray.length < 3) {
            Log.e("LoadingActivity", "Invalid date array length!");
            finish();
            return;
        }

        new Thread(() -> {
            try {
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
                    if (airportsMap.containsKey(code)) {
                        result.add(new ArrayList<>(Arrays.asList(airportsMap.get(code))));
                    } else {
                        Log.w("LoadingActivity", "Code not found in airportsMap: " + code);
                        result.add(new ArrayList<>(Arrays.asList("Unknown")));
                    }
                }

                Intent resultIntent = new Intent(LoadingActivity.this, ResultActivity.class);
                resultIntent.putExtra("result", result);
                resultIntent.putExtra("cost", cost);
                resultIntent.putExtra("costSeparated", costSeparated);
                startActivity(resultIntent);

                new Handler().postDelayed(this::finish, 500); // 延迟 500ms 关闭 Activity
            } catch (Exception e) {
                Log.e("LoadingActivity", "Error in background thread", e);
                runOnUiThread(() -> finish()); // 遇到异常，确保 UI 线程关闭 Activity
            }
        }).start();
    }
}
