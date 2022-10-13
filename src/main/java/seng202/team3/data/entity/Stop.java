package seng202.team3.data.entity;

/**
 * Used to represent stops within a Journey
 * 
 * @author Harrison Tyson
 * @version 1.0.0, Oct 22
 */
public class Stop extends Entity {
    /**
     * Charger at the stop
     */
    private Charger charger = null;

    /**
     * Coordinate of the stop
     */
    private Coordinate location;

    /**
     * Create a stop at a charger
     * 
     * @param charger charger object at the stop location
     */
    public Stop(Charger charger) {
        this.charger = charger;
        this.location = charger.getLocation();
    }

    /**
     * Create a stop at any point
     * 
     * @param position position of the stop
     */
    public Stop(Coordinate position) {
        this.location = position;
    }

    /**
     * Gets the charger object at the stop location
     * 
     * @return charger at the stop, null if not found
     */
    public Charger getCharger() {
        return charger;
    }

    /**
     * Sets the charger at the stop
     * 
     * @param charger charger to add to the stop
     */
    public void setCharger(Charger charger) {
        this.charger = charger;
    }

    /**
     * Gets the coordinate of the stop
     * 
     * @return coordinate of the stop
     */
    public Coordinate getLocation() {
        return this.location;
    }

    /**
     * Sets the coordinate of the stop
     * 
     * @param pos coordinate of the stop
     */
    public void setLocation(Coordinate pos) {
        this.location = pos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        Stop s;
        if (o instanceof Stop) {
            s = (Stop) o;
        } else {
            return false;
        }
        if (s.getCharger() != null
                && this.getCharger() != null) {
            return s.getLocation().equals(this.getLocation())
                    && s.getCharger().equals(this.getCharger());
        } else if (s.getCharger() == null
                && this.getCharger() == null) {
            return s.getLocation().equals(this.getLocation());
        } else {
            return false;
        }

    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int result = (getId() ^ (getId() >>> 32));
        result = 31 * result + location.hashCode();
        if (charger != null) {
            result = 31*result + charger.hashCode();
        }
        return result;
    }
}
