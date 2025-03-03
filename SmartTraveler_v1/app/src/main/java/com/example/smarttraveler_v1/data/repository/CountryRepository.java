package com.example.smarttraveler_v1.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.smarttraveler_v1.data.dao.CountryDao;
import com.example.smarttraveler_v1.data.database.CountryDatabase;
import com.example.smarttraveler_v1.data.model.Country;
import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class CountryRepository {
    private final static String FILE_NAME = "countries.csv";
    private CountryDatabase database;
    private CountryDao countryDao;
    private ExecutorService executorService;
    public CountryRepository(Context context) {
        database = CountryDatabase.getInstance(context);
        this.countryDao = database.countryDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }
    public boolean databaseIsEmpty() {
        Future<Boolean> future = executorService.submit(() -> countryDao.countCountries() == 0);
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<String> getAllCountriesName() {
        try {
            return executorService.submit(() -> countryDao.getAllCountriesName()).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    public String getCode(String countryName) {
        try {
            return executorService.submit(() -> countryDao.getCode(countryName)).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void insertAll(List<Country> countries) {
        executorService.execute(() -> {
            database.runInTransaction(() ->
                    countryDao.insertAll(countries));
            Log.d("CountriesRepository", "Inserted " + countryDao.countCountries() + " countries into database.");
        });
    }
    public void importCountries(Context context) {
        executorService.execute(() -> {
            List<Country> countries = new ArrayList<>();

            try(InputStream is = context.getAssets().open(FILE_NAME);
                CSVReader reader = new CSVReader(new InputStreamReader(is))) {

                String[] tokens;

                while ((tokens = reader.readNext()) != null) {
                    if (tokens.length > 1 && !tokens[1].equals("\\N")) {
                        Country country = new Country(tokens[0],tokens[1]);
                        countries.add(country);
                        Log.d("CountryDatabase",country.toString());
                    }
                }
                if (!countries.isEmpty()) {
                    insertAll(countries);
                }
            } catch (Exception e) {
                Log.e("CountryRepository", "Error reading CSV file: " + e.getMessage(), e);
            }
        });
    }

    public void shutdown() {
        executorService.shutdown();
    }
    public void resetDatabase(Context context) {
        context.deleteDatabase("country_database");
        Log.d("CountryRepository", "Database deleted!");
    }
}
