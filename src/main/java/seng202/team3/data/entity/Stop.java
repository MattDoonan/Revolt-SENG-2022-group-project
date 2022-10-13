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
     * Latitude of the stop
     */
    private Double lat;

    /**
     * Longitude of the stop
     */
    private Double lon;

    /**
     * Create a stop at a charger
     * 
     * @param charger charger object at the stop location
     */
    public Stop(Charger charger) {
        this.charger = charger;
        this.lat = charger.getLocation().getLat();
        this.lon = charger.getLocation().getLon();
    }

    /**
     * Create a stop at any point
     * 
     * @param lat latitude coordinate of the stop
     * @param lon longitude coordinate of the stop
     */
    public Stop(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
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
     * Gets the latitude of the stop
     * 
     * @return latitude of the stop
     */
    public Double getLat() {
        return this.lat;
    }

    /**
     * Sets the latitude of the stop
     * 
     * @param lat latitude of the stop
     */
    public void setLat(Double lat) {
        this.lat = lat;
    }

    /**
     * Gets the longitude of the stop
     * 
     * @return longitude of the stop
     */
    public Double getLon() {
        return this.lon;
    }

    /**
     * Sets the longitude of the stop
     * 
     * @param lon longitude of the stop
     */
    public void setLon(Double lon) {
        this.lon = lon;
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
            return s.getLat().equals(this.getLat())
                    && s.getLon().equals(this.getLon())
                    && s.getCharger().equals(this.getCharger());
        } else if (s.getCharger() == null
                && this.getCharger() == null) {
            return s.getLat().equals(this.getLat())
                    && s.getLon().equals(this.getLon());
        } else {
            return false;
        }

    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int result = (getId() ^ (getId() >>> 32));
        result = 31 * result + charger.hashCode();
        return result;
    }
}
