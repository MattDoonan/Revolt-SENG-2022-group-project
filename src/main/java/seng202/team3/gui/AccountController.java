package seng202.team3.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
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
    private Text accountName;

    /**
     * Text for account email
     */
    @FXML
    private Text accountEmail;
    
    /**
     * Initialize the window by getting the current User's Data
     *
     */
    public void init() {
        User user = UserManager.getUser();
        populateText(user);
    }

    /**
     * Populates the text fields
     * @param user User to get data from
     */
    private void populateText(User user) {
        accountName.setText(user.getAccountName());
        accountEmail.setText(user.getEmail());
    }
}
