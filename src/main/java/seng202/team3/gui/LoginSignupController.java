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
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
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
    private static final Border INVALID_STYLE = new Border(
        new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, 
            CornerRadii.EMPTY, BorderWidths.DEFAULT));

    /**
     * Styling for valid fields
     */
    private static final Border VALID_STYLE = new Border(
        new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, 
            CornerRadii.EMPTY, BorderWidths.DEFAULT));

    /**
     * id for login email node
     */
    private static final String LOGIN_EMAIL_NODE = "loginEmailField";

    /**
     * id for login password node
     */
    private static final String LOGIN_PASS_NODE = "loginPasswordField";

    /**
     * id for signup confirm password node
     */
    private static final String CONF_PASS_NODE = "confPassField";

    /**
     * id for signup password node
     */
    private static final String SIGNUP_PASS_NODE = "signupPasswordField";

    /**
     * id for signup email node
     */
    private static final String SIGNUP_EMAIL_NODE = "signupEmailField";

    /**
     * id for signup username node
     */
    private static final String SIGNUP_NAME_NODE = "signupUsernameField";

    /**
     * Manages all error tooltips
     */
    private ErrorHandler errors = new ErrorHandler();

    /**
     * Initialises the sign up
     */
    public LoginSignupController() {
        // Unused
        errors.add(CONF_PASS_NODE, "Passwords must match.");
        errors.add(SIGNUP_EMAIL_NODE, "Email required.");
        errors.add(LOGIN_EMAIL_NODE, "Username required.");
        errors.add(SIGNUP_PASS_NODE, "Password must be more than 4 characters.");
        errors.add(LOGIN_PASS_NODE, "Password required.");
        errors.add(SIGNUP_NAME_NODE, "Username required.");
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

        signupEmailField.setBorder(VALID_STYLE);
        signupUsernameField.setBorder(VALID_STYLE);
        signupPasswordField.setBorder(VALID_STYLE);
        confPassField.setBorder(VALID_STYLE);

        Boolean fail = false;

        if (!UserManager.checkEmail(signupEmailField.getText())) {
            errors.changeMessage(SIGNUP_EMAIL_NODE, "Invalid email.");
            if (signupEmailField.getText().isEmpty()) {
                errors.changeMessage(SIGNUP_EMAIL_NODE, "Email Required.");
            } 
            signupEmailField.setBorder(INVALID_STYLE);
            errors.show(SIGNUP_EMAIL_NODE);
            fail = true;
        }
        if (signupUsernameField.getText().isEmpty()) {
            signupUsernameField.setBorder(INVALID_STYLE);
            errors.show(SIGNUP_NAME_NODE);
            fail = true;
        } else if (signupUsernameField.getText().length() > 15) {
            errors.changeMessage(SIGNUP_NAME_NODE, "Username cannot be longer than 15 characters.");
            signupUsernameField.setBorder(INVALID_STYLE);
            errors.show(SIGNUP_NAME_NODE);
            fail = true;
        }
        if (signupPasswordField.getText().length() < 4) {
            errors.show(SIGNUP_PASS_NODE);
            signupPasswordField.setBorder(INVALID_STYLE);
            fail = true;
        }
        if (signupPasswordField.getText().length() < 4
                && signupPasswordField.getText().length() > 0) {
            errors.show(SIGNUP_PASS_NODE);
            signupPasswordField.setBorder(INVALID_STYLE);
            fail = true;
        }
        if (confPassField.getText().isEmpty()
                || !signupPasswordField.getText().equals(confPassField.getText())) {
            confPassField.setBorder(INVALID_STYLE);
            errors.show(CONF_PASS_NODE);
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
                loginEmailField.setBorder(INVALID_STYLE);
                loginPasswordField.setBorder(INVALID_STYLE);
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

        loginEmailField.setBorder(VALID_STYLE);
        loginPasswordField.setBorder(VALID_STYLE);

        if (loginEmailField.getText().isEmpty()) {
            loginEmailField.setBorder(INVALID_STYLE);
            errors.show(LOGIN_EMAIL_NODE);
        }
        if (loginPasswordField.getText().isEmpty()) {
            errors.show(LOGIN_PASS_NODE);
            loginPasswordField.setBorder(INVALID_STYLE);
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
