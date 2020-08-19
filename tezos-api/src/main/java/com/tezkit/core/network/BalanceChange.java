package com.tezkit.core.network;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class BalanceChange implements Comparable {
    private ZonedDateTime timestamp;
    private Double in;
    private Double out;
    private String counterparty;
    private Boolean isFee;

    @Override
    public int compareTo(Object balanceChange) {
        return this.getTimestamp().compareTo(((BalanceChange) balanceChange).getTimestamp());
    }
}
