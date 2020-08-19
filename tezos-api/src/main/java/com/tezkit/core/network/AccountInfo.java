package com.tezkit.core.network;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Account info from TzStats.
 */
@Data
public class AccountInfo {
    @JsonProperty("delegated_balance")
    private Double delegatedBalance;
}
