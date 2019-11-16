#Lista 3
#zad1
USE hobby;

CREATE INDEX idx_imie ON osoba (imie);
CREATE INDEX idX_data ON osoba (dataUrodzenia);
CREATE INDEX idx_sport ON sport (id,nazwa);
CREATE INDEX idx_inne on inne (nazwa, id);
CREATE INDEX idx_hobby ON hobby (osoba,id,typ);

SHOW INDEXES FROM osoba;
SHOW INDEXES FROM sport;
SHOW INDEXES FROM inne;
SHOW INDEXES FROM hobby;

#Istniały te na których był nałożony AutoIncrement - id

#zad2

SELECT plec FROM osoba WHERE imie LIKE "A%";
EXPLAIN SELECT plec FROM osoba WHERE imie LIKE "A%";

SELECT nazwa FROM sport WHERE typ = 'druzynowy' ORDER BY nazwa;
EXPLAIN SELECT nazwa FROM sport WHERE typ = 'druzynowy' ORDER BY nazwa;

SELECT x.id, y.id FROM sport AS x JOIN sport AS y ON x.lokacja=y.lokacja AND x.id>y.id; 
EXPLAIN SELECT x.id, y.id FROM sport AS x JOIN sport AS y ON x.lokacja=y.lokacja AND x.id>y.id; 

SELECT imie, nazwisko, dataUrodzenia FROM osoba WHERE dataUrodzenia>'1998-01-01';
EXPLAIN SELECT imie, nazwisko, dataUrodzenia FROM osoba WHERE dataUrodzenia>'1998-01-01';

#
DROP TABLE IF EXISTS tempTable;
CREATE TABLE tempTable SELECT nazwa, id, 'sport' AS rodzaj FROM sport;
INSERT INTO tempTable SELECT nazwa, id, 'inne' FROM inne ;
INSERT INTO tempTable SELECT nazwa, id, 'nauka' FROM nauka;    

SELECT nazwa, typ, COUNT(*) AS ilość FROM tempTable AS A JOIN hobby AS B ON A.id=B.id AND A.rodzaj=B.typ GROUP BY B.typ,A.nazwa ORDER BY ilość DESC LIMIT ilosc; 
EXPLAIN SELECT nazwa, typ, COUNT(*) AS ilość FROM tempTable AS A JOIN hobby AS B ON A.id=B.id AND A.rodzaj=B.typ GROUP BY B.typ,A.nazwa ORDER BY ilość DESC LIMIT 1; 

DROP TABLE tempTable;
#

SELECT imie, dataUrodzenia FROM osoba JOIN zwierzak ON osoba.id=zwierzak.ID ORDER BY osoba.dataUrodzenia ASC LIMIT 1;
EXPLAIN SELECT imie, dataUrodzenia FROM osoba JOIN zwierzak ON osoba.id=zwierzak.ID ORDER BY osoba.dataUrodzenia ASC LIMIT 1;


#zad3

CREATE TABLE zawody
(
id int NOT NULL AUTO_INCREMENT,
nazwa varchar(40),
pensja_min int,
pensja_max int,
CHECK (pensja_min>=2100),
PRIMARY KEY (id)
);

CREATE TABLE praca
(
idPracownika int NOT NULL,
idZawodu int NOT NULL,
pensja int
);

CREATE TABLE nZawodow
(
id int NOT NULL AUTO_INCREMENT,
nazwa varchar(40),
PRIMARY KEY (id)
);

INSERT INTO nZawodow (nazwa) VALUES ('Papiez'),('Pisarz'),('Nauczyciel'),('Informatyk'),('Prawnik'),('Kierowca'),('Lekarz'),('Sprzedawca'),('Konduktor'),('Strazak'),('Policjant');

SET @counter =1;

DELIMITER $$
CREATE PROCEDURE addZawody ()
BEGIN 
SET @counter =1;
WHILE @counter<12 DO  
	INSERT INTO zawody (nazwa,pensja_min,pensja_max) VALUES ( (SELECT nazwa FROM nZawodow WHERE id=@counter),2100+ FLOOR(RAND()*1000),3100+ FLOOR(RAND()*6000) );
    SET @counter=@counter+1;
	
    END WHILE;
END;$$
DELIMITER ;
CALL addZawody();

SELECT * FROM zawody;
DROP PROCEDURE addZawody;
DROP TABLE nZawodow;


DELIMITER $$
CREATE PROCEDURE kursor ()
BEGIN
	
    DECLARE koniec SMALLINT UNSIGNED;
    DECLARE idP int;
    DECLARE idZ int;
    DECLARE pensja int;

	DECLARE kur CURSOR FOR SELECT id FROM osoba;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET koniec = 1;
    
    OPEN kur;
    petla: LOOP
		FETCH kur INTO idP;
        
        IF koniec = 1 THEN 
			LEAVE petla;
		END IF;
        
        SET idZ = CEIL(RAND()*11);
        SET pensja = FLOOR(RAND()*(SELECT pensja_max-pensja_min FROM zawody WHERE id=idZ))+(SELECT pensja_min FROM zawody WHERE id=idZ);
        INSERT INTO praca VALUES (idP,idZ,pensja);
    END LOOP petla;
