import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import {
  DriverPassengerCourse,
  reservationsApi,
} from "../../api/reservationsApi";
import { useLanguage } from "../../context/LanguageContext";
import styles from "./DriverPassengerListPage.module.scss";

const DriverPassengerListPage = () => {
  const { locale, t } = useLanguage();
  const [courses, setCourses] = useState<DriverPassengerCourse[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const loadPassengerLists = async () => {
      setIsLoading(true);

      try {
        setCourses(await reservationsApi.getDriverPassengerLists());
      } catch (error) {
        console.error("Error loading passenger lists:", error);
        toast.error(t("driverPassengers.loadError"));
      } finally {
        setIsLoading(false);
      }
    };

    void loadPassengerLists();
  }, [t]);

  const formatDateTime = (value: string) =>
    new Intl.DateTimeFormat(locale, {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    }).format(new Date(value));

  const printPassengerLists = () => {
    window.print();
  };

  const passengerCount = courses.reduce(
    (sum, course) =>
      sum +
      course.passengers.reduce(
        (courseSum, passenger) => courseSum + Number(passenger.seats ?? 1),
        0,
      ),
    0,
  );

  return (
    <main className={styles.page}>
      <header className={styles.header}>
        <div>
          <p className={styles.label}>{t("driverPassengers.panel")}</p>
          <h1>{t("driverPassengers.title")}</h1>
          <p>
            {t("driverPassengers.subtitle", {
              courses: courses.length,
              passengers: passengerCount,
            })}
          </p>
        </div>
        <button
          type="button"
          className={styles.printButton}
          onClick={printPassengerLists}
          disabled={courses.length === 0}
        >
          {t("driverPassengers.print")}
        </button>
      </header>

      {isLoading && <p className={styles.state}>{t("driverPassengers.loading")}</p>}

      {!isLoading && courses.length === 0 && (
        <p className={styles.state}>{t("driverPassengers.empty")}</p>
      )}

      {!isLoading && courses.length > 0 && (
        <section className={styles.courseList}>
          {courses.map((course) => (
            <article
              key={`${course.routeId}-${course.departureTime}`}
              className={styles.courseCard}
            >
              <header className={styles.courseHeader}>
                <div>
                  <h2>
                    {course.origin} - {course.destination}
                  </h2>
                  <p>
                    {formatDateTime(course.departureTime)} |{" "}
                    {t("driverPassengers.bus")} {course.busId ?? t("common.none")}
                  </p>
                </div>
                <span>
                  {t("driverPassengers.seatsSummary", {
                    reserved: course.passengers.reduce(
                      (sum, passenger) => sum + Number(passenger.seats ?? 1),
                      0,
                    ),
                    total: course.totalSeats ?? "-",
                  })}
                </span>
              </header>

              <div className={styles.tableWrapper}>
                <table>
                  <thead>
                    <tr>
                      <th>{t("driverPassengers.passenger")}</th>
                      <th>{t("driverPassengers.contact")}</th>
                      <th>{t("driverPassengers.segment")}</th>
                      <th>{t("driverPassengers.seats")}</th>
                      <th>{t("driverPassengers.discount")}</th>
                    </tr>
                  </thead>
                  <tbody>
                    {course.passengers.map((passenger) => (
                      <tr key={passenger.reservationId}>
                        <td>{passenger.passengerName}</td>
                        <td>
                          <span>{passenger.email}</span>
                          <small>{passenger.phoneNumber ?? t("common.none")}</small>
                        </td>
                        <td>
                          {(passenger.boardingStop ?? course.origin) +
                            " - " +
                            (passenger.alightingStop ?? course.destination)}
                        </td>
                        <td>{passenger.seats}</td>
                        <td>{passenger.discountType ?? t("common.none")}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </article>
          ))}
        </section>
      )}
    </main>
  );
};

export default DriverPassengerListPage;
