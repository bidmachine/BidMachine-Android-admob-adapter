package com.google.ads.mediation.bidmachine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.mediation.MediationAdRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Locale;

import io.bidmachine.BidMachine;
import io.bidmachine.PriceFloorParams;
import io.bidmachine.TargetingParams;
import io.bidmachine.utils.BMError;
import io.bidmachine.utils.Gender;

class BidMachineUtils {

    static final String SELLER_ID = "seller_id";
    static final String COPPA = "coppa";
    static final String LOGGING_ENABLED = "logging_enabled";
    static final String TEST_MODE = "test_mode";
    static final String SUBJECT_TO_GDPR = "subject_to_gdpr";
    static final String HAS_CONSENT = "has_consent";
    static final String CONSENT_STRING = "consent_string";
    static final String AD_CONTENT_TYPE = "ad_content_type";
    static final String USER_ID = "user_id";
    static final String GENDER = "gender";
    static final String YOB = "yob";
    static final String KEYWORDS = "keywords";
    static final String COUNTRY = "country";
    static final String CITY = "city";
    static final String ZIP = "zip";
    static final String STURL = "sturl";
    static final String PAID = "paid";
    static final String BCAT = "bcat";
    static final String BADV = "badv";
    static final String BAPPS = "bapps";
    static final String PRICE_FLOORS = "price_floors";

    private static boolean isInitialized = false;

    /**
     * Preparing BidMachine before it may be used
     *
     * @param extras - bundle which contains one or more of:
     *               1. {@link BidMachineUtils#SELLER_ID};
     *               2. {@link BidMachineUtils#LOGGING_ENABLED};
     *               3. {@link BidMachineUtils#TEST_MODE}.
     * @return was initialize or not
     */
    static boolean prepareBidMachine(Context context, @NonNull Bundle extras) {
        Boolean loggingEnabled = getBoolean(extras, LOGGING_ENABLED);
        if (loggingEnabled != null) {
            BidMachine.setLoggingEnabled(loggingEnabled);
        }
        Boolean testMode = getBoolean(extras, TEST_MODE);
        if (testMode != null) {
            BidMachine.setTestMode(testMode);
        }
        if (!isInitialized) {
            String sellerId = getString(extras, SELLER_ID);
            if (!TextUtils.isEmpty(sellerId)) {
                BidMachine.initialize(context, sellerId);
                isInitialized = true;
                return true;
            } else {
                return false;
            }
        }
        return true;
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
            BidMachine.setConsentConfig(
                    hasConsent,
                    consentString != null
                            ? consentString
                            : "");
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
                || bmError == BMError.NotLoaded
                || bmError == BMError.TimeoutError) {
            return AdRequest.ERROR_CODE_NO_FILL;
        } else if (bmError == BMError.Server
                || bmError == BMError.Connection) {
            return AdRequest.ERROR_CODE_NETWORK_ERROR;
        } else if (bmError == BMError.IncorrectAdUnit) {
            return AdRequest.ERROR_CODE_INVALID_REQUEST;
        } else {
            return AdRequest.ERROR_CODE_INTERNAL_ERROR;
        }
    }

    /**
     * Prepare fused bundle from serverExtras and localExtras
     *
     * @param serverParameters - parameters from server
     * @param localExtras      - bundle with local parameters
     * @return fused bundle which contains serverExtras and localExtras
     */
    static Bundle getFusedBundle(@Nullable String serverParameters, @Nullable Bundle localExtras) {
        Bundle fusedExtras = new Bundle();
        if (localExtras != null) {
            fusedExtras.putAll(localExtras);
        }
        Bundle serverExtras = transformToBundle(serverParameters);
        if (serverExtras != null) {
            fusedExtras.putAll(serverExtras);
        }
        return fusedExtras;
    }

    /**
     * +----------+--------------------------------------------------------------------------------+------------+
     * |   Key    |                                   Definition                                   | Value type |
     * +----------+--------------------------------------------------------------------------------+------------+
     * | user_id  | Vendor-specific ID for the user                                                | String     |
     * | gender   | Gender, one of following: "F", "M", "O"                                        | String     |
     * | yob      | Year of birth as a 4-digit integer (e.g - 1990)                                | String     |
     * | keywords | List of keywords, interests, or intents (separated by comma)                   | String     |
     * | country  | Country of the user's home base (i.e., not necessarily their current location) | String     |
     * | city     | City of the user's home base (i.e., not necessarily their current location)    | String     |
     * | zip      | Zip of the user's home base (i.e., not necessarily their current location)     | String     |
     * | sturl    | App store URL for an installed app; for IQG 2.1 compliance                     | String     |
     * | paid     | Determines, if it is a free or paid version of the app                         | String     |
     * | bcat     | Block list of content categories using IDs (separated by comma)                | String     |
     * | badv     | Block list of advertisers by their domains (separated by comma)                | String     |
     * | bapps    | Block list of apps where ads are disallowed (separated by comma)               | String     |
     * +----------+--------------------------------------------------------------------------------+------------+
     * <p>
     * Targeting parameters must be set with help {@link BidMachineBundleBuilder}
     *
     * @param extras - bundle which contains the necessary parameters for targeting
     * @return TargetingParams with targeting from extras
     */
    static TargetingParams createTargetingParams(@NonNull Bundle extras) {
        TargetingParams targetingParams = new TargetingParams();
        String userId = getString(extras, USER_ID);
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
        Boolean paid = getBoolean(extras, PAID);
        if (paid != null) {
            targetingParams.setPaid(paid);
        }
        String bcat = getString(extras, BCAT);
        if (bcat != null) {
            for (String value : splitString(bcat)) {
                targetingParams.addBlockedAdvertiserIABCategory(value);
            }
        }
        String badv = getString(extras, BADV);
        if (badv != null) {
            for (String value : splitString(badv)) {
                targetingParams.addBlockedAdvertiserDomain(value);
            }
        }
        String bapps = getString(extras, BAPPS);
        if (bapps != null) {
            for (String value : splitString(bapps)) {
                targetingParams.addBlockedApplication(value);
            }
        }
        return targetingParams;
    }

    /**
     * +--------------+---------------------+---------------------+
     * |     Key      |     Definition      |     Value type      |
     * +--------------+---------------------+---------------------+
     * | price_floors | List of price floor | JSONArray in String |
     * +--------------+---------------------+---------------------+
     * <p>
     * Price floor parameters must be set with help {@link BidMachineBundleBuilder}
     *
     * @param extras - bundle which contains the necessary parameters for price floor
     * @return PriceFloorParams with price floors from extras
     */
    static PriceFloorParams createPriceFloorParams(@NonNull Bundle extras) {
        String priceFloors = getString(extras, PRICE_FLOORS);
        PriceFloorParams priceFloorParams = new PriceFloorParams();
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
     * Transform server parameters from string to {@link Bundle}
     *
     * @param serverParameters - parameters from server
     * @return equivalent server string in {@link Bundle}
     */
    private static Bundle transformToBundle(@Nullable String serverParameters) {
        if (TextUtils.isEmpty(serverParameters)) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(serverParameters);
            Bundle bundle = new Bundle();
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Object value = jsonObject.get(key);
                String valueString = String.valueOf(value);
                if (!TextUtils.isEmpty(valueString) && !valueString.equals("null")) {
                    bundle.putString(key, String.valueOf(value));
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

    private static String[] splitString(String value) {
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

}
