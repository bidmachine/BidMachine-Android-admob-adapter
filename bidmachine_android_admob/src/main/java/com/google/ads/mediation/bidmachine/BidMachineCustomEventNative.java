package com.google.ads.mediation.bidmachine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.NativeMediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventNative;
import com.google.android.gms.ads.mediation.customevent.CustomEventNativeListener;

import java.lang.ref.WeakReference;

import io.bidmachine.MediaAssetType;
import io.bidmachine.nativead.NativeAd;
import io.bidmachine.nativead.NativeListener;
import io.bidmachine.nativead.NativeRequest;
import io.bidmachine.nativead.view.NativeMediaView;
import io.bidmachine.utils.BMError;

public class BidMachineCustomEventNative implements CustomEventNative {

    private static final String TAG = BidMachineCustomEventNative.class.getSimpleName();

    private NativeAd nativeAd;
    private NativeMediaView nativeMediaView;

    @Override
    public void requestNativeAd(Context context,
                                CustomEventNativeListener customEventNativeListener,
                                String serverParameters,
                                NativeMediationAdRequest nativeMediationAdRequest,
                                Bundle localExtras) {
        if (context == null) {
            Log.d(TAG, "Failed to request ad. Context is null");
            customEventNativeListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }

        Bundle fusedBundle = BidMachineUtils.getFusedBundle(
                serverParameters,
                localExtras);
        BidMachineUtils.updateCoppa(
                fusedBundle,
                nativeMediationAdRequest.taggedForChildDirectedTreatment());
        BidMachineUtils.updateGDPR(fusedBundle);
        if (!BidMachineUtils.prepareBidMachine(context, fusedBundle)) {
            customEventNativeListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }
        NativeRequest nativeRequest = new NativeRequest.Builder()
                .setTargetingParams(BidMachineUtils.createTargetingParams(fusedBundle))
                .setPriceFloorParams(BidMachineUtils.createPriceFloorParams(fusedBundle))
                .setMediaAssetTypes(MediaAssetType.All)
                .build();
        nativeAd = new NativeAd(context);
        nativeAd.setListener(new BidMachineAdListener(context, customEventNativeListener));
        nativeAd.load(nativeRequest);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        if (nativeMediaView != null) {
            nativeMediaView = null;
        }
        if (nativeAd != null) {
            nativeAd.destroy();
            nativeAd = null;
        }
    }

    private final class BidMachineAdListener implements NativeListener {

        private final WeakReference<Context> weakReference;
        private final CustomEventNativeListener customEventNativeListener;

        BidMachineAdListener(Context context, CustomEventNativeListener customEventNativeListener) {
            weakReference = new WeakReference<>(context);
            this.customEventNativeListener = customEventNativeListener;
        }

        @Override
        public void onAdLoaded(@NonNull NativeAd nativeAd) {
            Context context = weakReference.get();
            if (context != null) {
                nativeMediaView = new NativeMediaView(context);
                BidMachineNativeMapper mapper = new BidMachineNativeMapper(nativeAd,
                                                                           nativeMediaView);
                customEventNativeListener.onAdLoaded(mapper);
            } else {
                customEventNativeListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR);
            }
        }

        @Override
        public void onAdLoadFailed(@NonNull NativeAd nativeAd, @NonNull BMError bmError) {

        }

        @Override
        public void onAdShown(@NonNull NativeAd nativeAd) {
            customEventNativeListener.onAdOpened();
        }

        @Override
        public void onAdImpression(@NonNull NativeAd nativeAd) {
            customEventNativeListener.onAdImpression();
        }

        @Override
        public void onAdClicked(@NonNull NativeAd nativeAd) {
            customEventNativeListener.onAdClicked();
            customEventNativeListener.onAdLeftApplication();
        }

        @Override
        public void onAdExpired(@NonNull NativeAd nativeAd) {

        }

    }

}