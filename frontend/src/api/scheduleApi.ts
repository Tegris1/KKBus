import axiosClient from "./axiosClient";
import { Schedule, ScheduleRequest } from "../types/schedule";

export const scheduleApi = {
  getMySchedule: async (): Promise<Schedule[]> => {
    const response = await axiosClient.get<Schedule[]>("/schedules/me");
    return response.data;
  },

  getAll: async (): Promise<Schedule[]> => {
    const response = await axiosClient.get<Schedule[]>("/schedules");
    return response.data;
  },

  create: async (schedule: ScheduleRequest): Promise<Schedule> => {
    const response = await axiosClient.post<Schedule>("/schedules", schedule);
    return response.data;
  },

  update: async (
    scheduleId: number,
    schedule: ScheduleRequest,
  ): Promise<Schedule> => {
    const response = await axiosClient.put<Schedule>(
      `/schedules/${scheduleId}`,
      schedule,
    );
    return response.data;
  },

  delete: async (scheduleId: number): Promise<void> => {
    await axiosClient.delete(`/schedules/${scheduleId}`);
  },
};
