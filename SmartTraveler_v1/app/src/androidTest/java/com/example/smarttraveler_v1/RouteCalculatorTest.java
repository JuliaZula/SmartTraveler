package com.example.smarttraveler_v1;

import android.util.Log;

import org.junit.Test;

public class RouteCalculatorTest {
    private String[] airports = {"yinchuan_cn","guangzhou_cn","shanghai_cn","beijing_cn"};
    private RouteCalculator testObject;
    @Test
    public void test() {
        FlightService flightService = new FlightService(airports,"2025-03-01T00:00:00", "2025-03-10T00:00:00");
        testObject = RouteCalculator.createRouteCalculator(flightService);
        testObject.calculate();
        printString(testObject.getVisitSuite());
        Log.d("RouteCalculatorTest",testObject.getApproximateCost()+"");
    }

    private void printString(String[] strings) {
        for(String s : strings) {
           Log.d("RouteCalculatorTest", s);
        }
    }
}
