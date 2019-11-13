#zad1
CREATE USER 'Maciej02@localhost';
SET PASSWORD FOR 'Maciej02@localhost' = password('200542');
GRANT Alter,Select,Insert,Update,Delete  ON *.* TO 'Maciej02@localhost';
GRANT ALL PRIVILEGES ON *.* TO 'Maciej02@localhost';
FLUSH PRIVILEGES;

CREATE DATABASE hobby;

USE hobby;

#zad2

CREATE TABLE osoba
(
id int NOT NULL AUTO_INCREMENT,
imie varchar(20) NOT NULL,
dataUrodzenia date NOT NULL,
plec char(1) NOT NULL,
PRIMARY KEY (id),
CHECK (dataUrodzenia>= (SELECT DATE_ADD(CURTIME(), INTERVAL 18 YEAR)))
);


CREATE TABLE sport
(
id int NOT NULL AUTO_INCREMENT,
nazwa varchar(20) NOT NULL,
typ enum('indywidualny', 'druzynowy', 'mieszany') DEFAULT 'druzynowy' NOT NULL,
lokacja varchar(20),
PRIMARY KEY (id)
);

CREATE TABLE nauka
(
id int NOT NULL AUTO_INCREMENT,
nazwa varchar(20) NOT NULL,
lokacja varchar(20),
PRIMARY KEY (id)
);

CREATE TABLE inne
(
id int NOT NULL AUTO_INCREMENT,
nazwa varchar(20) NOT NULL,
lokacja varchar(20),
towarzysze bool NOT NULL DEFAULT true,
PRIMARY KEY (id)
);

CREATE TABLE hobby
(
osoba int,
id int NOT NULL DEFAULT 0,
typ enum('sport','nauka','inne') NOT NULL,
CONSTRAINT PRIMARY KEY
primKeyName (id, typ, osoba)
);


#zad3

CREATE TABLE zwierzak
(
  name    VARCHAR(20),
  owner   VARCHAR(20),
  species VARCHAR(20),
  sex     CHAR(1),
  birth   DATE
);


USE menagerie;

SELECT * INTO OUTFILE '/var/lib/mysql-files/pet3.txt'
FROM pet;
SELECT * FROM pet;


USE hobby;

DELETE FROM zwierzak;
LOAD DATA LOCAL INFILE '/var/lib/mysql-files/pet3.txt' INTO TABLE zwierzak;
SELECT * FROM zwierzak;


DELETE FROM osoba;
INSERT INTO osoba(imie, dataUrodzenia, plec) 
(SELECT DISTINCT owner, DATE(NOW()) + INTERVAL -90 YEAR + INTERVAL RAND()*70*365.25 DAY, 't' 
FROM zwierzak 
GROUP BY owner);

UPDATE osoba
SET plec = IF(imie LIKE ('%e'), 'f', 'm');

SELECT * FROM osoba;


#zad4
ALTER TABLE osoba ADD COLUMN nazwisko varchar(50) NULL AFTER imie;

ALTER TABLE zwierzak ADD COLUMN ID int NULL AFTER name;

UPDATE zwierzak
SET ID = (SELECT id FROM osoba WHERE zwierzak.owner = imie);
ALTER TABLE zwierzak DROP owner;

SELECT * FROM zwierzak;


#zad5

ALTER TABLE hobby
ADD CONSTRAINT hobbyFK FOREIGN KEY
(osoba) REFERENCES osoba(id);

ALTER TABLE zwierzak
ADD CONSTRAINT zwierzakFK FOREIGN KEY
(ID) REFERENCES osoba(id);

#zad6

ALTER TABLE inne auto_increment = 7000;

#zad7

#Funkcja generujaca losowe ciągi znakow
DELIMITER $$
CREATE DEFINER=`root`@`%` FUNCTION `RandString`(length SMALLINT(3)) RETURNS varchar(100) CHARSET utf8
begin
    SET @returnStr = '';
    SET @allowedChars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
    SET @i = 0;

    WHILE (@i < length) DO
        SET @returnStr = CONCAT(@returnStr, substring(@allowedChars, FLOOR(RAND() * LENGTH(@allowedChars) + 1), 1));
        SET @i = @i + 1;
    END WHILE;
    RETURN @returnStr;
END;$$
DELIMITER ;

