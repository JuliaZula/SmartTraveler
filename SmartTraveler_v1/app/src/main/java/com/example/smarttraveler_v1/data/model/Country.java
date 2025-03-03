package com.example.smarttraveler_v1.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "countries")
public class Country {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    public String name; //Full name of the country or territory.
    @ColumnInfo(name = "so_code")
    public String so_code; //Unique two-letter ISO 3166-1 code for the country or territory. What we need

    public Country(String name, String so_code) {
        this.name = name;
        this.so_code = so_code;
    }

    @NonNull
    @Override
    public String toString() {
        return "Name: "+name+" Code: "+so_code;
    }

    //The data is UTF-8 encoded. The special value \N is used for "NULL" to indicate that no value is available, and is understood automatically by MySQL if imported.
    //Notes:
    //Some entries have DAFIF codes, but not ISO codes. These are primarily uninhabited islands without airports, and can be ignored for most purposes.
}
