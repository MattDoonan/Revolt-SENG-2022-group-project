package seng202.team3.unittest.data.database;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import seng202.team3.data.database.ChargerFilter;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.CsvInterpreter;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.entity.Charger;

/**
 * Tests for ChargerFilter {@link ChargerFilter} Class
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Aug 22
 */
public class ChargerFilterTest {
    /**
     * Has a test object with classes
     */
    List<Object> test;

    /**
     * After each tear-down
     */
    @AfterEach
    public void tearDown() {
        test = null;
        assertNull(test);
    }

    /**
     * Tests filter CHARGERID
     */
    @Test
    public void testChargerId() throws IOException {
        test = new CsvInterpreter().readData(
                new QueryBuilderImpl()
                        .withSource("charger")
                        .withFilter("CHARGERID", "30", ComparisonType.LESS_THAN)
                        .build(),
                Charger.class);

        int maximum = 0;
        for (Object c : test) {
            if (((Charger) c).getChargerId() > maximum) {
                maximum = ((Charger) c).getChargerId();
            }
        }
        assertTrue(maximum < 30);
    }

    @Test
    public void testUniqueChargerIds() throws IOException {
        test = new CsvInterpreter().readData(
                new QueryBuilderImpl()
                        .withSource("charger")
                        .withFilter("carparkcount", "2", ComparisonType.LESS_THAN)
                        .build(),
                Charger.class);

        ArrayList<Integer> integers = new ArrayList<>();
        boolean isUnique = true;
        for (Object c : test) {
            if (integers.contains(((Charger) c).getChargerId())) {
                isUnique = false;
            } else {
                integers.add(((Charger) c).getChargerId());
            }
        }
        assertTrue(isUnique);
    }

    @Test
    public void testDoubleFilters() throws IOException {
        test = new CsvInterpreter().readData(
                new QueryBuilderImpl()
                        .withSource("charger")
                        .withFilter("operator", "MERIDIAN ENERGY LIMITED", ComparisonType.CONTAINS)
                        .withFilter("latitude", "-40", ComparisonType.GREATER_THAN_EQUAL)
                        .build(),
                Charger.class);

        boolean correct = true;
        double minimum = -40.0;
        for (Object c : test) {
            if ((((Charger) c).getLocation().getLat() < minimum)
                    || !(((Charger) c).getOperator().contains("MERIDIAN ENERGY LIMITED"))) {
                correct = false;
                break;
            }
        }
        assertTrue(correct);
    }
}
