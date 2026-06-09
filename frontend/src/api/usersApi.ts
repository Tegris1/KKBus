import axiosClient from "./axiosClient";
import { User, UserRole } from "../types/user";

export const usersApi = {
  getUsers: async (): Promise<User[]> => {
    const response = await axiosClient.get<User[]>("/users");
    return response.data;
  },

  updateRole: async (id: number, role: UserRole): Promise<User> => {
    const response = await axiosClient.put<User>(`/users/${id}/role`, { role });
    return response.data;
  },
};
