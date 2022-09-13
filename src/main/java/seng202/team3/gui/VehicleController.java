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
import javafx.scene.Node;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Vehicle;
import seng202.team3.logic.VehicleManager;

//TODO: seperate out VehicleController to VehicleEditController

/**
 * Controller for the garage.fxml window
 * 
 * @author Team 3
 * @version 1.0.3, Aug 22
 */
public class VehicleController {

    // private static final Logger log = LogManager.getLogger();

    @FXML
    private Label inputBox;

    @FXML
    private ListView<String> vehicleList;

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
    private Button openVehicleUpdate;

    @FXML
    private Button nextBtn;

    @FXML
    private Button prevBtn;

    @FXML
    private Button addConnection;

    @FXML
    private Button saveChanges;

    @FXML
    private Button confirmDelete;

    @FXML
    private Label addedConnections;

    @FXML
    private TextField licenseText;

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

    private ObservableList<Button> imgBtns = FXCollections.observableArrayList();

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
        // openVehicleUpdate.setOnAction(e -> displayUpdate());
        // openVehicleUpdate.setOnAction(e -> selectedVehicle = null);
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

    // /**
    //  * Displays pop-up window to add a new vehicle to the garage
    //  */
    // @FXML
    // public void displayUpdate() {
    //     System.out.println("displayUpdate");
    //     // try {
    //     //     Parent root = FXMLLoader.load(getClass().getResource("/fxml/vehicle_update.fxml"));
    //     //     updatePopup.initModality(Modality.APPLICATION_MODAL);
    //     //     updatePopup.setResizable(false);
    //     //     updatePopup.setTitle("Vehicle Information");
    //     //     updatePopup.setScene(new Scene(root, 600, 500));
    //     //     updatePopup.showAndWait();
    //     // } catch (IOException e) {
    //     //     Logger logger = Logger.getLogger(getClass().getName());
    //     //     logger.log(Level.SEVERE, "Failed to create new Window.", e);
    //     // }

    // }

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
     * Launches the editable portion
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
            controller.setController(this);
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
     * Allows a user to edit a vehicle
     * @paramn event
     */
    @FXML
    public void editVehicle(ActionEvent event) {
        String source = ((Button) event.getSource()).getId();
        System.out.println("edit source: " + source);
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
     * Sets the vehicle to be deleted
     * @paramn event
     */
    @FXML
    public void setDelete(ActionEvent event) {
        String source = ((Button) event.getSource()).getId();
        System.out.println("delete source: " + source);

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

        System.out.println("promptscreen1 selectedVehicle: " + selectedVehicle);
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
            VehicleController controller = vehicleDelete.getController();
            controller.setController(this);
            modal.setAlwaysOnTop(true);
            modal.showAndWait();
            System.out.println("promptscreen2 selectedVehicle: " + selectedVehicle);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("promptscreen3 selectedVehicle: " + selectedVehicle);
            setData();
        }
    }



    /**
     * Deletes the selected vehicle
     */
    public void confirmDelete() {
        System.out.println("selectedVehicle: " + selectedVehicle);
        if (selectedVehicle != null) {
            try {
                SqlInterpreter.getInstance().deleteData("vehicle", 
                    selectedVehicle.getVehicleId());
                selectedVehicle = null;
                // getAllVehicles();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                cancel();
            }
        }

    }


    /**
     * Deletes the selected vehicle
     */
    @FXML
    public void confirm() {
        System.out.println("selectedVehicleDelete: " + selectedVehicle);
        cancel();
        confirmDelete();
    }

    /**
     * Cancels and closes the window
     */
    @FXML
    public void cancel() {
        Stage stage = (Stage) inputBox.getScene().getWindow();
        stage.close();
    }




    /**
     * Sets the ConnectorController holding all the controllers
     *
     * @param controller ConnectorController controller
     */
    public void setController(VehicleController controller) {
        this.controller = controller;
    }

    /**
     * Displays all the info of the vehicle, if there is a vehicle
     */
    public void displayInfo(Vehicle vehicle) {
        // System.out.println("vehicle: " + vehicle.toString());
        // System.out.println("vehicleID: " + Integer.toString(vehicle.getVehicleId()));
        selectedVehicle = vehicle;
        if (vehicle != null) {
            licenseText.setText("null");
            makeText.setText(vehicle.getMake());
            modelText.setText(vehicle.getModel());
            maxRangeText.setText(Integer.toString(vehicle.getMaxRange()));
            addedConnections.setText(vehicle.getConnectors().toString());
            imgName.setText(vehicle.getImgPath().replace("src/main/resources/images/", ""));
            connections = vehicle.getConnectors();
        }
    }









    /**
     * Adds vehicle data to each display if vehicleData size is large enough
     */
    public void setData() {
        populateDisplays("one", vehicleImageOne, 0);
        if (vehicleData.size() > 1) {
            populateDisplays("two", vehicleImageTwo, 1);
        }
        if (vehicleData.size() > 2) {
            populateDisplays("three", vehicleImageThree, 2);
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
                    // Image image = new Image(new FileInputStream(
                    //     vehicleData.get(index).getImgPath()));
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

    // /**
    //  * Creates test vehicles
    //  * TODO: Remove once data can be properly pulled from the database
    //  */
    // public void makeTestVehicles() {

    //     vehicleData.add(
    //             new Vehicle("Nissan", "Leaf", 270, new ArrayList<>()));
    //     vehicleData.add(
    //             new Vehicle("Nissan", "Leaf E+", 385, new ArrayList<>()));
    //     vehicleData.add(
    //             new Vehicle("Tesla", "X", 536, new ArrayList<>()));
    //     vehicleData.add(
    //             new Vehicle("Jaguar", "I-Pace", 470, new ArrayList<>()));
    //     vehicleData.add(
    //             new Vehicle("Audi", "E-Tron", 441, new ArrayList<>()));
    //     vehicleData.add(
    //             new Vehicle("Tesla", "5", 637, new ArrayList<>()));
    //     vehicleData.add(
    //             new Vehicle("Mercedes Benz", "EQC", 430, new ArrayList<>()));
    //     vehicleData.add(
    //             new Vehicle("Porsche", "Taycan", 480, new ArrayList<>()));
    //     vehicleData.get(0).setImgPath("src/main/resources/images/car_one.png");
    //     vehicleData.get(1).setImgPath("src/main/resources/images/car_three.png");
    //     vehicleData.get(2).setImgPath("src/main/resources/images/car_two.png");
    //     vehicleData.get(3).setImgPath("src/main/resources/images/car_one.png");
    //     vehicleData.get(4).setImgPath("src/main/resources/images/truck_one.png");
    //     vehicleData.get(5).setImgPath("src/main/resources/images/car_three.png");
    //     vehicleData.get(6).setImgPath("src/main/resources/images/car_two.png");
    //     vehicleData.get(7).setImgPath("src/main/resources/images/truck_two.png");
    // }

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
