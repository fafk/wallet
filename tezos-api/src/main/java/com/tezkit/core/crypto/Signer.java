package com.tezkit.core.crypto;

import com.goterl.lazycode.lazysodium.SodiumJava;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

import static com.tezkit.core.crypto.Hash.computeGenericHash;
import static com.tezkit.core.crypto.TezosKeys.edskPrefix;
import static org.bitcoinj.core.Utils.HEX;

/**
 * Sign data with a private key.
 */
public class Signer {

    public static final byte[] edsigPrefix = {9, (byte) 245, (byte) 205, (byte) 134, 18};

    /**
     * Sign data with a private key and a water mark to be shipped off to the network.
     *
     * @param bytes bytes to sign
     * @param privateKey private key to sign the data with
     * @param watermark watermark to prefix operations; 0x03 for wallet operations
     * @return an immutable data structure holding the data, sig and signed data
     */
    public static SignedData sign(byte[] bytes, byte[] privateKey, String watermark) {
        var sodium = new SodiumJava();

        // Remove the edsk prefix from the private key bytes.
        byte[] privateKeyBytes = Arrays.copyOfRange(
                privateKey, edskPrefix.length, privateKey.length);

        // Then we create a work array and check if the watermark parameter has been passed.
        byte[] workBytes = ArrayUtils.addAll(bytes);

        if (watermark != null) {
            byte[] wmBytes = HEX.decode(watermark);
            workBytes = ArrayUtils.addAll(wmBytes, workBytes);
        }

        // Hash (watermark + bytes) and sign the hash.
        byte[] hashedWorkBytes = computeGenericHash(workBytes, 32);// new byte[32];
        byte[] sig = new byte[64];
        sodium.crypto_sign_detached(
                sig, null, hashedWorkBytes, hashedWorkBytes.length, privateKeyBytes);

        // Crate edsig by encoding (edsig_prefix + sig).
        // `sbytes` will be the concatenation of bytes (in hex) + sig (in hex).
        byte[] edsigPrefixedSig;
        edsigPrefixedSig = ArrayUtils.addAll(edsigPrefix, sig);
        String sbytes = HEX.encode(bytes) + HEX.encode(sig);

        return SignedData.builder()
                .bytes(HEX.encode(bytes))
                .sig(HEX.encode(sig))
                .edsig(TezosBase58.encode(edsigPrefixedSig))
                .sbytes(sbytes)
                .build();
    }

    @Data
    @Builder
    public static class SignedData {
        private final String bytes;
        private final String sig;
        private final String edsig;
        private final String sbytes;
    }

}
