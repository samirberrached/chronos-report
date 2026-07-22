import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import vermegLogo from "../assets/vermeg-logo.svg";
import type { UserProfile } from "../types/report";

const PROFILE_OPTIONS: { value: UserProfile; label: string }[] = [
  { value: "admin", label: "Data Administrator" },
  { value: "financial", label: "Financial Officer" },
  { value: "product", label: "Product Manager" },
];

export function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [profile, setProfile] = useState<UserProfile>("admin");

  function handleSubmit(e: FormEvent) {
    e.preventDefault();
    // Authentification factice : pas de backend d'auth pour l'instant,
    // seul le profil choisi détermine le dashboard affiché.
    login(username || "invité", profile);
    navigate(`/dashboard/${profile}`);
  }

  return (
    <div className="login-page">
      <div className="login-card">
        <img src={vermegLogo} alt="VERMEG" className="vermeg-logo" />
        <h1>Chronos Report</h1>
        <p className="login-subtitle">Analytic Employee Time — connexion</p>

        <form onSubmit={handleSubmit}>
          <div className="field">
            <label htmlFor="username">Identifiant</label>
            <input
              id="username"
              className="input"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="prenom.nom"
              autoComplete="username"
            />
          </div>
          <div className="field">
            <label htmlFor="password">Mot de passe</label>
            <input
              id="password"
              className="input"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              autoComplete="current-password"
            />
          </div>
          <div className="field">
            <label htmlFor="profile">Profil</label>
            <select
              id="profile"
              className="select"
              value={profile}
              onChange={(e) => setProfile(e.target.value as UserProfile)}
            >
              {PROFILE_OPTIONS.map((opt) => (
                <option key={opt.value} value={opt.value}>
                  {opt.label}
                </option>
              ))}
            </select>
          </div>
          <button type="submit" className="btn btn-primary">
            Se connecter
          </button>
        </form>
      </div>
    </div>
  );
}
