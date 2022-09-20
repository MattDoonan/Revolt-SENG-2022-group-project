package seng202.team3.gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
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
 * @version 1.2.0, Aug 21
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

    private ObservableList<Vehicle> vehicleData = FXCollections.observableArrayList();

    private Vehicle selectedVehicle;

    private Stage editPopup = new Stage();

    private GarageManager manage = new GarageManager();

    private GarageController controller;

    /**
     * Initialize the window with data from the database
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
     * Launches the vehicle update modal, with the given vehicle null,
     * to signify that there is no vehicle to be updated, but a new one is
     * being added.
     */
    @FXML
    public void launchUpdate() {
        launchEditable(null);
    }

    /**
     * Sets selectedVehicle to the vehicle the user wants to edit,
     * then launches the edit popup with the selected vehicle's details pre-filled.
     * 
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
     * Displays pop-up window that allows a user to edit a
     * vehicle in the garage
     */
    public void launchEditable(Vehicle vehicle) {
        try {
            initializeEditable(vehicle);
            editPopup.showAndWait();
        } finally {
            editPopup = new Stage();
            refresh();
        }

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
            if (vehicle != null) {
                VehicleUpdateController controller = vehicleEdit.getController();
                controller.displayInfo(vehicle);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            refresh();
        }
    }

    /**
     * Sets selectedVehicle to the vehicle the user wants to delete,
     * then launches the confirm delete popup.
     * 
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
     * 
     * @param display   a string to represent the display to be cleared
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
     * Populate the given display with the vehicle at the given index of vehicleData
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
                                    + "Max. Range: " + vehicleData.get(index).getMaxRange()
                                    + " km\n"
                                    + "Connections: "
                                    + vehicleData.get(index).getConnectors().toString());
                    break;
                case "two":
                    makeModelTwo.setText(vehicleData.get(index).getMake() + " "
                            + vehicleData.get(index).getModel());
                    carDetailsTwo.setText(
                            "Current Charge: " + vehicleData.get(index).getBatteryPercent() + "%\n"
                                    + "Max. Range: " + vehicleData.get(index).getMaxRange()
                                    + " km\n"
                                    + "Connections: "
                                    + vehicleData.get(index).getConnectors().toString());
                    break;
                case "three":
                    makeModelThree.setText(vehicleData.get(index).getMake() + " "
                            + vehicleData.get(index).getModel());
                    carDetailsThree.setText(
                            "Current Charge: " + vehicleData.get(index).getBatteryPercent()
                                    + "%\n"
                                    + "Max. Range: " + vehicleData.get(index).getMaxRange()
                                    + " km\n"
                                    + "Connections: "
                                    + vehicleData.get(index).getConnectors().toString());
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
     * Method to call when next button is clicked.
     * Reshuffles vehicleData so the first three elements are the three to
     * be displayed in the user's garage.
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
     * Reshuffles vehicleData so the first three elements are the three to
     * be displayed in the user's garage.
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
