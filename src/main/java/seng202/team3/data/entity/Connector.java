package seng202.team3.data.entity;

/**
 * Represents different connectors attached to {@link Charger chargers}
 * 
 * @author Harrison Tyson
 * @version 1.0.0, Aug 22
 */
public class Connector {
    /** Plug style */
    String type;

    /** Type of current */
    String power;

    /** If it is able to be used */
    boolean isOperational;

    /** If it is currently in use */
    boolean inUse;

    /**
     * Constructor for the connector
     */
    public Connector(String type, String power, boolean isOperational, boolean inUse) {
        setType(type);
        setPower(power);
        setOperational(isOperational);
        setInUse(inUse);
    }

    /**
     * Gets the type of the connector
     * 
     * @return the type of the connector
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type of the connector
     * 
     * @param newType value for the type
     */
    public void setType(String newType) {
        type = newType;
    }

    /**
     * Gets the power of the connector
     * 
     * @return style of current the connector uses
     */
    public String getPower() {
        return power;
    }

    /**
     * Sets the power of the connector *
     * 
     * @param newPower value for the connector's power
     */
    public void setPower(String newPower) {
        power = newPower;
    }

    /**
     * Gets the operational status of the connector
     * 
     * @return boolean indicating operational status
     */
    public boolean isOperational() {
        return isOperational;
    }

    /**
     * Sets the operational status of the connector
     * 
     * @param newIsOperational new operational status
     */
    public void setOperational(boolean newIsOperational) {
        isOperational = newIsOperational;
    }

    /**
     * Gets the current use state of the connector
     * 
     * @return boolean indicating if the connector is being used
     */
    public boolean isInUse() {
        return inUse;
    }

    /**
     * Sets the current use state of the connector
     * 
     * @param newIsInUse new use state of the connector
     */
    public void setInUse(boolean newIsInUse) {
        inUse = newIsInUse;
    }

}
