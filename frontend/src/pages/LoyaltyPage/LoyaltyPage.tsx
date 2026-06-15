import { useCallback, useEffect, useState } from "react";
import { toast } from "react-toastify";
import { getWalletInfo } from "../../api/depositApi";
import {
  getRewardRedemptions,
  getRewards,
  LoyaltyReward,
  redeemReward,
  RewardRedemption,
} from "../../api/loyaltyApi";
import styles from "./LoyaltyPage.module.scss";

interface ApiError {
  response?: {
    data?: {
      message?: string;
    };
  };
}

const formatDate = (value: string) =>
  new Intl.DateTimeFormat("pl-PL", {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(new Date(value));

const LoyaltyPage = () => {
  const [points, setPoints] = useState(0);
  const [rewards, setRewards] = useState<LoyaltyReward[]>([]);
  const [redemptions, setRedemptions] = useState<RewardRedemption[]>([]);
  const [redeemingId, setRedeemingId] = useState<number | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const loadData = useCallback(async () => {
    try {
      const [wallet, rewardCatalog, ownedRewards] = await Promise.all([
        getWalletInfo(),
        getRewards(),
        getRewardRedemptions(),
      ]);
      setPoints(Number(wallet.points ?? 0));
      setRewards(rewardCatalog);
      setRedemptions(ownedRewards);
    } catch {
      toast.error("Nie udało się pobrać danych programu lojalnościowego.");
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    void loadData();
  }, [loadData]);

  const handleRedeem = async (reward: LoyaltyReward) => {
    try {
      setRedeemingId(reward.id);
      const redemption = await redeemReward(reward.id);
      toast.success(`Odebrano: ${redemption.rewardName}`);
      await loadData();
    } catch (error: unknown) {
      const message = (error as ApiError).response?.data?.message;
      toast.error(message ?? "Nie udało się odebrać nagrody.");
    } finally {
      setRedeemingId(null);
    }
  };

  if (isLoading) {
    return <main className={styles.page}>Ładowanie programu lojalnościowego...</main>;
  }

  return (
    <main className={styles.page}>
      <header className={styles.header}>
        <div>
          <h1>Program lojalnościowy</h1>
          <p>Za zakup biletu otrzymujesz 1 punkt za każde wydane 10 PLN.</p>
        </div>
        <div className={styles.balance}>
          <span>Twoje punkty</span>
          <strong>{points} pkt</strong>
        </div>
      </header>

      <section>
        <h2>Katalog nagród</h2>
        <div className={styles.rewardGrid}>
          {rewards.map((reward) => (
            <article className={styles.rewardCard} key={reward.id}>
              <div>
                <h3>{reward.name}</h3>
                <p>{reward.description}</p>
              </div>
              <div className={styles.rewardFooter}>
                <strong>{reward.pointsCost} pkt</strong>
                <button
                  type="button"
                  disabled={!reward.affordable || redeemingId !== null}
                  onClick={() => void handleRedeem(reward)}
                >
                  {redeemingId === reward.id
                    ? "Odbieranie..."
                    : reward.affordable
                      ? "Odbierz"
                      : "Za mało punktów"}
                </button>
              </div>
            </article>
          ))}
        </div>
      </section>

      <section className={styles.ownedSection}>
        <h2>Twoje odebrane nagrody</h2>
        {redemptions.length === 0 ? (
          <p className={styles.empty}>Nie masz jeszcze odebranych nagród.</p>
        ) : (
          <div className={styles.redemptionList}>
            {redemptions.map((redemption) => (
              <article className={styles.redemptionCard} key={redemption.id}>
                <div>
                  <h3>{redemption.rewardName}</h3>
                  <span>{formatDate(redemption.redeemedAt)}</span>
                </div>
                <code>{redemption.voucherCode}</code>
              </article>
            ))}
          </div>
        )}
      </section>
    </main>
  );
};

export default LoyaltyPage;
