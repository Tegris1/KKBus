import TicketList from "../../components/TicketList/TicketList";
import { useLanguage } from "../../context/LanguageContext";
import styles from "./TicketsPage.module.scss";

const TicketsPage = () => {
    const { t } = useLanguage();

    return (
        <main className={styles.ticketsPage}>
            <header className={styles.header}>
                <h1 className={styles.title}>{t("tickets.title")}</h1>
                <p className={styles.subtitle}>{t("tickets.subtitle")}</p>
            </header>

            <section className={styles.content}>
                <TicketList />
            </section>
        </main>
    );
};

export default TicketsPage;
