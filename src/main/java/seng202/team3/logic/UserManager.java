package seng202.team3.logic;

import java.io.IOException;
import java.sql.SQLException;

import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.Query;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.User;

/**
 * Logic layer for the user Controller
 *
 * @author Celia Allen
 * @version 1.0.0, Sep 23
 *
 */
public class UserManager {

    /**
     * List of available vehicles
     */
    // TODO: Remove after login added and adding chargers has been restricted to
    // signed in
    private static User user = new User("", "guest", PermissionLevel.USER);

    /**
     * Initialize UserManager
     */
    public UserManager() {

    }

    /**
     * Tries to log the user in, checking if the user exists in the database
     * 
     * @param username details to check the database with
     * @param password password to check
     */
    public User login(String username, String password) throws IOException, SQLException {
        return SqlInterpreter.getInstance().validatePassword(username, password);
    }

    /**
     * Returns the user who just signed up / logged in
     *
     * @return user
     */
    public static User getUser() {
        user.setUserid(1); // TODO: remove this - currently maps all added chargers to admin
        return user;
    }

    /**
     * Sets the active user
     * 
     * @param user user to log in
     */
    public static void setUser(User user) {
        UserManager.user = user;
    }

    /**
     * Saves the given user (and any changes made) to the database
     * 
     * @param user     the user whose details need to be saved
     * @param password the new password for the user
     */
    public void saveUser(User user, String password) {
        try {
            System.out.println(password);
            SqlInterpreter.getInstance().writeUser(user, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates a pre-existing user
     *
     * @param user the user to update
     */
    public void updateUser(User user) {
        try {
            SqlInterpreter.getInstance().writeUser(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
