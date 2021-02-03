package com.google.ads.mediation.bidmachine;

import android.os.Bundle;

import org.json.JSONArray;

import io.bidmachine.AdContentType;
import io.bidmachine.utils.Gender;

public class BidMachineBundleBuilder {

    /**
     * You unique seller id. To get your Seller Id or for more info please visit
     * https://bidmachine.io/
     */
    private String sellerId;

    /**
     * You mediation config
     */
    private String mediationConfig;

    /**
     * Flag indicating if COPPA regulations can be applied.
     * The Children's Online Privacy Protection Act (COPPA) was established by the U.S.
     * Federal Trade Commission.
     */
    private Boolean coppa;

    /**
     * Enable logs if required
     */
    private Boolean loggingEnabled;

    /**
     * Enable test mode
     */
    private Boolean testMode;

    /**
     * Flag indicating if GDPR regulations can be applied.
     * The General Data Protection Regulation (GDPR) is a regulation of the European Union.
     */
    private Boolean subjectToGDPR;

    /**
     * User has given consent to the processing of personal data relating to him or her.
     * https://www.eugdpr.org/
     */
    private Boolean hasConsent;

    /**
     * GDPR consent string (if applicable), indicating the compliance to the IAB standard
     * Consent String Format of the Transparency and Consent Framework technical specifications.
     */
    private String consentString;

    /**
     * Your custom endpoint
     */
    private String endpoint;

    /**
     * Content type for interstitial ad, one of following:
     * 1. {@link AdContentType#All} - Flag to request both Video and Static ad content types;
     * 2. {@link AdContentType#Static} - Flag to request Video ad content type only;
     * 3. {@link AdContentType#Video} - Flag to request Static ad content type only.
     */
    private AdContentType adContentType;

    /**
     * Vendor-specific ID for the user
     */
    private String userId;

    /**
     * Gender, one of following:
     * 1. {@link Gender#Male}
     * 2. {@link Gender#Female}
     * 3. {@link Gender#Omitted}
     */
    private Gender gender;

    /**
     * Year of birth as a 4-digit integer (e.g - 1990)
     */
    private Integer yob;

    /**
     * List of keywords, interests, or intents (separated by comma)
     */
    private String keywords;

    /**
     * Country of the user's home base (i.e., not necessarily their current location)
     */
    private String country;

    /**
     * City of the user's home base (i.e., not necessarily their current location)
     */
    private String city;

    /**
     * Zip of the user's home base (i.e., not necessarily their current location)
     */
    private String zip;

    /**
     * App store URL for an installed app; for IQG 2.1 compliance
     */
    private String sturl;

    /**
     * Determines, if it is a free or paid version of the app
     */
    private Boolean paid;

    /**
     * Block list of content categories using IDs (separated by comma)
     */
    private String bcat;

    /**
     * Block list of advertisers by their domains (separated by comma)
     */
    private String badv;

    /**
     * Block list of apps where ads are disallowed (separated by comma)
     */
    private String bapps;

    /**
     * List of price floor
     */
    private JSONArray priceFloors;

    public BidMachineBundleBuilder setSellerId(String sellerId) {
        this.sellerId = sellerId;
        return this;
    }

    public BidMachineBundleBuilder setMediationConfig(JSONArray mediationConfig) {
        if (mediationConfig != null) {
            this.mediationConfig = mediationConfig.toString();
        }
        return this;
    }

    public BidMachineBundleBuilder setMediationConfig(String mediationConfig) {
        this.mediationConfig = mediationConfig;
        return this;
    }

    public BidMachineBundleBuilder setCoppa(boolean coppa) {
        this.coppa = coppa;
        return this;
    }

    public BidMachineBundleBuilder setLoggingEnabled(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
        return this;
    }

    public BidMachineBundleBuilder setTestMode(boolean testMode) {
        this.testMode = testMode;
        return this;
    }

    public BidMachineBundleBuilder setSubjectToGDPR(boolean subjectToGDPR) {
        this.subjectToGDPR = subjectToGDPR;
        return this;
    }

    public BidMachineBundleBuilder setConsentConfig(boolean hasConsent, String consentString) {
        this.hasConsent = hasConsent;
        this.consentString = consentString;
        return this;
    }

