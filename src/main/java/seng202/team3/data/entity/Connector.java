package seng202.team3.data.entity;

/**
 * Represents different connectors attached to {@link Charger chargers}
 *
 * @author Harrison Tyson
 * @version 1.0.0, Aug 22
 */
public class Connector extends Entity {

    /** Plug style */
    private String type;

    /** Type of current */
    private String current;

    /** Power draw */
    private String powerDraw;

    /** Operational Status */
    private String status;

    /**
     * Number attached to {@link Charger charger}
     */
    private int count;

    /**
     * Constructor for the connector
     *
     * @param type    type of the connector
     * @param power   power draw of the connector
     * @param status  operative status of the connector
     * @param current current type of the connector
     * @param count   number of the connector attached
     */
    public Connector(String type, String power, String status, String current, int count) {
        setType(type);
        setPower(power);
        setOperational(status);
        setCurrent(current);
        setCount(count);
    }

    /**
     * Constructor for the connector with id
     *
     * @see #Connector(String, String, String, String, int)
     * @param type    a {@link java.lang.String} object
     * @param power   a {@link java.lang.String} object
     * @param status  a {@link java.lang.String} object
     * @param current a {@link java.lang.String} object
     * @param count   a int
     * @param id      a int
     */
    public Connector(String type, String power, String status, String current, int count, int id) {
        super(id);
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

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        Connector c;
        if (o instanceof Connector) {
            c = (Connector) o;
        } else {
            return false;
        }

        return c.getType().equals(this.getType())
                && c.getPower().equals(this.getPower())
                && c.getStatus().equals(this.getStatus())
                && c.getCurrent().equals(this.getCurrent());
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int result = (getId() ^ (getId() >>> 32));
        result = 31 * result + type.hashCode();
        result = 31 * result + current.hashCode();
        result = 31 * result + powerDraw.hashCode();
        result = 31 * result + status.hashCode();

        return result;
    }
}
