import axiosClient from "./axiosClient";

export interface CompanyInfo {
  name: string;
  owner: string;
  address: string;
  phone: string;
  fax: string;
  description: string;
  drivers: string[];
  secretariat: string[];
}

export interface Vehicle {
  id: number;
  fleetNumber: number;
  name: string;
  seats: number;
  status: "ACTIVE" | "IN_REPAIR" | "UNAVAILABLE";
  parkingLocation: string;
  averageFuelConsumption: number;
}

export const companyApi = {
  getInfo: async (): Promise<CompanyInfo> => {
    const response = await axiosClient.get<CompanyInfo>("company");
    return response.data;
  },
  getVehicles: async (): Promise<Vehicle[]> => {
    const response = await axiosClient.get<Vehicle[]>("vehicles");
    return response.data;
  },
};
