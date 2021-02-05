# BidMachine Android AdMobAdapter

[<img src="https://img.shields.io/badge/SDK%20Version-1.6.3-brightgreen">](https://github.com/bidmachine/BidMachine-Android-SDK)
[<img src="https://img.shields.io/badge/Adapter%20Version-1.6.3.10-green">](https://artifactory.bidmachine.io/bidmachine/io/bidmachine/ads.adapters.admob/1.6.3.10/)
[<img src="https://img.shields.io/badge/AdMob%20Version-19.7.0-blue">](https://developers.google.com/admob/android/quick-start)

* [Useful links](#useful-links)
* [Integration](#integration)
* [Types of integration](#types-of-integration)
* [Working with price rounding](#working-with-price-rounding)
* [What's new in last version](#whats-new-in-last-version)

## Useful links
* [BidMachine integration documentation](https://wiki.appodeal.com/display/BID/BidMachine+Android+SDK+Documentation)

## Integration
```gradle
repositories {
    //Add BidMachine maven repository
    maven {
        name 'BidMachine Ads maven repository'
        url 'https://artifactory.bidmachine.io/bidmachine'
    }
}

dependencies {
    //Add BidMachine SDK dependency
    implementation 'io.bidmachine:ads:1.6.3'
    //Add BidMachine SDK AdMob Adapter dependency
    implementation 'io.bidmachine:ads.adapters.admob:1.6.3.10'
    //Add AdMob SDK dependency
    implementation 'com.google.android.gms:play-services-ads:19.7.0'
}
```

## Types of integration
* [Classic AdMob implementation](example_admob)
* [HeaderBidding AdMob implementation](example_admob_fetch)
* [HeaderBidding AdManager implementation](example_ad_manager)

## Working with price rounding
BidMachine supports server-side price rounding.<br>
To setup it correctly - please contact your manager to set up your own rounding rules. Manager will provide you with the list of prices and you can use them to create orders/line items in partner's dashboard.<br>
If you prefer to automate this process - you can use PubMonkey plugin.<br>
Documentation about how to use plugin could be found [here](https://doc.bidmachine.io/eng/ssp-publisher-integration-documentation/bidmachine-custom-adapters/how-to-use-plugin-for-integration-via-mopub-google/creating-line-items-in-google-ad-manager-dashboard)

## What's new in last version
Please view the [changelog](CHANGELOG.md) for details.