DROP DATABASE klinika;
CREATE DATABASE klinika;
USE klinika;


#tworzenie tabel 

CREATE TABLE terminy
(
g_id int NOT NULL AUTO_INCREMENT,
dzien date NOT NULL,
godzina time NOT NULL,
id_lekarza int NOT NULL,
PRIMARY KEY (g_id)
);

CREATE TABLE rasy
(
r_id int NOT NULL AUTO_INCREMENT,
gatunek VARCHAR(45),
rasa VARCHAR(45),
PRIMARY KEY (r_id)
);


CREATE TABLE wlasciciele
(
w_id INT NOT NULL AUTO_INCREMENT,
imie VARCHAR(45) NOT NULL,
nazwisko VARCHAR(45) NOT NULL,
pesel CHAR(11) NOT NULL,
ulica VARCHAR(45) NOT NULL,
nr_domu VARCHAR(45),
nr_mieszkania VARCHAR(10),
kod_pocztowy CHAR(6),
PRIMARY KEY(w_id)
);


ALTER TABLE wlasciciele auto_increment = 1000;

CREATE TABLE pracownicy
(
staff_id INT NOT NULL AUTO_INCREMENT,
imie VARCHAR(45) NOT NULL,
nazwisko VARCHAR(45) NOT NULL,
pesel CHAR(11) NOT NULL,
typ ENUM('lekarz','sekretariat','admin'),
PRIMARY KEY(staff_id)
);

CREATE TABLE uzytkownicy 
(
id_u INT NOT NULL,
login VARCHAR(25) NOT NULL,
haslo VARCHAR(50) NOT NULL,
PRIMARY KEY(id_u)
);

CREATE TABLE pacjenci
(
p_id INT NOT NULL AUTO_INCREMENT,
nazwa VARCHAR(45) NOT NULL,
id_wlasciciela INT NOT NULL,
id_rasy INT NOT NULL,
rok_urodzenia INT,
umaszczenie VARCHAR(45),
PRIMARY KEY (p_id),
FOREIGN KEY (id_rasy) REFERENCES rasy(r_id),
FOREIGN KEY (id_wlasciciela) REFERENCES wlasciciele(w_id)
);

CREATE TABLE wizyty(
w_id INT NOT NULL AUTO_INCREMENT,
id_pacjenta INT NOT NULL,
id_terminu INT NOT NULL,
PRIMARY KEY (w_id),
FOREIGN KEY (id_terminu) REFERENCES terminy(g_id)
);

CREATE TABLE notatki(
n_id INT NOT NULL AUTO_INCREMENT,
id_pacjenta INT NOT NULL,
id_wizyty INT NOT NULL,
komentarz VARCHAR(1500) NOT NULL,
PRIMARY KEY (n_id),
FOREIGN KEY (id_pacjenta) REFERENCES pacjenci(p_id),
FOREIGN KEY (id_wizyty) REFERENCES wizyty(w_id)
);







########################
#Funkcja generujaca losowe ciągi znakow

DELIMITER $$
CREATE DEFINER=`root`@`%` FUNCTION `RandString`(length SMALLINT(3)) RETURNS varchar(100) CHARSET utf8
begin
    SET @returnStr = '';
    SET @allowedChars = 'ABCDEFGHIJKLMNOPRSTUWYZ';
    SET @i = 0;

    WHILE (@i < length) DO
        SET @returnStr = CONCAT(@returnStr, substring(@allowedChars, FLOOR(RAND() * LENGTH(@allowedChars) + 1), 1));
        SET @i = @i + 1;
    END WHILE;
    RETURN @returnStr;
END;$$
DELIMITER ;
###################
#Funkcja generujaca losowy pesel

