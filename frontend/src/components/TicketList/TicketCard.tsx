
import styles from "./TicketList.module.scss";

export interface TicketCardProps {
    id: number;
    origin: string;
    destination: string;
    departureTime: string;
    seatCount: number;
    totalPrice: number;
    status: 'new' | 'confirmed' | 'cancelled' | 'completed';
    onCancel: (id: number) => void;
}

const TicketCard = ({ id, origin, destination, departureTime, seatCount, totalPrice, status, onCancel }: TicketCardProps) => {
    const departureDate = new Date(departureTime);

    const canCancel = status === 'confirmed' &&
        (departureDate.getTime() - new Date().getTime()) > 24 * 60 * 60 * 1000;

    return (
        <div className={`${styles["ticket-card"]} ${styles[status]}`}>
            <div className={styles["ticket-header"]}>
                <span className={styles["route"]}>{origin} ➔ {destination}</span>
                <span className={styles["status-badge"]}>{status}</span>
            </div>
            <div className={styles["ticket-info"]}>
                <p>Data: {departureDate.toLocaleString()}</p>
                <p>Miejsca: {seatCount}</p>
                <p>Suma: {totalPrice.toFixed(2)} PLN</p>
            </div>
            {canCancel && (
                <button onClick={() => onCancel(id)} className={styles["cancel-btn"]}>
                    Anuluj rezerwację
                </button>
            )}
        </div>
    );
};

export default TicketCard;