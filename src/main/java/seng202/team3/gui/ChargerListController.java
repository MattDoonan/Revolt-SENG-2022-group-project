package seng202.team3.gui;

import java.io.BufferedInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.logic.Calculations;

/**
 * Class controls the charger list
 *
 * @author Matthew Doonan
 * @version 1.0.0
 */
public class ChargerListController {

    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

    /**
     * A VBox for charging table
     */
    @FXML
    private VBox chargerTable;

    /**
     * An HBox to display info
     */
    @FXML
    private HBox displayInfo;

    /**
     * The larger display Hbox
     */
    @FXML
    private VBox largeDisplayInfo;

    /**
     * The default image to be used
     */
    private Image image;

    /**
     * The map manager
     */
    private MainController manage;

    /**
     * Border plane it inherits
     */
    private BorderPane listView;

    /** Sets rounding for distance */
    private DecimalFormat rounding = new DecimalFormat("0.00"); // rounding to 6 decimal places


    /**
     * unused
     */
    public ChargerListController() {
        //unused
    }

    /**
     * Initialized the borderpane
     * @param chargerList the borderpane
     * @param controller the MainController
     */
    public void chargerListView(BorderPane chargerList, MainController controller) {
        manage = controller;
        listView = chargerList;
        fetchImage();
    }

    /**
     * Initialized the borderpane with the large view
     * @param chargerList the borderpane
     * @param controller the MainController
     */
    public void largerView(BorderPane chargerList, MainController controller) {
        manage = controller;
        listView = chargerList;
        fetchImage();
        loadLargerChargerView(manage.getManager().getSelectedCharger());
    }

    /**
     * Tries to fetch the image
     */
    public void fetchImage() {
        try {
            // Gets image and adds it to an Image View
            image = new Image(
                    new BufferedInputStream(
                            getClass().getResourceAsStream("/images/charger.png")));
        } catch (NullPointerException e) {
            image = null;
            logManager.error(e.getMessage());
        }
    }

