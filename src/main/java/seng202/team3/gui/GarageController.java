package seng202.team3.gui;

import java.io.BufferedInputStream;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.data.entity.Vehicle;
import seng202.team3.logic.GarageManager;

/**
 * Controller for the garage.fxml window
 * 
 * @author Celia Allen
 * @version 1.0.0, Aug 21
 */
public class GarageController {
    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

    /**
     * Make and model for the first vehicle
     */
    @FXML
    private TextArea makeModelOne;

    /**
     * Make and model for the second vehicle
     */
    @FXML
    private TextArea makeModelTwo;

    /**
     * Make and model for the third vehicle
     */
    @FXML
    private TextArea makeModelThree;

    /**
     * Image for the first vehicle
     */
    @FXML
    private ImageView vehicleImageOne;

    /**
     * Image for the second vehicle
     */
    @FXML
    private ImageView vehicleImageTwo;

    /**
     * Image for the third vehicle
     */
    @FXML
    private ImageView vehicleImageThree;

    /**
     * Details for the first vehicle
     */
    @FXML
    private TextArea carDetailsOne;

    /**
     * Details for the second vehicle
     */
    @FXML
    private TextArea carDetailsTwo;

    /**
     * Details for the third vehicle
     */
    @FXML
    private TextArea carDetailsThree;

    /**
     * Tells user whether the first vehicle is currently selected
     */
    @FXML
    private TextArea currSelectedOne;

    /**
     * Tells user whether the second vehicle is currently selected
     */
    @FXML
    private TextArea currSelectedTwo;

    /**
     * Tells user whether the third vehicle is currently selected
     */
    @FXML
    private TextArea currSelectedThree;

    /**
     * Button to edit the first vehicle
     */
    @FXML
    private Button editCarOne;

    /**
     * Button to edit the second vehicle
     */
    @FXML
    private Button editCarTwo;

    /**
     * Button to edit the third vehicle
     */
    @FXML
    private Button editCarThree;

    /**
     * Button to delete the first vehicle
     */
    @FXML
    private Button deleteCarOne;

    /**
     * Button to delete the second vehicle
     */
    @FXML
    private Button deleteCarTwo;

    /**
     * Button to delete the third vehicle
     */
    @FXML
    private Button deleteCarThree;

    /**
     * Button to set the first vehicle as the user's current vehicle
     */
    @FXML
    private Button currEvOne;

    /**
     * Button to set the second vehicle as the user's current vehicle
     */
    @FXML
    private Button currEvTwo;

    /**
     * Button to set the third vehicle as the user's current vehicle
     */
    @FXML
    private Button currEvThree;

    /**
     * Button to scroll the vehicle view forward
     */
    @FXML
    private Button nextBtn;

    /**
     * Button to scroll the vehicle view backward
     */
    @FXML
    private Button prevBtn;

    /**
     * All vehicles to be displayed
     */
    private ObservableList<Vehicle> vehicleData = FXCollections.observableArrayList();

    /**
     * Active vehicle
     */
    private Vehicle selectedVehicle;

    /**
     * Pop up window for editing vehicles
     */
    private Stage editPopup = new Stage();

    /**
     * Logic controller
     */
    private GarageManager manage = new GarageManager();

    /**
     * Max Range label text
     */
    private static final String MAX_RANGE_TXT = "Max Range: ";

    /**
     * Distance unit label text
     */
    private static final String DISTANCE_UNIT_TXT = "km";

    /**
     * Connections label text
     */
    private static final String CONNECTIONS_TXT = "Connections: ";

    /**
     * Vehicle One Display
     */
    private static final String VEHICLE_ONE = "one";

    /**
     * Vehicle Two Display
     */
    private static final String VEHICLE_TWO = "two";

    /**
     * Vehicle Three Display
     */
    private static final String VEHICLE_THREE = "three";

    /**
     * Initialize the GarageController
     */
    public GarageController() {
        // Ununsed
    }

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
     * Sets the user's current vehicle
     * 
     * @param event the event that called the method
     */
    @FXML
    public void setCurr(ActionEvent event) {
        for (Vehicle vehicle : vehicleData) {
            vehicle.setcurrVehicle(false);
            manage.saveCurrVehicle(vehicle);
        }
        String source = ((Button) event.getSource()).getId();
        Vehicle currVehicle = new Vehicle();
        switch (source) {
            case "currEvOne":
                vehicleData.get(0).setcurrVehicle(true);
                currVehicle = vehicleData.get(0);
                break;
            case "currEvTwo":
                vehicleData.get(1).setcurrVehicle(true);
                currVehicle = vehicleData.get(1);
                break;
            case "currEvThree":
                vehicleData.get(2).setcurrVehicle(true);
                currVehicle = vehicleData.get(2);
                break;
            default:
                break;
        }

        manage.saveCurrVehicle(currVehicle);
        refresh();

    }

