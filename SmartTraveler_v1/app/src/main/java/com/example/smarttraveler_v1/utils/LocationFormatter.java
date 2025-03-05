package com.example.smarttraveler_v1.utils;

import com.example.smarttraveler_v1.data.repository.CountryRepository;

public class LocationFormatter {
    public static String generateQueryLocation(String city, String country, CountryRepository repository) {
        String countryCode = repository.getCode(country);
        if (countryCode == null) {
            return null;
        }
        return city.toLowerCase()+ "_" + countryCode.toLowerCase();
    }
}
