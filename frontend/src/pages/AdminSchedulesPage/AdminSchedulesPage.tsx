import { FormEvent, useEffect, useMemo, useState } from "react";
import { toast } from "react-toastify";
import { scheduleApi } from "../../api/scheduleApi";
import { usersApi } from "../../api/usersApi";
import { Schedule, ScheduleRequest } from "../../types/schedule";
import { User } from "../../types/user";
import styles from "./AdminSchedulesPage.module.scss";

const EMPTY_FORM: ScheduleRequest = {
  employeeId: 0,
  busId: 0,
  workingDate: "",
  startTime: "",
  endTime: "",
};

const AdminSchedulesPage = () => {
  const [schedules, setSchedules] = useState<Schedule[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [form, setForm] = useState<ScheduleRequest>(EMPTY_FORM);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const employees = useMemo(
    () => users.filter((user) => user.role === "EMPLOYEE"),
    [users],
  );

  const employeeNames = useMemo(
    () =>
      users.reduce<Record<number, string>>((names, user) => {
        names[user.id] = user.username;
        return names;
      }, {}),
    [users],
  );

  const loadData = async () => {
    setLoading(true);

    try {
      const [scheduleData, userData] = await Promise.all([
        scheduleApi.getAll(),
        usersApi.getUsers(),
      ]);
      setSchedules(scheduleData);
      setUsers(userData);
    } catch (error) {
      console.error("Error loading schedule management data:", error);
      toast.error("Nie udalo sie pobrac grafikow.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void loadData();
  }, []);

  const updateForm = <Key extends keyof ScheduleRequest>(
    key: Key,
    value: ScheduleRequest[Key],
  ) => {
    setForm((current) => ({
      ...current,
      [key]: value,
    }));
  };

  const resetForm = () => {
    setForm(EMPTY_FORM);
    setEditingId(null);
  };

  const handleEdit = (schedule: Schedule) => {
    setEditingId(schedule.scheduleId);
    setForm({
      employeeId: schedule.employeeId,
      busId: schedule.busId,
      workingDate: schedule.workingDate,
      startTime: schedule.startTime.slice(0, 5),
      endTime: schedule.endTime.slice(0, 5),
    });
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();

    if (!form.employeeId || !form.busId) {
      toast.error("Wybierz pracownika i podaj numer autobusu.");
      return;
    }

    setSaving(true);

    try {
      const saved = editingId
        ? await scheduleApi.update(editingId, form)
        : await scheduleApi.create(form);

      setSchedules((current) =>
        editingId
          ? current.map((item) => (item.scheduleId === saved.scheduleId ? saved : item))
          : [...current, saved],
      );
      toast.success(editingId ? "Grafik zostal zaktualizowany." : "Grafik zostal dodany.");
      resetForm();
    } catch (error) {
      console.error("Error saving schedule:", error);
      toast.error("Nie udalo sie zapisac grafiku.");
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (scheduleId: number) => {
    try {
      await scheduleApi.delete(scheduleId);
      setSchedules((current) =>
        current.filter((item) => item.scheduleId !== scheduleId),
      );
      toast.success("Grafik zostal usuniety.");
    } catch (error) {
      console.error("Error deleting schedule:", error);
      toast.error("Nie udalo sie usunac grafiku.");
    }
  };

  const sortedSchedules = useMemo(
    () =>
      [...schedules].sort((first, second) =>
        `${first.workingDate} ${first.startTime}`.localeCompare(
          `${second.workingDate} ${second.startTime}`,
        ),
      ),
    [schedules],
  );

  return (
    <main className={styles.page}>
      <header className={styles.header}>
        <p className={styles.label}>Panel administratora</p>
        <h1>Grafiki pracownikow</h1>
      </header>

      <form className={styles.form} onSubmit={(event) => void handleSubmit(event)}>
        <div className={styles.field}>
          <label htmlFor="employeeId">Pracownik</label>
          <select
            id="employeeId"
            value={form.employeeId}
            onChange={(event) => updateForm("employeeId", Number(event.target.value))}
            required
          >
            <option value={0}>Wybierz pracownika</option>
            {employees.map((employee) => (
              <option key={employee.id} value={employee.id}>
                {employee.username} ({employee.email})
              </option>
            ))}
          </select>
        </div>

        <div className={styles.field}>
          <label htmlFor="busId">Autobus</label>
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
          <label htmlFor="workingDate">Data</label>
          <input
            id="workingDate"
            type="date"
            value={form.workingDate}
            onChange={(event) => updateForm("workingDate", event.target.value)}
            required
          />
        </div>

        <div className={styles.field}>
          <label htmlFor="startTime">Start</label>
          <input
            id="startTime"
            type="time"
            value={form.startTime}
            onChange={(event) => updateForm("startTime", event.target.value)}
            required
          />
        </div>

        <div className={styles.field}>
          <label htmlFor="endTime">Koniec</label>
          <input
            id="endTime"
            type="time"
            value={form.endTime}
            onChange={(event) => updateForm("endTime", event.target.value)}
            required
          />
        </div>

        <div className={styles.actions}>
          <button type="submit" disabled={saving}>
            {saving ? "Zapisywanie..." : editingId ? "Zapisz zmiany" : "Dodaj grafik"}
          </button>
          {editingId && (
            <button type="button" className={styles.secondary} onClick={resetForm}>
              Anuluj
            </button>
          )}
        </div>
      </form>

      {loading ? (
        <p className={styles.state}>Ladowanie grafikow...</p>
      ) : (
        <section className={styles.list}>
          {sortedSchedules.map((schedule) => (
            <article key={schedule.scheduleId} className={styles.card}>
              <div>
                <strong>{employeeNames[schedule.employeeId] ?? `ID ${schedule.employeeId}`}</strong>
                <span>
                  {schedule.workingDate}, {schedule.startTime.slice(0, 5)}-
                  {schedule.endTime.slice(0, 5)}
                </span>
              </div>
              <p>Autobus #{schedule.busId}</p>
              <div className={styles.cardActions}>
                <button type="button" onClick={() => handleEdit(schedule)}>
                  Edytuj
                </button>
                <button type="button" onClick={() => void handleDelete(schedule.scheduleId)}>
                  Usun
                </button>
              </div>
            </article>
          ))}
        </section>
      )}
    </main>
  );
};

export default AdminSchedulesPage;
