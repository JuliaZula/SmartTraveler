package com.example.smarttraveler_v1.core;

import static com.graphhopper.jsprit.core.algorithm.box.Jsprit.createAlgorithm;

import android.util.Log;

import com.example.smarttraveler_v1.network.FlightService;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class responsible for calculating the optimal travel route using flight prices.
 * It utilizes the jsprit library to solve the vehicle routing problem (VRP).
 */
public class RouteCalculator {
    private final FlightService flightService;
    private final String[] airports;
    private final int airportsNum;
    private final VehicleRoutingTransportCostsMatrix costMatrix;
    private VehicleTypeImpl vehicleType;
    private VehicleImpl vehicle;
    private VehicleRoutingProblem.Builder vrpBuilder;
    private String[] visitSuite;
    private double[] costSeparated;
    private int visitSuiteCursor;
    private double approximateCost;

    /**
     * Factory method to create a RouteCalculator instance.
     *
     * @param flightService The FlightService instance providing flight prices.
     * @return A RouteCalculator instance.
     * @throws IllegalArgumentException if the flight prices matrix is invalid.
     */
    public static RouteCalculator createRouteCalculator(FlightService flightService) {
        double[][] pricesMatrix = flightService.getFilledPricesMatrix();
        if (pricesMatrix == null) {
            throw new IllegalArgumentException("Error: Invalid pricesMatrix from FlightService");
        }
        return new RouteCalculator(flightService, pricesMatrix);
    }

    /**
     * Private constructor that initializes the RouteCalculator with flight data.
     *
     * @param flightService The FlightService instance.
     * @param pricesMatrix  The flight price matrix.
     */
    private RouteCalculator(FlightService flightService, double[][] pricesMatrix) {
        this.flightService = flightService;
        this.airports = flightService.getAirports().toArray(new String[0]);
        this.airportsNum = airports.length;
        this.costMatrix = setCostsMatrix(pricesMatrix, airports);
        prepareVehicle();
        this.visitSuite = new String[airportsNum + 1];
        this.costSeparated = new double[airportsNum];
    }

    /**
     * Converts the flight prices matrix into a jsprit-compatible cost matrix.
     *
     * @param pricesMatrix The matrix containing flight prices between airports.
     * @param airports     The array of airport codes.
     * @return A VehicleRoutingTransportCostsMatrix instance.
     */
    private VehicleRoutingTransportCostsMatrix setCostsMatrix(double[][] pricesMatrix, String[] airports) {
        VehicleRoutingTransportCostsMatrix.Builder matrixBuilder = VehicleRoutingTransportCostsMatrix.Builder.newInstance(false);
        for (int i = 0; i < pricesMatrix.length; i++) {
            for (int j = 0; j < pricesMatrix[i].length; j++) {
                if (i != j) {
                    matrixBuilder.addTransportDistance(airports[i], airports[j], pricesMatrix[i][j]);
                    Log.d("Matrix", "Cost from " + airports[i] + " to " + airports[j] + ": " + pricesMatrix[i][j]);
                }
            }
        }
        return matrixBuilder.build();
    }

    /**
     * Prepares the vehicle and initializes the VRP problem.
     */
    private void prepareVehicle() {
        this.vehicleType = VehicleTypeImpl.Builder.newInstance("vehicleType")
                .setCostPerDistance(1.0)
                .build();
        this.vehicle = VehicleImpl.Builder.newInstance("vehicle")
                .setStartLocation(com.graphhopper.jsprit.core.problem.Location.newInstance(getDeparture()))
                .setType(vehicleType)
                .build();
        this.vrpBuilder = VehicleRoutingProblem.Builder.newInstance()
                .setRoutingCost(costMatrix)
                .addVehicle(vehicle);

        for (int i = 1; i < airportsNum; i++) {
            Log.d("Service", "Adding service: " + airports[i]);
            Service service = Service.Builder.newInstance(airports[i])
                    .setLocation(com.graphhopper.jsprit.core.problem.Location.newInstance(airports[i]))
                    .build();
            this.vrpBuilder.addJob(service);
        }
    }

    /**
     * Gets the departure airport (first in the list).
     *
     * @return The departure airport code.
     */
    private String getDeparture() {
        return this.airports[0];
    }

    /**
     * Retrieves the approximate cost of the computed travel route.
     *
     * @return The total estimated travel cost.
     */
    public double getApproximateCost() {
        return approximateCost;
    }

    /**
     * Retrieves the ordered list of visited airports.
     *
     * @return An array containing the visit sequence.
     */
    public String[] getVisitSuite() {
        return visitSuite;
    }

    /**
     * Retrieves the cost of each segment in the travel route.
     *
     * @return An array of costs for each segment.
     */
    public double[] getCostSeparated() {
        return costSeparated;
    }

    /**
     * Computes the optimal route using the VRP solver and extracts the visit sequence.
     */
    public void calculate() {
        VehicleRoutingProblem problem = vrpBuilder.build();
        VehicleRoutingAlgorithm algorithm = createAlgorithm(problem);
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

        Log.d("Result", "Minimum Cost: " + bestSolution.getCost());
        if (bestSolution.getRoutes() == null) {
            Log.d("NULL", "Result is empty");
            throw new RuntimeException("Result is empty from RouteCalculator.calculate");
        }

        visitSuite[visitSuiteCursor++] = getDeparture();
        List<VehicleRoute> list = new ArrayList<>(bestSolution.getRoutes());
        list.sort(new com.graphhopper.jsprit.core.util.VehicleIndexComparator());

        for (VehicleRoute route : list) {
            double costs = 0;
            TourActivity prevAct = route.getStart();
            for (TourActivity act : route.getActivities()) {
                String jobId = (act instanceof TourActivity.JobActivity) ? ((TourActivity.JobActivity) act).getJob().getId() : "-";
                double c = problem.getTransportCosts().getTransportCost(prevAct.getLocation(), act.getLocation(), prevAct.getEndTime(), route.getDriver(), route.getVehicle());
                c += problem.getActivityCosts().getActivityCost(act, act.getArrTime(), route.getDriver(), route.getVehicle());
                costs += c;
                Log.d("SolutionPrinter", "  Activity: Job: " + jobId + ", Costs: " + Math.round(costs));
                costSeparated[visitSuiteCursor - 1] = c;
                visitSuite[visitSuiteCursor++] = jobId;
                prevAct = act;
            }
            double c = problem.getTransportCosts().getTransportCost(prevAct.getLocation(), route.getEnd().getLocation(), prevAct.getEndTime(), route.getDriver(), route.getVehicle());
            c += problem.getActivityCosts().getActivityCost(route.getEnd(), route.getEnd().getArrTime(), route.getDriver(), route.getVehicle());
            costs += c;
            costSeparated[visitSuiteCursor - 1] = c;
            visitSuite[visitSuiteCursor++] = getDeparture();
            Log.d("SolutionPrinter", ", Total Cost: " + Math.round(costs));
            approximateCost = Math.round(costs);
        }
    }
}
