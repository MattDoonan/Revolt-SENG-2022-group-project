package seng202.team3.logic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.logic.Calculations;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for a {@link Calculations} Calculations Class in Logic
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Aug 22
 */
public class CalculationsTest {

    /**
     * Creates new Coordinates
     */
    Coordinate coord1;
    Coordinate coord2;
    Coordinate coord3;
    Coordinate coord4;

    /**
     * BeforeEach Coordinate setup
     */
    @BeforeEach
    public void setUp() {
        //Christchurch Hospital
        coord1 = new Coordinate(1.1, 2.3,  -43.53418, 172.627572);
        //Christchurch Boys High School
        coord2 = new Coordinate(3.5, 4.4, -43.52425, 172.60019);
        //Auckland Grammar School
        coord3 = new Coordinate(4.5, 5.7, -36.85918, 174.76602);
        //Otago Boys School
        coord4 = new Coordinate(4.8, 7.7, -45.87135, 170.49551);
    }

    /**
     * AfterEach, tears it all down.
     */
    @AfterEach
    public void tearDown() {
        coord1 = null;
        coord2 = null;
        coord3 = null;
        coord4 = null;
        assertNull(coord1);
        assertNull(coord2);
        assertNull(coord3);
        assertNull(coord4);
    }

    /**
     * Checks {@link Calculations} calculateDistance
     */
    @Test
    public void testCalculateDistanceAccurate() {
        double distance = Calculations.calculateDistance(coord1, coord3);
        assertEquals(764, distance, 0.2);
    }

    @Test
    public void testCalculateNoDistance() {
        double distance = Calculations.calculateDistance(coord1, coord1);
        assertEquals(0, distance, 0.01);
    }

    @Test
    public void compareLongAndShortDistances() {
        double distance1 = Calculations.calculateDistance(coord2, coord3);
        double distance2 = Calculations.calculateDistance(coord2, coord4);
        assertTrue(distance1 > distance2);
    }

    @Test
    public void noCoordinateChangeWhenCalculating() {
        double distance1 = Calculations.calculateDistance(coord1, coord3);
        double distance2 = Calculations.calculateDistance(coord1, coord3);
        assertEquals(distance1, distance2, 0.01);
    }

}
