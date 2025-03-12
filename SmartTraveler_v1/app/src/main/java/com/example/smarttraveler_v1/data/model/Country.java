package com.example.smarttraveler_v1.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents a country entity in the Room database.
 * This class stores basic country information including its name and ISO code.
 */
@Entity(tableName = "countries")
public class Country {

    /** The full name of the country or territory. */
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    public String name;

    /**
     * The unique two-letter ISO 3166-1 code for the country or territory.
     * This is the primary code used for identification.
     */
    @ColumnInfo(name = "so_code")
    public String so_code;

    /**
     * Constructs a new {@link Country} instance.
     *
     * @param name    The full name of the country.
     * @param so_code The ISO 3166-1 code of the country.
     */
    public Country(@NonNull String name, String so_code) {
        this.name = name;
        this.so_code = so_code;
    }

    /**
     * Returns a string representation of the country object.
     *
     * @return A formatted string containing the country name and ISO code.
     */
    @NonNull
    @Override
    public String toString() {
        return "Name: " + name + " Code: " + so_code;
    }
    //The data is UTF-8 encoded. The special value \N is used for "NULL" to indicate that no value is available, and is understood automatically by MySQL if imported.
    //Notes:
    //Some entries have DAFIF codes, but not ISO codes. These are primarily uninhabited islands without airports, and can be ignored for most purposes.
}
