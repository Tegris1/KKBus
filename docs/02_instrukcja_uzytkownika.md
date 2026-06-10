# Instrukcja uzytkownika

## Wejscie do aplikacji

Po uruchomieniu frontendu aplikacja jest dostepna w przegladarce pod adresem:

```text
http://localhost:5174
```

Na pasku nawigacji dostepne sa strony publiczne oraz strony zalezne od roli uzytkownika. Linki rolowe sa oddzielone separatorem.

## Rejestracja i logowanie

1. Wejdz w `Rejestracja`.
2. Podaj dane klienta: nazwe uzytkownika, imie, nazwisko, date urodzenia, telefon, email i haslo.
3. Opcjonalnie dolacz do programu lojalnosciowego.
4. Po rejestracji przejdz do `Logowanie`.
5. Zaloguj sie adresem email i haslem.

Po zalogowaniu aplikacja pokazuje dodatkowe funkcje zalezne od roli konta.

## Wyszukiwanie trasy

1. Wejdz w `Znajdz trase`.
2. Wybierz miasto wyjazdu i miasto przyjazdu.
3. Kliknij `Szukaj tras`.
4. System wyswietli dostepne kursy tygodniowe.

Przy kursie widoczne sa:

- odjazd,
- przyjazd,
- cena,
- przystanki posrednie,
- wolne miejsca,
- formularz rezerwacji.

## Rezerwacja biletu

1. Wybierz przystanek poczatkowy.
2. Wybierz przystanek koncowy.
3. Wybierz ulge: normalny, uczen/student, dziecko do lat 5.
4. Podaj liczbe miejsc.
5. Opcjonalnie uzyj znizki za punkty lojalnosciowe.
6. Kliknij `Zarezerwuj`.

Cena biletu aktualizuje sie automatycznie po zmianie:

- odcinka przejazdu,
- liczby miejsc,
- ulgi,
- znizki punktowej.

Nie mozna kupic wiecej miejsc niz wynosi liczba wolnych miejsc w autobusie.

## Anulowanie rezerwacji

1. Wejdz w `Bilety`.
2. Znajdz aktywna rezerwacje.
3. Kliknij `Anuluj rezerwacje`.

Rezerwacje mozna anulowac najpozniej 24 godziny przed odjazdem. Po trzech anulowaniach konto traci mozliwosc rezerwowania na miesiac.

## Portfel

1. Wejdz w `Portfel`.
2. Sprawdz saldo i punkty lojalnosciowe.
3. Wprowadz kwote doladowania.
4. Wybierz metode platnosci.
5. Kliknij przycisk doladowania.

Srodki z portfela sa uzywane do zakupu biletow.

## Program lojalnosciowy

1. Wejdz w `Nagrody`.
2. Sprawdz liczbe punktow.
3. Wybierz nagrode z katalogu.
4. Kliknij `Odbierz`.

Punkty sa naliczane na podstawie wartosci zakupionego biletu.

## Funkcje kierowcy

Kierowca po zalogowaniu widzi dodatkowe strony:

- `Grafik` - tygodniowy grafik pracy.
- `Lista pasazerow` - lista pasazerow z rezerwacjami na kursy danego kierowcy.

Na stronie listy pasazerow kierowca moze:

- sprawdzic kurs,
- sprawdzic autobus,
- sprawdzic liczbe zajetych miejsc,
- zobaczyc dane kontaktowe pasazerow,
- zobaczyc odcinek przejazdu i ulge,
- wydrukowac liste przyciskiem `Drukuj liste`.

Wydruk mozna zapisac jako PDF z poziomu okna drukowania przegladarki.

## Funkcje sekretarki

Sekretarka ma dostep do:

- raportow sprzedazy,
- zarzadzania trasami,
- rezerwacji miejsc dla klientow,
- przegladania danych potrzebnych do obslugi klientow.

Raporty mozna generowac dla okresu:

- dziennego,
- tygodniowego,
- miesiecznego,
- rocznego.

Raport mozna przefiltrowac po kierowcy i pojezdzie oraz wydrukowac lub zapisac do PDF.

## Funkcje administratora

Administrator ma dostep do funkcji sekretarki i kierowcy oraz do zarzadzania uzytkownikami i grafikami.

Administrator moze:

- nadawac role uzytkownikom,
- zarzadzac grafikami,
- tworzyc i edytowac trasy,
- korzystac z raportow.

## Zarzadzanie trasami

1. Wejdz w `Dodaj trase`.
2. Aby utworzyc nowa trase, wypelnij formularz i kliknij `Dodaj trase`.
3. Aby edytowac istniejaca trase, wybierz trase z listy pod formularzem i kliknij `Edytuj`.
4. Po zmianie danych kliknij `Zapisz zmiany`.

Dane trasy obejmuja:

- miasto wyjazdu,
- miasto przyjazdu,
- przystanki posrednie,
- czas odjazdu i przyjazdu,
- kierowce,
- numer pojazdu,
- koszt paliwa,
- cene.

## Zmiana jezyka

Na pasku nawigacji znajduje sie przelacznik `PL / EN`. Klikniecie zmienia jezyk interfejsu na polski lub angielski.

