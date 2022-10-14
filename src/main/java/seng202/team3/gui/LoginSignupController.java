package seng202.team3.gui;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.User;
import seng202.team3.logic.UserManager;

/**
 * Controller for the signup.fxml window
 * 
 * @author Celia Allen, Michelle Hsieh
 * @version 1.0.0, Sep 23
 */
public class LoginSignupController {
    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

    /**
     * The signup username field
     */
    @FXML
    private TextField signupUsernameField;

    /**
     * The signup email field
     */
    @FXML
    private TextField signupEmailField;

    /**
     * The signup password field
     */
    @FXML
    private PasswordField signupPasswordField;

    /**
     * The show password field
     */
    @FXML
    private TextField showPassFieldSignup;

    /**
     * The signup confirm password field
     */
    @FXML
    private PasswordField confPassField;

    /**
     * The show confirm password field
     */
    @FXML
    private TextField showConfPassFieldSignup;

    /**
     * The login email field
     */
    @FXML
    private TextField loginEmailField;

    /**
     * The login password field
     */
    @FXML
    private PasswordField loginPasswordField;

    /**
     * The show password field
     */
    @FXML
    private TextField showPassLogin;

    /**
     * Button to show login password
     */
    @FXML
    private Button showLoginPassword;

    /**
     * The current stage
     */
    private Stage stage;

    /**
     * The manager
     */
    private UserManager manage = new UserManager();

    /**
     * The border pane
     */
    private BorderPane pane;

    /**
     * Instance of the MenuController
     */
    private MenuController menuControl;

    /**
     * Invalid login error
     */
    @FXML
    private Label invalidLogin;

    /**
     * invalid signup error
     */
    @FXML
    private Label invalidSignup;

    /**
     * Styling for invalid fields
     */
    private static final String INVALID_STYLE = "-fx-border-color: #ff0000;";

    /**
     * Styling for valid fields
     */
    private static final String VALID_STYLE = "-fx-border-color: default;";

    /**
     * Manages all error tooltips
     */
    private ErrorHandler errors = new ErrorHandler();

    /**
     * Initialises the sign up
     */
    public LoginSignupController() {
        // Unused
        errors.add("confPassField", "Passwords must match.");
        errors.add("signupEmailField", "Email required.");
        errors.add("loginEmailField", "Username required.");
        errors.add("signupPasswordField", "Password must be more than 4 characters.");
        errors.add("loginPasswordField", "Password required.");
        errors.add("signupUsernameField", "Username required.");
    }

    /**
     * Sets the stage
     *
     * @param stage the stage to be set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Gets the stage
     *
     * @return the Stage to be gotten
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Sets the border pane as pane
     *
     * @param pane the BorderPane for the controller
     */
    public void setPane(BorderPane pane) {
        this.pane = pane;
    }

    /**
     * Initialises the sign up
     * 
     * @param menuControl the controller for the menu
     */
    public void init(MenuController menuControl) {
        this.menuControl = menuControl;
        if (showPassLogin != null) {
            setIcon("show");
        }

    }

    /**
     * Checks the given user details for errors.
     * If no errors, calls manager to save user to database
     */
    @FXML
    public void signUp() {
        Boolean fail = signUpErrorChecks();

        if (Boolean.TRUE.equals(fail)) {
            invalidSignup.setVisible(true);
            logManager.warn("Incorrect user details");
            return;
        }

        User user = new User();
        user.setAccountName(signupUsernameField.getText());
        user.setEmail(signupEmailField.getText());
        user.setCarbonSaved(0);
        user.setLevel(PermissionLevel.USER);
        try {
            String hashedPassword = encryptThisString(signupPasswordField.getText());
            manage.saveUser(user, hashedPassword);
            menuControl.setUser(user);
            stage.close();
            logManager.info("New user created");
        } catch (IOException e) {
            invalidSignup.setVisible(true);
            logManager.error(e.getMessage());
        }
    }

