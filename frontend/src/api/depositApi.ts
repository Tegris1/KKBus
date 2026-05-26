import axiosClient from "./axiosClient";

export interface WalletInfo {
    id: number;
    userId: number;
    money: number;
    points: number;
}

export const getWalletInfo = async (): Promise<WalletInfo> => {
    const response = await axiosClient.get<WalletInfo>("wallet");
    return response.data;
};

export const depositFundsApi = async (amount: number): Promise<WalletInfo> => {
    const wallet = await getWalletInfo();
    const nextMoney = Number(wallet.money) + amount;

    const response = await axiosClient.patch<WalletInfo>("wallet/money", {
        money: nextMoney,
    });

    return response.data;
};
