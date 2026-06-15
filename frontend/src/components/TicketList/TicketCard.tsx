import styles from "./TicketList.module.scss";

export interface TicketCardProps {
    id: number;
    origin: string;
    destination: string;
    departureTime: string;
    seatCount: number;
    totalPrice: number;
    status: 'nowy' | 'zakupiony' | 'anulowany' | 'ukończony';
    onCancel: (id: number) => void;
    isCancelling?: boolean;
}

const TicketCard = ({ id, origin, destination, departureTime, seatCount, totalPrice, status, onCancel, isCancelling = false }: TicketCardProps) => {
    const departureDate = new Date(departureTime);
    const now = new Date();

    // Logika zgodna z UC8: Możliwość anulowania do 24 godzin przed wyjazdem [3, 4]
    const canCancel = status === 'zakupiony' &&
        (departureDate.getTime() - now.getTime()) > 24 * 60 * 60 * 1000;

    return (
        <div className={`${styles["ticket-card"]} ${styles[status]}`}>
            <div className={styles["ticket-header"]}>
                <span className={styles["route"]}>
                    <strong>{origin}</strong> ➔ <strong>{destination}</strong>
                </span>
                <span className={`${styles["status-badge"]} ${styles[status]}`}>
                    {status.toUpperCase()}
                </span>
            </div>

            <div className={styles["ticket-body"]}>
                <div className={styles["info-row"]}>
                    <span><strong>Data i godzina:</strong></span>
                    <span>{departureDate.toLocaleString('pl-PL')}</span>
                </div>
                <div className={styles["info-row"]}>
                    <span><strong>Liczba miejsc:</strong></span>
                    <span>{seatCount}</span>
                </div>
                <div className={styles["info-row"]}>
                    <span><strong>Suma do zapłaty:</strong></span>
                    <span className={styles["price"]}>{totalPrice.toFixed(2)} PLN</span>
                </div>
            </div>

            {canCancel && (
                <button 
                    onClick={() => onCancel(id)} 
                    className={styles["cancel-btn"]}
                    title="Anuluj rezerwację (możliwe do 24h przed odjazdem)"
                    disabled={isCancelling}
                >
                    {isCancelling ? "Anulowanie..." : "Anuluj rezerwację"}
                </button>
            )}
            
            {/* Dekoracyjna linia perforacji dla efektu biletu */}
            <div className={styles["perforation"]}></div>
        </div>
    );
};

export default TicketCard;
