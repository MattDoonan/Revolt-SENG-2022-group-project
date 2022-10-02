package seng202.team3.logic;

import static seng202.team3.data.entity.PermissionLevel.ADMIN;
import static seng202.team3.data.entity.PermissionLevel.CHARGEROWNER;
import static seng202.team3.data.entity.PermissionLevel.USER;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng202.team3.data.database.QueryBuilder;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.User;

/**
 * The AdminManager class which deals with administration of users
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class AdminManager {

    /**
     * The user who is the admin
     */
    private User admin;

    /**
     * The selected user
     */
    private User selectedUser;

    /**
     * The list of all users
     */
    private ObservableList<User> userList;

    /**
     * Initialises the admin manager
     */
    public AdminManager() {
    }

    /**
     * Sets the admin as the user
     *
     * @param user the user who is the admin
     */
    public void setAdmin(User user) {
        admin = user;
    }

    /**
     * Makes a list of all the users
     */
    public void makeUsers() {
        QueryBuilder mainDataQuery = new QueryBuilderImpl().withSource("user");
        try {
            List<User> userList = new ArrayList<>();
            for (Object o : SqlInterpreter.getInstance()
                    .readData(mainDataQuery.build(), User.class)) {
                userList.add((User) o);
            }

            this.userList = FXCollections
                    .observableList(userList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the admin of this class
     *
     * @return {@link User} the admin of this class
     */
    public User getAdmin() {
        return admin;
    }

    /**
     * Gets the UserList
     *
     * @return {@link User} a list of users
     */
    public ObservableList<User> getUserList() {
        return userList;
    }

    /**
     * Sets the selected user
     *
     * @param selectedUser the selectedUser
     */
    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    /**
     * Gets the selectedUser
     *
     * @return {@link User} the user being selected
     */
    public User getSelectedUser() {
        return selectedUser;
    }

    /**
     * Deletes the selected user
     */
    public void deleteUser() {
        try {
            SqlInterpreter.getInstance().deleteData("user", selectedUser.getUserid());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the selected user's permission
     *
     * @throws SQLException if not in database
     */
    public void updateUser() throws SQLException {
        try {
            if (selectedUser != admin) {
                SqlInterpreter.getInstance().writeUser(selectedUser);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Gets the permission as a string from the int value
     *
     * @param permission {@link seng202.team3.data.entity.PermissionLevel} requested
     * @return String of permission level
     */
    public String permissionString(PermissionLevel permission) {
        return switch (permission) {
            case USER -> "User";
            case CHARGEROWNER -> "Charger Owner";
            case ADMIN -> "Administration";
            default -> throw new IllegalStateException("No permisson allowed: " + permission);
        };
    }

    /**
     * Gets the permission level from the string value
     *
     * @param permission String of permission level requested
     * @return {@link PermissionLevel} of permission level
     */
    public PermissionLevel permissionLevel(String permission) {
        return switch (permission) {
            case "User" -> USER;
            case "Charger Owner" -> CHARGEROWNER;
            case "Administration" -> ADMIN;
            default -> null;
        };
    }


}
