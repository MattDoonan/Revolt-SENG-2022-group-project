package seng202.team3.data.entity;

import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvRecurse;
import java.util.ArrayList;
import java.util.List;
import seng202.team3.logic.UserManager;

/**
 * Representation of chargers that users can find and connect vehicles to
 *
 * @author Harrison Tyson
 * @version 1.0.1, Aug 22
 */
public class Charger extends Entity {

    /** When the charger was first available */
    @CsvBindByName(column = "dateFirstOperational")
    private String dateOpened;

    /** Name of the charger */
    @CsvBindByName(column = "name", required = true)
    private String name;

    /** {@link Connector Connectors} available on charger */
    @CsvBindAndSplitByName(column = "connectorsList", elementType = Connector.class, 
                            splitOn = ",(?=( )*\\{)", converter = ConnectorConverter.class, 
                            required = true)
    private List<Connector> connectors;

    /** {@link Coordinate Coordinate} information for the charger */
    @CsvRecurse
    private Coordinate location;

    /** Number of parks available at the charger */
    @CsvBindByName(column = "carParkCount", required = true)
    private int availableParks;

    /** Maximum amount of time that can be spent at a charger */
    @CsvCustomBindByName(column = "maxTimeLimit", converter = TimeLimitConverter.class)
    private Double timeLimit;

    /** Business that manages the charger */
    @CsvBindByName(column = "operator", required = true)
    private String operator;

    /** Business that owns the charger */
    private String owner;

    /** userid of the charger owner */
    private int ownerId;

    /** Name of the user to map ownership to for demo data */
    @CsvBindByName(column = "owner")
    private String demoOwner;

    /** Accessible to the public */
    private boolean isPublic;

    /** Has tourist attraction nearby */
    @CsvBindByName(column = "hasTouristAttraction", required = true)
    private boolean hasAttraction;

    /** Can be accessed 24 hours in the day */
    @CsvBindByName(column = "is24Hours", required = true)
    private boolean is24Hrs;

    /** Has cost for parking */
    @CsvBindByName(column = "hasCarparkCost", required = true)
    private boolean hasParkingCost;

    /** Has cost for charging */
    @CsvBindByName(column = "hasChargingCost", required = true)
    private boolean hasChargeCost;

    /** Stores the type of current used by connectors */
    private String currentType;

    /**
     * Total number of views the charger has had
     */
    private int views = 0;

    /**
     * Empty constructor for CSV object builder
     */
    public Charger() {
        connectors = new ArrayList<>();
        setOwnerId(UserManager.getUser().getId());
        setOwner(UserManager.getUser().getAccountName());
    }

    /**
     * Constructor for a charger
     *
     * @param connectors     list of attached {@link Connector connectors}
     * @param name           name of the charger
     * @param location       {@link Coordinate Coordinate} of the charger
     * @param availableParks number of available parks
     * @param timeLimit      maximum time limit for charging
     * @param operator       operator of the charger
     * @param dateOpened     date the charger was opened
     * @param hasAttraction  bool indicating nearby tourist attraction
     * @param is24Hrs        bool indicating open 24 hours
     * @param hasChargeCost  bool indicating if it has a charge cost
     * @param hasParkingCost bool indicating if it has a parking cost
     */
    public Charger(List<Connector> connectors, String name, Coordinate location,
            int availableParks, Double timeLimit, String operator, String dateOpened,
            boolean hasAttraction, boolean is24Hrs, boolean hasChargeCost, boolean hasParkingCost) {
        this.connectors = connectors;
        setLocation(location);
        setAvailableParks(availableParks);
        setTimeLimit(timeLimit);
        setOperator(operator);
        setHasAttraction(hasAttraction);
        setName(name);
        setDateOpened(dateOpened);
        setChargeCost(hasChargeCost);
        setParkingCost(hasParkingCost);
        setAvailable24Hrs(is24Hrs);
        setCurrentType();
        setOwnerId(UserManager.getUser().getId());
        setOwner(UserManager.getUser().getAccountName());
        demoOwner = null;
    }

    /**
     * Get the date the charger was first opened
     *
     * @return date first opened
     */
    public String getDateOpened() {
        return dateOpened;
    }

    /**
     * Set the date the charger was first opened
     *
     * @param dateOpened date first opened
     */
    public void setDateOpened(String dateOpened) {
        this.dateOpened = dateOpened;
    }

    /**
     * Add a new {@link Connector connector} to the charger
     *
     * @param c Connector to add
     */
    public void addConnector(Connector c) {
        connectors.add(c);
        setCurrentType();
    }

    /**
     * Remove a {@link Connector connector} from the charger
     *
     * @param connector Connector to remove
     */
    public void removeConnector(Connector connector) {
        connectors.remove(connector);
        setCurrentType();
    }

    /**
     * Get all {@link Connector connectors} on charger
     *
     * @return list of attached connectors
     */
    public List<Connector> getConnectors() {
        return connectors;
    }

    /**
     * Get location of the charger
     *
     * @return {@link Coordinate coordinates} of the charger
     */
    public Coordinate getLocation() {
        return location;
    }

    /**
     * Set location of the charger
     *
     * @param location new location of charger
     */
    public void setLocation(Coordinate location) {
        this.location = location;
    }

    /**
     * Get number of parks available
     *
     * @return total number of parks available
     */
    public int getAvailableParks() {
        return availableParks;
    }

    /**
     * Set number of parks available
     *
     * @param availableParks new number of parks available
     */
    public void setAvailableParks(int availableParks) {
        this.availableParks = availableParks;
    }

