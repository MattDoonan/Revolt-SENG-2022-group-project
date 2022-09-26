package seng202.team3.gui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.User;
import seng202.team3.logic.AdminManager;
import seng202.team3.logic.UserManager;


/**
 * An AdminController class that displays on the FXML
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class AdminController {

    /**
     * The Menu select part
     */
    @FXML
    private MenuButton menu;

    /**
     * The table in the admin controller
     */
    @FXML
    private TableView<User> table;

    /**
     * FXML for username column
     */
    @FXML
    private TableColumn<User, String> username;

    /**
     * FXML for email column
     */
    @FXML
    private TableColumn<User, String> email;

    /**
     * FXML for permissions column
     */
    @FXML
    private TableColumn<User, String> permissions;

    /**
     * The BorderPane to hold the object
     */
    private BorderPane border;

    /**
     * The {@link AdminManager} of this controller
     */
    private AdminManager manager;

    /**
     * String list of errors to display
     */
    private ArrayList<String> errors = new ArrayList<>();

    /**
     * To create the admin controller
     */
    public AdminController() {
    }

    /**
     * Initialises the borderpane and a new adminmanager
     *
     * @param border the BorderPane containing this class
     */
    public void init(BorderPane border) {
        this.border = border;
        manager = new AdminManager();
        manager.setAdmin(UserManager.getUser());
        updateTable();
    }

    /**
     * Updates the table of users
     */
    public void updateTable() {
        manager.makeUsers();
        table.getItems().clear();
        table.setItems(manager.getUserList());
        username.setCellValueFactory(
                user -> new ReadOnlyStringWrapper(user.getValue().getAccountName()));
        email.setCellValueFactory(
                user -> new ReadOnlyStringWrapper(user.getValue().getEmail()));
        permissions.setCellValueFactory(
                user -> new ReadOnlyStringWrapper(
                        manager.permissionString(user.getValue().getLevel())));
        table.refresh();
    }

    /**
     * Sets the item in the table to be the selected item
     */
    public void setSelectedUser() {
        manager.setSelectedUser(table.getSelectionModel().getSelectedItem());
    }

    /**
     * Loads a generic prompt screen pop-up {@link seng202.team3.gui.PopUpWindow}
     *
     * @param prompt a String of the instructions
     */
    public void loadPromptScreen(String prompt) {
        try {
            FXMLLoader popUp = new FXMLLoader(getClass().getResource(
                    "/fxml/generic_popup.fxml"));
            VBox root = popUp.load();
            Scene modalScene = new Scene(root);
            Stage modal = new Stage();
            modal.setScene(modalScene);
            modal.setResizable(false);
            modal.setTitle("Are you sure? ");
            modal.initModality(Modality.WINDOW_MODAL);
            PopUpWindow popController = popUp.getController();
            popController.addPrompt(prompt);
            modal.showAndWait();
            if (popController.getClicked()) {
                manager.deleteUser();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Adds a new user
     */
    @FXML
    public void addNewUser() {
        try {
            FXMLLoader popUp = new FXMLLoader(getClass().getResource(
                    "/fxml/add_user.fxml"));
            VBox root = popUp.load();
            Scene modalScene = new Scene(root);
            Stage modal = new Stage();
            modal.setScene(modalScene);
            modal.setResizable(false);
            modal.setTitle("Add User");
            modal.initModality(Modality.WINDOW_MODAL);
            NewUserController newController = popUp.getController();
            newController.init(manager);
            newController.addPrompt("Input new user details: ");
            modal.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            updateTable();
        }
    }

    /**
     * Launches an error popup when trying to do illegal things
     */
    public void launchErrorPopUps() {
        try {
            FXMLLoader error = new FXMLLoader(getClass().getResource(
                    "/fxml/error_popup.fxml"));
            AnchorPane base = error.load();
            Scene modalScene = new Scene(base);
            Stage modal = new Stage();
            modal.setScene(modalScene);
            modal.setResizable(false);
            modal.setTitle("Error With Users:");
            modal.initModality(Modality.WINDOW_MODAL);
            ErrorController errController = error.getController();
            errController.init();
            errController.setErrors(errors);
            errController.setPromptType("error");
            errController.displayErrors();
            modal.setAlwaysOnTop(true);
            modal.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a user
     */
    @FXML
    public void deleteUser() {
        setSelectedUser();
        if (manager.getSelectedUser() != null) {
            if (manager.getAdmin().getUserid() == manager.getSelectedUser().getUserid()) {
                errors.add("Cannot delete current user");
            }
        } else {
            errors.add("Please select a user");
        }
        if (!errors.isEmpty()) {
            launchErrorPopUps();
            errors.clear();
        } else {
            loadPromptScreen("Are you sure you'd like to \n"
                    + "delete this user?\n\n");
        }
        updateTable();
    }

    /**
     * Edits the permissions of a user
     */
    @FXML
    public void editPermissions() {
        setSelectedUser();
        if (manager.getSelectedUser() != null) {
            if (manager.getAdmin().getUserid() == manager.getSelectedUser().getUserid()) {
                errors.add("Cannot edit your own permissions!");
            } else if (menu.getText().equals("Select...")) {
                errors.add("Select a permission level.");
            }
        } else {
            errors.add("Please select a user.");
        }
        if (!errors.isEmpty()) {
            launchErrorPopUps();
            errors.clear();
        } else {
            manager.getSelectedUser().setLevel(manager.permissionLevel(menu.getText()));
            manager.addUser(manager.getSelectedUser());
        }
        updateTable();
    }

    /**
     * Updates your permissions menu
     */
    @FXML
    public void updateMenuUser() {
        setSelectedUser();
        updateMenu("User");
    }

    /**
     * Updates your permissions menu
     */
    @FXML
    public void updateMenuOwner() {
        setSelectedUser();
        updateMenu("Charger Owner");
    }

    /**
     * Updates your permissions menu
     */
    @FXML
    public void updateMenuAdmin() {
        setSelectedUser();
        updateMenu("Administration");
    }

    /**
     * Updates your permissions menu
     *
     * @param display the String to display
     */
    public void updateMenu(String display) {
        menu.setText(display);
        setSelectedUser();
    }

    /**
     * Goes back to account screen
     */
    @FXML
    public void goBack() {
        try {
            FXMLLoader accountLoader = new FXMLLoader(getClass()
                    .getResource("/fxml/account.fxml"));
            Parent accountViewParent = accountLoader.load();
            AccountController controller = accountLoader.getController();
            controller.init(border);
            border.setCenter(accountViewParent);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
