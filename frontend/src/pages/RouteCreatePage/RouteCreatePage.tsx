import { FormEvent, useEffect, useState } from "react";
import { toast } from "react-toastify";
import { routesApi } from "../../api/routesApi";
import { usersApi } from "../../api/usersApi";
import { useLanguage } from "../../context/LanguageContext";
import { RouteRequest } from "../../types/route";
import { DriverOption } from "../../types/user";
import styles from "./RouteCreatePage.module.scss";

const EMPTY_ROUTE: RouteRequest = {
  origin: "",
  departureTime: "",
  destination: "",
  arrivalTime: "",
  intermediateStops: [],
  price: 0,
  driverId: 0,
  busId: 0,
  fuelCost: 0,
};

const RouteCreatePage = () => {
  const { t } = useLanguage();
  const [form, setForm] = useState<RouteRequest>(EMPTY_ROUTE);
  const [saving, setSaving] = useState(false);
  const [drivers, setDrivers] = useState<DriverOption[]>([]);

  useEffect(() => {
    const loadDrivers = async () => {
      try {
        setDrivers(await usersApi.getDrivers());
      } catch {
        toast.error(t("routeCreate.driversError"));
      }
    };

    void loadDrivers();
  }, [t]);

  const updateForm = <Key extends keyof RouteRequest>(
    key: Key,
    value: RouteRequest[Key],
  ) => {
    setForm((current) => ({
      ...current,
      [key]: value,
    }));
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    const origin = form.origin.trim();
    const destination = form.destination.trim();
    const intermediateStops = form.intermediateStops
      .map((stop) => stop.trim())
      .filter(Boolean);

    if (form.arrivalTime <= form.departureTime) {
      toast.error(t("routeCreate.timeError"));
      return;
    }

    if (
      intermediateStops.some(
        (stop) =>
          stop.toLowerCase() === origin.toLowerCase() ||
          stop.toLowerCase() === destination.toLowerCase(),
      )
    ) {
      toast.error(t("routeCreate.stopError"));
      return;
    }

    setSaving(true);

    try {
      await routesApi.createRoute({
        ...form,
        origin,
        destination,
        intermediateStops,
      });
      toast.success(t("routeCreate.success"));
      setForm(EMPTY_ROUTE);
    } catch (error) {
      console.error("Error creating route:", error);
      toast.error(t("routeCreate.error"));
    } finally {
      setSaving(false);
    }
  };

  const addIntermediateStop = () => {
    setForm((current) => ({
      ...current,
      intermediateStops: [...current.intermediateStops, ""],
    }));
  };

  const updateIntermediateStop = (index: number, value: string) => {
    setForm((current) => ({
      ...current,
      intermediateStops: current.intermediateStops.map((stop, stopIndex) =>
        stopIndex === index ? value : stop,
      ),
    }));
  };

  const removeIntermediateStop = (index: number) => {
    setForm((current) => ({
      ...current,
      intermediateStops: current.intermediateStops.filter(
        (_stop, stopIndex) => stopIndex !== index,
      ),
    }));
  };

  return (
    <main className={styles.page}>
      <header className={styles.header}>
        <p className={styles.label}>{t("routeCreate.section")}</p>
        <h1>{t("routeCreate.title")}</h1>
        <p>{t("routeCreate.subtitle")}</p>
      </header>

      <form className={styles.form} onSubmit={(event) => void handleSubmit(event)}>
        <div className={styles.field}>
          <label htmlFor="origin">{t("routeCreate.origin")}</label>
          <input
            id="origin"
            value={form.origin}
            onChange={(event) => updateForm("origin", event.target.value)}
            required
          />
        </div>

        <div className={styles.field}>
          <label htmlFor="destination">{t("routeCreate.destination")}</label>
          <input
            id="destination"
            value={form.destination}
            onChange={(event) => updateForm("destination", event.target.value)}
            required
          />
        </div>

        <section className={styles.stopsSection}>
          <div className={styles.stopsHeader}>
            <div>
              <h2>{t("routeCreate.stops")}</h2>
              <p>{t("routeCreate.stopsHint")}</p>
            </div>
            <button
              type="button"
              className={styles.addStopButton}
              onClick={addIntermediateStop}
            >
              {t("routeCreate.addStop")}
            </button>
          </div>

          {form.intermediateStops.length > 0 ? (
            <div className={styles.stopsList}>
              {form.intermediateStops.map((stop, index) => (
                <div className={styles.stopRow} key={`intermediate-stop-${index}`}>
                  <label htmlFor={`intermediateStop-${index}`}>
                    {t("routeCreate.stop", { number: index + 1 })}
                  </label>
                  <input
                    id={`intermediateStop-${index}`}
                    value={stop}
                    onChange={(event) =>
                      updateIntermediateStop(index, event.target.value)
                    }
                    placeholder={t("routeCreate.stopPlaceholder")}
                  />
                  <button
                    type="button"
                    className={styles.removeStopButton}
                    onClick={() => removeIntermediateStop(index)}
                    aria-label={t("routeCreate.removeStop")}
                  >
                    {t("routeCreate.removeStop")}
                  </button>
                </div>
              ))}
            </div>
          ) : (
            <p className={styles.emptyStopsHint}>{t("routeCreate.noStops")}</p>
          )}
        </section>

        <div className={styles.field}>
          <label htmlFor="departureTime">{t("routeCreate.departure")}</label>
          <input
            id="departureTime"
            type="datetime-local"
            value={form.departureTime}
            onChange={(event) => updateForm("departureTime", event.target.value)}
            required
          />
        </div>

        <div className={styles.field}>
          <label htmlFor="arrivalTime">{t("routeCreate.arrival")}</label>
          <input
            id="arrivalTime"
            type="datetime-local"
            value={form.arrivalTime}
            onChange={(event) => updateForm("arrivalTime", event.target.value)}
            required
          />
        </div>

        <div className={styles.field}>
          <label htmlFor="driverId">{t("routeCreate.driver")}</label>
          <select
            id="driverId"
            value={form.driverId || ""}
            onChange={(event) => updateForm("driverId", Number(event.target.value))}
            required
          >
            <option value="">{t("routeCreate.selectDriver")}</option>
            {drivers.map((driver) => (
              <option key={driver.id} value={driver.id}>
                {driver.name}
              </option>
            ))}
          </select>
        </div>

        <div className={styles.field}>
          <label htmlFor="busId">{t("routeCreate.bus")}</label>
          <input
            id="busId"
            type="number"
            min="1"
            value={form.busId || ""}
            onChange={(event) => updateForm("busId", Number(event.target.value))}
            required
          />
        </div>

        <div className={styles.field}>
          <label htmlFor="fuelCost">{t("routeCreate.fuel")}</label>
          <input
            id="fuelCost"
            type="number"
            min="0"
            step="0.01"
            value={form.fuelCost || ""}
            onChange={(event) => updateForm("fuelCost", Number(event.target.value))}
            required
          />
        </div>

        <div className={styles.field}>
          <label htmlFor="price">{t("routeCreate.price")}</label>
          <input
            id="price"
            type="number"
            min="1"
            step="0.01"
            value={form.price || ""}
            onChange={(event) => updateForm("price", Number(event.target.value))}
            required
          />
        </div>

        <button type="submit" disabled={saving}>
          {saving ? t("routeCreate.adding") : t("routeCreate.add")}
        </button>
      </form>
    </main>
  );
};

export default RouteCreatePage;
