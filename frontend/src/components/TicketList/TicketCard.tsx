import { useLanguage } from "../../context/LanguageContext";
import styles from "./TicketList.module.scss";

export interface TicketCardProps {
    id: number;
    origin: string;
    destination: string;
    departureTime: string;
    seatCount: number;
    totalPrice: number;
    status: "nowy" | "zakupiony" | "anulowany" | "ukończony";
    onCancel: (id: number) => void;
    isCancelling?: boolean;
}

const TicketCard = ({ id, origin, destination, departureTime, seatCount, totalPrice, status, onCancel, isCancelling = false }: TicketCardProps) => {
    const { locale, t } = useLanguage();
    const departureDate = new Date(departureTime);
    const now = new Date();
    const canCancel = status === "zakupiony" &&
        (departureDate.getTime() - now.getTime()) > 24 * 60 * 60 * 1000;
    const statusLabel = status === "zakupiony"
        ? t("tickets.statusPurchased")
        : status === "ukończony"
            ? t("tickets.statusCompleted")
            : status.toUpperCase();

    return (
        <div className={`${styles["ticket-card"]} ${styles[status]}`}>
            <div className={styles["ticket-header"]}>
                <span className={styles["route"]}>
                    <strong>{origin}</strong> {"->"} <strong>{destination}</strong>
                </span>
                <span className={`${styles["status-badge"]} ${styles[status]}`}>
                    {statusLabel}
                </span>
            </div>

            <div className={styles["ticket-body"]}>
                <div className={styles["info-row"]}>
                    <span><strong>{t("tickets.date")}:</strong></span>
                    <span>{departureDate.toLocaleString(locale)}</span>
                </div>
                <div className={styles["info-row"]}>
                    <span><strong>{t("tickets.seats")}:</strong></span>
                    <span>{seatCount}</span>
                </div>
                <div className={styles["info-row"]}>
                    <span><strong>{t("tickets.total")}:</strong></span>
                    <span className={styles["price"]}>{totalPrice.toFixed(2)} PLN</span>
                </div>
            </div>

            {canCancel && (
                <button
                    onClick={() => onCancel(id)}
                    className={styles["cancel-btn"]}
                    title={t("tickets.cancelTitle")}
                    disabled={isCancelling}
                >
                    {isCancelling ? t("tickets.cancelling") : t("tickets.cancel")}
                </button>
            )}

            <div className={styles["perforation"]}></div>
        </div>
    );
};

export default TicketCard;