DROP FUNCTION IF EXISTS randomPesel;
DELIMITER $$
CREATE FUNCTION randomPesel() RETURNS char(11) CHARSET utf8
begin
    SET @returnStr = '';
    #losowy rok
	SET @temp = floor( rand() * (79) + 21 );
    IF @temp<10 THEN
		SET @temp=concat('0',@temp);
	END IF;
	SET @returnStr = concat(@returnStr,@temp);
  
    
    #miesiac
    SET @temp = floor( rand() * (12) + 1 );
	IF @temp<10 THEN
		SET @temp=concat('0',@temp);
	END IF;
	SET @returnStr = concat(@returnStr,@temp);
    
    #dzien
    SET @temp = floor( rand() * (28) + 1);
    IF @temp<10 THEN
		SET @temp=concat('0',@temp);
	END IF;
	SET @returnStr = concat(@returnStr,@temp);
    
    #losowe 4 znaki
    SET @temp = floor( rand() *9000 +1000);
    SET @returnStr = concat(@returnStr,@temp);
    
    #liczba kontrolna
    SET @temp = MID(MID(@returnStr,1,1)*9+MID(@returnStr,2,1)*7+MID(@returnStr,3,1)*3+MID(@returnStr,4,1)+MID(@returnStr,5,1)*9+MID(@returnStr,6,1)*7+MID(@returnStr,7,1)*3+MID(@returnStr,8,1)+MID(@returnStr,9,1)*9+MID(@returnStr,10,1)*7 ,-1,1);
    SET @returnStr = concat(@returnStr,@temp);
    
    
    RETURN @returnStr;
END;$$
DELIMITER ;


############################

#triggery

#sprawdzanie peseli
DROP TRIGGER IF EXISTS sprawdzPracownika;
DELIMITER $$
$$ 
CREATE TRIGGER sprawdzPracownika BEFORE INSERT ON pracownicy
FOR EACH ROW
BEGIN
    IF MID(MID(NEW.pesel,1,1)*9+MID(NEW.pesel,2,1)*7+MID(NEW.pesel,3,1)*3+MID(NEW.pesel,4,1)+MID(NEW.pesel,5,1)*9+MID(NEW.pesel,6,1)*7+MID(NEW.pesel,7,1)*3+MID(NEW.pesel,8,1)*1+MID(NEW.pesel,9,1)*9+MID(NEW.pesel,10,1)*7 ,-1,1) <>MID(NEW.pesel,11,1) THEN
		SIGNAL SQLSTATE '45000' 
		SET MESSAGE_TEXT = 'Bledny pesel';
	END IF;
    
    IF (SELECT COUNT(*) FROM pracownicy WHERE pesel=NEW.pesel AND TYP=NEW.typ )<>0 THEN
		SIGNAL SQLSTATE '45000' 
		SET MESSAGE_TEXT = 'Osoba istnieje juz w bazie';
	END IF;
    
	SET NEW.staff_id = (SELECT DISTINCT MAX(staff_id) FROM pracownicy)+1;

END$$
DELIMITER ;


DROP TRIGGER IF EXISTS sprawdzWlasciciela;
DELIMITER $$
$$ 
CREATE TRIGGER sprawdzWlasciciela BEFORE INSERT ON wlasciciele
FOR EACH ROW
BEGIN
    IF MID(MID(NEW.pesel,1,1)*9+MID(NEW.pesel,2,1)*7+MID(NEW.pesel,3,1)*3+MID(NEW.pesel,4,1)+MID(NEW.pesel,5,1)*9+MID(NEW.pesel,6,1)*7+MID(NEW.pesel,7,1)*3+MID(NEW.pesel,8,1)*1+MID(NEW.pesel,9,1)*9+MID(NEW.pesel,10,1)*7 ,-1,1) <>MID(NEW.pesel,11,1) THEN
		SIGNAL SQLSTATE '45000' 
		SET MESSAGE_TEXT = 'Bledny pesel';
	END IF;
    
    IF (SELECT COUNT(*) FROM wlasciciele WHERE pesel=NEW.pesel)<>0 THEN
		SIGNAL SQLSTATE '45000' 
		SET MESSAGE_TEXT = 'Osoba istnieje juz w bazie';
	END IF;
    
	SET NEW.w_id = (SELECT DISTINCT MAX(w_id) FROM wlasciciele)+1;

	

