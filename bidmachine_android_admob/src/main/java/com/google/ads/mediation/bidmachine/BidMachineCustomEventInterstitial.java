package com.google.ads.mediation.bidmachine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitial;
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener;

import io.bidmachine.AdContentType;
import io.bidmachine.AdsType;
import io.bidmachine.BidMachineFetcher;
import io.bidmachine.interstitial.InterstitialAd;
import io.bidmachine.interstitial.InterstitialListener;
import io.bidmachine.interstitial.InterstitialRequest;
import io.bidmachine.utils.BMError;

public final class BidMachineCustomEventInterstitial implements CustomEventInterstitial {

    private static final String TAG = BidMachineCustomEventInterstitial.class.getSimpleName();

    private InterstitialAd interstitialAd;

    @Override
    public void requestInterstitialAd(Context context,
                                      CustomEventInterstitialListener customEventInterstitialListener,
                                      String serverParameters,
                                      MediationAdRequest mediationAdRequest,
                                      Bundle localExtras) {
        if (context == null) {
            Log.d(TAG, "Failed to request ad. Context is null");
            customEventInterstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }
        Bundle fusedBundle = BidMachineUtils.getFusedBundle(
                serverParameters,
                localExtras);
        BidMachineUtils.updateCoppa(
                fusedBundle,
                mediationAdRequest.taggedForChildDirectedTreatment());
        BidMachineUtils.updateGDPR(fusedBundle);
        if (!BidMachineUtils.prepareBidMachine(context, fusedBundle)) {
            customEventInterstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }
        InterstitialRequest request;
        if (fusedBundle.containsKey(BidMachineFetcher.KEY_ID)) {
            request = BidMachineUtils.obtainCachedRequest(AdsType.Interstitial, fusedBundle);
            if (request == null) {
                Log.d(TAG, "Fetched AdRequest not found");
            } else {
                Log.d(TAG, "Fetched request resolved: " + request.getAuctionResult());
            }
        } else {
            InterstitialRequest.Builder interstitialRequestBuilder = new InterstitialRequest.Builder()
                    .setTargetingParams(BidMachineUtils.createTargetingParams(fusedBundle))
                    .setPriceFloorParams(BidMachineUtils.createPriceFloorParams(fusedBundle));
            AdContentType adContentType = getAdContentType(fusedBundle);
            if (adContentType != null) {
                interstitialRequestBuilder.setAdContentType(adContentType);
            } else {
                Log.d(TAG, "ad_content_type not found, will be used default AdContentType");
            }
            request = interstitialRequestBuilder.build();
        }
        if (request != null) {
            interstitialAd = new InterstitialAd(context);
            interstitialAd.setListener(new BidMachineAdListener(customEventInterstitialListener));
            interstitialAd.load(request);
        } else {
            customEventInterstitialListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NO_FILL);
        }
    }

    @Override
    public void showInterstitial() {
        if (interstitialAd != null && interstitialAd.canShow()) {
            interstitialAd.show();
        }
    }

    @Override
    public void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.setListener(null);
            interstitialAd.destroy();
            interstitialAd = null;
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    private AdContentType getAdContentType(@NonNull Bundle extras) {
        try {
            String value = BidMachineUtils.getString(extras, BidMachineUtils.AD_CONTENT_TYPE);
            if (value == null) {
                return null;
            }
            if (AdContentType.All.name().equals(value)) {
                return AdContentType.All;
            } else if (AdContentType.Static.name().equals(value)) {
                return AdContentType.Static;
            } else if (AdContentType.Video.name().equals(value)) {
                return AdContentType.Video;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private static class BidMachineAdListener implements InterstitialListener {

        private final CustomEventInterstitialListener customEventInterstitialListener;

        BidMachineAdListener(CustomEventInterstitialListener customEventInterstitialListener) {
            this.customEventInterstitialListener = customEventInterstitialListener;
        }

        @Override
        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
            customEventInterstitialListener.onAdLoaded();
        }

        @Override
        public void onAdLoadFailed(@NonNull InterstitialAd interstitialAd,
                                   @NonNull BMError bmError) {
            customEventInterstitialListener.onAdFailedToLoad(
                    BidMachineUtils.transformToAdMobErrorCode(bmError));
        }

        @Override
        public void onAdShown(@NonNull InterstitialAd interstitialAd) {
            customEventInterstitialListener.onAdOpened();
        }

        @Override
        public void onAdShowFailed(@NonNull InterstitialAd interstitialAd,
                                   @NonNull BMError bmError) {

        }

        @Override
        public void onAdImpression(@NonNull InterstitialAd interstitialAd) {

        }

        @Override
        public void onAdClicked(@NonNull InterstitialAd interstitialAd) {
            customEventInterstitialListener.onAdClicked();
            customEventInterstitialListener.onAdLeftApplication();
        }

        @Override
        public void onAdClosed(@NonNull InterstitialAd interstitialAd, boolean b) {
            customEventInterstitialListener.onAdClosed();
        }

        @Override
        public void onAdExpired(@NonNull InterstitialAd interstitialAd) {
            customEventInterstitialListener.onAdFailedToLoad(
                    BidMachineUtils.transformToAdMobErrorCode(BMError.Expired));
        }
    }

}
