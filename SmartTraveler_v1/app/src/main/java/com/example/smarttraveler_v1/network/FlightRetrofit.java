package com.example.smarttraveler_v1.network;

import com.example.smarttraveler_v1.BuildConfig;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton class responsible for creating and managing a Retrofit instance.
 * This class provides a single Retrofit instance to be used for network requests.
 */
public class FlightRetrofit {

    /** The base URL for the API, retrieved from BuildConfig. */
    public static final String URL = BuildConfig.API_BASE_URL;

    /** The Retrofit instance for handling network requests. */
    public static Retrofit retrofit;

    /**
     * Returns the singleton Retrofit instance.
     * If the instance is not yet created, it initializes it with the base URL.
     *
     * @return A {@link Retrofit} instance for making API requests.
     */
    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(URL)  // Set the base API URL
                    .addConverterFactory(GsonConverterFactory.create())  // Use Gson for JSON serialization
                    .build();
        }
        return retrofit;
    }
}

