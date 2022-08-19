package seng202.team3.data.entity;

import com.opencsv.bean.AbstractCsvConverter;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.apache.commons.lang3.StringUtils;

/**
 * Converts a {@link Connector connectors} list field saved as a string into
 * connector objects
 * 
 * @author Harrison Tyson
 * @version 1.0.0, Aug 22
 */
public class ConnectorConverter extends AbstractCsvConverter {

    @Override
    public Object convertToRead(String value) throws CsvDataTypeMismatchException,
            CsvConstraintViolationException {
        String[] fields = value.substring(1, value.length() - 1).split(",");
        String current = fields[0].trim();
        String powerDraw = fields[1].trim();
        String type = fields[2].trim();
        String status = StringUtils.difference("Status: ", fields[3].trim());
        int count = Integer.parseInt(StringUtils.difference("Count: ", fields[4].trim()));

        return new Connector(type, powerDraw, status, current, count);
    }
}