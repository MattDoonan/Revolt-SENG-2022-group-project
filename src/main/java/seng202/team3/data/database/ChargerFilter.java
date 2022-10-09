package seng202.team3.data.database;

import com.opencsv.bean.BeanVerifier;
import com.opencsv.exceptions.CsvConstraintViolationException;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Entity;

/**
 * Filter class for filtering charger objects from csv
 * Filters are executed on opencsv beans as they are generated
 *
 * @author Harrison Tyson
 * @version 1.0.0, Aug 22
 */
public class ChargerFilter implements BeanVerifier<Entity> {

    /**
     * Field to filter by
     */
    private String field;

    /**
     * Criteria to compare field against
     */
    private String criteria;

    /**
     * Operator to use during comparison
     */
    private ComparisonType compareMethod;

    /**
     * Constructor for a charger filter object
     * 
     * @param field         field to filter by
     * @param criteria      criteria to compare field against
     * @param compareMethod operator to use during comparison
     */
    ChargerFilter(String field, String criteria, ComparisonType compareMethod) { //
        this.field = field.toLowerCase(); // convert to lower case for consistency in code
        this.criteria = criteria;
        this.compareMethod = compareMethod;
    }

    /** {@inheritDoc} */
    @Override
    public boolean verifyBean(Entity bean) throws CsvConstraintViolationException {
        Charger c = (Charger) bean; // Convert to charger object
        boolean result;

        switch (field) { // cases are all lower case to allow field argument to be case insensitive
            case "chargerid":
                result = compare(c.getId());
                break;
            case "carparkcount":
                result = compare(c.getAvailableParks());
                break;
            case "datefirstoperational":
                result = compare(c.getDateOpened());
                break;
            case "name":
                result = compare(c.getName());
                break;
            case "operator":
                result = compare(c.getOperator());
                break;
            case "owner":
                result = compare(c.getOwner());
                break;

            // Connectors are an arraylist so convert to stream for faster processing
            case "connectortype":
                result = c.getConnectors().stream().anyMatch(x -> compare(x.getType()));
                break;
            case "connectorcurrent":
                result = c.getConnectors().stream().anyMatch(x -> compare(x.getCurrent()));
                break;
            case "connectorpowerdraw":
                result = c.getConnectors().stream().anyMatch(x -> compare(x.getPower()));
                break;
            case "connectorstatus":
                result = c.getConnectors().stream().anyMatch(x -> compare(x.getStatus()));
                break;
            case "address":
                result = compare(c.getLocation().getAddress());
                break;
            case "x":
                result = compare(c.getLocation().getXpos());
                break;
            case "y":
                result = compare(c.getLocation().getYpos());
                break;
            case "latitude":
                result = compare(c.getLocation().getLat());
                break;
            case "longitude":
                result = compare(c.getLocation().getLon());
                break;
            case "maxtimelimit":
                result = compare(c.getTimeLimit());
                break;
            case "hastouristattraction":
                result = c.getHasAttraction() == Boolean.parseBoolean(criteria);
                break;
            case "is24hours":
                result = c.getAvailable24Hrs() == Boolean.parseBoolean(criteria);
                break;
            case "hascarparkcost":
                result = c.getParkingCost() == Boolean.parseBoolean(criteria);
                break;
            case "haschargingcost": // bool
                result = c.getChargeCost() == Boolean.parseBoolean(criteria);
                break;
            default:
                result = true;
                break;
        }
        return result;
    }

    /**
     * Compare value of a {@link field field} against the {@link criteria criteria}
     * using the
     * {@link ComparisonType ComparisonType}
     * 
     * @param fieldValue value to compare against
     * @return boolean indicating result of comparison
     */
    private boolean compare(Double fieldValue) {
        switch (compareMethod) {
            case EQUAL:
                return fieldValue == Double.parseDouble(criteria);
            case LESS_THAN:
                return fieldValue < Double.parseDouble(criteria);
            case LESS_THAN_EQUAL:
                return fieldValue <= Double.parseDouble(criteria);
            case GREATER_THAN_EQUAL:
                return fieldValue >= Double.parseDouble(criteria);
            case GREATER_THAN:
                return fieldValue > Double.parseDouble(criteria);
            default:
                throw new IllegalArgumentException(
                        "Cannot compare type Double using " + compareMethod.name());
        }
    }

    /**
     * Compare value of a {@link field field} against the {@link criteria criteria}
     * using the
     * {@link ComparisonType ComparisonType}
     * 
     * @see ChargerFilter#compare(Double)
     * @param fieldValue the value to compare against
     * @return boolean indicating result of comparison
     */
    private boolean compare(String fieldValue) {
        switch (compareMethod) {
            case EQUAL:
                return fieldValue.equals(criteria);
            case CONTAINS:
                return (fieldValue.toLowerCase()).contains(criteria.toLowerCase());
            default:
                throw new IllegalArgumentException(
                        "Cannot compare type String using " + compareMethod.name());

        }
    }

    /**
     * Compare value of a {@link field field} against the {@link criteria criteria}
     * using the {@link ComparisonType ComparisonType}
     * 
     * @see ChargerFilter#compare(Double)
     * @param fieldValue the value to compare against
     * @return boolean indicating result of comparison
     */
    private boolean compare(int fieldValue) {
        switch (compareMethod) {
            case EQUAL:
                return fieldValue == Integer.parseInt(criteria);
            case LESS_THAN:
                return fieldValue < Integer.parseInt(criteria);
            case LESS_THAN_EQUAL:
                return fieldValue <= Integer.parseInt(criteria);
            case GREATER_THAN_EQUAL:
                return fieldValue >= Integer.parseInt(criteria);
            case GREATER_THAN:
                return fieldValue > Integer.parseInt(criteria);
            default:
                throw new IllegalArgumentException(
                        "Cannot compare type int using " + compareMethod.name());
        }
    }

}