    /**
     * Sets selectedVehicle to the vehicle the user wants to edit,
     * then launches the edit popup with the selected vehicle's details pre-filled.
     * 
     * @param event the event that called the method
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
     * 
     * @param vehicle the vehicle to be edited
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
            editPopup.initModality(Modality.APPLICATION_MODAL);
            if (vehicle != null) {
                VehicleUpdateController controller = vehicleEdit.getController();
                controller.displayInfo(vehicle);
            }
        } catch (IOException e) {
            logManager.error(e.getMessage());
        } finally {
            refresh();
        }
    }

    /**
     * Sets selectedVehicle to the vehicle the user wants to delete,
     * then launches the confirm delete popup.
     * 
     * @param event the event that called the method
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
            modal.initModality(Modality.APPLICATION_MODAL);
            VehicleUpdateController controller = vehicleDelete.getController();
            controller.init();
            controller.setSelectedVehicle(vehicle);
            modal.setAlwaysOnTop(true);
            MainWindow.setController(controller);
            modal.showAndWait();
        } catch (IOException e) {
            logManager.error(e.getMessage());
        } finally {
            refresh();
        }
    }

    /**
     * Adds vehicle data to each display if vehicleData size is large enough
     * Disables next and prev buttons if less than three vehicles to display
     */
    public void setData() {
        if (!vehicleData.isEmpty()) {
            populateDisplays(VEHICLE_ONE, vehicleImageOne, 0);
            editCarOne.setVisible(true);
            deleteCarOne.setVisible(true);
            currEvOne.setVisible(true);
        } else {
            editCarOne.setVisible(false);
            deleteCarOne.setVisible(false);
            currEvOne.setVisible(false);
            clearDisplay(VEHICLE_ONE, vehicleImageOne);
        }
        if (vehicleData.size() > 1) {
            populateDisplays(VEHICLE_TWO, vehicleImageTwo, 1);
            editCarTwo.setVisible(true);
            deleteCarTwo.setVisible(true);
            currEvTwo.setVisible(true);
        } else {
            editCarTwo.setVisible(false);
            deleteCarTwo.setVisible(false);
            currEvTwo.setVisible(false);
            clearDisplay(VEHICLE_TWO, vehicleImageTwo);
        }
        if (vehicleData.size() > 2) {
            populateDisplays(VEHICLE_THREE, vehicleImageThree, 2);
            editCarThree.setVisible(true);
            deleteCarThree.setVisible(true);
            currEvThree.setVisible(true);
        } else {
            editCarThree.setVisible(false);
            deleteCarThree.setVisible(false);
            currEvThree.setVisible(false);
            clearDisplay(VEHICLE_THREE, vehicleImageThree);
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
            case VEHICLE_ONE:
                makeModelOne.setText("");
                carDetailsOne.setText("");
                currSelectedOne.setText("");
                break;
            case VEHICLE_TWO:
                makeModelTwo.setText("");
                carDetailsTwo.setText("");
                currSelectedTwo.setText("");
                break;
            case VEHICLE_THREE:
                makeModelThree.setText("");
                carDetailsThree.setText("");
                currSelectedThree.setText("");
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
        if (!vehicleData.isEmpty()) {
            switch (display) {
                case VEHICLE_ONE:
                    makeModelOne.setText(vehicleData.get(index).getMake() + " "
                            + vehicleData.get(index).getModel());
                    carDetailsOne.setText(
                            MAX_RANGE_TXT + vehicleData.get(index).getMaxRange()
                                    + DISTANCE_UNIT_TXT + "\n"
                                    + CONNECTIONS_TXT
                                    + vehicleData.get(index).getConnectors().toString());
                    if (vehicleData.get(index).getCurrVehicle()) {
                        currSelectedOne.setText("Currently selected");
                    } else {
                        currSelectedOne.setText("");
                    }

                    break;
                case VEHICLE_TWO:
                    makeModelTwo.setText(vehicleData.get(index).getMake() + " "
                            + vehicleData.get(index).getModel());
                    carDetailsTwo.setText(
                            MAX_RANGE_TXT + vehicleData.get(index).getMaxRange()
                                    + DISTANCE_UNIT_TXT + "\n"
                                    + CONNECTIONS_TXT
                                    + vehicleData.get(index).getConnectors().toString());
                    if (vehicleData.get(index).getCurrVehicle()) {
                        currSelectedTwo.setText("Currently selected");
                    } else {
                        currSelectedTwo.setText("");
                    }
                    break;
                case VEHICLE_THREE:
                    makeModelThree.setText(vehicleData.get(index).getMake() + " "
                            + vehicleData.get(index).getModel());
                    carDetailsThree.setText(
                            MAX_RANGE_TXT + vehicleData.get(index).getMaxRange()
                                    + DISTANCE_UNIT_TXT + "\n"
                                    + CONNECTIONS_TXT
                                    + vehicleData.get(index).getConnectors().toString());
                    if (vehicleData.get(index).getCurrVehicle()) {
                        currSelectedThree.setText("Currently selected");
                    } else {
                        currSelectedThree.setText("");
                    }
                    break;
                default:
                    break;
            }

            setImg(index, imageview);

        }
    }

    /**
     * Set the image of the pane
     * 
     * @param index     pane to set
     * @param imageview image view to populate
     */
    private void setImg(int index, ImageView imageview) {
        try {
            if (vehicleData.get(index).getImgPath().equals(
                    "/images/null")) {
                imageview.setImage(null);
            } else {
                Image image = new Image(new BufferedInputStream(
                        getClass().getResourceAsStream(vehicleData.get(index).getImgPath())));
                imageview.setImage(image);
            }

        } catch (NullPointerException e) {
            logManager.error(e.getMessage());
        }
    }

    /**
     * Method to call when next button is clicked.
     * Reshuffles vehicleData so the first three elements are the three to
     * be displayed in the user's garage.
     */
    @FXML
    public void nextBtnClicked() {
        if (!vehicleData.isEmpty()) {
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
        if (!vehicleData.isEmpty()) {
            Vehicle vehicle = vehicleData.get(vehicleData.size() - 1);
            vehicleData.remove(vehicle);
            vehicleData.add(0, vehicle);
            setData();
        }
    }

    /**
     * Returns the manager for tests
     * 
     * @return the manager class
     */
    public GarageManager getManage() {
        return manage;
    }
}
