import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { AxiosError } from "axios";
import { authApi } from "../../api/authApi";
import ErrorMessage from "../../components/ErrorMessage/ErrorMessage";
import styles from "./RegisterPage.module.scss";
import bgImage from "../../assets/logowanietlo.jpg";
import { useLanguage } from "../../context/LanguageContext";

const RegisterPage: React.FC = () => {
  const navigate = useNavigate();
  const { t } = useLanguage();
  const [username, setUsername] = useState<string>("");
  const [firstName, setFirstName] = useState<string>("");
  const [lastName, setLastName] = useState<string>("");
  const [birthDate, setBirthDate] = useState<string>("");
  const [phoneNumber, setPhoneNumber] = useState<string>("");
  const [loyaltyProgram, setLoyaltyProgram] = useState<boolean>(true);
  const [email, setEmail] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [confirmPassword, setConfirmPassword] = useState<string>("");
  const [errors, setErrors] = useState<{ [key: string]: string }>({});

  const validate = (): boolean => {
    const newErrors: { [key: string]: string } = {};

    if (!username.trim()) {
      newErrors.username = t("auth.usernameRequired");
    }
    if (!firstName.trim()) {
      newErrors.firstName = t("auth.firstNameRequired");
    }
    if (!lastName.trim()) {
      newErrors.lastName = t("auth.lastNameRequired");
    }
    if (!birthDate) {
      newErrors.birthDate = t("auth.birthDateRequired");
    }
    if (!phoneNumber.trim()) {
      newErrors.phoneNumber = t("auth.phoneNumberRequired");
    }
    if (!email.trim()) {
      newErrors.email = t("auth.emailRequired");
    } else if (!/\S+@\S+\.\S+/.test(email)) {
      newErrors.email = t("auth.emailInvalid");
    }
    if (!password) {
      newErrors.password = t("auth.passwordRequired");
    } else if (password.length < 6) {
      newErrors.password = t("auth.passwordLength");
    }
    if (!confirmPassword) {
      newErrors.confirmPassword = t("auth.confirmRequired");
    } else if (password !== confirmPassword) {
      newErrors.confirmPassword = t("auth.passwordMismatch");
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrors({});

    if (!validate()) return;

    try {
      await authApi.register({
        username,
        email,
        password,
        firstName,
        lastName,
        birthDate,
        phoneNumber,
        loyaltyProgram,
      });
      alert(t("auth.registrationSuccess"));
      navigate("/login");
    } catch (requestError) {
      if (requestError instanceof AxiosError) {
        if (requestError.response?.data) {
          const errorData = requestError.response.data;
          setErrors(
            errorData.errors || {
              general: errorData.message || t("auth.registrationError"),
            },
          );
        } else {
          setErrors({ general: t("auth.registrationError") });
        }
      } else {
        setErrors({ general: t("auth.unknownRegistrationError") });
      }
    }
  };

  return (
    <div className={styles.pageWrapper}>
      <div
        className={styles.imageSection}
        style={{ backgroundImage: `url(${bgImage})` }}
      />

      <div className={styles.registerSection}>
        <div className={styles.container}>
          <h2>{t("auth.welcome")}</h2>
          <p style={{ marginBottom: "20px", color: "#666" }}>
            {t("auth.registerSubtitle")}
          </p>
          <form onSubmit={handleRegister} className={styles.form}>
            <div className={styles.formGroup}>
              <label>{t("auth.username")}:</label>
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
                className={styles.input}
              />
              {errors.username && <ErrorMessage message={errors.username} />}
            </div>
            <div className={styles.formGroup}>
              <label>{t("auth.firstName")}:</label>
              <input
                type="text"
                value={firstName}
                onChange={(e) => setFirstName(e.target.value)}
                required
                className={styles.input}
              />
              {errors.firstName && <ErrorMessage message={errors.firstName} />}
            </div>
            <div className={styles.formGroup}>
              <label>{t("auth.lastName")}:</label>
              <input
                type="text"
                value={lastName}
                onChange={(e) => setLastName(e.target.value)}
                required
                className={styles.input}
              />
              {errors.lastName && <ErrorMessage message={errors.lastName} />}
            </div>
            <div className={styles.formGroup}>
              <label>{t("auth.birthDate")}:</label>
              <input
                type="date"
                value={birthDate}
                onChange={(e) => setBirthDate(e.target.value)}
                required
                className={styles.input}
              />
              {errors.birthDate && <ErrorMessage message={errors.birthDate} />}
            </div>
            <div className={styles.formGroup}>
              <label>{t("auth.phoneNumber")}:</label>
              <input
                type="tel"
                value={phoneNumber}
                onChange={(e) => setPhoneNumber(e.target.value)}
                required
                className={styles.input}
              />
              {errors.phoneNumber && <ErrorMessage message={errors.phoneNumber} />}
            </div>
            <div className={styles.formGroup}>
              <label>
                <input
                  type="checkbox"
                  checked={loyaltyProgram}
                  onChange={(e) => setLoyaltyProgram(e.target.checked)}
                />{" "}
                {t("auth.loyaltyOptIn")}
              </label>
            </div>
            <div className={styles.formGroup}>
              <label>{t("auth.email")}:</label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                className={styles.input}
              />
              {errors.email && <ErrorMessage message={errors.email} />}
            </div>
            <div className={styles.formGroup}>
              <label>{t("auth.password")}:</label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                className={styles.input}
              />
              {errors.password && <ErrorMessage message={errors.password} />}
            </div>
            <div className={styles.formGroup}>
              <label>{t("auth.confirmPassword")}:</label>
              <input
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
                className={styles.input}
              />
              {errors.confirmPassword && (
                <ErrorMessage message={errors.confirmPassword} />
              )}
            </div>
            {errors.general && <ErrorMessage message={errors.general} />}
            <button type="submit" className={styles.button}>
              {t("auth.register")}
            </button>
            <div style={{ marginTop: "20px", fontSize: "0.9rem" }}>
              {t("auth.hasAccount")}{" "}
              <span
                style={{ color: "#003366", cursor: "pointer", fontWeight: "bold" }}
                onClick={() => navigate("/login")}
              >
                {t("auth.login")}
              </span>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;
