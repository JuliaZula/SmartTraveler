package com.example.smarttraveler_v1.network;

import android.util.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Function;

import retrofit2.Call;
import retrofit2.Response;
/**
 * Service class responsible for fetching flight prices and managing the price matrix.
 * This class interacts with {@link FlightApi} to retrieve flight data asynchronously.
 */
public class FlightService {
    private final FlightApi flightApi;
    private final List<String> airports;

    private final String startDate;

    private final String endDate;

    private final ExecutorService executor;

    private final int matrixDimension;
    private final double[][] pricesMatrix;
    private final String[] emptyPassages;
    private int emptyPassagesCursor;

    /**
     * Initializes a FlightService instance with the provided parameters.
     *
     * @param airportsTable An array of country codes.
     * @param startDate     The start date for flight searches in ISO 8601 format (e.g., "YYYY-MM-DD'T'HH:mm:ss").
     * @param endDate       The end date for flight searches in ISO 8601 format (e.g., "YYYY-MM-DD'T'HH:mm:ss").
     */
    public FlightService (String[] airportsTable, String startDate, String endDate) {
        this.flightApi = FlightRetrofit.getRetrofit().create(FlightApi.class);
        this.airports = Arrays.asList(airportsTable);
        this.matrixDimension = airports.size();
        this.startDate = startDate;
        this.endDate = endDate;
        int threadNumber = getThreadNumber(airports.size());
        this.executor = Executors.newFixedThreadPool(threadNumber);
        this.pricesMatrix = new double[matrixDimension][matrixDimension];
        this.emptyPassages = new String[matrixDimension*matrixDimension-matrixDimension];
        this.emptyPassagesCursor = 0;
    }

//    public double[][] getPricesMatrix() {
//        return pricesMatrix;
//    }
    /**
     * Retrieves the list of airports.
     *
     * @return A list of airport codes.
     */
    public List<String> getAirports() {
        return airports;
    }
    /**
     * Retrieves the list of missing flight passages.
     *
     * @return An array containing descriptions of missing flights.
     */
    public String[] getEmptyPassages() {
        return emptyPassages;
    }
    /**
     * Determines the optimal number of threads for concurrent execution.
     *
     * @param airportsNumber The number of airports.
     * @return The number of threads to use.
     */
    private int getThreadNumber(int airportsNumber) {
        int cpuNumber = Runtime.getRuntime().availableProcessors();
        return Math.min(airportsNumber,cpuNumber*2);
    }
    /**
     * Creates a CompletableFuture to fetch flight prices and process the response.
     *
     * @param index            The index of the departure airport.
     * @param jointDestinations A function to determine destination airports.
     * @param insertMatrix     A function to insert the retrieved prices into the matrix.
     * @return A CompletableFuture that completes when the operation finishes.
     */
    public CompletableFuture<Void> getFuture(int index, Function<Integer,String> jointDestinations, BiConsumer<Integer,List<Double>> insertMatrix) {
        String departure = airports.get(index);
        String destinations = jointDestinations.apply(index);

        return fetchPrices(departure,destinations)
                .exceptionally(this::exceptionHandler)
                .thenAccept(response -> processResponse(response,index, insertMatrix));
    }
    /**
     * Initiates a future task for right-hand-side destinations.
     *
     * @param index The departure airport index.
     * @return A CompletableFuture that completes when the task finishes.
     */
    public CompletableFuture<Void> oneRightFuture(int index) {
        return getFuture(index, this::jointRightDestinations, this::insertMatrixRight);
    }
    /**
     * Retrieves the completed flight prices matrix.
     *
     * @return The completed price matrix or null if not fully populated.
     */
    public double[][] getFilledPricesMatrix() {
        Boolean allFilled = fillMatrix().join();
        if (allFilled) {return pricesMatrix;}
        return null;
    }
    /**
     * Asynchronously fills the flight prices matrix.
     *
     * @return A CompletableFuture that resolves to true if all prices are fetched.
     */
    public CompletableFuture<Boolean> fillMatrix() {
        int taskNumber = 2*(matrixDimension-2)+2;
        CompletableFuture[] futures = new CompletableFuture[taskNumber];
        int taskCounter = 0;

        for (int i = 0; i < matrixDimension; i++) {
            if (i==0) {
                futures[taskCounter++] = getFuture(0, this::jointRightDestinations, this::insertMatrixRight);
            } else if (i==matrixDimension-1) {
                futures[taskCounter++] = getFuture(matrixDimension-1, this::jointLeftDestinations, this::insertMatrixLeft);
            } else {
                futures[taskCounter++] = getFuture(i, this::jointRightDestinations, this::insertMatrixRight);
                futures[taskCounter++] = getFuture(i, this::jointLeftDestinations, this::insertMatrixLeft);
            }
        }

        return CompletableFuture.allOf(futures).thenApply(v->allFilled());
    }
    /**
     * Checks if the flight price matrix is fully populated.
     *
     * @return True if all required prices are available, false otherwise.
     */
    public boolean allFilled() {
        for (int i = 0; i < matrixDimension; i++) {
            for (int j = 0; j < matrixDimension; j++) {
                if (i!=j && pricesMatrix[i][j]==0.) {
                    addEmptyPassage(i,j);
                }
            }
        }
        return emptyPassagesCursor==0;
    }
    /**
     * Logs missing flight passages in the emptyPassages array.
     *
     * @param row    The departure airport index.
     * @param column The destination airport index.
     */
    public void addEmptyPassage(int row, int column) {
         emptyPassages[emptyPassagesCursor++] = "From "+airports.get(row)+" To "+airports.get(column);
    }
    /**
     * Fetches flight prices from the API asynchronously.
     *
     * @param departure    The departure airport.
     * @param destinations A comma-separated list of destination airports.
     * @return A CompletableFuture containing a list of flight prices.
     */
    private CompletableFuture<List<Double>> fetchPrices(String departure, String destinations) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (departure == null || destinations == null) {
                    throw new RuntimeException("Programming error: (destinations) for fetchPrices is null");
                }
                Call<List<Double>> call = flightApi.getFlights(departure,destinations,startDate,endDate);
                Response<List<Double>> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    return response.body();
                } else {
                    throw new RuntimeException("API response failed: " + response.code() + " - " + response.message());
                }
            } catch (Exception e) {
                throw new RuntimeException("Api error:"+e.getMessage());
            }
        },executor);
    }
    /**
     * Handles exceptions during API requests and logs the error.
     *
     * @param e The thrown exception.
     * @return An empty list to indicate failure.
     */
    private List<Double> exceptionHandler(Throwable e) {
        Log.e("API request failed", e.getMessage(), e);
        return Collections.<Double>emptyList();
    }
    /**
     * Processes the API response and updates the price matrix.
     *
     * @param response      The list of flight prices.
     * @param index         The departure airport index.
     * @param insertMatrix  A function that inserts prices into the matrix.
     */
    private void processResponse(List<Double> response, int index, BiConsumer<Integer,List<Double>> insertMatrix) {
        if (!response.isEmpty()) {
            insertMatrix.accept(index,response);
        }
    }
    /**
     * Constructs a comma-separated string of right-side destination airports (airports after the current index).
     *
     * @param index The index of the departure airport.
     * @return A comma-separated list of right-side destination airports or null if invalid index.
     */
    public String jointRightDestinations(int index) {//utilise plus 1
        index++;
        if (index >= airports.size() || index <= 0) {return null;}
        List<String> destinationsList = airports.subList(index, airports.size());
        return String.join(",",destinationsList);
    }
    /**
     * Constructs a comma-separated string of left-side destination airports (airports before the current index).
     *
     * @param index The index of the departure airport.
     * @return A comma-separated list of left-side destination airports or null if invalid index.
     */
    public String jointLeftDestinations(int index) {//utilise sans moines 1
        if (index < 1 || index >= airports.size()) {return null;}
        List<String> destinationsList = airports.subList(0,index);
        return String.join(",",destinationsList);
    }
    /**
     * Inserts flight prices into the matrix for right-side destinations since the airport index.
     *
     * @param index  The departure airport index.
     * @param prices The list of flight prices for destinations after the index.
     */
    public void insertMatrixRight(int index, List<Double> prices) {
        for (int i = 0; i < prices.size(); i++) {
            if (index+i+1 >= matrixDimension) {return;}//check how to deal the error
            double price = prices.get(i) == null ? 0. : prices.get(i);
            pricesMatrix[index][index+i+1] = price;
        }
    }
    /**
     * Inserts flight prices into the matrix for left-side destinations.
     *
     * @param index  The departure airport index.
     * @param prices The list of flight prices for destinations before the index.
     */
    public void insertMatrixLeft(int index, List<Double> prices) {
        for (int i = 0; i < prices.size(); i++) {
            if (i >= matrixDimension) {return;}//check how to deal the error
            double price = prices.get(i) == null ? 0. : prices.get(i);
            pricesMatrix[index][i] = price;
        }
    }
}
