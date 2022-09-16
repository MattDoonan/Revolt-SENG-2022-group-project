package seng202.team3.gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng202.team3.data.entity.Vehicle;
import seng202.team3.logic.GarageManager;


/**
 * Controller for the garage.fxml window
 * 
 * @author Celia Allen
 * @version 1.0.3, Aug 22
 */
public class GarageController {

    @FXML
    private TextArea makeModelOne;

    @FXML
    private TextArea makeModelTwo;

    @FXML
    private TextArea makeModelThree;

    @FXML
    private ImageView vehicleImageOne;

    @FXML
    private ImageView vehicleImageTwo;

    @FXML
    private ImageView vehicleImageThree;

    @FXML
    private TextArea carDetailsOne;

    @FXML
    private TextArea carDetailsTwo;

    @FXML
    private TextArea carDetailsThree;

    @FXML
    private Button editCarOne;

    @FXML
    private Button editCarTwo;

    @FXML
    private Button editCarThree;

    @FXML
    private Button deleteCarOne;

    @FXML
    private Button deleteCarTwo;

    @FXML
    private Button deleteCarThree;

    @FXML
    private Button nextBtn;

    @FXML
    private Button prevBtn;

    @FXML
    private Button addConnection;

    @FXML
    private Label addedConnections;

    @FXML
    private TextField makeText;

    @FXML
    private TextField modelText;

    @FXML
    private TextField maxRangeText;

    @FXML
    private ComboBox<String> connectorType;

    @FXML
    private Label imgName;

    @FXML
    private ScrollPane imgsDisplay;

    private ObservableList<Vehicle> vehicleData = FXCollections.observableArrayList();

    private Vehicle selectedVehicle;

    private Stage updatePopup = new Stage();

    private Stage editPopup = new Stage();

    private GarageManager manage = new GarageManager();

    private GarageController controller;

    private Stage stage;


