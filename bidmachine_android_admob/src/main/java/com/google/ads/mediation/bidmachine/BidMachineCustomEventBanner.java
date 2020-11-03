package com.google.ads.mediation.bidmachine;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventBanner;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;

import io.bidmachine.AdsType;
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
            BidMachineUtils.onAdFailedToLoad(customEventBannerListener,
                                             AdRequest.ERROR_CODE_INVALID_REQUEST,
                                             "Failed to request ad. Context is null");
            return;
        }
        if (adSize == null) {
            BidMachineUtils.onAdFailedToLoad(customEventBannerListener,
                                             AdRequest.ERROR_CODE_INVALID_REQUEST,
                                             "Failed to request ad. AdSize is null");
            return;
        }
        Bundle serverExtras = BidMachineUtils.transformToBundle(serverParameters);
        if (BidMachineUtils.isPreBidIntegration(localExtras)
                && !BidMachineUtils.isServerExtrasValid(serverExtras, localExtras)) {
            BidMachineUtils.onAdFailedToLoad(customEventBannerListener,
                                             AdRequest.ERROR_CODE_INVALID_REQUEST,
                                             "Local or Server extras invalid");
            return;
        }
        Bundle fusedBundle = BidMachineUtils.getFusedBundle(serverExtras, localExtras);
        if (!BidMachineUtils.prepareBidMachine(context, fusedBundle, mediationAdRequest)) {
            BidMachineUtils.onAdFailedToLoad(customEventBannerListener,
                                             AdRequest.ERROR_CODE_INVALID_REQUEST,
                                             "Check BidMachine integration");
            return;
        }

        BannerRequest request;
        if (BidMachineUtils.isPreBidIntegration(fusedBundle)) {
            request = BidMachineUtils.obtainCachedRequest(AdsType.Banner, fusedBundle);
            if (request == null) {
                BidMachineUtils.onAdFailedToLoad(customEventBannerListener,
                                                 AdRequest.ERROR_CODE_INVALID_REQUEST,
                                                 "Fetched AdRequest not found");
                return;
            } else {
                Log.d(TAG, "Fetched request resolved: " + request.getAuctionResult());
                request.notifyMediationWin();
            }
        } else {
            BannerSize bannerSize = transformToBannerSize(adSize);
            if (bannerSize == null) {
                BidMachineUtils.onAdFailedToLoad(customEventBannerListener,
                                                 AdRequest.ERROR_CODE_INVALID_REQUEST,
                                                 "Input AdSize not supported");
                return;
            } else {
                request = new BannerRequest.Builder()
                        .setSize(bannerSize)
                        .setTargetingParams(BidMachineUtils.createTargetingParams(fusedBundle))
                        .setPriceFloorParams(BidMachineUtils.createPriceFloorParams(fusedBundle))
                        .build();
            }
        }

        bannerView = new BannerView(context);
        bannerView.setListener(new BidMachineAdListener(customEventBannerListener));
        bannerView.load(request);
        Log.d(TAG, "Attempt load banner with size - " + request.getSize());
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
            BidMachineUtils.onAdFailedToLoad(customEventBannerListener, bmError);
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
            BidMachineUtils.onAdFailedToLoad(customEventBannerListener, BMError.Expired);
        }

    }

}
