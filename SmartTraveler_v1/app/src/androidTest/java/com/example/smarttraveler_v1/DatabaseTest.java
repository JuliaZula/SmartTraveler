package com.example.smarttraveler_v1;

import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.smarttraveler_v1.data.model.Airport;
import com.example.smarttraveler_v1.data.repository.AirportRepository;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class DatabaseTest {
    private Context context;
    private AirportRepository repository;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        repository = new AirportRepository(context);
        repository.resetDatabase(context);
        repository.importAirports(context);
    }

    @Test
    public void testGetAirportsByCity() throws InterruptedException {
        List<Airport> airports = repository.getAirportsByCity("London");

        assertNotNull(airports);
        //assertTrue(airports.size() > 0);

        for (Airport airport : airports) {
            Log.d("AirportRepositoryTest", "Airport: " + airport.name);
        }
    }
}
