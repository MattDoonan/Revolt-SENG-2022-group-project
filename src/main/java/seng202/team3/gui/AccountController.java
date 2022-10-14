package seng202.team3.gui;

import java.io.IOException;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.User;
import seng202.team3.logic.UserManager;

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
     * invalid account details error
     */
    @FXML
    private Label invalidUpdateAccount;

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
        new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));

    /**
     * Styling for valid fields
     */
    private static final Border VALID_STYLE = new Border(
        new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));

    /**
     * id for invalid username
     */
    private static final String NAME_ERROR = "accountNameError";

    /**
     * id for invalid email
     */
    private static final String EMAIL_ERROR = "accountEmailError";

    /**
     * id for invalid email
     */
    private static final String EMAIL_REQ_ERROR = "accountEmailReqError";

    /**
     * id for invalid password
     */
    private static final String PASSWORD_ERROR = "accountPassError";

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
     * @param border the BorderPane
     */
    public void init(BorderPane border) {
        errors.add("accountEmail", "Invalid email.");
        errors.add("accountName", "Username cannot be empty.");
        errors.add("accountPassword", "Password must be more than 4 characters.");
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
        String notImplementedYet = "Not implemented yet";
        accountName.setText(user.getAccountName());
        accountEmail.setText(user.getEmail());
        distanceTravelled.setText(notImplementedYet);
        numberStops.setText(notImplementedYet);
        carbonSaved.setText(notImplementedYet);
        mostViewed.setText(notImplementedYet);
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
        Boolean fail = checkUserDetails();

        if (Boolean.TRUE.equals(fail)) {
            invalidUpdateAccount.setVisible(true);
            logManager.warn("Incorrect user details");
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
                accountPassword.setBorder(INVALID_STYLE);
            } else {
                manage.saveUser(user, UserManager
                        .encryptThisString(accountPassword.getText()));
                UserManager.setUser(user);
            }
            editDetails();
        } catch (IOException | SQLException e) {
            logManager.error(e.getMessage());
            accountPassword.setBorder(INVALID_STYLE);
            accountName.setBorder(INVALID_STYLE);
            accountEmail.setBorder(INVALID_STYLE);
        }
        tableRefresh();
        logManager.info("User information updated");
        invalidUpdateAccount.setVisible(false);

    }

    /**
     * Checks if the user's inputs have errors
     * 
     * @return whethere there are any errors in the user's details
     */
    public Boolean checkUserDetails() {
        errors.hideAll();

        accountName.setBorder(VALID_STYLE);
        accountEmail.setBorder(VALID_STYLE);
        accountPassword.setBorder(VALID_STYLE);

        Boolean fail = false;

        if (!UserManager.checkEmail(accountEmail.getText())) {
            errors.changeMessage("accountEmail", "Invalid email.");
            if (accountEmail.getText().isEmpty()) {
                errors.changeMessage("accountEmail", "Email cannot be empty.");
            }
            accountEmail.setBorder(INVALID_STYLE);
            errors.show("accountEmail");
            fail = true;
        }
        if (accountName.getText().isEmpty()) {
            accountName.setBorder(INVALID_STYLE);
            errors.show("accountName");
            fail = true;
        }
        if (accountPassword.getText().length() < 4 && accountPassword.getText().length() > 0) {
            accountPassword.setBorder(INVALID_STYLE);
            errors.show("accountPassword");
            fail = true;
        }

        return fail;
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
