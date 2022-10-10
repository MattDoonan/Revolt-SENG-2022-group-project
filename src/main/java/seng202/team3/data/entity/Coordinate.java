package seng202.team3.data.entity;

import com.opencsv.bean.CsvBindByName;

/**
 * Stores location information
 *
 * @author Harrison Tyson
 * @version 1.0.0, Aug 22
 */
public class Coordinate {
    /** Latitude coordinate */
    @CsvBindByName(column = "latitude", required = true)
    private Double lat;

    /** Longitude coordinate */
    @CsvBindByName(column = "longitude", required = true)
    private Double lon;

    /** Physical address */
    @CsvBindByName(column = "address", required = true)
    String address;

    /**
     * Constructor for Coordinate
     *
     * @param lat     latitude
     * @param lon     longitude
     * @param address physical address
     */
    public Coordinate(Double lat, Double lon, String address) {
        setLat(lat);
        setLon(lon);
        setAddress(address);
    }

    /**
     * Empty constructor for the CSV object builder
     */
    public Coordinate() {

    }

    /**
     * Constructor for coordinate without address
     *
     * @see #Coordinate(Double, Double, Double, Double, String)
     * @param lat a {@link java.lang.Double} object
     * @param lon a {@link java.lang.Double} object
     */
    public Coordinate(Double lat, Double lon) {
        this(lat, lon, "");
    }

    /**
     * Gets the real world latitude
     *
     * @return latitude coordinate
     */
    public Double getLat() {
        return lat;
    }

    /**
     * Sets the real world latitude
     *
     * @param lat new latitude
     */
    public void setLat(Double lat) {
        this.lat = lat;
    }

    /**
     * Gets the real world longitude
     *
     * @return longitude coordinate
     */
    public Double getLon() {
        return lon;
    }

    /**
     * Sets the real world longitude
     *
     * @param lon new longitude
     */
    public void setLon(Double lon) {
        this.lon = lon;
    }

    /**
     * Gets the physical address
     *
     * @return physical address of the coordinate
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the physical address
     *
     * @param address new address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        Coordinate c;
        if (o instanceof Coordinate) {
            c = (Coordinate) o;
        } else {
            return false;
        }

        return c.getLat().equals(this.getLat())
                && c.getLon().equals(this.getLon())
                && c.getAddress().equals(this.getAddress());
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int result = (1 ^ (1 >>> 32));
        result = 31 * result + lat.hashCode();
        result = 31 * result + lon.hashCode();
        result = 31 * result + address.hashCode();

        return result;
    }

}
