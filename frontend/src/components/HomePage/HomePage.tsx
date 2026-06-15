import styles from "./HomePage.module.scss";
import { Link } from "react-router-dom";
import { useLanguage } from "../../context/LanguageContext";

const HomePage = () => {
  const { t } = useLanguage();

  return (
    <div className={styles.home}>
      <h1>KKBus</h1>
      <h3>{t("home.tagline")}</h3>
      <div className={styles.buttons}>
        <Link to="/route-search" className={styles.btn}>
          {t("nav.search")}
        </Link>
      </div>
    </div>
  );
};

export default HomePage;
