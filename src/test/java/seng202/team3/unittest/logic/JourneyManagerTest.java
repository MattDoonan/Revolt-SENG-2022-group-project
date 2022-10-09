package seng202.team3.unittest.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import seng202.team3.data.database.SqlInterpreter;
import seng202.team3.data.entity.User;
import seng202.team3.logic.JourneyManager;

public class JourneyManagerTest {

    private static final Logger logManager = LogManager.getLogger();

    private JourneyManager manager;
    static SqlInterpreter db;
    static User testUser;

    @BeforeEach
    public void setUp() {

    }

    /**
     * AfterEach, sets everything to null.
     */
    @AfterEach
    public void tearDown() {

    }

}
