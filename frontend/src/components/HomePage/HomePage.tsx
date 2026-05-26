import styles from "./HomePage.module.scss";
import { Link } from "react-router-dom";

const HomePage = () => {
  return (
    <div className={styles.home}>
      <h1>KKBus</h1>
      <h3>Tu łączą się nasze drogi!</h3>
      <div className={styles.buttons}>
        <Link to="/route-search" className={styles.btn}>Znajdź trasę</Link>
      </div>
    </div>
  );
};

export default HomePage;
