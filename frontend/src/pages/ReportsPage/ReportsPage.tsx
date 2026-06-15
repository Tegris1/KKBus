import { FormEvent, useEffect, useState } from "react";
import { toast } from "react-toastify";
import { reportsApi } from "../../api/reportsApi";
import { useLanguage } from "../../context/LanguageContext";
import { ReportOptions, ReportPeriod, TicketReport } from "../../types/report";
import styles from "./ReportsPage.module.scss";

const localDate = () => {
  const now = new Date();
  const offset = now.getTimezoneOffset() * 60_000;
  return new Date(now.getTime() - offset).toISOString().slice(0, 10);
};

const ReportsPage = () => {
  const { locale, t } = useLanguage();
  const periodLabels: Record<ReportPeriod, string> = {
    DAILY: t("reports.daily"),
    WEEKLY: t("reports.weekly"),
    MONTHLY: t("reports.monthly"),
    YEARLY: t("reports.yearly"),
  };
  const [period, setPeriod] = useState<ReportPeriod>("MONTHLY");
  const [referenceDate, setReferenceDate] = useState(localDate());
  const [driverId, setDriverId] = useState("");
  const [busId, setBusId] = useState("");
  const [options, setOptions] = useState<ReportOptions>({ drivers: [], busIds: [] });
  const [report, setReport] = useState<TicketReport | null>(null);
  const [loading, setLoading] = useState(false);

  const formatMoney = (value: number) =>
    new Intl.NumberFormat(locale, {
      style: "currency",
      currency: "PLN",
    }).format(value);
  const formatDate = (value: string) =>
    new Intl.DateTimeFormat(locale, { dateStyle: "medium" }).format(new Date(value));
  const formatDateTime = (value: string) =>
    new Intl.DateTimeFormat(locale, {
      dateStyle: "medium",
      timeStyle: "short",
    }).format(new Date(value));

  useEffect(() => {
    const loadOptions = async () => {
      try {
        setOptions(await reportsApi.getOptions());
      } catch {
        toast.error(t("reports.optionsError"));
      }
    };

    void loadOptions();
  }, [t]);

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
      toast.error(t("reports.generateError"));
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className={styles.page}>
      <header className={styles.pageHeader}>
        <div>
          <p className={styles.eyebrow}>{t("reports.panel")}</p>
          <h1>{t("reports.title")}</h1>
          <p>{t("reports.subtitle")}</p>
        </div>
      </header>

      <form className={styles.filters} onSubmit={(event) => void generateReport(event)}>
        <label>
          {t("reports.type")}
          <select value={period} onChange={(event) => setPeriod(event.target.value as ReportPeriod)}>
            {Object.entries(periodLabels).map(([value, label]) => (
              <option key={value} value={value}>{label}</option>
            ))}
          </select>
        </label>

        <label>
          {t("reports.referenceDate")}
          <input
            type="date"
            value={referenceDate}
            onChange={(event) => setReferenceDate(event.target.value)}
            required
          />
        </label>

        <label>
          {t("reports.driver")}
          <select value={driverId} onChange={(event) => setDriverId(event.target.value)}>
            <option value="">{t("reports.allDrivers")}</option>
            {options.drivers.map((driver) => (
              <option key={driver.id} value={driver.id}>{driver.name}</option>
            ))}
          </select>
        </label>

        <label>
          {t("reports.vehicle")}
          <select value={busId} onChange={(event) => setBusId(event.target.value)}>
            <option value="">{t("reports.allVehicles")}</option>
            {options.busIds.map((id) => (
              <option key={id} value={id}>{t("reports.vehicle")} {id}</option>
            ))}
          </select>
        </label>

        <button type="submit" disabled={loading}>
          {loading ? t("reports.generating") : t("reports.generate")}
        </button>
      </form>

      {report ? (
        <section className={styles.report}>
          <div className={styles.reportHeader}>
            <div>
              <p className={styles.eyebrow}>KKBus</p>
              <h2>{t("reports.title")} - {periodLabels[report.periodType].toLowerCase()}</h2>
              <p>{formatDate(report.periodStart)} - {formatDate(report.periodEnd)}</p>
            </div>
            <div className={styles.reportActions}>
              <span>{t("reports.generated")}: {formatDateTime(report.generatedAt)}</span>
              <button type="button" onClick={() => window.print()}>
                {t("reports.print")}
              </button>
            </div>
          </div>

          <div className={styles.summary}>
            <article><span>{t("reports.courses")}</span><strong>{report.courseCount}</strong></article>
            <article><span>{t("reports.soldTickets")}</span><strong>{report.soldTickets}</strong></article>
            <article><span>{t("reports.passengers")}</span><strong>{report.passengerCount}</strong></article>
            <article><span>{t("reports.revenue")}</span><strong>{formatMoney(report.revenue)}</strong></article>
            <article><span>{t("reports.fuel")}</span><strong>{formatMoney(report.fuelCost)}</strong></article>
            <article className={report.profit < 0 ? styles.loss : styles.profit}>
              <span>{t("reports.result")}</span><strong>{formatMoney(report.profit)}</strong>
            </article>
          </div>

          {report.courses.length === 0 ? (
            <p className={styles.empty}>{t("reports.empty")}</p>
          ) : (
            <div className={styles.courseList}>
              {report.courses.map((course) => (
                <article key={`${course.routeId}-${course.departureTime}`} className={styles.course}>
                  <header>
                    <div>
                      <h3>{course.origin} {"->"} {course.destination}</h3>
                      <p>{formatDateTime(course.departureTime)}</p>
                    </div>
                    <div className={styles.assignment}>
                      <span>{t("reports.driver")}: <strong>{course.driverName}</strong></span>
                      <span>{t("reports.vehicle")}: <strong>{course.busId ?? t("common.none")}</strong></span>
                    </div>
                  </header>

                  <div className={styles.courseMetrics}>
                    <span>{t("reports.tickets")} <strong>{course.soldTickets}</strong></span>
                    <span>{t("reports.passengers")} <strong>{course.passengerCount}</strong></span>
                    <span>{t("reports.revenue")} <strong>{formatMoney(course.revenue)}</strong></span>
                    <span>{t("reports.fuel")} <strong>{formatMoney(course.fuelCost)}</strong></span>
                    <span>{t("reports.result")} <strong>{formatMoney(course.profit)}</strong></span>
                  </div>

                  <table>
                    <thead>
                      <tr><th>{t("reports.segment")}</th><th>{t("reports.passengerCount")}</th></tr>
                    </thead>
                    <tbody>
                      {course.segments.map((segment, index) => (
                        <tr key={`${course.routeId}-${index}`}>
                          <td>{segment.origin} {"->"} {segment.destination}</td>
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
          {t("reports.placeholder")}
        </section>
      )}
    </main>
  );
};

export default ReportsPage;
