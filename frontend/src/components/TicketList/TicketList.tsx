import { useState } from "react";
import TicketCard, { TicketCardProps } from "./TicketCard";
import styles from "./TicketList.module.scss";

const MOCK_DATA: Omit<TicketCardProps, 'onCancel'>[] = [
    { id: 1, origin: "Kraków", destination: "Katowice", departureTime: "2024-06-20T15:00:00", seatCount: 2, totalPrice: 30.0, status: 'confirmed' },
    { id: 2, origin: "Katowice", destination: "Kraków", departureTime: "2024-04-10T10:00:00", seatCount: 1, totalPrice: 15.0, status: 'completed' }
];

const TicketList = () => {
    const [reservations, setReservations] = useState(MOCK_DATA);

    const handleCancel = (id: number) => {
        setReservations(prev =>
            prev.map(ticket =>
                ticket.id === id ? { ...ticket, status: 'cancelled' } : ticket
            )
        );
        console.log(`Anulowano rezerwację o ID: ${id}`);
    };

    return (
        <div className={styles["ticket-list-wrapper"]}>
            <div className={styles["items-grid"]}>
                {reservations.length > 0 ? (
                    reservations.map((ticket) => (
                        <TicketCard
                            key={ticket.id}
                            {...ticket}
                            onCancel={handleCancel}
                        />
                    ))
                ) : (
                    <p className={styles["empty-msg"]}>Brak rezerwacji do wyświetlenia.</p>
                )}
            </div>
        </div>
    );
};

export default TicketList;