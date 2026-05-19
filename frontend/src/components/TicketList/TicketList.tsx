import TicketCard from "./TicketCard";
import styles from "./TicketList.module.scss";
import { toast } from "react-toastify";

const MOCK_RESERVATIONS: any[] = [
    { id: 101, origin: "Kraków", destination: "Katowice", departureTime: "2026-06-20T08:00:00", seatCount: 2, totalPrice: 30.00, status: "zakupiony" },
    { id: 105, origin: "Katowice", destination: "Kraków", departureTime: "2026-06-22T16:30:00", seatCount: 1, totalPrice: 15.00, status: "zakupiony" },
    { id: 109, origin: "Kraków", destination: "Katowice", departureTime: "2026-05-10T12:00:00", seatCount: 1, totalPrice: 15.00, status: "ukończony" }
];

const TicketList = () => {
    const handleCancelRequest = (id: number) => {
        // Symulacja akcji anulowania zgodnie z transactionform.txt [5]
        toast.info(`Przetwarzanie anulowania rezerwacji nr ${id}...`);
        console.log("Anulowano bilet o ID:", id);
    };

    return (
        <div className={styles["tickets-grid"]}>
            {MOCK_RESERVATIONS.map(res => (
                <TicketCard 
                    key={res.id} 
                    {...res} 
                    onCancel={handleCancelRequest} 
                />
            ))}
        </div>
    );
};

export default TicketList;