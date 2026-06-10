# Informacje o testach i metodologia zarzadzania projektem

## Testy automatyczne

Projekt posiada testy automatyczne backendu w katalogu:

```text
backend/backend/src/test/java
```

Najwazniejsze testowane obszary:

- rezerwacje,
- anulowanie rezerwacji,
- blokada rezerwowania po anulowaniach,
- blokada zakupu ponad pojemnosc autobusu,
- tygodniowa powtarzalnosc kursow,
- tworzenie i aktualizacja tras,
- raporty,
- program lojalnosciowy,
- bezpieczenstwo dostepu do endpointow rezerwacji,
- inicjalizacja brakujacych przypisan kierowcy i autobusu dla tras.

Lista istotnych klas testowych:

- `ReservationServiceTest`
- `RouteServiceTest`
- `WeeklyRouteServiceTest`
- `ReportServiceTest`
- `LoyaltyServiceTest`
- `ReservationControllerSecurityTest`
- `LegacyRouteAssignmentInitializerTest`
- `ScheduleControllerTest`

## Uruchamianie testow backendu

W katalogu backendu:

```bash
cd backend/backend
./mvnw test
```

Mozna uruchomic tez wybrany zestaw testow:

```bash
./mvnw test "-Dtest=ReservationServiceTest,RouteServiceTest,ReservationControllerSecurityTest"
```

Podczas prac nad projektem uruchamiane byly m.in. testy:

```bash
./mvnw test "-Dtest=ReservationServiceTest,RouteServiceTest"
./mvnw test "-Dtest=ReservationServiceTest,ReservationControllerSecurityTest"
./mvnw test "-Dtest=RouteServiceTest,ReservationServiceTest,ReservationControllerSecurityTest"
```

Wynik: testy przechodzily poprawnie.

## Testy frontendu i walidacja kodu

Frontend nie posiada osobnych testow jednostkowych, ale byl regularnie sprawdzany przez:

```bash
npm run build
npm run lint
```

`npm run build` sprawdza kompilacje TypeScript oraz budowanie aplikacji Vite.

`npm run lint` sprawdza kod frontendu przez ESLint.

Podczas prac komendy przechodzily poprawnie. W buildzie pojawia sie znane ostrzezenie o zasobie:

```text
../../assets/login-bus-bg.jpg referenced in ../../assets/login-bus-bg.jpg didn't resolve at build time
```

Ostrzezenie nie blokuje budowania aplikacji.

## Testy manualne

Poza testami automatycznymi projekt byl sprawdzany manualnie przez wykonywanie glownych scenariuszy:

1. Rejestracja i logowanie uzytkownika.
2. Wyszukiwanie trasy.
3. Rezerwacja biletu.
4. Zmiana przystanku koncowego i sprawdzenie aktualizacji ceny.
5. Uzycie ulgi uczniowskiej/studenckiej.
6. Uzycie znizki punktowej.
7. Proba zakupu ponad liczbe wolnych miejsc.
8. Anulowanie rezerwacji.
9. Sprawdzenie blokady po trzech anulowaniach.
10. Generowanie raportow.
11. Drukowanie raportow.
12. Edycja istniejacej trasy przez admina/sekretarke.
13. Sprawdzenie listy pasazerow przez kierowce.
14. Drukowanie listy pasazerow.
15. Przelaczanie jezyka polski/angielski.

## Zakres pokrycia wymagan testami

Najlepiej pokryte testami sa funkcje backendowe o najwyzszym ryzyku:

- logika rezerwacji,
- limity czasowe,
- limity miejsc,
- punkty lojalnosciowe,
- raporty,
- trasy tygodniowe,
- role i dostep do wybranych endpointow.

Mniejszy poziom automatyzacji dotyczy frontendu. Frontend byl walidowany przez build, lint oraz testy manualne w przegladarce.

## Metodologia zarzadzania projektem

Projekt byl prowadzony w sposob iteracyjny, zblizony do lekkiego Scrum/Kanban.

Prace byly dzielone na male zadania funkcjonalne, np.:

- dodanie roli sekretarki,
- raporty sprzedazy,
- anulowanie rezerwacji,
- program lojalnosciowy,
- znizki za punkty,
- obsluga ulg,
- edycja tras,
- listy pasazerow dla kierowcy,
- tlumaczenia PL/EN,
- dane mockowe do prezentacji.

Kazde zadanie bylo realizowane w cyklu:

1. Analiza obecnego kodu.
2. Implementacja backendu, jezeli byla potrzebna.
3. Implementacja frontendu.
4. Dodanie lub aktualizacja testow.
5. Uruchomienie builda i lintowania.
6. Korekta bledow.

## Organizacja pracy

Zastosowano podejscie przyrostowe. Najpierw realizowano funkcje krytyczne dla dzialania systemu, a nastepnie dopracowywano widoki, tlumaczenia, walidacje i dane prezentacyjne.

Priorytetyzacja wygladala nastepujaco:

- najpierw funkcje wymagane przez specyfikacje,
- nastepnie naprawa bledow blokujacych prezentacje,
- potem poprawa UX i komunikatow,
- na koncu dokumentacja i stabilizacja.

## Kontrola jakosci

Kontrola jakosci obejmowala:

- testy jednostkowe i integracyjne backendu,
- kompilacje TypeScript,
- lintowanie frontendu,
- reczne testy scenariuszy uzytkownika,
- sprawdzanie uprawnien rol,
- sprawdzanie kontraktow API miedzy frontendem a backendem.

## Role w projekcie

W aplikacji zaimplementowano role zgodne z wymaganiami:

- `USER` - klient.
- `EMPLOYEE` - kierowca/pracownik.
- `SECRETARY` - sekretarka.
- `ADMIN` - administrator/wlasciciel.

Role byly wykorzystywane do ograniczania dostepu do funkcji administracyjnych, raportow, grafikow, list pasazerow i zarzadzania trasami.

## Ryzyka i ograniczenia

Najwazniejsze ryzyka:

- brak pelnych testow automatycznych frontendu,
- zaleznosc od lokalnej bazy MySQL,
- czesc danych prezentacyjnych jest generowana przez seedery,
- komunikaty backendowe sa glownie po polsku i bez osobnego systemu i18n.

Ograniczenia te nie blokuja prezentacji projektu, ale warto je uwzglednic przy dalszym rozwoju.

