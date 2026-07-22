import { createContext, useContext, useState, type ReactNode } from "react";
import type { UserProfile } from "../types/report";

interface AuthState {
  username: string;
  profile: UserProfile;
}

interface AuthContextValue {
  auth: AuthState | null;
  login: (username: string, profile: UserProfile) => void;
  logout: () => void;
}

const STORAGE_KEY = "chronos.auth";

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

function readStoredAuth(): AuthState | null {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as AuthState;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [auth, setAuth] = useState<AuthState | null>(() => readStoredAuth());

  function login(username: string, profile: UserProfile) {
    const next = { username, profile };
    localStorage.setItem(STORAGE_KEY, JSON.stringify(next));
    setAuth(next);
  }

  function logout() {
    localStorage.removeItem(STORAGE_KEY);
    setAuth(null);
  }

  return <AuthContext.Provider value={{ auth, login, logout }}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth doit être utilisé à l'intérieur d'un AuthProvider");
  return ctx;
}
