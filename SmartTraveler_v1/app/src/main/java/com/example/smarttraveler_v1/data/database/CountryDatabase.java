package com.example.smarttraveler_v1.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.smarttraveler_v1.data.dao.CountryDao;
import com.example.smarttraveler_v1.data.model.Country;

/**
 * Room Database class for managing the country database.
 * Implements the singleton pattern to prevent multiple instances.
 */
@Database(entities = {Country.class}, version = 1)
public abstract class CountryDatabase extends RoomDatabase {

    /**
     * Provides access to the DAO for performing database operations.
     *
     * @return An instance of {@link CountryDao}.
     */
    public abstract CountryDao countryDao();

    /** The singleton instance of the database. */
    private static volatile CountryDatabase INSTANCE;

    /**
     * Retrieves the singleton instance of the database.
     * If the instance is not already created, it initializes it in a thread-safe manner.
     *
     * @param context The application context.
     * @return The singleton instance of {@link CountryDatabase}.
     */
    public static CountryDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (CountryDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            CountryDatabase.class,
                            "country_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
