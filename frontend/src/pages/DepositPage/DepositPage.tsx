import { useCallback, useEffect, useState } from "react";
import { toast } from "react-toastify";
import { getWalletInfo } from "../../api/depositApi";
import DepositForm from "../../components/Deposit/DepositForm";
import { useLanguage } from "../../context/LanguageContext";
import styles from "./DepositPage.module.scss";

const DepositPage = () => {
    const { t } = useLanguage();
    const [balance, setBalance] = useState<number>(0);
    const [points, setPoints] = useState<number>(0);
    const [isLoading, setIsLoading] = useState<boolean>(true);

    const refreshWalletData = useCallback(async () => {
        try {
            const data = await getWalletInfo();
            setBalance(Number(data.money ?? 0));
            setPoints(Number(data.points ?? 0));
        } catch {
            toast.error(t("wallet.loadError"));
        } finally {
            setIsLoading(false);
        }
    }, [t]);

    useEffect(() => {
        void refreshWalletData();
    }, [refreshWalletData]);

    if (isLoading) return <div className={styles.loading}>{t("wallet.loading")}</div>;

    return (
        <main className={styles.depositPage}>
            <header className={styles.headerSection}>
                <h1 className={styles.mainTitle}>{t("wallet.title")}</h1>
            </header>

            <section className={styles.walletOverview}>
                <div className={styles.infoCard}>
                    <span className={styles.label}>{t("wallet.balance")}</span>
                    <div className={styles.value}>
                        {balance.toFixed(2)} <span>PLN</span>
                    </div>
                </div>

                <div className={`${styles.infoCard} ${styles.loyaltyCard}`}>
                    <span className={styles.label}>{t("wallet.loyalty")}</span>
                    <div className={styles.value}>
                        {points} <span>pkt</span>
                    </div>
                </div>
            </section>

            <section className={styles.formSection}>
                <h3 className={styles.formTitle}>{t("wallet.topUp")}</h3>
                <DepositForm onDepositSuccess={refreshWalletData} />
            </section>
        </main>
    );
};

export default DepositPage;
