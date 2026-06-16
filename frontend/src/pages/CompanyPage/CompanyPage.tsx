import { useEffect, useState } from "react";
import { companyApi, CompanyInfo, Vehicle } from "../../api/companyApi";
import { useLanguage } from "../../context/LanguageContext";
import styles from "./CompanyPage.module.scss";

const CompanyPage = () => {
  const { t } = useLanguage();
  const [info, setInfo] = useState<CompanyInfo | null>(null);
  const [vehicles, setVehicles] = useState<Vehicle[]>([]);

  useEffect(() => {
    const loadData = async () => {
      const [companyInfo, vehicleData] = await Promise.all([
        companyApi.getInfo(),
        companyApi.getVehicles(),
      ]);
      setInfo(companyInfo);
      setVehicles(vehicleData);
    };

    void loadData();
  }, []);

  if (!info) {
    return <main className={styles.page}>{t("company.loading")}</main>;
  }

  return (
      <main className={styles.page}>
        <header className={styles.header}>
          <p>{t("company.publicInfo")}</p>
          <h1>{info.name}</h1>
          <span>{t("company.description")}</span>
        </header>

        <section className={styles.grid}>
          <article>
            <h2>{t("company.contact")}</h2>
            <p><strong>{t("company.owner")}:</strong> {info.owner}</p>
            <p><strong>{t("company.address")}:</strong> {info.address}</p>
            <p><strong>{t("company.phone")}:</strong> {info.phone}</p>
            <p><strong>{t("company.fax")}:</strong> {info.fax}</p>
          </article>
          <article>
            <h2>{t("company.employees")}</h2>
            <p><strong>{t("company.drivers")}:</strong> {info.drivers.join(", ")}</p>
            <p><strong>{t("company.secretariat")}:</strong> {info.secretariat.join(", ")}</p>
          </article>
        </section>

        <section className={styles.vehicles}>
          <h2>{t("company.vehicles")}</h2>
          <div className={styles.vehicleGrid}>
            {vehicles.map((vehicle) => (
              <article key={vehicle.id}>
                <h3>{vehicle.name}</h3>
                <p>{t("company.fleetNumber")}: {vehicle.fleetNumber}</p>
                <p>{t("company.seats")}: {vehicle.seats}</p>
                <p>{t("company.status")}: {t(`vehicleStatus.${vehicle.status}`)}</p>
                <p>{t("company.parking")}: {vehicle.parkingLocation}</p>
              </article>
            ))}
        </div>
      </section>
    </main>
  );
};

export default CompanyPage;
