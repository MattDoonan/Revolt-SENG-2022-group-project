package seng202.team3.gui.controller;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.data.entity.Vehicle;
import seng202.team3.gui.MainWindow;
import seng202.team3.gui.util.ErrorHandler;
import seng202.team3.logic.manager.UserManager;
import seng202.team3.logic.manager.VehicleUpdateManager;

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
     * Lable to display the name of the chosen vehicle image
     */
    @FXML
    private Label imgName;

    /**
     * Lable to display the currently added connections
     */
    @FXML
    private ListView<GridPane> addedConnections;

    /**
     * Dropdown of the connector types
     */
    @FXML
    private ComboBox<String> connectorType;

    /**
     * Button to save a connector
     */
    @FXML
    private Button addConnectionBtn;

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
    private static final Border INVALID_STYLE = new Border(
            new BorderStroke(Color.RED, BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY, BorderWidths.DEFAULT));

    /**
     * Styling for invalid fields
     */
    private static final Border VALID_STYLE = new Border(
            new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID,
                    CornerRadii.EMPTY, new BorderWidths(0.5)));

    /**
     * Default path for images
     */
    private static final String IMGPATH = "/images/";

    /**
     * id for make node
     */
    private static final String MAKE_NODE = "makeText";

    /**
     * id for model node
     */
    private static final String MODEL_NODE = "modelText";

    /**
     * id for range node
     */
    private static final String RANGE_NODE = "maxRangeText";

    /**
     * id for connector node
     */
    private static final String CONNECTOR_NODE = "connectorType";

    /**
     * Delete button text
     */
    private static final String DELETE_BUTTON_TEXT = "Delete";

    /**
     * Connection prompt text
     */
    private static final String CONN_PROMPT_TEXT = "Connection: ";

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
     * Stores all of the tooltips used for error messages
     */
    private ErrorHandler errors = new ErrorHandler();

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
        errors = new ErrorHandler();
        errors.add(MAKE_NODE, "Vehicle make required.");
        errors.add(MODEL_NODE, "Vehicle model required.");
        errors.add(RANGE_NODE, "Max range required");
        errors.add(CONNECTOR_NODE,
                "A vehicle must have at least one connector.");

        makeText.setBorder(VALID_STYLE);
        modelText.setBorder(VALID_STYLE);
        maxRangeText.setBorder(VALID_STYLE);
        connectorType.setBorder(VALID_STYLE);
        
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
            vehicle = selectedVehicle;
        } else {
            vehicle = new Vehicle();
        }

        if (selectedImg != null) {
            vehicle.setImgPath(IMGPATH + selectedImg);
        } else {
            vehicle.setImgPath(IMGPATH + "car_one.png");
        }

        Boolean fail = checkForErrors();

        if (Boolean.TRUE.equals(fail)) {
            invalidVehicle.setVisible(true);
            logManager.warn("Incorrect vehicle details");
        } else {
            vehicle.setOwner(UserManager.getUser().getId());
            vehicle.setMake(makeText.getText());
            vehicle.setModel(modelText.getText());
            vehicle.setMaxRange(Integer.parseInt(maxRangeText.getText()));
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
     * Enables the addConnector button
     */
    @FXML
    public void enableConnectorBtn() {
        addConnectionBtn.setDisable(false);
    }

    /**
     * Checks if there are any errors when a user adds/updates a vehicle.
     * 
     * @return whether there were any errors
     */
    public Boolean checkForErrors() {
        errors.hideAll();

        makeText.setBorder(VALID_STYLE);
        modelText.setBorder(VALID_STYLE);
        maxRangeText.setBorder(VALID_STYLE);
        connectorType.setBorder(VALID_STYLE);

        Boolean fail = false;

        if (makeText.getText().isEmpty()) {
            makeText.setBorder(INVALID_STYLE);
            errors.show(MAKE_NODE);
            fail = true;
        } else if (makeText.getText().length() > 30) {
            makeText.setBorder(INVALID_STYLE);
            errors.changeMessage(MAKE_NODE, "Vehicle make cannot be longer than 30 characters");
            errors.show(MAKE_NODE);
            fail = true;
        }
        if (modelText.getText().isEmpty()) {
            modelText.setBorder(INVALID_STYLE);
            errors.show(MODEL_NODE);
            fail = true;
        } else if (modelText.getText().length() > 30) {
            modelText.setBorder(INVALID_STYLE);
            errors.changeMessage(MODEL_NODE, "Vehicle model cannot be longer than 30 characters");
            errors.show(MODEL_NODE);
            fail = true;
        }
        Boolean rangeFlag = false;
        try {
            if (maxRangeText.getText().isEmpty()) {
                errors.changeMessage(RANGE_NODE, "Max. range required.");
                rangeFlag = true;
            } else if (Integer.parseInt(maxRangeText.getText()) < 0) {
                errors.changeMessage(RANGE_NODE, "Max. range cannot be negative.");
                rangeFlag = true;
            } else if (Integer.parseInt(maxRangeText.getText()) > 1000) {
                errors.changeMessage(RANGE_NODE, "Max. range cannot be larger than 1000 km.");
                rangeFlag = true;
            }
        } catch (NumberFormatException e) {
            errors.changeMessage(RANGE_NODE, "Max. range must be a whole number.");
            rangeFlag = true;
        }

        if (Boolean.TRUE.equals(rangeFlag)) {
            maxRangeText.setBorder(INVALID_STYLE);
            errors.show(RANGE_NODE);
            fail = true;
        }

        if (connections.isEmpty()) {
            connectorType.setBorder(INVALID_STYLE);
            errors.show(CONNECTOR_NODE);
            fail = true;
        }

        return fail;
    }

    /**
     * Adds a new connection (for an EV) when the button is clicked
     */
    public void addConnection() {
        if (connectorType.getValue() != null) {
            connectorType.setBorder(VALID_STYLE);
            if (connectorType.getValue().equals("Other...")) {
                GridPane root = new GridPane();
                Button save = new Button("Save");
                TextField connector = new TextField("Connector Type");
                root.addRow(0, connector);
                root.addRow(1, save);
                root.setStyle("-fx-padding: 20;");
                save.setOnMouseClicked((MouseEvent event) -> {
                    Button button = new Button(DELETE_BUTTON_TEXT);
                    button.setStyle("-fx-border-color: #a9a9a9;");
                    button.setId(connector.getText());
                    button.setOnAction(this::deleteConnection);
                    Label label = new Label(CONN_PROMPT_TEXT + connector.getText());
                    label.setMaxWidth(190);
                    label.setMinWidth(190);
                    GridPane gridpane = new GridPane();
                    gridpane.add(label, 1, 0);
                    gridpane.add(button, 2, 0);
                    gridpane.getColumnConstraints().add(new ColumnConstraints(0));
                    gridpane.getColumnConstraints().add(new ColumnConstraints(210));
                    GridPane.setHalignment(button, HPos.CENTER);
                    addedConnections.getItems().add(gridpane);

                    Stage connectorStage = (Stage) save.getScene().getWindow();
                    connectorStage.close();
                });
                connectorPopup.initModality(Modality.APPLICATION_MODAL);
                connectorPopup.setResizable(false);
                connectorPopup.setTitle("Other Connector");
                connectorPopup.setScene(new Scene(root, 300, 100));
            } else if (!connections.contains(connectorType.getValue())) {
                connections.add(connectorType.getValue());
                Button button = new Button(DELETE_BUTTON_TEXT);
                button.setStyle("-fx-border-color: #a9a9a9;");
                button.setId(connectorType.getValue());
                button.setOnAction(this::deleteConnection);
                Label label = new Label(CONN_PROMPT_TEXT + connectorType.getValue());
                label.setMaxWidth(190);
                label.setMinWidth(190);

                GridPane gridpane = new GridPane();
                gridpane.add(label, 1, 0);
                gridpane.add(button, 2, 0);
                gridpane.getColumnConstraints().add(new ColumnConstraints(0));
                gridpane.getColumnConstraints().add(new ColumnConstraints(210));
                GridPane.setHalignment(button, HPos.RIGHT);
                GridPane.setHalignment(label, HPos.LEFT);
                addedConnections.getItems().add(gridpane);
            } else {
                errors.changeMessage(CONNECTOR_NODE, "Can only have one type of each connector.");
                connectorType.setBorder(INVALID_STYLE);
                errors.show(CONNECTOR_NODE);
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
                addConnectionBtn.setDisable(true);
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
            imgName.setText(vehicle.getImgPath().replace(IMGPATH, ""));
            selectedImg = vehicle.getImgPath().replace(IMGPATH, "");
            connections = vehicle.getConnectors();
            for (String connection : connections) {
                Button button = new Button(DELETE_BUTTON_TEXT);
                button.setStyle("-fx-border-color: #a9a9a9;");
                button.setId(connection);
                button.setOnAction(this::deleteConnection);
                Label label = new Label(CONN_PROMPT_TEXT + connection);
                HBox hbox = new HBox();
                Region filler = new Region();
                HBox.setHgrow(filler, Priority.ALWAYS);
                hbox.getChildren().addAll(label, filler, button);

                GridPane gridpane = new GridPane();
                gridpane.add(label, 1, 0);
                gridpane.add(button, 2, 0);
                GridPane.setHalignment(button, HPos.RIGHT);

                addedConnections.getItems().add(gridpane);
            }
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

    /**
     * Gets the manager of this vehicle add
     *
     * @return the {@link VehicleUpdateManager} of this controller
     */
    public VehicleUpdateManager getManage() {
        return manage;
    }

    /**
     * Gets error handling object for tooltip messages
     * 
     * @return errorhandler with entry field tooltip alerts
     */
    public ErrorHandler getErrors() {
        return errors;
    }
}
