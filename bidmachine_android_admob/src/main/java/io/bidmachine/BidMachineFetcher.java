package io.bidmachine;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import io.bidmachine.AdRequest.AdRequestListener;
import io.bidmachine.models.AuctionResult;
import io.bidmachine.utils.BMError;

public class BidMachineFetcher {

    public static final String KEY_ID = "bm_id";
    public static final String KEY_PRICE = "bm_pf";

    private static final BigDecimal DEF_PRICE_ROUNDING = new BigDecimal("0.01");
    private static final RoundingMode DEF_ROUNDING_MODE = RoundingMode.CEILING;

    private static BigDecimal priceRounding = DEF_PRICE_ROUNDING;
    private static RoundingMode priceRoundingMode = DEF_ROUNDING_MODE;

    private static EnumMap<AdsType, Map<String, AdRequest>> cachedRequests =
            new EnumMap<>(AdsType.class);

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static void setPriceRounding(double rounding) {
        setPriceRounding(rounding, DEF_ROUNDING_MODE);
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static void setPriceRounding(double rounding, RoundingMode roundingMode) {
        if (roundingMode == RoundingMode.UNNECESSARY) {
            throw new IllegalArgumentException("Invalid rounding mode");
        }
        priceRounding = new BigDecimal(String.valueOf(rounding));
        priceRoundingMode = roundingMode;
    }

    @Nullable
    @SuppressWarnings({"unchecked", "WeakerAccess"})
    public static <T extends AdRequest> Map<String, String> fetch(T adRequest) {
        AuctionResult auctionResult = adRequest.getAuctionResult();
        if (auctionResult == null) {
            return null;
        }
        final String id = auctionResult.getId();
        final AdsType adsType = adRequest.getType();
        adRequest.addListener(new AdRequestListener() {
            @Override
            public void onRequestSuccess(@NonNull AdRequest adRequest,
                                         @NonNull AuctionResult auctionResult) {
                //ignore
            }

            @Override
            public void onRequestFailed(@NonNull AdRequest adRequest, @NonNull BMError bmError) {
                //ignore
            }

            @Override
            public void onRequestExpired(@NonNull AdRequest adRequest) {
                release(adsType, id);
            }
        });
        synchronized (BidMachineFetcher.class) {
            Map<String, AdRequest> cached = cachedRequests.get(adsType);
            if (cached == null) {
                cached = new HashMap<>();
                cachedRequests.put(adsType, cached);
            }
            cached.put(id, adRequest);
        }
        Map<String, String> result = new HashMap<>();
        result.put(KEY_ID, id);
        result.put(KEY_PRICE, roundPrice(auctionResult.getPrice()));
        return result;
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static <T extends AdRequest> boolean release(@NonNull T adRequest) {
        AuctionResult auctionResult = adRequest.getAuctionResult();
        if (auctionResult != null) {
            return release(adRequest.getType(), auctionResult.getId());
        }
        return false;
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static boolean release(@NonNull AdsType adsType,
                                  @NonNull Map<String, String> fetchedParams) {
        String requestId = fetchedParams.get(KEY_ID);
        if (requestId != null) {
            return release(adsType, requestId);
        }
        return false;
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static boolean release(@NonNull AdsType adsType, @NonNull String id) {
        synchronized (BidMachineFetcher.class) {
            Map<String, AdRequest> cached = cachedRequests.get(adsType);
            if (cached != null) {
                return cached.remove(id) != null;
            }
            return false;
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends AdRequest> T pop(@NonNull AdsType adsType, @NonNull String id) {
        synchronized (BidMachineFetcher.class) {
            Map<String, AdRequest> cached = cachedRequests.get(adsType);
            if (cached != null) {
                T result = (T) cached.get(id);
                if (result != null && result.getAuctionResult() != null) {
                    cached.remove(result.getAuctionResult().getId());
                }
                return result;
            }
            return null;
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static String roundPrice(double price) {
        BigDecimal value = new BigDecimal(String.valueOf(price));
        BigDecimal roundedValue = priceRounding.signum() == 0
                ? value
                : (value.divide(priceRounding, 0, priceRoundingMode)).multiply(priceRounding);
        return roundedValue.setScale(priceRounding.scale(), RoundingMode.HALF_UP).toString();
    }
}
