export interface Route {
  id: number;
  origin: string;
  destination: string;
  departureTime: string;
  arrivalTime: string;
  price: number;
  reservation?: unknown;
}

export interface Reservation {
  routeId: number;
  seats: number;
}

export interface RouteRequest {
  origin: string;
  departureTime: string;
  destination: string;
  arrivalTime: string;
  price: number;
}
