export type UserRole = "USER" | "EMPLOYEE" | "SECRETARY" | "ADMIN";

export interface User {
  id: number;
  username: string;
  email: string;
  role: UserRole | null;
}

export interface DriverOption {
  id: number;
  name: string;
}
