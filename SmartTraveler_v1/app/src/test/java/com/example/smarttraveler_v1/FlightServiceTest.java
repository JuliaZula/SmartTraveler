package com.example.smarttraveler_v1;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlightServiceTest {
    private String[] airports = {"A","B","C","D","E"};
    private FlightService testObject;

    @Before
    public void setUp() {
        testObject = new FlightService(airports, "2024-01-01", "2024-01-10");
    }

    @Test
    public void testJointRightDestinations() {
        String s0 = testObject.jointRightDestinations(0);
        String s1 = testObject.jointRightDestinations(1);
        String s2 = testObject.jointRightDestinations(2);
        String s4 = testObject.jointRightDestinations(4);
        assertNull(s4);
        assertEquals("B,C,D,E",s0);
        assertEquals("C,D,E",s1);
    }

    @Test
    public void testJointLeftDestinations() {
        String s0 = testObject.jointLeftDestinations(0);
        String s1 = testObject.jointLeftDestinations(1);
        String s2 = testObject.jointLeftDestinations(2);
        String s4 = testObject.jointLeftDestinations(4);
        assertNull(s0);
        assertEquals("A",s1);
        assertEquals("A,B",s2);
        assertEquals("A,B,C,D",s4);
    }

    @Test
    public void testAllFilled() {
        boolean result = testObject.allFilled();
        System.out.println(String.join(" ",testObject.getEmptyPassages()));
        assertFalse(result);
    }


    @Test
    public void testInsertMatrixRight() {

        List<Double> l1 = new ArrayList<>();
        l1.add(1.);
        l1.add(2.);
        l1.add(3.);
        l1.add(null);

        testObject.insertMatrixRight(0,l1);
//        double[][] result = testObject.getPricesMatrix();
//
//        assertEquals(1.,result[0][1],1e-9);
//        assertEquals(2.,result[0][2],1e-9);
//        assertEquals(3.,result[0][3],1e-9);
//        assertEquals(0.,result[0][4],1e-9);
    }

    @Test
    public void testInsertMatrixLeft() {

        List<Double> l1 = new ArrayList<>();
        l1.add(1.);
        l1.add(2.);
        l1.add(3.);
        l1.add(null);

        testObject.insertMatrixLeft(4,l1);
//        double[][] result = testObject.getPricesMatrix();
//
//        assertEquals(1.,result[4][0],1e-9);
//        assertEquals(2.,result[4][1],1e-9);
//        assertEquals(3.,result[4][2],1e-9);
//        assertEquals(0.,result[4][3],1e-9);
//
//        System.out.println(formatPricesMatrix(testObject.getPricesMatrix()));
    }

    private String formatPricesMatrix(double[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (double[] row : matrix) {
            sb.append(Arrays.toString(row)).append("\n");
        }
        return sb.toString();
    }

}