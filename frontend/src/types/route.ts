export interface Route {
  id: number;
  origin: string;
  destination: string;
  departureTime: string;
  arrivalTime: string;
  intermediateStops: string[];
  price: number;
  driverId: number;
  busId: number;
  fuelCost: number;
  reservation?: unknown;
}

export interface Reservation {
  routeId: number;
  seats: number;
  travelDepartureTime: string;
  usePointsDiscount: boolean;
}

export interface RouteRequest {
  origin: string;
  departureTime: string;
  destination: string;
  arrivalTime: string;
  intermediateStops: string[];
  price: number;
  driverId: number;
  busId: number;
  fuelCost: number;
}
