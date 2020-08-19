package com.tezkit.core;

import com.tezkit.core.network.BalanceChange;
import com.tezkit.core.network.ExchangeRate;
import com.tezkit.core.network.RPCException;
import com.tezkit.core.network.TzStatsRPC;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TzStatsAPI {

    /**
     * Asynchronously fetch balance history of an address from TzStats. Provides a BalanceChange
     * instance list with time of change and how much XTZ came in and out.
     *
     * @param address account to fetch balance for
     * @return a Future returning a list of balance changes
     */
    public static CompletableFuture<List<BalanceChange>> getBalanceChangesAsync(String address) {
        return TzStatsRPC.getAllBalanceChanges(address);
    }

    /**
     * Fetch balance history of an address from TzStats. Provides a BalanceChange
     * instance list with time of change and how much XTZ came in and out.
     *
     * @param address account to fetch balance for
     * @return a Future returning a list of balance changes
     */
    public static List<BalanceChange> getBalanceChanges(String address) throws RPCException {
        try {
            return TzStatsRPC.getAllBalanceChanges(address).get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RPCException("TzStats exception: " + e.toString());
        }
    }

    /**
     * Get current XTZ/USD exchange rate in the last hour (open, high, low).
     *
     * @return exchange rate info
     * @throws RPCException
     */
    public static CompletableFuture<ExchangeRate> getExchangeRate() {
        return TzStatsRPC.getExchangeRate();
    }

}
