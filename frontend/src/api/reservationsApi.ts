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

export interface DriverPassengerReservation {
  reservationId: number;
  passengerName: string;
  email: string;
  phoneNumber: string | null;
  seats: number;
  boardingStop: string | null;
  alightingStop: string | null;
  discountType: string | null;
}

export interface DriverPassengerCourse {
  routeId: number;
  origin: string;
  destination: string;
  departureTime: string;
  arrivalTime: string;
  busId: number | null;
  totalSeats: number | null;
  passengers: DriverPassengerReservation[];
}

export const reservationsApi = {
  getAll: async (): Promise<Reservation[]> => {
    const response = await axiosClient.get<Reservation[]>("/reservations");
    return response.data;
  },

  cancel: async (id: number): Promise<void> => {
    await axiosClient.delete(`/reservations/${id}`);
  },

  getDriverPassengerLists: async (): Promise<DriverPassengerCourse[]> => {
    const response = await axiosClient.get<DriverPassengerCourse[]>(
      "/driver/passenger-lists",
    );
    return response.data;
  },
};
