package seng202.team3.gui.controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.data.entity.User;
import seng202.team3.data.entity.util.PermissionLevel;
import seng202.team3.gui.controller.charger.TableController;
import seng202.team3.gui.util.ErrorHandler;
import seng202.team3.gui.util.PopUpWindow;
import seng202.team3.logic.manager.UserManager;

/**
 * Logic layer for the user Controller
 *
 * @author Angus Kirtlan, Matthew Doonan
 * @version 1.0.0, Sep 22
 *
 */
public class AccountController {
    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

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
    private Button confirmAccount;

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
     * Delete button
     */
    @FXML
    private Button delete;

    /**
     * Borderpane to implement chargers
     */
    @FXML
    private BorderPane chargerTable;

    /**
     * Boolean on if it is in administration view
     */
    private boolean isAdminView = false;

    /** the user manager */
    private UserManager manage = new UserManager();

    /** Table controller */
    private TableController controller;

    /**
     * Styling for invalid fields
     */
    private static final Border INVALID_STYLE = new Border(
            new BorderStroke(Color.RED, BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY, BorderWidths.DEFAULT));

    /**
     * Styling for invalid fields
     */
    private static final Border VALID_STYLE = new Border(
            new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY, BorderWidths.DEFAULT));

    /**
     * The controller
     */
    private MenuController mainController;

    /**
     * id for username node
     */
    private static final String NAME_NODE = "accountName";

    /**
     * id for email node
     */
    private static final String EMAIL_NODE = "accountEmail";

    /**
     * id for password node
     */
    private static final String PASSWORD_NODE = "accountPassword";

    /**
     * Stores all of the tooltips used for error messages
     */
    private ErrorHandler errors = new ErrorHandler();

    /**
     * Unused constructor
     */
    public AccountController() {
        // Unused
    }

    /**
     * Initialize the window by getting the current User's Data
     *
     * @param c the controller
     */
    public void init(MenuController c) {
        this.mainController = c;
        errors.add(NAME_NODE, "Invalid name.");
        errors.add(EMAIL_NODE, "Email cannot be empty.");
        errors.add(PASSWORD_NODE, "Password must be more than 4 characters.");
        User user = UserManager.getUser();
        populateText(user);
        setChargerTable();
        editAdmin.setText("Administration Edit Page");
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
                AdminController adminCtrl = editor.getController();
                adminCtrl.init(chargerTable);
                chargerTable.setTop(editorParent);
            } catch (IOException e) {
                logManager.error(e.getMessage());
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
                logManager.error(e.getMessage());
            }
        }
    }

    /**
     * Makes it so you can edit your own details on click
     */
    @FXML
    public void editDetails() {
        if (editAccountButton.getText().equals("Edit Account")) {
            editAccountButton.setText("Back");
            confirmAccount.setVisible(true);
            accountPassword.setVisible(true);
            if (UserManager.getUser().getLevel() != PermissionLevel.ADMIN) {
                delete.setVisible(true);
            }
            accountName.setEditable(true);
            accountEmail.setEditable(true);
            accountName.setStyle("-fx-border-color: #000000; -fx-background-color: #FFFFFF;");
            accountEmail.setStyle("-fx-border-color: #000000; -fx-background-color: #FFFFFF;");

        } else {
            editAccountButton.setText("Edit Account");
            confirmAccount.setVisible(false);
            accountPassword.setVisible(false);
            delete.setVisible(false);
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
    @FXML
    public void confirmChanges() {
        Boolean fail = checkUserDetails();

        if (Boolean.TRUE.equals(fail)) {
            logManager.warn("Incorrect user details");
            return;
        }

        User user = new User();
        user.setAccountName(accountName.getText());
        user.setEmail(accountEmail.getText());
        user.setCarbonSaved(UserManager.getUser().getCarbonSaved());
        user.setLevel(UserManager.getUser().getLevel());
        user.setId(UserManager.getUser().getId());

        try {
            if (accountPassword.getText().length() == 0) {
                manage.updateUser(user);
                UserManager.setUser(user);

            } else if (accountPassword.getText().length() < 4) {
                accountPassword.setBorder(INVALID_STYLE);
            } else {
                manage.saveUser(user, UserManager
                        .encryptThisString(accountPassword.getText()));
                UserManager.setUser(user);
                accountEmail.setBorder(VALID_STYLE);
                accountPassword.setBorder(VALID_STYLE);
                accountName.setBorder(VALID_STYLE);
                accountPassword.setText("");
            }
            editDetails();
        } catch (IOException e) {
            logManager.error(e.getMessage());
            accountPassword.setBorder(INVALID_STYLE);
            accountName.setBorder(INVALID_STYLE);
            accountEmail.setBorder(INVALID_STYLE);
        }
        tableRefresh();
        logManager.info("User information updated");
    }

    /**
     * Checks if the user's inputs have errors
     * 
     * @return whethere there are any errors in the user's details
     */
    public Boolean checkUserDetails() {
        errors.hideAll();

        accountName.setBorder(Border.EMPTY);
        accountEmail.setBorder(Border.EMPTY);
        accountPassword.setBorder(Border.EMPTY);

        Boolean fail = false;

        if (!UserManager.checkEmail(accountEmail.getText())) {
            errors.changeMessage(EMAIL_NODE, "Invalid email.");
            if (accountEmail.getText().isEmpty()) {
                errors.changeMessage(EMAIL_NODE, "Email cannot be empty.");
            }
            accountEmail.setBorder(INVALID_STYLE);
            errors.show(EMAIL_NODE);
            fail = true;
        } else {
            accountEmail.setBorder(VALID_STYLE);
        }
        if (accountName.getText().isEmpty()) {
            accountName.setBorder(INVALID_STYLE);
            errors.show(NAME_NODE);
            fail = true;
        } else if (accountName.getText().length() > 15) {
            errors.changeMessage(NAME_NODE, "Username cannot be longer than 15 characters.");
            accountName.setBorder(INVALID_STYLE);
            errors.show(NAME_NODE);
            fail = true;
        } else {
            accountName.setBorder(VALID_STYLE);
        }
        if (accountPassword.getText().length() < 4 && accountPassword.getText().length() > 0) {
            accountPassword.setBorder(INVALID_STYLE);
            errors.show(PASSWORD_NODE);
            fail = true;
        } else {
            accountPassword.setBorder(VALID_STYLE);
        }

        return fail;
    }

    /**
     * Called when delete account button pressed
     */
    @FXML
    public void deleteAccount() {
        loadPromptScreen("Are you sure you'd like to \n"
                + "delete your account?\n\n");
    }

    /**
     * Loads a generic prompt screen pop-up
     * {@link seng202.team3.gui.util.PopUpWindow}
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
                mainController.deleteUser();
            }
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }
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

    /**
     * Gets error handling object for tooltip messages
     * 
     * @return errorhandler with entry field tooltip alerts
     */
    public ErrorHandler getErrors() {
        return errors;
    }
}
