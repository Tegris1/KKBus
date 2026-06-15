import axiosClient from "./axiosClient";
import { ReportOptions, ReportPeriod, TicketReport } from "../types/report";

export interface ReportFilters {
  period: ReportPeriod;
  referenceDate: string;
  driverId?: number;
  busId?: number;
}

export const reportsApi = {
  getOptions: async (): Promise<ReportOptions> => {
    const response = await axiosClient.get<ReportOptions>("/reports/options");
    return response.data;
  },

  generate: async (filters: ReportFilters): Promise<TicketReport> => {
    const response = await axiosClient.get<TicketReport>("/reports/tickets", {
      params: filters,
    });
    return response.data;
  },
};