END$$
DELIMITER ;


#############################################
#dodawanie pracownika i wlasciciela do tabeli uzytkownikow

DROP TRIGGER IF EXISTS dodajPracownika;
DELIMITER $$
$$ 
CREATE TRIGGER dodajPracownika AFTER INSERT ON pracownicy
FOR EACH ROW
BEGIN
	
    IF NEW.typ='lekarz' THEN
		INSERT INTO uzytkownicy (id_u,login,haslo) VALUES(NEW.staff_id, CONCAT(NEW.pesel,'l'),sha1(CONCAT(NEW.imie,NEW.nazwisko)));
	ELSEIF NEW.typ<>'admin' THEN
		INSERT INTO uzytkownicy (id_u,login,haslo) VALUES(NEW.staff_id, CONCAT(NEW.pesel,'s'),sha1(CONCAT(NEW.imie,NEW.nazwisko)));
	ELSE
		INSERT INTO uzytkownicy (id_u,login,haslo) VALUES(NEW.staff_id, CONCAT('admin',NEW.staff_id),sha1(CONCAT(NEW.imie,NEW.nazwisko)));
	END IF;

END$$
DELIMITER ;


DROP TRIGGER IF EXISTS dodajWlasciciela;
DELIMITER $$
$$ 
CREATE TRIGGER dodajWlasciciela AFTER INSERT ON wlasciciele
FOR EACH ROW
BEGIN
	INSERT INTO uzytkownicy (id_u,login,haslo) VALUES(NEW.w_id, CONCAT(NEW.pesel,'w'),sha1(CONCAT(NEW.imie,NEW.nazwisko)));
   
END$$
DELIMITER ;

#######################################################
#usuwanie wlasciciela i pracownika z tabeli uzytkownikow

DROP TRIGGER IF EXISTS usunWlasciciela;
DELIMITER $$
$$ 
CREATE TRIGGER usunWlasciciela AFTER DELETE ON wlasciciele
FOR EACH ROW
BEGIN
	DELETE FROM uzytkownicy WHERE id_u=OLD.w_id;
END$$
DELIMITER ;

DROP TRIGGER IF EXISTS usunPracownika;
DELIMITER $$
$$ 
CREATE TRIGGER usunPracownika AFTER DELETE ON pracownicy
FOR EACH ROW
BEGIN
	DELETE FROM uzytkownicy WHERE id_u=OLD.staff_id;
END$$
DELIMITER ;

#######################################################
#usuwanie wszystkich notatek po usunieciu pacjenta

DROP TRIGGER IF EXISTS usunPacjenta;
DELIMITER $$
$$ 
CREATE TRIGGER usunPacjenta AFTER DELETE ON pacjenci
FOR EACH ROW
BEGIN
	DELETE FROM notatki WHERE n_id=OLD.p_id;
END$$
DELIMITER ;

###############
#usuwanie zwierzat po usunieciu wlasciciela

DROP TRIGGER IF EXISTS usunZwierzetaWlasciciela;
DELIMITER $$
$$ 
CREATE TRIGGER usunZwierzetaWlasciciela AFTER DELETE ON wlasciciele
FOR EACH ROW
BEGIN
	DELETE FROM pacjenci WHERE p_id=OLD.w_id;
END$$
DELIMITER ;


##########################################3
#sprawdzanie czy nie ma takiej rasy już
DROP TRIGGER IF EXISTS sprawdzRasy;
DELIMITER $$
$$ 
CREATE TRIGGER sprawdzRasy BEFORE INSERT ON rasy
FOR EACH ROW
BEGIN
	IF NEW.gatunek IN (SELECT gatunek FROM rasy) AND NEW.rasa IN (SELECT rasa FROM rasy) THEN
		SIGNAL SQLSTATE '45000' 
		SET MESSAGE_TEXT = 'Rasa istnieje juz w bazie';
	END IF;
