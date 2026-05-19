import { useState } from "react";
import PaymentMethodCard from "./PaymentMethodCard";
import styles from "./DepositForm.module.scss";
import { toast } from "react-toastify";

// Definicja interfejsu dla propsów, aby komponent mógł komunikować się ze stroną nadrzędną
interface DepositFormProps {
    onDepositSuccess: (amount: number) => void;
}

const METHODS = [
    { id: "blik", name: "BLIK", icon: "📱" },
    { id: "card", name: "Karta płatnicza", icon: "💳" },
    { id: "transfer", name: "Przelew", icon: "🏦" },
];

const DepositForm = ({ onDepositSuccess }: DepositFormProps) => {
    const [amount, setAmount] = useState<string>("");
    const [selectedMethod, setSelectedMethod] = useState<string>("blik");

    const handleDeposit = (e: React.FormEvent) => {
        e.preventDefault();
        const depositValue = parseFloat(amount);

        // Walidacja kwoty zgodnie ze standardami systemów płatniczych
        if (!amount || depositValue <= 0) {
            toast.error("Wprowadź poprawną kwotę doładowania.");
            return;
        }

        // Symulacja akcji UI-only z powiadomieniem (styl transactionform.txt) [2]
        toast.success(`Zainicjowano wpłatę ${depositValue.toFixed(2)} PLN metodą ${selectedMethod.toUpperCase()}`);
        
        // Wywołanie funkcji przekazanej w propsach, aby zaktualizować stan konta na stronie DepositPage
        onDepositSuccess(depositValue);
        
        // Resetowanie pola wprowadzania
        setAmount("");
    };

    return (
        <div className={styles["deposit-wrapper"]}>
            <form onSubmit={handleDeposit} className={styles["deposit-form"]}>
                <div className={styles["input-section"]}>
                    {/* Zastosowanie pogrubienia zgodnie z Twoim życzeniem */}
                    <label><strong>Kwota doładowania (PLN):</strong></label>
                    <input
                        type="number"
                        step="0.01"
                        value={amount}
                        onChange={(e) => setAmount(e.target.value)}
                        placeholder="0.00"
                        className={styles["deposit-input"]}
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
                    Doładuj konto portfela
                </button>
            </form>
        </div>
    );
};

export default DepositForm;