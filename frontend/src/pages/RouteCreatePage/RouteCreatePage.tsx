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
  intermediateStops: [],
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
    const origin = form.origin.trim();
    const destination = form.destination.trim();
    const intermediateStops = form.intermediateStops
      .map((stop) => stop.trim())
      .filter(Boolean);

    if (form.arrivalTime <= form.departureTime) {
      toast.error("Czas przyjazdu musi byc pozniejszy niz czas odjazdu.");
      return;
    }

    if (
      intermediateStops.some(
        (stop) =>
          stop.toLowerCase() === origin.toLowerCase() ||
          stop.toLowerCase() === destination.toLowerCase(),
      )
    ) {
      toast.error("Przystanek posredni nie moze byc taki sam jak start lub cel.");
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
      toast.success("Trasa zostala dodana.");
      setForm(EMPTY_ROUTE);
    } catch (error) {
      console.error("Error creating route:", error);
      toast.error("Nie udalo sie dodac trasy.");
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

        <section className={styles.stopsSection}>
          <div className={styles.stopsHeader}>
            <div>
              <h2>Przystanki posrednie</h2>
              <p>Dodaj miasta pomiedzy wyjazdem a przyjazdem w kolejnosci przejazdu.</p>
            </div>
            <button
              type="button"
              className={styles.addStopButton}
              onClick={addIntermediateStop}
            >
              Dodaj przystanek
            </button>
          </div>

          {form.intermediateStops.length > 0 ? (
            <div className={styles.stopsList}>
              {form.intermediateStops.map((stop, index) => (
                <div className={styles.stopRow} key={`intermediate-stop-${index}`}>
                  <label htmlFor={`intermediateStop-${index}`}>
                    Przystanek {index + 1}
                  </label>
                  <input
                    id={`intermediateStop-${index}`}
                    value={stop}
                    onChange={(event) =>
                      updateIntermediateStop(index, event.target.value)
                    }
                    placeholder="Nazwa miasta lub przystanku"
                  />
                  <button
                    type="button"
                    className={styles.removeStopButton}
                    onClick={() => removeIntermediateStop(index)}
                    aria-label={`Usun przystanek ${index + 1}`}
                  >
                    Usun
                  </button>
                </div>
              ))}
            </div>
          ) : (
            <p className={styles.emptyStopsHint}>
              Brak przystankow posrednich dla tej trasy.
            </p>
          )}
        </section>

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
