package com.example.smarttraveler_v1.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface representing the Flight API for retrieving flight prices.
 * This API uses Retrofit for network communication and interacts with a Flask backend.
 */
public interface FlightApi {

    /**
     * Fetches flight prices based on departure location, destinations, and travel dates.
     *
     * This API calls the Flask backend at "/get_flights", which processes the request asynchronously.
     * The response is a list of flight prices (as Double values).
     *
     * @param departure     The departure city with country code in the format "city_country"
     *                      (e.g., "beijing_cn" for Beijing, China). This parameter is required.
     * @param destinations  A comma-separated list of destination cities with country codes
     *                      (e.g., "paris_fr,london_uk"). This parameter is required.
     * @param startDate     The start date of the trip in ISO 8601 format (e.g., "YYYY-MM-DD'T'HH:mm:ss").
     *                      This parameter is required.
     * @param endDate       The end date of the trip in ISO 8601 format (e.g., "YYYY-MM-DD'T'HH:mm:ss").
     *                      This parameter is required.
     * @return A {@link Call} object containing a list of flight prices as Double values.
     *         If the request fails or the server encounters an issue, an error response will be returned.
     */
    @GET("/get_flights")
    Call<List<Double>> getFlights(
            @Query("departure") String departure,
            @Query("destinations") String destinations, // Example: "paris_fr,london_uk"
            @Query("start_date") String startDate, // Example: 2025-05-18T00:00:00
            @Query("end_date") String endDate // Required, must not be null
    );
}
