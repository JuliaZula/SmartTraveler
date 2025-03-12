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

/**
 * Repository class for managing country-related database operations.
 * This class acts as an abstraction layer between the Room database and the UI.
 */
public class CountryRepository {

    /** Name of the CSV file containing country data. */
    private static final String FILE_NAME = "countries.csv";

    /** The database instance for managing country records. */
    private final CountryDatabase database;

    /** The DAO interface for accessing country-related data. */
    private final CountryDao countryDao;

    /** Executor service for handling database operations asynchronously. */
    private final ExecutorService executorService;

    /**
     * Initializes a new {@link CountryRepository} instance.
     *
     * @param context The application context.
     */
    public CountryRepository(Context context) {
        database = CountryDatabase.getInstance(context);
        this.countryDao = database.countryDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Checks if the country database is empty.
     *
     * @return True if the database contains no countries, false otherwise.
     */
    public boolean databaseIsEmpty() {
        Future<Boolean> future = executorService.submit(() -> countryDao.countCountries() == 0);
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves the names of all countries stored in the database.
     *
     * @return A list of country names, or an empty list if an error occurs.
     */
    public List<String> getAllCountriesName() {
        try {
            return executorService.submit(() -> countryDao.getAllCountriesName()).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Retrieves the ISO code of a country based on its name.
     *
     * @param countryName The name of the country.
     * @return The ISO code as a {@link String}, or null if not found or if an error occurs.
     */
    public String getCode(String countryName) {
        try {
            return executorService.submit(() -> countryDao.getCode(countryName)).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Inserts a list of countries into the database.
     * If a country with the same primary key already exists, it will be replaced.
     *
     * @param countries The list of {@link Country} objects to be inserted.
     */
    public void insertAll(List<Country> countries) {
        executorService.execute(() -> {
            database.runInTransaction(() -> countryDao.insertAll(countries));
            Log.d("CountriesRepository", "Inserted " + countryDao.countCountries() + " countries into database.");
        });
    }

    /**
     * Imports country data from a CSV file located in the assets folder and inserts it into the database.
     *
     * @param context The application context.
     */
    public void importCountries(Context context) {
        executorService.execute(() -> {
            List<Country> countries = new ArrayList<>();

            try (InputStream is = context.getAssets().open(FILE_NAME);
                 CSVReader reader = new CSVReader(new InputStreamReader(is))) {

                String[] tokens;

                while ((tokens = reader.readNext()) != null) {
                    // Ensure that the country has a valid name and code
                    if (tokens.length > 1 && !tokens[1].equals("\\N")) {
                        Country country = new Country(tokens[0], tokens[1]);
                        countries.add(country);
                        Log.d("CountryDatabase", country.toString());
                    }
                }

                // Insert all parsed countries into the database
                if (!countries.isEmpty()) {
                    insertAll(countries);
                }
            } catch (Exception e) {
                Log.e("CountryRepository", "Error reading CSV file: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Shuts down the executor service to release resources.
     * Should be called when the repository is no longer needed.
     */
    public void shutdown() {
        executorService.shutdown();
    }

    /**
     * Deletes the entire country database and logs the action.
     *
     * @param context The application context.
     */
    public void resetDatabase(Context context) {
        context.deleteDatabase("country_database");
        Log.d("CountryRepository", "Database deleted!");
    }
}
