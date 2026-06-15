import axiosClient from "./axiosClient";
import { Route, Reservation, RouteRequest } from "../types/route";

interface RouteResponse {
  id: number;
  origin: string;
  destination: string;
  departureTime: string;
  arrivalTime: string;
  intermediateStops?: string[] | null;
  price: number | string | null;
  driverId: number;
  busId: number;
  fuelCost: number | string | null;
  reservation?: unknown;
}

export interface ReservationResponse {
  amount?: number;
  awardedPoints?: number;
  pointsSpent?: number;
  discountAmount?: number;
}

const normalizeRoute = (route: RouteResponse): Route => ({
  id: route.id,
  origin: route.origin,
  destination: route.destination,
  departureTime: route.departureTime,
  arrivalTime: route.arrivalTime,
  intermediateStops: route.intermediateStops ?? [],
  price: Number(route.price ?? 0),
  driverId: route.driverId,
  busId: route.busId,
  fuelCost: Number(route.fuelCost ?? 0),
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

  createReservation: async (
    reservation: Reservation,
  ): Promise<ReservationResponse> => {
    const response = await axiosClient.post<ReservationResponse>(
      "reservations",
      reservation,
    );
    return response.data;
  },

  createRoute: async (route: RouteRequest): Promise<Route> => {
    const response = await axiosClient.post<RouteResponse>("route", route);
    return normalizeRoute(response.data);
  },
};
