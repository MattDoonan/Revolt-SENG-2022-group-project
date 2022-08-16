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
    String current;

    /** Power draw */
    String powerDraw;

    /** Operational Status */
    String status;

    /**
     * Number attached to {@link Charger charger}
     */
    int count;

    /**
     * Constructor for the connector
     */
    public Connector(String type, String power, String status, String current, int count) {
        setType(type);
        setPower(power);
        setOperational(status);
        setCurrent(current);
        setCount(count);
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
        return powerDraw;
    }

    /**
     * Sets the power of the connector *
     * 
     * @param newPower value for the connector's power
     */
    public void setPower(String newPower) {
        powerDraw = newPower;
    }

    /**
     * Gets the operational status of the connector
     * 
     * @return operational status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the operational status of the connector
     * 
     * @param newStatus new operational status
     */
    public void setOperational(String newStatus) {
        status = newStatus;
    }

    /**
     * Gets the current of the connector
     * 
     * @return current used by connector
     */
    public String getCurrent() {
        return current;
    }

    /**
     * Sets the current of the connector
     * 
     * @param newCurrent new current of the connector
     */
    public void setCurrent(String newCurrent) {
        current = newCurrent;
    }

    /**
     * Gets the number of the connector attached to a {@link Charger charger}
     * 
     * 
     * @return number of connectors
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets the number of connectors attached to a {@link Charger charger}
     * 
     * @param newCount new number of connectors
     */
    public void setCount(int newCount) {
        count = newCount;
    }

}
