package seng202.team3.gui;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.data.entity.Vehicle;
import seng202.team3.logic.UserManager;
import seng202.team3.logic.VehicleUpdateManager;

/**
 * A vehicle editing controller
 *
 * @author Celia Allen
 * @version 1.0.0, Sep 13
 */
public class VehicleUpdateController {
    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

    /** The active stage */
    private Stage stage;

    /**
     * The textfield for the vehicle's make
     */
    @FXML
    private TextField makeText;

    /**
     * The textfield for the vehicle's model
     */
    @FXML
    private TextField modelText;

    /**
     * The textfield for the vehicle's maximum range
     */
    @FXML
    private TextField maxRangeText;

    /**
     * The textfield for the vehicle's current charge
     */
    @FXML
    private TextField currChargeText;

    /**
     * Lable to display the name of the chosen vehicle image
     */
    @FXML
    private Label imgName;

    /**
     * Lable to display the currently added connections
     */
    @FXML
    private ListView<HBox> addedConnections;

    /**
     * Dropdown of the connector types
     */
    @FXML
    private ComboBox<String> connectorType;

    /**
     * Button to save the changes to the database
     */
    @FXML
    private Button saveChanges;

    /**
     * Used to get the current scene to close it
     */
    @FXML
    private Label inputBox;

    /**
     * Invalid vehicle lable
     */
    @FXML
    private Label invalidVehicle;

    /**
     * Save the selected image
     */
    private Button saveImg = new Button("Select");

    /**
     * Cancel selecting an image
     */
    private Button cancelImg = new Button("Cancel");

    /**
     * TActive vehicle
     */
    private Vehicle selectedVehicle;

    /**
     * Active image
     */
    private String selectedImg;

    /**
     * The vehicles connections
     */
    private List<String> connections = new ArrayList<>();

    /**
     * The images that the user can choose from (button form)
     */
    private ObservableList<Button> imgBtns = FXCollections.observableArrayList();

    /**
     * The images that the user can choose from (names)
     */
    private String[] imgNames = { "car_one.png", "car_two.png", "car_three.png",
        "truck_one.png", "truck_two.png" };

    /**
     * Styling for invalid fields
     */
    private static final String INVALID_STYLE = "-fx-border-color: #ff0000;";

    /**
     * Styling for valid fields
     */
    private static final String VALID_STYLE = "-fx-border-color: default;";

    /**
     * Default path for images
     */
    private static final String IMGPATH = "/images/";

    /**
     * Error message for invalid make
     */
    private static final String MAKE_ERROR = "Vehicle make required.";

    /**
     * Error message for invalid model
     */
    private static final String MODEL_ERROR = "Vehicle model required.";

    /**
     * Delete button text
     */
    private static final String DELETE_BUTTON_TEXT = "Delete";

    /**
     * Connection prompt text
     */
    private static final String CONN_PROMPT_TEXT = "Connection: ";

    /**
     * List of user input errors for adding/editing vehicles
     */
    private ArrayList<String> errors = new ArrayList<>();

    /**
     * The popup for selecting an image
     */
    private Stage imagePopup = new Stage();

    /**
     * The popup for an 'Other...' connector, allowing user to write in a unique
     * value
     */
    private Stage connectorPopup = new Stage();

    /**
     * The manager
     */
    private VehicleUpdateManager manage = new VehicleUpdateManager();

    /**
     * Controller of the caller - USED FOR TESTING
     */
    private Object prevController = null;

    /**
     * Error tooltip for the make field
     */
    private Tooltip makeError = new Tooltip();

    /**
     * Error tooltip for the model field
     */
    private Tooltip modelError = new Tooltip();

    /**
     * Error tooltip for the range field
     */
    private Tooltip rangeError = new Tooltip();

    /**
     * Error tooltip for the connector field
     */
    private Tooltip connectorError = new Tooltip();



    /**
     * Initialises the Vehicle editing
     */
    public VehicleUpdateController() {
        // Unused
    }

    /**
     * Initialises the controller
     */
    public void init() {
        stage = (Stage) inputBox.getScene().getWindow();
        prevController = MainWindow.getController();
        MainWindow.setController(this);
    }