    /**
     * Adds every charger in charger list to the vbox
     *
     * @param chargersToAdd a {@link ObservableList} object
     */
    public void addChargersToDisplay(ObservableList<Charger> chargersToAdd) {

        chargerTable.getChildren().removeAll(chargerTable.getChildren()); // clears vbox
        for (int i = 0; i < chargersToAdd.size(); i++) {
            HBox add = new HBox(); // creates HBox that will contain the changer info

            // adds the cached image
            if (image != null) {
                add.getChildren().add(new ImageView(image));
            } else {
                Label substitueText = new Label("Image");
                add.getChildren().add(substitueText); // adds to the HBox
            }
            // Create Vbox to contain the charger info
            VBox content = new VBox(new Text(chargersToAdd.get(i).getName()),
                    new Text(chargersToAdd.get(i).getLocation().getAddress()),
                    new Text(chargersToAdd.get(i).getOperator()),
                    new Text("\n" + rounding.format(Calculations.calculateDistance(
                            manage.getManager().getPosition(), chargersToAdd.get(i).getLocation()))
                            + "km"));
            add.getChildren().add(content); // Adds charger content to HBox
            add.setPadding(new Insets(10));
            add.setSpacing(10);
            int finalI = i;
            add.setId("charger" + finalI + "");
            add.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> selectToView(finalI));
            // Changes Hover style
            add.addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET,
                    event -> add.setStyle("-fx-background-color:#FFF8EB;"));
            // Changes off hover style
            add.addEventHandler(MouseEvent.MOUSE_EXITED_TARGET,
                    event -> add.setStyle("-fx-background-color:#FFFFFF;"));
            // Adds the HBox to the main VBox
            chargerTable.getChildren().add(add);
        }
        if (!chargerTable.getChildren().isEmpty()) {
            if (manage.getManager().getSelectedCharger() == null) {
                viewChargers(chargersToAdd.get(0));
            } else {
                viewChargers(manage.getManager().getSelectedCharger());
            }
        } else {
            viewChargers(null);
        }
    }

    /**
     * Refresh the vbox filled with chargers
     */
    public void refreshTable() {
        manage.getManager()
                .setSelectedCharger(manage.getManager().getCloseChargerData().get(0));
        if (displayInfo != null) {
            addChargersToDisplay(manage.getManager().getCloseChargerData());
        } else {
            viewChargers(manage.getManager().getSelectedCharger());
        }
    }

    /**
     * Gets the image and sets the size
     * @param size the size of the image
     * @return the image as an image view
     */
    private ImageView getImage(Integer size) {
        ImageView chargerImg = null;
        try {
            // Gets image for charger
            chargerImg = new ImageView(new Image(
                    new BufferedInputStream(
                            getClass().getResourceAsStream("/images/charger.png"))));
            // Edits the width and height to 150px
            chargerImg.setFitHeight(size);
            chargerImg.setFitWidth(size);
        } catch (NullPointerException e) {
            logManager.error(e.getMessage());
        }
        return chargerImg;
    }

    /**
     * Load selected charger preview
     *
     * @param c Charger to load in
     */
    private void loadSelectedPreview(Charger c) {
        ImageView chargerImg = getImage(150);
        displayInfo.getChildren().add(chargerImg); // adds to the HBox
        VBox display = new VBox(); // Creates Vbox to contain text

        Text chargerName = new Text(c.getName());
        chargerName.setStyle("-fx-font-size : 17");
        chargerName.setWrappingWidth(325);
        display.getChildren().add(chargerName);

        Text chargerAddress = new Text(c.getLocation().getAddress());
        chargerAddress.setStyle("-fx-font-size : 17");
        chargerAddress.setWrappingWidth(325);
        display.getChildren().add(chargerAddress);

        display.getChildren().add(new Text());
        display.getChildren().add(new Text("Owner is: " + c.getOwner() + ""));
        String word = manage.getManager().getConnectors(c);
        display.getChildren().add(new Text("Current types " + word + ""));
        display.getChildren().add(
                new Text("Has " + c.getAvailableParks() + " parking spaces"));
        display.getChildren().add(new Text("\n" + rounding.format(Calculations.calculateDistance(
                manage.getManager().getPosition(), c.getLocation()))
                + "km"));
        // Adds the charger info to the HBox
        displayInfo.getChildren().add(display);
        manage.getManager().setSelectedCharger(c);
    }

    /**
     * Loads the larger charger area
     * @param c the charger to view
     */
    private void loadLargerChargerView(Charger c) {
        ImageView chargerImg = getImage(200);
        largeDisplayInfo.getChildren().add(chargerImg);

        Text large1 = new Text();
        largeDisplayInfo.getChildren().add(large1);

        Text chargerName = new Text(c.getName());
        chargerName.setStyle("-fx-font-size : 30");
        chargerName.setWrappingWidth(480);
        largeDisplayInfo.getChildren().add(chargerName);

        Text owner = new Text("Owner: " + c.getOwner() + "");
        owner.setStyle("-fx-font-size : 20");
        owner.setWrappingWidth(480);
        largeDisplayInfo.getChildren().add(owner);


        if (c.getOperator() != null) {
            largeDisplayInfo.getChildren().add(
                    new Text("Operator is: " + c.getOperator() + ""));
        }

        Text distance = new Text("" + rounding.format(Calculations.calculateDistance(
                manage.getManager().getPosition(), c.getLocation())) + "km");
        largeDisplayInfo.getChildren().add(distance);

        Text large2 = new Text();
        largeDisplayInfo.getChildren().add(large2);

        Text chargerAddress = new Text(c.getLocation().getAddress());
        chargerAddress.setStyle("-fx-font-size : 20");
        chargerAddress.setWrappingWidth(480);
        largeDisplayInfo.getChildren().add(chargerAddress);
        DecimalFormat f = new DecimalFormat("0.000000"); // rounding to 6 decimal places
        Text chargerLat = new Text("Latitude: " + f.format(c.getLocation().getLat()) + "");
        Text chargerLon = new Text("Longitude: " + f.format(c.getLocation().getLon()) + "");
        largeDisplayInfo.getChildren().addAll(chargerLat, chargerLon);

        Text large3 = new Text();
        largeDisplayInfo.getChildren().add(large3);

        Text info = new Text("General Information");
        info.setStyle("-fx-font-size : 17");
        largeDisplayInfo.getChildren().add(info);
        largeDisplayInfo.getChildren().add(
                new Text("Has " + c.getAvailableParks() + " parking spaces"));
        for (Text t : getMainText(c)) {
            largeDisplayInfo.getChildren().add(t);
        }
        Text large4 = new Text();
        largeDisplayInfo.getChildren().add(large4);

        Text connector = new Text("Connectors");
        connector.setStyle("-fx-font-size : 17");
        largeDisplayInfo.getChildren().add(connector);
        addConnectorsToView(c);
    }

    /**
     * Add connector info the larger display
     * @param c the charger that is being displayed
     */
    private void addConnectorsToView(Charger c) {
        for (Connector con : c.getConnectors()) {
            Text type = new Text("" + con.getType() + "");
            Text count = new Text("Number of connectors: " + con.getCount() + "");
            Text current = new Text("Current: " + con.getCurrent() + "");
            Text power = new Text("Power Type: " + con.getPower() + "");
            Text status = new Text("Status: " + con.getStatus() + "");
            Text spacing = new Text();
            largeDisplayInfo.getChildren().addAll(type, count, current, power, status, spacing);
        }
    }

    /**
     * Returns a list of texts to add to the vbox
     * @param c the charger c
     * @return array list of texts
     */
    private ArrayList<Text> getMainText(Charger c) {
        ArrayList<Text> texts = new ArrayList<Text>();
        // If statements are there to make different text depending on the charger info
        if (c.getChargeCost()) {
            texts.add(new Text("Charger has a cost"));
        } else {
            texts.add(new Text("Charger has no cost"));
        }
        if (c.getAvailable24Hrs()) {
            texts.add(new Text("Open 24/7"));
        } else {
            texts.add(new Text("Not open 24/7"));
        }
        if (c.getTimeLimit() == Double.POSITIVE_INFINITY) {
            texts.add(new Text("Has no time limit"));
        } else {
            texts.add(new Text("Has " + c.getTimeLimit() + " minute limit"));
        }
        if (c.getHasAttraction()) {
            texts.add(new Text("Has near by attraction"));
        }
        return texts;
    }

    /**
     * Changes active charger on selected and moves the map
     *
     * @param number a int
     */
    public void selectToView(int number) {
        Charger selected = manage.getManager().getCloseChargerData().get(number);
        manage.getManager().setSelectedCharger(selected);
        viewChargers(selected);
        if (manage.getMapController() != null) {
            manage.getMapController().changePosition(selected.getLocation());
        }
    }

    /**
     * Display charger info on panel
     *
     * @param c charger to display information about
     */
    public void viewChargers(Charger c) {
        // Clears the HBox of nodes (items)
        if (displayInfo != null) {
            displayInfo.getChildren().removeAll(displayInfo.getChildren());
        } else if (largeDisplayInfo != null) {
            largeDisplayInfo.getChildren().removeAll(largeDisplayInfo.getChildren());
        } else {
            logManager.error("No view box returned");
            return;
        }
        // Check if there is no charger
        if (c == null) {
            if (!manage.getManager().getCloseChargerData().isEmpty()) {
                manage.getManager().setSelectedCharger(
                        manage.getManager().getCloseChargerData().get(0));
                viewChargers(manage.getManager().getCloseChargerData().get(0));
            } else {
                if (displayInfo != null) {
                    displayInfo.getChildren().add(new Text("No Charger Selected"));
                    displayInfo.setAlignment(Pos.CENTER);
                } else {
                    largeDisplayInfo.getChildren().add(new Text("No Charger Selected"));
                }
            }
        } else {
            if (displayInfo != null) {
                loadSelectedPreview(c);
            } else {
                loadLargerChargerView(c);
            }
        }
    }

    /**
     * Sends to the main controller
     */
    @FXML
    public void editCharger() {
        manage.editCharger();
    }

    /**
     * Button call that swaps the views
     */
    @FXML
    public void largeChargerView() {
        manage.createLargeChargerView();
    }

    /**
     * Button call that swaps the views
     */
    @FXML
    public void listChargerView() {
        manage.createListController();
    }

    /**
     * Gets the vbox to check if null
     * @return boolean for if it is null
     */
    public Boolean getChargerTable() {
        if (chargerTable == null) {
            return false;
        }
        return true;
    }
}
