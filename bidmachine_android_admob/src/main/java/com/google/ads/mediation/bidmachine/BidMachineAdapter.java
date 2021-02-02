package com.google.ads.mediation.bidmachine;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.ads.AdFormat;
import com.google.android.gms.ads.mediation.Adapter;
import com.google.android.gms.ads.mediation.InitializationCompleteCallback;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationConfiguration;
import com.google.android.gms.ads.mediation.MediationRewardedAd;
import com.google.android.gms.ads.mediation.MediationRewardedAdCallback;
import com.google.android.gms.ads.mediation.MediationRewardedAdConfiguration;
import com.google.android.gms.ads.mediation.VersionInfo;
import com.google.android.gms.ads.reward.mediation.MediationRewardedVideoAdAdapter;

import java.util.List;

public final class BidMachineAdapter extends Adapter {

    private static final String TAG = BidMachineAdapter.class.getSimpleName();

    private BidMachineRewardedAd bidMachineRewardedAd;

    @Override
    public VersionInfo getSDKVersionInfo() {
        String versionString = io.bidmachine.BuildConfig.VERSION_NAME;
        String[] splits = versionString.split("\\.");
        int major = Integer.parseInt(splits[0]);
        int minor = Integer.parseInt(splits[1]);
        int micro = Integer.parseInt(splits[2]);
        return new VersionInfo(major, minor, micro);
    }

    @Override
    public VersionInfo getVersionInfo() {
        String versionString = BuildConfig.VERSION_NAME;
        String[] splits = versionString.split("\\.");
        int major = Integer.parseInt(splits[0]);
        int minor = Integer.parseInt(splits[1]);
        int micro = Integer.parseInt(splits[2]) * 100 + Integer.parseInt(splits[3]);
        return new VersionInfo(major, minor, micro);
    }

    @Override
    public void initialize(Context context,
                           InitializationCompleteCallback initializationCompleteCallback,
                           List<MediationConfiguration> mediationConfigurationList) {
        if (context == null) {
            Log.d(TAG, "Failed to request ad. Context is null");
            initializationCompleteCallback.onInitializationFailed("Context is null");
            return;
        }

        Bundle serverExtras = new Bundle();
        for (MediationConfiguration mediationConfiguration : mediationConfigurationList) {
            if (mediationConfiguration.getFormat() == AdFormat.REWARDED) {
                String serverParameters = BidMachineUtils.getString(mediationConfiguration.getServerParameters(),
                                                                    MediationRewardedVideoAdAdapter.CUSTOM_EVENT_SERVER_PARAMETER_FIELD);
                Bundle serverParameterExtras = BidMachineUtils.transformToBundle(serverParameters);
                if (serverParameterExtras != null) {
                    serverExtras.putAll(serverParameterExtras);
                }
            }
        }

        if (!BidMachineUtils.prepareBidMachine(context,
                                               serverExtras,
                                               (MediationRewardedAdConfiguration) null)) {
            initializationCompleteCallback.onInitializationFailed(
                    "prepareBidMachine ended with false");
            return;
        }

        initializationCompleteCallback.onInitializationSucceeded();
    }

    @Override
    public void loadRewardedAd(MediationRewardedAdConfiguration mediationRewardedAdConfiguration,
                               MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback> mediationAdLoadCallback) {
        bidMachineRewardedAd = new BidMachineRewardedAd();
        bidMachineRewardedAd.loadAd(mediationRewardedAdConfiguration, mediationAdLoadCallback);
    }

}