    /**
     * Set selectedVehicle
     * 
     * @param vehicle the vehicle to set selectedVehicle to
     */
    public void setSelectedVehicle(Vehicle vehicle) {
        this.selectedVehicle = vehicle;
    }

    /**
     * Checks the given vehicle details for errors.
     * If no errors, calls manager to save vehicle to database
     */
    @FXML
    public void saveChanges() {
        Vehicle vehicle;

        if (selectedVehicle != null) {
            if (selectedImg != null) {
                selectedVehicle.setImgPath(IMGPATH + selectedImg);
            } else {
                selectedVehicle.setImgPath(IMGPATH + "null");
            }
            vehicle = selectedVehicle;
        } else {
            vehicle = new Vehicle();
        }

        Boolean fail = checkForErrors();

        if (Boolean.TRUE.equals(fail)) {
            invalidVehicle.setVisible(true);
            logManager.warn("Incorrect vehicle details");
        } else {
            vehicle.setOwner(UserManager.getUser().getUserid());
            vehicle.setMake(makeText.getText());
            vehicle.setModel(modelText.getText());
            vehicle.setMaxRange(Integer.parseInt(maxRangeText.getText()));
            vehicle.setBatteryPercent(100.0);
            vehicle.setConnectors(connections);
            manage.saveVehicle(vehicle);
            makeText.setText(null);
            modelText.setText(null);
            maxRangeText.setText(null);
            addedConnections.getItems().clear();
            imgName.setText(null);
            connections = new ArrayList<>();
            connectorType.setPromptText("Connector Type");
            selectedVehicle = null;
            Stage popupStage = (Stage) saveChanges.getScene().getWindow();
            popupStage.close();
        }

    }

    /**
     * Checks if there are any errors when a user adds/updates a vehicle.
     * 
     * @return whether there were any errors
     */
    public Boolean checkForErrors() {
        makeError.hide();
        modelError.hide();
        rangeError.hide();
        connectorError.hide();

        makeText.setStyle(VALID_STYLE);
        modelText.setStyle(VALID_STYLE);
        maxRangeText.setStyle(VALID_STYLE);
        connectorType.setStyle(VALID_STYLE);

        Point2D pMake = makeText.localToScene(0.0, 0.0);
        Point2D pModel = modelText.localToScene(0.0, 0.0);
        Point2D pRange = maxRangeText.localToScene(0.0, 0.0);
        Point2D pConn = connectorType.localToScene(0.0, 0.0);

        Boolean fail = false;

        if (makeText.getText().isEmpty()) {
            makeError.setText(MAKE_ERROR);

            makeText.setStyle(INVALID_STYLE);
            makeError.show(makeText,
                pMake.getX() + makeText.getScene().getX() + makeText.getScene().getWindow().getX() + makeText.getWidth() + 25,
                pMake.getY() + makeText.getScene().getY() + makeText.getScene().getWindow().getY());
            fail = true;
        }
        if (modelText.getText().isEmpty()) {
            modelError.setText(MODEL_ERROR);
            modelText.setStyle(INVALID_STYLE);
            modelError.show(modelText,
                pModel.getX() + modelText.getScene().getX() + modelText.getScene().getWindow().getX() + modelText.getWidth() + 25,
                pModel.getY() + modelText.getScene().getY() + modelText.getScene().getWindow().getY());
            fail = true;
        }
        Boolean rangeFlag = false;
        try {
            if (maxRangeText.getText().isEmpty()) {
                rangeError.setText("Max. range required.");
                rangeFlag = true;
            } else if (Integer.parseInt(maxRangeText.getText()) < 0) {
                rangeError.setText("Max. range cannot be negative.");
                rangeFlag = true;
            }
        } catch (NumberFormatException e) {
            rangeError.setText("Max. range must be a whole number.");
            rangeFlag = true;
        }
        if (Boolean.TRUE.equals(rangeFlag)) {
            maxRangeText.setStyle(INVALID_STYLE);
            rangeError.show(maxRangeText,
                pRange.getX() + maxRangeText.getScene().getX() + maxRangeText.getScene().getWindow().getX() + maxRangeText.getWidth() + 25,
                pRange.getY() + maxRangeText.getScene().getY() + maxRangeText.getScene().getWindow().getY());
            fail = true;
        }

        if (connections.isEmpty()) {
            connectorError.setText("A vehicle must have at least one connector.");
            connectorType.setStyle(INVALID_STYLE);
            connectorError.show(connectorType,
                pConn.getX() + connectorType.getScene().getX() + connectorType.getScene().getWindow().getX() + connectorType.getWidth() + 25,
                pConn.getY() + connectorType.getScene().getY() + connectorType.getScene().getWindow().getY());
            fail = true;
        }

        return fail;
    }

