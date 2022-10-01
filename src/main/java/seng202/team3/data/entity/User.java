package seng202.team3.data.entity;

/**
 * Stores information about Users
 *
 * @author Matthew Doonan
 * @version 1.0.0, Sep 2022
 */
public class User {

    /** users id in database */
    private int userid;

    /** String for the email of the user */
    private String email;

    /** String of the account name */
    private String accountName;

    /** double for the carbon saved */
    private double carbonSaved;

    /** Permissions level as int */
    private PermissionLevel level;

    /** The user's currently-selected vehicle */
    private Vehicle vehicle;

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
     * sets the user id to an int
     * 
     * @param id the user if to be set to
     */
    public void setUserid(int id) {
        userid = id;
    }

    /**
     * Gets the userid
     * 
     * @return integer for the user id
     */
    public int getUserid() {
        return userid;
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

        return u.getUserid() == this.getUserid()
                && u.getEmail().equals(this.getEmail())
                && u.getAccountName().equals(this.getAccountName())
                && u.getCarbonSaved() == this.getCarbonSaved()
                && u.getLevel().equals(this.getLevel());
    }
}
