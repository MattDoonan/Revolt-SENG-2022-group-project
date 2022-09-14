package seng202.team3.gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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
import javafx.scene.control.ListView;
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
import seng202.team3.logic.VehicleManager;


/**
 * Controller for the garage.fxml window
 * 
 * @author Celia Allen
 * @version 1.0.3, Aug 22
 */
public class VehicleController {

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

    private ArrayList<String> connections = new ArrayList<String>();

    private Vehicle selectedVehicle;

    private Stage updatePopup = new Stage();

    private VehicleManager manage = new VehicleManager();

    private VehicleController controller;


    /**
     * Initialize the window
     *
     */
    public void init() {
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
     * Displays pop-up window to add a new vehicle to the garage
     */
    @FXML
    public void displayUpdate() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/vehicle_update.fxml"));
            updatePopup.initModality(Modality.APPLICATION_MODAL);
            updatePopup.setResizable(false);
            updatePopup.setTitle("Vehicle Information");
            updatePopup.setScene(new Scene(root, 600, 500));
            updatePopup.showAndWait();
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
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
    public void launchEditable(Vehicle vehicle) {
        try {
            FXMLLoader vehicleEdit = new FXMLLoader(getClass().getResource(
                    "/fxml/vehicle_update.fxml"));
            AnchorPane root = vehicleEdit.load();
            Scene modalScene = new Scene(root);
            Stage modal = new Stage();
            modal.setScene(modalScene);
            modal.setResizable(false);
            modal.setTitle("Vehicle Information");
            modal.initModality(Modality.WINDOW_MODAL);
            VehicleUpdateController controller = vehicleEdit.getController();
            controller.displayInfo(vehicle);
            modal.setAlwaysOnTop(true);
            modal.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            setData();
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
     * Sets the VehicleController holding all the controllers
     *
     * @param controller VehicleController controller
     */
    public void setController(VehicleController controller) {
        this.controller = controller;
    }


    /**
     * Adds vehicle data to each display if vehicleData size is large enough
     */
    public void setData() {
        if (vehicleData.size() > 0) {
            populateDisplays("one", vehicleImageOne, 0);
        } else {
            editCarOne.setVisible(false);
            deleteCarOne.setVisible(false);
        }
        if (vehicleData.size() > 1) {
            populateDisplays("two", vehicleImageTwo, 1);
        } else {
            editCarTwo.setVisible(false);
            deleteCarTwo.setVisible(false);
        }
        if (vehicleData.size() > 2) {
            populateDisplays("three", vehicleImageThree, 2);
        } else {
            editCarThree.setVisible(false);
            deleteCarThree.setVisible(false);
        }        
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
                        "Current Charge: " + vehicleData.get(index).getBatteryPercent() + "\n"
                        + "Max. Range: " + vehicleData.get(index).getMaxRange() + "\n"
                        + "Connections: " + vehicleData.get(index).getConnectors().toString());
                    break;
                case "two":
                    makeModelTwo.setText(vehicleData.get(index).getMake() + " "
                        + vehicleData.get(index).getModel());
                    carDetailsTwo.setText(
                        "Current Charge: " + vehicleData.get(index).getBatteryPercent() + "\n"
                        + "Max. Range: " + vehicleData.get(index).getMaxRange() + "\n"
                        + "Connections: " + vehicleData.get(index).getConnectors().toString());
                    break;
                case "three":
                    makeModelThree.setText(vehicleData.get(index).getMake() + " "
                        + vehicleData.get(index).getModel());
                    carDetailsThree.setText(
                        "Current Charge: " + vehicleData.get(index).getBatteryPercent() + "\n"
                        + "Max. Range: " + vehicleData.get(index).getMaxRange() + "\n"
                        + "Connections: " + vehicleData.get(index).getConnectors().toString());
                    break;
                default:
                    break;
            }
            try {
                if (vehicleData.get(index).getImgPath() != null) {
                    Image image = new Image(new FileInputStream(
                        "src/main/resources/images/car_one.png"));
                    imageview.setImage(image);
                } else {
                    imageview.setImage(null);
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
