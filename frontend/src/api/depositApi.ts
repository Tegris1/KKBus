export const getWalletInfo = async (clientId: number) => {
    const response = await fetch(`/api/clients/${clientId}/wallet/`);
    if (!response.ok) {
        throw new Error("Nie udało się pobrać informacji o stanie konta.");
    }
    return response.json(); // Zwraca np. { balance: 125.50, points: 450 }
};

/**
 * Wysyła żądanie doładowania konta klienta.
 */
export const depositFundsApi = async (clientId: number, amount: number, method: string) => {
    const response = await fetch(`/api/clients/${clientId}/deposit/`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ amount, method }),
    });

    if (!response.ok) {
        throw new Error("Wystąpił błąd podczas przetwarzania wpłaty.");
    }
    return response.json();
};