package com.tezkit.core.crypto;

import org.bitcoinj.crypto.MnemonicCode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.tezkit.core.crypto.Hash.computeGenericHash;

/**
 * A module of functions that transform public/private keys to their tezos representation.
 */
public class TezosKeys {
    public static final byte[] edpkPrefix = new byte[]{(byte) 13, (byte) 15, (byte) 37, (byte) 217};
    public static final byte[] edskPrefix = new byte[]{(byte) 43, (byte) 246, (byte) 78, (byte) 7};
    public static final byte[] tz1Prefix = new byte[]{(byte) 6, (byte) 161, (byte) 159};
    public static final byte[] tz2Prefix = new byte[]{(byte) 6, (byte) 161, (byte) 161};
    public static final byte[] tz3Prefix = new byte[]{(byte) 6, (byte) 161, (byte) 164};

    /**
     * Build a tezos public key hash from pure public key.
     *
     * @param sodiumPublicKey raw public key bytes
     * @return public key hash with a checksum
     */
    static public byte[] getTezosPublicKeyHash(byte[] sodiumPublicKey) {
        var genericHash = computeGenericHash(sodiumPublicKey, 20);
        byte[] firstFourOfDoubleChecksum;
        byte[] prefixedGenericHash = new byte[23];
        System.arraycopy(tz1Prefix, 0, prefixedGenericHash, 0, 3);
        System.arraycopy(genericHash, 0, prefixedGenericHash, 3, 20);
        firstFourOfDoubleChecksum = Sha256Hash.hashTwiceTakeFour(prefixedGenericHash);
        byte[] prefixedPKHashWithChecksum = new byte[27];
        System.arraycopy(prefixedGenericHash, 0, prefixedPKHashWithChecksum, 0, 23);
        System.arraycopy(firstFourOfDoubleChecksum, 0, prefixedPKHashWithChecksum, 23, 4);
        return prefixedPKHashWithChecksum;
    }

    /**
     * Build private key in with tezos prefix with checksum.
     *
     * @param sodiumPrivateKey raw private key bytes
     * @return private key in tezos format
     */
    static public byte[] getTezosPrivateKey(byte[] sodiumPrivateKey) {
        byte[] prefixedSecKey = new byte[68];
        byte[] firstFourOfDoubleChecksum;
        System.arraycopy(edskPrefix, 0, prefixedSecKey, 0, 4);
        System.arraycopy(sodiumPrivateKey, 0, prefixedSecKey, 4, 64);
        firstFourOfDoubleChecksum = Sha256Hash.hashTwiceTakeFour(prefixedSecKey);
        byte[] prefixedSecKeyWithChecksum = new byte[72];
        System.arraycopy(prefixedSecKey, 0, prefixedSecKeyWithChecksum, 0, 68);
        System.arraycopy(firstFourOfDoubleChecksum, 0, prefixedSecKeyWithChecksum, 68, 4);
        return prefixedSecKeyWithChecksum;
    }

    /**
     * Build a public key with tezos prefix and checksum.
     *
     * @param sodiumPublicKey raw public key bytes
     * @return public key in tezos format
     */
    static public byte[] getTezosPublicKey(byte[] sodiumPublicKey) {
        byte[] prefixedPubKey = new byte[36];
        System.arraycopy(edpkPrefix, 0, prefixedPubKey, 0, 4);
        System.arraycopy(sodiumPublicKey, 0, prefixedPubKey, 4, 32);
        byte[] firstFourOfDoubleChecksum = Sha256Hash.hashTwiceTakeFour(prefixedPubKey);
        byte[] prefixedPubKeyWithChecksum = new byte[40];
        System.arraycopy(prefixedPubKey, 0, prefixedPubKeyWithChecksum, 0, 36);
        System.arraycopy(firstFourOfDoubleChecksum, 0, prefixedPubKeyWithChecksum, 36, 4);
        return prefixedPubKeyWithChecksum;
    }

    /**
     * Get raw public key from a public key in tezos format. Strips the checksum and prefix.
     * @param tezosPrivateKey key in tezos format
     * @return raw key
     */
    static public byte[] rawKeyFromPublicKey(byte[] tezosPrivateKey) {
        var keyWithoutChecksum = Base58Check.verifyAndRemoveChecksum(tezosPrivateKey);
        return removePrefix(keyWithoutChecksum, edpkPrefix.length);
    }

    /**
     * Remove the first `prefixLen` bytes from a byte array. The original array remains intact.
     * @param bytes source bytes
     * @param prefixLen how many bytes should be removed from the beginning
     * @return a new array without the prefix
     */
    private static byte[] removePrefix(byte[] bytes, Integer prefixLen) {
        var outputLen = bytes.length - prefixLen;
        byte[] out = new byte[outputLen];
        System.arraycopy(bytes, prefixLen, out, 0, outputLen);
        return out;
    }

    /**
     * Get seed bytes from mnemonic and passphrase. The public and private keys are derived from it.
     *
     * @param mnemonicWords mnemonic words
     * @param passphrase    passphrase
     * @return seed data to generate a public/private key pair
     */
    static public byte[] getSeedBytesFromMnemonic(
            List<String> mnemonicWords, String passphrase) {

        byte[] src_seed = MnemonicCode.toSeed(mnemonicWords, passphrase);
        return Arrays.copyOfRange(src_seed, 0, 32);
    }

}
