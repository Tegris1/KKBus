import axiosClient from "./axiosClient";
import { Route, Reservation } from "../types/route";

export const routesApi = {
  getRoutes: async (origin: string, destination: string): Promise<Route[]> => {
    const response = await axiosClient.get("/route", {
      params: {
        origin,
        destination,
      },
    });
    return response.data;
  },

  createReservation: async (reservation: Reservation): Promise<void> => {
    await axiosClient.post("/reservations", reservation);
  },
};
