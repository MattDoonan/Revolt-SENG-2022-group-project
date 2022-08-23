package seng202.team3.gui;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn;
import javafx.scene.text.Text;
import seng202.team3.data.database.ComparisonType;
import seng202.team3.data.database.CsvInterpreter;
import seng202.team3.data.database.Query;
import seng202.team3.data.database.QueryBuilderImpl;
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
    private Slider changeDistance;

    @FXML
    private TextField searchField;

    @FXML
    private TextField searchCharger;

    @FXML
    private TextArea displayInfo;

    @FXML
    private Text distanceDisplay;

    @FXML
    private CheckBox acButton;

    @FXML
    private CheckBox dcButton;

    @FXML
    private TableView chargerTable;

    @FXML
    private CheckBox attractionButton;

    @FXML
    private CheckBox chargingCost;

    @FXML
    private TableColumn<Integer, String> AddressCol = new TableColumn<>("Address");

    @FXML
    private TableColumn<Integer, String> operatorCol = new TableColumn<>("Operator");

    @FXML
    private TableColumn<Integer, Double> DistanceCol = new TableColumn<>("km");

    public Query mainDataQuerry;

    private ObservableList<Charger> chargerData;

    private ChargerManager chargerManager = new ChargerManager();

    private Coordinate dummyPosition = new Coordinate(1574161.4056 , 5173542.4743, -43.5097, 172.5452);

    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        mainDataQuerry = new QueryBuilderImpl().withSource("charger").build();
        makeAllChargers();
        tableMaker();
        viewChargers(chargerData.get(0));
        insetText();
        selectToView();
        onSliderChanged();

    }

    public void makeAllChargers() {
        try {
            chargerData = FXCollections.observableList((List<Charger>) (List<?>) new CsvInterpreter().readData(mainDataQuerry, Charger.class));
        }
        catch(IOException e){
            e.printStackTrace();
        }
        compareDistance();
    }

    public void compareDistance() {
        ArrayList arrayCloserChargers = chargerManager.getNearbyChargers(new ArrayList<>(chargerData), dummyPosition, changeDistance.getValue());
        ObservableList closerChargers = FXCollections.observableList(arrayCloserChargers);
        addChargersToDisplay(closerChargers);
    }

    public void viewChargers(Charger c) {
        displayInfo.clear();
        String word = "";
        ArrayList<String> check = new ArrayList<String>();
        for(int i = 0; i < c.getConnectors().size(); i++) {
            if (!check.contains(c.getConnectors().get(i).getCurrent())){
                word = word + " " + c.getConnectors().get(i).getCurrent();
                check.add(c.getConnectors().get(i).getCurrent());
            }
        }
        displayInfo.appendText("Operator: "+ c.getOperator() +"\n"+ "Location: " + c.getLocation().getAddress() +"\n"+ "Number of parks: " +c.getAvailableParks() +"\nTime Limit "+c.getTimeLimit()+"\nHas Attraction = "+c.getHasAttraction()+"\nHas cost "+c.getChargeCost()+"\nCharger Type:"+word+"");
    }


    public void selectToView(){
        chargerTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                viewChargers(chargerData.get(chargerTable.getSelectionModel().getSelectedIndex()));

            }
        });

    }

    public void tableMaker() {
        chargerTable.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        AddressCol.setMaxWidth( 1f * Integer.MAX_VALUE * 30 );
        operatorCol.setMaxWidth( 1f * Integer.MAX_VALUE * 15 );
        DistanceCol.setMaxWidth( 1f * Integer.MAX_VALUE *5 );

        chargerTable.getColumns().add(AddressCol);
        chargerTable.getColumns().add(operatorCol);
        chargerTable.getColumns().add(DistanceCol);
        chargerTable.requestFocus();
        chargerTable.getSelectionModel().select(0);
    }
    public void addChargersToDisplay(ObservableList<Charger> wantedChargers) {

        chargerTable.getItems().clear();

        for (int i = 0; i < wantedChargers.size(); i++) {
            chargerTable.getItems().add(i);
        }

        AddressCol.setCellValueFactory(cellData -> {
            Integer rowIndex = cellData.getValue();
            return new ReadOnlyStringWrapper(wantedChargers.get(rowIndex).getLocation().getAddress());
        });
        
        operatorCol.setCellValueFactory(cellData -> {
            Integer rowIndex = cellData.getValue();
            return new ReadOnlyStringWrapper(wantedChargers.get(rowIndex).getOperator());
        });

        DistanceCol.setCellValueFactory(cellData -> {
            Integer rowIndex = cellData.getValue();
            return new ReadOnlyDoubleWrapper(Math.round((Calculations.calculateDistance(dummyPosition, wantedChargers.get(rowIndex).getLocation()))*10.0)/10.0).asObject();
        });

    }

    /**
     * Update for chargers when user searches
     *
     */
    public void insetText() {
        searchCharger.setOnMouseClicked(e -> {
            if(searchCharger.getText().contains("Search Charger")) {
                searchCharger.clear();
            }
        });
        searchCharger.textProperty().addListener((observable, oldValue, newValue) -> {
            mainDataQuerry.addFilter("address", searchCharger.getText(), ComparisonType.CONTAINS);
            makeAllChargers();
        });
    }

    @FXML
    public void onSliderChanged() {

        changeDistance.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                distanceDisplay.textProperty().setValue("Distance ("+Math.round(changeDistance.getValue())+" km)");
                if(! changeDistance.isValueChanging()) {
                    compareDistance();
                }
            }
        });
    }


    public void acChargersOnly(ActionEvent actionEvent) {
        if(acButton.isSelected()) {
            mainDataQuerry.addFilter("current", "AC", ComparisonType.CONTAINS);
        } else {
            mainDataQuerry.removeFilter("current");
        }
        makeAllChargers();
    }

    public void dcChargersOnly(ActionEvent actionEvent) {
        if(dcButton.isSelected()) {
            mainDataQuerry.addFilter("current", "DC", ComparisonType.CONTAINS);
        } else {
            mainDataQuerry.removeFilter("current");
        }
        makeAllChargers();
    }

    public void attractionNeeded(ActionEvent actionEvent) {
        if(attractionButton.isSelected()) {
            mainDataQuerry.addFilter("hasTouristAttraction", "True", ComparisonType.CONTAINS);
        } else {
            mainDataQuerry.removeFilter("hasTouristAttraction");
        }
        makeAllChargers();
    }

    public void noChargingCostNeeded(ActionEvent actionEvent) {
        if(chargingCost.isSelected()) {
            mainDataQuerry.addFilter("hasChargingCost", "False", ComparisonType.CONTAINS);
        } else {
            mainDataQuerry.removeFilter("hasChargingCost");
        }
        makeAllChargers();
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

    public void editTable() {

    }

}


