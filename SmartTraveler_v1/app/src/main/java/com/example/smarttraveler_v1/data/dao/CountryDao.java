package com.example.smarttraveler_v1.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.smarttraveler_v1.data.model.Country;

import java.util.List;

@Dao
public interface CountryDao {
    @Query("SELECT * FROM countries")
    List<Country> getAllCountries();
    @Query("SELECT name FROM countries")
    List<String> getAllCountriesName();
    @Query("SELECT COUNT(*) FROM countries")
    int countCountries();
    @Query("SELECT so_code FROM countries WHERE name LIKE :countryName")
    String getCode(String countryName);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Country> countries);
}
