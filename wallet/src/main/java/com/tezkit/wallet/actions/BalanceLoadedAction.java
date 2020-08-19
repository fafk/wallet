package com.tezkit.wallet.actions;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigInteger;

@Data
@AllArgsConstructor
public class BalanceLoadedAction {
    private final String address;
    private final BigInteger balance;
}