END$$
DELIMITER ;



###############################
#tworzenie losowej bazy rekordów
DROP PROCEDURE IF EXISTS generateDatabase;
DECLARE @bCounter,@lekarz, @aCounter, @temp INT,@sDay datetime,
DELIMITER $$
CREATE PROCEDURE generateDataBase()
BEGIN 
	#rasy
    SET @bCounter =25;
    WHILE @bCounter!=0 DO
		INSERT INTO rasy (gatunek,rasa) VALUES (RANDSTRING(4+FLOOR(RAND()*10)),RANDSTRING(6+FLOOR(RAND()*6)));
        SET @bCounter = @bCounter-1;
	END WHILE;
    
    #lekarze
    SET @bCounter =3;
     WHILE @bCounter!=0 DO
		INSERT INTO pracownicy (imie,nazwisko,pesel,typ) VALUES (RANDSTRING(4+FLOOR(RAND()*10)),RANDSTRING(6+FLOOR(RAND()*6)),randomPesel(),'lekarz');
        SET @bCounter = @bCounter-1;
	END WHILE;
    
    #sekretariat
     SET @bCounter =2;
     WHILE @bCounter!=0 DO
		INSERT INTO pracownicy (imie,nazwisko,pesel,typ) VALUES (RANDSTRING(4+FLOOR(RAND()*10)),RANDSTRING(6+FLOOR(RAND()*6)),randomPesel(),'sekretariat');
        SET @bCounter = @bCounter-1;
	END WHILE;
    
    #wlasciciele
     SET @bCounter =40;
     WHILE @bCounter!=0 DO
		INSERT INTO wlasciciele (imie,nazwisko,pesel,ulica,nr_domu,nr_mieszkania, kod_pocztowy) 
			VALUES (RANDSTRING(4+FLOOR(RAND()*10)),RANDSTRING(6+FLOOR(RAND()*6)),randomPesel(),RANDSTRING(4+FLOOR(RAND()*10)),FLOOR(rand()*120)+1,FLOOR(rand()*20)+1,CONCAT(10+FLOOR(RAND()*89),'-',100+FLOOR(RAND()*899)));
        SET @bCounter = @bCounter-1;
	END WHILE;
   
    
    #pacjenci
    SET @bCounter =120;
     WHILE @bCounter!=0 DO
		INSERT INTO pacjenci (nazwa, id_wlasciciela, id_rasy, rok_urodzenia, umaszczenie) 
			VALUES (RANDSTRING(2+FLOOR(RAND()*12)),(SELECT w_id FROM wlasciciele ORDER BY rand() LIMIT 1),(SELECT r_id FROM rasy ORDER BY rand() LIMIT 1),FLOOR(1990+rand()*25),(RANDSTRING(4+FLOOR(RAND()*10))));
        SET @bCounter = @bCounter-1;
	END WHILE;
    
    #terminy
    SET @bCounter =60;
    SET @aCounter =12;
		
	Insert INTO terminy (dzien,godzina,id_lekarza) VALUES (date(curdate()),'0:01',(SELECT staff_id FROM pracownicy WHERE typ = 'lekarz' ORDER BY RAND() LIMIT 1));
	SET @sDay = CONCAT(DATE_ADD((SELECT MAX(dzien) FROM terminy), INTERVAL 1 DAY), ' 7:30');
	DELETE FROM terminy WHERE godzina='0:01' AND dzien= date(curdate());
    
	SET @lekarz = (SELECT staff_id FROM pracownicy WHERE typ = 'lekarz' ORDER BY rand() LIMIT 1);
	WHILE (@bCounter!=0) DO
		IF @aCounter=0 THEN
			SET @aCounter=12;
            SET @bCounter = @bCounter-1;
            SET @lekarz = (SELECT staff_id FROM pracownicy WHERE typ = 'lekarz' ORDER BY rand() LIMIT 1);
        END IF;
			
	
		INSERT INTO terminy (dzien,godzina, id_lekarza) 
			VALUES (DATE_ADD(@sDay, INTERVAL @bCounter DAY),DATE_ADD(time(@sDay), INTERVAL @aCounter*30 MINUTE),@lekarz);
		SET @aCounter = @aCounter-1;
        
	END WHILE;
    
    #wizyty
     SET @bCounter =100;
     WHILE @bCounter!=0 DO
		DROP TABLE IF EXISTS view1;
		CREATE TABLE view1 AS (SELECT id_terminu FROM wizyty);
		INSERT INTO wizyty (id_pacjenta, id_terminu)
			VALUES ((SELECT p_id FROM pacjenci ORDER BY rand() LIMIT 1),(SELECT g_id FROM terminy WHERE g_id NOT IN (SELECT * FROM view1) ORDER BY rand() LIMIT 1));
        SET @bCounter = @bCounter-1;
        
	END WHILE;
    DROP TABLE IF EXISTS wizyty_view;
    
    #notatki
    SET @bCounter =60;
     WHILE @bCounter!=0 DO
     SET @aCounter = (SELECT w_id FROM wizyty ORDER BY rand() LIMIT 1);
		INSERT INTO notatki (id_pacjenta, id_wizyty, komentarz)
			VALUES ((SELECT id_pacjenta FROM wizyty WHERE w_id=@aCounter),@aCounter,RANDSTRING(50+FLOOR(RAND()*50)));
        SET @bCounter = @bCounter-1;
	END WHILE;
    
    INSERT INTO pracownicy (imie,nazwisko,pesel,typ) VALUES ('ma','le',98092805975,'lekarz');
    INSERT INTO pracownicy (imie,nazwisko,pesel,typ) VALUES ('ma','le',98092805975,'sekretariat');
    INSERT INTO wlasciciele (imie,nazwisko,pesel,ulica,nr_domu,nr_mieszkania,kod_pocztowy) VALUES ('ma','le','98092805975','asd','23','4','55-555');
	INSERT INTO pracownicy (imie,nazwisko,pesel,typ) VALUES ('ma','le',98092805975,'admin');
    
