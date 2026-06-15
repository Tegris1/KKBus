import { useEffect, useMemo, useState } from "react";
import { toast } from "react-toastify";
import { scheduleApi } from "../../api/scheduleApi";
import { Schedule, ScheduleDay, SCHEDULE_DAYS } from "../../types/schedule";
import styles from "./EmployeeSchedulePage.module.scss";

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

const createEmptyWeek = () =>
  SCHEDULE_DAYS.reduce<Record<ScheduleDay, Schedule[]>>((week, day) => {
    week[day.value] = [];
    return week;
  }, {} as Record<ScheduleDay, Schedule[]>);

const EmployeeSchedulePage = () => {
  const [schedule, setSchedule] = useState<Schedule[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");

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

  const weeklySchedule = useMemo(() => {
    const week = createEmptyWeek();

    schedule.forEach((item) => {
      if (week[item.dayOfWeek]) {
        week[item.dayOfWeek].push(item);
      }
    });

    SCHEDULE_DAYS.forEach((day) => {
      week[day.value].sort((first, second) =>
        first.startTime.localeCompare(second.startTime),
      );
    });

    return week;
  }, [schedule]);

  return (
    <main className={styles.page}>
      <header className={styles.header}>
        <p className={styles.label}>Grafik pracownika</p>
        <h1>Tygodniowy grafik</h1>
        <p className={styles.date}>Poniedzialek - Niedziela</p>
      </header>

      {isLoading && <p className={styles.state}>Ladowanie grafiku...</p>}

      {!isLoading && error && <p className={styles.error}>{error}</p>}

      {!isLoading && !error && (
        <section className={styles.weekGrid}>
          {SCHEDULE_DAYS.map((day) => (
            <article key={day.value} className={styles.dayCard}>
              <header className={styles.dayHeader}>
                <h2>{day.label}</h2>
                <span>{weeklySchedule[day.value].length} zmian</span>
              </header>

              {weeklySchedule[day.value].length === 0 ? (
                <p className={styles.emptyDay}>Brak zmian</p>
              ) : (
                <div className={styles.scheduleList}>
                  {weeklySchedule[day.value].map((item) => (
                    <div key={item.scheduleId} className={styles.scheduleCard}>
                      <div className={styles.timeBlock}>
                        <span>{formatTime(item.startTime)}</span>
                        <small>{formatTime(item.endTime)}</small>
                      </div>

                      <div className={styles.details}>
                        <h3>Autobus #{item.busId}</h3>
                        <p>
                          Czas pracy: {getShiftDuration(item.startTime, item.endTime)}
                        </p>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </article>
          ))}
        </section>
      )}
    </main>
  );
};

export default EmployeeSchedulePage;
