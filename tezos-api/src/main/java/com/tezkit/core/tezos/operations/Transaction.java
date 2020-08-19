package com.tezkit.core.tezos.operations;

import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

@Data
@Builder
public class Transaction extends Operation {
    @NoEncoding
    @Builder.Default
    private String kind = "6c";
    @PublicKeyHashEncoding
    private String source;
    private BigInteger fee;
    private BigInteger counter;
    @Builder.Default
    private BigInteger gasLimit = DEFAULT_GAS_LIMIT;
    @Builder.Default
    private BigInteger storageLimit = DEFAULT_STORAGE_LIMIT;
    private BigInteger amount;
    @AddressEncoding
    private String destination;
    @NoEncoding
    @Builder.Default
    private String parameters = "00"; // default is no additional params
}
