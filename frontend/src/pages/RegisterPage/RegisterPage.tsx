// src/pages/RegisterPage/RegisterPage.tsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AxiosError } from 'axios';
import { authApi } from '../../api/authApi';
import ErrorMessage from '../../components/ErrorMessage/ErrorMessage';
import styles from './RegisterPage.module.scss';
import bgImage from "../../assets/logowanietlo.jpg";

const RegisterPage: React.FC = () => {
  const navigate = useNavigate();
  const [username, setUsername] = useState<string>('');
  const [email, setEmail] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [confirmPassword, setConfirmPassword] = useState<string>('');
  const [errors, setErrors] = useState<{ [key: string]: string }>({});

  const validate = (): boolean => {
    const newErrors: { [key: string]: string } = {};

    if (!username.trim()) {
      newErrors.username = 'username jest wymagany.';
    }

    if (!email.trim()) {
      newErrors.email = 'Email jest wymagany.';
    } else if (!/\S+@\S+\.\S+/.test(email)) {
      newErrors.email = 'Nieprawidłowy format email.';
    }

    if (!password) {
      newErrors.password = 'Hasło jest wymagane.';
    } else if (password.length < 6) {
      newErrors.password = 'Hasło musi mieć co najmniej 6 znaków.';
    }

    if (!confirmPassword) {
      newErrors.confirmPassword = 'Powtórzenie hasła jest wymagane.';
    } else if (password !== confirmPassword) {
      newErrors.confirmPassword = 'Hasła muszą być identyczne.';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrors({}); // Reset błędów

    if (!validate()) return;

    try {
      await authApi.register({ username, email, password });
      alert('Rejestracja udana. Możesz się teraz zalogować.');
      navigate('/login');
    } catch (error) {
      if (error instanceof AxiosError) {
        if (error.response?.data) {
          const errorData = error.response.data;
          setErrors(errorData.errors || { general: errorData.message || 'Błąd rejestracji. Spróbuj ponownie.' });
        } else {
          setErrors({ general: 'Błąd rejestracji. Spróbuj ponownie.' });
        }
      } else {
        setErrors({ general: 'Wystąpił nieznany błąd podczas rejestracji.' });
      }
    }
  };
return (
    <div className={styles.pageWrapper}>
      {/* Lewa sekcja na Twoje zdjęcie tła */}
      <div 
        className={styles.imageSection} 
        style={{ backgroundImage: `url(${bgImage})` }}
      />

      {/* Prawa sekcja z formularzem - owinięta w registerSection */}
      <div className={styles.registerSection}>
        <div className={styles.container}>
          <h2>Witaj w KKBus</h2>
          <p style={{ marginBottom: '20px', color: '#666' }}>Zarejestruj się, aby zarządzać rezerwacjami</p>
          <form onSubmit={handleRegister} className={styles.form}>
            <div className={styles.formGroup}>
              <label>Nazwa użytkownika:</label>
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
              <label>Email:</label>
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
              <label>Hasło:</label>
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
              <label>Powtórz hasło:</label>
              <input
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
                className={styles.input}
              />
              {errors.confirmPassword && <ErrorMessage message={errors.confirmPassword} />}
            </div>
            {errors.general && <ErrorMessage message={errors.general} />}
            <button type="submit" className={styles.button}>Zarejestruj</button>
            <div style={{ marginTop: '20px', fontSize: '0.9rem' }}>
               Masz już konto? <span style={{ color: '#003366', cursor: 'pointer', fontWeight: 'bold' }} onClick={() => navigate('/login')}>Zaloguj się</span>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;
