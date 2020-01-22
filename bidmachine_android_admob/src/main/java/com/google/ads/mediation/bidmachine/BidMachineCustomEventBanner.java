package com.google.ads.mediation.bidmachine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventBanner;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;

import io.bidmachine.AdsType;
import io.bidmachine.BidMachineFetcher;
import io.bidmachine.banner.BannerListener;
import io.bidmachine.banner.BannerRequest;
import io.bidmachine.banner.BannerSize;
import io.bidmachine.banner.BannerView;
import io.bidmachine.utils.BMError;

public final class BidMachineCustomEventBanner implements CustomEventBanner {

    private static final String TAG = BidMachineCustomEventBanner.class.getSimpleName();

    private BannerView bannerView;

    @Override
    public void requestBannerAd(Context context,
                                CustomEventBannerListener customEventBannerListener,
                                String serverParameters,
                                AdSize adSize,
                                MediationAdRequest mediationAdRequest,
                                Bundle localExtras) {
        if (context == null) {
            Log.d(TAG, "Failed to request ad. Context is null");
            customEventBannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }
        if (adSize == null) {
            Log.d(TAG, "Failed to request ad. AdSize is null");
            customEventBannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
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
            customEventBannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }
        BannerRequest request = null;
        BannerSize bannerSize = null;
        int errorCode = -1;
        if (fusedBundle.containsKey(BidMachineFetcher.KEY_ID)) {
            request = BidMachineUtils.obtainCachedRequest(AdsType.Banner, fusedBundle);
            if (request == null) {
                Log.d(TAG, "Fetched AdRequest not found");
                errorCode = AdRequest.ERROR_CODE_NO_FILL;
            } else {
                bannerSize = request.getSize();
                Log.d(TAG, "Fetched request resolved: " + request.getAuctionResult());
            }
        } else {
            bannerSize = transformToBannerSize(adSize);
            if (bannerSize == null) {
                Log.d(TAG, "Failed to request ad. Input AdSize not supported");
                errorCode = AdRequest.ERROR_CODE_INVALID_REQUEST;
            } else {
                request = new BannerRequest.Builder()
                        .setSize(bannerSize)
                        .setTargetingParams(BidMachineUtils.createTargetingParams(fusedBundle))
                        .setPriceFloorParams(BidMachineUtils.createPriceFloorParams(fusedBundle))
                        .build();
            }
        }
        if (request != null) {
            bannerView = new BannerView(context);
            bannerView.setListener(new BidMachineAdListener(customEventBannerListener));
            bannerView.load(request);
            Log.d(TAG, "Load attempted with size " + bannerSize);
        } else {
            customEventBannerListener.onAdFailedToLoad(errorCode != -1
                                                               ? errorCode
                                                               : AdRequest.ERROR_CODE_INVALID_REQUEST);
        }
    }

    @Override
    public void onDestroy() {
        if (bannerView != null) {
            bannerView.setListener(null);
            bannerView.destroy();
            bannerView = null;
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    private BannerSize transformToBannerSize(@NonNull AdSize adSize) {
        switch (adSize.getWidth()) {
            case 300:
                return BannerSize.Size_300x250;
            case 320:
                return BannerSize.Size_320x50;
            case 728:
                return BannerSize.Size_728x90;
            default:
                return null;
        }
    }

    private static class BidMachineAdListener implements BannerListener {

        private final CustomEventBannerListener customEventBannerListener;

        BidMachineAdListener(CustomEventBannerListener customEventBannerListener) {
            this.customEventBannerListener = customEventBannerListener;
        }

        @Override
        public void onAdLoaded(@NonNull BannerView bannerView) {
            customEventBannerListener.onAdLoaded(bannerView);
        }

        @Override
        public void onAdLoadFailed(@NonNull BannerView bannerView,
                                   @NonNull BMError bmError) {
            customEventBannerListener.onAdFailedToLoad(
                    BidMachineUtils.transformToAdMobErrorCode(bmError));
        }

        @Override
        public void onAdShown(@NonNull BannerView bannerView) {

        }

        @Override
        public void onAdImpression(@NonNull BannerView bannerView) {

        }

        @Override
        public void onAdClicked(@NonNull BannerView bannerView) {
            customEventBannerListener.onAdClicked();
            customEventBannerListener.onAdOpened();
            customEventBannerListener.onAdLeftApplication();
        }

        @Override
        public void onAdExpired(@NonNull BannerView bannerView) {
            customEventBannerListener.onAdFailedToLoad(
                    BidMachineUtils.transformToAdMobErrorCode(BMError.Expired));
        }

    }

}
