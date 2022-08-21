package seng202.team3.data.entity;

import java.util.ArrayList;

/**
 * Representation of chargers that users can find and connect vehicles to
 * 
 * @author Harrison Tyson
 * @version 1.0.1, Aug 22
 */
public class Charger {
    /** Unique identifier */
    int chargerId;

    /** When the charger was first available */
    String dateOpened;

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

    /** Business that owns the charger */
    String owner;

    /** Accessible to the public */
    boolean isPublic;

    /** Has tourist attraction nearby */
    boolean hasAttraction;

    /** Can be accessed 24 hours in the day */
    boolean is24Hrs;

    /** Has cost for parking */
    boolean hasParkingCost;

    /** Has cost for charging */
    boolean hasChargeCost;

    /** Has warning for charger high cost */
    boolean warningHighCost;

    /** Has warning for charger long wait time */
    boolean warningLongWait;

    /** Has warning for charger low availabilty */
    boolean warningLowAvailability;    

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
     * Get unique identifier for the charger
     * 
     * @return unique identification number
     */
    public int getChargerId() {
        return chargerId;
    }

    /**
     * Set unique identifier for the charger
     * 
     * @param chargerId unique identifier for the charger
     */
    public void setChargerId(int chargerId) {
        this.chargerId = chargerId;
    }

    /**
     * Get the date the charger was first opened
     * 
     * @return date first opened
     */
    public String getDateOpened() {
        return dateOpened;
    }

    /**
     * Set the date the charger was first opened
     * 
     * @param dateOpened date first opened
     */
    public void setDateOpened(String dateOpened) {
        this.dateOpened = dateOpened;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Get the public availability of the charger
     * 
     * @return public availability of the charger
     */
    public boolean getPublic() {
        return isPublic;
    }

    /**
     * Set the public availability of the charger
     * 
     * @param isPublic public availability of the charger
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

    /**
     * Get all day availability
     * 
     * @return bool indicating all day availability
     */
    public boolean getAvailable24Hrs() {
        return is24Hrs;
    }

    /**
     * Set all day availability
     * 
     * @param available24Hrs bool indicating all day availability
     */
    public void setAvailable24Hrs(boolean available24Hrs) {
        is24Hrs = available24Hrs;
    }

    /**
     * Get has a parking cost
     * 
     * @return bool indicating parking cost
     */
    public boolean getParkingCost() {
        return hasParkingCost;
    }

    /**
     * Set has a parking cost
     * 
     * @param parkingCost bool indicating parking cost
     */
    public void setParkingCost(boolean parkingCost) {
        hasParkingCost = parkingCost;
    }

    /**
     * Get has charge cost
     * 
     * @return bool indicating charging cost
     */
    public boolean getChargeCost() {
        return hasChargeCost;
    }

    /**
     * Set has charger cost
     * 
     * @param chargeCost bool indicating charging cost
     */
    public void setChargeCost(boolean chargeCost) {
        hasChargeCost = chargeCost;
    }

    /**
     * Set warning high cost
     * 
     * @param warningHighCost bool indicating high cost warning
     */
    public void setWarningHighCost(boolean warningHighCost) {
        this.warningHighCost = warningHighCost;
    }

    /**
     * Set warning high cost
     * 
     * @param warningLongWait bool indicating long wait warning
     */
    public void setWarningLongWait(boolean warningLongWait) {
        this.warningLongWait = warningLongWait;
    }
    
    /**
     * Set warning high cost
     * 
     * @param warningLowAvailability bool indicating low availability warning
     */
    public void setWarningLowAvailability(boolean warningLowAvailability) {
        this.warningLowAvailability = warningLowAvailability;
    }

    /**
     * Gets boolean warnings that are set to true
     * 
     * @return ArrayList of boolean warnings
     */
    public ArrayList<String> getWarnings() {
        ArrayList<String> warnings = new ArrayList<String>();
        if (this.warningHighCost) {
            warnings.add("high cost");
        }
        if (this.warningLongWait) {
            warnings.add("long wait");
        }
        if (this.warningLowAvailability) {
            warnings.add("low availability");
        }
        return warnings;
    }
}
