package com.tezkit.wallet.actions;

import com.tezkit.core.network.ExchangeRate;
import lombok.Data;

@Data
public class ExchangeRateLoadedAction {
    private final ExchangeRate rate;

    public ExchangeRateLoadedAction(ExchangeRate rate) {
        this.rate = rate;
    }
}
