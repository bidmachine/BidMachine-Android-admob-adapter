package com.google.ads.mediation.bidmachine;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.MediationRewardedAdConfiguration;
import com.google.android.gms.ads.mediation.customevent.CustomEventListener;
import com.google.android.gms.ads.rewarded.RewardedAd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import io.bidmachine.AdsType;
import io.bidmachine.BidMachine;
import io.bidmachine.BidMachineFetcher;
import io.bidmachine.PriceFloorParams;
import io.bidmachine.Publisher;
import io.bidmachine.TargetingParams;
import io.bidmachine.banner.BannerRequest;
import io.bidmachine.interstitial.InterstitialRequest;
import io.bidmachine.nativead.NativeRequest;
import io.bidmachine.rewarded.RewardedRequest;
import io.bidmachine.utils.BMError;
import io.bidmachine.utils.Gender;

public class BidMachineUtils {

    private static final String TAG = BidMachineUtils.class.getSimpleName();
    private static final String ERROR_DOMAIN = "com.google.ads.mediation.bidmachine";

    public static final String DEFAULT_BANNER_KEY = "bidmachine-banner";
    public static final String DEFAULT_INTERSTITIAL_KEY = "bidmachine-interstitial";
    public static final String DEFAULT_REWARDED_KEY = "bidmachine-rewarded";
    public static final long DEFAULT_DELAY = 100;

    static final String SELLER_ID = "seller_id";
    static final String MEDIATION_CONFIG = "mediation_config";
    static final String COPPA = "coppa";
    static final String LOGGING_ENABLED = "logging_enabled";
    static final String TEST_MODE = "test_mode";
    static final String SUBJECT_TO_GDPR = "subject_to_gdpr";
    static final String HAS_CONSENT = "has_consent";
    static final String CONSENT_STRING = "consent_string";
    static final String ENDPOINT = "endpoint";
    static final String AD_CONTENT_TYPE = "ad_content_type";
    static final String MEDIA_ASSET_TYPES = "media_asset_types";
    static final String USER_ID = "user_id";
    static final String GENDER = "gender";
    static final String YOB = "yob";
    static final String KEYWORDS = "keywords";
    static final String COUNTRY = "country";
    static final String CITY = "city";
    static final String ZIP = "zip";
    static final String STURL = "sturl";
    static final String STORE_CAT = "store_cat";
    static final String STORE_SUB_CAT = "store_subcat";
    static final String FMW_NAME = "fmw_name";
    static final String PAID = "paid";
    static final String BCAT = "bcat";
    static final String BADV = "badv";
    static final String BAPPS = "bapps";
    static final String PRICE_FLOORS = "price_floors";
    static final String PUBLISHER_ID = "pubid";
    static final String PUBLISHER_NAME = "pubname";
    static final String PUBLISHER_DOMAIN = "pubdomain";
    static final String PUBLISHER_CATEGORIES = "pubcat";

    static void onAdFailedToLoad(@NonNull CustomEventListener listener, @NonNull BMError bmError) {
        onAdFailedToLoad(listener,
                         transformToAdMobErrorCode(bmError),
                         bmError.getMessage());
    }

    static void onAdFailedToLoad(@NonNull CustomEventListener listener,
                                 int errorCode,
                                 @NonNull String errorMessage) {
        Log.d(TAG, errorMessage);
        listener.onAdFailedToLoad(createAdError(errorCode, errorMessage));
    }

    static void onAdFailedToLoad(@NonNull MediationAdLoadCallback<?, ?> mediationAdLoadCallback,
                                 @NonNull BMError bmError) {
        onAdFailedToLoad(mediationAdLoadCallback,
                         transformToAdMobErrorCode(bmError),
                         bmError.getMessage());
    }

    static void onAdFailedToLoad(@NonNull MediationAdLoadCallback<?, ?> mediationAdLoadCallback,
                                 int errorCode,
                                 @NonNull String errorMessage) {
        Log.d(TAG, errorMessage);
        mediationAdLoadCallback.onFailure(createAdError(errorCode, errorMessage));
    }

