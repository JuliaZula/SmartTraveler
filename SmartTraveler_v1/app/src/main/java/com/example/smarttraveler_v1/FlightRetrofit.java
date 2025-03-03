package com.example.smarttraveler_v1;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FlightRetrofit {
    public static final String URL = "http://10.0.2.2:5002";
    public static Retrofit retrofit;

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return  retrofit;
    }
}
