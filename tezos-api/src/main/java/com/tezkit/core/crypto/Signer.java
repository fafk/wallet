package com.tezkit.core.crypto;

import com.goterl.lazycode.lazysodium.SodiumJava;
import lombok.Builder;
import lombok.Data;

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
     * @param bytes      bytes to sign
     * @param privateKey private key to sign the data with
     * @param watermark  watermark to prefix operations; 0x03 for wallet operations
     * @return an immutable data structure holding the data, sig and signed data
     */
    public static SignedData sign(byte[] bytes, byte[] privateKey, String watermark) {
        var sodium = new SodiumJava();

        // Remove the edsk prefix from the private key bytes.
        byte[] privateKeyBytes = Arrays.copyOfRange(
                privateKey, edskPrefix.length, privateKey.length);

        // Then we create a work array and check if the watermark parameter has been passed.
        byte[] workBytes = addAll(bytes);

        if (watermark != null) {
            byte[] wmBytes = HEX.decode(watermark);
            workBytes = addAll(wmBytes, workBytes);
        }

        // Hash (watermark + bytes) and sign the hash.
        byte[] hashedWorkBytes = computeGenericHash(workBytes, 32);// new byte[32];
        byte[] sig = new byte[64];
        sodium.crypto_sign_detached(
                sig, null, hashedWorkBytes, hashedWorkBytes.length, privateKeyBytes);

        // Create edsig by encoding (edsig_prefix + sig).
        // `sbytes` will be the concatenation of bytes (in hex) + sig (in hex).
        byte[] edsigPrefixedSig;
        edsigPrefixedSig = addAll(edsigPrefix, sig);
        String sbytes = HEX.encode(bytes) + HEX.encode(sig);

        return SignedData.builder()
                .bytes(HEX.encode(bytes))
                .sig(HEX.encode(sig))
                .edsig(Base58Check.encode(edsigPrefixedSig))
                .sbytes(sbytes)
                .build();
    }

    private static byte[] addAll(byte[] array1, byte... array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        } else {
            byte[] joinedArray = new byte[array1.length + array2.length];
            System.arraycopy(array1, 0, joinedArray, 0, array1.length);
            System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
            return joinedArray;
        }
    }

    private static byte[] clone(byte[] array) {
        return array == null ? null : array.clone();
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
