show tables;
SELECT name, owner FROM pet ORDER BY owner asc;
SELECT name, birth FROM pet WHERE species='dog';
SELECT name, owner FROM pet WHERE birth LIKE '%-08-%' OR birth LIKE '%-09-%' OR birth LIKE '%-10-%' OR birth LIKE '%-11-%' OR birth LIKE '%-12-%';
SELECT DISTINCT species FROM pet WHERE sex='f';
SELECT name, date FROM event WHERE remark LIKE '%gave%' ; 
SELECT DISTINCT owner FROM pet WHERE name LIKE '%ffy';
SELECT name, owner FROM pet WHERE death IS NULL ORDER BY owner asc;
SELECT owner FROM pet GROUP BY owner HAVING COUNT(*)>1;
SELECT DISTINCT pet.owner, pet.name FROM pet INNER JOIN event ON pet.name=event.name WHERE event.type='birthday' ORDER BY pet.name DESC;
SELECT name FROM pet WHERE birth BETWEEN '1992-01-01' AND '1994-05-31';
SELECT name FROM pet ORDER BY birth LIMIT 2;
SELECT pet.name FROM (SELECT MAX(birth) AS birth FROM pet) AS A INNER JOIN pet ON A.birth = pet.birth;
SELECT pet.owner FROM event INNER JOIN pet ON event.name=pet.name WHERE event.date > (SELECT MIN(date) FROM event WHERE name='SLIM' AND type='vet');
SELECT owner FROM pet WHERE owner NOT IN (SELECT pet.owner FROM event INNER JOIN pet ON event.name=pet.name WHERE event.type='birthday') ORDER BY pet.birth;
SELECT T1.owner AS X, T2.owner AS Y, T1.species AS pet FROM pet AS T1 INNER JOIN pet AS T2 ON T1.species=T2.species AND T1.owner>T2.owner;
ALTER TABLE event ADD performer TEXT(40) AFTER date;
UPDATE (event JOIN pet On event.name=pet.name) SET performer = pet.owner WHERE type NOT IN ('vet','litter');
UPDATE (event JOIN pet On event.name=pet.name) SET performer = 'Kowalski&Nowak' WHERE type IN ('vet','litter');
UPDATE pet SET owner = 'Diane' WHERE species='cat';
SELECT species as 'Gatunek', COUNT(*) AS 'liczba' FROM pet GROUP BY species ORDER BY COUNT(*) DESC ;
DELETE FROM pet WHERE death IS NOT NULL;
ALTER TABLE pet DROP death;

INSERT INTO pet VALUES ('Kitty','Pat','cat','f','1999-01-04');
INSERT INTO event VALUES ('Kitty','1999-02-05','Pat','birthday','Gave him a new mouse');


SELECT * FROM event;
SELECT * FROM pet;

DELETE FROM event WHERE date IS NULL;
DELETE FROM event WHERE name = 'Kitty';

