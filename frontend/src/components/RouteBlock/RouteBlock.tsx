import { useContext, useState } from "react";
import { AuthContext } from "../../context/AuthContext";
import { Route } from "../../types/route";
import { routesApi } from "../../api/routesApi";
import { toast } from "react-toastify";
import styles from "./RouteBlock.module.scss";

interface RouteBlockProps {
  route: Route;
  onReservationSuccess?: () => void;
}

const RouteBlock = ({ route, onReservationSuccess }: RouteBlockProps) => {
  const { isAuthenticated } = useContext(AuthContext);
  const [isLoading, setIsLoading] = useState(false);
  const [seats, setSeats] = useState(1);

  const handleReservation = async () => {
    if (!isAuthenticated) {
      toast.error("Musisz być zalogowany, aby zarezerwować trasę!");
      return;
    }

    setIsLoading(true);
    try {
      await routesApi.createReservation({
        routeId: route.id,
        seats,
      });
      toast.success("✅ Rezerwacja dodana!");
      if (onReservationSuccess) {
        onReservationSuccess();
      }
      setSeats(1);
    } catch (error: any) {
      if (error.response?.status === 400) {
        toast.error(
          error.response.data?.message || "Błąd walidacji rezerwacji.",
        );
      } else {
        toast.error("Błąd podczas tworzenia rezerwacji.");
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className={styles["route-block"]}>
      <div className={styles["route-header"]}>
        <h3>
          {route.origin} → {route.destination}
        </h3>
      </div>

      <div className={styles["route-details"]}>
        <div className={styles["detail-item"]}>
          <span className={styles["label"]}>Odjazd:</span>
          <span>{route.departureTime}</span>
        </div>
        <div className={styles["detail-item"]}>
          <span className={styles["label"]}>Przyjazd:</span>
          <span>{route.arrivalTime}</span>
        </div>
        <div className={styles["detail-item"]}>
          <span className={styles["label"]}>Cena:</span>
          <span className={styles["price"]}>PLN {route.price.toFixed(2)}</span>
        </div>
      </div>

      <div className={styles["reservation-section"]}>
        <div className={styles["seats-input"]}>
          <label htmlFor={`seats-${route.id}`}>Liczba miejsc:</label>
          <input
            id={`seats-${route.id}`}
            type="number"
            min="1"
            value={seats}
            onChange={(e) =>
              setSeats(Math.max(1, parseInt(e.target.value) || 1))
            }
            disabled={!isAuthenticated || isLoading}
          />
        </div>
        <button
          className={styles["reserve-button"]}
          onClick={handleReservation}
          disabled={!isAuthenticated || isLoading}
          title={!isAuthenticated ? "Zaloguj się, aby zarezerwować" : ""}
        >
          {isLoading ? "Rezerwuję..." : "Zarezerwuj"}
        </button>
      </div>
    </div>
  );
};

export default RouteBlock;
