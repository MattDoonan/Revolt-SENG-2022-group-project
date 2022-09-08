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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng202.team3.data.entity.Vehicle;


/**
 * Controller for the vehicle.fxml window
 * 
 * @author Team 3
 * @version 1.0.3, Aug 22
 */
public class VehicleController {

    // private static final Logger log = LogManager.getLogger();


    @FXML
    private ListView<String> vehicleList;

    @FXML
    private TextArea vehicleDisplayOne;

    @FXML
    private TextArea vehicleDisplayTwo;

    @FXML
    private TextArea vehicleDisplayThree;

    @FXML
    private ImageView vehicleImageOne;

    @FXML
    private ImageView vehicleImageTwo;

    @FXML
    private ImageView vehicleImageThree;

    @FXML
    private Button addVehicle;

    @FXML
    private Button nextBtn;

    @FXML
    private Button prevBtn;

    @FXML
    private Button addConnection;

    @FXML
    private Button addVehicleBtn;

    @FXML
    private Label addedConnections;

    @FXML
    private TextField licenseText;

    @FXML
    private TextField makeText;

    @FXML
    private TextField modelText;

    @FXML
    private TextField curChargeText;

    @FXML
    private TextField maxRangeText;

    @FXML
    private TextField connectionsText;

    @FXML
    private Label imgName;

    @FXML
    private ScrollPane imgsDisplay;

    private ObservableList<Vehicle> vehicleData = FXCollections.observableArrayList();

    private ObservableList<Button> imgBtns = FXCollections.observableArrayList();

    private ArrayList<String> connections = new ArrayList<String>();

    private String[] imgNames = {"car_one.png", "car_two.png", "car_three.png", 
        "truck_one.png", "truck_two.png"};

    private String selectedImg;
    
    private Button saveImg = new Button("Select");

    private Stage popup = new Stage();


    /**
     * Initialize the window
     *
     */
    public void init() {
        makeTestVehicles();
        setData(vehicleDisplayOne, vehicleImageOne, 0);

        if (vehicleData.size() > 1) {
            setData(vehicleDisplayTwo, vehicleImageTwo, 1);
            setData(vehicleDisplayThree, vehicleImageThree, 2);
        }

        addVehicle.setOnAction(e -> displayPopup());

    }

    /**
     * Displays pop-up window to add a new vehicle to the garage
     */
    public void displayPopup() {
        try {

            Parent root = FXMLLoader.load(getClass().getResource("/fxml/new_vehicle.fxml"));
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setResizable(false);
            popup.setTitle("Add Vehicle");
            popup.setScene(new Scene(root, 600, 500));
            popup.showAndWait();


        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }

    }


    /**
     * Adds a new connection (for an EV) when the button is clicked
     */
    @FXML
    public void addConnection() {
        connections.add(connectionsText.getText());
        addedConnections.setText("Connection: "
                + connectionsText.getText() + "\n" + addedConnections.getText());
        connectionsText.setText(null);
    }

    /**
     * Adds the new vehicle to the list of vehicles (doesn't work quite yet)
     */
    @FXML
    public void addVehicle() {
        Vehicle vehicle = new Vehicle(licenseText.getText(), makeText.getText(),
                modelText.getText(), Float.parseFloat(curChargeText.getText()),
                Integer.parseInt(maxRangeText.getText()), connections);
        vehicle.setImgPath("src/main/resources/images/" + selectedImg);
        vehicleData.add(vehicle);

        System.out.println(vehicle);

        Stage popupStage = (Stage) addVehicleBtn.getScene().getWindow();
        popupStage.close();
    }


