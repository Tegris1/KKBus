import { FormEvent, useEffect, useState } from "react";
import { toast } from "react-toastify";
import { reportsApi } from "../../api/reportsApi";
import { ReportOptions, ReportPeriod, TicketReport } from "../../types/report";
import styles from "./ReportsPage.module.scss";

const PERIOD_LABELS: Record<ReportPeriod, string> = {
  DAILY: "Dzienny",
  WEEKLY: "Tygodniowy",
  MONTHLY: "Miesięczny",
  YEARLY: "Roczny",
};

const localDate = () => {
  const now = new Date();
  const offset = now.getTimezoneOffset() * 60_000;
  return new Date(now.getTime() - offset).toISOString().slice(0, 10);
};

const formatMoney = (value: number) =>
  new Intl.NumberFormat("pl-PL", {
    style: "currency",
    currency: "PLN",
  }).format(value);

const formatDate = (value: string) =>
  new Intl.DateTimeFormat("pl-PL", { dateStyle: "medium" }).format(new Date(value));

const formatDateTime = (value: string) =>
  new Intl.DateTimeFormat("pl-PL", {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(new Date(value));

const ReportsPage = () => {
  const [period, setPeriod] = useState<ReportPeriod>("MONTHLY");
  const [referenceDate, setReferenceDate] = useState(localDate());
  const [driverId, setDriverId] = useState("");
  const [busId, setBusId] = useState("");
  const [options, setOptions] = useState<ReportOptions>({ drivers: [], busIds: [] });
  const [report, setReport] = useState<TicketReport | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const loadOptions = async () => {
      try {
        setOptions(await reportsApi.getOptions());
      } catch {
        toast.error("Nie udało się pobrać filtrów raportu.");
      }
    };

    void loadOptions();
  }, []);

  const generateReport = async (event: FormEvent) => {
    event.preventDefault();
    setLoading(true);

    try {
      const generatedReport = await reportsApi.generate({
        period,
        referenceDate,
        driverId: driverId ? Number(driverId) : undefined,
        busId: busId ? Number(busId) : undefined,
      });
      setReport(generatedReport);
    } catch {
      toast.error("Nie udało się wygenerować raportu.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className={styles.page}>
      <header className={styles.pageHeader}>
        <div>
          <p className={styles.eyebrow}>Panel sekretarki</p>
          <h1>Raport sprzedaży biletów</h1>
          <p>Raport obejmuje kursy odjeżdżające w wybranym okresie.</p>
        </div>
      </header>

      <form className={styles.filters} onSubmit={(event) => void generateReport(event)}>
        <label>
          Typ raportu
          <select value={period} onChange={(event) => setPeriod(event.target.value as ReportPeriod)}>
            {Object.entries(PERIOD_LABELS).map(([value, label]) => (
              <option key={value} value={value}>{label}</option>
            ))}
          </select>
        </label>

        <label>
          Data odniesienia
          <input
            type="date"
            value={referenceDate}
            onChange={(event) => setReferenceDate(event.target.value)}
            required
          />
        </label>

        <label>
          Kierowca
          <select value={driverId} onChange={(event) => setDriverId(event.target.value)}>
            <option value="">Wszyscy kierowcy</option>
            {options.drivers.map((driver) => (
              <option key={driver.id} value={driver.id}>{driver.name}</option>
            ))}
          </select>
        </label>

        <label>
          Pojazd
          <select value={busId} onChange={(event) => setBusId(event.target.value)}>
            <option value="">Wszystkie pojazdy</option>
            {options.busIds.map((id) => (
              <option key={id} value={id}>Pojazd {id}</option>
            ))}
          </select>
        </label>

        <button type="submit" disabled={loading}>
          {loading ? "Generowanie..." : "Generuj podgląd"}
        </button>
      </form>

      {report ? (
        <section className={styles.report}>
          <div className={styles.reportHeader}>
            <div>
              <p className={styles.eyebrow}>KKBus</p>
              <h2>Raport {PERIOD_LABELS[report.periodType].toLowerCase()}</h2>
              <p>{formatDate(report.periodStart)} - {formatDate(report.periodEnd)}</p>
            </div>
            <div className={styles.reportActions}>
              <span>Wygenerowano: {formatDateTime(report.generatedAt)}</span>
              <button type="button" onClick={() => window.print()}>
                Drukuj / zapisz PDF
              </button>
            </div>
          </div>

          <div className={styles.summary}>
            <article><span>Kursy</span><strong>{report.courseCount}</strong></article>
            <article><span>Sprzedane bilety</span><strong>{report.soldTickets}</strong></article>
            <article><span>Pasażerowie</span><strong>{report.passengerCount}</strong></article>
            <article><span>Przychód</span><strong>{formatMoney(report.revenue)}</strong></article>
            <article><span>Koszt paliwa</span><strong>{formatMoney(report.fuelCost)}</strong></article>
            <article className={report.profit < 0 ? styles.loss : styles.profit}>
              <span>Wynik</span><strong>{formatMoney(report.profit)}</strong>
            </article>
          </div>

          {report.courses.length === 0 ? (
            <p className={styles.empty}>Brak kursów spełniających wybrane kryteria.</p>
          ) : (
            <div className={styles.courseList}>
              {report.courses.map((course) => (
                <article key={course.routeId} className={styles.course}>
                  <header>
                    <div>
                      <h3>{course.origin} → {course.destination}</h3>
                      <p>{formatDateTime(course.departureTime)}</p>
                    </div>
                    <div className={styles.assignment}>
                      <span>Kierowca: <strong>{course.driverName}</strong></span>
                      <span>Pojazd: <strong>{course.busId ?? "brak"}</strong></span>
                    </div>
                  </header>

                  <div className={styles.courseMetrics}>
                    <span>Bilety <strong>{course.soldTickets}</strong></span>
                    <span>Pasażerowie <strong>{course.passengerCount}</strong></span>
                    <span>Przychód <strong>{formatMoney(course.revenue)}</strong></span>
                    <span>Paliwo <strong>{formatMoney(course.fuelCost)}</strong></span>
                    <span>Wynik <strong>{formatMoney(course.profit)}</strong></span>
                  </div>

                  <table>
                    <thead>
                      <tr><th>Odcinek</th><th>Liczba pasażerów</th></tr>
                    </thead>
                    <tbody>
                      {course.segments.map((segment, index) => (
                        <tr key={`${course.routeId}-${index}`}>
                          <td>{segment.origin} → {segment.destination}</td>
                          <td>{segment.passengerCount}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </article>
              ))}
            </div>
          )}
        </section>
      ) : (
        <section className={styles.placeholder}>
          Wybierz kryteria i wygeneruj podgląd raportu.
        </section>
      )}
    </main>
  );
};

export default ReportsPage;
