import { useState } from "react";
import { routesApi } from "../../api/routesApi";
import { Route } from "../../types/route";
import RouteBlock from "../RouteBlock/RouteBlock";
import styles from "./TransactionForm.module.scss";
import { toast } from "react-toastify";

const CITIES = [
  "Warszawa",
  "Kraków",
  "Wrocław",
  "Poznań",
  "Gdańsk",
  "Szczecin",
  "Łódź",
  "Trójmiasto",
  "Bydgoszcz",
  "Katowice",
];

interface CitySelection {
  origin: string;
  destination: string;
}

const TransactionForm = () => {
  const [citySelection, setCitySelection] = useState<CitySelection>({
    origin: "",
    destination: "",
  });
  const [routes, setRoutes] = useState<Route[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [hasSearched, setHasSearched] = useState(false);

  const handleCityChange = (field: "origin" | "destination", value: string) => {
    setCitySelection((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!citySelection.origin || !citySelection.destination) {
      toast.error("Wybierz miasto wyjazdu i przyjazdu!");
      return;
    }

    if (citySelection.origin === citySelection.destination) {
      toast.error("Miasto wyjazdu i przyjazdu nie mogą być takie same!");
      return;
    }

    setIsLoading(true);
    setHasSearched(true);
    try {
      const data = await routesApi.getRoutes(
        citySelection.origin,
        citySelection.destination,
      );
      setRoutes(data);

      if (data.length === 0) {
        toast.info("Brak dostępnych tras dla wybranego połączenia.");
      }
    } catch (error: any) {
      toast.error("Błąd przy pobieraniu tras. Spróbuj ponownie.");
      console.error("Error fetching routes:", error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className={styles["route-selector"]}>
      <h2>Zarezerwuj miejsce w autobusie</h2>

      <form onSubmit={handleSearch} className={styles["search-form"]}>
        <div className={styles["form-group"]}>
          <label htmlFor="origin-city">Miasto wyjazdu:</label>
          <select
            id="origin-city"
            value={citySelection.origin}
            onChange={(e) => handleCityChange("origin", e.target.value)}
            required
          >
            <option value="">-- Wybierz miasto --</option>
            {CITIES.map((city) => (
              <option key={city} value={city}>
                {city}
              </option>
            ))}
          </select>
        </div>

        <div className={styles["form-group"]}>
          <label htmlFor="destination-city">Miasto przyjazdu:</label>
          <select
            id="destination-city"
            value={citySelection.destination}
            onChange={(e) => handleCityChange("destination", e.target.value)}
            required
          >
            <option value="">-- Wybierz miasto --</option>
            {CITIES.map((city) => (
              <option key={city} value={city}>
                {city}
              </option>
            ))}
          </select>
        </div>

        <button type="submit" disabled={isLoading}>
          {isLoading ? "Szukam tras..." : "Szukaj tras"}
        </button>
      </form>

      {hasSearched && (
        <div className={styles["routes-container"]}>
          {isLoading ? (
            <p className={styles["loading"]}>Ładowanie tras...</p>
          ) : routes.length > 0 ? (
            <div className={styles["routes-list"]}>
              <h3>Dostępne trasy ({routes.length})</h3>
              {routes.map((route) => (
                <RouteBlock
                  key={route.id}
                  route={route}
                  onReservationSuccess={() => {
                    toast.success("Rezerwacja udana!");
                  }}
                />
              ))}
            </div>
          ) : (
            <p className={styles["no-routes"]}>
              Brak dostępnych tras dla wybranego połączenia.
            </p>
          )}
        </div>
      )}
    </div>
  );
};

export default TransactionForm;
