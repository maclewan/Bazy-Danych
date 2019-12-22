CREATE DATABASE klinika;
USE klinika;
#DROP DATABASE klinika;


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
PRIMARY KEY (w_id)
);

ALTER TABLE wlasciciele auto_increment = 1000;

CREATE TABLE pracownicy
(
staff_id INT NOT NULL AUTO_INCREMENT,
imie VARCHAR(45) NOT NULL,
nazwisko VARCHAR(45) NOT NULL,
pesel CHAR(11) NOT NULL,
typ ENUM('lekarz','sekretariat'),
PRIMARY KEY (staff_id)
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
FOREIGN KEY (id_pacjenta) REFERENCES pacjenci(p_id),
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


CREATE TABLE uzytkownicy 
(
id_u INT NOT NULL,
login VARCHAR(25) NOT NULL,
haslo VARCHAR(25) NOT NULL,
PRIMARY KEY (id_u),
FOREIGN KEY (id_u) REFERENCES wlasciciele(w_id),
FOREIGN KEY (id_u) REFERENCES pracownicy(staff_id)
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


#dodawanie triggerów

DROP TRIGGER IF EXISTS dodajPracownika;
DELIMITER $$
$$ 
CREATE TRIGGER dodajPracownika BEFORE INSERT ON pracownicy
FOR EACH ROW
BEGIN
	#Sprawdzanie peselu
	SET NEW.staff_id = (SELECT DISTINCT MAX(staff_id) FROM pracownicy)+1;
    IF NEW.typ='lekarz' THEN
		INSERT INTO uzytkownicy (id_u,login,haslo) VALUES(NEW.staff_id, CONCAT(NEW.pesel,'l'),CONCAT(NEW.imie,NEW.nazwisko));
	ELSE
		INSERT INTO uzytkownicy (id_u,login,haslo) VALUES(NEW.staff_id, CONCAT(NEW.pesel,'s'),CONCAT(NEW.imie,NEW.nazwisko));
	END IF;

END$$
DELIMITER ;

DROP TRIGGER IF EXISTS dodajWlasciciela;
DELIMITER $$
$$ 
CREATE TRIGGER dodajWlasciciela BEFORE INSERT ON wlasciciele
FOR EACH ROW
BEGIN
	#Sprawdzanie peselu
	SET NEW.w_id = (SELECT DISTINCT MAX(w_id) FROM wlasciciele)+1;
    
	INSERT INTO uzytkownicy (id_u,login,haslo) VALUES(NEW.w_id, CONCAT(NEW.pesel,'w'),CONCAT(NEW.imie,NEW.nazwisko));
	
END$$
DELIMITER ;


