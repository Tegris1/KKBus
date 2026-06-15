import { useContext, useState } from "react";
import { toast } from "react-toastify";
import { routesApi } from "../../api/routesApi";
import { AuthContext } from "../../context/AuthContext";
import { useLanguage } from "../../context/LanguageContext";
import { Route } from "../../types/route";
import styles from "./RouteBlock.module.scss";

interface RouteBlockProps {
  route: Route;
  availablePoints: number | null;
  onReservationSuccess?: () => Promise<void> | void;
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

const getReservationValidationMessage = (error: unknown) => {
  const response = (error as ApiErrorResponse).response;

  if (response?.status !== 400) {
    return null;
  }

  return response.data?.message || response.data?.error || null;
};

const RouteBlock = ({
  route,
  availablePoints,
  onReservationSuccess,
}: RouteBlockProps) => {
  const { isAuthenticated } = useContext(AuthContext);
  const { locale, t } = useLanguage();
  const [isLoading, setIsLoading] = useState(false);
  const [seats, setSeats] = useState(1);
  const [usePointsDiscount, setUsePointsDiscount] = useState(false);
  const price = Number(route.price ?? 0);
  const regularTotal = price * seats;
  const discountAmount = usePointsDiscount ? Math.min(10, regularTotal) : 0;
  const finalTotal = regularTotal - discountAmount;
  const canUseDiscount = availablePoints !== null && availablePoints >= 50;
  const hasDeparted = new Date(route.departureTime).getTime() <= Date.now();
  const intermediateStops = route.intermediateStops.filter(Boolean);
  const routePath = [route.origin, ...intermediateStops, route.destination];

  const formatDateTime = (value: string) => {
    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
      return value;
    }

    return new Intl.DateTimeFormat(locale, {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    }).format(date);
  };

  const handleReservation = async () => {
    if (hasDeparted) {
      toast.error(t("route.pastError"));
      return;
    }

    if (!isAuthenticated) {
      toast.error(t("route.loginRequired"));
      return;
    }

    setIsLoading(true);
    try {
      const reservation = await routesApi.createReservation({
        routeId: route.id,
        seats,
        travelDepartureTime: route.departureTime,
        usePointsDiscount,
      });
      const discountMessage = reservation.pointsSpent
        ? t("route.discountUsed", {
            points: reservation.pointsSpent,
            amount: Number(reservation.discountAmount ?? 0).toFixed(2),
          })
        : "";
      toast.success(
        `${t("route.bookingSuccess", {
          points: reservation.awardedPoints ?? 0,
        })}${discountMessage}`,
      );
      await onReservationSuccess?.();
      setSeats(1);
      setUsePointsDiscount(false);
    } catch (error: unknown) {
      toast.error(getReservationValidationMessage(error) ?? t("route.bookingError"));
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className={styles["route-block"]}>
      <div className={styles["route-header"]}>
        <h3>{routePath.join(" -> ")}</h3>
        <span>{t("route.weekly")}</span>
      </div>

      <div className={styles["route-details"]}>
        <div className={styles["detail-item"]}>
          <span className={styles.label}>{t("route.departure")}:</span>
          <span>{formatDateTime(route.departureTime)}</span>
        </div>
        <div className={styles["detail-item"]}>
          <span className={styles.label}>{t("route.arrival")}:</span>
          <span>{formatDateTime(route.arrivalTime)}</span>
        </div>
        <div className={styles["detail-item"]}>
          <span className={styles.label}>{t("route.price")}:</span>
          <span className={styles.price}>PLN {price.toFixed(2)}</span>
        </div>
      </div>

      {intermediateStops.length > 0 && (
        <div className={styles["intermediate-stops"]}>
          <span className={styles["stops-label"]}>{t("route.stops")}:</span>
          <ol>
            {intermediateStops.map((stop, index) => (
              <li key={`${route.id}-stop-${index}`}>{stop}</li>
            ))}
          </ol>
        </div>
      )}

      <div className={styles["reservation-section"]}>
        <div className={styles["seats-input"]}>
          <label htmlFor={`seats-${route.id}`}>{t("route.seats")}:</label>
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
        <div className={styles["discount-control"]}>
          <button
            type="button"
            className={`${styles["discount-button"]} ${
              usePointsDiscount ? styles.active : ""
            }`}
            disabled={!canUseDiscount || isLoading || hasDeparted}
            onClick={() => setUsePointsDiscount((current) => !current)}
          >
            {usePointsDiscount ? t("route.removeDiscount") : t("route.useDiscount")}
          </button>
          <small>
            {availablePoints === null
              ? t("route.loadingPoints")
              : t("route.availablePoints", { points: availablePoints })}
          </small>
        </div>
        <div className={styles["price-summary"]}>
          {usePointsDiscount && (
            <span className={styles["regular-price"]}>
              {regularTotal.toFixed(2)} PLN
            </span>
          )}
          <strong>{finalTotal.toFixed(2)} PLN</strong>
        </div>
        <button
          className={styles["reserve-button"]}
          onClick={handleReservation}
          disabled={!isAuthenticated || isLoading || hasDeparted}
          title={
            hasDeparted
              ? t("route.finished")
              : !isAuthenticated
                ? t("route.loginRequired")
                : ""
          }
        >
          {hasDeparted
            ? t("route.finished")
            : isLoading
              ? t("route.booking")
              : t("route.book")}
        </button>
      </div>
    </div>
  );
};

export default RouteBlock;
