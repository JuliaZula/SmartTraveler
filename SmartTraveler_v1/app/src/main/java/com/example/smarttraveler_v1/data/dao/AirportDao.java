package com.example.smarttraveler_v1.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.smarttraveler_v1.data.model.Airport;

import java.util.List;

@Dao
public interface AirportDao {
    @Query("SELECT * FROM airports")
    List<Airport> getAllAirports();
    @Query("SELECT * FROM airports WHERE city LIKE :cityName")
    List<Airport> getAirportsByCity(String cityName);
    @Query("SELECT COUNT(*) FROM airports")
    int countAirports();
    @Query("SELECT city FROM airports WHERE country = :countryName")
    List<String> getCitiesByCountry(String countryName);
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Airport> airports);
}
