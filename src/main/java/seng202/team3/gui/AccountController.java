package seng202.team3.gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
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
    private Text accountName;

    /**
     * Text for account email
     */
    @FXML
    private Text accountEmail;

    /**
     * EditAdmin button
     */
    @FXML
    private Button editAdmin;

    /**
     * The borderpane containing this
     */
    private BorderPane border;

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
        this.border = border;
        if (user.getLevel() != PermissionLevel.ADMIN) {
            editAdmin.setOpacity(0);
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
        if (UserManager.getUser().getLevel() == PermissionLevel.ADMIN) {
            try {
                FXMLLoader editor = new FXMLLoader(getClass()
                        .getResource("/fxml/admin_page.fxml"));
                Parent editorParent = editor.load();
                AdminController controller = editor.getController();
                controller.init(border);
                border.setCenter(editorParent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
