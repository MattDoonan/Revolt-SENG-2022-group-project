package seng202.team3.testfx;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import seng202.team3.data.database.QueryBuilder;
import seng202.team3.data.database.QueryBuilderImpl;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.Charger;
import seng202.team3.data.entity.Connector;
import seng202.team3.data.entity.Coordinate;
import seng202.team3.gui.ChargerController;
import seng202.team3.logic.GeoLocationHandler;

/**
 * TestFx to test adding a charger
 *
 * @author Michelle Hsieh
 * @version 1.0.0, Sep 22
 */
public class AddChargerPageTestFx extends TestFxBase {
    /**
     * Logger
     */
    private static final Logger logManager = LogManager.getLogger();

    private Charger charger;
    private static SqlInterpreter database;
    private ChargerController controller;

    /**
     * Creates a test database
     *
     * @throws Exception execeptions
     */
    @BeforeAll
    public static void initialise() throws Exception {
        SqlInterpreter.removeInstance();
        database = SqlInterpreter.initialiseInstanceWithUrl(
                "jdbc:sqlite:./target/test-classes/test_database.db");
        database.defaultDatabase();
    }

    /**
     * Starts everything
     *
     * @param stage The Stage which this application is on
     * @throws Exception if stage issues
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader editor = new FXMLLoader(getClass()
                .getResource("/fxml/charger_info.fxml"));
        Parent editorParent = editor.load();
        Scene scene = new Scene(editorParent);
        controller = editor.getController();
        addChargers();
        stage.setScene(scene);
        controller.init(stage);
        stage.show();
    }

    /**
     * Adds just one charger to database
     *
     * @throws IOException IO error handling
     */
    public void addChargers() throws IOException {
        Coordinate coord1 = new Coordinate(1.1, 2.3, -43.53418, 172.627572);
        Connector connector = new Connector("ChardaMo", "AC", "Available", "123", 3);

        charger = new Charger(
                new ArrayList<>(List.of(connector)), "Hosp", coord1, 1, 0.3, "Meridian",
                "2020/1/1 00:00:00", true,
                false, false, false);
        database.writeCharger(charger);
    }

    /**
     * Checks if adding a charger without filling out throws errors
     */
    @Test
    public void addChargerFail() {
        GeoLocationHandler.setCoordinate(new Coordinate(1.1, 2.3, -43.60, 172.572), "Trial location");
        clickOn("#saveButton");
        clickOn("#okay");
    }

    /**
     * Checks if adding a charger works
     */
    @Test
    public void addChargerSuccess() {
        GeoLocationHandler.setCoordinate(new Coordinate(1.1, 2.3, -43.60, 172.572), "Trial location");
        clickOn("#name");
        write("Trial");
        clickOn("#operator");
        write("Me");
        clickOn("#parks");
        write("2");
        clickOn("#time");
        write("129");
        clickOn("#cost");
        clickOn("#addConnectorButton");
        clickOn("#currentField");
        write("something");
        clickOn("#wattageField");
        write("12");
        clickOn("#chargingPointsField");
        write("12");
        clickOn("#typeField");
        write("else");
        clickOn("#statusField");
        write("operative");
        clickOn("#saveConnectors");
        clickOn("#lat");
        write("-53.00");
        clickOn("#lon");
        write("123.0");
        clickOn("#saveButton");

        ArrayList<Charger> chargers = new ArrayList<>();

        try {
            QueryBuilder mainDataQuery = new QueryBuilderImpl().withSource("charger");
            for (Object o : SqlInterpreter.getInstance()
                    .readData(mainDataQuery.build(), Charger.class)) {
                chargers.add((Charger) o);
            }
        } catch (IOException e) {
            logManager.error(e.getMessage());
            ;
        }
        assertEquals(2, chargers.size());
    }

}
