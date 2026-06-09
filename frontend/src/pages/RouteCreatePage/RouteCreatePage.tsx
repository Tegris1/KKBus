import { FormEvent, useState } from "react";
import { toast } from "react-toastify";
import { routesApi } from "../../api/routesApi";
import { RouteRequest } from "../../types/route";
import styles from "./RouteCreatePage.module.scss";

const EMPTY_ROUTE: RouteRequest = {
  origin: "",
  departureTime: "",
  destination: "",
  arrivalTime: "",
  price: 0,
};

const RouteCreatePage = () => {
  const [form, setForm] = useState<RouteRequest>(EMPTY_ROUTE);
  const [saving, setSaving] = useState(false);

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

    if (form.arrivalTime <= form.departureTime) {
      toast.error("Czas przyjazdu musi byc pozniejszy niz czas odjazdu.");
      return;
    }

    setSaving(true);

    try {
      await routesApi.createRoute(form);
      toast.success("Trasa zostala dodana.");
      setForm(EMPTY_ROUTE);
    } catch (error) {
      console.error("Error creating route:", error);
      toast.error("Nie udalo sie dodac trasy.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <main className={styles.page}>
      <header className={styles.header}>
        <p className={styles.label}>Zarzadzanie trasami</p>
        <h1>Dodaj nowa trase</h1>
      </header>

      <form className={styles.form} onSubmit={(event) => void handleSubmit(event)}>
        <div className={styles.field}>
          <label htmlFor="origin">Miasto wyjazdu</label>
          <input
            id="origin"
            value={form.origin}
            onChange={(event) => updateForm("origin", event.target.value)}
            required
          />
        </div>

        <div className={styles.field}>
          <label htmlFor="destination">Miasto przyjazdu</label>
          <input
            id="destination"
            value={form.destination}
            onChange={(event) => updateForm("destination", event.target.value)}
            required
          />
        </div>

        <div className={styles.field}>
          <label htmlFor="departureTime">Odjazd</label>
          <input
            id="departureTime"
            type="datetime-local"
            value={form.departureTime}
            onChange={(event) => updateForm("departureTime", event.target.value)}
            required
          />
        </div>

        <div className={styles.field}>
          <label htmlFor="arrivalTime">Przyjazd</label>
          <input
            id="arrivalTime"
            type="datetime-local"
            value={form.arrivalTime}
            onChange={(event) => updateForm("arrivalTime", event.target.value)}
            required
          />
        </div>

        <div className={styles.field}>
          <label htmlFor="price">Cena</label>
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
          {saving ? "Dodawanie..." : "Dodaj trase"}
        </button>
      </form>
    </main>
  );
};

export default RouteCreatePage;