END; $$
DELIMITER ;

DROP PROCEDURE kursor;

CALL kursor();    
    
#zad4

    
DELIMITER $$
CREATE PROCEDURE zad4 (
IN agg VARCHAR(20), 
IN kol VARCHAR(20), 
OUT X TEXT)

BEGIN
    SET X = NULL;
    SET @result='';
	
    IF LOWER(kol) = 'imie' OR
		LOWER(kol) = 'plec' OR
		LOWER(kol) = 'nazwisko' OR
		LOWER(kol) = 'dataUrodzenia' THEN
        SET @result='';
	ELSE
		SIGNAL SQLSTATE '45000' 
    SET MESSAGE_TEXT = 'Niepoprawna nazwa kolumny';
	END IF;
    
    
    IF UPPER(agg) = 'COUNT' AND LOWER(kol) IN ('imie','plec') THEN
		SET @question = CONCAT('SELECT COUNT(',kol,') FROM osoba INTO @result');
		PREPARE statement FROM @question;
		EXECUTE statement;
		DEALLOCATE PREPARE statement;
        
	ELSEIF UPPER(agg) = 'GROUP_CONCAT' AND LOWER(kol) IN ('imie','nazwisko','dataUrodzenia','plec') THEN
		SET @question = CONCAT ('SELECT group_concat(',kol,') FROM osoba INTO @result');
		PREPARE statement FROM @question;
		EXECUTE statement;
		DEALLOCATE PREPARE statement;

	ELSEIF UPPER(agg) IN ('MIN','MAX') AND LOWER(kol) IN ('dataUrodzenia') THEN
		SET @question = CONCAT ('SELECT ',agg,'(dataUrodzenia) FROM osoba INTO @result');
		PREPARE statement FROM @question;
		EXECUTE statement;
		DEALLOCATE PREPARE statement;
        
	ELSEIF UPPER(agg) IN ('AVG','STD','VAR_POP') AND LOWER(kol) IN ('dataUrodzenia') THEN
		SET @question = CONCAT ('SELECT ',agg,'(YEAR(CURDATE())-YEAR(dataUrodzenia)) FROM osoba INTO @result');
		PREPARE statement FROM @question;
		EXECUTE statement;
		DEALLOCATE PREPARE statement;
	
    ELSE
		SIGNAL SQLSTATE '45000' 
		SET MESSAGE_TEXT = 'Niepoprawna nazwa funkcji agregującej lub próba wykonania jej na złych kolumnach';
	
    END IF;
    
SET X = CONCAT(kol,", ",agg,", ",@result)  ;  

END; $$
DELIMITER ;


CALL zad4('count','imie',@wynik);
SELECT @wynik;

CALL zad4('count','nazwisko',@wynik);
SELECT @wynik;

CALL zad4('GROUP_CONCAT','plec',@wynik);
SELECT @wynik;

CALL zad4('Max','dataUrodzenia',@wynik);
SELECT @wynik;

CALL zad4('std','dataUrodzenia',@wynik);
SELECT @wynik;
    

DROP PROCEDURE zad4;

#zad5

CREATE TABLE hasla
(
id int,
pass varchar(40) NOT NULL);

DROP TABLE hasla;

DELIMITER $$
CREATE PROCEDURE wypelniacz ()
BEGIN

    DECLARE koniec SMALLINT UNSIGNED;
	DECLARE idd int;
    DECLARE nam varchar(40);
    
    DECLARE kur2 CURSOR FOR SELECT id, imie FROM osoba;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET koniec = 1;
    
    OPEN kur2;
    petla: LOOP
		FETCH kur2 INTO idd, nam;
        
        IF koniec = 1 THEN 
			LEAVE petla;
		END IF;
        
        INSERT INTO hasla (id, pass) VALUES (idd, sha1(CONCAT(nam,idd)));
    END LOOP petla;



END; $$
DELIMITER ;

CALL wypelniacz();

DELETE FROM hasla;
DROP procedure wypelniacz;


DELIMITER $$
CREATE PROCEDURE logowanie (IN nam VARCHAR(40),IN idd INT, IN has VARCHAR(40), OUT dataU date)
BEGIN
	SET @tester = sha1(CONCAT(nam,idd));
    IF(@tester=has) THEN
		SET dataU = (SELECT DISTINCT dataUrodzenia FROM osoba JOIN hasla ON osoba.id=hasla.id WHERE osoba.id=idd);
	ELSE 
		SET dataU = DATE(NOW()) + INTERVAL -90 YEAR + INTERVAL RAND()*70*365.25 DAY;
	END IF;
    
END; $$
DELIMITER ;


CALL logowanie('Diane','2','1a8bf017fc6631c5e8d06459d9c64848eaec6270',@outData);
SELECT @outData; #true
CALL logowanie('Diane','2','2a8bf017fc6631c5e8d06459d9c64848eaec6270',@outData);
SELECT @outData; #false


#zad6



