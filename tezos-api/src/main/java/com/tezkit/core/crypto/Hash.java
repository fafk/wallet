package com.tezkit.core.crypto;

import com.goterl.lazycode.lazysodium.SodiumJava;

public class Hash {

    /**
     * Get a BLAKE2b hash of bytes.
     *
     * @param bytes bytes to hash
     * @param len length of hash ouut in bytes
     * @return hash of bytes
     */
    static public byte[] computeGenericHash(byte[] bytes, Integer len) {
        byte[] out = new byte[len];
        new SodiumJava().crypto_generichash(out, out.length, bytes, bytes.length, bytes, 0);
        return out;
    }

}
