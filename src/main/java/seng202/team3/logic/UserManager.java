package seng202.team3.logic;

import java.io.IOException;
import seng202.team3.data.database.SqlInterpreter;
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
     * Active user
     */
    private static User user = null;

    /**
     * Initialize UserManager
     */
    public UserManager() {

    }

    /**
     * Tries to log the user in, checking if the user exists in the database
     * 
     * @param user     details to check the database with
     * @param password password to check
     */
    public void login(User user, String password) {
        try {
            if (SqlInterpreter.getInstance().validatePassword(user, password)) {
                UserManager.setUser(user);
                // TODO: display confirmation message
            } else {
                // TODO: display error message
            }
        } catch (IOException e) {
            e.printStackTrace(); // TODO: GUI to handle this error
        }
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
     * @param user     the user whose details need to be saved
     * @param password the new password for the user
     */
    public void saveUser(User user, String password) {
        try {
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
