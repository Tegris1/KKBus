import React, {
  createContext,
  useContext,
  useState,
  useCallback,
  useMemo,
  useEffect,
} from "react";

interface AuthContextType {
  isAuthenticated: boolean;
  roles: string[];
  isEmployee: boolean;
  isAdmin: boolean;
  canManageRoutes: boolean;
  login: (token: string) => string[];
  logout: () => void;
}

const AuthContext = createContext<AuthContextType>({
  isAuthenticated: false,
  roles: [],
  isEmployee: false,
  isAdmin: false,
  canManageRoutes: false,
  login: () => [],
  logout: () => {},
});

interface TokenPayload {
  exp?: number;
  roles?: string[];
}

const parseTokenPayload = (token: string): TokenPayload | null => {
  try {
    const encodedPayload = token.split(".")[1];
    if (!encodedPayload) {
      return null;
    }

    const payload = encodedPayload.replace(/-/g, "+").replace(/_/g, "/");
    const paddedPayload = payload.padEnd(
      payload.length + ((4 - (payload.length % 4)) % 4),
      "=",
    );

    return JSON.parse(atob(paddedPayload)) as TokenPayload;
  } catch {
    return null;
  }
};

// Utility function to check token expiration
const isTokenExpired = (token: string): boolean => {
  const payload = parseTokenPayload(token);
  return !payload?.exp || payload.exp * 1000 < Date.now();
};

const getTokenRoles = (token: string | null): string[] => {
  if (!token || isTokenExpired(token)) {
    return [];
  }

  return parseTokenPayload(token)?.roles ?? [];
};

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [roles, setRoles] = useState<string[]>(() => {
    const token = localStorage.getItem("accessToken");
    return getTokenRoles(token);
  });

  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(() => {
    const token = localStorage.getItem("accessToken");
    if (token && isTokenExpired(token)) {
      localStorage.removeItem("accessToken");
      return false;
    }
    return !!token;
  });

  const login = useCallback((token: string) => {
    const nextRoles = getTokenRoles(token);

    localStorage.setItem("accessToken", token);
    setRoles(nextRoles);
    setIsAuthenticated(true);

    return nextRoles;
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem("accessToken");
    setRoles([]);
    setIsAuthenticated(false);
  }, []);

  // Check token expiration periodically
  useEffect(() => {
    const interval = setInterval(() => {
      const token = localStorage.getItem("accessToken");
      if (token && isTokenExpired(token)) {
        logout();
      }
    }, 60000);

    return () => clearInterval(interval);
  }, [logout]);

  const isEmployee = roles.includes("EMPLOYEE");
  const isAdmin = roles.includes("ADMIN");
  const canManageRoutes = isAdmin || isEmployee;

  const value = useMemo(
    () => ({
      isAuthenticated,
      roles,
      isEmployee,
      isAdmin,
      canManageRoutes,
      login,
      logout,
    }),
    [
      isAuthenticated,
      roles,
      isEmployee,
      isAdmin,
      canManageRoutes,
      login,
      logout,
    ],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = () => useContext(AuthContext);

export { AuthContext };