    static AdError createAdError(int errorCode, @NonNull String errorMessage) {
        return new AdError(errorCode, errorMessage, ERROR_DOMAIN);
    }

    /**
     * Preparing BidMachine before it may be used
     *
     * @param extras - bundle which contains one or more of:
     *               1. {@link BidMachineUtils#SELLER_ID};
     *               2. {@link BidMachineUtils#LOGGING_ENABLED};
     *               3. {@link BidMachineUtils#TEST_MODE};
     *               4. {@link BidMachineUtils#MEDIATION_CONFIG};
     *               5. {@link BidMachineUtils#ENDPOINT}.
     * @return was initialize or not
     */
    static boolean prepareBidMachine(@NonNull Context context,
                                     @NonNull Bundle extras,
                                     @Nullable MediationAdRequest mediationAdRequest) {
        BidMachineUtils.updateCoppa(extras, taggedForChildDirectedTreatment(mediationAdRequest));
        return prepareBidMachine(context, extras);
    }

    static boolean prepareBidMachine(@NonNull Context context,
                                     @NonNull Bundle extras,
                                     @Nullable MediationRewardedAdConfiguration mediationRewardedAdConfiguration) {
        BidMachineUtils.updateCoppa(extras,
                                    taggedForChildDirectedTreatment(mediationRewardedAdConfiguration));
        return prepareBidMachine(context, extras);
    }

    private static boolean prepareBidMachine(@NonNull Context context, @NonNull Bundle extras) {
        Boolean loggingEnabled = getBoolean(extras, LOGGING_ENABLED);
        if (loggingEnabled != null) {
            BidMachine.setLoggingEnabled(loggingEnabled);
        }
        Boolean testMode = getBoolean(extras, TEST_MODE);
        if (testMode != null) {
            BidMachine.setTestMode(testMode);
        }
        String endpoint = getString(extras, ENDPOINT);
        if (!TextUtils.isEmpty(endpoint)) {
            assert endpoint != null;
            BidMachine.setEndpoint(endpoint);
        }
        BidMachine.setPublisher(createPublisher(extras));
        BidMachineUtils.updateGDPR(extras);
        if (!BidMachine.isInitialized()) {
            String jsonData = getString(extras, MEDIATION_CONFIG);
            if (jsonData != null) {
                BidMachine.registerNetworks(jsonData);
            }
            String sellerId = getString(extras, SELLER_ID);
            if (!TextUtils.isEmpty(sellerId)) {
                assert sellerId != null;
                BidMachine.initialize(context, sellerId);
                return true;
            } else {
                Log.d(TAG, "Failed to request ad. seller_id not found");
                return false;
            }
        }
        return true;
    }

    private static int taggedForChildDirectedTreatment(@Nullable MediationAdRequest mediationAdRequest) {
        return mediationAdRequest != null
                ? mediationAdRequest.taggedForChildDirectedTreatment()
                : MediationAdRequest.TAG_FOR_CHILD_DIRECTED_TREATMENT_UNSPECIFIED;
    }

    private static int taggedForChildDirectedTreatment(@Nullable MediationRewardedAdConfiguration mediationRewardedAdConfiguration) {
        return mediationRewardedAdConfiguration != null
                ? mediationRewardedAdConfiguration.taggedForChildDirectedTreatment()
                : MediationRewardedAdConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_UNSPECIFIED;
    }

    /**
     * Update GDPR state
     *
     * @param extras - bundle which contains one or more of:
     *               1. {@link BidMachineUtils#SUBJECT_TO_GDPR};
     *               2. {@link BidMachineUtils#HAS_CONSENT};
     *               3. {@link BidMachineUtils#CONSENT_STRING}.
     */
    static void updateGDPR(Bundle extras) {
        Boolean subjectToGDPR = getBoolean(extras, SUBJECT_TO_GDPR);
        if (subjectToGDPR != null) {
            BidMachine.setSubjectToGDPR(subjectToGDPR);
        }
        Boolean hasConsent = getBoolean(extras, HAS_CONSENT);
        String consentString = getString(extras, CONSENT_STRING);
        if (hasConsent != null) {
            BidMachine.setConsentConfig(hasConsent, consentString);
        }
    }

