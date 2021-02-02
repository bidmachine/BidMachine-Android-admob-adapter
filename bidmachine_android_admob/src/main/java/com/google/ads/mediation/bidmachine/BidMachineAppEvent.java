package com.google.ads.mediation.bidmachine;

import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AppEventListener;

public class BidMachineAppEvent implements AppEventListener {

    @NonNull
    private final ResultListener listener;
    @NonNull
    private final String bidMachineKey;
    private final long delay;
    @Nullable
    private final AppEventListener baseAppEventListener;

    private final Handler handler = new Handler();
    private final Runnable delayRunnable = new Runnable() {
        @Override
        public void run() {
            sendResult(false);
        }
    };
    private boolean resultSent = false;

    public BidMachineAppEvent(@NonNull ResultListener listener,
                              @NonNull String bidMachineKey,
                              long delay,
                              @Nullable AppEventListener baseAppEventListener) {
        this.listener = listener;
        this.bidMachineKey = bidMachineKey;
        this.delay = delay;
        this.baseAppEventListener = baseAppEventListener;
    }

    @Override
    public void onAppEvent(String key, String value) {
        if (baseAppEventListener != null) {
            baseAppEventListener.onAppEvent(key, value);
        }
        sendResult(TextUtils.equals(bidMachineKey, key));
    }

    private void sendResult(boolean result) {
        stop();

        if (!resultSent) {
            resultSent = true;
            listener.onResult(result);
        }
    }

    private void start() {
        handler.postDelayed(delayRunnable, delay);
    }

    private void stop() {
        handler.removeCallbacks(delayRunnable);
    }


    public static void setListener(@NonNull AdManagerAdView adManagerAdView,
                                   @NonNull ResultListener listener,
                                   @NonNull String bidMachineKey,
                                   long delay) {
        AppEventListener newAppEventListener = createAppEventListener(adManagerAdView.getAppEventListener(),
                                                                      listener,
                                                                      bidMachineKey,
                                                                      delay);
        adManagerAdView.setAppEventListener(newAppEventListener);
    }

    public static void setListener(@NonNull AdManagerInterstitialAd adManagerInterstitialAd,
                                   @NonNull ResultListener listener,
                                   @NonNull String bidMachineKey,
                                   long delay) {
        AppEventListener newAppEventListener = createAppEventListener(adManagerInterstitialAd.getAppEventListener(),
                                                                      listener,
                                                                      bidMachineKey,
                                                                      delay);
        adManagerInterstitialAd.setAppEventListener(newAppEventListener);
    }

    private static AppEventListener createAppEventListener(@NonNull AppEventListener appEventListener,
                                                           @NonNull ResultListener listener,
                                                           @NonNull String bidMachineKey,
                                                           long delay) {
        AppEventListener baseAppEventListener = findBaseAppEvent(appEventListener);
        BidMachineAppEvent bidMachineAppEvent = new BidMachineAppEvent(listener,
                                                                       bidMachineKey,
                                                                       delay,
                                                                       baseAppEventListener);
        bidMachineAppEvent.start();
        return bidMachineAppEvent;
    }

    private static AppEventListener findBaseAppEvent(@Nullable AppEventListener appEventListener) {
        AppEventListener baseAppEventListener = appEventListener;
        if (baseAppEventListener instanceof BidMachineAppEvent) {
            baseAppEventListener = ((BidMachineAppEvent) baseAppEventListener).baseAppEventListener;
        }
        return baseAppEventListener;
    }

}