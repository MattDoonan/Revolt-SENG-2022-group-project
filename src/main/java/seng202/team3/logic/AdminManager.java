package seng202.team3.logic;

import static seng202.team3.data.entity.PermissionLevel.ADMIN;
import static seng202.team3.data.entity.PermissionLevel.CHARGEROWNER;
import static seng202.team3.data.entity.PermissionLevel.USER;

import java.io.IOException;
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
 * The AdminManager stub
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
     * Adds a new user
     *
     * @param user the {@link User} which will be added
     */
    public void addUser(User user) {
        try {
            SqlInterpreter.getInstance().writeUser(user, "password");
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
            case ADMIN -> "Admin";
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
        switch (permission) {
            case "User":
                return USER;
            case "Charger Owner":
                return CHARGEROWNER;
            case "Administration":
                return ADMIN;
            default:
                return null;
        }
    }


}
