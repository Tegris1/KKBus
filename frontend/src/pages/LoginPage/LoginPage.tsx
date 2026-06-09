import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AxiosError } from 'axios';
import { authApi } from '../../api/authApi';
import ErrorMessage from '../../components/ErrorMessage/ErrorMessage';
import styles from './Login.Page.module.scss'; // Upewnij się, że nazwa pliku się zgadza
import { useAuth } from '../../context/AuthContext';
import bgImage from "../../assets/logowanietlo.jpg";

const LoginPage: React.FC = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [error, setError] = useState<string>('');

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
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
    } catch (error) {
      if (error instanceof AxiosError) {
        if (error.response?.data) {
          setError(typeof error.response.data === 'string' 
            ? error.response.data 
            : error.response.data.message || 'Błąd logowania');
        } else {
          setError('Wystąpił błąd podczas logowania.');
        }
      } else {
        setError('Wystąpił nieznany błąd podczas logowania.');
      }
    }
  };

  return (
    <div className={styles.pageWrapper}>
      {/* Lewa strona: Sekcja ze zdjęciem tła KKBus */}
      <div 
        className={styles.imageSection} 
        style={{ backgroundImage: `url(${bgImage})` }}
      >
        {/* Możesz tu dodać opcjonalny napis powitalny na zdjęciu */}
      </div>

      {/* Prawa strona: Sekcja logowania */}
      <div className={styles.loginSection}>
        <div className={styles.container}>
          <h2 className={styles.title}>Witaj w KKBus</h2>
          <p style={{ marginBottom: '20px', color: '#666' }}>Zaloguj się, aby zarządzać rezerwacjami</p>
          
          <form onSubmit={handleLogin} className={styles.form}>
            <div className={styles.formGroup}>
              <label><strong>Email:</strong></label>
              <input 
                type="email" 
                value={email} 
                onChange={(e) => setEmail(e.target.value)} 
                required 
                placeholder="twoj@email.pl"
                className={styles.input}
              />
            </div>
            
            <div className={styles.formGroup}>
              <label><strong>Hasło:</strong></label>
              <input 
                type="password" 
                value={password} 
                onChange={(e) => setPassword(e.target.value)} 
                required 
                placeholder="••••••••"
                className={styles.input}
              />
            </div>

            {error && <ErrorMessage message={error} />}

            <button type="submit" className={styles.button}>Zaloguj</button>
            
            <div style={{ marginTop: '20px', fontSize: '0.9rem' }}>
               Nie masz konta? <span style={{ color: '#003366', cursor: 'pointer', fontWeight: 'bold' }} onClick={() => navigate('/register')}>Zarejestruj się</span>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
