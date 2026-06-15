import { useCallback, useEffect, useState } from "react";
import { toast } from "react-toastify";
import { routesApi } from "../../api/routesApi";
import RouteBlock from "../../components/RouteBlock/RouteBlock";
import { Route } from "../../types/route";
import styles from "./RouteSearchPage.module.scss";
import { getWalletInfo } from "../../api/depositApi";
import { useLanguage } from "../../context/LanguageContext";

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

const RouteSearchPage = () => {
  const { t } = useLanguage();
  const [citySelection, setCitySelection] = useState<CitySelection>({
    origin: "",
    destination: "",
  });
  const [routes, setRoutes] = useState<Route[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [hasSearched, setHasSearched] = useState(false);
  const [availablePoints, setAvailablePoints] = useState<number | null>(null);

  const refreshPoints = useCallback(async () => {
    try {
      const wallet = await getWalletInfo();
      setAvailablePoints(Number(wallet.points ?? 0));
    } catch {
      setAvailablePoints(0);
    }
  }, []);

  useEffect(() => {
    void refreshPoints();
  }, [refreshPoints]);

  const handleCityChange = (field: "origin" | "destination", value: string) => {
    setCitySelection((prev) => ({
      ...prev,
      [field]: value,
    }));
  };

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!citySelection.origin || !citySelection.destination) {
      toast.error(t("search.selectBoth"));
      return;
    }

    if (citySelection.origin === citySelection.destination) {
      toast.error(t("search.sameCity"));
      return;
    }

    setIsLoading(true);
    setHasSearched(true);
    setRoutes([]);

    try {
      const data = await routesApi.getRoutes(
        citySelection.origin,
        citySelection.destination,
      );

      setRoutes(data);

      if (data.length === 0) {
        toast.info(t("search.empty"));
      }
    } catch (error) {
      toast.error(t("search.loadError"));
      console.error("Error fetching routes:", error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className={styles["route-selector"]}>
      <h2>{t("search.title")}</h2>

      <form onSubmit={handleSearch} className={styles["search-form"]}>
        <div className={styles["form-group"]}>
          <label htmlFor="origin-city">{t("search.origin")}:</label>
          <select
            id="origin-city"
            value={citySelection.origin}
            onChange={(e) => handleCityChange("origin", e.target.value)}
            required
          >
            <option value="">{t("search.selectCity")}</option>
            {CITIES.map((city) => (
              <option key={city} value={city}>
                {city}
              </option>
            ))}
          </select>
        </div>

        <div className={styles["form-group"]}>
          <label htmlFor="destination-city">{t("search.destination")}:</label>
          <select
            id="destination-city"
            value={citySelection.destination}
            onChange={(e) => handleCityChange("destination", e.target.value)}
            required
          >
            <option value="">{t("search.selectCity")}</option>
            {CITIES.map((city) => (
              <option key={city} value={city}>
                {city}
              </option>
            ))}
          </select>
        </div>

        <button type="submit" disabled={isLoading}>
          {isLoading ? t("search.searching") : t("search.search")}
        </button>
      </form>

      {hasSearched && (
        <div className={styles["routes-container"]}>
          {isLoading ? (
            <p className={styles.loading}>{t("search.loading")}</p>
          ) : routes.length > 0 ? (
            <div className={styles["routes-list"]}>
              <h3>
                {t("search.available")} ({routes.length})
              </h3>
              {routes.map((route) => (
                <RouteBlock
                  key={route.id}
                  route={route}
                  availablePoints={availablePoints}
                  onReservationSuccess={refreshPoints}
                />
              ))}
            </div>
          ) : (
            <p className={styles["no-routes"]}>{t("search.empty")}</p>
          )}
        </div>
      )}
    </div>
  );
};

export default RouteSearchPage;
