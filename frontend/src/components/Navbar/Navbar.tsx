import React from "react";
import { Link } from "react-router-dom";
import styles from "./Navbar.module.scss";
import { useAuth } from "../../context/AuthContext";
import { useLanguage } from "../../context/LanguageContext";

const Navbar: React.FC = () => {
  const {
    isAuthenticated,
    isEmployee,
    isSecretary,
    isAdmin,
    canManageRoutes,
    logout,
  } = useAuth();
  const { language, setLanguage, t } = useLanguage();
  const hasRoleLinks = isEmployee || isSecretary || isAdmin || canManageRoutes;

  return (
    <nav className={styles.navbar}>
      <h1 className={styles.logo}>
        <Link to="/">KKBus</Link>
      </h1>
      <ul className={styles["nav-list"]}>
        <div className={styles["nav-container"]}>
          <li>
            <Link to="/company">{t("nav.company")}</Link>
          </li>
          <li>
            <Link to="/route-search">{t("nav.search")}</Link>
          </li>
          {isAuthenticated && (
            <>
              <li>
                <Link to="/my-tickets">{t("nav.tickets")}</Link>
              </li>
              <li>
                <Link to="/deposit">{t("nav.wallet")}</Link>
              </li>
              <li>
                <Link to="/loyalty">{t("nav.rewards")}</Link>
              </li>
              {hasRoleLinks && (
                <span className={styles["role-links"]}>
                  {isEmployee && (
                    <>
                      <li>
                        <Link to="/employee-schedule">{t("nav.schedule")}</Link>
                      </li>
                      <li>
                        <Link to="/driver/passengers">
                          {t("nav.passengerLists")}
                        </Link>
                      </li>
                    </>
                  )}
                  {isAdmin && (
                    <>
                      <li>
                        <Link to="/admin/users">{t("nav.roles")}</Link>
                      </li>
                      <li>
                        <Link to="/admin/schedules">{t("nav.schedules")}</Link>
                      </li>
                    </>
                  )}
                  {(isSecretary || isAdmin) && (
                    <li>
                      <Link to="/reports">{t("nav.reports")}</Link>
                    </li>
                  )}
                  {canManageRoutes && (
                    <li>
                      <Link to="/routes/new">{t("nav.addRoute")}</Link>
                    </li>
                  )}
                </span>
              )}
              <li>
                <Link to="/transactions">{t("nav.history")}</Link>
              </li>
            </>
          )}
        </div>
        <div className={styles["nav-container"]}>
          {!isAuthenticated ? (
            <>
              <li>
                <Link to="/login">{t("nav.login")}</Link>
              </li>
              <li>
                <Link to="/register">{t("nav.register")}</Link>
              </li>
            </>
          ) : (
            <li>
              <button onClick={logout} className={styles.logout}>
                {t("nav.logout")}
              </button>
            </li>
          )}
          <li className={styles.languageSwitcher} aria-label={t("language.label")}>
            <button
              type="button"
              className={language === "pl" ? styles.activeLanguage : ""}
              onClick={() => setLanguage("pl")}
            >
              PL
            </button>
            <button
              type="button"
              className={language === "en" ? styles.activeLanguage : ""}
              onClick={() => setLanguage("en")}
            >
              EN
            </button>
          </li>
        </div>
      </ul>
    </nav>
  );
};

export default Navbar;
