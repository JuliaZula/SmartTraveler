package com.example.smarttraveler_v1;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import android.util.Log;

import com.example.smarttraveler_v1.network.FlightService;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class ApiTest {
    //private String[] airports = {"yinchuan_cn","guangzhou_cn","shanghai_cn","beijing_CN"};
    private String[] airports = {"cairo_eg","beijing_cn"};
    private FlightService testObject;
    @Before
    public void setUp() {
        testObject = new FlightService(airports, "2025-05-18T00:00:00", "2025-05-19T00:00:00");
    }

//    @Test
//    public void testGetPricesRight_RealApi() throws Exception {
//        CompletableFuture<Void> future = testObject.oneRightFuture(0);
//        try {
//            future.get(3, TimeUnit.SECONDS);
//        } catch (Exception e) {
//            fail("Expected no exception, but got: " + Objects.toString(e.getMessage(), "Unknown error"));
//        }
//
//        double[][] result = testObject.getPricesMatrix();
//        assertNotNull(result);
//
//        String matrixString = formatPricesMatrix(result);
//        Log.d("Updated Prices Matrix: " ,matrixString);
//
//        assertFalse("Prices Matrix should not be empty:\n" + matrixString, matrixString.isEmpty());
//    }

//    @Test
//    public void testFillMatrix() {
//        if (testObject.getFilledPricesMatrix() == null) {
//            Log.d("APITest", "is null");
//            return;
//        }
//        testObject.getFilledPricesMatrix();
//        String matrixString = formatPricesMatrix(testObject.getPricesMatrix());
//        Log.d("Updated Prices Matrix: " ,matrixString);
//    }

//    @Test
//    public void testCallExecuteDirectly() throws Exception {
//        String departure = "yinchuan_cn";
//        String destinations = "guangzhou_cn,shanghai_cn,beijing_cn";
//
//        FlightApi flightApi = FlightRetrofit.getRetrofit().create(FlightApi.class);
//
//        Call<List<Double>> call = flightApi.getFlights(departure, destinations, "2025-03-01T00:00:00", "2025-03-10T00:00:00");
//
//        Log.d("API_TEST", "Starting call.execute()...");
//
//        Response<List<Double>> response = call.execute();
//
//        Log.d("API_TEST", "Call.execute() finished.");
//
//        if (response.isSuccessful()) {
//            Log.d("API_TEST", "Response received: " + response.code());
//            Log.d("API_TEST", "Response body: " + response.body());
//        } else {
//            Log.e("API_TEST", "Response failed: " + response.code() + " - " + response.message());
//        }
//    }

    private String formatPricesMatrix(double[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (double[] row : matrix) {
            sb.append(Arrays.toString(row)).append("\n");
        }
        return sb.toString();
    }
}

