package seng202.team3.data.entity;

/**
 * Abstraction for entities that are stored
 * 
 * @author Harrison Tyson
 * @version 1.0.0. Oct 22
 */
public class Entity {
    /**
     * Unique identifier
     */
    private int id;

    /**
     * Initializes without a default id
     */
    public Entity() {
        // Empty for children that are initialized without ids
    }

    /**
     * Initializes instance with an id
     * 
     * @param id unique identification number
     */
    public Entity(int id) {
        setId(id);
    }

    /**
     * Gets the id of the entity
     * 
     * @return the unique id number
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the entity
     * 
     * @param id the unique id number
     */
    public void setId(int id) {
        this.id = id;
    }

}