#Wlasciwa procedura
DECLARE @bCounter , @temp INT,
DELIMITER $$
CREATE PROCEDURE dod (IN tabName varchar(20), IN ilosc int)
BEGIN 
    SET @bCounter =ilosc;
    WHILE @bCounter!=0 DO
    
		IF tabName='inne' THEN
			INSERT INTO inne (nazwa,lokacja,towarzysze) VALUES (  RANDSTRING(8+FLOOR(RAND()*5)) , RANDSTRING(10+FLOOR(RAND()*5)), IF (FLOOR(RAND()*2) =0 , true, false));
        ELSEIF tabName='nauka' THEN
			INSERT INTO nauka (nazwa,lokacja) VALUES (  RANDSTRING(8+FLOOR(RAND()*5)) , RANDSTRING(10+FLOOR(RAND()*5)));
        ELSEIF tabName='osoba' THEN
			INSERT INTO osoba (imie,nazwisko,dataUrodzenia,plec) VALUES(RANDSTRING(3+FLOOR(RAND()*4)), RANDSTRING(3+FLOOR(RAND()*6)), DATE(NOW()) + INTERVAL -90 YEAR + INTERVAL RAND()*70*365.25 DAY , IF (FLOOR(RAND()*2)=0,'m','f'));
        ELSEIF tabName='sport' THEN
			INSERT INTO sport (nazwa, typ, lokacja) VALUES ( RANDSTRING(8+FLOOR(RAND()*5)),IF (FLOOR(RAND()*3) =0 , 'indywidualny', IF (FLOOR(RAND()*2) =0 , 'druzynowy', 'mieszany') ), RANDSTRING(10+FLOOR(RAND()*5)) );
		ELSEIF tabName='hobby' THEN
			IF FLOOR(RAND()*3) =0 THEN
				SET @temp = (SELECT id FROM sport ORDER BY RAND() LIMIT 1);
				INSERT INTO hobby (osoba,typ,id) VALUES( (SELECT id FROM osoba ORDER BY RAND() LIMIT 1) , 'sport', @temp);
            ELSEIF FLOOR(RAND()*2) =0 THEN
				SET @temp = (SELECT id FROM nauka ORDER BY RAND() LIMIT 1);
				INSERT INTO hobby (osoba,typ,id) VALUES( (SELECT id FROM osoba ORDER BY RAND() LIMIT 1) , 'nauka', @temp);
            ELSE 
				SET @temp = (SELECT id FROM inne ORDER BY RAND() LIMIT 1);
				INSERT INTO hobby (osoba,typ,id) VALUES( (SELECT id FROM osoba ORDER BY RAND() LIMIT 1) , 'inne', @temp);
            END IF;
        END IF;
        
		SET @bCounter = @bCounter-1;
	END WHILE;
END;$$
DELIMITER ;

CALL dod('osoba',1000);
CALL dod('sport',300);
CALL dod('nauka',300);
CALL dod('inne',500);
CALL dod('hobby',1300);
		DELETE FROM hobby;
        DROP PROCEDURE dod;
