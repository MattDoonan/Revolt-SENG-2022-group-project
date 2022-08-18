package seng202.team3.gui;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.data.entity.Coordinate;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.util.ArrayList;
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
    private TableColumn<Integer, String> operatorCol = new TableColumn<>("Operator");

    @FXML
    private TableColumn<Integer, String> locationCol = new TableColumn<>("Location");

    @FXML
    private TableColumn<Integer, String> parksCol = new TableColumn<>("Num. Parks");

    @FXML
    private TableColumn<Integer, String> attractionCol = new TableColumn<>("Attraction");


    private ObservableList<Charger> chargerData = FXCollections.observableArrayList();  

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
        chargerData.add((new Charger(new ArrayList<Connector>(), new Coordinate(34.0, 32.0, 22.0, 33.0, "UC"), 14, 100.0, "Tesla", true, true)));
        chargerData.add((new Charger(new ArrayList<Connector>(), new Coordinate(44.0, 53.0, 34.0, 35.0, "Mc Donalds"), 10, 100.0, "BMW", true, true)));
        chargerData.add((new Charger(new ArrayList<Connector>(), new Coordinate(42.0, 23.0, 32.0, 54.0, "BK"), 20, 100.0, "Ford", true, true)));
        chargerData.add((new Charger(new ArrayList<Connector>(), new Coordinate(30.0, 43.0, 55.0, 44.0, "Target"), 55, 100.0, "Mazda", true, true)));
    }

    public void viewChargers(Charger c) {
        displayInfo.clear();
        displayInfo.appendText("Operator: "+ c.getOperator() +"\n"+ "Location: " + c.getLocation() +"\n"+ "Number of parks: " +c.getAvailableParks() +"\nHas Attraction = "+c.getHasAttraction()+"\n");
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
        operatorCol.setMaxWidth( 1f * Integer.MAX_VALUE * 25 );
        locationCol.setMaxWidth( 1f * Integer.MAX_VALUE * 25 );
        parksCol.setMaxWidth( 1f * Integer.MAX_VALUE * 25 );
        attractionCol.setMaxWidth( 1f * Integer.MAX_VALUE * 25 );

        for (int i = 0; i < chargerData.size(); i++) {
            chargerTable.getItems().add(i);
        }
        
        operatorCol.setCellValueFactory(cellData -> {
            Integer rowIndex = cellData.getValue();
            return new ReadOnlyStringWrapper(chargerData.get(rowIndex).getOperator());
        });

        locationCol.setCellValueFactory(cellData -> {
            Integer rowIndex = cellData.getValue();
            return new ReadOnlyStringWrapper(chargerData.get(rowIndex).getLocation().toString());
        });

        parksCol.setCellValueFactory(cellData -> {
            Integer rowIndex = cellData.getValue();
            return new ReadOnlyStringWrapper(Integer.toString(chargerData.get(rowIndex).getAvailableParks()));
        });

        attractionCol.setCellValueFactory(cellData -> {
            Integer rowIndex = cellData.getValue();
            return new ReadOnlyStringWrapper(String.valueOf(chargerData.get(rowIndex).getHasAttraction()));
        });

        chargerTable.getColumns().add(operatorCol);
        chargerTable.getColumns().add(locationCol);
        chargerTable.getColumns().add(parksCol);
        chargerTable.getColumns().add(attractionCol);

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
}