    public BidMachineBundleBuilder setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public BidMachineBundleBuilder setAdContentType(AdContentType adContentType) {
        this.adContentType = adContentType;
        return this;
    }

    public BidMachineBundleBuilder setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public BidMachineBundleBuilder setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public BidMachineBundleBuilder setYob(int yob) {
        this.yob = yob;
        return this;
    }

    public BidMachineBundleBuilder setKeywords(String keywords) {
        this.keywords = keywords;
        return this;
    }

    public BidMachineBundleBuilder setCountry(String country) {
        this.country = country;
        return this;
    }

    public BidMachineBundleBuilder setCity(String city) {
        this.city = city;
        return this;
    }

    public BidMachineBundleBuilder setZip(String zip) {
        this.zip = zip;
        return this;
    }

    public BidMachineBundleBuilder setSturl(String sturl) {
        this.sturl = sturl;
        return this;
    }

    public BidMachineBundleBuilder setPaid(Boolean paid) {
        this.paid = paid;
        return this;
    }

    public BidMachineBundleBuilder setBcat(String bcat) {
        this.bcat = bcat;
        return this;
    }

    public BidMachineBundleBuilder setBadv(String badv) {
        this.badv = badv;
        return this;
    }

    public BidMachineBundleBuilder setBapps(String bapps) {
        this.bapps = bapps;
        return this;
    }

    public BidMachineBundleBuilder setPriceFloors(JSONArray priceFloors) {
        this.priceFloors = priceFloors;
        return this;
    }

    public Bundle build() {
        Bundle extras = new Bundle();
        if (sellerId != null) {
            extras.putString(BidMachineUtils.SELLER_ID, sellerId);
        }
        if (mediationConfig != null) {
            extras.putString(BidMachineUtils.MEDIATION_CONFIG, mediationConfig);
        }
        if (coppa != null) {
            extras.putBoolean(BidMachineUtils.COPPA, coppa);
        }
        if (loggingEnabled != null) {
            extras.putBoolean(BidMachineUtils.LOGGING_ENABLED, loggingEnabled);
        }
        if (testMode != null) {
            extras.putBoolean(BidMachineUtils.TEST_MODE, testMode);
        }
        if (subjectToGDPR != null) {
            extras.putBoolean(BidMachineUtils.SUBJECT_TO_GDPR, subjectToGDPR);
        }
        if (hasConsent != null) {
            extras.putBoolean(BidMachineUtils.HAS_CONSENT, hasConsent);
        }
        if (consentString != null) {
            extras.putString(BidMachineUtils.CONSENT_STRING, consentString);
        }
        if (endpoint != null) {
            extras.putString(BidMachineUtils.ENDPOINT, endpoint);
        }
        if (adContentType != null) {
            extras.putString(BidMachineUtils.AD_CONTENT_TYPE, adContentType.name());
        }
        if (userId != null) {
            extras.putString(BidMachineUtils.USER_ID, userId);
        }
        if (gender != null) {
            extras.putString(BidMachineUtils.GENDER, gender.name());
        }
        if (yob != null) {
            extras.putInt(BidMachineUtils.YOB, yob);
        }
        if (keywords != null) {
            extras.putString(BidMachineUtils.KEYWORDS, keywords);
        }
        if (country != null) {
            extras.putString(BidMachineUtils.COUNTRY, country);
        }
        if (city != null) {
            extras.putString(BidMachineUtils.CITY, city);
        }
        if (zip != null) {
            extras.putString(BidMachineUtils.ZIP, zip);
        }
        if (sturl != null) {
            extras.putString(BidMachineUtils.STURL, sturl);
        }
        if (paid != null) {
            extras.putBoolean(BidMachineUtils.PAID, paid);
        }
        if (bcat != null) {
            extras.putString(BidMachineUtils.BCAT, bcat);
        }
        if (badv != null) {
            extras.putString(BidMachineUtils.BADV, badv);
        }
        if (bapps != null) {
            extras.putString(BidMachineUtils.BAPPS, bapps);
        }
        if (priceFloors != null) {
            extras.putString(BidMachineUtils.PRICE_FLOORS, priceFloors.toString());
        }
        return extras;
    }

}
