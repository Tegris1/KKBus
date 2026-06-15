export type ReportPeriod = "DAILY" | "WEEKLY" | "MONTHLY" | "YEARLY";

export interface ReportSegment {
  origin: string;
  destination: string;
  passengerCount: number;
}

export interface CourseReport {
  routeId: number;
  origin: string;
  destination: string;
  departureTime: string;
  busId: number | null;
  driverId: number | null;
  driverName: string;
  soldTickets: number;
  passengerCount: number;
  revenue: number;
  fuelCost: number;
  profit: number;
  segments: ReportSegment[];
}

export interface TicketReport {
  periodType: ReportPeriod;
  periodStart: string;
  periodEnd: string;
  generatedAt: string;
  selectedDriverId: number | null;
  selectedBusId: number | null;
  courseCount: number;
  soldTickets: number;
  passengerCount: number;
  revenue: number;
  fuelCost: number;
  profit: number;
  courses: CourseReport[];
}

export interface ReportOptions {
  drivers: Array<{ id: number; name: string }>;
  busIds: number[];
}