    /**
     * Update Coppa state from two sources:
     * 1. {@link BidMachineBundleBuilder#setCoppa(boolean)};
     * 2. {@link AdRequest.Builder#tagForChildDirectedTreatment(boolean)}.
     * If one of them is true then coppa is true.
     *
     * @param extras                          - bundle which contains {@link BidMachineUtils#COPPA}
     * @param taggedForChildDirectedTreatment - coppa from {@link AdRequest.Builder#tagForChildDirectedTreatment(boolean)}
     */
    static void updateCoppa(Bundle extras, int taggedForChildDirectedTreatment) {
        Boolean coppaExtras = getBoolean(extras, COPPA);
        if (coppaExtras != null && coppaExtras
                || taggedForChildDirectedTreatment ==
                MediationAdRequest.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE) {
            BidMachine.setCoppa(true);
        }
    }

    /**
     * Transform BidMachine error to AdMob error
     *
     * @param bmError - BidMachine error object
     * @return AdMob error object
     */
    static int transformToAdMobErrorCode(@NonNull BMError bmError) {
        if (bmError == BMError.NoContent
                || bmError == BMError.NotLoaded) {
            return AdRequest.ERROR_CODE_NO_FILL;
        } else if (bmError == BMError.Server
                || bmError == BMError.Connection
                || bmError == BMError.TimeoutError) {
            return AdRequest.ERROR_CODE_NETWORK_ERROR;
        } else if (bmError == BMError.IncorrectAdUnit) {
            return AdRequest.ERROR_CODE_INVALID_REQUEST;
        } else {
            return AdRequest.ERROR_CODE_INTERNAL_ERROR;
        }
    }

    static boolean isPreBidIntegration(@Nullable Bundle extras) {
        return extras != null && extras.containsKey(BidMachineFetcher.KEY_ID);
    }

    static boolean isServerExtrasValid(@Nullable Bundle serverExtras,
                                       @Nullable Bundle localExtras) {
        String serverPrice = serverExtras != null ? serverExtras.getString("bm_pf") : null;
        String localPrice = localExtras != null ? localExtras.getString("bm_pf") : null;
        return !TextUtils.isEmpty(serverPrice)
                && !TextUtils.isEmpty(localPrice)
                && serverPrice.equals(localPrice);
    }

    /**
     * Prepare fused bundle from serverExtras and localExtras
     *
     * @param serverExtras - bundle with server parameters
     * @param localExtras  - bundle with local parameters
     * @return fused bundle which contains serverExtras and localExtras
     */
    static Bundle getFusedBundle(@Nullable Bundle serverExtras, @Nullable Bundle localExtras) {
        Bundle fusedExtras = new Bundle();
        if (localExtras != null) {
            fusedExtras.putAll(localExtras);
        }
        if (serverExtras != null) {
            fusedExtras.putAll(serverExtras);
        }
        return fusedExtras;
    }

