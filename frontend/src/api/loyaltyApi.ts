import axiosClient from "./axiosClient";

export interface LoyaltyReward {
  id: number;
  name: string;
  description: string;
  pointsCost: number;
  affordable: boolean;
}

export interface RewardRedemption {
  id: number;
  rewardName: string;
  description: string;
  pointsCost: number;
  redeemedAt: string;
  voucherCode: string;
}

export const getRewards = async (): Promise<LoyaltyReward[]> => {
  const response = await axiosClient.get<LoyaltyReward[]>("loyalty/rewards");
  return response.data;
};

export const getRewardRedemptions = async (): Promise<RewardRedemption[]> => {
  const response = await axiosClient.get<RewardRedemption[]>(
    "loyalty/redemptions",
  );
  return response.data;
};

export const redeemReward = async (
  rewardId: number,
): Promise<RewardRedemption> => {
  const response = await axiosClient.post<RewardRedemption>(
    `loyalty/rewards/${rewardId}/redeem`,
  );
  return response.data;
};
