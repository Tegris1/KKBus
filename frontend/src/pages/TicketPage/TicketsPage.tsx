import TicketList from "../../components/TicketList/TicketList";

const TicketsPage = () => {
    return (
        <main style={{ padding: "20px" }}>
            <h1 style={{ color: "#003366" }}>Twoje Bilety KKBus</h1>
            <section>
                <TicketList />
            </section>
        </main>
    );
};

export default TicketsPage;