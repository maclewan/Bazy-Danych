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