    /**
     * Get the max time limit at the charger
     *
     * @return max time that can be spent at charger
     */
    public Double getTimeLimit() {
        return timeLimit;
    }

    /**
     * Set the max time limit at the charger
     *
     * @param timeLimit max time that can be spent at charger
     */
    public void setTimeLimit(Double timeLimit) {
        if (timeLimit == 0.0) {
            this.timeLimit = Double.POSITIVE_INFINITY;
        } else {
            this.timeLimit = timeLimit;
        }
    }

    /**
     * Get the operator of the charger
     *
     * @return operator of the charger
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Set the operator of the charger
     *
     * @param operator operator of the charger
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * Gets the demo owner of the charger
     *
     * @return the demo owner of the charger
     */
    public String getDemoOwner() {
        return demoOwner;
    }

    /**
     * Sets the demo owner of the charger
     *
     * @param owner new demo owner of the charger
     */
    public void setDemoOwner(String owner) {
        this.demoOwner = owner;
    }

    /**
     * Gets the owner of the charger
     *
     * @return the owner of the charger
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the owner of the charger
     *
     * @param owner new owner of the charger
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Get unique identifier for the owner
     *
     * @return unique identification number
     */
    public int getOwnerId() {
        return ownerId;
    }

    /**
     * Set unique identifier for the owner
     *
     * @param ownerId unique identifier for the charger
     */
    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * Get the public availability of the charger
     *
     * @return public availability of the charger
     */
    public boolean getPublic() {
        return isPublic;
    }

    /**
     * Set the public availability of the charger
     *
     * @param isPublic public availability of the charger
     */
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    /**
     * Get has nearby tourist attractions
     *
     * @return bool indicating has nearby tourist attractions
     */
    public boolean getHasAttraction() {
        return hasAttraction;
    }

    /**
     * Set has nearby tourist attractions
     *
     * @param hasAttraction bool indicating has nearby tourist attractions
     */
    public void setHasAttraction(boolean hasAttraction) {
        this.hasAttraction = hasAttraction;
    }

    /**
     * Get all day availability
     *
     * @return bool indicating all day availability
     */
    public boolean getAvailable24Hrs() {
        return is24Hrs;
    }

    /**
     * Set all day availability
     *
     * @param available24Hrs bool indicating all day availability
     */
    public void setAvailable24Hrs(boolean available24Hrs) {
        is24Hrs = available24Hrs;
    }

    /**
     * Get has a parking cost
     *
     * @return bool indicating parking cost
     */
    public boolean getParkingCost() {
        return hasParkingCost;
    }

    /**
     * Set has a parking cost
     *
     * @param parkingCost bool indicating parking cost
     */
    public void setParkingCost(boolean parkingCost) {
        hasParkingCost = parkingCost;
    }

    /**
     * Get has charge cost
     *
     * @return bool indicating charging cost
     */
    public boolean getChargeCost() {
        return hasChargeCost;
    }

    /**
     * Set has charger cost
     *
     * @param chargeCost bool indicating charging cost
     */
    public void setChargeCost(boolean chargeCost) {
        hasChargeCost = chargeCost;
    }

    /**
     * Get the name of the charger
     *
     * @return name of the charger
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the charger
     *
     * @param name new name of the charger
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the current type of the charger
     */
    public void setCurrentType() {
        ArrayList<String> types = new ArrayList<>();
        for (Connector c : this.connectors) {
            if (!types.contains(c.getCurrent())) {
                types.add(c.getCurrent());
            }
        }

        if (types.size() > 1) {
            StringBuilder str = new StringBuilder();

            for (String type : types) {
                if (types.indexOf(type) == 0) {
                    str.append(type);
                } else {
                    str.append(" " + type);
                }
            }

            this.currentType = str.toString();
        } else {
            currentType = types.get(0);
        }

    }

    /**
     * Get the current type of the charger
     *
     * @return the current type of the charger
     */
    public String getCurrentType() {
        if (currentType == null) {
            setCurrentType();
        }
        return currentType;
    }

    /**
     * Sets the current number of views of the charger
     * 
     * @param views current number of views
     */
    public void setViews(int views) {
        this.views = views;
    }

    /**
     * Gets the current number of views of the charger
     * 
     * @return current number of views
     */
    public int getViews() {
        return views;
    }

    /**
     * Increases the current number of views of the charger by one
     */
    public void incrementViews() {
        views++;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        Charger c;
        if (o instanceof Charger) {
            c = (Charger) o;
        } else {
            return false;
        }

        return c.getDateOpened().equals(this.getDateOpened())
                && c.getName().equals(this.getName())
                && c.getConnectors().equals(this.getConnectors())
                && c.getLocation().equals(this.getLocation())
                && c.getAvailableParks() == this.getAvailableParks()
                && c.getTimeLimit().equals(this.getTimeLimit())
                && c.getOperator().equals(this.getOperator())
                && c.getOwnerId() == this.getOwnerId()
                && c.getOwner().equals(this.getOwner())
                && c.getPublic() == this.getPublic()
                && c.getHasAttraction() == this.getHasAttraction()
                && c.getParkingCost() == this.getParkingCost()
                && c.getChargeCost() == this.getChargeCost()
                && c.getCurrentType().equals(this.getCurrentType())
                && c.getViews() == this.getViews();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int result = (getId() ^ (getId() >>> 32));
        result = 31 * result + dateOpened.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + connectors.hashCode();
        result = 31 * result + location.hashCode();
        result = 31 * result + availableParks;
        result = 31 * result + timeLimit.hashCode();
        result = 31 * result + operator.hashCode();
        result = 31 * result + owner.hashCode();
        result = 31 * result + currentType.hashCode();

        return result;
    }
}
