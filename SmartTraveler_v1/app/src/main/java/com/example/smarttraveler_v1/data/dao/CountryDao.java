package com.example.smarttraveler_v1.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.smarttraveler_v1.data.model.Country;

import java.util.List;

/**
 * DAO (Data Access Object) interface for managing country-related database operations.
 * This interface provides methods to query, insert, and count countries in the Room database.
 */
@Dao
public interface CountryDao {

    /**
     * Retrieves all countries from the database.
     *
     * @return A list of {@link Country} objects representing all stored countries.
     */
    @Query("SELECT * FROM countries")
    List<Country> getAllCountries();

    /**
     * Retrieves the names of all countries from the database.
     *
     * @return A list of country names as {@link String}.
     */
    @Query("SELECT name FROM countries")
    List<String> getAllCountriesName();

    /**
     * Counts the total number of countries stored in the database.
     *
     * @return The total count of countries as an integer.
     */
    @Query("SELECT COUNT(*) FROM countries")
    int countCountries();

    /**
     * Retrieves the country code (`so_code`) for a given country name.
     *
     * @param countryName The name of the country.
     * @return The country code as a {@link String}, or null if not found.
     */
    @Query("SELECT so_code FROM countries WHERE name LIKE :countryName")
    String getCode(String countryName);

    /**
     * Inserts a list of countries into the database.
     * If a country with the same primary key already exists, it will be replaced.
     *
     * @param countries The list of {@link Country} objects to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Country> countries);
}
