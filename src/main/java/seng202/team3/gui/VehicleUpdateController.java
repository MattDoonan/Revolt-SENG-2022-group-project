package seng202.team3.gui;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Vehicle;

/**
 * A vehicle editing controller
 *
 * @author Celia Allen
 * @version 1.0.0, Sep 22
 */
public class VehicleUpdateController {

    @FXML
    private TextField makeText;

    @FXML
    private TextField modelText;

    @FXML
    private TextField maxRangeText;

    @FXML
    private Label imgName;

    @FXML
    private Label addedConnections;

    @FXML
    private ComboBox<String> connectorType;

    @FXML
    private Button saveChanges;
    
    @FXML
    private Label inputBox;

    private Button saveImg = new Button("Select");

    private Vehicle selectedVehicle;

    private String selectedImg = "car_one.png";

    private ArrayList<String> connections = new ArrayList<String>();

    private ObservableList<Button> imgBtns = FXCollections.observableArrayList();

    private String[] imgNames = {"car_one.png", "car_two.png", "car_three.png",
        "truck_one.png", "truck_two.png"};

    private Stage imagePopup = new Stage();

    private Stage connectorPopup = new Stage();

    /**
     * Initialises the Vehicle editing
     */
    public VehicleUpdateController() {
    }

    /**
     * Set selectedVehicle
     * @param vehicle the vehicle to set selectedVehicle to
     */
    public void setSelectedVehicle(Vehicle vehicle) {
        this.selectedVehicle = vehicle;
    }


    /**
     * Saves the changes; if new, will use the coordinates to make an entry
     */
    @FXML
    public void saveChanges() {
        Vehicle vehicle;
        // System.out.println("selectedVehicle: " + selectedVehicle);
        if (selectedVehicle != null) {
            if (selectedImg != null) {
                selectedVehicle.setImgPath("src/main/resources/images/" + selectedImg);
            }
            selectedVehicle.setConnectors(connections);
            selectedVehicle.setMaxRange(Integer.parseInt(maxRangeText.getText()));
            selectedVehicle.setModel(modelText.getText());
            selectedVehicle.setMake(makeText.getText());
            vehicle = selectedVehicle;
            // System.out.println("VControl selectedVehicleID: " + selectedVehicle.getVehicleId());
        } else {
            vehicle = new Vehicle(makeText.getText(),
            modelText.getText(),
            Integer.parseInt(maxRangeText.getText()), connections);
            vehicle.setImgPath("src/main/resources/images/" + selectedImg);
            // System.out.println("VControl vehicleid: " + vehicle.getVehicleId());
        }
        // System.out.println("VControl VehicleID: " + vehicle.getVehicleId());
        try {
            SqlInterpreter.getInstance().writeVehicle(vehicle);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            makeText.setText(null);
            modelText.setText(null);
            maxRangeText.setText(null);
            addedConnections.setText(null);
            imgName.setText(null);
            connections = new ArrayList<>();
            connectorType.setPromptText("Connector Type");
        }
        selectedVehicle = null;
        Stage popupStage = (Stage) saveChanges.getScene().getWindow();
        popupStage.close();
    }


    /**
     * Adds a new connection (for an EV) when the button is clicked
     */
    @FXML
    public void addConnection() {
        if (connectorType.getValue().equals("Other...")) {
            GridPane root = new GridPane();
            Button save = new Button("Save");
            TextField connector = new TextField("Connector Type");
            root.addRow(0, connector);
            root.addRow(1, save);
            root.setStyle("-fx-padding: 20;");
            save.setOnMouseClicked((MouseEvent event) -> {
                connections.add(connector.getText());
                addedConnections.setText("Connection: "
                        + connector.getText() + "\n" + addedConnections.getText());
                Stage connectorStage = (Stage) save.getScene().getWindow();
                connectorStage.close();
            });
            connectorPopup.initModality(Modality.APPLICATION_MODAL);
            connectorPopup.setResizable(false);
            connectorPopup.setTitle("Other Connector");
            connectorPopup.setScene(new Scene(root, 300, 100));
            connectorPopup.showAndWait();
        } else {
            connections.add(connectorType.getValue());
            addedConnections.setText("Connection: "
                    + connectorType.getValue() + "\n" + addedConnections.getText());
        }
    }


