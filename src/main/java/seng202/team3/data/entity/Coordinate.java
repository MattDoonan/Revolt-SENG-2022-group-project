package seng202.team3.data.entity;

/**
 * Stores location information
 * 
 * @author Harrison Tyson
 * @version 1.0.0, Aug 22
 */
public class Coordinate {
    /** X-Coordinate on Waka-Kotahi map */
    Double xpos;

    /** Y-Coordinate on Waka-Kotahi map */
    Double ypos;

    /** Latitude coordinate */
    Double lat;

    /** Longitude coordinate */
    Double lon;

    /** Physical address */
    String address;

    /**
     * Constructor for Coordinate
     */
    public Coordinate(Double xpos, Double ypos, Double lat, Double lon, String address) {
        setXpos(xpos);
        setYpos(ypos);
        setLat(lat);
        setLon(lon);
        setAddress(address);
    }

    /**
     * Constructor for coordinate without address
     */
    public Coordinate(Double xpos, Double ypos, Double lat, Double lon) {
        this(xpos, ypos, lat, lon, "");
    }

    /**
     * Gets the Waka-Kotahi x coordinate
     * 
     * @return X position on the Waka-Kotahi map
     */
    public Double getXpos() {
        return xpos;
    }

    /**
     * Sets the Waka-Kotahi x coordinate
     * 
     * @param xpos new x position
     */
    public void setXpos(Double xpos) {
        this.xpos = xpos;
    }

    /**
     * Gets the Waka-Kotahi y coordinate
     * 
     * @return Y position on the Waka-Kotahi map
     */
    public Double getYpos() {
        return ypos;
    }

    /**
     * Sets the Waka-Kotahi y coordinate
     * 
     * @param ypos new y position
     */
    public void setYpos(Double ypos) {
        this.ypos = ypos;
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

}
