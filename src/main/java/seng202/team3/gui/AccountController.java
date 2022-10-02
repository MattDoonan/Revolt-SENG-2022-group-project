package seng202.team3.gui;

import java.io.IOException;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.User;
import seng202.team3.logic.UserManager;

/**
 * Logic layer for the user Controller
 *
 * @author Angus Kirtlan
 * @version 1.0.0, Sep 22
 *
 */
public class AccountController {

    /**
     * Button for signout
     */
    @FXML
    private Button logoutButton;

    /**
     * Button to edit account
     */
    @FXML
    private Button editAccountButton;

    /**
     * Test for account name
     */
    @FXML
    private TextField accountName;

    /**
     * Text for account email
     */
    @FXML
    private TextField accountEmail;

    /**
     * Text for setting password
     */
    @FXML
    private TextField accountPassword;

    /**
     * Button to confirm changes to user info
     */
    @FXML
    private Button confirm;

    /**
     * distance travelled text
     */
    @FXML
    private Text distanceTravelled;

    /**
     * number of stops text
     */
    @FXML
    private Text numberStops;

    /**
     * carbon saved text
     */
    @FXML
    private Text carbonSaved;

    /**
     * most viewed text
     */
    @FXML
    private Text mostViewed;

    /**
     * EditAdmin button
     */
    @FXML
    private Button editAdmin;

    /**
     * Borderpane to implement chargers
     */
    @FXML
    private BorderPane chargerTable;

    /**
     * Boolean on if it is in administration view
     */
    private boolean isAdminView = false;

    /**
     * The borderpane containing this
     */
    private BorderPane border;

    /** the user manager */
    private UserManager manage = new UserManager();

    /** Table controller */
    private TableController controller;

    /**
     * Unused constructor
     */
    public AccountController() {
        // Unused
    }

    /**
     * Initialize the window by getting the current User's Data
     *
     * @param border the BorderPane
     */
    public void init(BorderPane border) {
        User user = UserManager.getUser();
        populateText(user);
        setChargerTable();
        editAdmin.setText("Administration Edit Page");
        this.border = border;
        if (user.getLevel() == PermissionLevel.ADMIN) {
            editAdmin.setVisible(true);
        }
    }

    /**
     * Populates the text fields
     * 
     * @param user User to get data from
     */
    private void populateText(User user) {
        accountName.setText(user.getAccountName());
        accountEmail.setText(user.getEmail());
        distanceTravelled.setText("Not implemented yet");
        numberStops.setText("Not implemented yet");
        carbonSaved.setText("Not implemented yet");
        mostViewed.setText("Not implemented yet");
    }

    /**
     * Loads the admin editing page
     */
    @FXML
    public void adminEditing() {
        if (UserManager.getUser().getLevel() == PermissionLevel.ADMIN && !isAdminView) {
            editAdmin.setText("View All Chargers");
            isAdminView = true;
            try {
                FXMLLoader editor = new FXMLLoader(getClass()
                        .getResource("/fxml/admin_page.fxml"));
                Parent editorParent = editor.load();
                AdminController controller = editor.getController();
                controller.init(chargerTable);
                chargerTable.setTop(editorParent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            setChargerTable();
            isAdminView = false;
            editAdmin.setText("Administration Edit Page");
        }
    }

    /**
     * Opens the charger table for charger owners and admins
     */
    public void setChargerTable() {
        if (UserManager.getUser().getLevel() == PermissionLevel.ADMIN
                || UserManager.getUser().getLevel() == PermissionLevel.CHARGEROWNER) {
            try {
                FXMLLoader mainScene = new FXMLLoader(getClass()
                        .getResource("/fxml/main_table.fxml"));
                Parent mainNode = mainScene.load();
                controller = mainScene.getController();
                controller.init();
                chargerTable.setTop(mainNode);
                controller.setUser(UserManager.getUser());
                controller.populateTable();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Makes it so you can edit your own details on click
     */
    public void editDetails() {
        if (editAccountButton.getText().equals("Edit Account")) {
            editAccountButton.setText("Back");
            confirm.setVisible(true);
            accountPassword.setVisible(true);
            accountName.setEditable(true);
            accountEmail.setEditable(true);
            accountName.setStyle("-fx-border-color: #000000; -fx-background-color: #FFFFFF;");
            accountEmail.setStyle("-fx-border-color: #000000; -fx-background-color: #FFFFFF;");

        } else {
            editAccountButton.setText("Edit Account");
            confirm.setVisible(false);
            accountPassword.setVisible(false);
            accountName.setEditable(false);
            accountEmail.setEditable(false);
            accountName.setStyle("-fx-border-color: transparent; "
                    + "-fx-background-color: transparent;");
            accountEmail.setStyle("-fx-border-color: transparent; "
                    + "-fx-background-color: transparent;");
            populateText(UserManager.getUser());
        }
    }

    /**
     * Confirms the changes made from the user
     */
    public void confirmChanges() {
        Boolean fail = false;
        if (!UserManager.checkEmail(accountEmail.getText())) {
            accountEmail.setStyle("-fx-border-color: #ff0000;");
            fail = true;
        }
        if (accountName.getText().isEmpty()) {
            accountName.setStyle("-fx-border-color: #ff0000;");
            fail = true;
        }
        if (fail) {
            return;
        }
        User user = new User();
        user.setAccountName(accountName.getText());
        user.setEmail(accountEmail.getText());
        user.setCarbonSaved(UserManager.getUser().getCarbonSaved());
        user.setLevel(UserManager.getUser().getLevel());
        user.setUserid(UserManager.getUser().getUserid());

        try {
            if (accountPassword.getText().length() == 0) {
                manage.updateUser(user);
                UserManager.setUser(user);

            } else if (accountPassword.getText().length() < 4) {
                accountPassword.setStyle("-fx-border-color: #ff0000;");
            } else {
                manage.saveUser(user, accountPassword.getText());
                UserManager.setUser(user);
            }
            editDetails();
        } catch (IOException | SQLException e) {
            accountPassword.setStyle("-fx-border-color: #ff0000;");
            accountName.setStyle("-fx-border-color: #ff0000;");
            accountEmail.setStyle("-fx-border-color: #ff0000;");
        }
        tableRefresh();
    }

    /**
     * Calls table refresh if the table exists
     */
    public void tableRefresh() {
        if (UserManager.getUser().getLevel() == PermissionLevel.ADMIN
                || UserManager.getUser().getLevel() == PermissionLevel.CHARGEROWNER) {
            controller.refreshTable();
        }
    }
}
