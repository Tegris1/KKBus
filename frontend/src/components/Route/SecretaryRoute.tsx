import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

const SecretaryRoute = ({ children }: { children: React.ReactNode }) => {
  const { isAuthenticated, isSecretary, isAdmin } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return isSecretary || isAdmin ? <>{children}</> : <Navigate to="/" replace />;
};

export default SecretaryRoute;
