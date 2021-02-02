package com.google.ads.mediation.bidmachine;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationRewardedAd;
import com.google.android.gms.ads.mediation.MediationRewardedAdCallback;
import com.google.android.gms.ads.mediation.MediationRewardedAdConfiguration;
import com.google.android.gms.ads.reward.mediation.MediationRewardedVideoAdAdapter;
import com.google.android.gms.ads.rewarded.RewardItem;

import io.bidmachine.AdsType;
import io.bidmachine.rewarded.RewardedAd;
import io.bidmachine.rewarded.RewardedListener;
import io.bidmachine.rewarded.RewardedRequest;
import io.bidmachine.utils.BMError;

public class BidMachineRewardedAd implements MediationRewardedAd {

    private static final String TAG = BidMachineRewardedAd.class.getSimpleName();

    private RewardedAd rewardedAd;

    public void loadAd(MediationRewardedAdConfiguration mediationRewardedAdConfiguration,
                       MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> mediationAdLoadCallback) {
        Context context = mediationRewardedAdConfiguration.getContext();
        if (context == null) {
            Log.d(TAG, "Failed to request ad. Context is null");
            mediationAdLoadCallback.onFailure(new AdError(AdRequest.ERROR_CODE_INVALID_REQUEST,
                                                          "Context is null",
                                                          AdError.UNDEFINED_DOMAIN));
            return;
        }

        String serverParameters = BidMachineUtils.getString(mediationRewardedAdConfiguration.getServerParameters(),
                                                            MediationRewardedVideoAdAdapter.CUSTOM_EVENT_SERVER_PARAMETER_FIELD);
        Bundle serverParameterExtras = BidMachineUtils.transformToBundle(serverParameters);
        Bundle localExtras = mediationRewardedAdConfiguration.getMediationExtras();
        if (BidMachineUtils.isPreBidIntegration(localExtras)
                && !BidMachineUtils.isServerExtrasValid(serverParameterExtras, localExtras)) {
            Log.d(TAG, "Server parameters are configured incorrectly");
            mediationAdLoadCallback.onFailure(new AdError(AdRequest.ERROR_CODE_NO_FILL,
                                                          "Server parameters are configured incorrectly",
                                                          AdError.UNDEFINED_DOMAIN));
            return;
        }
        Bundle fusedBundle = BidMachineUtils.getFusedBundle(serverParameterExtras, localExtras);
        if (!BidMachineUtils.prepareBidMachine(context,
                                               fusedBundle,
                                               mediationRewardedAdConfiguration)) {
            mediationAdLoadCallback.onFailure(new AdError(AdRequest.ERROR_CODE_INVALID_REQUEST,
                                                          "prepareBidMachine ended with false",
                                                          AdError.UNDEFINED_DOMAIN));
            return;
        }

        RewardedRequest request;
        AdError adError = new AdError(AdRequest.ERROR_CODE_INVALID_REQUEST,
                                      "RewardedAd failed to load",
                                      AdError.UNDEFINED_DOMAIN);
        if (BidMachineUtils.isPreBidIntegration(fusedBundle)) {
            request = BidMachineUtils.obtainCachedRequest(AdsType.Rewarded, fusedBundle);
            if (request == null) {
                adError = new AdError(AdRequest.ERROR_CODE_NO_FILL,
                                      "Fetched AdRequest not found",
                                      AdError.UNDEFINED_DOMAIN);
                Log.d(TAG, "Fetched AdRequest not found");
            } else {
                request.notifyMediationWin();
                Log.d(TAG, "Fetched request resolved: " + request.getAuctionResult());
            }
        } else {
            request = new RewardedRequest.Builder()
                    .setTargetingParams(BidMachineUtils.createTargetingParams(fusedBundle))
                    .setPriceFloorParams(BidMachineUtils.createPriceFloorParams(fusedBundle))
                    .build();
        }
        if (request != null) {
            rewardedAd = new RewardedAd(context);
            rewardedAd.setListener(new Listener(this, mediationAdLoadCallback));
            rewardedAd.load(request);
            Log.d(TAG, "Attempt load rewarded");
        } else {
            mediationAdLoadCallback.onFailure(adError);
        }
    }

    @Override
    public void showAd(Context context) {
        if (rewardedAd != null && rewardedAd.canShow()) {
            rewardedAd.show();
        }
    }

    public void destroy() {
        if (rewardedAd != null) {
            rewardedAd.setListener(null);
            rewardedAd.destroy();
            rewardedAd = null;
        }
    }


    private static class Listener implements RewardedListener {

        private final BidMachineRewardedAd bidMachineRewardedAd;
        private final MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> mediationAdLoadCallback;
        private MediationRewardedAdCallback mediationRewardedAdCallback;

        Listener(BidMachineRewardedAd bidMachineRewardedAd,
                 MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> mediationAdLoadCallback) {
            this.bidMachineRewardedAd = bidMachineRewardedAd;
            this.mediationAdLoadCallback = mediationAdLoadCallback;
        }

        @Override
        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
            mediationRewardedAdCallback = mediationAdLoadCallback.onSuccess(bidMachineRewardedAd);
        }

        @Override
        public void onAdLoadFailed(@NonNull RewardedAd rewardedAd, @NonNull BMError bmError) {
            onError(bmError);
        }

        @Override
        public void onAdShown(@NonNull RewardedAd rewardedAd) {
            mediationRewardedAdCallback.onAdOpened();
            mediationRewardedAdCallback.onVideoStart();
        }

        @Override
        public void onAdShowFailed(@NonNull RewardedAd rewardedAd, @NonNull BMError bmError) {
            mediationRewardedAdCallback.onAdFailedToShow(new AdError(AdRequest.ERROR_CODE_INTERNAL_ERROR,
                                                                     bmError.getMessage(),
                                                                     AdError.UNDEFINED_DOMAIN));
        }

        @Override
        public void onAdImpression(@NonNull RewardedAd rewardedAd) {
            mediationRewardedAdCallback.reportAdImpression();
        }

        @Override
        public void onAdClicked(@NonNull RewardedAd rewardedAd) {
            mediationRewardedAdCallback.reportAdClicked();
        }

        @Override
        public void onAdClosed(@NonNull RewardedAd rewardedAd, boolean b) {
            mediationRewardedAdCallback.onAdClosed();

            bidMachineRewardedAd.destroy();
        }

        @Override
        public void onAdRewarded(@NonNull RewardedAd rewardedAd) {
            mediationRewardedAdCallback.onVideoComplete();
            mediationRewardedAdCallback.onUserEarnedReward(new BidMachineReward());
        }

        @Override
        public void onAdExpired(@NonNull RewardedAd rewardedAd) {
            onError(BMError.Expired);
        }

        private void onError(BMError bmError) {
            int errorCode = BidMachineUtils.transformToAdMobErrorCode(bmError);
            mediationAdLoadCallback.onFailure(new AdError(errorCode,
                                                          bmError.getMessage(),
                                                          AdError.UNDEFINED_DOMAIN));
            bidMachineRewardedAd.destroy();
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