DROP TABLE IF EXISTS charger;
--SPLIT
CREATE TABLE IF NOT EXISTS charger
(
    chargerID INTEGER NOT NULL constraint dk_charger PRIMARY KEY,
    xPos REAL,
    yPos REAL,
    operator VARCHAR(50),
    owner VARCHAR(50) NOT NULL,
    address VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    is24Hrs BIT,
    numCarParks INTEGER,
    hasCarParkCost BIT,
    timeLimit INTEGER,
    attraction BIT,
    latPos INTEGER,
    lonPos INTEGER,
    dateOpened TEXT,
    chargingCost BIT
    );
--SPLIT
DROP TABLE IF EXISTS connector;
--SPLIT
CREATE TABLE IF NOT EXISTS connector
(
    connectorID INTEGER NOT NULL constraint dk_connector PRIMARY KEY,
    currentType VARCHAR(50) NOT NULL,
    power VARCHAR(50) NOT NULL,
    count INTEGER,
    isOperational VARCHAR(30),
    chargerID INTEGER NOT NULL references Charger(chargerID),
    type VARCHAR(50) NOT NULL
    );
--SPLIT
DROP TABLE IF EXISTS user;
--SPLIT
CREATE TABLE if not exists user
(
    userID INTEGER NOT NULL constraint dk_user PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    accountName VARCHAR(50),
    homeAddress VARCHAR(255),
    carbon_used REAL,
    permissions INTEGER NOT NULL constraint dk_per check ( permissions IN (1,2,3))
    );
--SPLIT
DROP TABLE IF EXISTS note;
--SPLIT
CREATE TABLE if not exists note
(
    reviewId INTEGER NOT NULL constraint dk_note PRIMARY KEY,
    chargerID INTEGER NOT NULL references Charger(chargerID),
    user VARCHAR(100) NOT NULL references User(userID),
    rating REAL,
    publicText VARCHAR(255),
    privateText VARCHAR(255)
    );
--SPLIT
DROP TABLE IF EXISTS favourite;
--SPLIT
CREATE TABLE if not exists favourite
(
    favouriteID INTEGER NOT NULL constraint dk_fav PRIMARY KEY,
    user VARCHAR(100) NOT NULL references User(userID),
    chargerID INTEGER NOT NULL references Charger(chargerID)

    );
--SPLIT
DROP TABLE IF EXISTS vehicle;
--SPLIT
CREATE TABLE if not exists vehicle
(
    vehicleID INTEGER NOT NULL constraint dk_Veh PRIMARY KEY,
    user VARCHAR(100) NOT NULL references User(userID),
    carName VARCHAR(10),
    range_km INTEGER not null
    );
--SPLIT
DROP TABLE IF EXISTS journey;
--SPLIT
CREATE TABLE if not exists journey
(
    journeyId INTEGER NOT NULL constraint dk_journey PRIMARY KEY ,
    user VARCHAR(100) NOT NULL references User(userID),
    vehicleID INTEGER NOT NULL references Vehicle(vehicleID),
    startLat REAL NOT NULL,
    startLon REAL NOT NULL,
    endLat REAL NOT NULL,
    endLon REAL NOT NULL,
    startDate TEXT,
    finishDate TEXT
    );
--SPLIT
DROP TABLE IF EXISTS stop;
--SPLIT
CREATE TABLE if not exists stop
(
    stopID INTEGER NOT NULL constraint dk_stop PRIMARY KEY,
    latPos INTEGER,
    lonPos INTEGER,
    journeyID INTEGER NOT NULL references Journey(journeyId),
    chargerID INTEGER references Charger(chargerID),
    stopOrder INTEGER NOT NULL
    );
--SPLIT
INSERT INTO charger (xPos, yPos, chargerID, name, operator, owner, address, is24Hrs, numCarParks, hasCarParkCost, timeLimit, attraction, latPos, lonPos, dateOpened, chargingCost) 
VALUES 
    (1366541.2354,5153202.1642,1,"YHA MT COOK","MERIDIAN ENERGY LIMITED","MERIDIAN ENERGY LIMITED","4 Kitchener Dr, Mount Cook National Park 7999, New Zealand",1,1,0,0,0,-43.73745,170.100913,"2020/05/01 00:00:00+00",1),
    (1570148.5238,5173542.4743,2,"CHRISTCHURCH ADVENTURE PARK","MERIDIAN ENERGY LIMITED","MERIDIAN ENERGY LIMITED","Worsleys Rd, Cashmere, Christchurch 8022, New Zealand",0,4,0,0,1,-43.59049,172.630201,"2020/05/01 00:00:00+00",0),
    (1822955.3955,5488854.3202,3,"PUKAHA NATIONAL WILDLIFE CENTRE","MERIDIAN ENERGY LIMITED","MERIDIAN ENERGY LIMITED","85379 State Highway 2, Mount Bruce 5881",1,2,0,0,1,-40.721068,175.639788,"2020/08/12 00:00:00+00",0);
--SPLIT
INSERT INTO connector (connectorID, currentType, power, count, isOperational, chargerID, type)
VALUES
    (1,"AC","22 kW", 1, "Operative", 1, "Type 2 Socketed"),
    (2,"AC","44 kW", 4, "Operative", 2, "Type 2 Socketed"),
    (3,"AC","7 kW", 2, "Operative", 3, "Type 2 Socketed"),
    (4,"AC","7 kW", 2, "Operative", 3, "Type 2 CCS");