package com.google.ads.mediation.bidmachine;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.OnContextChangedListener;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.mediation.MediationRewardedVideoAdAdapter;
import com.google.android.gms.ads.reward.mediation.MediationRewardedVideoAdListener;

import java.lang.ref.WeakReference;

import io.bidmachine.AdsType;
import io.bidmachine.rewarded.RewardedAd;
import io.bidmachine.rewarded.RewardedListener;
import io.bidmachine.rewarded.RewardedRequest;
import io.bidmachine.utils.BMError;

@Deprecated
public final class BidMachineMediationRewardedAdAdapter implements MediationRewardedVideoAdAdapter, OnContextChangedListener {

    private static final String TAG = BidMachineMediationRewardedAdAdapter.class.getSimpleName();

    private RewardedAd rewardedAd;
    private WeakReference<Context> contextWeakReference;
    private MediationRewardedVideoAdListener mediationRewardedVideoAdListener;

    @Override
    public void initialize(Context context,
                           MediationAdRequest mediationAdRequest,
                           String s,
                           MediationRewardedVideoAdListener mediationRewardedVideoAdListener,
                           Bundle mediationServerExtras,
                           Bundle localExtras) {
        if (context == null) {
            Log.d(TAG, "Failed to request ad. Context is null");
            mediationRewardedVideoAdListener.onInitializationFailed(this,
                                                                    AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }

        String serverParameters = BidMachineUtils.getString(mediationServerExtras,
                                                            CUSTOM_EVENT_SERVER_PARAMETER_FIELD);
        Bundle serverExtras = BidMachineUtils.transformToBundle(serverParameters);
        Bundle fusedBundle = BidMachineUtils.getFusedBundle(serverExtras, localExtras);
        if (!BidMachineUtils.prepareBidMachine(context, fusedBundle, mediationAdRequest)) {
            mediationRewardedVideoAdListener.onAdFailedToLoad(this,
                                                              AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }

        contextWeakReference = new WeakReference<>(context);
        this.mediationRewardedVideoAdListener = mediationRewardedVideoAdListener;
        mediationRewardedVideoAdListener.onInitializationSucceeded(this);
    }

    @Override
    public void loadAd(MediationAdRequest mediationAdRequest,
                       Bundle mediationServerExtras,
                       Bundle localExtras) {
        Context context = contextWeakReference != null ? contextWeakReference.get() : null;
        if (context == null) {
            Log.d(TAG, "Failed to request ad. Context is null");
            mediationRewardedVideoAdListener.onAdFailedToLoad(this,
                                                              AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }
        String serverParameters = BidMachineUtils.getString(mediationServerExtras,
                                                            CUSTOM_EVENT_SERVER_PARAMETER_FIELD);
        Bundle serverExtras = BidMachineUtils.transformToBundle(serverParameters);
        if (BidMachineUtils.isPreBidIntegration(localExtras)
                && !BidMachineUtils.isServerExtrasValid(serverExtras, localExtras)) {
            mediationRewardedVideoAdListener.onAdFailedToLoad(this,
                                                              AdRequest.ERROR_CODE_NO_FILL);
            return;
        }
        Bundle fusedBundle = BidMachineUtils.getFusedBundle(serverExtras, localExtras);
        if (!BidMachineUtils.prepareBidMachine(context, fusedBundle, mediationAdRequest)) {
            mediationRewardedVideoAdListener.onAdFailedToLoad(this,
                                                              AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }

        RewardedRequest request;
        int errorCode = AdRequest.ERROR_CODE_INVALID_REQUEST;
        if (BidMachineUtils.isPreBidIntegration(fusedBundle)) {
            request = BidMachineUtils.obtainCachedRequest(AdsType.Rewarded, fusedBundle);
            if (request == null) {
                errorCode = AdRequest.ERROR_CODE_NO_FILL;
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
            rewardedAd.setListener(new BidMachineAdListener(this,
                                                            mediationRewardedVideoAdListener));
            rewardedAd.load(request);
            Log.d(TAG, "Attempt load rewarded");
        } else {
            mediationRewardedVideoAdListener.onAdFailedToLoad(this,
                                                              errorCode);
        }
    }

    @Override
    public void showVideo() {
        if (rewardedAd != null && rewardedAd.canShow()) {
            rewardedAd.show();
        }
    }

    @Override
    public boolean isInitialized() {
        return contextWeakReference != null && mediationRewardedVideoAdListener != null;
    }

    @Override
    public void onDestroy() {
        if (rewardedAd != null) {
            rewardedAd.setListener(null);
            rewardedAd.destroy();
            rewardedAd = null;
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onContextChanged(Context context) {
        contextWeakReference = new WeakReference<>(context);
    }

    private static class BidMachineAdListener implements RewardedListener {

        private final MediationRewardedVideoAdAdapter mediationRewardedVideoAdAdapter;
        private final MediationRewardedVideoAdListener mediationRewardedVideoAdListener;

        BidMachineAdListener(MediationRewardedVideoAdAdapter mediationRewardedVideoAdAdapter,
                             MediationRewardedVideoAdListener mediationRewardedVideoAdListener) {
            this.mediationRewardedVideoAdAdapter = mediationRewardedVideoAdAdapter;
            this.mediationRewardedVideoAdListener = mediationRewardedVideoAdListener;
        }

        @Override
        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
            mediationRewardedVideoAdListener.onAdLoaded(mediationRewardedVideoAdAdapter);
        }

        @Override
        public void onAdLoadFailed(@NonNull RewardedAd rewardedAd,
                                   @NonNull BMError bmError) {
            mediationRewardedVideoAdListener.onAdFailedToLoad(
                    mediationRewardedVideoAdAdapter,
                    BidMachineUtils.transformToAdMobErrorCode(bmError)
            );
        }

        @Override
        public void onAdShown(@NonNull RewardedAd rewardedAd) {
            mediationRewardedVideoAdListener.onAdOpened(mediationRewardedVideoAdAdapter);
            mediationRewardedVideoAdListener.onVideoStarted(mediationRewardedVideoAdAdapter);
        }

        @Override
        public void onAdShowFailed(@NonNull RewardedAd rewardedAd,
                                   @NonNull BMError bmError) {
        }

        @Override
        public void onAdImpression(@NonNull RewardedAd rewardedAd) {

        }

        @Override
        public void onAdClicked(@NonNull RewardedAd rewardedAd) {
            mediationRewardedVideoAdListener.onAdClicked(mediationRewardedVideoAdAdapter);
            mediationRewardedVideoAdListener.onAdLeftApplication(mediationRewardedVideoAdAdapter);
        }

        @Override
        public void onAdClosed(@NonNull RewardedAd rewardedAd, boolean b) {
            mediationRewardedVideoAdListener.onAdClosed(mediationRewardedVideoAdAdapter);
        }

        @Override
        public void onAdRewarded(@NonNull RewardedAd rewardedAd) {
            mediationRewardedVideoAdListener.onVideoCompleted(mediationRewardedVideoAdAdapter);
            mediationRewardedVideoAdListener.onRewarded(mediationRewardedVideoAdAdapter,
                                                        new BidMachineReward());
        }

        @Override
        public void onAdExpired(@NonNull RewardedAd rewardedAd) {
            mediationRewardedVideoAdListener.onAdFailedToLoad(
                    mediationRewardedVideoAdAdapter,
                    BidMachineUtils.transformToAdMobErrorCode(BMError.Expired));
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
