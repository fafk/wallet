package com.tezkit.core.crypto;

import org.bitcoinj.core.Base58;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;

public class TezosBase58 {

    private static int CHECKSUM_SIZE = 4;

    public static String encode(byte[] data) {
        return Base58.encode(addChecksum(data));
    }

    public static byte[] decode(String encoded) {
        byte[] valueWithChecksum = Base58.decode(encoded);
        byte[] value = verifyAndRemoveChecksum(valueWithChecksum);
        if (value == null) {
            throw new IllegalArgumentException("Base58 checksum is invalid");
        } else {
            return value;
        }
    }

    private static byte[] verifyAndRemoveChecksum(byte[] data) {
        byte[] value = Arrays.copyOfRange(data, 0, data.length - CHECKSUM_SIZE);
        byte[] checksum = Arrays.copyOfRange(data, data.length - CHECKSUM_SIZE, data.length);
        byte[] expectedChecksum = getChecksum(value);
        return Arrays.equals(checksum, expectedChecksum) ? value : null;
    }

    private static byte[] addChecksum(byte[] data) {
        byte[] checksum = getChecksum(data);
        byte[] result = new byte[data.length + checksum.length];
        System.arraycopy(data, 0, result, 0, data.length);
        System.arraycopy(checksum, 0, result, data.length, checksum.length);
        return result;
    }

    private static byte[] getChecksum(byte[] data) {
        return Arrays.copyOfRange(Sha256Hash.hashTwice(data), 0, CHECKSUM_SIZE);
    }

}


