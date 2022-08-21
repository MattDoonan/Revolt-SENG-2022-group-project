package seng202.team3.gui;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.data.entity.Coordinate;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import seng202.team3.logic.Calculations;
import seng202.team3.logic.ChargerManager;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.util.*;

/**
 * Controller for the main.fxml window
 * @author seng202 teaching team
 */
public class MainController {

    // private static final Logger log = LogManager.getLogger();

    // @FXML
    // private Label defaultLabel;


    // @FXML
    // private ListView listOfChargers;

    @FXML
    private TextField searchField;

    @FXML
    private TextField searchCharger;

    @FXML
    private TextArea displayInfo;

    @FXML
    private TableView chargerTable;

    @FXML
    private TableColumn<Integer, String> AddressCol = new TableColumn<>("Address");

    @FXML
    private TableColumn<Integer, String> operatorCol = new TableColumn<>("Operator");

    @FXML
    private TableColumn<Integer, String> DistanceCol = new TableColumn<>("km");


    private ObservableList<Charger> chargerData = FXCollections.observableArrayList();

    private Coordinate dummyPosition = new Coordinate(1574161.4056 , 5173542.4743, -43.5097, 172.5452);

    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {

        makeTestChargers();
        //chargers.setOnMouseClicked(new EventHandler<MouseEvent>() {
        //     @Override
        //    public void handle(MouseEvent event) {
        //        chargerLabel.setText("Charger '" + chargers.getSelectionModel().getSelectedItem() + "' was selected.");
        //    }
    //    });
        addChargersToDisplay();
        viewChargers(chargerData.get(0));
        insetText();
        selectToView();

    }

    public void makeTestChargers() {
        chargerData.add((new Charger(new ArrayList<Connector>(), new Coordinate(1574161.4056, 5177263.1348, -43.557139, 172.680089, "3 Garlands Road, Woolston"), 4, 240.0, "Meridian", true, true)));
        chargerData.add((new Charger(new ArrayList<Connector>(), new Coordinate(1570148.5238, 5173542.4743, -43.59049, 172.630201, "Worsleys Rd, Cashmere"), 10, 100.0, "BMW", true, true)));
        chargerData.add((new Charger(new ArrayList<Connector>(), new Coordinate(1568290.4372, 5179226.0935, -43.539238, 172.607516, "Whiteleigh Avenue, Addington"), 20, 100.0, "Ford", true, true)));
        chargerData.add((new Charger(new ArrayList<Connector>(), new Coordinate(1568058.0099, 5179103.6026, -43.540331, 172.604632, "Whiteleigh Avenue, Addington"), 55, 100.0, "Mazda", true, true)));
    }

    public void viewChargers(Charger c) {
        displayInfo.clear();
        displayInfo.appendText("Operator: "+ c.getOperator() +"\n"+ "Location: " + c.getLocation().getAddress() +"\n"+ "Number of parks: " +c.getAvailableParks() +"\nTime Limit"+c.getTimeLimit()+"\nHas Attraction = "+c.getHasAttraction()+"\n");
    }


    // public void selectToView(){
    //     listOfChargers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
    //         @Override
    //         public void changed(ObservableValue observableValue, Object o, Object t1) {
    //             viewChargers(chargerList.get(listOfChargers.getSelectionModel().getSelectedIndex()));

    //         }
    //     });
    // }

    public void selectToView(){
        chargerTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                viewChargers(chargerData.get(chargerTable.getSelectionModel().getSelectedIndex()));

            }
        });

    }

    // public void addChargersToDisplay() {
    //     ObservableList<String> chargernames = FXCollections.observableArrayList();
    //     for(int i = 0; i < chargerList.size(); i++) {
    //             chargernames.add(chargerList.get(i).getOperator());
    //     }
    //     listOfChargers.setItems(chargernames);
    // }


    public void addChargersToDisplay() {
  
        chargerTable.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        AddressCol.setMaxWidth( 1f * Integer.MAX_VALUE * 35 );
        operatorCol.setMaxWidth( 1f * Integer.MAX_VALUE * 10 );
        DistanceCol.setMaxWidth( 1f * Integer.MAX_VALUE *5 );

        for (int i = 0; i < chargerData.size(); i++) {
            chargerTable.getItems().add(i);
        }

        AddressCol.setCellValueFactory(cellData -> {
            Integer rowIndex = cellData.getValue();
            return new ReadOnlyStringWrapper(chargerData.get(rowIndex).getLocation().getAddress());
        });
        
        operatorCol.setCellValueFactory(cellData -> {
            Integer rowIndex = cellData.getValue();
            return new ReadOnlyStringWrapper(chargerData.get(rowIndex).getOperator());
        });

        DistanceCol.setCellValueFactory(cellData -> {
            Integer rowIndex = cellData.getValue();
            return new ReadOnlyStringWrapper(""+(Math.round(Calculations.calculateDistance(dummyPosition, chargerData.get(rowIndex).getLocation())))+"km");
        });

        chargerTable.getColumns().add(AddressCol);
        chargerTable.getColumns().add(operatorCol);
        chargerTable.getColumns().add(DistanceCol);

        chargerTable.requestFocus();
        chargerTable.getSelectionModel().select(0);
    }

    /**
     * Update for chargers when user searches
     *
     */
    public void insetText() {
        searchCharger.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(searchCharger.getText());
        });
    }

    /**
     * Method to call when our counter button is clicked
     *
     */
    @FXML
    public void onButtonClicked() {

        // ObservableList<String> data = FXCollections.observableArrayList();
        // IntStream.range(0, 1000).mapToObj(Integer::toString).forEach(data::add);

        FilteredList<String> filteredData = new FilteredList<>(FXCollections.observableArrayList(), s -> true);

        // TextField filterInput = new TextField();
        // filterInput.textProperty().addListener(obs->{
        String filter = searchField.getText(); 
        if(filter == null || filter.length() == 0) {
            filteredData.setPredicate(s -> true);
        }
        else {
            filteredData.setPredicate(s -> s.contains(filter));
        }
        // });

        // chargers.setItems(FXCollections.observableList(filteredData));

        // BorderPane content = new BorderPane(new ListView<>(filteredData));
        // content.setBottom(filterInput);

        // Scene scene = new Scene(content, 500, 500);
        // primaryStage.setScene(scene);
        // primaryStage.show();
    }

    @FXML
    public void loadVehicleScreen() {

        try {
            // FXMLLoader fxmlLoader = new FXMLLoader();
            // fxmlLoader.setLocation(getClass().getResource("/fxml/vehicle.fxml"));
            FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/vehicle.fxml"));
            Parent root = baseLoader.load();
            /* 
             * if "fx:controller" is not set in fxml
             * fxmlLoader.setController(NewWindowController);
             */
            Scene scene = new Scene(root, 1080, 720);
            Stage stage = new Stage();

            VehicleController baseController = baseLoader.getController();
            baseController.init(stage);


            stage.setTitle("Revolt App");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.SEVERE, "Failed to create new Window.", e);
        }
    }
}


