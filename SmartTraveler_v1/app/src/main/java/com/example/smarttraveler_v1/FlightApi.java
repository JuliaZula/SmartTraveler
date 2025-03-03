package com.example.smarttraveler_v1;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlightApi {
    @GET("/get_flights")
    Call<List<Double>> getFlights(
            @Query("departure") String departure,
            @Query("destinations") String destinations, //place1,place2
            @Query("start_date") String startDate, //2025-05-08
            @Query("end_date") String endDate
    );
}
