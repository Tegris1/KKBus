import { useState } from "react";
import PaymentMethodCard from "./PaymentMethodCard";
import styles from "./Deposit.module.scss";
import { toast } from "react-toastify";

const METHODS = [
    { id: "blik", name: "BLIK", icon: "📱" },
    { id: "card", name: "Karta płatnicza", icon: "💳" },
    { id: "transfer", name: "Przelew", icon: "🏦" },
];

const DepositForm = () => {
    const [amount, setAmount] = useState<string>("");
    const [selectedMethod, setSelectedMethod] = useState<string>("blik");

    const handleDeposit = (e: React.FormEvent) => {
        e.preventDefault();
        if (!amount || parseFloat(amount) <= 0) {
            toast.error("Wprowadź poprawną kwotę.");
            return;
        }

        // UI-only action
        toast.success(`Zainicjowano wpłatę ${amount} PLN metodą ${selectedMethod.toUpperCase()}`);
        setAmount("");
    };

    return (
        <div className={styles["deposit-wrapper"]}>
            <form onSubmit={handleDeposit} className={styles["deposit-form"]}>
                <div className={styles["input-section"]}>
                    <label><strong>Kwota doładowania (PLN):</strong></label>
                    <input
                        type="number"
                        value={amount}
                        onChange={(e) => setAmount(e.target.value)}
                        placeholder="0.00"
                    />
                </div>

                <div className={styles["methods-section"]}>
                    <label><strong>Wybierz metodę płatności:</strong></label>
                    <div className={styles["methods-grid"]}>
                        {METHODS.map(m => (
                            <PaymentMethodCard
                                key={m.id}
                                {...m}
                                isSelected={selectedMethod === m.id}
                                onSelect={setSelectedMethod}
                            />
                        ))}
                    </div>
                </div>

                <button type="submit" className={styles["submit-btn"]}>
                    Wpłać środki
                </button>
            </form>
        </div>
    );
};

export default DepositForm;