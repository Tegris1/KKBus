import { useEffect, useState } from 'react';
import { Transaction, transactionsApi } from '../../api/transactionsApi';
import styles from './TransactionsListPage.module.scss';

const TransactionsListPage = () => {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>('');

  useEffect(() => {
    const fetchTransactions = async () => {
      try {
        const data = await transactionsApi.getAll();
        setTransactions(data);
      } catch (err: any) {
        setError('Błąd połączenia z serwerem KKBus.');
      } finally {
        setIsLoading(false);
      }
    };
    fetchTransactions();
  }, []);

  if (isLoading) return <div className={styles.loading}>Pobieranie historii...</div>;
  if (error) return <div className={styles.error}>{error}</div>;

  return (
    <main className={styles.transactionsPage}>
      <header className={styles.headerSection}>
        <h2 className={styles.title}>Historia Transakcji</h2>
        <p className={styles.subtitle}>Przeglądaj swoje wydatki i doładowania portfela KKBus</p>
      </header>

      <div className={styles.listContainer}>
        {transactions.length > 0 ? (
          transactions.map(tx => (
            <div 
              key={tx.id} 
              className={`${styles.transactionItem} ${tx.type === 'INCOME' ? styles.income : styles.expense}`}
            >
              <div className={styles.typeIcon}>
                {tx.type === 'INCOME' ? '↓' : '↑'}
              </div>

              <div className={styles.details}>
                <strong>{tx.type === 'INCOME' ? 'Doładowanie konta' : 'Zakup biletu'}</strong>
                <div className={styles.tags}>
                  {tx.tags?.map(tag => (
                    <span key={tag} className={styles.tag}>{tag}</span>
                  )) || <span className={styles.tag}>Ogólne</span>}
                </div>
                {tx.notes && <span className={styles.notes}>{tx.notes}</span>}
              </div>

              <div className={styles.amount}>
                {tx.type === 'INCOME' ? '+' : '-'}{tx.amount.toFixed(2)} PLN
              </div>
            </div>
          ))
        ) : (
          <div className={styles.loading}>Brak zarejestrowanych transakcji.</div>
        )}
      </div>
    </main>
  );
};

export default TransactionsListPage;