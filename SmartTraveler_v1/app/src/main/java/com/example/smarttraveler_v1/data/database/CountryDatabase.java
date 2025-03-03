package com.example.smarttraveler_v1.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.smarttraveler_v1.data.dao.CountryDao;
import com.example.smarttraveler_v1.data.model.Country;

@Database(entities = {Country.class}, version = 1)
public abstract class CountryDatabase extends RoomDatabase {
    public abstract CountryDao countryDao();
    private static volatile CountryDatabase INSTANCE;
    public static CountryDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (CountryDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            CountryDatabase.class,
                            "country_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
