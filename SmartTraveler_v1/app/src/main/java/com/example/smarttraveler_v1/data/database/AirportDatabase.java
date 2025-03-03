package com.example.smarttraveler_v1.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.smarttraveler_v1.data.dao.AirportDao;
import com.example.smarttraveler_v1.data.model.Airport;

@Database(entities = {Airport.class}, version = 1)
public abstract class AirportDatabase extends RoomDatabase {
    public abstract AirportDao airportDao();
    private static volatile AirportDatabase INSTANCE;
    public static AirportDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AirportDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AirportDatabase.class, "airport_database")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
