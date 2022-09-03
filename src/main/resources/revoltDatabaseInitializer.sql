DROP TABLE IF EXISTS charger;
--SPLIT
CREATE TABLE IF NOT EXISTS charger
(
    chargerID INTEGER PRIMARY KEY AUTOINCREMENT,
    xPos REAL,
    yPos REAL,
    operator VARCHAR(50),
    owner VARCHAR(50) NOT NULL,
    address VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    is24Hrs BIT,
    numCarParks INTEGER,
    hasCarParkCost BIT,
    timeLimit REAL,
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
    connectorID INTEGER constraint dk_connector PRIMARY KEY AUTOINCREMENT,
    currentType VARCHAR(50) NOT NULL,
    power VARCHAR(50) NOT NULL,
    count INTEGER,
    isOperational VARCHAR(30),
    chargerID INTEGER NOT NULL references Charger(chargerID),
    type VARCHAR(50) NOT NULL
    );
--SPLIT
DROP TABLE IF EXISTS note;
--SPLIT
CREATE TABLE if not exists note
(
    reviewId INTEGER constraint dk_note PRIMARY KEY AUTOINCREMENT,
    chargerID INTEGER NOT NULL references Charger(chargerID),
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
    chargerID INTEGER NOT NULL references Charger(chargerID)

    );
--SPLIT
DROP TABLE IF EXISTS vehicle;
--SPLIT
CREATE TABLE if not exists vehicle
(
    vehicleID INTEGER constraint dk_Veh PRIMARY KEY AUTOINCREMENT,
    make VARCHAR(10),
    model VARCHAR(10),
    rangeKM INTEGER not null,
    connectorType VARCHAR(20)
    );
--SPLIT
DROP TABLE IF EXISTS journey;
--SPLIT
CREATE TABLE if not exists journey
(
    journeyId INTEGER constraint dk_journey PRIMARY KEY AUTOINCREMENT,
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
    stopID INTEGER constraint dk_stop PRIMARY KEY AUTOINCREMENT,
    latPos INTEGER,
    lonPos INTEGER,
    journeyID INTEGER NOT NULL references Journey(journeyId),
    chargerID INTEGER references Charger(chargerID),
    stopOrder INTEGER NOT NULL
    );