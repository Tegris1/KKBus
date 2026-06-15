import { useState } from "react";
import { toast } from "react-toastify";
import { depositFundsApi } from "../../api/depositApi";
import { useLanguage } from "../../context/LanguageContext";
import PaymentMethodCard from "./PaymentMethodCard";
import styles from "./DepositForm.module.scss";

interface DepositFormProps {
    onDepositSuccess: () => Promise<void> | void;
}

const DepositForm = ({ onDepositSuccess }: DepositFormProps) => {
    const { t } = useLanguage();
    const methods = [
        { id: "blik", name: "BLIK", icon: "B" },
        { id: "card", name: t("wallet.card"), icon: "K" },
        { id: "transfer", name: t("wallet.transfer"), icon: "P" },
    ];
    const [amount, setAmount] = useState<string>("");
    const [selectedMethod, setSelectedMethod] = useState<string>("blik");
    const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

    const handleDeposit = async (e: React.FormEvent) => {
        e.preventDefault();
        const depositValue = Number(amount);

        if (!amount || Number.isNaN(depositValue) || depositValue <= 0) {
            toast.error(t("wallet.invalidAmount"));
            return;
        }

        try {
            setIsSubmitting(true);
            await depositFundsApi(depositValue);
            toast.success(
                t("wallet.success", {
                    amount: depositValue.toFixed(2),
                    method: selectedMethod.toUpperCase(),
                }),
            );
            setAmount("");
            await onDepositSuccess();
        } catch {
            toast.error(t("wallet.error"));
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className={styles["deposit-wrapper"]}>
            <form onSubmit={handleDeposit} className={styles["deposit-form"]}>
                <div className={styles["input-section"]}>
                    <label><strong>{t("wallet.amount")}:</strong></label>
                    <input
                        type="number"
                        step="0.01"
                        min="0.01"
                        value={amount}
                        onChange={(e) => setAmount(e.target.value)}
                        placeholder="0.00"
                        className={styles["deposit-input"]}
                    />
                </div>

                <div className={styles["methods-section"]}>
                    <label><strong>{t("wallet.method")}:</strong></label>
                    <div className={styles["methods-grid"]}>
                        {methods.map((method) => (
                            <PaymentMethodCard
                                key={method.id}
                                {...method}
                                isSelected={selectedMethod === method.id}
                                onSelect={setSelectedMethod}
                            />
                        ))}
                    </div>
                </div>

                <button type="submit" className={styles["submit-btn"]} disabled={isSubmitting}>
                    {isSubmitting ? t("wallet.processing") : t("wallet.submit")}
                </button>
            </form>
        </div>
    );
};

export default DepositForm;
