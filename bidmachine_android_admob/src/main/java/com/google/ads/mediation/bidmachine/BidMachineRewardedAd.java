package com.google.ads.mediation.bidmachine;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationRewardedAd;
import com.google.android.gms.ads.mediation.MediationRewardedAdCallback;
import com.google.android.gms.ads.mediation.MediationRewardedAdConfiguration;
import com.google.android.gms.ads.rewarded.RewardItem;

import io.bidmachine.AdsType;
import io.bidmachine.rewarded.RewardedAd;
import io.bidmachine.rewarded.RewardedListener;
import io.bidmachine.rewarded.RewardedRequest;
import io.bidmachine.utils.BMError;

public class BidMachineRewardedAd implements MediationRewardedAd, RewardedListener {

    private static final String TAG = BidMachineRewardedAd.class.getSimpleName();

    @Nullable
    private MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> mediationAdLoadCallback;
    @Nullable
    private MediationRewardedAdCallback mediationRewardedAdCallback;
    @Nullable
    private RewardedAd rewardedAd;

    public void loadAd(MediationRewardedAdConfiguration adConfiguration,
                       MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> mediationAdLoadCallback) {
        Context context = adConfiguration.getContext();
        if (context == null) {
            BidMachineUtils.onAdFailedToLoad(mediationAdLoadCallback,
                                             AdRequest.ERROR_CODE_INVALID_REQUEST,
                                             "Failed to request ad. Context is null");
            return;
        }

        Bundle serverExtras = BidMachineUtils.findServerExtras(adConfiguration);
        Bundle localExtras = adConfiguration.getMediationExtras();
        if (BidMachineUtils.isPreBidIntegration(localExtras)
                && !BidMachineUtils.isServerExtrasValid(serverExtras, localExtras)) {
            BidMachineUtils.onAdFailedToLoad(mediationAdLoadCallback,
                                             AdRequest.ERROR_CODE_INVALID_REQUEST,
                                             "Local or Server extras invalid");
            return;
        }
        Bundle fusedBundle = BidMachineUtils.getFusedBundle(serverExtras, localExtras);
        if (!BidMachineUtils.prepareBidMachine(context, fusedBundle, adConfiguration)) {
            BidMachineUtils.onAdFailedToLoad(mediationAdLoadCallback,
                                             AdRequest.ERROR_CODE_INVALID_REQUEST,
                                             "Check BidMachine integration");
            return;
        }

        RewardedRequest request;
        if (BidMachineUtils.isPreBidIntegration(fusedBundle)) {
            request = BidMachineUtils.obtainCachedRequest(AdsType.Rewarded, fusedBundle);
            if (request == null) {
                BidMachineUtils.onAdFailedToLoad(mediationAdLoadCallback,
                                                 AdRequest.ERROR_CODE_INVALID_REQUEST,
                                                 "Fetched AdRequest not found");
                return;
            } else {
                Log.d(TAG, "Fetched request resolved: " + request.getAuctionResult());
                request.notifyMediationWin();
            }
        } else {
            request = new RewardedRequest.Builder()
                    .setTargetingParams(BidMachineUtils.createTargetingParams(fusedBundle))
                    .setPriceFloorParams(BidMachineUtils.createPriceFloorParams(fusedBundle))
                    .build();
        }

        this.mediationAdLoadCallback = mediationAdLoadCallback;

        rewardedAd = new RewardedAd(context);
        rewardedAd.setListener(this);
        rewardedAd.load(request);
        Log.d(TAG, "Attempt load rewarded");
    }

    @Override
    public void showAd(Context context) {
        if (rewardedAd != null && rewardedAd.canShow()) {
            rewardedAd.show();
        } else {
            onAdShowFailed(BMError.Internal);
        }
    }

    public void destroy() {
        if (rewardedAd != null) {
            rewardedAd.setListener(null);
            rewardedAd.destroy();
            rewardedAd = null;
        }
        mediationRewardedAdCallback = null;
        mediationAdLoadCallback = null;
    }

    @Override
    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
        if (mediationAdLoadCallback != null) {
            mediationRewardedAdCallback = mediationAdLoadCallback.onSuccess(this);
        }
    }

    @Override
    public void onAdLoadFailed(@NonNull RewardedAd rewardedAd, @NonNull BMError bmError) {
        if (mediationAdLoadCallback != null) {
            BidMachineUtils.onAdFailedToLoad(mediationAdLoadCallback, bmError);
        }
    }

    @Override
    public void onAdShown(@NonNull RewardedAd rewardedAd) {
        if (mediationRewardedAdCallback != null) {
            mediationRewardedAdCallback.onAdOpened();
            mediationRewardedAdCallback.onVideoStart();
        }
    }

    @Override
    public void onAdShowFailed(@NonNull RewardedAd rewardedAd, @NonNull BMError bmError) {
        onAdShowFailed(bmError);
    }

    void onAdShowFailed(@NonNull BMError bmError) {
        String errorMessage = bmError.getMessage();
        Log.d(TAG, errorMessage);

        if (mediationRewardedAdCallback != null) {
            int adMobErrorCode = BidMachineUtils.transformToAdMobErrorCode(bmError);
            AdError adError = BidMachineUtils.createAdError(adMobErrorCode, errorMessage);
            mediationRewardedAdCallback.onAdFailedToShow(adError);
        }
    }

    @Override
    public void onAdImpression(@NonNull RewardedAd rewardedAd) {
        if (mediationRewardedAdCallback != null) {
            mediationRewardedAdCallback.reportAdImpression();
        }
    }

    @Override
    public void onAdClicked(@NonNull RewardedAd rewardedAd) {
        if (mediationRewardedAdCallback != null) {
            mediationRewardedAdCallback.reportAdClicked();
        }
    }

    @Override
    public void onAdClosed(@NonNull RewardedAd rewardedAd, boolean b) {
        if (mediationRewardedAdCallback != null) {
            mediationRewardedAdCallback.onAdClosed();
        }

        destroy();
    }

    @Override
    public void onAdRewarded(@NonNull RewardedAd rewardedAd) {
        if (mediationRewardedAdCallback != null) {
            mediationRewardedAdCallback.onVideoComplete();
            mediationRewardedAdCallback.onUserEarnedReward(new BidMachineReward());
        }
    }

    @Override
    public void onAdExpired(@NonNull RewardedAd rewardedAd) {
        if (mediationAdLoadCallback != null) {
            BidMachineUtils.onAdFailedToLoad(mediationAdLoadCallback, BMError.Expired);
        }
    }


    private static final class BidMachineReward implements RewardItem {

        @Override
        public String getType() {
            return "";
        }

        @Override
        public int getAmount() {
            return 0;
        }

    }

}