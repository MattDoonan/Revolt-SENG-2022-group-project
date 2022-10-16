package seng202.team3.unittest.logic.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.logic.util.Calculations;

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
    Coordinate coord5;
    Coordinate coord6;
    Coordinate coord7;
    Coordinate coord8;

    /**
     * BeforeEach Coordinate setup
     */
    @BeforeEach
    public void setUp() {
        // Christchurch Hospital
        coord1 = new Coordinate(-43.53418, 172.627572, "CHHosp");
        // Christchurch Boys High School
        coord2 = new Coordinate(-43.52425, 172.60019, "CHBoys");
        // Auckland Grammar School
        coord3 = new Coordinate(-36.85918, 174.76602, "Auckland");
        // Otago Boys School
        coord4 = new Coordinate(-45.87135, 170.49551, "Otago");
        // NZMCA Park, Weedons
        coord5 = new Coordinate(-43.56326, 172.43662, "Weedons");
        // Ashburton Domain
        coord6 = new Coordinate(-43.89523, 171.76171, "Ashburt");
        // Rakaia Domain
        coord7 = new Coordinate(-43.74852, 172.02586, "Rakaia");
        // Ellesmere College
        coord8 = new Coordinate(-43.75682, 172.28899, "Elles");

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
        coord5 = null;
        coord6 = null;
        coord7 = null;
        coord8 = null;
        assertNull(coord1);
        assertNull(coord2);
        assertNull(coord3);
        assertNull(coord4);
        assertNull(coord5);
        assertNull(coord6);
        assertNull(coord7);
        assertNull(coord8);
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

    /**
     * Test the chargers between points functionality
     */
    @Test
    public void testIsWithinRange() {
        boolean inDistance = true;

        double distance = Calculations.calculateDistance(coord1, coord6) * 1.3;
        ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
        coords.add(coord1);
        coords.add(coord2);
        coords.add(coord3);
        coords.add(coord4);
        coords.add(coord5);
        coords.add(coord6);
        coords.add(coord7);
        coords.add(coord8);

        List<Coordinate> filtered = coords.stream()
                .filter(c -> Calculations.isWithinRange(c, coord1, coord6, distance))
                .toList();

        for (Coordinate coordinate : filtered) {
            if ((Calculations.calculateDistance(coordinate, coord1)
                    + Calculations.calculateDistance(coordinate, coord6)) > distance) {
                inDistance = false;
                break;
            }
        }
        assertTrue(inDistance);
    }

    @Test
    public void isReasonableRange() {
        double distance = Calculations.calculateDistance(coord1, coord6) * 1.2;
        ArrayList<Coordinate> coords = new ArrayList<>();
        coords.add(coord1);
        coords.add(coord2);
        coords.add(coord3);
        coords.add(coord4);
        coords.add(coord5);
        coords.add(coord6);
        coords.add(coord7);
        coords.add(coord8);

        List<Coordinate> filtered = coords.stream()
                .filter(c -> Calculations.isWithinRange(c, coord1, coord6, distance))
                .toList();

        assertTrue(filtered.size() == 6);
    }

}
