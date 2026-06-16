# Kod zrodlowy programu

## Informacje ogolne

Projekt KKBus jest aplikacja webowa dla firmy transportowej. System obsluguje wyszukiwanie kursow, rezerwacje i anulowanie biletow, portfel klienta, program lojalnosciowy, role pracownikow, zarzadzanie trasami, raporty oraz listy pasazerow dla kierowcow.

Kod zrodlowy jest podzielony na dwie glowne czesci:

- `backend/backend` - aplikacja serwerowa Spring Boot.
- `frontend` - aplikacja kliencka React + TypeScript + Vite.

## Backend

Backend zostal napisany w Java 21 z uzyciem Spring Boot 3.5.6. Glowna odpowiedzialnosc backendu to udostepnianie REST API, autoryzacja uzytkownikow, obsluga logiki biznesowej i zapis danych w bazie MySQL.

Najwazniejsze technologie:

- Spring Web - kontrolery REST.
- Spring Data JPA - repozytoria i obsluga bazy danych.
- Spring Security - autoryzacja i zabezpieczenie endpointow.
- JWT - tokeny logowania.
- MySQL Connector - polaczenie z baza MySQL.
- MapStruct - mapowanie DTO i encji.
- JUnit, Mockito, Spring Security Test - testy automatyczne.

Glowna struktura backendu:

- `controllers` - warstwa API, np. `RouteController`, `ReservationController`, `UserController`, `ReportController`.
- `services` - logika biznesowa, np. `ReservationService`, `RouteService`, `ReportService`, `LoyaltyService`.
- `repositories` - dostep do danych przez Spring Data JPA.
- `model` - encje domenowe, np. `User`, `Route`, `Reservation`, `Vehicle`, `Wallet`, `Schedule`.
- `dtos` - obiekty przesylane przez API.
- `config` - konfiguracja aplikacji, seedery danych, zabezpieczenia.
- `security` - obsluga JWT i filtry autoryzacji.

Najwazniejsze funkcje backendu:

- Rejestracja i logowanie uzytkownika.
- Role: klient, kierowca/pracownik, sekretarka, administrator.
- Tworzenie, wyszukiwanie i edytowanie tras.
- Obsluga kursow powtarzalnych tygodniowo.
- Rezerwacja biletow z wyborem odcinka, ulgi i liczby miejsc.
- Blokada zakupu ponad pojemnosc autobusu.
- Anulowanie rezerwacji najpozniej 24h przed odjazdem.
- Blokada rezerwowania na miesiac po 3 anulowaniach.
- Portfel klienta i platnosc za bilet.
- Program lojalnosciowy i nagrody za punkty.
- Raporty sprzedazy dla sekretarki/admina.
- Lista pasazerow dla kierowcy z mozliwoscia wydruku.

## Frontend

Frontend zostal napisany w React 19 z TypeScriptem. Aplikacja korzysta z Vite, React Router, Axios, SCSS Modules i React Toastify.

Najwazniejsze technologie:

- React - komponenty UI.
- TypeScript - typowanie kodu.
- React Router - routing po stronach.
- Axios - komunikacja z backendem.
- SCSS Modules - lokalne style komponentow.
- React Toastify - komunikaty dla uzytkownika.
- ESLint - statyczna analiza kodu.

Glowna struktura frontendu:

- `api` - funkcje komunikacji z backendem, np. `routesApi`, `reservationsApi`, `reportsApi`.
- `components` - wspolne komponenty, np. `Navbar`, `RouteBlock`, `TicketList`.
- `context` - konteksty globalne, np. `AuthContext`, `LanguageContext`.
- `pages` - ekrany aplikacji, np. `RouteSearchPage`, `ReportsPage`, `RouteCreatePage`.
- `routes` - konfiguracja routingu aplikacji.
- `types` - typy TypeScript.
- `styles` - zmienne i style wspolne.

Najwazniejsze funkcje frontendu:

- Wyszukiwanie tras.
- Rezerwacja biletu.
- Wybieranie przystanku poczatkowego i koncowego.
- Dynamiczne przeliczanie ceny po zmianie odcinka, ulgi lub znizki punktowej.
- Podglad wolnych miejsc.
- Anulowanie rezerwacji.
- Portfel i historia transakcji.
- Program lojalnosciowy.
- Raporty i drukowanie raportow.
- Zarzadzanie trasami przez admina/sekretarke.
- Lista pasazerow dla kierowcy i drukowanie listy.
- Przelaczanie jezyka polski/angielski.

## Uruchamianie projektu

Backend:

```bash
cd backend/backend
./mvnw spring-boot:run
```

Frontend:

```bash
cd frontend
npm install
npm run dev
```

Domyslny port frontendu: `5174`.

Backend laczy sie z baza MySQL zgodnie z konfiguracja w `application.yaml`.

## Dane testowe i seedowane

Aplikacja zawiera seedery danych firmowych i mockowych.

Przykladowe konta firmowe maja haslo:

```text
Kkbus123!
```

Przykladowe konta mockowe maja haslo:

```text
Mock123!
```

Mock data jest wlaczane przez konfiguracje:

```yaml
app:
  mock-data:
    enabled: true
```

