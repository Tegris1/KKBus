import { useState, useEffect } from "react";
import DepositForm from "../../components/Deposit/DepositForm";
import { getWalletInfo } from "../../api/depositApi";
import { toast } from "react-toastify";
import styles from "./DepositPage.module.scss";

const DepositPage = () => {
    const currentClientId = 1; // Docelowo pobierane z AuthContext
    const [balance, setBalance] = useState<number>(0);
    const [points, setPoints] = useState<number>(0);
    const [isLoading, setIsLoading] = useState<boolean>(true);

    const refreshWalletData = async () => {
        try {
            const data = await getWalletInfo(currentClientId);
            setBalance(data.balance);
            setPoints(data.points);
        } catch (error: any) {
            toast.error("Błąd podczas pobierania danych portfela");
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        refreshWalletData();
    }, []);

    if (isLoading) return <div className={styles.loading}>Ładowanie danych...</div>;

    return (
        <main className={styles.depositPage}>
            <header className={styles.headerSection}>
                <h1 className={styles.mainTitle}>Twój Portfel KKBus</h1>
            </header>

            <section className={styles.walletOverview}>
                {/* Karta głównego salda */}
                <div className={styles.infoCard}>
                    <span className={styles.label}>Dostępne środki</span>
                    <div className={styles.value}>
                        {balance.toFixed(2)} <span>PLN</span>
                    </div>
                </div>

                {/* Karta punktów lojalnościowych - UC10 [3] */}
                <div className={`${styles.infoCard} ${styles.loyaltyCard}`}>
                    <span className={styles.label}>Program Lojalnościowy</span>
                    <div className={styles.value}>
                        {points} <span>pkt</span>
                    </div>
                </div>
            </section>

            <section className={styles.formSection}>
                <h3 className={styles.formTitle}>Zasilenie konta</h3>
                <DepositForm onDepositSuccess={refreshWalletData} />
            </section>
        </main>
    );
};

export default DepositPage;