    /**
     * Lets the user select an image (from a list) to represent their vehicle
     */
    @FXML
    public void selectImg() {
        try {

            saveImg.setOnAction(e -> save());

            saveImg.setLayoutX(538);
            saveImg.setLayoutY(360);


            for (int i = 0; i < imgNames.length; i++) {

                Image img = new Image(new FileInputStream("src/main/resources/images/" 
                    + imgNames[i]));
                ImageView view = new ImageView(img);
                Button button = new Button();
                button.setGraphic(view);
                button.setId(imgNames[i]);
                imgBtns.add(button);

                button.setOnAction(e -> btnSelected(e));
            }
            VBox anchor = new VBox();
            anchor.getChildren().addAll(imgBtns);

            ScrollPane imgsDisplay = new ScrollPane();
            imgsDisplay.setPrefViewportWidth(570);
            imgsDisplay.setPrefViewportHeight(330);

            imgsDisplay.setContent(anchor);
            imgsDisplay.setPannable(true); 

            AnchorPane root = new AnchorPane();
            root.getChildren().addAll(imgsDisplay, saveImg);

            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setResizable(false);
            popup.setTitle("Select Image");
            popup.setScene(new Scene(root, 600, 400));
            popup.showAndWait();


        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }

    /**
     * Updates selectedImg to the ID of the currently-selected image
     * @param e ActionEvent
     */
    public void btnSelected(ActionEvent e) {
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
     * Populate display with index-th vehicle
     *
     * @param display   location to put the text
     * @param imageview path to vehicle image
     * @param index     index of the vehicle to display
     */
    public void setData(TextArea display, ImageView imageview, int index) {
        display.setText(vehicleData.get(index).getMake() + " " 
                + vehicleData.get(index).getModel() + "\n\n"
                + "License Plate: " + vehicleData.get(index).getLicensePlate() + "\n"
                + "Current Charge: " + vehicleData.get(index).getBatteryPercent() + "\n"
                + "Max. Range: " + vehicleData.get(index).getMaxRange() + "\n"
                + "Connections: " + vehicleData.get(index).getConnectors().toString());

        
        try {
            if (vehicleData.get(index).getImgPath() != null) {
                Image image = new Image(new FileInputStream(vehicleData.get(index).getImgPath()));
                imageview.setImage(image);
            } else {
                imageview.setImage(null);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }  

    }

    /**
     * Creates test vehicles
     * TODO: Remove once data can be properly pulled from the database
     */
    public void makeTestVehicles() {

        vehicleData.add(
                new Vehicle("ABC123", "Nissan", "Leaf", (float) 60.5, 270,
                        new ArrayList<>()));
        vehicleData.add(
                new Vehicle("AAA111", "Nissan", "Leaf E+", (float) 87, 385,
                        new ArrayList<>()));
        vehicleData.add(
                new Vehicle("XYZ789", "Tesla", "X", (float) 100.5, 536,
                        new ArrayList<>()));
        vehicleData.add(
                new Vehicle("QWE768", "Jaguar", "I-Pace", (float) 40, 470,
                        new ArrayList<>()));
        vehicleData.add(
                new Vehicle("FJG788", "Audi", "E-Tron", (float) 63, 441,
                        new ArrayList<>()));
        vehicleData.add(
                new Vehicle("WWW333", "Tesla", "5", (float) 43, 637,
                        new ArrayList<>()));
        vehicleData.add(
                new Vehicle("HGJ449", "Mercedes Benz", "EQC", (float) 77, 430,
                        new ArrayList<>()));
        vehicleData.add(
                new Vehicle("IUH909", "Porsche", "Taycan", (float) 98, 480,
                        new ArrayList<>()));
        vehicleData.get(0).setImgPath("src/main/resources/images/car_one.png");
        vehicleData.get(1).setImgPath("src/main/resources/images/car_three.png");
        vehicleData.get(2).setImgPath("src/main/resources/images/car_two.png");
        vehicleData.get(3).setImgPath("src/main/resources/images/car_one.png");
        vehicleData.get(4).setImgPath("src/main/resources/images/truck_one.png");
        vehicleData.get(5).setImgPath("src/main/resources/images/car_three.png");
        vehicleData.get(6).setImgPath("src/main/resources/images/car_two.png");
        vehicleData.get(7).setImgPath("src/main/resources/images/truck_two.png");
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
            setData(vehicleDisplayOne, vehicleImageOne, 0);
            setData(vehicleDisplayTwo, vehicleImageTwo, 1);
            setData(vehicleDisplayThree, vehicleImageThree, 2);
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
            setData(vehicleDisplayOne, vehicleImageOne, 0);
            setData(vehicleDisplayTwo, vehicleImageTwo, 1);
            setData(vehicleDisplayThree, vehicleImageThree, 2);
        }
    }
}
