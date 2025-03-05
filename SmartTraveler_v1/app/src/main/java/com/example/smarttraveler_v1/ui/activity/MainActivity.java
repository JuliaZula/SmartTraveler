package com.example.smarttraveler_v1.ui.activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.example.smarttraveler_v1.R;
import com.example.smarttraveler_v1.data.repository.CountryRepository;
import com.example.smarttraveler_v1.utils.LocationFormatter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import android.view.View;
import android.widget.LinearLayout;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private TextView tvSelectDates;
    private LinearLayout touristSpotsContainer;
    private boolean isDateSelected = false;

    private Button btnAddTouristSpot, btnClear, btnSubmit;
    private EditText editDepartureCountry, editDepartureCity;
    private CountryRepository countryRepository;

    private int[] startDateArray = new int[3]; // {year, month, day}
    private int[] endDateArray = new int[3];   // {year, month, day}
    private String departure;                 // city_countryCode
    private String[] touristSpots;            // city_countryCode
    private Map<String,String[]> airportsMap  = new HashMap<>(); //<{country,city},code>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countryRepository = new CountryRepository(this);
        countryRepository.importCountries(this);

        tvSelectDates = findViewById(R.id.tv_select_dates);
        touristSpotsContainer = findViewById(R.id.tourist_spots_container);
        btnAddTouristSpot = findViewById(R.id.btn_add_tourist_spot);
        btnClear = findViewById(R.id.btn_clear);
        btnSubmit = findViewById(R.id.btn_submit);
        editDepartureCountry = findViewById(R.id.edit_departure_country);
        editDepartureCity = findViewById(R.id.edit_departure_city);

        btnSubmit.setEnabled(false);
        btnAddTouristSpot.setEnabled(false);

        editDepartureCountry.addTextChangedListener(textWatcher);
        editDepartureCity.addTextChangedListener(textWatcher);
        tvSelectDates.setOnClickListener(v -> showDateRangePicker());
        btnAddTouristSpot.setOnClickListener(v -> addTouristSpot());
        btnClear.setOnClickListener(v -> clearForm());
        btnSubmit.setOnClickListener(v -> collectTripData());
    }

    // show calender selector
    private void showDateRangePicker() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select Travel Dates");

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointForward.now());
        builder.setCalendarConstraints(constraintsBuilder.build());

        MaterialDatePicker<Pair<Long, Long>> picker = builder.build();
        picker.show(getSupportFragmentManager(), picker.toString());

        picker.addOnPositiveButtonClickListener(selection -> {
            if (selection instanceof Pair) {
                Pair<Long, Long> dateRange = (Pair<Long, Long>) selection;

                LocalDate startDate = Instant.ofEpochMilli(dateRange.first)
                        .atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate endDate = Instant.ofEpochMilli(dateRange.second)
                        .atZone(ZoneId.systemDefault()).toLocalDate();

                startDateArray = new int[]{startDate.getYear(), startDate.getMonthValue(), startDate.getDayOfMonth()};
                endDateArray = new int[]{endDate.getYear(), endDate.getMonthValue(), endDate.getDayOfMonth()};

                tvSelectDates.setText(String.format(Locale.getDefault(),
                        "TravelDate 📅：%d-%02d-%02d → %d-%02d-%02d",
                        startDateArray[0], startDateArray[1], startDateArray[2],
                        endDateArray[0], endDateArray[1], endDateArray[2]));

                isDateSelected = true;

                Log.d("TripDates", "起始日期: " + Arrays.toString(startDateArray));
                Log.d("TripDates", "截止日期: " + Arrays.toString(endDateArray));

                validateForm(); // 检查表单是否填满
            }
        });
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            validateDepartureInput();
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private void validateDepartureInput() {
        String country = editDepartureCountry.getText().toString().trim();
        String city = editDepartureCity.getText().toString().trim();

        btnAddTouristSpot.setEnabled(!country.isEmpty() && !city.isEmpty());
    }


    private void collectTripData() {
        if (!parseDeparture() || !parseTouristSpots()) {
            Log.e("TripData", "failed！");
            return;
        }

        Intent loadingIntent = new Intent(MainActivity.this, LoadingActivity.class);
        loadingIntent.putExtra("departure", departure);
        loadingIntent.putExtra("touristSpots", touristSpots);
        loadingIntent.putExtra("startDateArray", startDateArray);
        loadingIntent.putExtra("endDateArray", endDateArray);
        loadingIntent.putExtra("airportsMap", new HashMap<>(airportsMap));
        startActivity(loadingIntent);
    }



    private void printString(String[] strings) {
        for(String s : strings) {
            for(String s1 : Objects.requireNonNull(airportsMap.get(s))) {
                Log.d("TripData",s1);
            }
        }
    }

    private boolean parseDeparture() {
        String country = editDepartureCountry.getText().toString().trim();
        String city = editDepartureCity.getText().toString().trim();
        if (country.isEmpty() || city.isEmpty()) return false;

        departure = LocationFormatter.generateQueryLocation(city, country, countryRepository);
        if (departure == null || departure.isEmpty()) {
            Log.e("parseDeparture", "Failed to generate departure location!");
            return false;
        }

        String[] departureArray = new String[2];
        departureArray[0] = country;
        departureArray[1] = city;

        airportsMap.put(departure,departureArray);
        return true;
    }

    private boolean parseTouristSpots() {
        List<String> spotsList = new ArrayList<>();

        for (int i = 0; i < touristSpotsContainer.getChildCount(); i++) {
            View row = touristSpotsContainer.getChildAt(i);
            if (row instanceof LinearLayout) {
                LinearLayout rowLayout = (LinearLayout) row;
                EditText countryField = (EditText) rowLayout.getChildAt(0);
                EditText cityField = (EditText) rowLayout.getChildAt(1);

                String country = countryField.getText().toString().trim();
                String city = cityField.getText().toString().trim();
                if (!country.isEmpty() && !city.isEmpty()) {
                    String touristCode = LocationFormatter.generateQueryLocation(city, country, countryRepository);
                    spotsList.add(touristCode);

                    String[] touristArray = new String[2];
                    touristArray[0] = country;
                    touristArray[1] = city;

                    airportsMap.put(touristCode,touristArray);
                }
            }
        }

        if (spotsList.isEmpty()) return false;
        touristSpots = spotsList.toArray(new String[0]);
        return true;
    }

    private void addTouristSpot() {
        LinearLayout rowLayout = new LinearLayout(this);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        rowLayout.setGravity(Gravity.CENTER_VERTICAL);

        EditText country = new EditText(this);
        country.setHint("Country");
        country.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        country.setPadding(16, 16, 16, 16);

        EditText city = new EditText(this);
        city.setHint("City");
        city.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        city.setPadding(16, 16, 16, 16);

        rowLayout.addView(country);
        rowLayout.addView(city);
        touristSpotsContainer.addView(rowLayout);

        validateForm();
    }

    private void clearForm() {
        editDepartureCountry.setText("");
        editDepartureCity.setText("");
        tvSelectDates.setText("TravelDate 📅");
        touristSpotsContainer.removeAllViews();

        btnSubmit.setEnabled(false);
        isDateSelected = false;
    }
    private void validateForm() {
        boolean isDepartureFilled = !editDepartureCountry.getText().toString().trim().isEmpty()
                && !editDepartureCity.getText().toString().trim().isEmpty();
        boolean hasTouristSpots = touristSpotsContainer.getChildCount() > 0;

        btnSubmit.setEnabled(isDateSelected && isDepartureFilled && hasTouristSpots);
    }
}