#Zad8
DECLARE @sql VARCHAR(100),
DELIMITER $$
$$
CREATE PROCEDURE wypiszHobby (IN id INT,IN category VARCHAR(20))
BEGIN

	SET @sql = CONCAT('SELECT ',category,'.nazwa FROM hobby JOIN ',category,' ON  hobby.typ=''',category,''' AND hobby.id=',category,'.id WHERE hobby.osoba=',id);
    PREPARE hobbyList FROM @sql;
	EXECUTE hobbyList;
	DEALLOCATE PREPARE hobbyList;

END$$
DELIMITER ;

call wypiszHobby(19,'inne');

#Zad9
DECLARE @sql2 VARCHAR(100),
DELIMITER $$
$$
CREATE PROCEDURE wypiszHobbyOsoby (IN id INT)
BEGIN
	
    SET @sql2 = CONCAT('CREATE TABLE temptable SELECT inne.nazwa FROM hobby JOIN inne ON  hobby.typ=''inne'' AND hobby.id=inne.id WHERE hobby.osoba=', id);
    PREPARE wypiszHobbyList FROM @sql2;
    EXECUTE wypiszHobbyList;
    SET @sql2 = CONCAT('INSERT INTO temptable SELECT sport.nazwa FROM hobby JOIN sport ON  hobby.typ=''sport'' AND hobby.id=sport.id WHERE hobby.osoba=', id);
    PREPARE wypiszHobbyList FROM @sql2;
    EXECUTE wypiszHobbyList;
    SET @sql2 = CONCAT('INSERT INTO temptable SELECT nauka.nazwa FROM hobby JOIN nauka ON  hobby.typ=''nauka'' AND hobby.id=nauka.id WHERE hobby.osoba=', id);
    PREPARE wypiszHobbyList FROM @sql2;
    EXECUTE wypiszHobbyList;
    SELECT nazwa AS 'Nazwa Hobby' FROM temptable;
    DROP TABLE temptable;
    
    DEALLOCATE PREPARE wypiszHobbyList;
END$$
DELIMITER ;

call wypiszHobbyOsoby(3);
    
#10
DECLARE @sql2 VARCHAR(100),
DELIMITER $$
$$
CREATE PROCEDURE wypiszHobbyOsoby2 (IN id INT)
BEGIN
    SET @sql2 = CONCAT('CREATE TABLE temptable SELECT inne.nazwa FROM hobby JOIN inne ON  hobby.typ=''inne'' AND hobby.id=inne.id WHERE hobby.osoba=', id);
    PREPARE wypiszHobbyList FROM @sql2;
    EXECUTE wypiszHobbyList;
    
    SET @sql2 = CONCAT('INSERT INTO temptable SELECT sport.nazwa FROM hobby JOIN sport ON  hobby.typ=''sport'' AND hobby.id=sport.id WHERE hobby.osoba=', id);
    PREPARE wypiszHobbyList FROM @sql2;
    EXECUTE wypiszHobbyList;
    
    SET @sql2 = CONCAT('INSERT INTO temptable SELECT nauka.nazwa FROM hobby JOIN nauka ON  hobby.typ=''nauka'' AND hobby.id=nauka.id WHERE hobby.osoba=', id);
    PREPARE wypiszHobbyList FROM @sql2;
    EXECUTE wypiszHobbyList;
    
    SET @sql2 = CONCAT('INSERT INTO temptable SELECT DISTINCT species FROM zwierzak WHERE ID=', id);
    PREPARE wypiszHobbyList FROM @sql2;
    EXECUTE wypiszHobbyList;
    
    SELECT nazwa AS 'Nazwa Hobby' FROM temptable;
    DROP TABLE temptable;
    
    DEALLOCATE PREPARE wypiszHobbyList;
END$$
DELIMITER ;

call wypiszHobbyOsoby2(1);   

#zad11
 

 
DROP TRIGGER IF EXISTS zad11;
DELIMITER $$
$$ 
CREATE TRIGGER zad11 BEFORE INSERT ON hobby
FOR EACH ROW
BEGIN
	IF NEW.osoba NOT IN (SELECT id FROM osoba) THEN
		SET NEW.osoba = (SELECT DISTINCT id FROM osoba ORDER BY RAND());
	END IF;
    
    IF NEW.typ='nauka' THEN
		SET NEW.id = (SELECT DISTINCT MAX(id) FROM nauka)+1;
        CALL dod('nauka',1);
	ELSEIF NEW.typ='sport' THEN
		SET NEW.id = (SELECT DISTINCT MAX(id) FROM sport)+1;
        CALL dod('sport',1);
	ELSEIF NEW.typ='inne' THEN
		SET NEW.id = (SELECT DISTINCT MAX(id) FROM inne)+1;
        CALL dod('inne',1);
        
	END IF;
END$$
DELIMITER ;
    
INSERT INTO hobby VALUES (2,123,'inne');


#zad12

DROP TRIGGER IF EXISTS zad12;
DELIMITER $$
$$ 
CREATE TRIGGER zad12 AFTER DELETE ON sport
FOR EACH ROW
BEGIN
DELETE FROM hobby
    WHERE hobby.id = OLD.id;
    
END$$
DELIMITER ;

#zad13

DROP TRIGGER IF EXISTS zad13a;
DELIMITER $$
$$ 
CREATE TRIGGER zad13a AFTER DELETE ON nauka
FOR EACH ROW
BEGIN
DELETE FROM hobby
    WHERE hobby.id = OLD.id;
    
END$$
DELIMITER ;

#? xD
DROP TRIGGER IF EXISTS zad13b;
DELIMITER $$
$$ 
CREATE TRIGGER zad13b BEFORE UPDATE ON nauka
FOR EACH ROW
BEGIN

	IF NEW.id IN (SELECT id FROM nauka) THEN
		SET NEW.id = OLD.id;
	END IF;


END$$
DELIMITER ;


#zad14

DROP TRIGGER IF EXISTS takiseTrigger;
DELIMITER $$
$$ 
CREATE TRIGGER takiseTrigger BEFORE DELETE ON hobby
FOR EACH ROW
BEGIN
	DELETE FROM inne WHERE OLD.id = inne.id AND OLD.typ='inne';
	DELETE FROM sport WHERE OLD.id = sport.id AND OLD.typ='sport';
	DELETE FROM nauka WHERE OLD.id = nauka.id AND OLD.typ='nauka';
END$$
DELIMITER ;



DROP TRIGGER IF EXISTS zad14;
DELIMITER $$
$$ 
CREATE TRIGGER zad14 BEFORE DELETE ON osoba
FOR EACH ROW
BEGIN
    DELETE FROM hobby WHERE osoba = OLD.id; 
    UPDATE zwierzak SET ID =(SELECT id FROM osoba ORDER BY RAND() LIMIT 1) WHERE zwierzak.ID = OLD.ID;
 
END$$
DELIMITER ;

DELETE FROM osoba WHERE osoba.id=1;




/* 
Zad15
Tak bo odnoszą się do różnych zdarzeń, różnych tabel i nie kolidują ze sobą, oraz nie wywołują siebie wzajemnie w pętli
*/

#zad16

CREATE TABLE tempTable(
id int NOT NULL,
nazwa varchar(20) NOT NULL,
typ varchar(20) NOT NULL,
lokacja varchar(20),
towarzysze BOOL,
typHobby varchar(20) NOT NULL
);

INSERT INTO tempTable (id, nazwa,lokacja,typ,towarzysze, typHobby) (SELECT id, nazwa, lokacja, '-', towarzysze, 'inne' FROM inne);
INSERT INTO tempTable (id, nazwa,lokacja,typ,towarzysze, typHobby) (SELECT id, nazwa, lokacja, '-', false, 'nauka' FROM nauka);
INSERT INTO tempTable (id, nazwa,lokacja,typ,towarzysze, typHobby) (SELECT id, nazwa, lokacja, typ, false, 'sport' FROM sport);

CREATE VIEW widok AS
	(SELECT A.id AS id, A.nazwa AS nazwa, A.typHobby as typHobby, A.lokacja as lokacja, A.towarzysze as towarzysze, A.typ as typ, B.osoba as osoba FROM tempTable AS A JOIN hobby AS B ON A.id=B.id AND A.typHobby=B.typ);

CREATE VIEW zad16 AS
	(SELECT id, typHobby, nazwa, lokacja, towarzysze, typ, COUNT(*) AS 'Ilość zajmujących się tym hobby' FROM widok GROUP BY typHobby, id, nazwa, lokacja, towarzysze, typ ORDER BY id);

SELECT * FROM zad16;


DROP VIEW widok;
DROP TABLE tempTable;

#zad17


CREATE TABLE tempTable2(
id int NOT NULL,
nazwa varchar(20) NOT NULL,
typ varchar(20) NOT NULL,
lokacja varchar(20),
towarzysze BOOL,
typHobby varchar(20) NOT NULL,
idWlasciciela int DEFAULT 0
);

INSERT INTO tempTable2 (id, nazwa,lokacja,typ,towarzysze, typHobby) (SELECT id, nazwa, lokacja, '-', towarzysze, 'inne' FROM inne);
INSERT INTO tempTable2 (id, nazwa,lokacja,typ,towarzysze, typHobby) (SELECT id, nazwa, lokacja, '-', false, 'nauka' FROM nauka);
INSERT INTO tempTable2 (id, nazwa,lokacja,typ,towarzysze, typHobby) (SELECT id, nazwa, lokacja, typ, false, 'sport' FROM sport);
INSERT INTO tempTable2 (id, nazwa,lokacja,typ,towarzysze, typHobby, idWlasciciela) (SELECT 0, name , '-', species, false, 'zwierzak',ID FROM zwierzak);

CREATE VIEW zad17 AS
	(SELECT DISTINCT osoba, imie, nazwisko, dataUrodzenia, plec, A.id AS 'id sportu' , A.nazwa AS 'nazwa sportu', typHobby, lokacja, towarzysze, A.typ as typ
    FROM tempTable2 AS A JOIN hobby AS B ON ((A.id=B.id AND A.typHobby=B.typ) OR B.osoba=A.idWlasciciela) JOIN osoba as C ON (B.osoba = C.id) ORDER BY osoba);



SELECT * FROM zad17;
DROP VIEW zad17;
DROP TABLE tempTable2;

#zad18
#Napisz procedurę bez argumentów wejściowych, z jednym argumentem wyjściowym (lub funkcję zwracającą), która wróci imię oraz wiek osoby posiadającej największą liczbę hobby.

SELECT osoba, count(*) AS 'Ilość hobby' FROM hobby GROUP BY osoba ORDER BY count(*) DESC, rand() ;

DELIMITER $$
CREATE PROCEDURE zad18 (OUT stringOut varchar(40))
BEGIN 
SET @nrOsoby = (SELECT osoba AS 'Ilość hobby' FROM hobby GROUP BY osoba ORDER BY count(*) DESC, rand() LIMIT 1);
SET @imie=(SELECT imie FROM osoba WHERE id=@nrOsoby);
SET @urodzony=(SELECT dataUrodzenia FROM osoba WHERE id=@nrOsoby);
SET @wiek=(  (YEAR(CURDATE()) - YEAR(@urodzony)) - (RIGHT(CURDATE(), 5) < RIGHT(@urodzony, 5)));
SET stringOut=concat(@imie,"-",@wiek," lat");
#SET stringOut=@imie;

END$$
DELIMITER ;

CALL zad18(@String18);
SELECT @String18;

DROP PROCEDURE zad18;