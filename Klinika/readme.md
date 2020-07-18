## Table of contents
* [Technologies](#technologies)
* [General info](#general-info)
* [Application description](#Application-description)
* [Hibernate](#Hibernate)
	

## Technologies
<img src="https://hsto.org/webt/rg/a1/3b/rga13bp-mbl4ljkpbd-fuu6pzfw.png" alt="drawing" height=50px/>
<img src="https://vignette.wikia.nocookie.net/jfx/images/5/5a/JavaFXIsland600x300.png/revision/latest?cb=20070917150551" alt="drawing" height=50px/>
<img src="https://i0.wp.com/gluonhq.com/wp-content/uploads/2015/02/SceneBuilderLogo.png?fit=781%2C781&ssl=1" alt="drawing" height=50px/>
<img src="https://www.techcentral.ie/wp-content/uploads/2019/07/Java_jdk_logo_web-372x210.jpg" alt="drawing" height=50px/>
<img src="https://upload.wikimedia.org/wikipedia/en/thumb/6/62/MySQL.svg/1920px-MySQL.svg.png" alt="drawing" height=50px/> 
<img src="https://upload.wikimedia.org/wikipedia/commons/thumb/d/d5/IntelliJ_IDEA_Logo.svg/1024px-IntelliJ_IDEA_Logo.svg.png" alt="drawing" height=50px/> 



## General info
Projekt studencki na zajęcia z Baz Danych i Systemów Informacyjnych.
Tworzony przez [Maciej Lewandowicz](https://github.com/sasuke5055).

[Tresc zadania](https://cs.pwr.edu.pl/syga/arch/w2019/db/Lab_projekt.pdf)

## Application description
Aplikacja ma na celu ułatwić weterynarzom i właścicielom ich zwierzaków przechowywanie informacji o badaniach i 
zabiegach. Właściciele za pomocą aplikacji mogą wyszukiwać wolne terminy i zapisać/wypisywać się na wizyty. 
Lekarze mogą wprowadzać informacje o odbytych wizytach oraz przeprowadzone zabiegi do systemu, 
wpisywać dostępne godziny wizyt. Właściciele mogą te wyniki wyświetlać, porównywać z poprzednimi. 
System przewiduje stanowisko sekretarki, mającej możliwość rejestrowania pacjentów, umawiać im wizyty 
i wyświetlać historię chorób.

Schemat bazy danych:

![Schemat](images/diagram.png)

Do aplikacji nalezy zalogować się unikanlym loginem i hasłem. Hasła są przechowywane zaszyfrowane funkcją jednokierunkową sha1.

![](images/logowanie.png)

Panel główny różni się w zależności od typu użytkownika - właściciela, sekretariatu, lekarza i admina. 
Admin posiada dostęp do całej aplikacji, zatem zaprezentowany zostanie aplikacja z perspektywy admina.

![](images/w1.png)

W aplikacji użytkownik może przeglądać zwierzęta, edytować ich dane, usuwać je, dodawać nowe, czy umawiać im wizyty.
Podobnie można dodawać nowe gatunki i rasy.

![](images/w2.png)

Interesujące nas pozycje w aplikacji możemy wyszukiwać za pomocą ciągu znaków.


![](images/gif.gif)

Istnieje możliwosć wyszukania terminu i umówiania wówczas wizyty. 


![](images/w4.png)

Lekarz do wizyty ma możliwość dopisania notatki.

![](images/w5.png)

![](images/w55.png)

Notatka ta następnie pojawia się w szczegółach danego zwierzaka.

![](images/w555.png)

Z panelu wizyt można również dodać nowe terminy wizyt, z wyłączeniem tych już istniejących.

![](images/w6.png)

Z zakładki właściele można dodawać, edytować jak i usuwać właścicieli.

![](images/w7.png)

Podobnie dla pracowników, tutaj można również podać pełniony zawód. 
Z tej zakładki da się również wykonać backup i przywracanie zapisany stan bazy danych

![](images/w8.png)

## MySQL
Do projektu został napisany skrypt w dialekcie MySQL generujący bazę danych, konieczne triggery dla zachowania jej spójności,
indexy poprawiające wydajność bazy, utworzeni potrzebni użytkownicy oraz napisana funkcja generująca przykładowe dane 
losowe potrzebne, w celu prezentacji apliakcji oraz debugowania.