    /**
     * Targeting parameters must be set with help {@link BidMachineBundleBuilder}
     *
     * @param extras - bundle which contains the necessary parameters for targeting
     * @return TargetingParams with targeting from extras
     */
    static TargetingParams createTargetingParams(@NonNull Bundle extras) {
        TargetingParams targetingParams = new TargetingParams();
        String userId = getString(extras, USER_ID);
        if (userId == null) {
            userId = getString(extras, "userId");
        }
        if (userId != null) {
            targetingParams.setUserId(userId);
        }
        Gender gender = getGender(extras);
        if (gender != null) {
            targetingParams.setGender(gender);
        }
        Integer birthdayYear = getInteger(extras, YOB);
        if (birthdayYear != null) {
            targetingParams.setBirthdayYear(birthdayYear);
        }
        String keywords = getString(extras, KEYWORDS);
        if (keywords != null) {
            targetingParams.setKeywords(splitString(keywords));
        }
        String country = getString(extras, COUNTRY);
        if (country != null) {
            targetingParams.setCountry(country);
        }
        String city = getString(extras, CITY);
        if (city != null) {
            targetingParams.setCity(city);
        }
        String zip = getString(extras, ZIP);
        if (zip != null) {
            targetingParams.setZip(zip);
        }
        String sturl = getString(extras, STURL);
        if (sturl != null) {
            targetingParams.setStoreUrl(sturl);
        }
        String storeCategory = getString(extras, STORE_CAT);
        if (storeCategory != null) {
            targetingParams.setStoreCategory(storeCategory);
        }
        String storeSubCategories = getString(extras, STORE_SUB_CAT);
        if (storeSubCategories != null) {
            targetingParams.setStoreSubCategories(splitString(storeSubCategories));
        }
        String frameworkName = getString(extras, FMW_NAME);
        if (frameworkName != null) {
            targetingParams.setFramework(frameworkName);
        }
        Boolean paid = getBoolean(extras, PAID);
        if (paid != null) {
            targetingParams.setPaid(paid);
        }
        String bcat = getString(extras, BCAT);
        if (bcat != null) {
            for (String value : splitString(bcat)) {
                targetingParams.addBlockedAdvertiserIABCategory(value.trim());
            }
        }
        String badv = getString(extras, BADV);
        if (badv != null) {
            for (String value : splitString(badv)) {
                targetingParams.addBlockedAdvertiserDomain(value.trim());
            }
        }
        String bapps = getString(extras, BAPPS);
        if (bapps != null) {
            for (String value : splitString(bapps)) {
                targetingParams.addBlockedApplication(value.trim());
            }
        }
        return targetingParams;
    }

    /**
     * Price floor parameters must be set with help {@link BidMachineBundleBuilder}
     *
     * @param extras - bundle which contains the necessary parameters for price floor
     * @return PriceFloorParams with price floors from extras
     */
    static PriceFloorParams createPriceFloorParams(@NonNull Bundle extras) {
        PriceFloorParams priceFloorParams = new PriceFloorParams();
        String priceFloors = getString(extras, PRICE_FLOORS);
        if (TextUtils.isEmpty(priceFloors)) {
            priceFloors = getString(extras, "priceFloors");
        }
        if (TextUtils.isEmpty(priceFloors)) {
            return priceFloorParams;
        }

        try {
            JSONArray jsonArray = new JSONArray(priceFloors);
            for (int i = 0; i < jsonArray.length(); i++) {
                Object object = jsonArray.opt(i);
                if (object instanceof JSONObject) {
                    JSONObject jsonObject = (JSONObject) object;
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String id = iterator.next();
                        double price = parsePrice(jsonObject.opt(id));
                        if (!TextUtils.isEmpty(id) && price > -1) {
                            priceFloorParams.addPriceFloor(id, price);
                        }
                    }
                } else {
                    double price = parsePrice(object);
                    if (price > -1) {
                        priceFloorParams.addPriceFloor(price);
                    }
                }
            }
        } catch (Exception e) {
            return new PriceFloorParams();
        }

