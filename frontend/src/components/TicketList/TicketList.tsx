import { useEffect, useState } from "react";
import { reservationsApi, type Reservation } from "../../api/reservationsApi";
import TicketCard from "./TicketCard";
import styles from "./TicketList.module.scss";
import { toast } from "react-toastify";

interface ApiErrorResponse {
    response?: {
        data?: {
            message?: string;
            error?: string;
        };
    };
}

const TicketList = () => {
    const [reservations, setReservations] = useState<Reservation[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState("");
    const [cancellingId, setCancellingId] = useState<number | null>(null);

    useEffect(() => {
        const loadReservations = async () => {
            try {
                setReservations(await reservationsApi.getAll());
            } catch {
                setError("Nie udało się pobrać rezerwacji.");
            } finally {
                setIsLoading(false);
            }
        };

        void loadReservations();
    }, []);

    const handleCancelRequest = async (id: number) => {
        setCancellingId(id);

        try {
            await reservationsApi.cancel(id);
            setReservations((current) => current.filter((reservation) => reservation.id !== id));
            toast.success("Rezerwacja została anulowana, a środki zwrócone do portfela.");
        } catch (error: unknown) {
            const response = (error as ApiErrorResponse).response;
            toast.error(
                response?.data?.message ||
                response?.data?.error ||
                "Nie udało się anulować rezerwacji."
            );
        } finally {
            setCancellingId(null);
        }
    };

    if (isLoading) return <p>Ładowanie rezerwacji...</p>;
    if (error) return <p>{error}</p>;
    if (reservations.length === 0) return <p>Nie masz żadnych rezerwacji.</p>;

    return (
        <div className={styles["tickets-grid"]}>
            {reservations.map((reservation) => {
                const departureTime = reservation.departureTime;
                const status = new Date(departureTime).getTime() > Date.now()
                    ? "zakupiony"
                    : "ukończony";

                return (
                    <TicketCard
                        key={reservation.id}
                        id={reservation.id}
                        origin={reservation.origin}
                        destination={reservation.destination}
                        departureTime={departureTime}
                        seatCount={reservation.seats ?? 1}
                        totalPrice={Number(reservation.amount)}
                        status={status}
                        onCancel={handleCancelRequest}
                        isCancelling={cancellingId === reservation.id}
                    />
                );
            })}
        </div>
    );
};

export default TicketList;
