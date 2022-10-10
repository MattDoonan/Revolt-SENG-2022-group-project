package seng202.team3.data.entity;

/**
 * Enumeration for different objects that are storable
 * Acts as an interface between data and logic layers
 * 
 * @author Harrison Tyson
 * @version 1.0.0, Oct 22
 */
public enum EntityType {
    /**
     * Charger entity type
     */
    CHARGER,

    /**
     * Conenctor entity type
     */
    CONNECTOR,

    /**
     * Journey entity type
     */
    JOURNEY,

    /**
     * User entity type
     */
    USER,

    /**
     * Vehicle entity type
     */
    VEHICLE,

    /**
     * Stop entity type
     */
    STOP;

    /**
     * Gets the database table mapped to by the enum
     * 
     * @return string name of the database table
     */
    public String getAsDatabase() {
        switch (this) {
            case CHARGER:
                return "charger";
            case CONNECTOR:
                return "connector";
            case JOURNEY:
                return "journey";
            case USER:
                return "user";
            case VEHICLE:
                return "vehicle";
            case STOP:
                return "stop";
            default:
                return null;
        }

    }

    /**
     * Gets the csv name for the file storing the entity
     * 
     * @return the csv file name
     */
    public String getAsCsv() {
        switch (this) {
            case CHARGER:
                return "/charger";
            case CONNECTOR:
                return null; // not implemented
            case JOURNEY:
                return null; // not implemented
            case USER:
                return null; // not implemented
            case VEHICLE:
                return null; // not implemented
            case STOP:
                return null; // not implemented
            default:
                return null;
        }

    }

    /**
     * Gets the object mapped to the enum
     * 
     * @return Class object mapped to enum
     */
    public Class<? extends Entity> getAsClass() {
        switch (name()) {
            case "CHARGER":
                return Charger.class;
            case "CONNECTOR":
                return Connector.class;
            case "JOURNEY":
                return Journey.class;
            case "USER":
                return User.class;
            case "VEHICLE":
                return Vehicle.class;
            default:
                return null;
        }
    }
}
