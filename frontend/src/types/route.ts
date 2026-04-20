export interface Route {
  id: number;
  origin: string;
  destination: string;
  departureTime: string;
  arrivalTime: string;
  price: number;
  reservation?: any;
}

export interface Reservation {
  routeId: number;
  seats: number;
}
