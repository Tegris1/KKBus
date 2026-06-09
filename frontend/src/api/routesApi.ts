import axiosClient from "./axiosClient";
import { Route, Reservation, RouteRequest } from "../types/route";

interface RouteResponse {
  id: number;
  origin: string;
  destination: string;
  departureTime: string;
  arrivalTime: string;
  price: number | string | null;
  reservation?: unknown;
}

const normalizeRoute = (route: RouteResponse): Route => ({
  id: route.id,
  origin: route.origin,
  destination: route.destination,
  departureTime: route.departureTime,
  arrivalTime: route.arrivalTime,
  price: Number(route.price ?? 0),
  reservation: route.reservation,
});

export const routesApi = {
  getRoutes: async (origin: string, destination: string): Promise<Route[]> => {
    const response = await axiosClient.get<RouteResponse[]>("route", {
      params: {
        origin,
        destination,
      },
    });
    return response.data.map(normalizeRoute);
  },

  createReservation: async (reservation: Reservation): Promise<void> => {
    await axiosClient.post("reservations", reservation);
  },

  createRoute: async (route: RouteRequest): Promise<Route> => {
    const response = await axiosClient.post<RouteResponse>("route", route);
    return normalizeRoute(response.data);
  },
};
