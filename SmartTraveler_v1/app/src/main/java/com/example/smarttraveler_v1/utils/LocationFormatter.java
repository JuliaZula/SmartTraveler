package com.example.smarttraveler_v1.utils;

import com.example.smarttraveler_v1.data.repository.CountryRepository;

/**
 * Utility class for formatting location queries.
 * Generates a location string in the format "city_countryCode".
 */
public class LocationFormatter {

    /**
     * Generates a query-friendly location string in the format "city_countryCode".
     * The city name is converted to lowercase, and the country code is retrieved from the repository.
     *
     * @param city       The name of the city.
     * @param country    The name of the country.
     * @param repository The {@link CountryRepository} instance used to retrieve the country code.
     * @return A formatted location string (e.g., "paris_fr") or null if the country code is not found.
     */
    public static String generateQueryLocation(String city, String country, CountryRepository repository) {
        String countryCode = repository.getCode(country);
        if (countryCode == null) {
            return null;
        }
        return city.toLowerCase() + "_" + countryCode.toLowerCase();
    }
}
