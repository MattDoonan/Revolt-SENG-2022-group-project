CREATE TABLE IF NOT EXISTS Charger
(
    xPos REAL,
    yPos REAL,
    chargerID INTEGER NOT NULL constraint dk_charger PRIMARY KEY,
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

CREATE TABLE IF NOT EXISTS Connector
(
    connectorID INTEGER NOT NULL constraint dk_connector PRIMARY KEY,
    currentType VARCHAR(50) NOT NULL,
    power VARCHAR(50) NOT NULL,
    isOperational TEXT constraint isBoolean CHECK ( isOperational IN ('TRUE', 'FALSE')),
    inUse TEXT constraint isBoolean CHECK ( inUse IN ('TRUE', 'FALSE')),
    chargerID INTEGER NOT NULL references Charger(chargerID)

);

CREATE TABLE if not exists User
(
    email VARCHAR(100) NOT NULL constraint dk_user PRIMARY KEY,
    accountName VARCHAR(50),
    homeAddress VARCHAR(255),
    carbon_used REAL,
    permissions INTEGER NOT NULL constraint dk_per check ( permissions IN (1,2,3))
);

CREATE TABLE if not exists Note
(
    reviewId INTEGER NOT NULL constraint dk_note PRIMARY KEY,
    chargerID INTEGER NOT NULL references Charger(chargerID),
    user VARCHAR(100) NOT NULL references User(email),
    rating REAL,
    publicText VARCHAR(255),
    privateText VARCHAR(255)
);

CREATE TABLE if not exists Favourite
(
    favouriteID INTEGER NOT NULL constraint dk_fav PRIMARY KEY,
    user VARCHAR(100) NOT NULL references User(email),
    chargerID INTEGER NOT NULL references Charger(chargerID)

);

CREATE TABLE if not exists Vehicle
(
    vehicleID INTEGER NOT NULL constraint dk_Veh PRIMARY KEY,
    user VARCHAR(100) NOT NULL references User(email),
    carName VARCHAR(10),
    range_km INTEGER not null
);

CREATE TABLE if not exists Journey
(
    journeyId INTEGER NOT NULL constraint dk_journey PRIMARY KEY ,
    user VARCHAR(100) NOT NULL references User(email),
    vehicleID INTEGER NOT NULL references Vehicle(vehicleID),
    startLat REAL NOT NULL,
    startLon REAL NOT NULL,
    endLat REAL NOT NULL,
    endLon REAL NOT NULL,
    startDate TEXT,
    finishDate TEXT
);

CREATE TABLE if not exists Stop
(
    stopID INTEGER NOT NULL constraint dk_stop PRIMARY KEY,
    JourneyID INTEGER NOT NULL references Journey(journeyId),
    chargerID INTEGER NOT NULL references Charger(chargerID),
    stopOrder INTEGER NOT NULL
)

