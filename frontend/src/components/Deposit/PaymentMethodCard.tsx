import styles from "./DepositForm.module.scss";

interface PaymentMethodProps {
    id: string;
    name: string;
    icon: string;
    isSelected: boolean;
    onSelect: (id: string) => void;
}

const PaymentMethodCard = ({ id, name, icon, isSelected, onSelect }: PaymentMethodProps) => {
    return (
        <div
            className={`${styles["method-card"]} ${isSelected ? styles.selected : ""}`}
            onClick={() => onSelect(id)}
        >
            <span className={styles["icon"]}>{icon}</span>
            <strong>{name}</strong>
        </div>
    );
};

export default PaymentMethodCard;