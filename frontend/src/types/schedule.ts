export type ScheduleDay =
  | "MONDAY"
  | "TUESDAY"
  | "WEDNESDAY"
  | "THURSDAY"
  | "FRIDAY"
  | "SATURDAY"
  | "SUNDAY";

export const SCHEDULE_DAYS: { value: ScheduleDay; label: string }[] = [
  { value: "MONDAY", label: "Poniedzialek" },
  { value: "TUESDAY", label: "Wtorek" },
  { value: "WEDNESDAY", label: "Sroda" },
  { value: "THURSDAY", label: "Czwartek" },
  { value: "FRIDAY", label: "Piatek" },
  { value: "SATURDAY", label: "Sobota" },
  { value: "SUNDAY", label: "Niedziela" },
];

export interface Schedule {
  scheduleId: number;
  employeeId: number;
  busId: number;
  dayOfWeek: ScheduleDay;
  startTime: string;
  endTime: string;
}

export interface ScheduleRequest {
  employeeId: number;
  busId: number;
  dayOfWeek: ScheduleDay;
  startTime: string;
  endTime: string;
}
