package com.google.ads.mediation.bidmachine;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.ads.formats.NativeAd.Image;
import com.google.android.gms.ads.formats.UnifiedNativeAdAssetNames;
import com.google.android.gms.ads.mediation.UnifiedNativeAdMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.bidmachine.nativead.NativeAd;
import io.bidmachine.nativead.view.NativeMediaView;

class BidMachineNativeMapper extends UnifiedNativeAdMapper {

    private static final String TAG = BidMachineNativeMapper.class.getSimpleName();

    private final NativeAd nativeAd;
    private final NativeMediaView nativeMediaView;

    BidMachineNativeMapper(final NativeAd nativeAd, final NativeMediaView nativeMediaView) {
        this.nativeAd = nativeAd;
        this.nativeMediaView = nativeMediaView;

        setHeadline(nativeAd.getTitle());
        setBody(nativeAd.getDescription());
        setCallToAction(nativeAd.getCallToAction());
        setStarRating((double) nativeAd.getRating());
        setIcon(new BidMachineImage(null));
        setImages(new ArrayList<Image>() {{
            add(new BidMachineImage(null));
        }});
        setMediaView(nativeMediaView);
        setAdChoicesContent(nativeAd.getProviderView(nativeMediaView.getContext()));
        setHasVideoContent(true);
        setOverrideImpressionRecording(true);
        setOverrideClickHandling(true);
    }

    @Override
    public void trackViews(View view,
                           Map<String, View> clickableAssetViews,
                           Map<String, View> nonClickableAssetViews) {
        if (view instanceof ViewGroup) {
            Set<View> clickableViews = new HashSet<>();
            ImageView iconView = null;
            boolean mediaViewContains = false;
            for (Map.Entry<String, View> clickableAssets : clickableAssetViews.entrySet()) {
                clickableViews.add(clickableAssets.getValue());
                String key = clickableAssets.getKey();
                if (key.equals(UnifiedNativeAdAssetNames.ASSET_ICON)) {
                    iconView = (ImageView) clickableAssets.getValue();
                } else if (key.equals(UnifiedNativeAdAssetNames.ASSET_MEDIA_VIDEO)) {
                    mediaViewContains = true;
                }
            }
            NativeMediaView nativeMediaView = mediaViewContains
                    ? this.nativeMediaView
                    : null;
            nativeAd.registerView((ViewGroup) view, iconView, nativeMediaView, clickableViews);
        } else {
            Log.w(TAG, "Failed to trackViews, View must be ViewGroup");
        }
    }


    @Override
    public void untrackView(View view) {
        nativeAd.unregisterView();
    }

    private static final class BidMachineImage extends Image {

        private final Uri uri;

        BidMachineImage(Uri uri) {
            this.uri = uri;
        }

        @Override
        public Drawable getDrawable() {
            return null;
        }

        @Override
        public Uri getUri() {
            return uri;
        }

        @Override
        public double getScale() {
            return 0;
        }
    }

}