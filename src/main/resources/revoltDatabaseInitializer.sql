--SPLIT
DROP TABLE IF EXISTS user;
--SPLIT
CREATE TABLE if not exists user
(
   userid INTEGER NOT NULL constraint dk_users PRIMARY KEY AUTOINCREMENT,
   email VARCHAR(250) NOT NULL,
   username VARCHAR(15) NOT NULL UNIQUE,
   password VARCHAR(170) NOT NULL,
   permissions INTEGER NOT NULL,
   carbonSaved REAL
   );
--SPLIT
INSERT INTO user (username, password, email, carbonSaved, permissions)
VALUES ("admin",
        "c7ad44cbad762a5da0a452f9e854fdc1e0e7a52a38015f23f3eab1d80b931dd472634dfac71cd34ebc35d16ab7fb8a90c81f975113d6c7538dc69dd8de9077ec",
        "admin@admin.com",
        0,
        3);
--SPLIT
DROP TABLE IF EXISTS charger;
--SPLIT
CREATE TABLE IF NOT EXISTS charger
(
    chargerid INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    operator VARCHAR(50),
    owner INTEGER NOT NULL references User(userid),
    address VARCHAR(255) NOT NULL,
    name VARCHAR(20) NOT NULL,
    is24hours BIT,
    carparkcount INTEGER,
    hascarparkcost BIT,
    maxtimelimit REAL,
    hastouristattraction BIT,
    latitude INTEGER,
    longitude INTEGER,
    datefirstoperational TEXT,
    haschargingcost BIT,
    currenttype VARCHAR(20),
    views INTEGER default 0
    );
--SPLIT
DROP TABLE IF EXISTS connector;
--SPLIT
CREATE TABLE IF NOT EXISTS connector
(
    connectorid INTEGER NOT NULL constraint dk_connector PRIMARY KEY AUTOINCREMENT,
    connectorcurrent VARCHAR(50) NOT NULL,
    connectorpowerdraw VARCHAR(50) NOT NULL,
    count INTEGER,
    connectorstatus VARCHAR(30),
    chargerid INTEGER NOT NULL references Charger(chargerid),
    connectortype VARCHAR(50) NOT NULL
    );
--SPLIT
DROP TABLE IF EXISTS vehicle;
--SPLIT
CREATE TABLE if not exists vehicle
(
    vehicleid INTEGER NOT NULL constraint dk_Veh PRIMARY KEY AUTOINCREMENT,
    owner INTEGER NOT NULL references user(userid),
    make VARCHAR(15),
    model VARCHAR(15),
    rangekm INTEGER NOT NULL,
    connectorType VARCHAR(40),
    imgPath VARCHAR(100),
    currVehicle BIT
    );
--SPLIT
DROP TABLE IF EXISTS journey;
--SPLIT
CREATE TABLE if not exists journey
(
    journeyid INTEGER NOT NULL constraint dk_journey PRIMARY KEY AUTOINCREMENT,
    userid INTEGER NOT NULL references user(userid),
    vehicleid INTEGER NOT NULL references Vehicle(vehicleid),
    startLat REAL NOT NULL,
    startLon REAL NOT NULL,
    endLat REAL NOT NULL,
    endLon REAL NOT NULL,
    startDate TEXT,
    title TEXT
    );
--SPLIT

DROP TABLE IF EXISTS stop;
--SPLIT
CREATE TABLE if not exists stop
(   stopid INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    journeyid INTEGER NOT NULL references Journey(journeyid),
    position INTEGER NOT NULL,
    lat REAL NOT NULL,
    lon REAL NOT NULL,
    chargerid INTEGER references Charger(chargerid),
    UNIQUE(journeyid, position) ON CONFLICT REPLACE
    );