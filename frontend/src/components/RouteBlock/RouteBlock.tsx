import { useContext, useState } from "react";
import { toast } from "react-toastify";
import { routesApi } from "../../api/routesApi";
import { AuthContext } from "../../context/AuthContext";
import { Route } from "../../types/route";
import styles from "./RouteBlock.module.scss";

interface RouteBlockProps {
  route: Route;
  onReservationSuccess?: () => void;
}

interface ApiErrorResponse {
  response?: {
    status?: number;
    data?: {
      message?: string;
      error?: string;
    };
  };
}

const formatDateTime = (value: string) => {
  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return new Intl.DateTimeFormat("pl-PL", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(date);
};

const getReservationValidationMessage = (error: unknown) => {
  const response = (error as ApiErrorResponse).response;

  if (response?.status !== 400) {
    return null;
  }

  return (
    response.data?.message ||
    response.data?.error ||
    "Blad walidacji rezerwacji."
  );
};

const RouteBlock = ({ route, onReservationSuccess }: RouteBlockProps) => {
  const { isAuthenticated } = useContext(AuthContext);
  const [isLoading, setIsLoading] = useState(false);
  const [seats, setSeats] = useState(1);
  const price = Number(route.price ?? 0);
  const hasDeparted = new Date(route.departureTime).getTime() <= Date.now();
  const intermediateStops = route.intermediateStops.filter(Boolean);
  const routePath = [route.origin, ...intermediateStops, route.destination];

  const handleReservation = async () => {
    if (hasDeparted) {
      toast.error("Nie można zarezerwować zakończonego kursu.");
      return;
    }

    if (!isAuthenticated) {
      toast.error("Musisz być zalogowany, aby zarezerwować trasę!");
      return;
    }

    setIsLoading(true);
    try {
      const reservation = await routesApi.createReservation({
        routeId: route.id,
        seats,
        travelDepartureTime: route.departureTime,
      });
      toast.success(
        `Rezerwacja dodana! Zdobyto ${reservation.awardedPoints ?? 0} pkt.`,
      );
      onReservationSuccess?.();
      setSeats(1);
    } catch (error: unknown) {
      const validationMessage = getReservationValidationMessage(error);

      if (validationMessage) {
        toast.error(validationMessage);
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
        <h3>{routePath.join(" -> ")}</h3>
        <span>kurs tygodniowy</span>
      </div>

      <div className={styles["route-details"]}>
        <div className={styles["detail-item"]}>
          <span className={styles.label}>Odjazd:</span>
          <span>{formatDateTime(route.departureTime)}</span>
        </div>
        <div className={styles["detail-item"]}>
          <span className={styles.label}>Przyjazd:</span>
          <span>{formatDateTime(route.arrivalTime)}</span>
        </div>
        <div className={styles["detail-item"]}>
          <span className={styles.label}>Cena:</span>
          <span className={styles.price}>PLN {price.toFixed(2)}</span>
        </div>
      </div>

      {intermediateStops.length > 0 && (
        <div className={styles["intermediate-stops"]}>
          <span className={styles["stops-label"]}>Przystanki posrednie:</span>
          <ol>
            {intermediateStops.map((stop, index) => (
              <li key={`${route.id}-stop-${index}`}>{stop}</li>
            ))}
          </ol>
        </div>
      )}

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
          disabled={!isAuthenticated || isLoading || hasDeparted}
          title={
            hasDeparted
              ? "Ten kurs już się odbył"
              : !isAuthenticated
                ? "Zaloguj się, aby zarezerwować"
                : ""
          }
        >
          {hasDeparted
            ? "Kurs zakończony"
            : isLoading
              ? "Rezerwuję..."
              : "Zarezerwuj"}
        </button>
      </div>
    </div>
  );
};

export default RouteBlock;
