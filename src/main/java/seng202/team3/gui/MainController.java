package seng202.team3.gui;

import javafx.collections.ObservableList;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.data.entity.Coordinate;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import javax.swing.table.TableColumn;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the main.fxml window
 * @author seng202 teaching team
 */
public class MainController {

    // private static final Logger log = LogManager.getLogger();

    // @FXML
    // private Label defaultLabel;

    @FXML
    private TableView chargers;

    @FXML
    private TableColumn chargerID;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private Label chargerLabel;

    @FXML
    private TextField searchBar;

    @FXML
    private TextField searchCharger;

    public List<String> chargerList = new ArrayList<String>();


    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        // List<String> values = Arrays.asList("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve");
        // List<Charger> chargerList = Arrays.asList(new Charger(ArrayList<Connector> connectors, Coordinate location, int availableParks,
        // Double timeLimit, String operator, boolean isPublic, boolean hasAttraction));
        makeTestChargers();
        updateChargerList(chargerList);
        chargers.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                chargerLabel.setText("Charger '" + chargers.getSelectionModel().getSelectedItem() + "' was selected.");
            }
        });
        insetText();

    }

    public void makeTestChargers() {
        chargerList.add((new Charger(new ArrayList<Connector>(), new Coordinate(34.0, 32.0, 22.0, 33.0, "UC"), 14, 100.0, "Tesla", true, true)).getOperator());
        chargerList.add((new Charger(new ArrayList<Connector>(), new Coordinate(44.0, 53.0, 34.0, 35.0, "Mc Donalds"), 10, 100.0, "Tesla", true, true)).getOperator());
        chargerList.add((new Charger(new ArrayList<Connector>(), new Coordinate(42.0, 23.0, 32.0, 54.0, "BK"), 20, 100.0, "Tesla", true, true)).getOperator());
        chargerList.add((new Charger(new ArrayList<Connector>(), new Coordinate(30.0, 43.0, 55.0, 44.0, "Target"), 55, 100.0, "Tesla", true, true)).getOperator());
    }

    public void updateChargerList(List charge) {
        // idk
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

        chargers.setItems(FXCollections.observableList(filteredData));

        // BorderPane content = new BorderPane(new ListView<>(filteredData));
        // content.setBottom(filterInput);

        // Scene scene = new Scene(content, 500, 500);
        // primaryStage.setScene(scene);
        // primaryStage.show();
    }
}