    /**
     * Initialize the window
     *
     */
    public void init() {
        refresh();
    }

    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage, MenuController menu) {
        this.stage = stage;
        refresh();
    }


    /**
     * Refresh the garage display with the user's up-to-date vehicles
     */
    public void refresh() {
        manage.resetQuery();
        manage.getAllVehicles();
        vehicleData = manage.getData();
        setData();
    }


    /**
     * Initialises the pop-up window that allows a user to add or edit a 
     * vehicle to/in the garage
     */
    public void initializeUpdate() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/vehicle_update.fxml"));
            updatePopup.initModality(Modality.APPLICATION_MODAL);
            updatePopup.setResizable(false);
            updatePopup.setTitle("Vehicle Information");
            updatePopup.setScene(new Scene(root, 337, 497));
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        } finally {
            refresh();
        }
    }


    /**
     * Displays pop-up window that allows a user to add a 
     * vehicle to/in the garage
     */
    @FXML
    public void displayUpdate() {
        try {
            if (updatePopup.getTitle() == null) {
                initializeUpdate();
            }
            updatePopup.showAndWait();
        } finally {
            refresh();
        }
        
    }


    /**
     * Sets selectedVehicle to the vehicle to be edited
     * @paramn event, the event that called the method
     */
    @FXML
    public void setEdit(ActionEvent event) {
        String source = ((Button) event.getSource()).getId();
        switch (source) {
            case "editCarOne":
                selectedVehicle = vehicleData.get(0);
                break;
            case "editCarTwo":
                selectedVehicle = vehicleData.get(1);
                break;
            case "editCarThree":
                selectedVehicle = vehicleData.get(2);  
                break;
            default:
                break;
        }
        launchEditable(selectedVehicle);
    }


    /**
     * Sets selectedVehicle to the vehicle to be deleted
     * @paramn event, the event that called the method
     */
    @FXML
    public void setDelete(ActionEvent event) {
        String source = ((Button) event.getSource()).getId();
        switch (source) {
            case "deleteCarOne":
                selectedVehicle = vehicleData.get(0);
                break;
            case "deleteCarTwo":
                selectedVehicle = vehicleData.get(1);
                break;
            case "deleteCarThree":
                selectedVehicle = vehicleData.get(2);  
                break;
            default:
                break;
        }
        launchDelete(selectedVehicle);
    }


    /**
     * Launches the editable pop-up for vehicles
     *
     * @param vehicle the {@link Vehicle} for the vehicle info. Null if adding.
     */
    public void initializeEditable(Vehicle vehicle) {
        try {
            FXMLLoader vehicleEdit = new FXMLLoader(getClass().getResource(
                    "/fxml/vehicle_update.fxml"));
            AnchorPane root = vehicleEdit.load();
            Scene modalScene = new Scene(root);
            editPopup.setScene(modalScene);
            editPopup.setResizable(false);
            editPopup.setTitle("Vehicle Information");
            editPopup.initModality(Modality.WINDOW_MODAL);
            VehicleUpdateController controller = vehicleEdit.getController();
            controller.displayInfo(vehicle);
            editPopup.setAlwaysOnTop(true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // setData();
            refresh();
        }
    }


    /**
     * Displays pop-up window that allows a user to edit a 
     * vehicle to/in the garage
     */
    public void launchEditable(Vehicle vehicle) {
        try {
            if (editPopup.getTitle() == null) {
                initializeEditable(vehicle);
            }
            editPopup.showAndWait();
        } finally {
            refresh();
        }
        
    }

    
    /**
     * Launches the confirm delete popup
     *
     * @param vehicle the vehicle to be deleted
    */
    public void launchDelete(Vehicle vehicle) {
        try {
            FXMLLoader vehicleDelete = new FXMLLoader(getClass().getResource(
                    "/fxml/vehicle_prompt.fxml"));
            VBox root = vehicleDelete.load();
            Scene modalScene = new Scene(root);
            Stage modal = new Stage();
            modal.setScene(modalScene);
            modal.setResizable(false);
            modal.setTitle("Delete Vehicle:");
            modal.initModality(Modality.WINDOW_MODAL);
            VehicleUpdateController controller = vehicleDelete.getController();
            controller.setSelectedVehicle(vehicle);
            modal.setAlwaysOnTop(true);
            modal.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            refresh();
        }
    }


    /**
     * Sets the GarageController holding all the controllers
     *
     * @param controller GarageController controller
     */
    public void setController(GarageController controller) {
        this.controller = controller;
    }


    /**
     * Adds vehicle data to each display if vehicleData size is large enough
     * Disables next and prev buttons if less than three vehicles to display
     */
    public void setData() {
        if (vehicleData.size() > 0) {
            populateDisplays("one", vehicleImageOne, 0);
            editCarOne.setVisible(true);
            deleteCarOne.setVisible(true);
        } else {
            editCarOne.setVisible(false);
            deleteCarOne.setVisible(false);
            clearDisplay("one", vehicleImageOne);
        }
        if (vehicleData.size() > 1) {
            populateDisplays("two", vehicleImageTwo, 1);
            editCarTwo.setVisible(true);
            deleteCarTwo.setVisible(true);
        } else {
            editCarTwo.setVisible(false);
            deleteCarTwo.setVisible(false);
            clearDisplay("two", vehicleImageTwo);
        }
        if (vehicleData.size() > 2) {
            populateDisplays("three", vehicleImageThree, 2);
            editCarThree.setVisible(true);
            deleteCarThree.setVisible(true);
        } else {
            editCarThree.setVisible(false);
            deleteCarThree.setVisible(false);
            clearDisplay("three", vehicleImageThree);
        }        
        if (vehicleData.size() <= 3) {
            nextBtn.setDisable(true);
            prevBtn.setDisable(true);
        } else {
            nextBtn.setDisable(false);
            prevBtn.setDisable(false);
        }
    }

    /**
     * Clears the given display of text and image(s)
     * @param display a string to represent the display to be cleared
     * @param imageview the ImageView to clear
     */
    public void clearDisplay(String display, ImageView imageview) {
        switch (display) {
            case "one":
                makeModelOne.setText("");
                carDetailsOne.setText("");
                break;
            case "two":
                makeModelTwo.setText("");
                carDetailsTwo.setText("");
                break;
            case "three":
                makeModelThree.setText("");
                carDetailsThree.setText("");
                break;
            default:
                break;
        }
        imageview.setImage(null);
    }


    /**
     * Populate the given display with the vehicle at the given index
     *
     * @param display   location to put the text
     * @param imageview path to vehicle image
     * @param index     index of the vehicle to display
     */
    public void populateDisplays(String display, ImageView imageview, int index) {
        if (vehicleData.size() > 0) {
            switch (display) {
                case "one":
                    makeModelOne.setText(vehicleData.get(index).getMake() + " "
                        + vehicleData.get(index).getModel());
                    carDetailsOne.setText(
                        "Current Charge: " + vehicleData.get(index).getBatteryPercent() + "%\n"
                        + "Max. Range: " + vehicleData.get(index).getMaxRange() + " km\n"
                        + "Connections: " + vehicleData.get(index).getConnectors().toString());
                    break;
                case "two":
                    makeModelTwo.setText(vehicleData.get(index).getMake() + " "
                        + vehicleData.get(index).getModel());
                    carDetailsTwo.setText(
                        "Current Charge: " + vehicleData.get(index).getBatteryPercent() + "%\n"
                        + "Max. Range: " + vehicleData.get(index).getMaxRange() + " km\n"
                        + "Connections: " + vehicleData.get(index).getConnectors().toString());
                    break;
                case "three":
                    makeModelThree.setText(vehicleData.get(index).getMake() + " "
                        + vehicleData.get(index).getModel());
                    carDetailsThree.setText(
                        "Current Charge: " + vehicleData.get(index).getBatteryPercent() + "%\n"
                        + "Max. Range: " + vehicleData.get(index).getMaxRange() + " km\n"
                        + "Connections: " + vehicleData.get(index).getConnectors().toString());
                    break;
                default:
                    break;
            }
            try {
                if (vehicleData.get(index).getImgPath().equals(
                        "src/main/resources/images/null")) {
                    imageview.setImage(null);
                } else {
                    Image image = new Image(new FileInputStream(
                        vehicleData.get(index).getImgPath()));
                    imageview.setImage(image);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Method to call when next button is clicked
     *
     */
    @FXML
    public void nextBtnClicked() {
        if (vehicleData.size() > 0) {
            Vehicle vehicle = vehicleData.get(0);
            vehicleData.remove(vehicle);
            vehicleData.add(vehicle);
            setData();
        }
    }


    /**
     * Method to call when prev button is clicked
     *
     */
    @FXML
    public void prevBtnClicked() {
        if (vehicleData.size() > 0) {
            Vehicle vehicle = vehicleData.get(vehicleData.size() - 1);
            vehicleData.remove(vehicle);
            vehicleData.add(0, vehicle);
            setData();
        }
    }
}
