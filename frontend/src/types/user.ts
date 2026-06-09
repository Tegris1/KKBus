export type UserRole = "USER" | "EMPLOYEE" | "ADMIN";

export interface User {
  id: number;
  username: string;
  email: string;
  role: UserRole | null;
}