    /**
     * Lets the user select an image (from a list) to represent their vehicle
     */
    @FXML
    public void selectImg() {
        try {
            saveImg.setOnAction(e -> save());
            saveImg.setLayoutX(280);
            saveImg.setLayoutY(360);
            for (int i = 0; i < imgNames.length; i++) {
                Image img = new Image(new FileInputStream("src/main/resources/images/"
                    + imgNames[i]));
                ImageView view = new ImageView(img);
                Button button = new Button();
                button.setGraphic(view);
                button.setId(imgNames[i]);
                imgBtns.add(button);
                button.setOnAction(e -> imgSelected(e));
            }
            displayImgSelect();
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "File not found.", e);
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
        imgsDisplay.setStyle("-fx-background-color: white;");

        // imagePopup.setScene(new Scene(imgsDisplay, 337, 450));
        // imagePopup.show();
        AnchorPane root = new AnchorPane();
        root.getChildren().addAll(imgsDisplay, saveImg);
        imagePopup.initModality(Modality.APPLICATION_MODAL);
        imagePopup.setResizable(false);
        imagePopup.setTitle("Select Image");
        imagePopup.setScene(new Scene(root, 337, 400));


        // TilePane anchor = new TilePane();

        // for (int i = 0; i < imgBtns.size(); i++) {
        //     anchor.getChildren().add(imgBtns.get(i));
        // }

        // anchor.setTileAlignment(Pos.TOP_LEFT);

        // // anchor.getChildren().addAll(imgBtns);
        // ScrollPane imgsDisplay = new ScrollPane();
        // imgsDisplay.setPrefViewportWidth(230);
        // imgsDisplay.setPrefViewportHeight(450);
        // imgsDisplay.setContent(anchor);
        // imgsDisplay.setHbarPolicy(ScrollBarPolicy.NEVER);
        // imgsDisplay.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        // // imgsDisplay.setPannable(true);
        // // AnchorPane root = new AnchorPane();
        // // root.getChildren().addAll(anchor, saveImg);
        // imagePopup.initModality(Modality.APPLICATION_MODAL);
        // imagePopup.setResizable(false);
        // imagePopup.setTitle("Select Image");
        // imagePopup.setScene(new Scene(imgsDisplay, 337, 450));
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
     * @param e ActionEvent
     */
    public void imgSelected(ActionEvent e) {
        selectedImg = ((Node) e.getSource()).getId();
    }


    /**
     * Saves the currently selected image for the user's vehicle
     */
    public void save() {
        System.out.println(selectedImg);
        imgName.setText(selectedImg);
        Stage popupStage = (Stage) saveImg.getScene().getWindow();
        popupStage.close();
    }


    /**
     * Displays all the info of the vehicle, if there is a vehicle
     */
    public void displayInfo(Vehicle vehicle) {
        selectedVehicle = vehicle;
        if (vehicle != null) {
            makeText.setText(vehicle.getMake());
            modelText.setText(vehicle.getModel());
            maxRangeText.setText(Integer.toString(vehicle.getMaxRange()));
            addedConnections.setText(vehicle.getConnectors().toString());
            imgName.setText(vehicle.getImgPath().replace("src/main/resources/images/", ""));
            connections = vehicle.getConnectors();
        }
    }


    /**
     * Deletes the selected vehicle
     */
    @FXML
    public void confirmDelete() {
        if (selectedVehicle != null) {
            try {
                SqlInterpreter.getInstance().deleteData("vehicle", 
                    selectedVehicle.getVehicleId());
                selectedVehicle = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cancel();
    }

    /**
     * Cancels and closes the window
     */
    @FXML
    public void cancel() {
        Stage stage = (Stage) inputBox.getScene().getWindow();
        stage.close();
    }


}
