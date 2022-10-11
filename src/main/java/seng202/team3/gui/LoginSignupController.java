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
     * Button to show signup password
     */
    @FXML
    private Button showPassSignup;

    /**
     * Button to show signup password
     */
    @FXML
    private Button showConfPassSignup;

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

    /** Invalid login error */
    @FXML
    private Label invalidLogin;

    /** invalid signup error */
    @FXML
    private Label invalidSignup;

    /**
     * Styling for invalid fields
     */
    private static final String INVALID_STYLE = "-fx-border-color: #ff0000;";

    /**
     * Login label text
     */
    private static final String LOGIN_LABEL = "login";

    /**
     * Sign up label text
     */
    private static final String SIGNUP_LABEL = "signup";

    /**
     * Confirm label text
     */
    private static final String SIGNUP_CONFIRM_LABEL = "signupconf";

    /**
     * Initialises the sign up
     */
    public LoginSignupController() {
        // Unused
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
            setIcon(LOGIN_LABEL, "show");
        } else if (showPassSignup != null) {
            setIcon(SIGNUP_LABEL, "show");
            setIcon(SIGNUP_CONFIRM_LABEL, "show");
        }
    }

    /**
     * Checks the given user details for errors.
     * If no errors, calls manager to save user to database
     */
    @FXML
    public void signUp() {
        Boolean fail = false;
        if (!UserManager.checkEmail(signupEmailField.getText())) {
            signupEmailField.setStyle(INVALID_STYLE);
            fail = true;
        }
        if (signupUsernameField.getText().isEmpty()) {
            signupUsernameField.setStyle(INVALID_STYLE);
            fail = true;
        }
        if (signupPasswordField.getText().length() < 4) {
            signupPasswordField.setStyle(INVALID_STYLE);
            confPassField.setStyle(INVALID_STYLE);
            fail = true;
        }
        if (!signupPasswordField.getText().equals(confPassField.getText())) {
            confPassField.setStyle(INVALID_STYLE);
            fail = true;
        }
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
     * Logs the user in
     */
    @FXML
    public void login() {
        try {
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
            }
        } catch (SQLException | IOException e) {
            loginPasswordField.clear();
            invalidLogin.setVisible(true);
            logManager.error(e.getMessage());
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
        String source = ((Button) event.getSource()).getId();
        TextField passTextField = new TextField();
        PasswordField passField = new PasswordField();
        String popup = "";
        switch (source) {
            case "showLoginPassword":
                passTextField = showPassLogin;
                passField = loginPasswordField;
                popup = LOGIN_LABEL;
                break;
            case "showPassSignup":
                passTextField = showPassFieldSignup;
                passField = signupPasswordField;
                popup = SIGNUP_LABEL;
                break;
            case "showConfPassSignup":
                passTextField = showConfPassFieldSignup;
                passField = confPassField;
                popup = SIGNUP_CONFIRM_LABEL;
                break;
            default:
                break;
        }

        if (passTextField.isVisible()) {
            passTextField.setVisible(false);
            passTextField.setText(null);
            setIcon(popup, "show");
        } else if (!passField.getText().equals("")) {
            passTextField.setVisible(true);
            passTextField.setText(passField.getText());
            setIcon(popup, "hide");
        }
    }

    /**
     * Set the button icon
     * 
     * @param popup the password field to add the icon to
     * @param type  the type the icon should be
     */
    public void setIcon(String popup, String type) {
        Button button = new Button();
        if (popup.equals(LOGIN_LABEL)) {
            button = showLoginPassword;
        } else if (popup.equals(SIGNUP_LABEL)) {
            button = showPassSignup;
        } else if (popup.equals(SIGNUP_CONFIRM_LABEL)) {
            button = showConfPassSignup;
        }
        if (type.equals("show")) {
            GlyphsDude.setIcon(button, FontAwesomeIcon.EYE);
        } else {
            GlyphsDude.setIcon(button, FontAwesomeIcon.EYE_SLASH);
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

}
