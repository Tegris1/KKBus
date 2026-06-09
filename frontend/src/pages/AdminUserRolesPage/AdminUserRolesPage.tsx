import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { usersApi } from "../../api/usersApi";
import { User, UserRole } from "../../types/user";
import styles from "./AdminUserRolesPage.module.scss";

const ROLES: UserRole[] = ["USER", "EMPLOYEE", "ADMIN"];

const AdminUserRolesPage = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [draftRoles, setDraftRoles] = useState<Record<number, UserRole>>({});
  const [loading, setLoading] = useState(true);
  const [savingUserId, setSavingUserId] = useState<number | null>(null);

  const loadUsers = async () => {
    setLoading(true);

    try {
      const data = await usersApi.getUsers();
      setUsers(data);
      setDraftRoles(
        data.reduce<Record<number, UserRole>>((roles, user) => {
          roles[user.id] = user.role ?? "USER";
          return roles;
        }, {}),
      );
    } catch (error) {
      console.error("Error loading users:", error);
      toast.error("Nie udalo sie pobrac uzytkownikow.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void loadUsers();
  }, []);

  const handleRoleChange = (userId: number, role: UserRole) => {
    setDraftRoles((current) => ({
      ...current,
      [userId]: role,
    }));
  };

  const handleSave = async (user: User) => {
    const role = draftRoles[user.id] ?? "USER";
    setSavingUserId(user.id);

    try {
      const updatedUser = await usersApi.updateRole(user.id, role);
      setUsers((current) =>
        current.map((item) => (item.id === updatedUser.id ? updatedUser : item)),
      );
      toast.success("Rola zostala zapisana.");
    } catch (error) {
      console.error("Error updating user role:", error);
      toast.error("Nie udalo sie zapisac roli.");
    } finally {
      setSavingUserId(null);
    }
  };

  return (
    <main className={styles.page}>
      <header className={styles.header}>
        <p className={styles.label}>Panel administratora</p>
        <h1>Role uzytkownikow</h1>
      </header>

      {loading ? (
        <p className={styles.state}>Ladowanie uzytkownikow...</p>
      ) : (
        <section className={styles.table}>
          {users.map((user) => {
            const selectedRole = draftRoles[user.id] ?? "USER";
            const isDirty = selectedRole !== (user.role ?? "USER");

            return (
              <article key={user.id} className={styles.row}>
                <div className={styles.user}>
                  <strong>{user.username}</strong>
                  <span>{user.email}</span>
                </div>

                <select
                  value={selectedRole}
                  onChange={(event) =>
                    handleRoleChange(user.id, event.target.value as UserRole)
                  }
                >
                  {ROLES.map((role) => (
                    <option key={role} value={role}>
                      {role}
                    </option>
                  ))}
                </select>

                <button
                  type="button"
                  onClick={() => void handleSave(user)}
                  disabled={!isDirty || savingUserId === user.id}
                >
                  {savingUserId === user.id ? "Zapisywanie..." : "Zapisz"}
                </button>
              </article>
            );
          })}
        </section>
      )}
    </main>
  );
};

export default AdminUserRolesPage;
