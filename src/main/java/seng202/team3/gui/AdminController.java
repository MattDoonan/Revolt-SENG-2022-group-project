package seng202.team3.gui;

import java.io.IOException;
import java.sql.SQLException;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

    /**
     * The Menu select part
     */
    @FXML
    private MenuButton menu;

    /**
     * FXML for update permissions button
     */
    @FXML
    private Button updatePermissions;

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
     * FXML for delete user button
     */
    @FXML
    private Button delete;

    /**
     * The BorderPane to hold the object
     */
    private BorderPane border;

    /**
     * The {@link AdminManager} of this controller
     */
    private AdminManager manager;

    /**
     * Handler for error message tooltips
     */
    private ErrorHandler errors = new ErrorHandler();

    /**
     * User select prompt
     */
    private static final String SELECT_USER = "Please select a user";

    /**
     * Delete Error ID
     */
    private static final String DELETE_ERROR = "deleteError";

    /**
     * Permissions Button error id
     */
    private static final String PERMISSIONS_ERROR = "permissionsEditError";

    /**
     * To create the admin controller
     */
    public AdminController() {
        // Unused
    }

    /**
     * Initialises the borderpane and a new adminmanager
     *
     * @param border the BorderPane containing this class
     */
    public void init(BorderPane border) {
        errors.add(DELETE_ERROR, SELECT_USER);
        errors.add("permissionsMenuError", "Select a permission level.");
        errors.add(PERMISSIONS_ERROR, SELECT_USER);
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
            modal.initModality(Modality.APPLICATION_MODAL);
            PopUpWindow popController = popUp.getController();
            popController.addPrompt(prompt);
            modal.showAndWait();
            if (Boolean.TRUE.equals(popController.getClicked())) {
                manager.deleteUser();
            }
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }
    }

    /**
     * Deletes a user
     */
    @FXML
    public void deleteUser() {
        errors.hideAll();
        setSelectedUser();

        Point2D pointDelete = delete.localToScene(0.0, 0.0);

        boolean errorOccured = false;
        if (manager.getSelectedUser() == null) {
            errors.changeMessage(DELETE_ERROR, SELECT_USER);
            errorOccured = true;
        } else if (manager.getAdmin().getUserid() == manager.getSelectedUser().getUserid()) {
            errors.changeMessage(DELETE_ERROR, "Cannot delete current user");
            errorOccured = true;
        }

        if (errorOccured) {
            errors.get(DELETE_ERROR).show(delete, pointDelete.getX()
                    + delete.getScene().getX()
                    + delete.getScene().getWindow().getX(),
                    pointDelete.getY() + delete.getScene().getY()
                            + delete.getScene().getWindow().getY()
                            + delete.getHeight());
        } else {
            loadPromptScreen("Are you sure you'd like to \n"
                    + "delete this user (and owned chargers)?\n\n");
        }
        updateTable();
    }

    /**
     * Updates the permissions of a user
     *
     * @throws SQLException if SQL error
     */
    @FXML
    public void editPermissions() throws SQLException {
        errors.hideAll();

        Point2D pointMenu = menu.localToScene(0.0, 0.0);
        Point2D pointUpdate = updatePermissions.localToScene(0.0, 0.0);

        setSelectedUser();

        boolean permissionsErr = false;
        if (manager.getSelectedUser() == null) {
            errors.changeMessage(PERMISSIONS_ERROR, SELECT_USER);
            permissionsErr = true;
        } else if (manager.getAdmin().getUserid() == manager.getSelectedUser().getUserid()) {
            errors.changeMessage(PERMISSIONS_ERROR, "Cannot edit your own permissions!");
            permissionsErr = true;
        }

        if (menu.getText().equals("Select...")) {
            permissionsErr = true;
            errors.get("permissionsMenuError").show(menu, pointMenu.getX()
                    + menu.getScene().getX()
                    + menu.getScene().getWindow().getX(),
                    pointMenu.getY() + menu.getScene().getY()
                            + menu.getScene().getWindow().getY()
                            + menu.getHeight());
        }

        if (permissionsErr) {
            errors.get(PERMISSIONS_ERROR).show(updatePermissions, pointUpdate.getX()
                    + updatePermissions.getScene().getX()
                    + updatePermissions.getScene().getWindow().getX(),
                    pointUpdate.getY() + updatePermissions.getScene().getY()
                            + updatePermissions.getScene().getWindow().getY()
                            + updatePermissions.getHeight());
        } else {
            manager.getSelectedUser().setLevel(manager.permissionLevel(menu.getText()));
            manager.updateUser();
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
            FXMLLoader mainScene = new FXMLLoader(getClass()
                    .getResource("/fxml/main_table.fxml"));
            Parent mainNode = mainScene.load();
            TableController controller = mainScene.getController();
            controller.init();
            border.setTop(mainNode);
            controller.setUser(UserManager.getUser());
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }

    }

    /**
     * Opens the charger table for the specific charger owner
     */
    @FXML
    public void setChargerTable() {
        try {
            FXMLLoader mainScene = new FXMLLoader(getClass()
                    .getResource("/fxml/main_table.fxml"));
            Parent mainNode = mainScene.load();
            TableController controller = mainScene.getController();
            controller.init();
            border.setTop(mainNode);
            controller.setUser(table.getSelectionModel().getSelectedItem());
            controller.populateTable();
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }
    }

    /**
     * Gets the {@link AdminManager} of the controller
     *
     * @return the AdminManager of this controller
     */
    public AdminManager getManager() {
        return manager;
    }

    /**
     * Gets error handler for the controller
     * 
     * @return the error handler for the controller
     */
    public ErrorHandler getErrors() {
        return errors;
    }
}