END;$$
DELIMITER ;

#INSERT INTO pracownicy (imie,nazwisko,pesel,typ) VALUES ('Maciej','Lewandowicz',98092805975,'admin');
CALL generateDataBase();



#CREATE USER 'log@localhost';
#SET PASSWORD FOR 'log@localhost' = 'pas';
#GRANT Select ON klinika.uzytkownicy TO 'log@localhost';
#FLUSH PRIVILEGES;

#CREATE USER 'wlasc@localhost';
#SET PASSWORD FOR 'log@localhost' = 'w3pa2kvi3';
#GRANT Select ON klinika.uzytkownicy TO 'log@localhost';
#FLUSH PRIVILEGES;

#CREATE USER 'lek@localhost';
#SET PASSWORD FOR 'log@localhost' = 'l3efj29chj';
#GRANT Select ON klinika.uzytkownicy TO 'log@localhost';
#FLUSH PRIVILEGES;

#CREATE USER 'sek@localhost';
#SET PASSWORD FOR 'log@localhost' = 's4f52vserg';
#GRANT Select ON klinika.uzytkownicy TO 'log@localhost';
#FLUSH PRIVILEGES;

#CREATE USER 'adm@localhost';
#SET PASSWORD FOR 'log@localhost' = 'a4vdmq9diw';
#GRANT Select ON klinika.uzytkownicy TO 'log@localhost';
#FLUSH PRIVILEGES;


#INSERT INTO wlasciciele (imie,nazwisko,pesel,ulica,nr_domu,nr_mieszkania,kod_pocztowy) VALUES ("s","t","78110166134","asd","123","4","55-555");
#INSERT INTO pracownicy (imie,nazwisko,pesel,typ) VALUES ('pracownik','miesiaca','98092805975','lekarz');
#SELECT * FROM pracownicy;
#Select * from wlasciciele;
#Select * from uzytkownicy;
#SELECT * FROM notatki;