    /**
     * Checks the signup fields for errors, and displays messages to the user
     * if there are errors
     * 
     * @return whether there were any errors
     */
    public Boolean signUpErrorChecks() {

        errors.hideAll();

        signupEmailField.setStyle(VALID_STYLE);
        signupUsernameField.setStyle(VALID_STYLE);
        signupPasswordField.setStyle(VALID_STYLE);
        confPassField.setStyle(VALID_STYLE);

        Boolean fail = false;

        if (!UserManager.checkEmail(signupEmailField.getText())) {
            errors.changeMessage("signupEmailField", "Invalid email.");
            if (signupEmailField.getText().isEmpty()) {
                errors.changeMessage("signupEmailField", "Email Required.");
            }
            signupEmailField.setStyle(INVALID_STYLE);
            errors.show("signupEmailField");
            fail = true;
        }
        if (signupUsernameField.getText().isEmpty()) {
            signupUsernameField.setStyle(INVALID_STYLE);
            errors.show("signupUsernameField");
            fail = true;
        }
        if (signupPasswordField.getText().length() < 4) {
            errors.show("signupPasswordField");
            signupPasswordField.setStyle(INVALID_STYLE);
            fail = true;
        }
        if (signupPasswordField.getText().length() < 4
                && signupPasswordField.getText().length() > 0) {
            errors.show("signupPasswordField");
            signupPasswordField.setStyle(INVALID_STYLE);
            fail = true;
        }
        if (confPassField.getText().isEmpty()
                || !signupPasswordField.getText().equals(confPassField.getText())) {
            confPassField.setStyle(INVALID_STYLE);
            errors.show("confPassField");
            fail = true;
        }

        return fail;
    }

    /**
     * Logs the user in
     */
    @FXML
    public void login() {
        try {
            loginErrorChecks();
            String hashedPassword = encryptThisString(loginPasswordField.getText());
            User user = manage.login(loginEmailField.getText(), hashedPassword);

            if (user != null) {
                menuControl.setUser(user);
                stage.close();
                logManager.info("Logged in successfully");
            } else {
                loginPasswordField.clear();
                loginEmailField.clear();
                invalidLogin.setVisible(true);
                logManager.info("Username or password incorrect");
                loginEmailField.setStyle(INVALID_STYLE);
                loginPasswordField.setStyle(INVALID_STYLE);
            }
        } catch (SQLException | IOException e) {
            loginPasswordField.clear();
            invalidLogin.setVisible(true);
            logManager.error(e.getMessage());
        }
    }

    /**
     * Checks the login fields for errors, and displays messages to the user
     * if there are errors
     */
    public void loginErrorChecks() {
        errors.hideAll();

        loginEmailField.setStyle(VALID_STYLE);
        loginPasswordField.setStyle(VALID_STYLE);

        if (loginEmailField.getText().isEmpty()) {
            loginEmailField.setStyle(INVALID_STYLE);
            errors.show("loginEmailField");
        }
        if (loginPasswordField.getText().isEmpty()) {
            errors.show("loginPasswordField");
            loginPasswordField.setStyle(INVALID_STYLE);
        }
    }

    /**
     * Redirects the user to the login screen
     */
    @FXML
    public void loginRedirect() {
        menuControl.createLoginWindow("/fxml/login.fxml", "Login", pane, stage);
    }

    /**
     * Redirects the user to the signup screen
     */
    @FXML
    public void signUpRedirect() {
        menuControl.createLoginWindow("/fxml/signup.fxml", "Signup", pane, stage);
    }

    /**
     * Closes this window.
     */
    public void close() {
        stage.close();
        MainWindow.setController(MenuController.getController());
    }

    /**
     * Shows/hides the user's password
     * 
     * @param event the event that called the function
     */
    @FXML
    public void showPassword(ActionEvent event) {
        if (showPassLogin.isVisible()) {
            showPassLogin.setVisible(false);
            showPassLogin.setText(null);
            setIcon("show");
        } else if (!loginPasswordField.getText().equals("")) {
            showPassLogin.setVisible(true);
            showPassLogin.setText(loginPasswordField.getText());
            setIcon("hide");
        }
    }

    /**
     * Set the button icon
     * 
     * @param type the type the icon should be
     */
    public void setIcon(String type) {

        if (type.equals("show")) {
            GlyphsDude.setIcon(showLoginPassword, FontAwesomeIcon.EYE);
        } else {
            GlyphsDude.setIcon(showLoginPassword, FontAwesomeIcon.EYE_SLASH);
        }
    }

    /**
     * Logs in on enter
     *
     * @param e the event handler
     */
    @FXML
    public void onEnter(ActionEvent e) {
        login();
    }

    /**
     * Signs up on enter
     *
     * @param e the event handler
     */
    @FXML
    public void signing(ActionEvent e) {
        signUp();
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
     * Returns the error handler manager instance
     * 
     * @return error handler object
     */
    public ErrorHandler getErrors() {
        return errors;
    }

}
