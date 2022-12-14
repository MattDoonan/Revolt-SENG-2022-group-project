package seng202.team3.data.entity;

import seng202.team3.data.entity.util.PermissionLevel;

/**
 * Stores information about Users
 *
 * @author Matthew Doonan
 * @version 1.0.0, Sep 2022
 */
public class User extends Entity {

    /** String for the email of the user */
    private String email;

    /** String of the account name */
    private String accountName;

    /** double for the carbon saved */
    private double carbonSaved;

    /** Permissions level as int */
    private PermissionLevel level;

    /**
     * Constructor for user class
     * 
     * @param email       String for the email
     * @param accountName String of the account name
     * @param level       the permission level
     */
    public User(String email, String accountName, PermissionLevel level) {
        this.email = email;
        this.accountName = accountName;
        this.level = level;
        this.carbonSaved = 0;
    }

    /**
     * Default constructor
     */
    public User() {
        // Unused
    }

    /**
     * Edits the current email of the user
     * 
     * @param update a string representing the email
     */
    public void setEmail(String update) {
        email = update;
    }

    /**
     * Returns the email
     * 
     * @return String of the users email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the account name
     * 
     * @param newName the new account name
     */
    public void setAccountName(String newName) {
        accountName = newName;
    }

    /**
     * Returns the account name
     * 
     * @return String of the account name
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * Adds to the users carbon saved stat
     * 
     * @param add double for the carbon saved
     */
    public void setCarbonSaved(double add) {
        carbonSaved += add;
    }

    /**
     * returns the users carbon savings
     * 
     * @return the carbon saved double
     */
    public double getCarbonSaved() {
        return carbonSaved;
    }

    /**
     * Sets the level of the users permissions
     * 
     * @param update the new user permissions
     */
    public void setLevel(PermissionLevel update) {
        this.level = update;
    }

    /**
     * Gets the level of the user permissions
     * 
     * @return integer of the user permissions
     */
    public PermissionLevel getLevel() {
        return level;
    }

    @Override
    public boolean equals(Object o) {
        User u;
        if (o instanceof User) {
            u = (User) o;
        } else {
            return false;
        }

        return u.getEmail().equals(this.getEmail())
                && u.getAccountName().equals(this.getAccountName())
                && u.getCarbonSaved() == this.getCarbonSaved()
                && u.getLevel().equals(this.getLevel());
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int result = (getId() ^ (getId() >>> 32));
        result = 31 * result + email.hashCode();
        result = 31 * result + accountName.hashCode();

        return result;
    }
}
