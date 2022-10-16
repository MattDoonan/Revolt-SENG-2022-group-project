package seng202.team3.data.entity.util;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import seng202.team3.data.entity.Connector;

/**
 * Converts a {@link Connector connectors} list field saved as a string into
 * connector objects
 *
 * @author Harrison Tyson
 * @version 1.0.0, Aug 22
 */
public class TimeLimitConverter extends AbstractBeanField<Double, String> {

    /**
     * Unused constructor
     */
    public TimeLimitConverter() {
        // unused
    }

    /** {@inheritDoc} */
    @Override
    public Object convert(String value) throws CsvDataTypeMismatchException,
            CsvConstraintViolationException {
        Double result;
        try { // Get value as Double
            result = Double.parseDouble(value);
        } catch (NumberFormatException e) { // Number is string ('Unlimited') or invalid
            result = 0.0;
        }

        return result;
    }
}
