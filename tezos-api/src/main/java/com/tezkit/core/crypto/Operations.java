package com.tezkit.core.crypto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.HashMap;

/**
 * Build operation payloads that are transmitted to a tezos node.
 */
public class Operations {

    @Getter
    @Setter
    public static BigInteger defaultGasLimit = BigInteger.valueOf(15400);
    @Getter
    @Setter
    public static BigInteger defaultStorageLimit = BigInteger.valueOf(300);
    @Getter
    @Setter
    public static BigInteger defaultFee = BigInteger.valueOf(2940);

    public static HashMap<String, String> emptyTransaction() {
        var output = new HashMap<String, String>();
        output.put("fee", getDefaultFee().toString());
        output.put("gas_limit", getDefaultGasLimit().toString());
        output.put("storage_limit", getDefaultStorageLimit().toString());
        return output;
    }

    public static HashMap<String, String> revealWithDefaults(
            String from, String pubKey, Integer counter) {
        var output = emptyTransaction();
        output.put("kind", "reveal");
        output.put("source", from);
        output.put("public_key", pubKey);
        output.put("counter", String.valueOf(counter));
        return output;
    }

    public static HashMap<String, String> transferWithDefaults(
            String from, String to, BigInteger amount, Integer counter) {
        var output = emptyTransaction();
        output.put("kind", "transaction");
        output.put("destination", to);
        output.put("source", from);
        output.put("amount", amount.toString());
        output.put("counter", String.valueOf(counter));
        return output;
    }

}
