-- use centralDatabase;
drop table gate;
drop table runway;
drop table flight;
drop table airportUsers;
drop table aircraft;
drop table airport;
drop table airlineUsers;

create table aircraft
	(tailNumber		varchar(6), 
	 manufacturer	varchar(50),
	 model			varchar(50), 
	 primary key (tailNumber)
	);

create table airport
	(IATA		varchar(20), 
	 name		varchar(100), 
	 city		varchar(50),
	 state 		varchar(50),
	 country 	varchar(50), 
	 humanResources int, 
	 runways 	int, 
	 gates 		int, 
	 primary key (IATA)
	);

create table airlineUsers
	(userid 	varchar(20),
	 name		varchar(20), 
	 password 	varchar(100), 
	 primary key (userid)
	);

create table airportUsers
	(userid 	varchar(20), 
	 password 	varchar(100), 
	 primary key (userid),
	 foreign key (userid) references airport (IATA)
	 	on delete cascade
	);

create table flight
	(aircraft			varchar(20), 
	 id					varchar(20), 
	 origin		    	varchar(20),
	 destination    	varchar(20),
	 SGDT				datetime,
	 EGDT				datetime,
	 SGAT				datetime,
	 EGAT				datetime,
	 SRDT				datetime,
	 ERDT				datetime,
	 SRAT				datetime,
	 ERAT				datetime,
	 duration				int,
	 distance				int,
	 numPassengers			int,
	 status					varchar(20),
	 gateNo					int,
	 resources_alloc		int,
	 resources_req			int,
	 runwayNumber			int,
	 enterQueueNumber		int,
	 currentQueueNumber		int,
	 primary key (aircraft, SGDT),
	 foreign key (aircraft) references aircraft (tailNumber)
	 	on delete cascade,
	 foreign key (origin) references airport (IATA)
	 	on delete cascade,
	 foreign key (destination) references airport (IATA)
	 	on delete cascade
	);

create table gate
	(IATA		varchar(20), 
	 gateNumber	int,
	 status     int,
	 aircraft	varchar(20),
	 SGDT		datetime,
	 primary key (IATA, gateNumber),
	 foreign key (IATA) references airport (IATA)
	 	on delete cascade,
	 foreign key (aircraft, SGDT) references flight (aircraft, SGDT)
	 	on delete cascade
	);

create table runway
	(IATA		varchar(20), 
	 runwayNumber	int,
	 queueLen 	int,
	 primary key (IATA, runwayNumber),
	 foreign key (IATA) references airport (IATA)
	 	on delete cascade
	); 
insert into airlineUsers values ('JA', 'Jet Airways', 'JA'),('IN','Indigo', 'IN'),('AI','Air India', 'AI'),('SJ', 'Spice Jet', 'SJ'),('VI', 'Vistara', 'VI');

LOAD DATA INFILE '/var/lib/mysql-files/aircrafts.csv' 
INTO TABLE aircraft 
FIELDS TERMINATED BY ','
ENCLOSED BY '"' 
LINES TERMINATED BY '\n'
IGNORE 1 ROWS;

LOAD DATA INFILE '/var/lib/mysql-files/airports.csv' 
INTO TABLE airport
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS;

LOAD DATA INFILE '/var/lib/mysql-files/airportsUsers.csv' 
INTO TABLE airportUsers
FIELDS TERMINATED BY ',' 
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS;

LOAD DATA INFILE '/var/lib/mysql-files/flights.csv' 
INTO TABLE flight 
FIELDS TERMINATED BY ','
ENCLOSED BY '"' 
LINES TERMINATED BY '\n'
(SGDT, id, aircraft, duration,origin,destination,distance,numPassengers);

update flight set SGAT = DATE_ADD(SGDT, INTERVAL duration MINUTE);

UPDATE flight SET EGDT = SGDT, EGAT = SGAT, status = 'NOGATE', ERDT = SGDT, ERAT = SGAT, SRDT = SGDT, SRAT = SGAT;

insert into gate (IATA,gatenumber,status) values ('LAS',1,0),('LAS',2,0),('LAS',3,0),('LAS',4,0),('LAS',5,0),('LAS',6,0),('LAS',7,0),('LAS',8,0),('LAS',9,0),('LAS',10,0);
insert into runway values ('LAS',1,0),('LAS',2,0),('LAS',3,0);
update flight set resources_req = numPassengers*0.1 + 5,resources_alloc = 0;
update flight set status = 'TAKEOFF' where destination = 'LAS' and EGDT < CURRENT_TIMESTAMP and SGAT > CURRENT_TIMESTAMP;
update flight set status = 'TAKEOFF' where origin = 'LAS' and EGDT < CURRENT_TIMESTAMP;




