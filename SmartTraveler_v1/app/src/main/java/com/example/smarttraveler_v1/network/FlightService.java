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

    public double[][] getPricesMatrix() {
        return pricesMatrix;
    }

    public List<String> getAirports() {
        return airports;
    }

    public String[] getEmptyPassages() {
        return emptyPassages;
    }

    private int getThreadNumber(int airportsNumber) {
        int cpuNumber = Runtime.getRuntime().availableProcessors();
        return Math.min(airportsNumber,cpuNumber*2);
    }

    public CompletableFuture<Void> getFuture(int index, Function<Integer,String> jointDestinations, BiConsumer<Integer,List<Double>> insertMatrix) {
        String departure = airports.get(index);
        String destinations = jointDestinations.apply(index);

        return fetchPrices(departure,destinations)
                .exceptionally(this::exceptionHandler)
                .thenAccept(response -> processResponse(response,index, insertMatrix));
    }

    public CompletableFuture<Void> oneRightFuture(int index) {
        return getFuture(index, this::jointRightDestinations, this::insertMatrixRight);
    }

    public double[][] getFilledPricesMatrix() {
        Boolean allFilled = fillMatrix().join();
        if (allFilled) {return pricesMatrix;}
        return null;
    }

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


    public void addEmptyPassage(int row, int column) {
         emptyPassages[emptyPassagesCursor++] = "From "+airports.get(row)+" To "+airports.get(column);
    }

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

    private List<Double> exceptionHandler(Throwable e) {
        Log.e("API request failed", e.getMessage(), e);
        return Collections.<Double>emptyList();
    }
    private void processResponse(List<Double> response, int index, BiConsumer<Integer,List<Double>> insertMatrix) {
        if (!response.isEmpty()) {
            insertMatrix.accept(index,response);
        }
    }

    public String jointRightDestinations(int index) {//utilise plus 1
        index++;
        if (index >= airports.size() || index <= 0) {return null;}
        List<String> destinationsList = airports.subList(index, airports.size());
        return String.join(",",destinationsList);
    }

    public String jointLeftDestinations(int index) {//utilise sans moines 1
        if (index < 1 || index >= airports.size()) {return null;}
        List<String> destinationsList = airports.subList(0,index);
        return String.join(",",destinationsList);
    }

    public void insertMatrixRight(int index, List<Double> prices) {
        for (int i = 0; i < prices.size(); i++) {
            if (index+i+1 >= matrixDimension) {return;}//check how to deal the error
            double price = prices.get(i) == null ? 0. : prices.get(i);
            pricesMatrix[index][index+i+1] = price;
        }
    }

    public void insertMatrixLeft(int index, List<Double> prices) {
        for (int i = 0; i < prices.size(); i++) {
            if (i >= matrixDimension) {return;}//check how to deal the error
            double price = prices.get(i) == null ? 0. : prices.get(i);
            pricesMatrix[index][i] = price;
        }
    }
}
