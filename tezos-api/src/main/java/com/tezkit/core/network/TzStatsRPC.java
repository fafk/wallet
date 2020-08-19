package com.tezkit.core.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TzStatsRPC {

    @Getter
    @Setter
    private static String TzStatsUrl = "https://api.tzstats.com";

    /**
     * Fetch balance history of an address and remap result to BalanceChange instances.
     * @param address account to fetch balance for
     * @return a Future returning a list of balance changes
     */
    public static CompletableFuture<List<BalanceChange>> getAllBalanceChanges(String address) {
        return Network.getAsync(buildGetAllBalanceChangesURL(address)).thenApply((series) -> {
            try {
                var res = List.of(new ObjectMapper().readValue(series.body(), Object[][].class));

                return res.stream().map((balanceChangeArray) -> {
                    Instant i = Instant.ofEpochMilli((Long) balanceChangeArray[0]);
                    return BalanceChange.builder()
                            .timestamp(ZonedDateTime.ofInstant(i, ZoneOffset.UTC))
                            .in((Double) balanceChangeArray[1])
                            .out((Double) balanceChangeArray[2])
                            .counterparty((String) balanceChangeArray[3])
                            .isFee((Integer) balanceChangeArray[4] > 0)
                            .build();
                }).collect(Collectors.toList());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public static CompletableFuture<ExchangeRate> getExchangeRate() {
        return Network.getAsync(buildGetExchangeRateURL()).thenApply((series) -> {
            try {
                var res = new ObjectMapper().readValue(series.body(), Object[][].class)[0];
                Instant i = Instant.ofEpochMilli((Long) res[0]);

                return ExchangeRate.builder()
                        .time(ZonedDateTime.ofInstant(i, ZoneOffset.UTC))
                        .open((Double) res[1])
                        .high((Double) res[2])
                        .low((Double) res[3])
                        .build();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

//    public static CompletableFuture<ExchangeRate> getAccountInfo(String address) {
//        return Network.getAsync(buildAccountInfoURL(address)).thenApply((series) -> {
//            try {
//                var res = new ObjectMapper().readValue(series.body(), AccountInfo.class);
//
//                return ExchangeRate.builder()
//                        .time(ZonedDateTime.ofInstant(i, ZoneOffset.UTC))
//                        .open((Double) res[1])
//                        .high((Double) res[2])
//                        .low((Double) res[3])
//                        .build();
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//                return null;
//            }
//        });
//    }

    private static String buildAccountInfoURL(String address) {
        return new StringBuilder()
                .append(getTzStatsUrl())
                .append("/explorer/account/")
                .append(address)
                .toString();
    }

    private static String buildGetExchangeRateURL() {
        return new StringBuilder()
                .append(getTzStatsUrl())
                .append("/series/kraken/XTZ_USD/ohlcv?start_date=now&collapse=1h")
                .toString();
    }
    private static String buildGetAllBalanceChangesURL(String address) {
        return new StringBuilder()
                .append(getTzStatsUrl())
                .append("/tables/flow?")
                .append("address=" + address + "&")
                .append("limit=500000&")
                .append("columns=time,amount_in,amount_out,counterparty,is_fee")
                .toString();
    }

}
