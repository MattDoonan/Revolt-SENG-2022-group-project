DROP TABLE IF EXISTS charger;
--SPLIT
CREATE TABLE IF NOT EXISTS charger
(
    chargerid INTEGER PRIMARY KEY AUTOINCREMENT,
    x REAL,
    y REAL,
    operator VARCHAR(50),
    owner VARCHAR(50) NOT NULL,
    address VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    is24hours BIT,
    carparkcount INTEGER,
    hascarparkcost BIT,
    maxtimelimit REAL,
    hastouristattraction BIT,
    latitude INTEGER,
    longitude INTEGER,
    datefirstoperational TEXT,
    haschargingcost BIT
    );
--SPLIT
DROP TABLE IF EXISTS connector;
--SPLIT
CREATE TABLE IF NOT EXISTS connector
(
    connectorid INTEGER constraint dk_connector PRIMARY KEY AUTOINCREMENT,
    connectorcurrent VARCHAR(50) NOT NULL,
    connectorpowerdraw VARCHAR(50) NOT NULL,
    count INTEGER,
    connectorstatus VARCHAR(30),
    chargerid INTEGER NOT NULL references Charger(chargerID),
    connectortype VARCHAR(50) NOT NULL
    );
--SPLIT
DROP TABLE IF EXISTS note;
--SPLIT
CREATE TABLE if not exists note
(
    reviewId INTEGER constraint dk_note PRIMARY KEY AUTOINCREMENT,
    chargerid INTEGER NOT NULL references Charger(chargerID),
    rating REAL,
    publicText VARCHAR(255),
    privateText VARCHAR(255)
    );
--SPLIT
DROP TABLE IF EXISTS favourite;
--SPLIT
CREATE TABLE if not exists favourite
(
    favouriteID INTEGER constraint dk_fav PRIMARY KEY AUTOINCREMENT,
    chargerid INTEGER NOT NULL references Charger(chargerid)

    );
--SPLIT
DROP TABLE IF EXISTS vehicle;
--SPLIT
CREATE TABLE if not exists vehicle
(
    vehicleid INTEGER constraint dk_Veh PRIMARY KEY AUTOINCREMENT,
    make VARCHAR(10),
    model VARCHAR(10),
    rangekm INTEGER not null,
    connectorType VARCHAR(40),
    batteryPercent REAL,
    imgPath VARCHAR(100)
    );
--SPLIT
DROP TABLE IF EXISTS journey;
--SPLIT
CREATE TABLE if not exists journey
(
    journeyid INTEGER constraint dk_journey PRIMARY KEY AUTOINCREMENT,
    vehicleid INTEGER NOT NULL references Vehicle(vehicleID),
    startLat REAL NOT NULL,
    startLon REAL NOT NULL,
    startX REAL,
    startY REAL,
    endLat REAL NOT NULL,
    endLon REAL NOT NULL,
    endX REAL,
    endY REAL,
    startDate TEXT,
    endDate TEXT
    );
--SPLIT
DROP TABLE IF EXISTS stop;
--SPLIT
CREATE TABLE if not exists stop
(
    journeyid INTEGER NOT NULL references Journey(journeyid),
    position INTEGER NOT NULL,
    chargerid INTEGER NOT NULL references Charger(chargerid),
    PRIMARY KEY (journeyid, position)
    );