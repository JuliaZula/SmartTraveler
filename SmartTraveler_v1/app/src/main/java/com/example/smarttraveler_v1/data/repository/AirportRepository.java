package com.example.smarttraveler_v1.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.smarttraveler_v1.data.dao.AirportDao;
import com.example.smarttraveler_v1.data.database.AirportDatabase;
import com.example.smarttraveler_v1.data.model.Airport;
import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AirportRepository {
    private static final String FILE_NAME = "airports.csv";
    private final AirportDatabase database;
    private final AirportDao airportDao;
    private final ExecutorService executor;
    public AirportRepository(Context context) {
        this.database = AirportDatabase.getInstance(context);
        this.airportDao = database.airportDao();
        this.executor = Executors.newSingleThreadExecutor();
    }
    public List<Airport> getAirportsByCity(String cityName) {
        try {
            return executor.submit(() -> airportDao.getAirportsByCity(cityName)).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getCitiesByCountry(String countryName) {
        try {
            return executor.submit(() -> airportDao.getCitiesByCountry(countryName)).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void insertAll(List<Airport> airports) {
        executor.execute(() -> {
            database.runInTransaction(() -> {
                airportDao.insertAll(airports);
                Log.d("AirportRepository", "Inside transaction: inserted " + airports.size() + " airports");
            });
            Log.d("AirportRepository", "Successfully inserted " + airportDao.countAirports() + " airports.");
        });
    }


    public void importAirports(Context context) {
        executor.execute(() -> {
            List<Airport> airports = new ArrayList<>();

            try (InputStream is = context.getAssets().open(FILE_NAME);
                 CSVReader reader = new CSVReader(new InputStreamReader(is))) {

                String[] tokens;
                while ((tokens = reader.readNext()) != null) {

                        int id = Integer.parseInt(tokens[0].trim());
                        String name = tokens[1].trim();
                        String city = tokens[2].trim();
                        String country = tokens[3].trim();

                        Airport airport = new Airport(id, name, city, country);
                        Log.d("AirPort",airport.id+"");
                        airports.add(airport);
                }
                if (!airports.isEmpty())  {
                    insertAll(airports);
                    Log.d("AirportRepository", "Total airports1 parsed from CSV: " + airports.size());
                }
            } catch (Exception e) {
                Log.e("AirportRepository", "Error reading CSV file: " + e.getMessage(), e);
            }
        });
    }

    public void shutdown() {
        executor.shutdown();
    }

    public void resetDatabase(Context context) {
        context.deleteDatabase("airport_database");
        Log.d("AirportRepository", "Database deleted!");
    }
}
