package com.example.smarttraveler_v1;

import android.util.Log;

import com.example.smarttraveler_v1.core.RouteCalculator;
import com.example.smarttraveler_v1.network.FlightService;

import org.junit.Test;

public class RouteCalculatorTest {
    private String[] airports = {"shanghai_cn","paris_fr","london_gb","beijing_cn"};
    private RouteCalculator testObject;
    @Test
    public void test() {
        FlightService flightService = new FlightService(airports,"2025-03-05T00:00:00", "2025-03-20T00:00:00");
        testObject = RouteCalculator.createRouteCalculator(flightService);
        testObject.calculate();
//        printString(testObject.getVisitSuite());
        Log.d("RouteCalculatorTest",testObject.getApproximateCost()+"");
        testObject.compareSolutions();
    }

    private void printString(String[] strings) {
        for(String s : strings) {
           Log.d("RouteCalculatorTest", s);
        }
    }
}