    /**
     * Adds a new connection (for an EV) when the button is clicked
     */
    public void addConnection() {
        if (connectorType.getValue() != null) {
            if (connectorType.getValue().equals("Other...")) {
                GridPane root = new GridPane();
                Button save = new Button("Save");
                TextField connector = new TextField("Connector Type");
                root.addRow(0, connector);
                root.addRow(1, save);
                root.setStyle("-fx-padding: 20;");
                save.setOnMouseClicked((MouseEvent event) -> {
                    Button button = new Button(DELETE_BUTTON_TEXT);
                    button.setId(connector.getText());
                    button.setOnAction(this::deleteConnection);
                    Label label = new Label(CONN_PROMPT_TEXT + connector.getText());
                    HBox hbox = new HBox();
                    Region filler = new Region();
                    HBox.setHgrow(filler, Priority.ALWAYS);
                    hbox.getChildren().addAll(label, filler, button);
                    connections.add(connector.getText());
                    addedConnections.getItems().add(hbox);

                    Stage connectorStage = (Stage) save.getScene().getWindow();
                    connectorStage.close();
                });
                connectorPopup.initModality(Modality.APPLICATION_MODAL);
                connectorPopup.setResizable(false);
                connectorPopup.setTitle("Other Connector");
                connectorPopup.setScene(new Scene(root, 300, 100));
            } else {

                connections.add(connectorType.getValue());
                Button button = new Button(DELETE_BUTTON_TEXT);
                button.setId(connectorType.getValue());
                button.setOnAction(this::deleteConnection);
                Label label = new Label(CONN_PROMPT_TEXT + connectorType.getValue());
                HBox hbox = new HBox();
                Region filler = new Region();
                HBox.setHgrow(filler, Priority.ALWAYS);
                hbox.getChildren().addAll(label, filler, button);
                addedConnections.getItems().add(hbox);

            }
        }

    }

    /**
     * Displays pop-up window that allows a user add a unique connector
     * to a vehicle
     * 
     */
    @FXML
    public void launchConnector() {
        if (!(connectorType.getValue().equals("Other..."))) {
            addConnection();
        } else {
            try {
                addConnection();
                connectorPopup.showAndWait();
            } finally {
                connectorPopup = new Stage();
            }
        }
    }

    /**
     * Delete a connection
     * 
     * @param e ActionEvent
     */
    public void deleteConnection(ActionEvent e) {
        int index = connections.indexOf(((Node) e.getSource()).getId());
        connections.remove(((Node) e.getSource()).getId());
        addedConnections.getItems().remove(index);

    }

    /**
     * Lets the user select an image (from a list) to represent their vehicle
     */
    @FXML
    public void selectImg() {
        try {
            saveImg.setOnAction(e -> setImg());
            saveImg.setLayoutX(225);
            saveImg.setLayoutY(360);
            cancelImg.setOnAction(e -> cancelImg());
            cancelImg.setLayoutX(280);
            cancelImg.setLayoutY(360);

            for (int i = 0; i < imgNames.length; i++) {
                Image img = new Image(
                        new BufferedInputStream(
                                getClass().getResourceAsStream(IMGPATH + imgNames[i])));
                ImageView view = new ImageView(img);
                Button button = new Button();
                button.setGraphic(view);
                button.setId(imgNames[i]);
                imgBtns.add(button);
                button.setOnAction(this::imgSelected);
            }
            displayImgSelect();
        } catch (NullPointerException e) {
            logManager.error(e.getMessage());
        }
    }

