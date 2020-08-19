package com.tezkit.wallet.view;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FormatterTest {

    @Test
    public void formatBalanceTest() {
        assertEquals(
                "1.00 XTZ",
                new Formatter(2, "XTZ").balance(BigInteger.valueOf(1_000_000)));
        assertEquals(
                "0.12 XTZ",
                new Formatter(2, "XTZ").balance(BigInteger.valueOf(120_000)));
    }

}
