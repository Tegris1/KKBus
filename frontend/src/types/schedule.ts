export interface Schedule {
  scheduleId: number;
  employeeId: number;
  busId: number;
  workingDate: string;
  startTime: string;
  endTime: string;
}

export interface ScheduleRequest {
  employeeId: number;
  busId: number;
  workingDate: string;
  startTime: string;
  endTime: string;
}
