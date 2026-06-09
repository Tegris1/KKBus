import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

const EmployeeRoute = ({ children }: { children: React.ReactNode }) => {
  const { isAuthenticated, isEmployee } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return isEmployee ? <>{children}</> : <Navigate to="/" replace />;
};

export default EmployeeRoute;
