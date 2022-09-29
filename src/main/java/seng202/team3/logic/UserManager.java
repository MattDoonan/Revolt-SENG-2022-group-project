package seng202.team3.logic;

import java.io.IOException;
import java.sql.SQLException;
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
     * Guest user
     */
    private static User guest = new User("guest", "guest@gmail.com", PermissionLevel.GUEST);


    /**
     * Active user
     */
    private static User user = guest;

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
     * @return the user object null if fails
     * @throws IOException  if database layer fails
     * @throws SQLException if database layer fails
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
     * Gets the guest
     *
     * @return {@link User} the guest user
     */
    public static User getGuest() {
        return guest;
    }

    /**
     * Saves the given user (and any changes made) to the database
     * 
     * @param user     the user whose details need to be saved
     * @param password the new password for the user
     * @throws IOException if the database fails
     */
    public void saveUser(User user, String password) throws IOException {
        SqlInterpreter.getInstance().writeUser(user, password);
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
