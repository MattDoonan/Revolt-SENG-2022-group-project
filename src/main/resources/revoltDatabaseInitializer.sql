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
    alwaysOpen TEXT constraint isBoolean CHECK ( alwaysOpen IN ('TRUE', 'FALSE')),
    numCarParks INTEGER,
    hasCarParkCost TEXT constraint isBoolean CHECK ( hasCarParkCost IN ('TRUE', 'FALSE')),
    maxTime INTEGER,
    attraction TEXT constraint isBoolean CHECK ( attraction IN ('TRUE', 'FALSE')),
    latPos INTEGER,
    lonPos INTEGER,
    currenType VARCHAR(50),
    dateOpened TEXT,
    numConnectors INTEGER,
    chargingCost TEXT constraint isBoolean CHECK ( chargingCost IN ('TRUE', 'FALSE'))
    );
--SPLIT
DROP TABLE IF EXISTS connector;
--SPLIT
CREATE TABLE IF NOT EXISTS connector
(
    connectorID INTEGER NOT NULL constraint dk_connector PRIMARY KEY,
    currentType VARCHAR(50) NOT NULL,
    power VARCHAR(50) NOT NULL,
    isOperational TEXT constraint isBoolean CHECK ( isOperational IN ('TRUE', 'FALSE')),
    inUse TEXT constraint isBoolean CHECK ( inUse IN ('TRUE', 'FALSE')),
    chargerID INTEGER NOT NULL references Charger(chargerID)

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
    JourneyID INTEGER NOT NULL references Journey(journeyId),
    chargerID INTEGER NOT NULL references Charger(chargerID),
    stopOrder INTEGER NOT NULL
    )

