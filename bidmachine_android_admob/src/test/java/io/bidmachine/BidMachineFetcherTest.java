package io.bidmachine;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.math.RoundingMode;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class BidMachineFetcherTest {

    @Test
    public void setPriceRounding1() {
        BidMachineFetcher.setPriceRounding(0.01);

        String result = BidMachineFetcher.roundPrice(0.01);
        assertEquals("0.01", result);
        result = BidMachineFetcher.roundPrice(0.99);
        assertEquals("0.99", result);
        result = BidMachineFetcher.roundPrice(1.212323);
        assertEquals("1.22", result);
        result = BidMachineFetcher.roundPrice(1.34538483);
        assertEquals("1.35", result);
        result = BidMachineFetcher.roundPrice(1.4);
        assertEquals("1.40", result);
        result = BidMachineFetcher.roundPrice(1.58538483);
        assertEquals("1.59", result);
    }

    @Test
    public void setPriceRounding2() {
        BidMachineFetcher.setPriceRounding(0.1);

        String result = BidMachineFetcher.roundPrice(0.01);
        assertEquals("0.1", result);
        result = BidMachineFetcher.roundPrice(0.99);
        assertEquals("1.0", result);
        result = BidMachineFetcher.roundPrice(1.212323);
        assertEquals("1.3", result);
        result = BidMachineFetcher.roundPrice(1.34538483);
        assertEquals("1.4", result);
        result = BidMachineFetcher.roundPrice(1.4);
        assertEquals("1.4", result);
        result = BidMachineFetcher.roundPrice(1.58538483);
        assertEquals("1.6", result);
    }

    @Test
    public void setPriceRounding3() {
        BidMachineFetcher.setPriceRounding(0.01, RoundingMode.FLOOR);

        String result = BidMachineFetcher.roundPrice(0.01);
        assertEquals("0.01", result);
        result = BidMachineFetcher.roundPrice(0.99);
        assertEquals("0.99", result);
        result = BidMachineFetcher.roundPrice(1.212323);
        assertEquals("1.21", result);
        result = BidMachineFetcher.roundPrice(1.34538483);
        assertEquals("1.34", result);
        result = BidMachineFetcher.roundPrice(1.4);
        assertEquals("1.40", result);
        result = BidMachineFetcher.roundPrice(1.58538483);
        assertEquals("1.58", result);
    }

}