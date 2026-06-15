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
import { useLanguage } from "../../context/LanguageContext";
import styles from "./LoyaltyPage.module.scss";

interface ApiError {
  response?: {
    data?: {
      message?: string;
    };
  };
}

const LoyaltyPage = () => {
  const { locale, t } = useLanguage();
  const [points, setPoints] = useState(0);
  const [rewards, setRewards] = useState<LoyaltyReward[]>([]);
  const [redemptions, setRedemptions] = useState<RewardRedemption[]>([]);
  const [redeemingId, setRedeemingId] = useState<number | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const formatDate = (value: string) =>
    new Intl.DateTimeFormat(locale, {
      dateStyle: "medium",
      timeStyle: "short",
    }).format(new Date(value));

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
      toast.error(t("loyalty.loadError"));
    } finally {
      setIsLoading(false);
    }
  }, [t]);

  useEffect(() => {
    void loadData();
  }, [loadData]);

  const handleRedeem = async (reward: LoyaltyReward) => {
    try {
      setRedeemingId(reward.id);
      const redemption = await redeemReward(reward.id);
      toast.success(t("loyalty.redeemed", { name: redemption.rewardName }));
      await loadData();
    } catch (error: unknown) {
      const message = (error as ApiError).response?.data?.message;
      toast.error(message ?? t("loyalty.redeemError"));
    } finally {
      setRedeemingId(null);
    }
  };

  if (isLoading) {
    return <main className={styles.page}>{t("loyalty.loading")}</main>;
  }

  return (
    <main className={styles.page}>
      <header className={styles.header}>
        <div>
          <h1>{t("loyalty.title")}</h1>
          <p>{t("loyalty.subtitle")}</p>
        </div>
        <div className={styles.balance}>
          <span>{t("loyalty.points")}</span>
          <strong>{points} pkt</strong>
        </div>
      </header>

      <section>
        <h2>{t("loyalty.catalog")}</h2>
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
                    ? t("loyalty.redeeming")
                    : reward.affordable
                      ? t("loyalty.redeem")
                      : t("loyalty.notEnough")}
                </button>
              </div>
            </article>
          ))}
        </div>
      </section>

      <section className={styles.ownedSection}>
        <h2>{t("loyalty.owned")}</h2>
        {redemptions.length === 0 ? (
          <p className={styles.empty}>{t("loyalty.empty")}</p>
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
