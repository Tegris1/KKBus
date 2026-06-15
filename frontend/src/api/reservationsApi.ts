import axiosClient from "./axiosClient";

export interface Reservation {
  id: number;
  amount: number;
  seats: number | null;
  routeId: number;
  origin: string;
  destination: string;
  departureTime: string;
}

export const reservationsApi = {
  getAll: async (): Promise<Reservation[]> => {
    const response = await axiosClient.get<Reservation[]>("/reservations");
    return response.data;
  },

  cancel: async (id: number): Promise<void> => {
    await axiosClient.delete(`/reservations/${id}`);
  },
};