        return priceFloorParams;
    }

    /**
     * Publisher parameters must be set with help {@link BidMachineBundleBuilder}
     *
     * @param extras - map where are the necessary parameters for {@link Publisher}
     * @return {@link Publisher} with parameters from extras
     */
    @NonNull
    private static Publisher createPublisher(@NonNull Bundle extras) {
        Publisher.Builder publisherBuilder = new Publisher.Builder();
        publisherBuilder.setId(getString(extras, PUBLISHER_ID));
        publisherBuilder.setName(getString(extras, PUBLISHER_NAME));
        publisherBuilder.setDomain(getString(extras, PUBLISHER_DOMAIN));
        String publisherCategories = getString(extras, PUBLISHER_CATEGORIES);
        if (publisherCategories != null) {
            for (String value : splitString(publisherCategories)) {
                publisherBuilder.addCategory(value.trim());
            }
        }
        return publisherBuilder.build();
    }

    /**
     * Transform server parameters from string to {@link Bundle}
     *
     * @param serverParameters - parameters from server
     * @return equivalent server string in {@link Bundle}
     */
    static Bundle transformToBundle(@Nullable String serverParameters) {
        if (TextUtils.isEmpty(serverParameters)) {
            return null;
        }
        assert serverParameters != null;
        try {
            JSONObject jsonObject = new JSONObject(serverParameters);
            Bundle bundle = new Bundle();
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (!jsonObject.isNull(key)) {
                    Object value = jsonObject.get(key);
                    String valueString = String.valueOf(value);
                    if (!TextUtils.isEmpty(valueString) && !valueString.equals("null")) {
                        bundle.putString(key, String.valueOf(value));
                    }
                }
            }
            return bundle;
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    private static Object getValue(Bundle bundle, String key) {
        if (bundle == null || TextUtils.isEmpty(key)) {
            return null;
        }
        return bundle.get(key);
    }

    @Nullable
    static String getString(Bundle bundle, String key) {
        Object object = getValue(bundle, key);
        if (object instanceof String) {
            return (String) object;
        } else {
            return null;
        }
    }

    @Nullable
    private static Boolean getBoolean(Bundle bundle, String key) {
        Object object = getValue(bundle, key);
        if (object instanceof Boolean) {
            return (Boolean) object;
        } else if (object instanceof String) {
            return Boolean.parseBoolean((String) object);
        } else {
            return null;
        }
    }

    @Nullable
    private static Integer getInteger(Bundle bundle, String key) {
        Object object = getValue(bundle, key);
        if (object instanceof Integer) {
            return (int) object;
        } else if (object instanceof Double) {
            return ((Double) object).intValue();
        } else if (object instanceof String) {
            try {
                return Integer.parseInt((String) object);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    @Nullable
    private static Gender getGender(Bundle bundle) {
        String value = getString(bundle, GENDER);
        if (value == null) {
            return null;
        }
        if (Gender.Female.getOrtbValue().equals(value)) {
            return Gender.Female;
        } else if (Gender.Male.getOrtbValue().equals(value)) {
            return Gender.Male;
        } else {
            return Gender.Omitted;
        }
    }

    @NonNull
    static String[] splitString(String value) {
        if (TextUtils.isEmpty(value)) {
            return new String[0];
        }
        try {
            return value.split(",");
        } catch (Exception e) {
            return new String[0];
        }
    }

    private static double parsePrice(Object object) {
        if (object instanceof Double) {
            return (double) object;
        } else if (object instanceof Integer) {
            return ((Integer) object).doubleValue();
        } else if (object instanceof String) {
            return convertToPrice((String) object);
        }
        return -1;
    }

    private static double convertToPrice(String value) {
        if (!TextUtils.isEmpty(value)) {
            try {
                if (value.lastIndexOf('.') > value.lastIndexOf(',')) {
                    return NumberFormat.getInstance(Locale.TAIWAN).parse(value).doubleValue();
                } else {
                    return NumberFormat.getInstance().parse(value).doubleValue();
                }
            } catch (Exception e) {
                return -1;
            }
        }
        return -1;
    }

    @Nullable
    static <T extends io.bidmachine.AdRequest> T obtainCachedRequest(@NonNull AdsType adsType,
                                                                     @NonNull Bundle fusedBundle) {
        return obtainCachedRequest(adsType, fusedBundle.get(BidMachineFetcher.KEY_ID));
    }

    @Nullable
    @SuppressWarnings("unchecked")
    static <T extends io.bidmachine.AdRequest> T obtainCachedRequest(@NonNull AdsType adsType,
                                                                     @Nullable Object id) {
        return id != null ? (T) BidMachineFetcher.release(adsType, String.valueOf(id)) : null;
    }

    @Nullable
    public static String getRewardedAdKey(RewardedAd rewardedAd) {
        Bundle metadata = rewardedAd.getAdMetadata();
        return metadata.getString("AdTitle");
    }

    @NonNull
    private static Bundle fetch(@NonNull io.bidmachine.AdRequest<?, ?> adRequest) {
        Bundle bundle = new Bundle();
        Map<String, String> fetchParams = BidMachineFetcher.fetch(adRequest);
        if (fetchParams != null) {
            for (Map.Entry<String, String> entry : fetchParams.entrySet()) {
                bundle.putString(entry.getKey(), entry.getValue());
            }
        }
        return bundle;
    }


    /**
     * {@link AdRequest} creation method based on {@link io.bidmachine.AdRequest}
     *
     * @param adRequest - loaded {@link io.bidmachine.AdRequest}
     * @return {@link AdRequest} ready to loading
     */
    @NonNull
    public static AdRequest createAdRequest(@NonNull io.bidmachine.AdRequest<?, ?> adRequest) {
        return createAdRequestBuilder(adRequest).build();
    }

    /**
     * {@link AdRequest.Builder} creation method based on {@link io.bidmachine.AdRequest}
     *
     * @param adRequest - loaded {@link io.bidmachine.AdRequest}
     * @return {@link AdRequest.Builder} ready to building and loading
     */
    @NonNull
    public static AdRequest.Builder createAdRequestBuilder(@NonNull io.bidmachine.AdRequest<?, ?> adRequest) {
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        appendRequest(adRequestBuilder, adRequest);
        return adRequestBuilder;
    }

    /**
     * Append extras to {@link AdRequest.Builder} from loaded {@link io.bidmachine.AdRequest}
     *
     * @param builder   - {@link AdRequest.Builder} that will be built and loaded
     * @param adRequest - loaded {@link io.bidmachine.AdRequest}
     */
    public static void appendRequest(@NonNull AdRequest.Builder builder,
                                     @NonNull io.bidmachine.AdRequest<?, ?> adRequest) {
        if (adRequest instanceof BannerRequest) {
            builder.addCustomEventExtrasBundle(BidMachineCustomEventBanner.class,
                                               fetch(adRequest));
        } else if (adRequest instanceof InterstitialRequest) {
            builder.addCustomEventExtrasBundle(BidMachineCustomEventInterstitial.class,
                                               fetch(adRequest));
        } else if (adRequest instanceof RewardedRequest) {
            builder.addNetworkExtrasBundle(BidMachineAdapter.class,
                                           fetch(adRequest));
        } else if (adRequest instanceof NativeRequest) {
            builder.addCustomEventExtrasBundle(BidMachineCustomEventNative.class,
                                               fetch(adRequest));
        } else {
            Log.d(TAG, "Unknown AdRequest realization");
        }
    }


    /**
     * {@link AdManagerAdRequest} creation method based on {@link io.bidmachine.AdRequest}
     *
     * @param adRequest - loaded {@link io.bidmachine.AdRequest}
     * @return {@link AdManagerAdRequest} ready to loading
     */
    @NonNull
    public static AdManagerAdRequest createAdManagerRequest(@NonNull io.bidmachine.AdRequest<?, ?> adRequest) {
        return createAdManagerRequestBuilder(adRequest).build();
    }

    /**
     * {@link AdManagerAdRequest.Builder} creation method based on {@link io.bidmachine.AdRequest}
     *
     * @param adRequest - loaded {@link io.bidmachine.AdRequest}
     * @return {@link AdManagerAdRequest.Builder} ready to building and loading
     */
    @NonNull
    public static AdManagerAdRequest.Builder createAdManagerRequestBuilder(@NonNull io.bidmachine.AdRequest<?, ?> adRequest) {
        AdManagerAdRequest.Builder adRequestBuilder = new AdManagerAdRequest.Builder();
        appendRequest(adRequestBuilder, adRequest);
        return adRequestBuilder;
    }

    /**
     * Fill {@link AdManagerAdRequest.Builder} by loaded {@link io.bidmachine.AdRequest}
     *
     * @param builder   - {@link AdManagerAdRequest.Builder} that will be built and loaded
     * @param adRequest - loaded {@link io.bidmachine.AdRequest}
     */
    public static void appendRequest(@NonNull AdManagerAdRequest.Builder builder,
                                     @NonNull io.bidmachine.AdRequest<?, ?> adRequest) {
        appendCustomTargeting(builder, adRequest);
    }

    /**
     * Append custom targeting to {@link AdManagerAdRequest.Builder}
     * from loaded {@link io.bidmachine.AdRequest}
     *
     * @param builder   - {@link AdManagerAdRequest.Builder} that will be built and loaded
     * @param adRequest - loaded {@link io.bidmachine.AdRequest}
     */
    public static void appendCustomTargeting(@NonNull AdManagerAdRequest.Builder builder,
                                             @NonNull io.bidmachine.AdRequest<?, ?> adRequest) {
        Map<String, String> map = BidMachineFetcher.toMap(adRequest);
        appendCustomTargeting(builder, map);
    }

    /**
     * Append custom targeting to {@link AdManagerAdRequest.Builder}
     * from loaded {@link io.bidmachine.AdRequest}
     *
     * @param builder - {@link AdManagerAdRequest.Builder} that will be built and loaded
     * @param map     - parameters to be added as custom targeting
     */
    public static void appendCustomTargeting(@NonNull AdManagerAdRequest.Builder builder,
                                             @NonNull Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.addCustomTargeting(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Checking whether it is BidMachine creative or not
     *
     * @param adManagerAdView - loaded ad object
     * @param listener        - to transfer the result
     */
    public static void isBidMachineBanner(@NonNull AdManagerAdView adManagerAdView,
                                          @NonNull ResultListener listener) {
        isBidMachineBanner(adManagerAdView,
                           listener,
                           DEFAULT_BANNER_KEY,
                           DEFAULT_DELAY);
    }

    /**
     * Checking whether it is BidMachine creative or not
     *
     * @param adManagerAdView - loaded ad object
     * @param listener        - to transfer the result
     * @param bidMachineKey   - key from creative
     * @param delay           - maximum check time
     */
    public static void isBidMachineBanner(@NonNull AdManagerAdView adManagerAdView,
                                          @NonNull ResultListener listener,
                                          @NonNull String bidMachineKey,
                                          long delay) {
        BidMachineAppEvent.setListener(adManagerAdView, listener, bidMachineKey, delay);
    }

    /**
     * Checking whether it is BidMachine creative or not
     *
     * @param adManagerInterstitialAd - loaded ad object
     * @param listener                - to transfer the result
     */
    public static void isBidMachineInterstitial(@NonNull AdManagerInterstitialAd adManagerInterstitialAd,
                                                @NonNull ResultListener listener) {
        isBidMachineInterstitial(adManagerInterstitialAd,
                                 listener,
                                 DEFAULT_INTERSTITIAL_KEY,
                                 DEFAULT_DELAY);
    }

    /**
     * Checking whether it is BidMachine creative or not
     *
     * @param adManagerInterstitialAd - loaded ad object
     * @param listener                - to transfer the result
     * @param bidMachineKey           - key from creative
     * @param delay                   - maximum check time
     */
    public static void isBidMachineInterstitial(@NonNull AdManagerInterstitialAd adManagerInterstitialAd,
                                                @NonNull ResultListener listener,
                                                @NonNull String bidMachineKey,
                                                long delay) {
        BidMachineAppEvent.setListener(adManagerInterstitialAd, listener, bidMachineKey, delay);
    }

    /**
     * Checking whether it is BidMachine creative or not
     *
     * @param rewardedAd - loaded ad object
     * @return is BidMachine creative or not
     */
    public static boolean isBidMachineRewarded(@NonNull RewardedAd rewardedAd) {
        return isBidMachineRewarded(rewardedAd, DEFAULT_REWARDED_KEY);
    }

    /**
     * Checking whether it is BidMachine creative or not
     *
     * @param rewardedAd    - loaded ad object
     * @param bidMachineKey - key from creative
     * @return is BidMachine creative or not
     */
    public static boolean isBidMachineRewarded(@NonNull RewardedAd rewardedAd,
                                               @NonNull String bidMachineKey) {
        String key = BidMachineUtils.getRewardedAdKey(rewardedAd);
        return TextUtils.equals(bidMachineKey, key);
    }

}