    /**
     * Initialises the pop-up window that allows a user to select
     * a vehicle image.
     */
    public void initializeImgSelect() {
        TilePane anchor = new TilePane();
        anchor.setPadding(new Insets(5));
        anchor.setVgap(5);
        anchor.setHgap(5);
        anchor.setPrefColumns(2);
        for (int i = 0; i < imgBtns.size(); i++) {
            Button curr = imgBtns.get(i);
            curr.setPadding(new Insets(30));
            anchor.getChildren().add(curr);
        }
        ScrollPane imgsDisplay = new ScrollPane();
        imgsDisplay.setContent(anchor);
        imgsDisplay.setPrefViewportWidth(322);
        imgsDisplay.setPrefViewportHeight(320);
        AnchorPane root = new AnchorPane();
        root.getChildren().addAll(imgsDisplay, saveImg, cancelImg);
        imagePopup.initModality(Modality.APPLICATION_MODAL);
        imagePopup.setResizable(false);
        imagePopup.setTitle("Select Image");
        imagePopup.setScene(new Scene(root, 337, 400));
    }

    /**
     * Displays pop-up window that allows a user to select a
     * vehicle image
     */
    public void displayImgSelect() {
        if (imagePopup.getTitle() == null) {
            initializeImgSelect();
        }
        imagePopup.showAndWait();
    }

    /**
     * Updates selectedImg to the ID of the currently-selected image
     * 
     * @param e ActionEvent
     */
    public void imgSelected(ActionEvent e) {
        selectedImg = ((Node) e.getSource()).getId();
    }

    /**
     * Saves the currently selected image for the user's vehicle
     */
    public void setImg() {
        imgName.setText(selectedImg);
        Stage popupStage = (Stage) saveImg.getScene().getWindow();
        popupStage.close();
    }

    /**
     * Cancels the user's currently-selected image and closes the pop-up
     */
    public void cancelImg() {
        selectedImg = null;
        Stage temp = (Stage) saveImg.getScene().getWindow();
        temp.close();
    }

    /**
     * Displays all the info of the vehicle, if there is a vehicle
     * 
     * @param vehicle vehicle to display information for
     */
    public void displayInfo(Vehicle vehicle) {
        init();
        selectedVehicle = vehicle;
        if (vehicle != null) {
            makeText.setText(vehicle.getMake());
            modelText.setText(vehicle.getModel());
            maxRangeText.setText(Integer.toString(vehicle.getMaxRange()));
            currChargeText.setText(vehicle.getBatteryPercent().toString());
            imgName.setText(vehicle.getImgPath().replace(IMGPATH, ""));
            selectedImg = vehicle.getImgPath().replace(IMGPATH, "");
            connections = vehicle.getConnectors();
            for (String connection : connections) {
                Button button = new Button(DELETE_BUTTON_TEXT);
                button.setId(connection);
                button.setOnAction(this::deleteConnection);
                Label label = new Label(CONN_PROMPT_TEXT + connection);
                HBox hbox = new HBox();
                Region filler = new Region();
                HBox.setHgrow(filler, Priority.ALWAYS);
                hbox.getChildren().addAll(label, filler, button);
                addedConnections.getItems().add(hbox);
            }
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
            errorPopup.setTitle("Error With Vehicle:");
            errorPopup.initModality(Modality.APPLICATION_MODAL);
            ErrorController controller = error.getController();
            controller.init();
            controller.setErrors(errors);
            controller.setPromptType("error");
            controller.displayErrors();
            errorPopup.setAlwaysOnTop(true);
            errorPopup.showAndWait();
        } catch (IOException e) {
            logManager.error(e.getMessage());
        }
    }

    /**
     * Deletes the selected vehicle
     */
    @FXML
    public void confirmDelete() {
        if (selectedVehicle != null) {
            manage.deleteVehicle(selectedVehicle);
            selectedVehicle = null;
        }
        cancel();
    }

    /**
     * Cancels and closes the window
     */
    @FXML
    public void cancel() {
        MainWindow.setController(prevController);
        prevController = null;
        stage.close();
    }

}
