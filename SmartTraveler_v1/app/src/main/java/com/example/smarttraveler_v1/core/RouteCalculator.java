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

public class RouteCalculator {
    private final FlightService flightService;
    private final String[] airports;

    private final int airportsNum;
    private final VehicleRoutingTransportCostsMatrix costMatrix;
    VehicleTypeImpl vehicleType;
    VehicleImpl vehicle;
    VehicleRoutingProblem.Builder vrpBuilder;
    private String[] visitSuite;
    private double[] costSeparated;

    private int visitSuiteCursor;

    private double approximateCost;

    public static RouteCalculator createRouteCalculator(FlightService flightService) {
        double[][] pricesMatrix = flightService.getFilledPricesMatrix();
        if (pricesMatrix == null) {
            throw new IllegalArgumentException("Error: Invalid pricesMatrix from FlightService");
        } else {
            return new RouteCalculator(flightService, pricesMatrix);
        }
    }

    private RouteCalculator(FlightService flightService, double[][] pricesMatrix) {
        this.flightService = flightService;
        this.airports = flightService.getAirports().toArray(new String[0]);
        this.airportsNum = airports.length;
        this.costMatrix = setCostsMatrix(pricesMatrix, airports);
        prepareVehicle();
        this.visitSuite = new String[airportsNum+1];
        this.costSeparated = new double[airportsNum];
    }

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

        for (int i =1; i < airportsNum; i++) {
            Log.d("Service", "Adding service: " + airports[i]);
            Service service = Service.Builder.newInstance(airports[i])
                    .setLocation(com.graphhopper.jsprit.core.problem.Location.newInstance(airports[i]))
                    .build();
            this.vrpBuilder.addJob(service);
        }
    }

    private String getDeparture() {
        return this.airports[0];
    }

    public double getApproximateCost() {
        return approximateCost;
    }

    public String[] getVisitSuite() {
        return visitSuite;
    }
    public double[] getCostSeparated() { return costSeparated; }

    public void calculate() {
        VehicleRoutingProblem problem = vrpBuilder.build();
        VehicleRoutingAlgorithm algorithm = createAlgorithm(problem);
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

        Log.d("Result","Minimum Cost: " + bestSolution.getCost());
        if (bestSolution.getRoutes() == null) {
            Log.d("NULL","Result is empty");
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
                costSeparated[visitSuiteCursor-1] = c;
                visitSuite[visitSuiteCursor++] = jobId;
                prevAct = act;
            }
            double c = problem.getTransportCosts().getTransportCost(prevAct.getLocation(), route.getEnd().getLocation(), prevAct.getEndTime(), route.getDriver(), route.getVehicle());
            c += problem.getActivityCosts().getActivityCost(route.getEnd(), route.getEnd().getArrTime(), route.getDriver(), route.getVehicle());
            costs += c;
            costSeparated[visitSuiteCursor-1] = c;
            visitSuite[visitSuiteCursor++] = getDeparture();
            Log.d("SolutionPrinter",", Total Cost: " + Math.round(costs));
            approximateCost = Math.round(costs);
        }
    }

//    public void compareSolutions() {
//        VehicleRoutingProblem problem = vrpBuilder.build();
//        VehicleRoutingAlgorithm algorithm = createAlgorithm(problem);
//        algorithm.setMaxIterations(500);
//        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
//
//        if (solutions == null || solutions.isEmpty()) {
//            Log.d("SolutionComparison", "No solutions available for comparison.");
//            return;
//        }
//        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);
//        double bestCost = bestSolution.getCost();
//        Log.d("SolutionComparison", "Best Solution Cost: " + bestCost);
//
//        int solutionIndex = 1;
//        for (VehicleRoutingProblemSolution solution : solutions) {
//            double totalCost = 0;
//
//            for (VehicleRoute route : solution.getRoutes()) {
//                double routeCost = 0;
//                TourActivity prevAct = route.getStart();
//
//                for (TourActivity act : route.getActivities()) {
//                    double c = problem.getTransportCosts().getTransportCost(
//                            prevAct.getLocation(), act.getLocation(), prevAct.getEndTime(),
//                            route.getDriver(), route.getVehicle());
//                    c += problem.getActivityCosts().getActivityCost(
//                            act, act.getArrTime(), route.getDriver(), route.getVehicle());
//                    routeCost += c;
//                    prevAct = act;
//                }
//
//                double c = problem.getTransportCosts().getTransportCost(
//                        prevAct.getLocation(), route.getEnd().getLocation(), prevAct.getEndTime(),
//                        route.getDriver(), route.getVehicle());
//                c += problem.getActivityCosts().getActivityCost(
//                        route.getEnd(), route.getEnd().getArrTime(), route.getDriver(), route.getVehicle());
//                routeCost += c;
//
//                totalCost += routeCost;
//            }
//
//            double diff = totalCost - bestCost;
//            double percentageDiff = (diff / bestCost) * 100;
//
//            Log.d("SolutionComparison", "Solution " + solutionIndex + " Cost: " + totalCost +
//                    ", Difference to Best: " + diff + " (" + String.format("%.2f", percentageDiff) + "%)");
//
//            solutionIndex++;
//        }
//    }

}