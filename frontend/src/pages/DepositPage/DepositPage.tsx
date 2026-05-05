import DepositForm from "../../components/Deposit/DepositForm";

const DepositPage = () => {
    return (
        <main style={{ padding: "20px" }}>
            <h1 style={{ color: "#003366", marginLeft: "20px" }}>Zasil konto KKBus</h1>
            <DepositForm />
        </main>
    );
};

export default DepositPage;