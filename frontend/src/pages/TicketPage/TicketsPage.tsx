import TicketList from "../../components/TicketList/TicketList";
import styles from "./TicketsPage.module.scss";

const TicketsPage = () => {
    return (
        <main className={styles.ticketsPage}>
            <header className={styles.header}>
                <h1 className={styles.title}>Twoje Bilety KKBus</h1>
                <p className={styles.subtitle}>Zarządzaj swoimi aktywnymi i archiwalnymi przejazdami</p>
            </header>
            
            <section className={styles.content}>
                <TicketList />
            </section>
        </main>
    );
};

export default TicketsPage;