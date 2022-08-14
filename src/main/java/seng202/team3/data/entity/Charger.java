package seng202.team3.data.entity;

import java.util.ArrayList;

/**
 * Representation of chargers that users can find and connect vehicles to
 * 
 * @author Harrison Tyson
 * @version 1.0.0, Aug 22
 */
public class Charger {
    /** {@link Connector Connectors} available on charger */
    ArrayList<Connector> connectors = new ArrayList<Connector>();

    /** {@link Coordinate Coordinate} information for the charger */
    Coordinate location;

    /** Number of parks available at the charger */
    int availableParks;

    /** Maximum amount of time that can be spent at a charger */
    Double timeLimit;

    /** Business that manages the charger */
    String operator;

    /** Accessible to the public */
    boolean isPublic;

    /** Has tourist attraction nearby */
    boolean hasAttraction;

    /** Constructor for the Charger */
    public Charger(ArrayList<Connector> connectors, Coordinate location, int availableParks,
            Double timeLimit, String operator, boolean isPublic, boolean hasAttraction) {
        this.connectors = connectors;
        setLocation(location);
        setAvailableParks(availableParks);
        setTimeLimit(timeLimit);
        setOperator(operator);
        setPublic(isPublic);
        setHasAttraction(hasAttraction);
    }

    /**
     * Add a new {@link Connector connector} to the charger
     * 
     * @param connector Connector to add
     */
    public void addConnector(Connector connector) {
        connectors.add(connector);
    }

    /**
     * Remove a {@link Connector connector} from the charger
     * 
     * @param connector Connector to remove
     */
    public void removeConnector(Connector connector) {
        connectors.remove(connector);
    }

    /**
     * Get all {@link Connector connectors} on charger
     * 
     * @return list of attached connectors
     */
    public ArrayList<Connector> getConnectors() {
        return connectors;
    }

    /**
     * Get location of the charger
     * 
     * @return {@link Coordinate coordinates} of the charger
     */
    public Coordinate getLocation() {
        return location;
    }

    /**
     * Set location of the charger
     * 
     * @param location new location of charger
     */
    public void setLocation(Coordinate location) {
        this.location = location;
    }

    /**
     * Get number of parks available
     * 
     * @return total number of parks available
     */
    public int getAvailableParks() {
        return availableParks;
    }

    /**
     * Set number of parks available
     * 
     * @param availableParks new number of parks available
     */
    public void setAvailableParks(int availableParks) {
        this.availableParks = availableParks;
    }

    /**
     * Get the max time limit at the charger
     * 
     * @return max time that can be spent at charger
     */
    public Double getTimeLimit() {
        return timeLimit;
    }

    /**
     * Set the max time limit at the charger
     * 
     * @param timeLimit max time that can be spent at charger
     */
    public void setTimeLimit(Double timeLimit) {
        this.timeLimit = timeLimit;
    }

    /**
     * Get the operator of the charger
     * 
     * @return operator of the charger
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Set the operator of the charger
     * 
     * @param operator operator of the charger
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * Get the public availablity of the charger
     * 
     * @return public availablity of the charger
     */
    public boolean getPublic() {
        return isPublic;
    }

    /**
     * Set the public availablity of the charger
     * 
     * @param isPublic public availablity of the charger
     */
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    /**
     * Get has nearby tourist attractions
     * 
     * @return bool indicating has nearby tourist attractions
     */
    public boolean getHasAttraction() {
        return hasAttraction;
    }

    /**
     * Set has nearby tourist attractions
     * 
     * @param hasAttraction bool indicating has nearby tourist attractions
     */
    public void setHasAttraction(boolean hasAttraction) {
        this.hasAttraction = hasAttraction;
    }
}
