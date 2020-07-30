package com.tezkit.core.crypto;

import lombok.Builder;
import lombok.Data;

/**
 * An immutable data class keeping the private key, public key and public key hash with tezos
 * prefixes and check sums.
 */
@Data
@Builder
public class KeyHolder {
    private final String publicKeyHash;
    private final byte[] privateKey;
    private final byte[] publicKey;
}
