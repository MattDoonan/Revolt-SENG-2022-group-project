package seng202.team3.gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.entity.Charger;
import seng202.team3.logic.Calculations;
import seng202.team3.logic.MainManager;
import seng202.team3.logic.MapManager;

/**
 * Controller for the main.fxml window (the home)
 * 
 * @author Matthew Doonan, Michelle Hsieh
 * @version 1.0.1, Aug 22
 */
public class MainController {

    @FXML
    private CheckBox acButton;

    @FXML
    private Slider changeDistance;

    @FXML
    private TextField searchCharger;

    @FXML
    private CheckBox dcButton;

    @FXML
    private HBox displayInfo;

    @FXML
    private CheckBox distanceDisplay;

    @FXML
    private VBox chargerTable;

    @FXML
    private CheckBox attractionButton;

    @FXML
    private CheckBox noChargingCost;

    @FXML
    private CheckBox hasChargingCost;

    @FXML
    private BorderPane mainWindow;

    @FXML
    private CheckBox toggleTimeLimit;

    @FXML
    private Slider timeLimit;

    @FXML
    private CheckBox onParkingFilter;

    @FXML
    private Slider parkingLot;

    @FXML
    private CheckBox withoutCarparkCost;

    @FXML
    private CheckBox withCarparkCost;

    @FXML
    private CheckBox openAllButton;

    @FXML
    private CheckBox notOpenAllButton;

    @FXML
    private CheckBox noNearbyAttraction;

    private BorderPane menuWindow;

    private Stage stage;

    private MapViewController mapController;

