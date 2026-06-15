import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { AxiosError } from "axios";
import { authApi } from "../../api/authApi";
import ErrorMessage from "../../components/ErrorMessage/ErrorMessage";
import styles from "./Login.Page.module.scss";
import { useAuth } from "../../context/AuthContext";
import { useLanguage } from "../../context/LanguageContext";
import bgImage from "../../assets/logowanietlo.jpg";

const LoginPage: React.FC = () => {
  const { login } = useAuth();
  const { t } = useLanguage();
  const navigate = useNavigate();
  const [email, setEmail] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [error, setError] = useState<string>("");

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    try {
      const response = await authApi.login({ email, password });
      const roles = login(response.data.token);

      if (roles.includes("ADMIN")) {
        navigate("/admin/users");
      } else if (roles.includes("EMPLOYEE")) {
        navigate("/employee-schedule");
      } else {
        navigate("/my-tickets");
      }
    } catch (requestError) {
      if (requestError instanceof AxiosError) {
        if (requestError.response?.data) {
          setError(
            typeof requestError.response.data === "string"
              ? requestError.response.data
              : requestError.response.data.message || t("auth.loginError"),
          );
        } else {
          setError(t("auth.loginRequestError"));
        }
      } else {
        setError(t("auth.unknownLoginError"));
      }
    }
  };

  return (
    <div className={styles.pageWrapper}>
      <div
        className={styles.imageSection}
        style={{ backgroundImage: `url(${bgImage})` }}
      />

      <div className={styles.loginSection}>
        <div className={styles.container}>
          <h2 className={styles.title}>{t("auth.welcome")}</h2>
          <p style={{ marginBottom: "20px", color: "#666" }}>
            {t("auth.loginSubtitle")}
          </p>

          <form onSubmit={handleLogin} className={styles.form}>
            <div className={styles.formGroup}>
              <label>
                <strong>{t("auth.email")}:</strong>
              </label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                placeholder="you@email.com"
                className={styles.input}
              />
            </div>

            <div className={styles.formGroup}>
              <label>
                <strong>{t("auth.password")}:</strong>
              </label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                placeholder="********"
                className={styles.input}
              />
            </div>

            {error && <ErrorMessage message={error} />}

            <button type="submit" className={styles.button}>
              {t("auth.login")}
            </button>

            <div style={{ marginTop: "20px", fontSize: "0.9rem" }}>
              {t("auth.noAccount")}{" "}
              <span
                style={{ color: "#003366", cursor: "pointer", fontWeight: "bold" }}
                onClick={() => navigate("/register")}
              >
                {t("auth.register")}
              </span>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
