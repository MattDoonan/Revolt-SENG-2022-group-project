package seng202.team3.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
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
     * The signup confirm password field
     */
    @FXML
    private PasswordField confPassField;

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
     * List of user input errors for adding user / logging in
     */
    private ArrayList<String> errors = new ArrayList<>();

    /**
     * The manager
     */
    private UserManager manage = new UserManager();

    /**
     * Instance of the MenuController
     */
    private MenuController menuControl;

    /**
     * The password hashing function
     */
    private static Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(
            32, 64, 1, 15 * 1024, 2);

    /**
     * Initialises the sign up
     */
    public LoginSignupController() {
        // Unused
    }

    /**
     * Initialises the sign up
     *
     */
    public void init(MenuController menuControl) {
        this.menuControl = menuControl;
    }

    /**
     * Checks the given user details for errors.
     * If no errors, calls manager to save user to database
     */
    @FXML
    public void signUp() {
        User user = new User();

        if (signupUsernameField.getText().equals("")) {
            errors.add("Username required.");
        } else {
            user.setAccountName(signupUsernameField.getText());
        }
        if (signupEmailField.getText().equals("")) {
            errors.add("Email required.");
        } else {
            if (signupEmailField.getText() != null) {
                String ptn = "^(\\w+[!#\\$%&'\\*\\+-\\/=\\?\\^_`{|\\.]?\\w+)+";
                ptn += "@(\\w+-?\\w+)+\\.(\\w+-?\\w+)+(\\.\\w+-?\\w+)?$";
                boolean matchFound = Pattern.matches(ptn, signupEmailField.getText());
                if (!matchFound) {
                    errors.add("Invalid email address.");
                } else {
                    user.setEmail(signupEmailField.getText());
                }
            }
        }
        if (signupPasswordField.getText().equals("")) {
            errors.add("Password required.");
        } else {
            if (signupPasswordField.getText() != null) {
                String ptn = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[";
                ptn += "\\!@#&\\(\\)\\-\\[\\{\\}\\]:;',\\?\\/\\*~\\$\\^\\+\\=\\<\\>]).{8,20}$";
                // String ptn = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])";
                // ptn += ".{4,20}$";
                boolean matchFound = Pattern.matches(ptn, signupPasswordField.getText());
                if (!matchFound) {
                    String errorStr = "Password must contain one uppercase letter, one lowercase";
                    errorStr += " letter, one number and one special character.";
                    errors.add(errorStr);
                }
            }
        }
        if (confPassField.getText().equals("")) {
            errors.add("Confirm password required.");
        }
        if (!signupPasswordField.getText().equals(confPassField.getText())) {
            errors.add("Passwords must match.");
        }

        // var myPassword = "ThisIsMyPassword";
        // var encodedPassword = encoder.encode(myPassword);
        // System.out.println("encodedpassword: " + encodedPassword);
        // var validPassword = encoder.matches(myPassword, encodedPassword);
        // System.out.println("valid?: " + validPassword);

        user.setCarbonSaved(0);
        user.setLevel(PermissionLevel.USER);

        if (errors.isEmpty()) {
            var encodedPassword = encoder.encode(signupPasswordField.getText());
            manage.saveUser(user, encodedPassword);
        } else {
            launchErrorPopUps();
            errors.clear();
        }
    }

    /**
     * Logs the user in
     */
    @FXML
    public void login() {
        User user = new User();

        if (loginEmailField.getText().equals("")) {
            errors.add("Email required.");
        } else {
            user.setEmail(loginEmailField.getText());
        }
        if (loginPasswordField.getText().equals("")) {
            errors.add("Password required.");
        }

        if (errors.isEmpty()) {
            manage.login(user, loginPasswordField.getText());
        } else {
            launchErrorPopUps();
            errors.clear();
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
            Stage errorPopup = new Stage();
            errorPopup.setScene(modalScene);
            errorPopup.setResizable(false);
            errorPopup.setTitle("Error With:");
            errorPopup.initModality(Modality.WINDOW_MODAL);
            ErrorController controller = error.getController();
            controller.init();
            controller.setErrors(errors);
            controller.setPromptType("error");
            controller.displayErrors();
            errorPopup.setAlwaysOnTop(true);
            errorPopup.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Redirects the user to the login screen
     */
    @FXML
    public void loginRedirect() {
        Stage stage = (Stage) signupUsernameField.getScene().getWindow();
        stage.close();
        menuControl.loadLogin();
    }

    /**
     * Redirects the user to the signup screen
     */
    @FXML
    public void signUpRedirect() {
        Stage stage = (Stage) loginEmailField.getScene().getWindow();
        stage.close();
        menuControl.loadSignup();
    }

    /**
     * Allowing user to reset password
     */
    @FXML
    public void forgotPassword() {
        // TODO
    }

}
