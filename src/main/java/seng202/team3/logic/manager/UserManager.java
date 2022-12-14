package seng202.team3.logic.manager;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.database.util.ComparisonType;
import seng202.team3.data.database.util.QueryBuilderImpl;
import seng202.team3.data.entity.Entity;
import seng202.team3.data.entity.User;
import seng202.team3.data.entity.util.EntityType;
import seng202.team3.data.entity.util.PermissionLevel;

/**
 * Logic layer for the user Controller
 *
 * @author Celia Allen, Harrison Tyson
 * @version 1.0.0, Sep 23
 *
 */
public class UserManager {
    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

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
        // Unused
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
        logManager.info("User has been saved");
    }

    /**
     * Checks the email if its valid
     * From https://www.geeksforgeeks.org/sha-512-hash-in-java/
     * 
     * @param email the string of the email
     * @return the boolean of valid email.
     */
    public static boolean checkEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."
                + "[a-zA-Z0-9_+&*-]+)*@"
                + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
                + "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null) {
            return false;
        }
        return pat.matcher(email).matches();
    }

    /**
     * Updates a pre-existing user
     *
     * @param user the user to update
     * @throws IOException on sql fail
     */
    public void updateUser(User user) throws IOException {
        SqlInterpreter.getInstance().writeUser(user);
        setUser(getUserFromDatabase(user.getId()));
        logManager.info("User has been updated");
    }

    /**
     * Get a user from the database
     * 
     * @param id id of the user to get
     * @return the user
     * @throws IOException on sql fail
     */
    public User getUserFromDatabase(int id) throws IOException {
        List<Entity> usr = SqlInterpreter.getInstance().readData(
                new QueryBuilderImpl().withSource(EntityType.USER)
                        .withFilter("userid", "" + id, ComparisonType.EQUAL).build());
        return (User) usr.get(0);
    }

    /**
     * Hashes the user's password
     * From https://www.geeksforgeeks.org/sha-512-hash-in-java/
     * 
     * @param input the string to be encrypted
     * @return the encrypted string
     */
    public static String encryptThisString(String input) {
        try {
            // getInstance() method is called with algorithm SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            StringBuilder hashtext = new StringBuilder();
            hashtext.append(no.toString(16));
            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext.insert(0, '0');
            }

            // return the HashText
            return hashtext.toString();

        } catch (NoSuchAlgorithmException e) {
            // For specifying wrong message digest algorithms
            logManager.error(e.getMessage());
            return null;
        }
    }

    /**
     * Call Delete function to delete user from database
     */
    public static void deleteCurrentUser() {
        try {
            SqlInterpreter.getInstance().deleteData(EntityType.USER, getUser().getId());
            setUser(guest);
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }
    }

}
