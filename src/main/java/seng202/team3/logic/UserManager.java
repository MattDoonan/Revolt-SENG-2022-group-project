package seng202.team3.logic;

import java.io.IOException;
import seng202.team3.data.database.QueryBuilder;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
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
     * Query object to retrieve vehicles
     */
    private QueryBuilder userDataQuery;

    /**
     * List of available vehicles
     */
    private static User user = new User("", "Guest", PermissionLevel.USER);

    /**
     * Initialize UserManager
     */
    public UserManager() {

    }

    /**
     * Load the initial query
     */
    public void resetQuery() {
        userDataQuery = new QueryBuilderImpl().withSource("user");
    }

    /**
     * Create the vehicle list from the main Query
     *
     */
    public void getUserData() {
        // TODO: get user from password and email address
    }

    /**
     * Tries to log the user in, checking if the user exists in the database
     * 
     * @param user details to check the database with
     */
    public void login(User user) {
        // TODO
    }

    /**
     * Returns the user who just signed up / logged in
     *
     * @return user
     */
    public static User getUser() {
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
     * @param user the user whose details need to be saved
     */
    public void saveUser(User user) {
        // try {
        // SqlInterpreter.getInstance().writeUser(user);
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
    }

}
