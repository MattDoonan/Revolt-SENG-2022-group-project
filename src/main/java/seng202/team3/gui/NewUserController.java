package seng202.team3.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng202.team3.data.entity.PermissionLevel;
import seng202.team3.data.entity.User;
import seng202.team3.logic.AdminManager;



/**
 * The pop-up designed to add new user information on
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class NewUserController extends PopUpWindow {

    /**
     * Username textfield
     */
    @FXML
    private TextField username;

    /**
     * Email textfield
     */
    @FXML
    private TextField email;

    /**
     * the manager associated with this controller
     */
    private AdminManager manager;

    /**
     * String list of errors to display
     */
    private ArrayList<String> errors = new ArrayList<>();

    /**
     * Iniialises the new user controller
     */
    public NewUserController() {
        //unused
    }

    /**
     * Intialises the AdminManager with the controller
     *
     * @param manager the {@link AdminManager} associated
     */
    public void init(AdminManager manager) {
        this.manager = manager;
    }

    /**
     * {@inheritDoc}
     * Executes the correct action as needed as per controller
     */
    @Override
    @FXML
    public void confirm() {
        stage.setAlwaysOnTop(false);
        User user = new User();
        List<User> list = manager.getUserList().stream()
                .filter(oneUser -> (username.getText().equals(oneUser.getAccountName()))).toList();
        if (!username.getText().equals("")) {
            user.setAccountName(username.getText());
        } else if (list.size() != 0) {
            errors.add("Cannot be an existing username.");
        } else {
            errors.add("Need a username.");
        }
        if (!email.getText().equals("")) {
            String ptn = "^(\\w+[!#\\$%&'\\*\\+-\\/=\\?\\^_`{|\\.]?\\w+)+";
            ptn += "@(\\w+-?\\w+)+\\.(\\w+-?\\w+)+(\\.\\w+-?\\w+)?$";
            boolean matchFound = Pattern.matches(ptn, email.getText());
            if (!matchFound) {
                errors.add("Invalid email address.");
            } else {
                user.setEmail(email.getText());
            }
        } else {
            errors.add("Need an email address.");
        }
        if (errors.size() > 0) {
            launchErrorPopUps();
            errors.clear();
        } else {
            user.setLevel(PermissionLevel.USER);
            manager.addUser(user);
            stage.close();
        }
    }

    /**
     * Launches an error popup when trying to do illegal things
     */
    public void launchErrorPopUps() {
        try {
            stage.setAlwaysOnTop(false);
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

}
