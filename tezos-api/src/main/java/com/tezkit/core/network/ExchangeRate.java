package com.tezkit.core.network;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class ExchangeRate {
    private ZonedDateTime time;
    private Double open;
    private Double low;
    private Double high;
}
