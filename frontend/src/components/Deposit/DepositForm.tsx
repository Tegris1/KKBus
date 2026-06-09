import { useState } from "react";
import { toast } from "react-toastify";
import { depositFundsApi } from "../../api/depositApi";
import PaymentMethodCard from "./PaymentMethodCard";
import styles from "./DepositForm.module.scss";

interface DepositFormProps {
    onDepositSuccess: () => Promise<void> | void;
}

const METHODS = [
    { id: "blik", name: "BLIK", icon: "📱" },
    { id: "card", name: "Karta płatnicza", icon: "💳" },
    { id: "transfer", name: "Przelew", icon: "🏦" },
];

const DepositForm = ({ onDepositSuccess }: DepositFormProps) => {
    const [amount, setAmount] = useState<string>("");
    const [selectedMethod, setSelectedMethod] = useState<string>("blik");
    const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

    const handleDeposit = async (e: React.FormEvent) => {
        e.preventDefault();
        const depositValue = Number(amount);

        if (!amount || Number.isNaN(depositValue) || depositValue <= 0) {
            toast.error("Wprowadź poprawną kwotę doładowania.");
            return;
        }

        try {
            setIsSubmitting(true);
            await depositFundsApi(depositValue);
            toast.success(`Doładowano ${depositValue.toFixed(2)} PLN metodą ${selectedMethod.toUpperCase()}`);
            setAmount("");
            await onDepositSuccess();
        } catch {
            toast.error("Wystąpił błąd podczas przetwarzania wpłaty.");
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className={styles["deposit-wrapper"]}>
            <form onSubmit={handleDeposit} className={styles["deposit-form"]}>
                <div className={styles["input-section"]}>
                    <label><strong>Kwota doładowania (PLN):</strong></label>
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
                    <label><strong>Wybierz metodę płatności:</strong></label>
                    <div className={styles["methods-grid"]}>
                        {METHODS.map((method) => (
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
                    {isSubmitting ? "Przetwarzanie..." : "Doładuj konto portfela"}
                </button>
            </form>
        </div>
    );
};

export default DepositForm;
