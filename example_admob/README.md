# Classic AdMob implementation

* [Useful links](#useful-links)
* [Banner implementation](#banner-implementation)
* [MREC implementation](#mrec-implementation)
* [Interstitial implementation](#interstitial-implementation)
* [Rewarded implementation](#rewarded-implementation)
* [Native implementation](#native-implementation)

## Useful links
* [AdMob documentation](https://developers.google.com/admob/android/quick-start)

## Banner implementation
Server configuration sample:
```json
{
    "seller_id": "YOUR_SELLER_ID",
    "mediation_config": "YOUR_MEDIATION_CONFIG",
    "coppa": "true",
    "logging_enabled": "true",
    "test_mode": "true",
    "subject_to_gdpr": "true",
    "has_consent": "true",
    "consent_string": "YOUR_GDPR_CONSENT_STRING",
    "endpoint": "YOUR_ENDPOINT",
    "user_id": "YOUR_USER_ID",
    "gender": "F",
    "yob": "2000",
    "keywords": "Keyword_1,Keyword_2,Keyword_3,Keyword_4",
    "country": "YOUR_COUNTRY",
    "city": "YOUR_CITY",
    "zip": "YOUR_ZIP",
    "sturl": "https://store_url.com",
    "store_cat": "YOUR_STORE_CATEGORY",
    "store_subcat": "YOUR_STORE_SUB_CATEGORY_1,YOUR_STORE_SUB_CATEGORY_2",
    "fmw_name": "YOUR_FRAMEWORK_NAME",
    "paid": "true",
    "bcat": "IAB-1,IAB-3,IAB-5",
    "badv": "https://domain_1.com,https://domain_2.org",
    "bapps": "com.test.application_1,com.test.application_2,com.test.application_3",
    "price_floors": [{
            "id_1": 300.06
        }, {
            "id_2": 1000
        },
        302.006,
        1002
    ],
    "pubid": "YOUR_PUBLISHER_ID",
    "pubname": "YOUR_PUBLISHER_NAME",
    "pubdomain": "YOUR_PUBLISHER_DOMAIN",
    "pubcat": "YOUR_PUBLISHER_CATEGORIES_1,YOUR_PUBLISHER_CATEGORIES_2"
}
```

Local configuration sample:
```java
JSONArray priceFloors = new JSONArray();
try {
    priceFloors.put(new JSONObject().put("id1", 300.006));
    priceFloors.put(new JSONObject().put("id2", 1000));
    priceFloors.put(302.006);
    priceFloors.put(1002);
} catch (Exception e) {
    e.printStackTrace();
}

Bundle bundle = new BidMachineBundleBuilder()
        .setSellerId("YOUR_SELLER_ID")
        .setMediationConfig("YOUR_MEDIATION_CONFIG")
        .setCoppa(true)
        .setLoggingEnabled(true)
        .setTestMode(true)
        .setSubjectToGDPR(true)
        .setConsentConfig(true, "YOUR_CONSENT_STRING")
        .setEndpoint("YOUR_ENDPOINT")
        .setUserId("YOUR_USER_ID")
        .setGender(Gender.Male)
        .setYob(1990)
        .setKeywords("Keyword_1,Keyword_2,Keyword_3,Keyword_4")
        .setCountry("YOUR_COUNTRY")
        .setCity("YOUR_CITY")
        .setZip("YOUR_ZIP")
        .setSturl("https://store_url.com")
        .setStoreCategory("YOUR_STORE_CATEGORY")
        .setStoreSubCategories("YOUR_STORE_SUB_CATEGORY_1,YOUR_STORE_SUB_CATEGORY_2")
        .setFrameworkName(Framework.UNITY)
        .setPaid(true)
        .setBcat("IAB-1,IAB-3,IAB-5")
        .setBadv("https://domain_1.com,https://domain_2.org")
        .setBapps("com.test.application_1,com.test.application_2,com.test.application_3")
        .setPriceFloors(priceFloors)
        .setPublisherId("YOUR_PUBLISHER_ID")
        .setPublisherName("YOUR_PUBLISHER_NAME")
        .setPublisherDomain("YOUR_PUBLISHER_DOMAIN")
        .setPublisherCategories("YOUR_PUBLISHER_CATEGORIES_1,YOUR_PUBLISHER_CATEGORIES_2")
        .build();

// Set bundle to custom event banner
AdRequest adRequest = new AdRequest.Builder()
        .addCustomEventExtrasBundle(BidMachineCustomEventBanner.class, bundle)
        .build();

// Create new AdView instance and load it
AdView adView = new AdView(this);
adView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                  ViewGroup.LayoutParams.MATCH_PARENT));
adView.setAdUnitId(BANNER_ID);
adView.setAdSize(AdSize.BANNER);
adView.setAdListener(new BannerViewListener());
adView.loadAd(adRequest);
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineAdMobActivity.java#L114)

## MREC implementation
Server configuration sample:
```json
{
    "seller_id": "YOUR_SELLER_ID",
    "mediation_config": "YOUR_MEDIATION_CONFIG",
    "coppa": "true",
    "logging_enabled": "true",
    "test_mode": "true",
    "subject_to_gdpr": "true",
    "has_consent": "true",
    "consent_string": "YOUR_GDPR_CONSENT_STRING",
    "endpoint": "YOUR_ENDPOINT",
    "user_id": "YOUR_USER_ID",
    "gender": "F",
    "yob": "2000",
    "keywords": "Keyword_1,Keyword_2,Keyword_3,Keyword_4",
    "country": "YOUR_COUNTRY",
    "city": "YOUR_CITY",
    "zip": "YOUR_ZIP",
    "sturl": "https://store_url.com",
    "store_cat": "YOUR_STORE_CATEGORY",
    "store_subcat": "YOUR_STORE_SUB_CATEGORY_1,YOUR_STORE_SUB_CATEGORY_2",
    "fmw_name": "YOUR_FRAMEWORK_NAME",
    "paid": "true",
    "bcat": "IAB-1,IAB-3,IAB-5",
    "badv": "https://domain_1.com,https://domain_2.org",
    "bapps": "com.test.application_1,com.test.application_2,com.test.application_3",
    "price_floors": [{
            "id_1": 300.06
        }, {
            "id_2": 1000
        },
        302.006,
        1002
    ],
    "pubid": "YOUR_PUBLISHER_ID",
    "pubname": "YOUR_PUBLISHER_NAME",
    "pubdomain": "YOUR_PUBLISHER_DOMAIN",
    "pubcat": "YOUR_PUBLISHER_CATEGORIES_1,YOUR_PUBLISHER_CATEGORIES_2"
}
```

Local configuration sample:
```java
JSONArray priceFloors = new JSONArray();
try {
    priceFloors.put(new JSONObject().put("id1", 300.006));
    priceFloors.put(new JSONObject().put("id2", 1000));
    priceFloors.put(302.006);
    priceFloors.put(1002);
} catch (Exception e) {
    e.printStackTrace();
}

Bundle bundle = new BidMachineBundleBuilder()
        .setSellerId("YOUR_SELLER_ID")
        .setMediationConfig("YOUR_MEDIATION_CONFIG")
        .setCoppa(true)
        .setLoggingEnabled(true)
        .setTestMode(true)
        .setSubjectToGDPR(true)
        .setConsentConfig(true, "YOUR_CONSENT_STRING")
        .setEndpoint("YOUR_ENDPOINT")
        .setUserId("YOUR_USER_ID")
        .setGender(Gender.Male)
        .setYob(1990)
        .setKeywords("Keyword_1,Keyword_2,Keyword_3,Keyword_4")
        .setCountry("YOUR_COUNTRY")
        .setCity("YOUR_CITY")
        .setZip("YOUR_ZIP")
        .setSturl("https://store_url.com")
        .setStoreCategory("YOUR_STORE_CATEGORY")
        .setStoreSubCategories("YOUR_STORE_SUB_CATEGORY_1,YOUR_STORE_SUB_CATEGORY_2")
        .setFrameworkName(Framework.UNITY)
        .setPaid(true)
        .setBcat("IAB-1,IAB-3,IAB-5")
        .setBadv("https://domain_1.com,https://domain_2.org")
        .setBapps("com.test.application_1,com.test.application_2,com.test.application_3")
        .setPriceFloors(priceFloors)
        .setPublisherId("YOUR_PUBLISHER_ID")
        .setPublisherName("YOUR_PUBLISHER_NAME")
        .setPublisherDomain("YOUR_PUBLISHER_DOMAIN")
        .setPublisherCategories("YOUR_PUBLISHER_CATEGORIES_1,YOUR_PUBLISHER_CATEGORIES_2")
        .build();

// Set bundle to custom event banner
AdRequest adRequest = new AdRequest.Builder()
        .addCustomEventExtrasBundle(BidMachineCustomEventBanner.class, bundle)
        .build();

// Create new AdView instance and load it
AdView adView = new AdView(this);
adView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                  ViewGroup.LayoutParams.MATCH_PARENT));
adView.setAdUnitId(MREC_ID);
adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
adView.setAdListener(new MrecViewListener());
adView.loadAd(adRequest);
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineAdMobActivity.java#L177)

## Interstitial implementation
Server configuration sample:
```json
{
    "seller_id": "YOUR_SELLER_ID",
    "mediation_config": "YOUR_MEDIATION_CONFIG",
    "coppa": "true",
    "logging_enabled": "true",
    "test_mode": "true",
    "subject_to_gdpr": "true",
    "has_consent": "true",
    "consent_string": "YOUR_CONSENT_STRING",
    "endpoint": "YOUR_ENDPOINT",
    "ad_content_type": "Static",
    "user_id": "YOUR_USER_ID",
    "gender": "M",
    "yob": "1990",
    "keywords": "Keyword_1,Keyword_2,Keyword_3,Keyword_4",
    "country": "YOUR_COUNTRY",
    "city": "YOUR_CITY",
    "zip": "YOUR_ZIP",
    "sturl": "https://store_url.com",
    "store_cat": "YOUR_STORE_CATEGORY",
    "store_subcat": "YOUR_STORE_SUB_CATEGORY_1,YOUR_STORE_SUB_CATEGORY_2",
    "fmw_name": "YOUR_FRAMEWORK_NAME",
    "paid": "true",
    "bcat": "IAB-1,IAB-3,IAB-5",
    "badv": "https://domain_1.com,https://domain_2.org",
    "bapps": "com.test.application_1,com.test.application_2,com.test.application_3",
    "price_floors": [{
            "id_1": 300.06
        }, {
            "id_2": 1000
        },
        302.006,
        1002
    ],
    "pubid": "YOUR_PUBLISHER_ID",
    "pubname": "YOUR_PUBLISHER_NAME",
    "pubdomain": "YOUR_PUBLISHER_DOMAIN",
    "pubcat": "YOUR_PUBLISHER_CATEGORIES_1,YOUR_PUBLISHER_CATEGORIES_2"
}
```

Local configuration sample:
```java
JSONArray priceFloors = new JSONArray();
try {
    priceFloors.put(new JSONObject().put("id1", 300.006));
    priceFloors.put(new JSONObject().put("id2", 1000));
    priceFloors.put(302.006);
    priceFloors.put(1002);
} catch (Exception e) {
    e.printStackTrace();
}

Bundle bundle = new BidMachineBundleBuilder()
        .setSellerId("YOUR_SELLER_ID")
        .setMediationConfig("YOUR_MEDIATION_CONFIG")
        .setCoppa(true)
        .setLoggingEnabled(true)
        .setTestMode(true)
        .setSubjectToGDPR(true)
        .setConsentConfig(true, "YOUR_CONSENT_STRING")
        .setAdContentType(AdContentType.Static)
        .setEndpoint("YOUR_ENDPOINT")
        .setUserId("YOUR_USER_ID")
        .setGender(Gender.Male)
        .setYob(1990)
        .setKeywords("Keyword_1,Keyword_2,Keyword_3,Keyword_4")
        .setCountry("YOUR_COUNTRY")
        .setCity("YOUR_CITY")
        .setZip("YOUR_ZIP")
        .setSturl("https://store_url.com")
        .setStoreCategory("YOUR_STORE_CATEGORY")
        .setStoreSubCategories("YOUR_STORE_SUB_CATEGORY_1,YOUR_STORE_SUB_CATEGORY_2")
        .setFrameworkName(Framework.UNITY)
        .setPaid(true)
        .setBcat("IAB-1,IAB-3,IAB-5")
        .setBadv("https://domain_1.com,https://domain_2.org")
        .setBapps("com.test.application_1,com.test.application_2,com.test.application_3")
        .setPriceFloors(priceFloors)
        .setPublisherId("YOUR_PUBLISHER_ID")
        .setPublisherName("YOUR_PUBLISHER_NAME")
        .setPublisherDomain("YOUR_PUBLISHER_DOMAIN")
        .setPublisherCategories("YOUR_PUBLISHER_CATEGORIES_1,YOUR_PUBLISHER_CATEGORIES_2")
        .build();

// Set bundle to custom event interstitial
AdRequest adRequest = new AdRequest.Builder()
        .addCustomEventExtrasBundle(BidMachineCustomEventInterstitial.class, bundle)
        .build();

// Load InterstitialAd
InterstitialAd.load(this, INTERSTITIAL_ID, adRequest, new InterstitialLoadListener());
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineAdMobActivity.java#L240)

## Rewarded implementation
Server configuration sample:
```json
{
    "seller_id": "YOUR_SELLER_ID",
    "mediation_config": "YOUR_MEDIATION_CONFIG",
    "coppa": "true",
    "logging_enabled": "true",
    "test_mode": "true",
    "subject_to_gdpr": "true",
    "has_consent": "true",
    "consent_string": "YOUR_GDPR_CONSENT_STRING",
    "endpoint": "YOUR_ENDPOINT",
    "user_id": "YOUR_USER_ID",
    "gender": "F",
    "yob": "2000",
    "keywords": "Keyword_1,Keyword_2,Keyword_3,Keyword_4",
    "country": "YOUR_COUNTRY",
    "city": "YOUR_CITY",
    "zip": "YOUR_ZIP",
    "sturl": "https://store_url.com",
    "store_cat": "YOUR_STORE_CATEGORY",
    "store_subcat": "YOUR_STORE_SUB_CATEGORY_1,YOUR_STORE_SUB_CATEGORY_2",
    "fmw_name": "YOUR_FRAMEWORK_NAME",
    "paid": "true",
    "bcat": "IAB-1,IAB-3,IAB-5",
    "badv": "https://domain_1.com,https://domain_2.org",
    "bapps": "com.test.application_1,com.test.application_2,com.test.application_3",
    "price_floors": [{
            "id_1": 300.06
        }, {
            "id_2": 1000
        },
        302.006,
        1002
    ],
    "pubid": "YOUR_PUBLISHER_ID",
    "pubname": "YOUR_PUBLISHER_NAME",
    "pubdomain": "YOUR_PUBLISHER_DOMAIN",
    "pubcat": "YOUR_PUBLISHER_CATEGORIES_1,YOUR_PUBLISHER_CATEGORIES_2"
}
```

Local configuration sample:
```java
JSONArray priceFloors = new JSONArray();
try {
    priceFloors.put(new JSONObject().put("id1", 300.006));
    priceFloors.put(new JSONObject().put("id2", 1000));
    priceFloors.put(302.006);
    priceFloors.put(1002);
} catch (Exception e) {
    e.printStackTrace();
}

Bundle bundle = new BidMachineBundleBuilder()
        .setSellerId("YOUR_SELLER_ID")
        .setMediationConfig("YOUR_MEDIATION_CONFIG")
        .setCoppa(true)
        .setLoggingEnabled(true)
        .setTestMode(true)
        .setSubjectToGDPR(true)
        .setConsentConfig(true, "YOUR_CONSENT_STRING")
        .setEndpoint("YOUR_ENDPOINT")
        .setUserId("YOUR_USER_ID")
        .setGender(Gender.Male)
        .setYob(1990)
        .setKeywords("Keyword_1,Keyword_2,Keyword_3,Keyword_4")
        .setCountry("YOUR_COUNTRY")
        .setCity("YOUR_CITY")
        .setZip("YOUR_ZIP")
        .setSturl("https://store_url.com")
        .setStoreCategory("YOUR_STORE_CATEGORY")
        .setStoreSubCategories("YOUR_STORE_SUB_CATEGORY_1,YOUR_STORE_SUB_CATEGORY_2")
        .setFrameworkName(Framework.UNITY)
        .setPaid(true)
        .setBcat("IAB-1,IAB-3,IAB-5")
        .setBadv("https://domain_1.com,https://domain_2.org")
        .setBapps("com.test.application_1,com.test.application_2,com.test.application_3")
        .setPriceFloors(priceFloors)
        .setPublisherId("YOUR_PUBLISHER_ID")
        .setPublisherName("YOUR_PUBLISHER_NAME")
        .setPublisherDomain("YOUR_PUBLISHER_DOMAIN")
        .setPublisherCategories("YOUR_PUBLISHER_CATEGORIES_1,YOUR_PUBLISHER_CATEGORIES_2")
        .build();

// Set bundle to mediation rewarded video ad adapter
AdRequest adRequest = new AdRequest.Builder()
        .addNetworkExtrasBundle(BidMachineAdapter.class, bundle)
        .build();

// Load RewardedAd
RewardedAd.load(this, REWARDED_ID, adRequest, new RewardedLoadListener());
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineAdMobActivity.java#L297)

## Native implementation
Server configuration sample:
```json
{
    "seller_id": "YOUR_SELLER_ID",
    "mediation_config": "YOUR_MEDIATION_CONFIG",
    "coppa": "true",
    "logging_enabled": "true",
    "test_mode": "true",
    "subject_to_gdpr": "true",
    "has_consent": "true",
    "consent_string": "YOUR_GDPR_CONSENT_STRING",
    "media_asset_types": "Icon,Video",
    "endpoint": "YOUR_ENDPOINT",
    "user_id": "YOUR_USER_ID",
    "gender": "F",
    "yob": "2000",
    "keywords": "Keyword_1,Keyword_2,Keyword_3,Keyword_4",
    "country": "YOUR_COUNTRY",
    "city": "YOUR_CITY",
    "zip": "YOUR_ZIP",
    "sturl": "https://store_url.com",
    "store_cat": "YOUR_STORE_CATEGORY",
    "store_subcat": "YOUR_STORE_SUB_CATEGORY_1,YOUR_STORE_SUB_CATEGORY_2",
    "fmw_name": "YOUR_FRAMEWORK_NAME",
    "paid": "true",
    "bcat": "IAB-1,IAB-3,IAB-5",
    "badv": "https://domain_1.com,https://domain_2.org",
    "bapps": "com.test.application_1,com.test.application_2,com.test.application_3",
    "price_floors": [{
            "id_1": 300.06
        }, {
            "id_2": 1000
        },
        302.006,
        1002
    ],
    "pubid": "YOUR_PUBLISHER_ID",
    "pubname": "YOUR_PUBLISHER_NAME",
    "pubdomain": "YOUR_PUBLISHER_DOMAIN",
    "pubcat": "YOUR_PUBLISHER_CATEGORIES_1,YOUR_PUBLISHER_CATEGORIES_2"
}
```

Local configuration sample:
```java
JSONArray priceFloors = new JSONArray();
try {
    priceFloors.put(new JSONObject().put("id1", 300.006));
    priceFloors.put(new JSONObject().put("id2", 1000));
    priceFloors.put(302.006);
    priceFloors.put(1002);
} catch (Exception e) {
    e.printStackTrace();
}

Bundle bundle = new BidMachineBundleBuilder()
        .setSellerId("YOUR_SELLER_ID")
        .setMediationConfig("YOUR_MEDIATION_CONFIG")
        .setCoppa(true)
        .setLoggingEnabled(true)
        .setTestMode(true)
        .setSubjectToGDPR(true)
        .setConsentConfig(true, "YOUR_CONSENT_STRING")
        .setMediaAssetTypes(MediaAssetType.Icon, MediaAssetType.Video)
        .setEndpoint("YOUR_ENDPOINT")
        .setUserId("YOUR_USER_ID")
        .setGender(Gender.Male)
        .setYob(1990)
        .setKeywords("Keyword_1,Keyword_2,Keyword_3,Keyword_4")
        .setCountry("YOUR_COUNTRY")
        .setCity("YOUR_CITY")
        .setZip("YOUR_ZIP")
        .setSturl("https://store_url.com")
        .setStoreCategory("YOUR_STORE_CATEGORY")
        .setStoreSubCategories("YOUR_STORE_SUB_CATEGORY_1,YOUR_STORE_SUB_CATEGORY_2")
        .setFrameworkName(Framework.UNITY)
        .setPaid(true)
        .setBcat("IAB-1,IAB-3,IAB-5")
        .setBadv("https://domain_1.com,https://domain_2.org")
        .setBapps("com.test.application_1,com.test.application_2,com.test.application_3")
        .setPriceFloors(priceFloors)
        .setPublisherId("YOUR_PUBLISHER_ID")
        .setPublisherName("YOUR_PUBLISHER_NAME")
        .setPublisherDomain("YOUR_PUBLISHER_DOMAIN")
        .setPublisherCategories("YOUR_PUBLISHER_CATEGORIES_1,YOUR_PUBLISHER_CATEGORIES_2")
        .build();

// Set bundle to mediation native ad adapter
AdRequest adRequest = new AdRequest.Builder()
        .addCustomEventExtrasBundle(BidMachineCustomEventNative.class, bundle)
        .build();

// Create new AdLoader instance and load
NativeListener nativeListener = new NativeListener();
AdLoader adLoader = new AdLoader.Builder(this, NATIVE_ID)
        .forNativeAd(nativeListener)
        .withAdListener(nativeListener)
        .build();
adLoader.loadAd(adRequest);
```
[*Example*](src/main/java/io/bidmachine/examples/BidMachineAdMobActivity.java#L353)