    private MainManager manage;

    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage, BorderPane menuWindow) {
        this.stage = stage;
        this.menuWindow = menuWindow;
        manage = new MainManager();
        loadMapView(this.stage);
        manage.resetQuery();
        manage.makeAllChargers();
        manage.setDistance(changeDistance.getValue());
        addChargersToDisplay(manage.getCloseChargerData());
        change();

    }

    /**
     * Display charger info on panel
     * 
     * @param c charger to display information about
     */
    public void viewChargers(Charger c) {
        // Clears the HBox of nodes (items)
        displayInfo.getChildren().removeAll(displayInfo.getChildren());
        // Check if there is no charger
        if (c == null) {
            if (manage.getCloseChargerData().size() != 0) {
                manage.setSelectedCharger(manage.getCloseChargerData().get(0));
                viewChargers(manage.getCloseChargerData().get(0));
            } else {
                displayInfo.getChildren().add(new Text("No Charger Selected"));
                displayInfo.setAlignment(Pos.CENTER);
            }
        } else {
            try {
                // Gets image for charger
                ImageView image = new ImageView(new Image(
                        new FileInputStream("src/main/resources/images/charger.png")));
                // Edits the width and height to 150px
                image.setFitHeight(150);
                image.setFitWidth(150);
                displayInfo.getChildren().add(image); // adds to the HBox
            } catch (FileNotFoundException e) {
                Label image = new Label("Image");
                displayInfo.getChildren().add(image);
            }
            VBox display = new VBox(); // Creates Vbox to contain text
            display.getChildren().add(new Text("" + c.getName() + ""));
            display.getChildren().add(new Text("" + c.getLocation().getAddress() + "\n"));
            String word = manage.getConnectors(c);
            display.getChildren().add(new Text("Current types " + word + ""));
            // If statements are there to make different text depending on the charger info
            if (c.getOperator() != null) {
                display.getChildren().add(new Text("Operator is: " + c.getOperator() + ""));
            }
            display.getChildren().add(new Text("Owner is: " + c.getOwner() + ""));
            if (c.getChargeCost()) {
                display.getChildren().add(new Text("Charger has a cost"));
            } else {
                display.getChildren().add(new Text("Charger has no cost"));
            }
            if (c.getAvailable24Hrs()) {
                display.getChildren().add(new Text("Open 24"));
            } else {
                display.getChildren().add(new Text("Open 24 hours"));
            }
            display.getChildren().add(new Text("Has " + c.getAvailableParks() + " parking spaces"));
            if (c.getTimeLimit() == Double.POSITIVE_INFINITY) {
                display.getChildren().add(new Text("Has no time limit"));
            } else {
                display.getChildren().add(new Text("Has " + c.getTimeLimit() + " minute limit"));
            }
            if (c.getHasAttraction()) {
                display.getChildren().add(new Text("Has near by attraction"));
            }
            // Adds the charger info to the HBox
            displayInfo.getChildren().add(display);
            getManager().setSelectedCharger(c);
        }
    }

    /**
     * Changes active charger on selected and moves the map
     */
    public void selectToView(int number) {
        Charger selectedCharger = manage.getCloseChargerData().get(number);
        manage.setSelectedCharger(selectedCharger);
        viewChargers(selectedCharger);
        if (mapController != null) {
            mapController.changePosition(selectedCharger.getLocation());
        }
    }

    /**
     * Adds every charger in charger list to the vbox
     */
    public void addChargersToDisplay(ObservableList<Charger> chargersToAdd) {

        chargerTable.getChildren().removeAll(chargerTable.getChildren()); // clears vbox
        for (int i = 0; i < chargersToAdd.size(); i++) {
            HBox add = new HBox(); // creates HBox that will contain the changer info
            try {
                // Gets image and adds it to an Image View
                ImageView image = new ImageView(new Image(
                        new FileInputStream("src/main/resources/images/charger.png")));
                add.getChildren().add(image);
            } catch (FileNotFoundException e) {
                Label image = new Label("Image");
                add.getChildren().add(image); // adds to the HBox
            }
            // Create Vbox to contain the charger info
            VBox content = new VBox(new Text(chargersToAdd.get(i).getName()),
                    new Text(chargersToAdd.get(i).getLocation().getAddress()),
                    new Text(chargersToAdd.get(i).getOperator()),
                    new Text("\n" + Math.round(Calculations.calculateDistance(
                            manage.getPosition(), chargersToAdd.get(i).getLocation()))
                            * 10.0 / 10.0 + "km"));
            add.getChildren().add(content); // Adds charger content to HBox
            add.setPadding(new Insets(10));
            add.setSpacing(10);
            int finalI = i;
            // Sets on click functionally
            add.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
                selectToView(finalI);
            });
            // Changes Hover style
            add.addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, event -> {
                add.setStyle("-fx-background-color:#FFF8EB;");
            });
            // Changes off hover style
            add.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, event -> {
                add.setStyle("-fx-background-color:#FFFFFF;");
            });
            // Adds the HBox to the main VBox
            chargerTable.getChildren().add(add);
        }
        if (getMapController().getConnectorStatus()) {
            getMapController().addChargersOnMap();
        }
        if (chargerTable.getChildren().size() != 0) {
            viewChargers(chargersToAdd.get(0));
        } else {
            viewChargers(null);
        }
    }

    /**
     * Refresh the vbox filled with chargers
     */
    public void refreshTable() {
        addChargersToDisplay(manage.getCloseChargerData());
    }

    /**
     * Sets the Original text and updates value for the slider filter
     * names so the user can see what they have input
     */
    public void change() {
        distanceDisplay.textProperty()
                .setValue("Minimum distance (" + Math.round(changeDistance.getValue()) + " km)");
        onParkingFilter.textProperty()
                .setValue("Minimum number of spaces ("
                        + Math.round(parkingLot.getValue()) + ")");
        toggleTimeLimit.textProperty()
                .setValue("Minimum time limit of ("
                        + Math.round(timeLimit.getValue()) + " minutes)");

        changeDistance.valueProperty().addListener((observableValue, number, t1) -> {
            distanceDisplay.textProperty()
                    .setValue("Minimum distance ("
                            + Math.round(changeDistance.getValue()) + " km)");
        });
        parkingLot.valueProperty().addListener((observableValue, number, t1) -> {
            onParkingFilter.textProperty()
                    .setValue("Minimum number of spaces ("
                            + Math.round(parkingLot.getValue()) + ")");
        });
        timeLimit.valueProperty().addListener((observableValue, number, t1) -> {
            toggleTimeLimit.textProperty()
                    .setValue("Minimum time limit of ("
                            + Math.round(timeLimit.getValue()) + " minutes)");
        });
    }

    /**
     * Adds queries onto the query builder according to the current buttons selected
     */
    public void executeSearch() {
        manage.resetQuery();
        if (toggleTimeLimit.isSelected()) {
            manage.adjustQuery("maxtimelimit",
                    Double.toString(timeLimit.getValue()), ComparisonType.GREATER_THAN_EQUAL);
        }
        if (onParkingFilter.isSelected()) {
            manage.adjustQuery("carparkcount",
                    Double.toString(parkingLot.getValue()), ComparisonType.GREATER_THAN_EQUAL);
        }
        if (withoutCarparkCost.isSelected()) {
            manage.adjustQuery("hascarparkcost", "False", ComparisonType.EQUAL);
        }
        if (withCarparkCost.isSelected()) {
            manage.adjustQuery("hascarparkcost", "True", ComparisonType.EQUAL);
        }
        if (acButton.isSelected()) {
            manage.adjustQuery("currenttype", "AC", ComparisonType.CONTAINS);
        }
        if (dcButton.isSelected()) {
            manage.adjustQuery("currenttype", "DC", ComparisonType.CONTAINS);
        }
        if (openAllButton.isSelected()) {
            manage.adjustQuery("is24hours", "True", ComparisonType.EQUAL);
        }
        if (notOpenAllButton.isSelected()) {
            manage.adjustQuery("is24hours", "False", ComparisonType.EQUAL);
        }
        if (attractionButton.isSelected()) {
            manage.adjustQuery("hastouristattraction", "True", ComparisonType.EQUAL);
        }
        if (noNearbyAttraction.isSelected()) {
            manage.adjustQuery("hastouristattraction", "False", ComparisonType.EQUAL);
        }
        if (noChargingCost.isSelected()) {
            manage.adjustQuery("haschargingcost", "False", ComparisonType.EQUAL);
        }
        if (hasChargingCost.isSelected()) {
            manage.adjustQuery("haschargingcost", "True", ComparisonType.EQUAL);
        }
        if (searchCharger.getText().length() != 0) {
            manage.adjustQuery("address", searchCharger.getText(), ComparisonType.CONTAINS);
        }

        manage.makeAllChargers();
        if (distanceDisplay.isSelected()) {
            manage.setDistance(changeDistance.getValue());
        } else {
            manage.setDistance(0);
        }
        ObservableList<Charger> chargers = manage.getCloseChargerData();
        addChargersToDisplay(chargers);
        if (chargers.size() != 0) {
            mapController.changePosition(chargers.get(0).getLocation());
        }
    }

    /**
     * Gets the Map View Controller
     *
     * @return MapViewController of map viewer
     */
    public MapViewController getMapController() {
        return mapController;
    }

    /**
     * Loads the map view into the main part of the main window
     *
     * @param stage stage to load with
     */
    private void loadMapView(Stage stage) {
        try {
            FXMLLoader webViewLoader = new FXMLLoader(getClass().getResource("/fxml/map.fxml"));
            Parent mapViewParent = webViewLoader.load();

            mapController = webViewLoader.getController();
            MapManager mapManager = new MapManager(manage);
            mapController.init(stage, mapManager);

            mainWindow.setCenter(mapViewParent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the MainManager created by the MainController
     *
     * @return {@link MainManager} the manager of this controller
     */
    public MainManager getManager() {
        return manage;
    }

    /**
     * Gets the manager to edit the charger
     */
    public void editCharger() {
        manage.editCharger();
    }

    /**
     * Toggles the route view on.
     */
    public void toggleRoute() {
        mapController.toggleRoute();
    }

    /**
     * Initialises the welcome page;
     */
    public void loadTableView() {
        try {
            FXMLLoader mainScene = new FXMLLoader(getClass()
                    .getResource("/fxml/main_table.fxml"));
            Parent mainNode = mainScene.load();
            TableController controller = mainScene.getController();
            controller.init();
            menuWindow.setCenter(mainNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
