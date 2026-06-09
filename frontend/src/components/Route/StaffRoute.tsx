import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

const StaffRoute = ({ children }: { children: React.ReactNode }) => {
  const { isAuthenticated, canManageRoutes } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return canManageRoutes ? <>{children}</> : <Navigate to="/" replace />;
};

export default StaffRoute;
