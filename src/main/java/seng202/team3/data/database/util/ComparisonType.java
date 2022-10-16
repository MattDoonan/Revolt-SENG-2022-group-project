package seng202.team3.data.database.util;

/**
 * Enumeration representing comparison types used in filters
 *
 * @author Harrison Tyson
 * @version 1.0.0, Aug 22
 */
public enum ComparisonType {
    /**
     * Check for field containing criteria
     */
    CONTAINS,
    /**
     * Check for field less then criteria
     */
    LESS_THAN,
    /**
     * Check for field less than or equal to criteria
     */
    LESS_THAN_EQUAL,
    /**
     * Check for field equal to criteria
     */
    EQUAL,
    /**
     * Check for field greater than or equal to criteria
     */
    GREATER_THAN_EQUAL,
    /**
     * Check for field greater than criteria
     */
    GREATER_THAN
}
