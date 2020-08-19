package com.tezkit.wallet.view;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@AllArgsConstructor
class Formatter {

    private Integer precision;
    private String suffix;

    /**
     * Convert micro tez to tez (times 10e-6)
     *
     * @param balance amount of micro tez
     * @return amount with a currency suffix
     */
    public String balance(BigInteger balance) {
        if (balance == null) return "...";
        var bal = new BigDecimal(balance)
                .divide(BigDecimal.valueOf(1_000_000))
                .setScale(precision, RoundingMode.HALF_DOWN)
                .toPlainString();

        return bal + " " + suffix;
    }

    /**
     * Convert micro tez to tez (times 10e-6)
     *
     * @param balance amount of micro tez
     * @return amount with a currency suffix
     */
    public String balance(BigDecimal balance) {
        if (balance == null) return "...";
        var bal = balance
                .setScale(precision, RoundingMode.HALF_DOWN)
                .toPlainString();

        return bal + suffix;
    }

}
