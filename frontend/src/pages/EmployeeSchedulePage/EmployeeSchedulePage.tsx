import { useEffect, useMemo, useState } from "react";
import { toast } from "react-toastify";
import { scheduleApi } from "../../api/scheduleApi";
import { Schedule } from "../../types/schedule";
import styles from "./EmployeeSchedulePage.module.scss";

const getTodayDate = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const day = String(now.getDate()).padStart(2, "0");

  return `${year}-${month}-${day}`;
};

const formatTime = (time: string) => time.slice(0, 5);

const getShiftDuration = (startTime: string, endTime: string) => {
  const [startHours, startMinutes] = startTime.split(":").map(Number);
  const [endHours, endMinutes] = endTime.split(":").map(Number);
  const start = startHours * 60 + startMinutes;
  const end = endHours * 60 + endMinutes;
  const duration = Math.max(end - start, 0);
  const hours = Math.floor(duration / 60);
  const minutes = duration % 60;

  if (hours === 0) {
    return `${minutes} min`;
  }

  if (minutes === 0) {
    return `${hours} h`;
  }

  return `${hours} h ${minutes} min`;
};

const EmployeeSchedulePage = () => {
  const [schedule, setSchedule] = useState<Schedule[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");
  const today = useMemo(getTodayDate, []);

  useEffect(() => {
    const fetchSchedule = async () => {
      setIsLoading(true);
      setError("");

      try {
        const data = await scheduleApi.getMySchedule();
        setSchedule(data);
      } catch (requestError) {
        console.error("Error fetching employee schedule:", requestError);
        setError("Nie udalo sie pobrac grafiku.");
        toast.error("Nie udalo sie pobrac grafiku.");
      } finally {
        setIsLoading(false);
      }
    };

    void fetchSchedule();
  }, []);

  const todaysSchedule = useMemo(
    () =>
      schedule
        .filter((item) => item.workingDate === today)
        .sort((first, second) => first.startTime.localeCompare(second.startTime)),
    [schedule, today],
  );

  return (
    <main className={styles.page}>
      <header className={styles.header}>
        <p className={styles.label}>Grafik pracownika</p>
        <h1>Dzisiejszy grafik</h1>
        <p className={styles.date}>{today}</p>
      </header>

      {isLoading && <p className={styles.state}>Ladowanie grafiku...</p>}

      {!isLoading && error && <p className={styles.error}>{error}</p>}

      {!isLoading && !error && todaysSchedule.length === 0 && (
        <section className={styles.empty}>
          <h2>Brak zmian na dzisiaj</h2>
          <p>Nie masz przypisanego zadnego kursu w dzisiejszym grafiku.</p>
        </section>
      )}

      {!isLoading && !error && todaysSchedule.length > 0 && (
        <section className={styles.scheduleList}>
          {todaysSchedule.map((item) => (
            <article key={item.scheduleId} className={styles.scheduleCard}>
              <div className={styles.timeBlock}>
                <span>{formatTime(item.startTime)}</span>
                <small>{formatTime(item.endTime)}</small>
              </div>

              <div className={styles.details}>
                <h2>Autobus #{item.busId}</h2>
                <p>Czas pracy: {getShiftDuration(item.startTime, item.endTime)}</p>
              </div>
            </article>
          ))}
        </section>
      )}
    </main>
  );
};

export default EmployeeSchedulePage;
