package seng202.team3.gui;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.User;
import seng202.team3.logic.UserManager;

/**
 * Controller for the signup.fxml window
 * 
 * @author Celia Allen
 * @version 1.0.0, Sep 23
 */
public class LoginSignupController {

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
     * List of user input errors for adding user / logging in
     */
    private ArrayList<String> errors = new ArrayList<>();

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
            setIcon("login", "show");
        } else if (showPassSignup != null) {
            setIcon("signup", "show");
            setIcon("signupconf", "show");
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
            signupEmailField.setStyle("-fx-border-color: #ff0000;");
            fail = true;
        }
        if (signupUsernameField.getText().isEmpty()) {
            signupUsernameField.setStyle("-fx-border-color: #ff0000;");
            fail = true;
        }
        if (signupPasswordField.getText().length() < 4) {
            signupPasswordField.setStyle("-fx-border-color: #ff0000;");
            confPassField.setStyle("-fx-border-color: #ff0000;");
            fail = true;
        }
        if (!signupPasswordField.getText().equals(confPassField.getText())) {
            confPassField.setStyle("-fx-border-color: #ff0000;");
            fail = true;
        }
        if (fail) {
            invalidSignup.setVisible(true);
            return;
        }
        User user = new User();
        user.setAccountName(signupUsernameField.getText());
        user.setEmail(signupEmailField.getText());
        user.setCarbonSaved(0);
        user.setLevel(PermissionLevel.USER);
        try {
            manage.saveUser(user, signupPasswordField.getText());
            menuControl.setUser(user);
            stage.close();
        } catch (IOException e) {
            invalidSignup.setVisible(true);
        }
    }

    /**
     * Logs the user in
     */
    @FXML
    public void login() {
        try {
            User user = manage.login(loginEmailField.getText(), loginPasswordField.getText());
            if (user != null) {
                menuControl.setUser(user);
                stage.close();
            } else {
                loginPasswordField.clear();
                invalidLogin.setVisible(true);
            }
        } catch (SQLException | IOException e) {
            loginPasswordField.clear();
            invalidLogin.setVisible(true);
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
     * Allowing user to reset password
     */
    @FXML
    public void forgotPassword() {
        // TODO
    }


    /**
     * Closes this window.
     */
    public void close() {
        stage.close();
    }


    /**
     * Shows/hides the user's password
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
                popup = "login";
                break;
            case "showPassSignup":
                passTextField = showPassFieldSignup;
                passField = signupPasswordField;
                popup = "signup";
                break;
            case "showConfPassSignup":
                passTextField = showConfPassFieldSignup;
                passField = confPassField;
                popup = "signupconf";
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
     * @param popup the password field to add the icon to
     * @param type the type the icon should be
     */
    public void setIcon(String popup, String type) {
        Button button = new Button();
        if (popup.equals("login")) {
            button = showLoginPassword;
        } else if (popup.equals("signup")) {
            button = showPassSignup;
        } else if (popup.equals("signupconf")) {
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

}
